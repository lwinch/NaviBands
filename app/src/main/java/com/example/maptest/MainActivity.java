package com.example.maptest;

import static com.example.maptest.Constants.DIRECTION_BROADCAST;
import static com.example.maptest.Constants.NOTIFICATION_MONITOR_UNBIND;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
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
import androidx.core.content.ContextCompat;

import com.example.maptest.uiListeners.MajorSeekBarChangeListener;
import com.example.maptest.uiListeners.MinorSeekBarChangeListener;

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
    TextView logTv, statusTv, thresholdTvMinor, thresholdTvMajor, bandMacTv;
    TextView bandConnectionStatusTv, bandBatteryStatusTv, bandChargingStatusTv;
    Button gotoConnectBtn;
    SeekBar thresholdSbMinor;
    SeekBar thresholdSbMajor;
//    private LiveData<Double> currentThreshold;
    boolean monitoringMode;
    MiBand miband;
    int currentBattery = -1, selectedVibrationMode = 0;
    String chargingStatus;
    Spinner vibrateSpinner;
    ArrayAdapter<String> vibrateOnlyAdapter;
    //private CompositeDisposable disposables;
    private Disposable batteryDisposable;

    String[] vibrateModesArray;

    private Map<String, Boolean> permissionStatus;

    private ActivityResultLauncher<String[]> permissionsLauncher;

    private SettingsDataStore settingsDataStore;
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
        settingsDataStore = SettingsDataStore.getInstance(this);
//        this.currentThreshold = settingsDataStore.distThresholdFlow;
        findViews();
        initializeVariables();
        addEventListeners();
        setStatusTv();
    }

    //methods used by onCreate
    private void findViews() {
        toggleMonitoringBtn = findViewById(R.id.toggleMonitoringBtn);
        logTv = findViewById(R.id.logtv);
        statusTv = findViewById(R.id.statustv);
        thresholdSbMinor = findViewById(R.id.thresholdSeekMinor);
        thresholdSbMajor = findViewById(R.id.thresholdSeekMajor);
        thresholdTvMinor = findViewById(R.id.thresholdTvMinor);
        thresholdTvMajor = findViewById(R.id.thresholdTvMajor);
        gotoConnectBtn = findViewById(R.id.gotoConnectBtn);
        bandMacTv = findViewById(R.id.bandMacTv);
        bandConnectionStatusTv = findViewById(R.id.bandConnectedStatusTv);
        bandBatteryStatusTv = findViewById(R.id.bandBatteryStatusTv);
        bandChargingStatusTv = findViewById(R.id.bandChargingStatusTv);
        vibrateSpinner = findViewById(R.id.vibrateOnlySpinner);
    }

    private void initializeVariables() {
        context = getApplicationContext();
        monitoringMode = false;

        //seek bar and its related text view
        thresholdSbMinor.setProgress(settingsDataStore.getMinorDistanceThreshold());
        thresholdSbMajor.setProgress(settingsDataStore.getMajorDistanceThreshold());
        String minorThresholdText = settingsDataStore.getMinorDistanceThreshold()
                + getString(R.string.first_distance_unit);
        String majorThresholdText = settingsDataStore.getMajorDistanceThreshold() / 10.
                + getString(R.string.second_distance_unit);
        thresholdTvMinor.setText(minorThresholdText);
        thresholdTvMajor.setText(majorThresholdText);

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

        permissionStatus = new HashMap<>();
        permissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), permissionStatus::putAll);
    }

    private void addEventListeners() {
        toggleMonitoringBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Log.d(TAG, "onCheckedChanged: " + isChecked);
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
                stopForegroundService();
                unregisterReceiver();
                context.sendBroadcast(new Intent(NOTIFICATION_MONITOR_UNBIND).putExtra("action", "unbind"));
            }
        });

        Resources res = getResources();
        thresholdSbMinor.setOnSeekBarChangeListener(new MinorSeekBarChangeListener(res, settingsDataStore, thresholdTvMinor));
        thresholdSbMajor.setOnSeekBarChangeListener(new MajorSeekBarChangeListener(res, settingsDataStore, thresholdTvMajor));

        gotoConnectBtn.setOnClickListener(v-> {
            Intent intent = new Intent(MainActivity.this, BandConnectActivity.class);
            //startActivity(intent);
            MainActivity.this.startActivity(intent.putExtra("requestCode", 69));
        });

        vibrateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedVibrationMode = position;
                Toast.makeText(
                        MainActivity.this,
                        "Band will vibrate for " + vibrateModesArray[position],
                        Toast.LENGTH_SHORT).show();
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

    //methods used by event Listeners
    private void getForegroundServicePermissions() {
        String[] permissions = {
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.FOREGROUND_SERVICE_SPECIAL_USE,
//                Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE
        };
        permissionsLauncher.launch(permissions);
        //ActivityCompat.requestPermissions(MainActivity.this, permissions, 0);
        //Check for Notification access
        //Cannot request this permission using a dialog because only system apps can request this permission
        if (Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners").contains(getApplicationContext().getPackageName())) {
            //service is enabled do something
            Log.d(TAG, "Notification access already enabled");
        } else {
            //service is not enabled try to enabled by calling...
            AlertDialog.Builder alertDialogBuilder = getAlertDialogBuilder();
            alertDialogBuilder.create().show();
        }
    }

    private AlertDialog.Builder getAlertDialogBuilder() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notification_listener_service);
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation);
        alertDialogBuilder.setPositiveButton(R.string.yes, (dialog, id) ->
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")));
        alertDialogBuilder.setNegativeButton(R.string.no,
                (dialog, id) -> {
                    // If you choose to not enable the notification listener
                    // the app. will not work as expected
                });
        return alertDialogBuilder;
    }

    private void startForegroundService() {
        Intent intent = new Intent(context, ForegroundService.class);
        intent.putExtra("title", "NaviBands");
        intent.putExtra("text", "Navigation Mode ON");
        intent.putExtra("currentMinorThreshold", settingsDataStore.getMinorDistanceThreshold());
        intent.putExtra("currentMajorThreshold", settingsDataStore.getMajorDistanceThreshold());
        ContextCompat.startForegroundService(context, intent);
        Log.d(TAG, "startForegroundService: " + R.string.monitoring_on);
    }

    private void stopForegroundService() {
        stopService(new Intent(context, ForegroundService.class));
        Log.d(TAG, "stopForegroundService: " + R.string.monitoring_off);
    }

    private void registerReceiver() {
        context.registerReceiver(directionsReceiver, new IntentFilter(DIRECTION_BROADCAST), Context.RECEIVER_EXPORTED);
        Log.d(TAG, "registerReceiver: DIRECTION_BROADCAST registered");
    }

    private void unregisterReceiver() {
        context.unregisterReceiver(directionsReceiver);
        Log.d(TAG, "unregisterReceiver: DIRECTION_BROADCAST unregistered");
    }

    /**
     * Retrieve Battery Info and update UI
     */
    private void refreshBatteryInfo(boolean onlyOnce){
        Log.d(TAG, "refreshBatteryInfo: " + miband.isPaired());
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

    private Integer[] getPatternFromDirection(String d) {
        Integer[] noVibration = new Integer[]{};
        if(Directions.isUTurn(d)) return CustomVibration.generatePattern(300,100,4);
        else if (Directions.isLeft(d)) return (selectedVibrationMode != 2) ? CustomVibration.LEFT_PULSE:noVibration;
        else if (Directions.isRight(d)) return (selectedVibrationMode != 1) ? CustomVibration.RIGHT_PULSE:noVibration;
        else switch (d) {
                case Directions.STRAIGHT:
                    return noVibration;
                case Directions.ALTERNATE:
                    return CustomVibration.FROWN;
                default:
                    return CustomVibration.generatePattern("600",",");
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