package th.in.ffc;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import rd.nalib.ExceptionNA;
import rd.nalib.NA;
import rd.nalib.ResponseListener;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.app.form.screening.dao.SfCardReadingHistoryDao;
import th.in.ffc.app.form.screening.model.SfCardReadingHistory;
import th.in.ffc.security.LoginActivity;
import th.in.ffc.security.LoginFragment;

public class SmartCardReaderActivity extends AppCompatActivity {

    public static final int MY_STORAGE_PERMISSION = 0x1;
    public static final int MY_LOCATION_PERMISSION = 0x2;
    public static final int REQUEST_ALL_FILE_PERMISSION = 0x3;
    public static final int REQUEST_STORAGE_PERMISSION = 0x4;
    private final int sleepTime = 10;     // = 10 ms
    private final int NA_POPUP = 0x80;
    private final int NA_FIRST = 0x40;
    private final int NA_RESERVE2 = 0x20;
    private final int NA_SCAN = 0x10;
    private final int NA_BLE1 = 0x08;
    private final int NA_BLE0 = 0x04;
    private final int NA_BT = 0x02;
    private final int NA_USB = 0x01;

    private final int NA_NO_ATEXT = 0x00;
    private final int NA_ATEXT = 0x01;

    private String NAVersion;
    private byte[] byteRes = null;
    private boolean bReturnResponseFinish = false;
    private NA NALibs;
    private MaterialButton bt_SelectReader, bt_Read, bt_UpdateLicense, bt_Exit;
    private TextView tv_Reader, tv_Result, tv_SoftwareInfo, tv_LicenseInfo;
    private ImageView iv_Photo;
    private MyHandler mHandler;
    private final Handler handler = new Handler();
    private int iRes = -999;
    private ArrayList<String> aRes = null;
    private String sRes = "";
    private ProgressDialog progressDialog;
    String readerSelect = "";

    // Collapsible views
    private LinearLayout steps_header;
    private LinearLayout steps_content;
    private ImageView steps_expand_icon;
    private LinearLayout actions_header;
    private LinearLayout actions_content;
    private ImageView actions_expand_icon;
    private boolean stepsExpanded = true;
    private boolean actionsExpanded = true;
    private String readResult = "";

    ResponseListener responseListener = new ResponseListener() {
        @Override
        public void onOpenLibNA(int i) {
            iRes = i;
            bReturnResponseFinish = true;
        }

        @Override
        public void onGetReaderListNA(ArrayList<String> arrayList, int i) {
            iRes = i;
            aRes = arrayList;
            bReturnResponseFinish = true;
        }

        @Override
        public void onSelectReaderNA(final int i) {
            iRes = i;
            bReturnResponseFinish = true;
        }

        @Override
        public void onGetNIDNumberNA(String s, int i) {
            iRes = i;
            sRes = s;
            bReturnResponseFinish = true;
        }

        @Override
        public void onGetNIDTextNA(final String s, int i) {
            iRes = i;
            sRes = s;
            bReturnResponseFinish = true;
        }

        @Override
        public void onGetNIDPhotoNA(byte[] bytes, int i) {
            iRes = i;
            byteRes = bytes;
            bReturnResponseFinish = true;
        }

        @Override
        public void onUpdateLicenseFileNA(int i) {
            iRes = i;
            bReturnResponseFinish = true;
        }
    };
    private boolean flagSetting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_card_reader);

        // ตั้งค่า Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("เครื่องอ่านบัตรประชาชน");

        try {
            NAVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // สร้าง ProgressDialog
        progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);

        HandlerThread myThread = new HandlerThread("Worker Thread");
        myThread.start();
        Looper mLooper = myThread.getLooper();
        mHandler = new MyHandler(mLooper);

        // ค้นหา Views ด้วย ID
        tv_Reader = findViewById(R.id.tv_Reader);
        tv_SoftwareInfo = findViewById(R.id.tv_SoftwareInfo);
        tv_LicenseInfo = findViewById(R.id.tv_LicenseInfo);
        tv_Result = findViewById(R.id.tv_Result);
        iv_Photo = findViewById(R.id.iv_Photo);

        // ค้นหาปุ่มแบบ MaterialButton
        bt_SelectReader = findViewById(R.id.bt_SelectReader);
        bt_Read = findViewById(R.id.bt_Read);
        bt_UpdateLicense = findViewById(R.id.bt_UpdateLicense);
        bt_Exit = findViewById(R.id.bt_Exit);

        // ค้นหา Collapsible Views
        steps_header = findViewById(R.id.steps_header);
        steps_content = findViewById(R.id.steps_content);
        steps_expand_icon = findViewById(R.id.steps_expand_icon);
        actions_header = findViewById(R.id.actions_header);
        actions_content = findViewById(R.id.actions_content);
        actions_expand_icon = findViewById(R.id.actions_expand_icon);

        // ตั้งค่า Click listeners สำหรับ collapsible headers
        setupCollapsibleViews();

        // กำหนด OnClickListener ให้กับปุ่ม
        bt_SelectReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_Result.setText("");
                tv_Result.setClickable(false);
                iv_Photo.setImageResource(R.drawable.ic_person);

                // แสดง Progress Dialog
                progressDialog.setMessage("กำลังค้นหาเครื่องอ่านบัตร...");
                progressDialog.show();

                Message msg = mHandler.obtainMessage();
                msg.obj = "findreader";
                mHandler.sendMessage(msg);
            }
        });

        bt_Read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_Result.setText("");
                tv_Result.setClickable(false);
                iv_Photo.setImageResource(R.drawable.ic_person);

                // แสดง Progress Dialog
                progressDialog.setMessage("กำลังอ่านข้อมูลบัตร...");
                progressDialog.show();

                Message msg = mHandler.obtainMessage();
                msg.obj = "read";
                mHandler.sendMessage(msg);
            }
        });

        // เปลี่ยน OnClickListener สำหรับปุ่ม Update License (ตอนนี้เป็นไอคอน)
        bt_UpdateLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_Result.setText("");
                tv_Result.setClickable(false);
                iv_Photo.setImageResource(R.drawable.ic_person);

                // แสดง Progress Dialog
                progressDialog.setMessage("กำลังอัปเดตลิขสิทธิ์...");
                progressDialog.show();

                Message msg = mHandler.obtainMessage();
                msg.obj = "updatelicense";
                mHandler.sendMessage(msg);
            }
        });

        // เปลี่ยนข้อความปุ่ม Exit เป็น "บันทึกและกลับสู่หน้าหลัก"
        bt_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*================= Deselect Reader =================*/
                NALibs.deselectReaderNA();

                /*================= Close Lib =================*/
                NALibs.closeLibNA();

                try {
                    // เตรียมข้อมูลรูปภาพจาก ImageView
                    Bitmap bitmap = null;

                    // ตรวจสอบประเภทของ Drawable ที่อยู่ใน ImageView
                    Drawable drawable = iv_Photo.getDrawable();
                    if (drawable instanceof BitmapDrawable) {
                        // ถ้าเป็น BitmapDrawable ให้ดึง Bitmap ออกมาโดยตรง
                        bitmap = ((BitmapDrawable) drawable).getBitmap();
                    } else if (drawable instanceof VectorDrawable) {
                        // ถ้าเป็น VectorDrawable ให้วาดลงบน Bitmap ใหม่
                        bitmap = Bitmap.createBitmap(
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                    } else if (drawable != null) {
                        // สำหรับ Drawable ประเภทอื่นๆ
                        bitmap = Bitmap.createBitmap(
                                drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);
                    } else {
                        // ถ้าไม่มี Drawable ให้สร้าง Bitmap เปล่า
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person);
                    }

                    // แปลง Bitmap เป็น byte array ด้วยคุณภาพสูง
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    // ส่งข้อมูลกลับ
                    Intent intent = new Intent();
                    intent.putExtra("result", readResult);
                    intent.putExtra("image", byteArray);
                    setResult(Activity.RESULT_OK, intent);
                    showSnackbar("บันทึกข้อมูลเรียบร้อยแล้ว");
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    setResult(Activity.RESULT_CANCELED);
                    showSnackbar("ไม่สามารถบันทึกข้อมูลได้: " + e.getMessage());
                    finish();
                }
            }
        });

        NALibs = new NA(this);

        /*================= get Software Info =================*/
        clearReturnResponse();
        String[] data = new String[1];
        NALibs.getSoftwareInfoNA(data);
        if (data[0] != null) {
            tv_SoftwareInfo.setText("Software Info: " + data[0]);
        }

        NALibs.setListenerNA(responseListener);

        /************** Location Permission for Bluetooth reader *************/
        /*           Can remove this block if use USB reader only            */

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(SmartCardReaderActivity.this,
                    new String[]{android.Manifest.permission.BLUETOOTH_SCAN, android.Manifest.permission.BLUETOOTH_CONNECT},
                    MY_LOCATION_PERMISSION);
        } else {
            if (ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                AlertDialog dialog = new AlertDialog.Builder(SmartCardReaderActivity.this, R.style.DialogTheme).create();
                dialog.setTitle("การขออนุญาต");
                dialog.setMessage("โปรดอนุญาตให้เข้าถึงตำแหน่งที่ตั้งเพื่อค้นหาเครื่องอ่านบัตร Bluetooth");
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE, "ตกลง", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SmartCardReaderActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
                        dialog.dismiss();
                    }
                });
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);

            } else {
                ActivityCompat.requestPermissions(SmartCardReaderActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
            }
            init();
        }
    }

    /**
     * ตั้งค่าส่วนที่พับเก็บได้ (Collapsible Views)
     */
    private void setupCollapsibleViews() {
        // ส่วนขั้นตอนการอ่านบัตร
        steps_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStepsContent();
            }
        });

        // ส่วนการดำเนินการ (ปุ่มต่างๆ)
        actions_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleActionsContent();
            }
        });
    }

    /**
     * สลับการแสดงส่วนเนื้อหาขั้นตอนการอ่านบัตร
     */
    private void toggleStepsContent() {
        stepsExpanded = !stepsExpanded;

        if (stepsExpanded) {
            // ขยาย
            expandView(steps_content);
            steps_expand_icon.setImageResource(R.drawable.ic_expand_more);
        } else {
            // พับเก็บ
            collapseView(steps_content);
            steps_expand_icon.setImageResource(R.drawable.ic_expand_less);
        }
    }

    /**
     * สลับการแสดงส่วนเนื้อหาการดำเนินการ
     */
    private void toggleActionsContent() {
        actionsExpanded = !actionsExpanded;

        if (actionsExpanded) {
            // ขยาย
            expandView(actions_content);
            actions_expand_icon.setImageResource(R.drawable.ic_expand_more);
        } else {
            // พับเก็บ
            collapseView(actions_content);
            actions_expand_icon.setImageResource(R.drawable.ic_expand_less);
        }
    }

    /**
     * ขยายมุมมอง (View) ด้วย Animation
     */
    private void expandView(final View view) {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = view.getMeasuredHeight();

        // เริ่มต้นด้วยความสูง 0
        view.getLayoutParams().height = 0;
        view.setVisibility(View.VISIBLE);

        ValueAnimator animator = ValueAnimator.ofInt(0, targetHeight);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    /**
     * พับเก็บมุมมอง (View) ด้วย Animation
     */
    private void collapseView(final View view) {
        final int initialHeight = view.getMeasuredHeight();

        ValueAnimator animator = ValueAnimator.ofInt(initialHeight, 0);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                view.getLayoutParams().height = (int) animation.getAnimatedValue();
                view.requestLayout();
            }
        });
        animator.setDuration(300);
        animator.start();
    }

    public void init() {
        /*** set USB reader in-app permission ***/
        /***
         pms: 0 = Disable USB reader in-app permission (default).
         pms: 1 = Enable USB reader in-app permission.
         pms: -1 = Get current permissions state.
         ***/

        int pms = 1;
        NALibs.setPermissionsNA(pms);

        clearReturnResponse();

        String mNIDReader = "/" + "NASample";
        String rootFolder = getFilesDir() + mNIDReader;
        String LICFileName = "/" + "rdnidlib.dls";
        writeFile(rootFolder + LICFileName, "rdnidlib.dls");                         // Write file Licence

        /*===================== Open Libs =====================*/
        NALibs.openLibNA(rootFolder + LICFileName);

        if (iRes != 0) {
            tv_Result.setClickable(false);
            setText(tv_Result, "เปิดไลบรารี่ไม่สำเร็จ โปรดเริ่มแอปใหม่");
            showSnackbar("เปิดไลบรารี่ไม่สำเร็จ");
            bt_SelectReader.setEnabled(false);
            bt_Read.setEnabled(false);
            bt_UpdateLicense.setEnabled(false);
            bt_Exit.setEnabled(true);
            return;
        }

        /*================= get License Info =================*/
        clearReturnResponse();
        String[] data = new String[1];
        NALibs.getLicenseInfoNA(data);
        if (data[0] != null) {
            tv_LicenseInfo.setText("License Info: " + data[0]);
        }
        tv_Result.setText("");
        bt_SelectReader.setEnabled(true);
        bt_Read.setEnabled(true);
        bt_UpdateLicense.setEnabled(true);
        bt_Exit.setEnabled(true);
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int i = 0;
        for(String permission : permissions){
            if(permission.compareTo(android.Manifest.permission.BLUETOOTH_SCAN) == 0 || permission.compareTo(android.Manifest.permission.BLUETOOTH_CONNECT) == 0){
                if(grantResults[i] == PERMISSION_GRANTED){
                    init();
                    return;
                }
            }
            i++;
        }
    }

    @Override
    public void onBackPressed() {
        // สร้าง AlertDialog ยืนยันการออก
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("ยืนยันการออก");
        builder.setMessage("คุณต้องการบันทึกข้อมูลและออกจากโปรแกรมหรือไม่?");
        builder.setPositiveButton("บันทึกและออก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // ทำงานเหมือนกับการกดปุ่ม "บันทึกและกลับสู่หน้าหลัก"
                bt_Exit.performClick();
            }
        });
        builder.setNegativeButton("ยกเลิก", null);
        builder.show();
    }

    public void setText(TextView tv, final String message) {
        tv_Result.setClickable(false);
        final TextView textView = tv;
        handler.post(() -> textView.setText(message));
    }

    public void clearReturnResponse() {
        iRes = -999;
        sRes = "";
        aRes = null;
        byteRes = null;
    }

    public void setEnableButton(final boolean SelectReader, final boolean Read, final boolean UpdateLicense, final boolean Exit) {
        handler.post(() -> {
            bt_SelectReader.setEnabled(SelectReader);
            bt_Read.setEnabled(Read);
            bt_UpdateLicense.setEnabled(UpdateLicense);
            bt_Exit.setEnabled(Exit);

            // ซ่อน Progress Dialog เมื่อเสร็จสิ้น
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        });
    }

    public void writeFile(String Path, String Filename) {
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open(Filename);
            File out = new File(Path);
            if (out.exists())
                return;
            File parent = new File(out.getParent());
            parent.mkdirs();
            byte[] buffer = new byte[1024];
            FileOutputStream fos = new FileOutputStream(out);
            int read;

            while ((read = is.read(buffer, 0, 1024)) >= 0) {
                fos.write(buffer, 0, read);
            }

            fos.flush();
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void waitResponse() {
        while (!bReturnResponseFinish) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void printException(int ex, String OldText) {
        if (OldText.compareTo("") != 0) {
            OldText += "\n\n";
        }
        switch (ex) {
            case ExceptionNA.NA_INTERNAL_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -1: ข้อผิดพลาดภายในระบบ");
                break;

            case ExceptionNA.NA_INVALID_LICENSE:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -2: เครื่องอ่านบัตรนี้ไม่มีลิขสิทธิ์");
                break;

            case ExceptionNA.NA_READER_NOT_FOUND:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -3: ไม่พบเครื่องอ่านบัตร");
                showSnackbar("ไม่พบเครื่องอ่านบัตร");
                break;

            case ExceptionNA.NA_CONNECTION_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -4: การเชื่อมต่อบัตรผิดพลาด");
                showSnackbar("ไม่สามารถเชื่อมต่อกับบัตรได้");
                break;

            case ExceptionNA.NA_GET_PHOTO_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -5: ไม่สามารถอ่านรูปถ่ายได้");
                break;

            case ExceptionNA.NA_GET_TEXT_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -6: ไม่สามารถอ่านข้อมูลได้");
                break;

            case ExceptionNA.NA_INVALID_CARD:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -7: บัตรไม่ถูกต้อง");
                showSnackbar("บัตรไม่ถูกต้อง โปรดตรวจสอบว่าเป็นบัตรประชาชนที่สมบูรณ์");
                break;

            case ExceptionNA.NA_UNKNOWN_CARD_VERSION:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -8: ไม่รู้จักเวอร์ชันของบัตร");
                break;

            case ExceptionNA.NA_DISCONNECTION_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -9: การตัดการเชื่อมต่อผิดพลาด");
                break;

            case ExceptionNA.NA_INIT_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -10: การเริ่มต้นผิดพลาด");
                break;

            case ExceptionNA.NA_READER_NOT_SUPPORTED:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -11: ไม่รองรับเครื่องอ่านบัตรนี้");
                showSnackbar("ไม่รองรับเครื่องอ่านบัตรนี้");
                break;

            case ExceptionNA.NA_LICENSE_FILE_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -12: ไฟล์ลิขสิทธิ์มีปัญหา");
                break;

            case ExceptionNA.NA_PARAMETER_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -13: พารามิเตอร์ผิดพลาด");
                break;

            case ExceptionNA.NA_INTERNET_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -15: การเชื่อมต่ออินเทอร์เน็ตมีปัญหา");
                showSnackbar("กรุณาตรวจสอบการเชื่อมต่ออินเทอร์เน็ต");
                break;

            case ExceptionNA.NA_CARD_NOT_FOUND:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -16: ไม่พบบัตร");
                showSnackbar("ไม่พบบัตร กรุณาเสียบบัตรใหม่");
                break;

            case ExceptionNA.NA_BLUETOOTH_DISABLED:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -17: Bluetooth ถูกปิดอยู่");
                showSnackbar("Bluetooth ถูกปิดอยู่ กรุณาเปิดใช้งาน");
                break;

            case ExceptionNA.NA_LICENSE_UPDATE_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -18: การอัปเดตลิขสิทธิ์ผิดพลาด");
                break;

            case ExceptionNA.NA_STORAGE_PERMISSION_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด " + ExceptionNA.NA_STORAGE_PERMISSION_ERROR + ": ไม่ได้รับอนุญาตให้เข้าถึงพื้นที่จัดเก็บข้อมูล");
                tv_Result.setClickable(true);
                tv_Result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            if (!Environment.isExternalStorageManager()) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                                    intent.addCategory("android.intent.category.DEFAULT");
                                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                                    startActivityForResult(intent, REQUEST_ALL_FILE_PERMISSION);
                                } catch (Exception e) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivityForResult(intent, REQUEST_ALL_FILE_PERMISSION);
                                }
                            }

                            if (ContextCompat.checkSelfPermission(v.getContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                if (!flagSetting) {
                                    flagSetting = true;
                                    startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
                                }
                            }
                        } else {
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            if (!flagSetting) {
                                flagSetting = true;
                                startActivityForResult(intent, REQUEST_STORAGE_PERMISSION);
                            }
                        }
                    }
                });
                break;

            case ExceptionNA.NA_LOCATION_PERMISSION_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด " + ExceptionNA.NA_LOCATION_PERMISSION_ERROR + ": ไม่ได้รับอนุญาตให้เข้าถึงตำแหน่งที่ตั้ง");
                showSnackbar("กรุณาอนุญาตให้เข้าถึงตำแหน่งที่ตั้งเพื่อใช้งานเครื่องอ่านบัตร Bluetooth");
                tv_Result.setClickable(true);
                tv_Result.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        if (!flagSetting) {
                            flagSetting = true;
                            startActivity(intent);
                        }
                    }
                });
                break;

            case ExceptionNA.NA_BLUETOOTH_PERMISSION_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -33: ไม่ได้รับอนุญาต Bluetooth");
                showSnackbar("กรุณาอนุญาตให้ใช้งาน Bluetooth");
                break;

            case ExceptionNA.NA_LOCATION_SERVICE_ERROR:
                setText(tv_Result, OldText + "รหัสข้อผิดพลาด -41: บริการตำแหน่งที่ตั้งมีปัญหา");
                showSnackbar("กรุณาเปิดบริการระบุตำแหน่ง");
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flagSetting) {
            Intent intent = new Intent(this, SmartCardReaderActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            flagSetting = false;
        }
    }

    private String getCurrentUsername() {
        SharedPreferences prefs = getSharedPreferences(LoginActivity.PREFS_FILE, Context.MODE_PRIVATE);
        return prefs.getString(LoginActivity.EXTRA_USER, "");
    }

    class MyHandler extends Handler {

        MyHandler(Looper myLooper) {
            super(myLooper);
        }

        public void handleMessage(Message msg) {
            String message = (String) msg.obj;
            switch (message) {
//                case "findreader":
//                    // ใช้ค่าเดิม แต่ไม่รวม NA_POPUP เพื่อไม่ให้แสดง popup ขึ้นมาเอง
//                    int listOption = NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT + NA_USB;
//                    setEnableButton(false, false, false, false);
//
//                    /*================= get Reader List =================*/
//                    bReturnResponseFinish = false;
//                    clearReturnResponse();
//
//                    // ตรวจสอบและขอสิทธิ์ที่จำเป็น
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                        if ((listOption & NA_SCAN) != 0 && ((listOption & NA_BT) != 0 || (listOption & NA_BLE1) != 0 || (listOption & NA_BLE0) != 0)) {
//                            if (ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) == PERMISSION_GRANTED &&
//                                    ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                                // ใช้ค่าเดิม
//                            } else {
//                                listOption = listOption - (NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT);  // ลบการสแกน BT ออก
//                            }
//                        }
//                    } else {
//                        if ((listOption & NA_SCAN) != 0 && ((listOption & NA_BT) != 0 || (listOption & NA_BLE1) != 0 || (listOption & NA_BLE0) != 0)) {
//                            if (ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
//                                // ใช้ค่าเดิม
//                            } else {
//                                listOption = listOption - (NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT);  // ลบการสแกน BT ออก
//                            }
//                        }
//                    }
//
//                    NALibs.getReaderListNA(listOption);
//                    waitResponse();
//                    printException(iRes, "");
//
//                    if (iRes == 0) {
//                        if (aRes != null && aRes.size() > 0) {
//                            // แสดง dialog ให้ผู้ใช้เลือกอุปกรณ์
//                            handler.post(() -> {
//                                if (progressDialog != null && progressDialog.isShowing()) {
//                                    progressDialog.dismiss();
//                                }
//
//                                AlertDialog.Builder builder = new AlertDialog.Builder(SmartCardReaderActivity.this, R.style.DialogTheme);
//                                builder.setTitle("โปรดเลือกเครื่องอ่านบัตร");
//
//                                // สร้าง array ของชื่ออุปกรณ์
//                                String[] deviceNames = aRes.toArray(new String[0]);
//
//                                builder.setItems(deviceNames, (dialog, which) -> {
//                                    // เมื่อผู้ใช้เลือกอุปกรณ์ จะเรียกใช้งาน selectReaderNA
//                                    readerSelect = aRes.get(which);
//
//                                    // แสดง Progress Dialog อีกครั้ง
//                                    progressDialog.setMessage("กำลังเชื่อมต่อกับเครื่องอ่านบัตร...");
//                                    progressDialog.show();
//
//                                    // เลือกอุปกรณ์ที่ผู้ใช้เลือก
//                                    bReturnResponseFinish = false;
//                                    clearReturnResponse();
//                                    setText(tv_Reader, "กำลังเชื่อมต่อกับเครื่องอ่านบัตร...");
//                                    NALibs.selectReaderNA(readerSelect);
//
//                                    // รอผลลัพธ์และดำเนินการต่อเหมือนเดิม
//                                    Message msg2 = mHandler.obtainMessage();
//                                    msg2.obj = "selectreader";
//                                    msg2.arg1 = which;
//                                    mHandler.sendMessage(msg2);
//                                });
//
//                                builder.setNegativeButton("ยกเลิก", (dialog, which) -> {
//                                    setEnableButton(true, true, true, true);
//                                    dialog.dismiss();
//                                });
//
//                                builder.show();
//                            });
//                        } else {
//                            setText(tv_Reader, "ไม่พบเครื่องอ่านบัตร");
//                            showSnackbar("กรุณาตรวจสอบการเชื่อมต่อเครื่องอ่านบัตร");
//                            setEnableButton(true, true, true, true);
//                        }
//                    } else {
//                        setText(tv_Reader, "ไม่พบเครื่องอ่านบัตร");
//                        showSnackbar("ไม่สามารถค้นหาเครื่องอ่านบัตร โปรดตรวจสอบการเชื่อมต่อ");
//                        setEnableButton(true, true, true, true);
//                    }
//                    break;
                /*================= When Click [Find Reader Button]   =================*/
                case "findreader": {
//                    int listOption = NA_POPUP + NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT + NA_USB;     //0x9F USB & BLE Reader
//                    int listOption =  NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT + NA_USB;
                    int listOption =  NA_USB;
                    setEnableButton(false, false, false, false);

                    /*================= get Reader List =================*/
                    bReturnResponseFinish = false;
                    clearReturnResponse();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if ((listOption & NA_SCAN) != 0 && ((listOption & NA_BT) != 0 || (listOption & NA_BLE1) != 0 || (listOption & NA_BLE0) != 0)) {
                            if (ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, android.Manifest.permission.BLUETOOTH_SCAN) == PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, android.Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                                listOption = listOption;
                            } else {
                                listOption = listOption - (NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT);  //remove BT Scanning
                            }
                        }
                    } else {
                        if ((listOption & NA_SCAN) != 0 && ((listOption & NA_BT) != 0 || (listOption & NA_BLE1) != 0 || (listOption & NA_BLE0) != 0)) {
                            if (ActivityCompat.checkSelfPermission(SmartCardReaderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
                                listOption = listOption;
                            } else {
                                listOption = listOption - (NA_SCAN + NA_BLE1 + NA_BLE0 + NA_BT);  //remove BT Scanning
                            }
                        }
                    }

                    NALibs.getReaderListNA(listOption);

                    waitResponse();

                    printException(iRes, "");

                    if (iRes == 0) {
                        setEnableButton(true, true, true, true);
                        break;
                    }

                    if (iRes < 0) {
                        setText(tv_Reader, "Reader not found.");
                        setEnableButton(true, true, true, true);
                        break;
                    }

                    String readerSelect = aRes.get(0);

                    /*================= Select Reader =================*/
                    bReturnResponseFinish = false;
                    clearReturnResponse();
                    setText(tv_Reader, "Reader Selecting...");
                    NALibs.selectReaderNA(readerSelect);
                    waitResponse();

                    printException(iRes, "");

                    String[] data = new String[1];
                    NALibs.getLicenseInfoNA(data);
                    if (data[0] != null) {
                        setText(tv_LicenseInfo, "License Info: " + data[0]);
                    }

                    setEnableButton(true, true, true, true);
                    if (iRes != ExceptionNA.NA_SUCCESS && iRes != ExceptionNA.NA_INVALID_LICENSE && iRes != ExceptionNA.NA_LICENSE_FILE_ERROR) {
                        setText(tv_Reader, "Reader not found.");
                        break;
                    } else if (iRes == ExceptionNA.NA_INVALID_LICENSE || iRes == ExceptionNA.NA_LICENSE_FILE_ERROR) {
                        setText(tv_Reader, "Reader: " + readerSelect);
                        break;
                    }

                    setText(tv_Reader, "Reader: " + readerSelect);

                    data = new String[1];
                    if (NALibs.getReaderInfoNA(data) == 0) {
                        setText(tv_Result, "getReaderInfoNA: " + data[0]);
                    }

                    break;
                }

                case "selectreader":
                    // รอการตอบกลับจาก selectReaderNA ที่เรียกไปแล้ว
                    waitResponse();
                    printException(iRes, "");

                    String[] data = new String[1];
                    NALibs.getLicenseInfoNA(data);
                    if (data[0] != null) {
                        setText(tv_LicenseInfo, "License Info: " + data[0]);
                    }

                    setEnableButton(true, true, true, true);
                    if (iRes != ExceptionNA.NA_SUCCESS && iRes != ExceptionNA.NA_INVALID_LICENSE && iRes != ExceptionNA.NA_LICENSE_FILE_ERROR) {
                        setText(tv_Reader, "ไม่พบเครื่องอ่านบัตร");
                        showSnackbar("ไม่สามารถเชื่อมต่อกับเครื่องอ่านบัตรได้");
                        break;
                    } else if (iRes == ExceptionNA.NA_INVALID_LICENSE || iRes == ExceptionNA.NA_LICENSE_FILE_ERROR) {
                        setText(tv_Reader, "เครื่องอ่านบัตร: " + readerSelect);
                        showSnackbar("เครื่องอ่านบัตรไม่มีลิขสิทธิ์ กรุณาอัปเดตลิขสิทธิ์");
                        break;
                    }

                    setText(tv_Reader, "เครื่องอ่านบัตร: " + readerSelect);
                    showSnackbar("เชื่อมต่อกับเครื่องอ่านบัตรสำเร็จ");

                    data = new String[1];
                    if (NALibs.getReaderInfoNA(data) == 0) {
                        setText(tv_Result, "ข้อมูลเครื่องอ่านบัตร: " + data[0]);
                    }
                    break;

                case "read":
                    long startTime = System.currentTimeMillis();
                    setEnableButton(false, false, false, false);
                    setText(tv_Result, "");
                    handler.post(() -> iv_Photo.setImageResource(R.drawable.ic_person));

                    /*================= Connect Card =================*/
                    int result = NALibs.connectCardNA();
                    if (result != ExceptionNA.NA_SUCCESS) {
                        setEnableButton(true, true, true, true);
                        printException(result, "");
                        break;
                    }
                    /*================= Get NID Text =================*/
                    bReturnResponseFinish = false;
                    clearReturnResponse();
                    int getTextOption = NA_NO_ATEXT;
                    NALibs.getNIDTextNA(getTextOption);

                    waitResponse();
                    printException(iRes, "");

                    if (iRes != ExceptionNA.NA_SUCCESS) {
                        setEnableButton(true, true, true, true);
                        NALibs.disconnectCardNA();
                        break;
                    }
                    if (iRes == ExceptionNA.NA_SUCCESS) {
                        // แปลงข้อมูลดิบให้อยู่ในรูปแบบที่อ่านง่าย
                        String formattedData = formatThaiIDCardData(sRes);
                        setText(tv_Result, formattedData);
                    } else {
                        setText(tv_Result, sRes);
                    }
//                    setText(tv_Result, sRes);

                    // บันทึกประวัติการอ่านบัตร
                    try {
                        // แยกข้อมูลจาก sRes (ข้อความที่อ่านได้จากบัตร)
                        String[] lines = sRes.split("\n");
                        String citizenId = "";
                        String citizenName = "";

                        // หาเลขบัตรประชาชนและชื่อจากข้อความที่อ่านได้
                        for (String line : lines) {
                            if (line.contains("#")) {
                                String[] parts = line.split("#");
                                if (parts.length > 1) {
                                    citizenId = parts[0].trim();
                                    citizenName = parts[1].trim() + " " + parts[2].trim()+ " " + parts[4].trim();
                                }
                            }
                        }

                        // สร้าง model และบันทึกข้อมูล
                        SfCardReadingHistory history = new SfCardReadingHistory();
                        history.setReadTimestamp(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
                        history.setUsername(getCurrentUsername());
                        history.setCitizenId(citizenId);
                        history.setCitizenName(citizenName);

                        // ข้อมูลอุปกรณ์
                        history.setDeviceModel(Build.MODEL);
                        history.setDeviceBrand(Build.MANUFACTURER);

                        // ข้อมูลเครื่องอ่านบัตร (ถ้ามี)
                        String readerInfo = tv_Reader.getText().toString();
                        history.setCardReaderModel(readerInfo.replace("เครื่องอ่านบัตร: ", ""));

                        // ข้อมูลเพิ่มเติม
                        history.setAppVersion(NAVersion);
                        history.setReadStatus(iRes == 0 ? "SUCCESS" : "FAILED");
                        history.setNotes("");

                        // บันทึกข้อมูลลงฐานข้อมูล
                        SfCardReadingHistoryDao dao = new SfCardReadingHistoryDao(SmartCardReaderActivity.this);
                        dao.insert(history);

                        // แสดง Snackbar เมื่อบันทึกสำเร็จ
                        if (iRes == 0) {
                            showSnackbar("อ่านข้อมูลบัตรและบันทึกประวัติเรียบร้อยแล้ว");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        // บันทึกล้มเหลว แต่ไม่ควรหยุดการทำงานของแอพ
                    }

                    final long difference = System.currentTimeMillis() - startTime;
                    final BigDecimal bd = new BigDecimal(difference / 1000.0);
                    handler.post(() -> {
                        setText(tv_Result, tv_Result.getText().toString() + "\nอ่านข้อความ: " + bd.setScale(2, RoundingMode.HALF_UP) + " วินาที");
                    });

                    /*================= Get NID Photo =================*/
                    bReturnResponseFinish = false;
                    clearReturnResponse();
                    NALibs.getNIDPhotoNA();

                    waitResponse();

                    printException(iRes, tv_Result.getText().toString());
                    if (iRes == 0 && byteRes != null && byteRes.length > 0) {
                        try {
                            final Bitmap bMap = BitmapFactory.decodeByteArray(byteRes, 0, byteRes.length);
                            if (bMap != null) {
                                handler.post(() -> iv_Photo.setImageBitmap(bMap));
                            } else {
                                handler.post(() -> iv_Photo.setImageResource(R.drawable.ic_person));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            handler.post(() -> iv_Photo.setImageResource(R.drawable.ic_person));
                        }
                    }

                    /*================= Disconnect Card =================*/
                    NALibs.disconnectCardNA();

                    setEnableButton(true, true, true, true);

                    if (iRes >= 0) {
                        final long difference2 = System.currentTimeMillis() - startTime;
                        final BigDecimal bd2 = new BigDecimal(difference2 / 1000.0);
                        handler.post(() -> setText(tv_Result, tv_Result.getText().toString() + ", ข้อความ+รูปถ่าย: " + bd2.setScale(2, RoundingMode.HALF_UP) + " วินาที"));
                    }
                    break;

                case "updatelicense":
                    // ... (ส่วนอื่นๆ ยังคงเหมือนเดิม)
                    setEnableButton(false, false, false, false);

                    /*================= Update License File =================*/
                    bReturnResponseFinish = false;
                    clearReturnResponse();
                    NALibs.updateLicenseFileNA();
                    waitResponse();

                    /*================= Retry Update =================*/
                    if (iRes == ExceptionNA.NA_LICENSE_UPDATE_ERROR) {
                        bReturnResponseFinish = false;
                        clearReturnResponse();
                        NALibs.updateLicenseFileNA();
                        waitResponse();
                    }

                    printException(iRes, "");

                    if (iRes == 0 || iRes == 1 || iRes == 2 || iRes == 3) {
                        String[] licData = new String[1];
                        NALibs.getLicenseInfoNA(licData);
                        if (licData[0] != null) {
                            setText(tv_LicenseInfo, "License Info: " + licData[0]);
                        }
                        setText(tv_Result, "รหัส " + iRes + ": อัปเดตลิขสิทธิ์สำเร็จ");
                        showSnackbar("อัปเดตลิขสิทธิ์สำเร็จ");
                    } else if (iRes == 100 || iRes == 101 || iRes == 102 || iRes == 103) {
                        setText(tv_Result, "รหัส " + iRes + ": คุณกำลังใช้ลิขสิทธิ์ล่าสุดอยู่แล้ว");
                        showSnackbar("คุณกำลังใช้ลิขสิทธิ์ล่าสุดอยู่แล้ว");
                    }

                    setEnableButton(true, true, true, true);
                    break;
            }

        }
        /**
         * แปลงข้อมูลบัตรประชาชนให้อยู่ในรูปแบบที่อ่านง่าย
         */
        private String formatThaiIDCardData(String rawData) {
            readResult = rawData;
            if (rawData == null || rawData.isEmpty()) {
                return "";
            }

            StringBuilder result = new StringBuilder();
            String[] fields = rawData.split("#");

            // ตรวจสอบว่ามีข้อมูลเพียงพอ
            if (fields.length < 15) {
                return rawData; // คืนค่าข้อมูลเดิมหากรูปแบบไม่ถูกต้อง
            }

            try {
                // หมายเลขบัตรประชาชน
                result.append("เลขประจำตัวประชาชน: ").append(fields[0]).append("\n\n");

                // ชื่อ-นามสกุล ภาษาไทย
                result.append("ชื่อ-สกุล: ").append(fields[1]).append(fields[2]);
                if (!fields[3].isEmpty()) {
                    result.append(" ").append(fields[3]);
                }
                result.append(" ").append(fields[4]).append("\n");

                // ชื่อ-นามสกุล ภาษาอังกฤษ
                result.append("Name: ").append(fields[5]).append(fields[6]);
                if (!fields[7].isEmpty()) {
                    result.append(" ").append(fields[7]);
                }
                result.append(" ").append(fields[8]).append("\n\n");

                // ที่อยู่
                result.append("ที่อยู่: ").append(fields[9]);
                // ตรวจสอบว่ามีหมู่บ้านหรือไม่
                if (!fields[10].isEmpty()) {
                    result.append(" ").append(fields[10]);
                }
                // ตรวจสอบว่ามีถนนหรือไม่
                if (!fields[11].isEmpty()) {
                    result.append(" ").append(fields[11]);
                }
                // ซอย
                if (!fields[12].isEmpty()) {
                    result.append(" ").append(fields[12]);
                }

                // แขวง/ตำบล, เขต/อำเภอ, จังหวัด
                result.append("\n      แขวง/ตำบล").append(fields[13])
                        .append(" เขต/อำเภอ").append(fields[14])
                        .append(" ").append(fields[15]).append("\n\n");

                // วันเดือนปีเกิด
                if (fields.length > 17 && fields[17].length() == 8) {
                    String birthDate = fields[17];
                    String year = birthDate.substring(0, 4);
                    String month = birthDate.substring(4, 6);
                    String day = birthDate.substring(6, 8);

                    // แปลงปี พ.ศ. เป็น ค.ศ.
                    int yearCE = Integer.parseInt(year) - 543;

                    result.append("วันเกิด: ").append(day).append("/").append(month).append("/").append(year)
                            .append(" (").append(day).append("/").append(month).append("/").append(yearCE).append(")").append("\n");
                }

                // ข้อมูลอื่นๆ
                if (fields.length > 18) {
                    result.append("ออกให้โดย: ").append(fields[18]).append("\n");
                }

                // วันออกบัตร
                if (fields.length > 19 && fields[19].length() == 8) {
                    String issueDate = fields[19];
                    String issueYear = issueDate.substring(0, 4);
                    String issueMonth = issueDate.substring(4, 6);
                    String issueDay = issueDate.substring(6, 8);

                    result.append("วันออกบัตร: ").append(issueDay).append("/").append(issueMonth).append("/").append(issueYear).append("\n");
                }

                // วันบัตรหมดอายุ
                if (fields.length > 20 && fields[20].length() == 8) {
                    String expireDate = fields[20];
                    String expireYear = expireDate.substring(0, 4);
                    String expireMonth = expireDate.substring(4, 6);
                    String expireDay = expireDate.substring(6, 8);

                    result.append("วันบัตรหมดอายุ: ").append(expireDay).append("/").append(expireMonth).append("/").append(expireYear).append("\n");
                }

                // เลขลำดับบัตร (ถ้ามี)
                if (fields.length > 21) {
                    result.append("เลขลำดับบัตร: ").append(fields[21]);
                }

            } catch (Exception e) {
                // กรณีมีข้อผิดพลาดในการแปลงข้อมูล ให้คืนค่าข้อมูลเดิม
                return rawData;
            }

            return result.toString();
        }
    }
}