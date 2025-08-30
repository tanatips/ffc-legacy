/* ***********************************************************************
 *                                                                 _ _ _
 *                                                               ( _ _  |
 *                                                           _ _ _ _  | |
 *                                                          (_ _ _  | |_|
 *  _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | | 
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (    
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _ 
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _| 
 *  a member of NSTDA, @Thailand
 *  
 * ***********************************************************************
 *
 *
 * FFC-Plus Project
 *
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.api.nhso.ApiUrlHelper;
import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.app.FFCGridActivity;
import th.in.ffc.app.form.screening.ApiUrlListActivity;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.interfaces.ApiTestCallback;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.security.CryptographerService;
import th.in.ffc.security.PdpaActivity;
import th.in.ffc.service.EncryptDbService;
import th.in.ffc.util.AssetReader;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.GenerateSeq;
import th.in.ffc.util.TokenValidator;
import th.in.ffc.widget.IntentBaseAdapter;
import th.in.ffc.BuildConfig;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * add description here! please
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class MainActivity extends FFCGridActivity {


    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle(R.string.app_version);



        boolean quit = getIntent().getBooleanExtra("quit", false);
        if (quit) {
            this.finish();
        }

        Intent intent = new Intent(Action.MAIN);
        intent.addCategory(Category.TAB);

        IntentBaseAdapter adapter = new IntentBaseAdapter(this, intent,
                R.layout.grid_item, R.id.image, R.id.text);

        super.setGridAdapter(adapter);
        GridView grid = super.getGridView();

        grid.setOnItemClickListener(adapter.getOnItemClickListener());

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean("receiver")) {
                Log.d(TAG, "regis");
                registerReceiver(mEncrypterReceiver, mEncryptFilter);
                mRegis = true;

                p = new ProgressDialog(MainActivity.this);
                p.setMessage(getString(R.string.please_wait));
                p.setCancelable(false);
                p.show();
            }
        } else {
            super.doCheckDateSetting();
        }
        showDialogPDPA();
        ApiUrlHelper.initializeAllApiUrls(this);

        // ตรวจสอบการตั้งค่าปัจจุบัน
        ApiUrlHelper.logCurrentConfiguration(this);


        // ตรวจสอบว่า APIs พร้อมใช้งานหรือไม่
        boolean allReady = ApiUrlHelper.areAllApisReady(this);
        Log.i(TAG, "All APIs ready: " + allReady);

        Log.i(TAG, "Application initialization completed");
//        startEncryptDbService();
        if(!isServiceRunning(EncryptDbService.class)) {
            Log.d(TAG, "EncryptDbService is not running, starting it now.");
            Intent serviceIntent = new Intent(this, EncryptDbService.class);

            // ใช้ startForegroundService สำหรับ Android 8.0+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }
        } else {
            Log.d(TAG, "EncryptDbService is already running.");
        }

    }
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

// ใช้งาน

    private void startEncryptDbService() {
        Intent serviceIntent = new Intent(this, EncryptDbService.class);
        startService(serviceIntent);
    }
    private void showDialogPDPA(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        SharedPreferences prefer = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);

        if(!prefer.getString("PDPA","").equals(formattedDate)) {

            SharedPreferences.Editor editor = prefer.edit();
            editor.putString("PDPA", formattedDate);
            editor.commit();
            SharedPreferences prefer2 = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);

            dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setTitle("ข้อตกลงการใช้บริการ");
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.activity_pdpa);
            dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            final CheckBox chkAllow = dialog.findViewById(R.id.chkAllow);
            final Button btnOK = dialog.findViewById(R.id.btnOK);
            final Button btnCancel = dialog.findViewById(R.id.btnCancel);
            final TextView tvpdpa = dialog.findViewById(R.id.tvPdpa);
            tvpdpa.setMovementMethod(new ScrollingMovementMethod());

            btnOK.setEnabled(false);
            chkAllow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    btnOK.setEnabled(b);
                }
            });
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                    System.exit(0);
                }
            });
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        finishAffinity();
                        System.exit(0);
                    }
                    return true;
                }
            });
            dialog.show();
        }
    }
    private boolean mRegis = false;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("receiver", mRegis);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // เพิ่ม icon setting
        MenuItem settings = menu.add(Menu.NONE, R.layout.menu_settings,
                Menu.NONE, "Settings");
        settings.setIcon(R.drawable.ic_action_setting);
        settings.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        MenuItem whatnew = menu.add(Menu.NONE, R.layout.whatnew_dialog,
                Menu.NONE, "what new");
        whatnew.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);



        SharedPreferences sp = getSharedPreferences(FamilyFolderCollector.TAG, MODE_PRIVATE);
        int prev = sp.getInt(FamilyFolderCollector.PREF_VERSION, 0);
        int now = getResources().getInteger(R.integer.version_code);
        whatnew.setIcon((now > prev) ? R.drawable.ic_action_star : R.drawable.ic_action_start_dark);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.layout.whatnew_dialog:
                String tag = "whatnew";
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment prev = fm.findFragmentByTag(tag);
                WhatnewDialogFragment f;
                if (prev != null) {
                    f = (WhatnewDialogFragment) prev;
                    ft.remove(f);
                } else {
                    f = (WhatnewDialogFragment) Fragment.instantiate(this,
                            WhatnewDialogFragment.class.getName(), null);
                }
                ft.addToBackStack(null);
                f.show(fm, tag);

                SharedPreferences.Editor se = getSharedPreferences(FamilyFolderCollector.TAG, MODE_PRIVATE).edit();
                se.putInt(FamilyFolderCollector.PREF_VERSION, getResources().getInteger(R.integer.version_code));
                se.commit();
                item.setIcon(R.drawable.ic_action_start_dark);

                return true;
            case R.layout.menu_settings:
//                showTokenValidationDialog();
                showSettingsMenu(findViewById(R.layout.menu_settings));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showSettingsMenu(View anchorView) {
        // สร้าง PopupMenu โดยให้แสดงที่ View ที่เป็น Anchor (ในที่นี้คือ ปุ่ม Settings)
        PopupMenu popup = new PopupMenu(this, anchorView);
        MenuInflater inflater = popup.getMenuInflater();

        // สร้างเมนูอย่างง่าย
        Menu menu = popup.getMenu();
        menu.add(Menu.NONE, 1, Menu.NONE, "ทดสอบ Token");
        menu.add(Menu.NONE, 2, Menu.NONE, "จัดการ API");

        // ตั้งค่า Event Listener เมื่อกดเลือกเมนู
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 1: // ทดสอบ Token
                        showTokenValidationDialog();
                        return true;
                    case 2: // จัดการ API
//                        showApiManagementDialog();
                        showApiManagementScreen();
                        return true;
                    default:
                        return false;
                }
            }
        });

        // แสดง PopupMenu
        popup.show();
    }
    private void showApiManagementScreen() {
        // สมมติว่าหน้าจอจัดการ API เดิมถูกเรียกผ่าน Intent นี้
        // หากคุณใช้วิธีอื่น ให้ปรับโค้ดส่วนนี้ตามความเหมาะสม
        Intent intent = new Intent(this, ApiUrlListActivity.class);
        startActivity(intent);

    }
    private void showApiManagementDialog() {
        // สร้าง dialog แบบเต็มหน้าจอ
        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        dialog.setTitle("จัดการ API");
        dialog.setContentView(R.layout.dialog_api_management); // คุณต้องสร้างไฟล์เลย์เอาท์นี้เพิ่ม
        dialog.setCancelable(true);

        // ตั้งค่าให้ dialog มีขนาดใหญ่ขึ้น
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        // ค้นหา View ต่างๆ ในไฟล์เลย์เอาท์และตั้งค่าการทำงาน
        // คุณต้องเพิ่มการทำงานที่ต้องการในส่วนนี้

        // แสดง dialog
        dialog.show();
    }
    private void showTokenValidationDialog() {
        // สร้าง dialog แบบเต็มหน้าจอ
        final Dialog dialog = new Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth);
        dialog.setTitle("Token Validation");
        dialog.setContentView(R.layout.dialog_token_validation);
        dialog.setCancelable(true);

        // ตั้งค่าให้ dialog มีขนาดใหญ่ขึ้น
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        // ===== TOKEN AUTH SECTION =====
        // อ้างอิงถึงวิดเจ็ตต่างๆ สำหรับ Token Auth
        final TextInputEditText editTokenAuth = dialog.findViewById(R.id.edit_token_auth);
        final TextInputEditText editCitizenIdAuth = dialog.findViewById(R.id.edit_citizen_id_auth);
        final Button btnValidateAuth = dialog.findViewById(R.id.btn_validate_auth);
        final Button btnTestApiAuth = dialog.findViewById(R.id.btn_test_api_auth);

        // Collapsible header สำหรับ Token Auth
        final RelativeLayout tokenAuthHeader = dialog.findViewById(R.id.token_auth_header);
        final LinearLayout tokenAuthContent = dialog.findViewById(R.id.token_auth_content);
        final ImageView tokenAuthExpandIcon = dialog.findViewById(R.id.token_auth_expand_icon);

        // ===== TOKEN CLAIM SECTION =====
        // อ้างอิงถึงวิดเจ็ตต่างๆ สำหรับ Token Claim
        final TextInputEditText editTokenClaim = dialog.findViewById(R.id.edit_token_claim);
        final EditText editJsonClaim = dialog.findViewById(R.id.edit_json_claim); // เปลี่ยนเป็น EditText แทน TextInputEditText
        final Button btnValidateClaim = dialog.findViewById(R.id.btn_validate_claim);
        final Button btnTestApiClaim = dialog.findViewById(R.id.btn_test_api_claim);
        final Button btnLoadDefaultJson = dialog.findViewById(R.id.btn_load_default_json);

        // Collapsible header สำหรับ Token Claim
        final RelativeLayout tokenClaimHeader = dialog.findViewById(R.id.token_claim_header);
        final LinearLayout tokenClaimContent = dialog.findViewById(R.id.token_claim_content);
        final ImageView tokenClaimExpandIcon = dialog.findViewById(R.id.token_claim_expand_icon);

        // ส่วนแสดงผลลัพธ์
        final TextView tvResult = dialog.findViewById(R.id.tv_validation_result);

        // ดึง token ล่าสุดจากฐานข้อมูลมาแสดง (ถ้ามี)
        String[] latestToken = getLatestTokenFromDatabase();
        if (latestToken != null) {
            editTokenAuth.setText(latestToken[0]);  // token_auth
            editTokenClaim.setText(latestToken[1]); // token_claim
        }

        // ตั้งค่า Collapsible สำหรับส่วน Token Auth
        tokenAuthHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenAuthContent.getVisibility() == View.VISIBLE) {
                    tokenAuthContent.setVisibility(View.GONE);
                    tokenAuthExpandIcon.setImageResource(R.drawable.ic_action_expand);
                } else {
                    tokenAuthContent.setVisibility(View.VISIBLE);
                    tokenAuthExpandIcon.setImageResource(R.drawable.ic_action_collapse);
                }
            }
        });

        // ตั้งค่า Collapsible สำหรับส่วน Token Claim
        tokenClaimHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tokenClaimContent.getVisibility() == View.VISIBLE) {
                    tokenClaimContent.setVisibility(View.GONE);
                    tokenClaimExpandIcon.setImageResource(R.drawable.ic_action_expand);
                } else {
                    tokenClaimContent.setVisibility(View.VISIBLE);
                    tokenClaimExpandIcon.setImageResource(R.drawable.ic_action_collapse);

                    // เปิดส่วน Token Claim แล้วกดปุ่มโหลด JSON ตัวอย่างโดยอัตโนมัติถ้ายังไม่มีข้อมูล
                    if (editJsonClaim.getText().toString().trim().isEmpty()) {
                        btnLoadDefaultJson.performClick();
                    }
                }
            }
        });
        String pcuCode=getPcuCode();
        String seq = GenerateSeq.generateSeq(pcuCode);
        // ข้อมูล JSON ตัวอย่าง
        final String defaultJsonData = "{\n" +
                "    \"fsDatas\": [\n" +
                "        {\n" +
                "            \"opd\": {\n" +
                "                \"SEQ\": \""+seq+"\",\n" +
                "                \"INSCL\": \"UCS\",\n" +
                "                \"DATEOPD\": \"2021-12-31T13:15:30\",\n" +
                "                \"PERMITNO\": \"PP1040589918\",\n" +
                "                \"HTYPE\": \"1\",\n" +
                "                \"UUC\": \"1\",\n" +
                "                \"CHIEFCOMP\": \"CHIEFCOMP_TEST\",\n" +
                "                \"BTEMP\": 37.6,\n" +
                "                \"SBP\": 130,\n" +
                "                \"DBP\": 89,\n" +
                "                \"PR\": 98,\n" +
                "                \"RR\": 20,\n" +
                "                \"WAISTLINE\": 34,\n" +
                "                \"WEIGHT\": 50,\n" +
                "                \"HEIGHT\": 150,\n" +
                "                \"HEADCIRCUM\": 35,\n" +
                "                \"CLINIC\": \"01\"\n" +
                "            },\n" +
                "            \"patient\": {\n" +
                "                \"SEQ\": \""+seq+"\",\n" +
                "                \"TYPE\": \"CID\",\n" +
                "                \"CID\": \"1730201588821\",\n" +
                "                \"PPN\": \"1730201588821\",\n" +
                "                \"PWD\": \"AA123456\",\n" +
                "                \"NAME.GIVEN\": \"อำนาจ\",\n" +
                "                \"NAME.FAMILY\": \"รอบรู้\",\n" +
                "                \"BIRTHDATE\": \"2021-12-31\",\n" +
                "                \"GENDER\": \"1\",\n" +
                "                \"ADDRESS.LINE\": \"99/999 หมู่ที่ 2 หมู่บ้านอบอุ่น ซอยอบอ้าว\",\n" +
                "                \"ADDRESS.CITY\": \"110101\",\n" +
                "                \"ADDRESS.DISTRICT\": \"1101\",\n" +
                "                \"ADDRESS.STATE\": \"11\",\n" +
                "                \"ADDRESS.POSTALCODE\": \"10310\",\n" +
                "                \"NATIONALITY\": \"099\",\n" +
                "                \"RACE\": \"ไทย\",\n" +
                "                \"HN\": \"Xxx000\",\n" +
                "                \"AN\": \"Xxx000\"\n" +
                "            },\n" +
                "            \"provider\": {\n" +
                "                \"SEQ\": \""+seq+"\",\n" +
                "                \"HCODE\": \"11415\",\n" +
                "                \"HCODE_NAME\": \"โรงพยาบาลเขาชัยสน\",\n" +
                "                \"HCODE_SEND\": \"11415\",\n" +
                "                \"HCODE_SEND_NAME\": \"โรงพยาบาลเขาชัยสน\",\n" +
                "                \"HMAIN\": \"10745\",\n" +
                "                \"HMAIN_NAME\": \"โรงพยาบาลสงขลา\"\n" +
                "            }\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        // เมื่อกดปุ่มโหลดข้อมูล JSON ตัวอย่าง
        btnLoadDefaultJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editJsonClaim.setText(defaultJsonData);
            }
        });

        // เมื่อกดปุ่มตรวจสอบ Token Auth
        btnValidateAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenAuth = editTokenAuth.getText().toString().trim();

                if (tokenAuth.isEmpty()) {
                    tvResult.setText("กรุณาระบุ Token Auth");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // ตรวจสอบรูปแบบของ token_auth
                if (TokenValidator.isValidUuidFormat(tokenAuth)) {
                    tvResult.setText("Token Auth ถูกต้องตามรูปแบบ UUID");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    // บันทึก token auth ลงฐานข้อมูล
                    String tokenClaim = editTokenClaim.getText().toString().trim();
                    if (!tokenClaim.isEmpty()) {
                        saveTokenToDatabase(tokenAuth, tokenClaim);
                    }
                } else {
                    tvResult.setText("รูปแบบ Token Auth ไม่ถูกต้อง ต้องเป็นรูปแบบ UUID เช่น 34913796-e515-4b33-9656-6a2eb64ef569");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });

        // เมื่อกดปุ่มตรวจสอบ Token Claim
        btnValidateClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenClaim = editTokenClaim.getText().toString().trim();

                if (tokenClaim.isEmpty()) {
                    tvResult.setText("กรุณาระบุ Token Claim");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // ตรวจสอบรูปแบบของ token_claim
                if (TokenValidator.isValidUuidFormat(tokenClaim)) {
                    tvResult.setText("Token Claim ถูกต้องตามรูปแบบ UUID");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    // บันทึก token claim ลงฐานข้อมูล
                    String tokenAuth = editTokenAuth.getText().toString().trim();
                    if (!tokenAuth.isEmpty()) {
                        saveTokenToDatabase(tokenAuth, tokenClaim);
                    }
                } else {
                    tvResult.setText("รูปแบบ Token Claim ไม่ถูกต้อง ต้องเป็นรูปแบบ UUID เช่น 34913796-e515-4b33-9656-6a2eb64ef569");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });

        // เมื่อกดปุ่มทดสอบ API ด้วย Token Auth
        btnTestApiAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenAuth = editTokenAuth.getText().toString().trim();
                String citizenId = editCitizenIdAuth.getText().toString().trim();

                if (tokenAuth.isEmpty()) {
                    tvResult.setText("กรุณาระบุ Token Auth");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                if (citizenId.isEmpty() || citizenId.length() != 13) {
                    tvResult.setText("กรุณาระบุเลขบัตรประชาชน 13 หลัก");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // แสดงสถานะกำลังทดสอบ
                tvResult.setText("กำลังทดสอบการเชื่อมต่อกับ API ด้วย Token Auth...");
                tvResult.setTextColor(getResources().getColor(android.R.color.black));

                // แสดง ProgressDialog ระหว่างรอผลลัพธ์
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("กำลังเชื่อมต่อกับ API...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // ใช้ ApiManager เพื่อเรียกใช้ API
                th.in.ffc.api.nhso.ApiManager.callGetApi(
                        MainActivity.this,
                        "REAL_PERSON",
                        tokenAuth,
                        citizenId,
                        new th.in.ffc.api.nhso.ApiManager.ApiCallback() {
                            @Override
                            public void onResult(boolean success, String message) {
                                // ปิด Progress Dialog
                                progressDialog.dismiss();

                                // อัปเดต TextView ผลลัพธ์
                                if (success) {
                                    tvResult.setText("ทดสอบ API ด้วย Token Auth สำเร็จ");
                                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                } else {
                                    tvResult.setText("ทดสอบ API ด้วย Token Auth ล้มเหลว");
                                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                }

                                // แสดงผลลัพธ์แบบ Dialog
                                String title = success ? "API ทำงานสำเร็จ" : "API ทำงานล้มเหลว";
                                showApiResultDialog(title, message, success);
                            }
                        }
                );
            }
        });
        // เมื่อกดปุ่มทดสอบ API ด้วย Token Claim
        btnTestApiClaim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenClaim = editTokenClaim.getText().toString().trim();
                String jsonData = editJsonClaim.getText().toString().trim();

                if (tokenClaim.isEmpty()) {
                    tvResult.setText("กรุณาระบุ Token Claim");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                if (jsonData.isEmpty()) {
                    tvResult.setText("กรุณาระบุข้อมูล JSON");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // ตรวจสอบรูปแบบ JSON
                try {
                    new org.json.JSONObject(jsonData);
                } catch (Exception e) {
                    tvResult.setText("รูปแบบ JSON ไม่ถูกต้อง: " + e.getMessage());
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // แสดงสถานะกำลังทดสอบ
                tvResult.setText("กำลังทดสอบการเชื่อมต่อกับ API ด้วย Token Claim...");
                tvResult.setTextColor(getResources().getColor(android.R.color.black));

                // แสดง ProgressDialog ระหว่างรอผลลัพธ์
                final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("กำลังเชื่อมต่อกับ API...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                // ใช้ ApiManager เพื่อเรียกใช้ API
                th.in.ffc.api.nhso.ApiManager.callPostApi(
                        MainActivity.this,
                        "CREATE_FS_DATA", // API code สำหรับ FS Data
                        tokenClaim,
                        jsonData,
                        new th.in.ffc.api.nhso.ApiManager.ApiCallback() {
                            @Override
                            public void onResult(boolean success, String message) {
                                // ปิด Progress Dialog
                                progressDialog.dismiss();

                                // อัปเดต TextView ผลลัพธ์
                                if (success) {
                                    tvResult.setText("ทดสอบ API ด้วย Token Claim สำเร็จ");
                                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                } else {
                                    tvResult.setText("ทดสอบ API ด้วย Token Claim ล้มเหลว");
                                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                }

                                // แสดงผลลัพธ์แบบ Dialog
                                String title = success ? "API ทำงานสำเร็จ" : "API ทำงานล้มเหลว";
                                showApiResultDialog(title, message, success);
                            }
                        }
                );
            }
        });
        // แสดง dialog แบบเต็มหน้าจอ
        dialog.show();

        // เปิดส่วน Token Auth โดยค่าเริ่มต้น
        tokenAuthHeader.performClick();
    }
    private void testTokenWithApi(final String token, final String citizenId, final String apiUrl,
                                  final ApiTestCallback callback) {
        final Handler handler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    // สร้าง URL
                    URL url;
                    if (apiUrl.contains("?")) {
                        // ถ้ามี query parameters อยู่แล้ว
                        url = new URL(apiUrl + "&SOURCE_ID=" + BuildConfig.API_SOURCE_ID + "&PID=" + citizenId);
                    } else {
                        // ถ้ายังไม่มี query parameters
                        url = new URL(apiUrl + "?SOURCE_ID=" + BuildConfig.API_SOURCE_ID + "&PID=" + citizenId);
                    }

                    // เปิดการเชื่อมต่อ
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    // กำหนด header สำหรับ Bearer Token
                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);

                    // กำหนด timeout
                    urlConnection.setConnectTimeout(BuildConfig.API_TIMEOUT);
                    urlConnection.setReadTimeout(BuildConfig.API_TIMEOUT);

                    // เชื่อมต่อ
                    urlConnection.connect();

                    // อ่านผลลัพธ์
                    final int responseCode = urlConnection.getResponseCode();

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // อ่านข้อมูลจาก response
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                            response.append("\n");
                        }
                        reader.close();

                        final String responseData = response.toString();

                        // ส่งผลลัพธ์กลับไปที่ UI thread
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(true, responseData);
                            }
                        });
                    } else {
                        // กรณีเกิดข้อผิดพลาด
                        BufferedReader reader;
                        if (urlConnection.getErrorStream() != null) {
                            reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        } else {
                            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        }

                        StringBuilder errorResponse = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            errorResponse.append(line);
                            errorResponse.append("\n");
                        }
                        reader.close();

                        final String errorMessage = "รหัสข้อผิดพลาด: " + responseCode + "\n" + errorResponse.toString();

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onResult(false, errorMessage);
                            }
                        });
                    }
                } catch (final Exception e) {
                    e.printStackTrace();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.onResult(false, "เกิดข้อผิดพลาด: " + e.getMessage());
                        }
                    });
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            }
        }).start();
    }

//    private void testTokenWithApi(final String token, final String citizenId, final ApiTestCallback callback) {
//        final Handler handler = new Handler(Looper.getMainLooper());
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection urlConnection = null;
//                try {
//                    // สร้าง URL
//                    URL url = new URL(BuildConfig.API_BASE_URL + BuildConfig.API_ENDPOINT_REAL_PERSON +
//                            "?SOURCE_ID=" + BuildConfig.API_SOURCE_ID + "&PID=" + citizenId);
//
////                    URL url = new URL("https://test.nhso.go.th/nhsoendpoint/api/RealPerson?SOURCE_ID=BKKCC&PID=" + citizenId);
//
//                    // เปิดการเชื่อมต่อ
//                    urlConnection = (HttpURLConnection) url.openConnection();
//                    urlConnection.setRequestMethod("GET");
//
//                    // กำหนด header สำหรับ Bearer Token
//                    urlConnection.setRequestProperty("Authorization", "Bearer " + token);
//
//                    // กำหนด timeout
//                    urlConnection.setConnectTimeout(BuildConfig.API_TIMEOUT);
//                    urlConnection.setReadTimeout(BuildConfig.API_TIMEOUT);
//
//                    // เชื่อมต่อ
//                    urlConnection.connect();
//
//                    // อ่านผลลัพธ์
//                    final int responseCode = urlConnection.getResponseCode();
//
//                    if (responseCode == HttpURLConnection.HTTP_OK) {
//                        // อ่านข้อมูลจาก response
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                        StringBuilder response = new StringBuilder();
//                        String line;
//
//                        while ((line = reader.readLine()) != null) {
//                            response.append(line);
//                        }
//                        reader.close();
//
//                        final String responseData = response.toString();
//
//                        // ส่งผลลัพธ์กลับไปที่ UI thread
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                callback.onResult(true, responseData);
//                            }
//                        });
//                    } else {
//                        // กรณีเกิดข้อผิดพลาด
//                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
//                        StringBuilder errorResponse = new StringBuilder();
//                        String line;
//
//                        while ((line = reader.readLine()) != null) {
//                            errorResponse.append(line);
//                        }
//                        reader.close();
//
//                        final String errorMessage = "รหัสข้อผิดพลาด: " + responseCode + "\n" + errorResponse.toString();
//
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                callback.onResult(false, errorMessage);
//                            }
//                        });
//                    }
//                } catch (final Exception e) {
//                    e.printStackTrace();
//
//                    handler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            callback.onResult(false, "เกิดข้อผิดพลาด: " + e.getMessage());
//                        }
//                    });
//                } finally {
//                    if (urlConnection != null) {
//                        urlConnection.disconnect();
//                    }
//                }
//            }
//        }).start();
//    }

    private void saveTokenToDatabase(String tokenAuth, String tokenClaim) {
        try {
            SfTokenDao tokenDao = new SfTokenDao(this);
            List<SfToken> tokens = tokenDao.getAllTokens();

            // ค้นหา token ที่มี token_auth ตรงกับที่ต้องการบันทึก
            SfToken existingToken = null;
            for (SfToken token : tokens) {
                if (token.getTokenAuth().equals(tokenAuth)) {
                    existingToken = token;
                    break;
                }
            }

            if (existingToken == null) {
                // ไม่พบ token นี้ในฐานข้อมูล ให้เพิ่มใหม่
                SfToken newToken = new SfToken();
                newToken.setTokenAuth(tokenAuth);
                newToken.setTokenClaim(tokenClaim);
                newToken.setCreatedDate(System.currentTimeMillis());
                newToken.setUpdatedDate(System.currentTimeMillis());
                tokenDao.deleteAllTokens();
                tokenDao.insert(newToken);
                Toast.makeText(this, "บันทึก Token สำเร็จ", Toast.LENGTH_SHORT).show();
            } else {
                // พบ token นี้แล้ว ให้อัพเดท token_claim
                existingToken.setTokenClaim(tokenClaim);
                existingToken.setUpdatedDate(System.currentTimeMillis());

                tokenDao.update(existingToken);
                Toast.makeText(this, "อัพเดท Token สำเร็จ", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "ไม่สามารถบันทึก Token ได้", Toast.LENGTH_SHORT).show();
        }
    }
    private String[] getLatestTokenFromDatabase() {
        String[] tokenData = null;
        try {
            SfTokenDao tokenDao = new SfTokenDao(this);
            List<SfToken> tokens = tokenDao.getAllTokens();

            if (!tokens.isEmpty()) {
                // เรียงลำดับตามวันที่สร้างล่าสุด (ถ้า getAllTokens ไม่ได้เรียงมาแล้ว)
                Collections.sort(tokens, new Comparator<SfToken>() {
                    @Override
                    public int compare(SfToken t1, SfToken t2) {
                        return Long.compare(t2.getCreatedDate(), t1.getCreatedDate());
                    }
                });

                SfToken latestToken = tokens.get(0); // เอา token ล่าสุด
                tokenData = new String[2];
                tokenData[0] = latestToken.getTokenAuth();
                tokenData[1] = latestToken.getTokenClaim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tokenData;
    }
    private EncrypterServiceRevicer mEncrypterReceiver = new EncrypterServiceRevicer();
    private IntentFilter mEncryptFilter = new IntentFilter(Action.ENCRYPT);
    ProgressDialog p;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRegis) {
            p.dismiss();
            p = null;
            unregisterReceiver(mEncrypterReceiver);
        }
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        builder.setTitle("exit?");
        builder.setPositiveButton(R.string.yes,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        p = new ProgressDialog(MainActivity.this);
                        p.setMessage(getString(R.string.please_wait));
                        p.setCancelable(false);
                        p.show();

                        registerReceiver(mEncrypterReceiver, mEncryptFilter);
                        mRegis = true;

                        SharedPreferences prefer = getSharedPreferences("MainActivity", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefer.edit();
                        editor.putString("PDPA", "");
                        editor.commit();

                        Intent service = new Intent(MainActivity.this, CryptographerService.class);
                        service.setAction(Action.ENCRYPT);
                        startService(service);
                    }
                });

        builder.setNegativeButton(R.string.no,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private class EncrypterServiceRevicer extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (p != null)
                p.dismiss();

            MainActivity.super.onBackPressed();

            Process.killProcess(Process.myPid());
        }
    }

    public static class WhatnewDialogFragment extends DialogFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setStyle(DialogFragment.STYLE_NORMAL,
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
        }

        TextView text;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            getDialog().setTitle("What's New!");
            View view = inflater.inflate(R.layout.whatnew_dialog, container,
                    false);
            text = (TextView) view.findViewById(R.id.content);

            return view;
        }

        @Override
        public void onActivityCreated(Bundle arg0) {
            super.onActivityCreated(arg0);

            String txt = AssetReader.read(getActivity(), "whatnew.txt");
            text.setMovementMethod(new ScrollingMovementMethod());
            text.setText(txt);
        }

    }
    private void showApiResultDialog(String title, String message, boolean isSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // กำหนด Title และ Icon ตามผลลัพธ์
        builder.setTitle(title);
        if (isSuccess) {
//            builder.setIcon(R.drawable.ic_action_success); // ถ้าไม่มี icon นี้ให้ใช้ icon อื่นที่เหมาะสม
        } else {
//            builder.setIcon(R.drawable.ic_action_error); // ถ้าไม่มี icon นี้ให้ใช้ icon อื่นที่เหมาะสม
        }

        // สร้าง ScrollView และ TextView เพื่อรองรับข้อความยาว
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);

        // ตั้งค่า TextView
        textView.setText(message);
        textView.setPadding(30, 30, 30, 30);
        textView.setTextIsSelectable(true); // สามารถเลือกข้อความได้

        // เพิ่ม TextView ใน ScrollView
        scrollView.addView(textView);

        // กำหนด ScrollView เป็น View ของ Dialog
        builder.setView(scrollView);

        // เพิ่มปุ่ม OK
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // เพิ่มปุ่มคัดลอกผลลัพธ์
        builder.setNeutralButton("คัดลอก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // คัดลอกข้อความไปยัง clipboard
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("API Result", message);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "คัดลอกข้อความแล้ว", Toast.LENGTH_SHORT).show();
            }
        });

        // แสดง Dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
