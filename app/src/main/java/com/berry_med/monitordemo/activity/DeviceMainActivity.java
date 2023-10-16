package com.berry_med.monitordemo.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//import ffc.app.R;
import com.berry_med.monitordemo.bluetooth.BTController;
import com.berry_med.monitordemo.data.DataParser;
import com.berry_med.monitordemo.data.ECG;
import com.berry_med.monitordemo.data.NIBP;
import com.berry_med.monitordemo.data.SpO2;
import com.berry_med.monitordemo.data.Temp;
import com.berry_med.monitordemo.dialog.BluetoothDeviceAdapter;
import com.berry_med.monitordemo.dialog.SearchDevicesDialog;
import com.berry_med.monitordemo.view.WaveformView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import th.in.ffc.R;

public class DeviceMainActivity extends AppCompatActivity implements BTController.Listener, DataParser.onPackageReceivedListener {


    private BTController mBtController;

    //UI
    private Button btnBtCtr;
    private TextView tvBtinfo;
    private TextView tvECGinfo;
    private TextView tvSPO2info;
    private TextView tvTEMPinfo;
    private TextView tvNIBPinfo;
    private Button btnSave;
    private LinearLayout llAbout;
    private TextView tvFWVersion;
    private TextView tvHWVersion;
    private WaveformView wfSpO2;
    private WaveformView wfECG;

    //Bluetooth
    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    BluetoothDeviceAdapter mBluetoothDeviceAdapter;
    SearchDevicesDialog mSearchDialog;
    ProgressDialog mConnectingDialog;
    ArrayList<BluetoothDevice> mBluetoothDevices;

    //data
    DataParser mDataParser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_main);
        initData();
        initView();
        // setToolBar();
        addEvent();
    }


    private void initData() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

            ) {
                requestPermissionsIfNecessary(new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_ADVERTISE,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
                return;
            }
        }
        else {
            // enable the Bluetooth Adapter
            BTController bt = new BTController(this);
            mBtController = bt.getDefaultBTController(this);
            mBtController.registerBroadcastReceiver(this);

            mBtController.enableBtAdpter();

            mDataParser = new DataParser(this);
            mDataParser.start();
        }

    }

    private void initView() {
        //UI widgets
        btnSave = (Button) findViewById(R.id.btnSave);
        btnBtCtr = (Button) findViewById(R.id.btnBtCtr);
        tvBtinfo = (TextView) findViewById(R.id.tvbtinfo);
        tvECGinfo = (TextView) findViewById(R.id.tvECGinfo);
        tvSPO2info = (TextView) findViewById(R.id.tvSPO2info);
        tvTEMPinfo = (TextView) findViewById(R.id.tvTEMPinfo);
        tvNIBPinfo = (TextView) findViewById(R.id.tvNIBPinfo);
//        llAbout = (LinearLayout) findViewById(R.id.llAbout);
//        tvFWVersion = (TextView) findViewById(R.id.tvFWverison);
//        tvHWVersion = (TextView) findViewById(R.id.tvHWverison);

        //Bluetooth Search Dialog
        mBluetoothDevices = new ArrayList<>();

        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(DeviceMainActivity.this, mBluetoothDevices);
        //mBtController.setBluetoothDeviceAdapter(mBluetoothDeviceAdapter);
        mSearchDialog = new SearchDevicesDialog(DeviceMainActivity.this, mBluetoothDeviceAdapter) {
            @Override
            public void onStartSearch() {
                mBluetoothDevices.clear();
                //mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(DeviceMainActivity.this,mBluetoothDevices);
                mBtController.startScan(true);

            }

            @Override
            public void onClickDeviceItem(int pos) {
                BluetoothDevice device = mBluetoothDevices.get(pos);
                mBtController.startScan(false);
                mBtController.connect(DeviceMainActivity.this, device);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED

                    ) {
                        requestPermissionsIfNecessary(new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_ADVERTISE,
                                Manifest.permission.BLUETOOTH_SCAN,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        });
                        return;
                    }
                }
                tvBtinfo.setText(device.getName() + ": " + device.getAddress());
                mConnectingDialog.show();
                mSearchDialog.dismiss();
            }
        };
        mSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBtController.startScan(false);
            }
        });

        mConnectingDialog = new ProgressDialog(DeviceMainActivity.this);
        mConnectingDialog.setMessage("Connecting...");

        //About Information
//        llAbout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBtController.write(DataParser.CMD_FW_VERSION);
//                mBtController.write(DataParser.CMD_HW_VERSION);
//            }
//        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
                finish();
            }
        });
        //SpO2 & ECG waveform
        wfSpO2 = (WaveformView) findViewById(R.id.wfSpO2);
        wfECG = (WaveformView) findViewById(R.id.wfECG);

    }
    private void requestPermissionsIfNecessary(String[] permissions) {
        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }
        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }
       private void SaveData()
       {
           Intent intent = new Intent();
           intent.putExtra("ECGInfo",tvECGinfo.getText());
           intent.putExtra("TEMPInfo",tvTEMPinfo.getText());
           intent.putExtra("NIBPInfo",tvNIBPinfo.getText());
           intent.putExtra("SPO2Info",tvSPO2info.getText());
           destroyBluetooth();
           setResult(RESULT_OK,intent);
       }
       private void destroyBluetooth(){
           if (mBtController.isBTConnected()) {
               mBtController.disconnect();
               mBtController.disableBtAdpter();
               mBtController.unregisterBroadcastReceiver(this);
               tvBtinfo.setText("");
           }
       }
        public void onClick (View v){
        switch (v.getId()) {
            case R.id.btnBtCtr:
                if(mBtController==null) {
                    BTController bt = new BTController(this);
                    mBtController = bt.getDefaultBTController(this);
                    mBtController.registerBroadcastReceiver(this);

                    mBtController.enableBtAdpter();

                    mDataParser = new DataParser(this);
                    mDataParser.start();
                }
                if (!mBtController.isBTConnected()) {

                    mBluetoothDevices.clear();

                    mSearchDialog.show();
                    mSearchDialog.startSearch();
                    mBtController.enableBtAdpter();
                    mBtController.startScan(true);
                    mBluetoothDeviceAdapter.notifyDataSetChanged();

                } else {
                    mBtController.disconnect();
                    tvBtinfo.setText("");
                }
                break;
            case R.id.btnNIBPStart:
                mBtController.write(DataParser.CMD_START_NIBP);
                break;
            case R.id.btnNIBPStop:
                mBtController.write(DataParser.CMD_STOP_NIBP);
                break;
            case R.id.btnSave:
//                mBtController.disconnect();
//                mBtController.disableBtAdpter();
//                mBtController=null;
//                break;

        }
    }

        @Override
        public void onDestroy () {
            super.onDestroy();
//            mSearchDialog.dismiss();
//            mSearchDialog = null;
//            mBtController.disableBtAdpter();
            try {
                if(mBtController.isRegistered()) {
                    mBtController.unregisterBroadcastReceiver(this);
                }
            }
            catch(Exception e){
            }
            finish();
    }


        //BTController implements
        @Override
        public void onFoundDevice (BluetoothDevice device){
        if (mBluetoothDevices.contains(device)) {
            return;
        }
        mBluetoothDevices.add(device);
            Log.d("device:",mBluetoothDevices.toString());
        mBluetoothDeviceAdapter.notifyDataSetChanged();
    }

        @Override
        public void onStopScan () {
            mSearchDialog.stopSearch();
    }

        @Override
        public void onStartScan () {
        mBluetoothDevices.clear();
        mBluetoothDeviceAdapter.notifyDataSetChanged();
    }

        @Override
        public void onConnected () {
        mConnectingDialog.setMessage("Connected √");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mConnectingDialog.dismiss();
                    }
                });
            }
        }, 800);

        btnBtCtr.setText("Disconnect");
    }

        @Override
        public void onDisconnected () {
            mBluetoothDevices.clear();
            //mBtController.disableBtAdpter();
            btnBtCtr.setText("Search Devices");

    }

        @Override
        public void onReceiveData ( byte[] dat){
        mDataParser.add(dat);
    }


        //DataParser implements
        @Override
        public void onSpO2WaveReceived ( int dat){
        wfSpO2.addAmp(dat);
    }

        @Override
        public void onSpO2Received ( final SpO2 spo2){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(spo2.toString().indexOf("-")<0) {
                    tvSPO2info.setText(spo2.toString());
                }
            }
        });
    }

        @Override
        public void onECGWaveReceived ( int dat){
        wfECG.addAmp(dat);
    }

        @Override
        public void onECGReceived ( final ECG ecg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(ecg.toString().indexOf("-")<0) {
                    tvECGinfo.setText(ecg.toString());
                }
            }
        });
    }

        @Override
        public void onTempReceived ( final Temp temp){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTEMPinfo.setText(temp.toString());
            }
        });
    }

        @Override
        public void onNIBPReceived ( final NIBP nibp){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvNIBPinfo.setText(nibp.toString());
            }
        });
    }

        @Override
        public void onFirmwareReceived ( final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvFWVersion.setText("Firmware Version:" + str);
            }
        });
    }

        @Override
        public void onHardwareReceived ( final String str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvHWVersion.setText("Hardware Version:" + str);
            }
        });
    }
        private void setToolBar ()
        {
//            androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);
//            if (getSupportActionBar() != null) {
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
////            getSupportActionBar().setDisplayShowHomeEnabled(true);
//                getSupportActionBar().setDisplayShowTitleEnabled(false);
//            }
//            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    SaveData();
//                    finish();
//                }
//            });
        }
        private void addEvent()
        {
            ImageButton homeAsUp  = findViewById(R.id.homeAsUp);
            homeAsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        destroyBluetooth();
                    }
                    catch(Exception e){
                    }
                    finish();
                }
            });
        }
    }

