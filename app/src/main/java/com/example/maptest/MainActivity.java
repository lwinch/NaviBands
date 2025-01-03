package com.example.maptest;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jashgopani.github.io.mibandsdk.MiBand;
import jashgopani.github.io.mibandsdk.models.CustomVibration;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Context context;
    ToggleButton toggleMonitoringBtn;
    TextView logTv, statusTv, thresholdTv, bandMacTv;
    TextView bandConnectionStatusTv, bandBatteryStatusTv, bandChargingStatusTv;
    Button gotoConnectBtn;
    SeekBar thresholdSb;
    private int currentThreshold = 5;
    boolean monitoringMode;
    MiBand miband;
    int currentBattery = -1,selectedVibrationMode = 0;
    String chargingStatus;
    Spinner vibrateSpinner;
    ArrayAdapter<String> vibrateOnlyAdapter;
    //private CompositeDisposable disposables;
    private Disposable batteryDisposable;

    String[] vibrateModesArray;

    private final Map<String, Boolean> permissionStatus = new HashMap<>();

    private ActivityResultLauncher<String[]> permissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(), permissionStatus::putAll);

    final BroadcastReceiver directionsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //update UI
            String oldData = logTv.getText().toString();
            String logText = intent.getStringExtra("newData") + oldData;
            logTv.setText(logText);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: called");
        init();
    }

    private void init() {
        findViews();
        initializeVariables();
        addEventListeners();
        getPermissions();
        setStatusTv();
        checkDeviceCompatibility();
    }

    //methods used by init
    private void checkDeviceCompatibility() {
        //get The bluetooth adapter
        BluetoothAdapter bluetoothAdapter;
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        //check bluetooth enabled or not
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 1);
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
            }
            MainActivity.this.startActivity(enableBtIntent);
        }
    }

    private void initializeVariables() {
        context = getApplicationContext();
        monitoringMode = false;

        //seek bar and its related text view
        thresholdSb.setProgress(currentThreshold);
        String thresholdText = currentThreshold + "m";
        thresholdTv.setText(thresholdText);

        //For MiBand
        miband = MiBand.getInstance(MainActivity.this);

        //vibrate mode speaker
        vibrateModesArray = new String[]{
                "LEFT + RIGHT",
                "LEFT ONLY",
                "RIGHT ONLY"
        };
        //setup adapter
        vibrateOnlyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, vibrateModesArray);
        vibrateOnlyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vibrateSpinner.setAdapter(vibrateOnlyAdapter);
    }

    private void findViews() {
        toggleMonitoringBtn = findViewById(R.id.toggleMonitoringBtn);
        logTv = findViewById(R.id.logtv);
        statusTv = findViewById(R.id.statustv);
        thresholdSb = findViewById(R.id.thresholdSeek);
        thresholdTv = findViewById(R.id.thresholdTv);
        gotoConnectBtn = findViewById(R.id.gotoConnectBtn);
        bandMacTv = findViewById(R.id.bandMacTv);
        bandConnectionStatusTv = findViewById(R.id.bandConnectedStatusTv);
        bandBatteryStatusTv = findViewById(R.id.bandBatteryStatusTv);
        bandChargingStatusTv = findViewById(R.id.bandChargingStatusTv);
        vibrateSpinner = findViewById(R.id.vibrateOnlySpinner);
    }

    private void addEventListeners() {
        toggleMonitoringBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged: Monitor");
            monitoringMode = isChecked;
            setStatusTv();
            if (isChecked) {
                //monitoring is on
                getForegroundServicePermissions();
                startForegroundService();
                registerReceiver();
                miband.vibrate(CustomVibration.generatePattern("600",","));
                NotificationMonitor.requestRebind(new ComponentName(this, NotificationMonitor.class));
            } else {
                //monitoring is off
                statusTv.setText(R.string.monitoring_off);
                stopForegroundService();
                unregisterReceiver();
                NotificationMonitor.notificationMonitor.requestUnbind();
            }
        });

        thresholdSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = MathUtils.clamp(progress, 5, 100);
                currentThreshold = roundTo(progress,5);
                String thresholdText = currentThreshold + "m";
                thresholdTv.setText(thresholdText);
                thresholdTv.setTextColor(Color.RED);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        gotoConnectBtn.setOnClickListener(v-> goToConnectActivity(false));

        vibrateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVibrationMode = position;
                Toast.makeText(MainActivity.this, "Band will vibrate for "+vibrateModesArray[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
                Log.d(TAG, "onNothingSelected: Done nothing");
            }
        });

    }

    private void setStatusTv() {
        statusTv.setText(monitoringMode ? R.string.monitoring_on : R.string.monitoring_off);
    }

    private void getPermissions() {
        String[] permissions = {
                //Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.POST_NOTIFICATIONS,
                //Manifest.permission.READ_EXTERNAL_STORAGE
        };
        //permissionsLauncher.launch(permissions);
        //ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);

        //Check for Notification access
//        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
//            //service is enabled do something
//            Log.d(TAG, "Notification access already enabled");
//        } else {
//            //TODO make sure this is up to date
//            //service is not enabled try to enabled by calling...
        //startActivity(new Intent("android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"));
//        }
        //verifyBandAvailability();
    }

    private void goToConnectActivity(boolean withResult) {
        Intent intent = new Intent(MainActivity.this, BandConnectActivity.class);
        if(!withResult)startActivity(intent);
        else MainActivity.this.startActivity(intent.putExtra("requestCode", 69));
    }

    private int roundTo(int i, int r) {
        r = Math.max(1, r);
        return (int) Math.max(r * (Math.round((double) i / r)),0);
    }

    private void getForegroundServicePermissions() {
        String[] permissions = {
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE,
                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
        };
        permissionsLauncher.launch(permissions);
        //ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);
    }

    private void startForegroundService() {
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra("title", "NaviBands");
        intent.putExtra("text", "Navigation Mode ON");
        intent.putExtra("currentThreshold", currentThreshold);
        ContextCompat.startForegroundService(context, intent);
        Log.d(TAG, "startMonitoringService: " + R.string.monitoring_on);
    }

    private void stopForegroundService() {
        stopService(new Intent(context, ForegroundService.class));
        statusTv.setText(R.string.monitoring_off);
        Log.d(TAG, "stopMonitoringService: " + R.string.monitoring_off);
    }

    private void registerReceiver() {
        context.registerReceiver(directionsReceiver, new IntentFilter(DIRECTION_BROADCAST), Context.RECEIVER_EXPORTED);
        Log.d(TAG, "registerReceiver: DIRECTION_BROADCAST registered");
    }

    private void unregisterReceiver() {
        context.unregisterReceiver(directionsReceiver);
        Log.d(TAG, "unregisterReceiver: DIRECTION_BROADCAST unregistered");
    }

    private Integer[] getPatternFromDirection(String d) {
        Integer[] noVibration = new Integer[]{};
        if(Directions.isUTurn(d)) return CustomVibration.generatePattern(300,100,4);
        else if(Directions.isLeft(d)) return (selectedVibrationMode!=2)?CustomVibration.LEFT_PULSE:noVibration;
        else if(Directions.isRight(d)) return (selectedVibrationMode!=1)?CustomVibration.RIGHT_PULSE:noVibration;
        else switch (d) {
                case Directions.STRAIGHT:
                    return noVibration;
                case Directions.ALTERNATE:
                    return CustomVibration.FROWN;
                default:
                    return CustomVibration.generatePattern("600",",");
            }
    }
    private void updateBandStats(){
        boolean paired = miband.isPaired();
        if (paired){
            bandMacTv.setText(miband.getDevice().toString());
            bandConnectionStatusTv.setText(MiBand.getStatus(MiBand.PAIRED));
            bandMacTv.setTextColor(Color.GREEN);
            bandBatteryStatusTv.setText(currentBattery==-1?"---":String.valueOf(currentBattery));
            bandChargingStatusTv.setText(chargingStatus==null?"---": chargingStatus);
        } else {
            bandConnectionStatusTv.setText(MiBand.getStatus(MiBand.DISCONNECTED));
            bandMacTv.setTextColor(Color.RED);
            bandBatteryStatusTv.setText("---");
            bandChargingStatusTv.setText("---");
        }
    }

    /**
     * Retrieve Battery Info and update UI
     */
    private void refreshBatteryInfo(boolean onlyOnce){
        Log.d(TAG, "refreshBatteryInfo: "+miband.isPaired());
        if(miband.isPaired()) {
            batteryDisposable = miband.getBatteryInfo(5, TimeUnit.MINUTES, onlyOnce)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(info -> {
                        Log.d(TAG, "refreshBatteryInfo: " + info);
                        chargingStatus = info.getStatus();
                        currentBattery = info.getLevel();
                        updateBandStats();
                    }, err -> {
                        Toast.makeText(context, err.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, err.toString());
                        Log.e(TAG, Arrays.toString(err.getStackTrace()));
                    }, () -> Log.d(TAG, "getBatteryInfo: onComplete"));
        }
    }

    private void disconnectAndUnpair() {
        currentBattery = -1;
        if (batteryDisposable != null) {
            batteryDisposable.dispose();
        }
        miband.disconnect(true);
    }

    //for test vibrate buttons
    public void onClick(View v) {
        try {
            int vId = v.getId();
            if (vId == R.id.testLeft) {
                miband.vibrate(getPatternFromDirection(Directions.LEFT));
            } else if (vId == R.id.testRight) {
                miband.vibrate(getPatternFromDirection(Directions.RIGHT));
            } else if (vId == R.id.testStraight) {
                miband.vibrate(getPatternFromDirection(Directions.STRAIGHT));
            } else if (vId == R.id.testUturn) {
                miband.vibrate(getPatternFromDirection(Directions.U_LEFT));
            } else if (vId == R.id.testAlternate) {
                miband.vibrate(getPatternFromDirection(Directions.ALTERNATE));
            } else if (vId == R.id.testArrived) {
                miband.vibrate(getPatternFromDirection(Directions.ARRIVED));
            }
        }catch (Exception e){
            Log.e(TAG, e.toString());
            Log.e(TAG, Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(MainActivity.this,ForegroundService.class));
        Log.d(TAG, "onDestroy: Main Activity destroyed");
        disconnectAndUnpair();
        //disposables.clear();
        super.onDestroy();
    }

    //to handle results from other activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==69){//result is from connect activity
            if(resultCode==App.DEVICE_CONNECTED){
                Log.d(TAG, "onActivityResult: Device Connected = "+miband.getDevice());
                miband.vibrate(CustomVibration.SMILE);
                refreshBatteryInfo(true);
            }else if(resultCode == App.DEVICE_DISCONNECTED){
                Log.d(TAG, "onActivityResult: No device connected");
            }else if(resultCode == App.DEVICE_NULL){
                Log.d(TAG, "onActivityResult: No device selected");
            }
            updateBandStats();
        }
    }
}