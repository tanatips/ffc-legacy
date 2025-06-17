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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.berry_med.monitordemo.bluetooth.BTController;
import com.berry_med.monitordemo.data.DataParser;
import com.berry_med.monitordemo.data.ECG;
import com.berry_med.monitordemo.data.NIBP;
import com.berry_med.monitordemo.data.SpO2;
import com.berry_med.monitordemo.data.Temp;
import com.berry_med.monitordemo.dialog.BluetoothDeviceAdapter;
import com.berry_med.monitordemo.dialog.SearchDevicesDialog;
import com.berry_med.monitordemo.view.WaveformView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import th.in.ffc.R;
import android.location.LocationManager;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.content.Context;

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
    private final int REQUEST_ENABLE_LOCATION = 1003;
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
        initView();
        addEvent();
        // เรียก initData หลังจาก UI พร้อมแล้ว
        initData();
    }

    private void initData() {
        // ตรวจสอบ permissions ก่อน
        if (!checkAllPermissions()) {
            requestAllPermissions();
            return;
        }

        // ตรวจสอบ Location Services
        if (!isLocationEnabled()) {
            Log.w("DeviceMainActivity", "Location Services is disabled");
            showLocationRequiredDialog();
            return;
        }

        // ถ้า permissions ครบและ Location เปิดแล้ว เริ่มใช้งาน Bluetooth
        initializeBluetooth();
    }

    private boolean checkAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ (API 31+)
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0+ (API 23+)
            return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true; // Android 5.1 และต่ำกว่า
    }

    private void requestAllPermissions() {
        ArrayList<String> permissionsToRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android 6.0+ permissions
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    permissionsToRequest.toArray(new String[0]),
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        } else {
            initializeBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                initializeBluetooth();
            } else {
                Toast.makeText(this, "ต้องการ Bluetooth และ Location permissions เพื่อใช้งาน", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initializeBluetooth() {
        if (mBtController == null) {
            BTController bt = new BTController(this);
            mBtController = bt.getDefaultBTController(this);
            mBtController.registerBroadcastReceiver(this);
            mBtController.enableBtAdpter();

            mDataParser = new DataParser(this);
            mDataParser.start();
        }
    }

    // แก้ไขใน method initView() เท่านั้น

    private void initView() {
        //UI widgets
        btnSave = (Button) findViewById(R.id.btnSave);
        btnBtCtr = (Button) findViewById(R.id.btnBtCtr);
        tvBtinfo = (TextView) findViewById(R.id.tvbtinfo);
        tvECGinfo = (TextView) findViewById(R.id.tvECGinfo);
        tvSPO2info = (TextView) findViewById(R.id.tvSPO2info);
        tvTEMPinfo = (TextView) findViewById(R.id.tvTEMPinfo);
        tvNIBPinfo = (TextView) findViewById(R.id.tvNIBPinfo);

        //Bluetooth Search Dialog
        mBluetoothDevices = new ArrayList<>();
        mBluetoothDeviceAdapter = new BluetoothDeviceAdapter(DeviceMainActivity.this, mBluetoothDevices);

        mSearchDialog = new SearchDevicesDialog(DeviceMainActivity.this, mBluetoothDeviceAdapter) {
            @Override
            public void onStartSearch() {
                mBluetoothDevices.clear();
                if (mBtController != null) {
                    Log.d("DeviceMainActivity", "Starting Bluetooth scan...");
                    mBtController.startScan(true);
                } else {
                    Log.e("DeviceMainActivity", "BTController is null!");
                    Toast.makeText(DeviceMainActivity.this, "Bluetooth not initialized", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onClickDeviceItem(int pos) {
                BluetoothDevice device = mBluetoothDevices.get(pos);
                if (mBtController != null) {
                    mBtController.startScan(false);
                    mBtController.connect(DeviceMainActivity.this, device);
                }

                // แก้ไข getContext() เป็น DeviceMainActivity.this
                String deviceInfo = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(DeviceMainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                        deviceInfo = device.getName() + ": " + device.getAddress();
                    } else {
                        deviceInfo = "Device: " + device.getAddress();
                    }
                } else {
                    // Android 10 และต่ำกว่า - ไม่ต้อง check BLUETOOTH_CONNECT permission
                    try {
                        String deviceName = device.getName();
                        deviceInfo = (deviceName != null ? deviceName : "Unknown Device") + ": " + device.getAddress();
                    } catch (SecurityException e) {
                        deviceInfo = "Device: " + device.getAddress();
                    }
                }

                tvBtinfo.setText(deviceInfo);
                mConnectingDialog.show();
                mSearchDialog.dismiss();
            }
        };

        mSearchDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mBtController != null) {
                    Log.d("DeviceMainActivity", "Stopping Bluetooth scan...");
                    mBtController.startScan(false);
                }
            }
        });

        mConnectingDialog = new ProgressDialog(DeviceMainActivity.this);
        mConnectingDialog.setMessage("Connecting...");

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
    // ลบ method requestPermissionsIfNecessary เดิม เพราะเราใช้ requestAllPermissions แทน

    private void SaveData() {
        Intent intent = new Intent();
        intent.putExtra("ECGInfo", tvECGinfo.getText());
        intent.putExtra("TEMPInfo", tvTEMPinfo.getText());
        intent.putExtra("NIBPInfo", tvNIBPinfo.getText());
        intent.putExtra("SPO2Info", tvSPO2info.getText());
        destroyBluetooth();
        setResult(RESULT_OK, intent);
    }

    private void destroyBluetooth() {
        if (mBtController != null) {
            if (mBtController.isBTConnected()) {
                mBtController.disconnect();
                mBtController.disableBtAdpter();
                mBtController.unregisterBroadcastReceiver(this);
                tvBtinfo.setText("");
            }
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBtCtr:
                // ตรวจสอบ permissions ก่อนใช้งาน Bluetooth
                if (!checkAllPermissions()) {
                    requestAllPermissions();
                    return;
                }

                // ตรวจสอบ Location Services
                if (!isLocationEnabled()) {
                    Log.w("DeviceMainActivity", "Location Services disabled, showing dialog");
                    showLocationRequiredDialog();
                    return;
                }

                if (mBtController == null) {
                    initializeBluetooth();
                }

                if (!mBtController.isBTConnected()) {
                    mBluetoothDevices.clear();
                    mSearchDialog.show();
                    mSearchDialog.startSearch();
                    mBtController.enableBtAdpter();
                    mBtController.startScan(true);
                    mBluetoothDeviceAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "กำลังค้นหาอุปกรณ์ Bluetooth...", Toast.LENGTH_SHORT).show();
                } else {
                    mBtController.disconnect();
                    tvBtinfo.setText("");
                }
                break;
            case R.id.btnNIBPStart:
                startNIBPMeasurement();
                break;
            case R.id.btnNIBPStop:
                if (mBtController != null) {
                    mBtController.write(DataParser.CMD_STOP_NIBP);
                }
                break;
            case R.id.btnSave:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mBtController != null && mBtController.isRegistered()) {
                mBtController.unregisterBroadcastReceiver(this);
            }
        } catch (Exception e) {
            Log.e("DeviceMainActivity", "Error in onDestroy", e);
        }
    }

    //BTController implements
    @Override
    public void onFoundDevice(BluetoothDevice device) {
        if (mBluetoothDevices.contains(device)) {
            return;
        }
        mBluetoothDevices.add(device);
        Log.d("device:", mBluetoothDevices.toString());
        mBluetoothDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStopScan() {
        if (mSearchDialog != null) {
            mSearchDialog.stopSearch();
        }
    }

    @Override
    public void onStartScan() {
        mBluetoothDevices.clear();
        mBluetoothDeviceAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnected() {
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
    public void onDisconnected() {
        mBluetoothDevices.clear();
        btnBtCtr.setText("Search Devices");
    }

    @Override
    public void onReceiveData(byte[] dat) {
        if (mDataParser != null) {
            mDataParser.add(dat);
        }
    }

    //DataParser implements
    @Override
    public void onSpO2WaveReceived(int dat) {
        if (wfSpO2 != null) {
            wfSpO2.addAmp(dat);
        }
    }

    @Override
    public void onSpO2Received(final SpO2 spo2) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (spo2.toString().indexOf("-") < 0) {
                    tvSPO2info.setText(spo2.toString());
                }
            }
        });
    }

    @Override
    public void onECGWaveReceived(int dat) {
        if (wfECG != null) {
            wfECG.addAmp(dat);
        }
    }

    @Override
    public void onECGReceived(final ECG ecg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (ecg.toString().indexOf("-") < 0) {
                    tvECGinfo.setText(ecg.toString());
                }
            }
        });
    }

    @Override
    public void onTempReceived(final Temp temp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTEMPinfo.setText(temp.toString());
            }
        });
    }

    @Override
    public void onNIBPReceived(final NIBP nibp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvNIBPinfo.setText(nibp.toString());

                // Get status code (bits 2-5)
                int statusCode = (nibp.getStatus() >> 2) & 0x0F;

                // Update UI based on status
                switch (statusCode) {
                    case NIBP.STATUS_TESTING:
                        // Show progress indicator
                        break;
                    case NIBP.STATUS_CUFF_LOOSE:
                        Toast.makeText(DeviceMainActivity.this, "Cuff is too loose - please adjust", Toast.LENGTH_SHORT).show();
                        break;
                    case NIBP.STATUS_ERROR:
                    case NIBP.STATUS_NO_RESULT:
                        Toast.makeText(DeviceMainActivity.this, "NIBP measurement failed - please try again", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    @Override
    public void onFirmwareReceived(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvFWVersion != null) {
                    tvFWVersion.setText("Firmware Version:" + str);
                }
            }
        });
    }

    @Override
    public void onHardwareReceived(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvHWVersion != null) {
                    tvHWVersion.setText("Hardware Version:" + str);
                }
            }
        });
    }

    private void addEvent() {
        ImageButton homeAsUp = findViewById(R.id.homeAsUp);
        if (homeAsUp != null) {
            homeAsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        destroyBluetooth();
                    } catch (Exception e) {
                        Log.e("DeviceMainActivity", "Error destroying bluetooth", e);
                    }
                    finish();
                }
            });
        }
    }

    private void startNIBPMeasurement() {
        if (mBtController != null) {
            // Set adult mode first (if not already set)
            mBtController.write(new byte[]{0x55, (byte) 0xaa, 0x04, 0x09, 0x01, (byte) 0xF1});

            // Set target pressure to 150mmHg (0x4B * 2 = 150)
            mBtController.write(new byte[]{0x55, (byte) 0xaa, 0x04, 0x0A, 0x4B, (byte) 0xA6});

            // Start NIBP measurement
            mBtController.write(DataParser.CMD_START_NIBP);
        }
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null) {
            return false;
        }

        // ตรวจสอบว่า GPS หรือ Network provider เปิดอยู่หรือไม่
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Log.d("LocationCheck", "GPS Enabled: " + gpsEnabled);
        Log.d("LocationCheck", "Network Enabled: " + networkEnabled);

        return gpsEnabled || networkEnabled;
    }
    private void showLocationRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("ต้องเปิด Location Services")
                .setMessage("การสแกนอุปกรณ์ Bluetooth ต้องการให้เปิด Location Services ก่อน\n\nคุณต้องการเปิดการตั้งค่า Location หรือไม่?")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("เปิดการตั้งค่า", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // เปิดหน้า Location Settings
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        try {
                            startActivityForResult(intent, REQUEST_ENABLE_LOCATION);
                        } catch (Exception e) {
                            // ถ้าไม่สามารถเปิดหน้า Location Settings ได้ ให้เปิดหน้า Settings หลัก
                            Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);
                            startActivity(settingsIntent);
                            Toast.makeText(DeviceMainActivity.this,
                                    "กรุณาเปิด Location ในส่วน Location Settings",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DeviceMainActivity.this,
                                "ไม่สามารถสแกน Bluetooth ได้หากไม่เปิด Location Services",
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_LOCATION) {
            // ตรวจสอบอีกครั้งว่า Location เปิดแล้วหรือยัง
            if (isLocationEnabled()) {
                Log.d("DeviceMainActivity", "Location enabled, initializing Bluetooth");
                Toast.makeText(this, "Location เปิดแล้ว! สามารถใช้งาน Bluetooth ได้", Toast.LENGTH_SHORT).show();

                // ถ้า permissions ครบแล้ว ให้เริ่มใช้งาน Bluetooth
                if (checkAllPermissions()) {
                    initializeBluetooth();
                }
            } else {
                Log.w("DeviceMainActivity", "Location still disabled after settings");
                Toast.makeText(this, "กรุณาเปิด Location Services เพื่อใช้งาน Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        Log.d("DeviceMainActivity", "onResume - checking permissions and location");

        // ตรวจสอบ permissions และ location เมื่อกลับมาที่หน้าจอ
        if (checkAllPermissions() && isLocationEnabled()) {
            if (mBtController == null) {
                Log.d("DeviceMainActivity", "Initializing Bluetooth in onResume");
                initializeBluetooth();
            }
        }
    }
    // ลบ methods ที่ซ้ำซ้อนใน onResume, checkAllBluetoothPermissions, requestBluetoothPermissions, initializeBluetooth
    // เพราะเราได้ refactor แล้วในส่วนบน
}