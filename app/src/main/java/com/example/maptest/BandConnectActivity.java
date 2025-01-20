package com.example.maptest;

import static com.example.maptest.Constants.DEVICE_CONNECTED;
import static com.example.maptest.Constants.DEVICE_NULL;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jashgopani.github.io.mibandsdk.MiBand;

public class BandConnectActivity extends AppCompatActivity implements ScanResultsAdapter.OnScannedDeviceListener{

    private static final long SCAN_PERIOD = 6000;
    //private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "BandConnectActivity";
    private final Context context = BandConnectActivity.this;
    private BluetoothAdapter bluetoothAdapter;
    private ToggleButton scanBtn, connectBtn;
    private TextView statusTv;
    private boolean isScanning;
    private HashSet<String> addressHashSet;
    private ArrayList<BluetoothDevice> deviceArrayList;
    private ProgressBar progressBar;
    private RecyclerView scanRv;
    private ScanResultsAdapter scanResultsAdapter;
    private BluetoothDevice currentDevice;
    private boolean connected, paired;
    private MiBand miBand;
    int requestCode;
    CompositeDisposable disposables;
    private Map<String, Boolean> permissionStatus;

    private ActivityResultLauncher<String[]> permissionsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_band_connect);
        Log.d(TAG, "onCreate: BandConnectActivity + "+MiBand.getInstance(BandConnectActivity.this).getDevice());
        initializeClassFields();
        findViews();
        configureViews();
        setEventListeners();
        checkDeviceCompatibility();
        updateUIControls();
    }

    //methods used by onCreate
    private void initializeClassFields() {
        requestCode = getIntent().getIntExtra("requestCode", -1);
        addressHashSet = new HashSet<>();
        deviceArrayList = new ArrayList<>();
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        scanResultsAdapter = new ScanResultsAdapter(deviceArrayList, this);
        miBand = MiBand.getInstance(context);
        paired = miBand.isPaired();
        try{
            currentDevice = miBand.getDevice();
            Log.d(TAG, "initializeClassFields: " + currentDevice);
        }catch (Exception e){
            currentDevice = null;
        }
        disposables = new CompositeDisposable();
        //if band is connected then subscribe to the connection subject to get the live status
        if (paired) {
            disposables.add(miBand.connect(null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(handleConnectionNext(), handleConnectionError(),handleConnectionComplete()));
        }
        setResult(DEVICE_NULL);
        permissionStatus = new HashMap<>();
        permissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), permissionStatus::putAll);
    }

    private void findViews() {
        scanBtn = findViewById(R.id.scan_btn);
        connectBtn = findViewById(R.id.connect_btn);
        statusTv = findViewById(R.id.status_tv);
        scanRv = findViewById(R.id.scan_rv);
        progressBar = findViewById(R.id.progressBar);
    }

    private void configureViews() {
        scanRv.setAdapter(scanResultsAdapter);
        scanRv.setLayoutManager(new LinearLayoutManager(context));
    }

    private void setEventListeners() {
        //scans nearby BLE devices and stops scanning in SCAN_PERIOD time
        scanBtn.setOnClickListener((buttonView) -> {
            boolean isChecked = scanBtn.isChecked();

            Log.d(TAG, "Find Device Btn : " + isChecked);
            //change the scanning status
            isScanning = isChecked;

            if(isChecked){
                resetAdapterData();
                //subscribe to scanCallbacks observer
                disposables.add(miBand.startScan(SCAN_PERIOD)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(handleScanResult(),handleScanError(), handleScanComplete()));
            }else {
                disposables.add(miBand.stopScan()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(handleScanResult(),handleScanError()));
            }
            updateUIControls();
        });

        //connects to the selected device
        connectBtn.setOnClickListener((buttonView) -> {
            boolean isChecked = connectBtn.isChecked();

            if(isChecked)
                connectAndPair();
            else
                disconnectAndUnpair();
            updateUIControls();
        });

    }

    private void checkDeviceCompatibility() {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        String[] permissions = {
                //Manifest.permission.ACCESS_COARSE_LOCATION,
                //Manifest.permission.ACCESS_FINE_LOCATION,
                //Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT,
        };
        permissionsLauncher.launch(permissions);

        //check bluetooth enabled or not
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                //ActivityCompat.requestPermissions(BandConnectActivity.this, permissions, 0);
                // TODO: Consider calling ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                BandConnectActivity.this.startActivity(enableBtIntent);
            } else {
                finish();
            }
        }

    }

    // methods used by event listeners
    private void resetAdapterData() {
        addressHashSet.clear();
        deviceArrayList.clear();
        scanResultsAdapter.updateList(deviceArrayList);
    }

    private void connectAndPair() {
        toast("Connecting...");
        updateUIControls();
        statusTv.setText(R.string.connecting);
        deviceArrayList.clear();
        scanResultsAdapter.updateList(deviceArrayList);
        disposables.add(miBand.connect(currentDevice)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(handleConnectionNext(), handleConnectionError(),handleConnectionComplete()));
        updateUIControls();
    }

    private void disconnectAndUnpair() {
        updateUIControls();
        updateStatusTv(String.valueOf(R.string.disconnecting));
        miBand.disconnect(true);
        updateUIControls();
    }

    //UI related methods
    private void updateUIControls() {
        //update button state and textviews
        progressBar.setVisibility(isScanning ? View.VISIBLE : View.INVISIBLE);

        //only toggle the find device button if any device is not connected
        scanBtn.setClickable(!isScanning && !paired);
        scanBtn.setAlpha(scanBtn.isClickable() ? 1f : 0.2f);
        if(!paired)scanBtn.setChecked(isScanning);

        statusTv.setTextColor(isScanning ? Color.LTGRAY : !deviceArrayList.isEmpty() ? Color.BLUE :paired?Color.GREEN:Color.RED);
        connectBtn.setClickable(!isScanning && currentDevice!=null);
        connectBtn.setAlpha(connectBtn.isClickable() ? 1f : 0.2f);
        connectBtn.setChecked(paired);

        String mac = currentDevice==null?"":currentDevice.toString();
        connectBtn.setTextOn("Disconnect "+mac);
        connectBtn.setTextOff("Connect "+mac);
        updateStatusTv();
    }


    private void updateStatusTv() {
        if (currentDevice == null) {
            statusTv.setText(R.string.status_doScan);
        } else {
            statusTv.setText(currentDevice.getAddress());
            statusTv.setTextColor(Color.BLUE);
        }
    }

    private void updateStatusTv(String statusText){
        statusTv.setText(statusText);
    }


    /**
     * Handles click event on Recycler View
     * @param position the position of the click
     */
    @Override
    public void onDeviceClick(int position) {
        //onclick listener for recycler view item
        if (!isScanning) {//click works only if scanning is complete
            BluetoothDevice device = deviceArrayList.get(position);
            currentDevice = device;
            Log.d(TAG, "onDeviceClick: " + position + " | " + device);
        }
        updateUIControls();
    }

    /**
     * Utility toast method
     * @param msg message that goes in the Toast
     */
    private void toast(final String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    //methods for handling observable results

    /**
     * Handle onComplete of scanning method
     * @return action that updates the UI controls
     */
    private Action handleScanComplete() {
        return () -> {
            isScanning = false;
            updateUIControls();
        };
    }

    /**
     * Handle errors from Scanning method
     * @return consumer that logs error
     */
    private Consumer<? super Throwable> handleScanError() {
        return (Consumer<Throwable>) throwable -> {
            Log.d(TAG, "Scanning Error Received: \n");
            if (throwable != null) {
                Log.d(TAG, throwable.getMessage() == null ? "NULL MESSAGE" : throwable.getMessage());
                Log.d(TAG, Arrays.toString(throwable.getStackTrace()));
            }
        };
    }

    /**
     * Handle each device detected while scanning ble devices
     * @return ScanResult : It is the result given by BLE ScanCallback Use getDevice method to handle
     */
    private Consumer<? super ScanResult> handleScanResult() {
        return (Consumer<ScanResult>) result -> {
            //this method handles the scan results
            BluetoothDevice device = result.getDevice();
            if (addressHashSet.add(device.getAddress())) {
                deviceArrayList.add(device);
                String st = "Found " + deviceArrayList.size() + " devices";
                statusTv.setText(st);
                Log.d(TAG, "leScanCallBack: New device added : " + device.getAddress());
                scanResultsAdapter.updateList(deviceArrayList);
            }
        };
    }
    /**
     * Handle onNext result of connectionSubject
     * it emits true when connected and false when disconnected
     * @return handling result value
     */
    private Consumer<? super Integer> handleConnectionNext() {
        return (Consumer<Integer>) result->{
            //if result is true = connection successful
            //else disconnect successful
            Log.d(TAG, "handleConnectionNext: From connectionSubject : "+MiBand.getStatus(result));
            if(result==MiBand.PAIRED){
                paired = true;
                setResult(DEVICE_CONNECTED);
                if (requestCode != -1) {
                    finish();
                }
            }else{
                paired=false;
            }
            updateUIControls();
        };
    }

    /**
     * Handle onError result of connectionSubject
     * @return Action to be performed on receiving any error
     */
    private Consumer<? super Throwable> handleConnectionError() {
        return (Consumer<Throwable>)error -> {
            paired = false;
            toast(error.getMessage());
            updateUIControls();
        };
    }

    /**
     * Handle onComplete result of connectionSubject
     * @return Action to be performed. onComplete does emit any value
     */
    private Action handleConnectionComplete() {
        return () -> {
            //onComplete Method
            Log.d(TAG, "handleConnectionComplete: Band Disconnected");
            paired = false;
            updateUIControls();
        };
    }


    //Other Activity Lifecycle methods
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if(miBand.getDevice()==null){
//            toast("You Need To Connect to a device first");
            setResult(DEVICE_NULL);
            finish();
        }else{
            setResult(DEVICE_CONNECTED);
            finish();
        }
    }
}