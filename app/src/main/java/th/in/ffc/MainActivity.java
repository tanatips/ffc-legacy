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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.api.nhso.NhsoApiCaller;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.app.FFCGridActivity;
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.interfaces.ApiTestCallback;
import th.in.ffc.app.form.screening.model.SfToken;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.security.CryptographerService;
import th.in.ffc.security.PdpaActivity;
import th.in.ffc.util.AssetReader;
import th.in.ffc.util.DateTime;
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
                showTokenValidationDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showTokenValidationDialog() {
        // สร้าง dialog
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Token Validation");
        dialog.setContentView(R.layout.dialog_token_validation);
        dialog.setCancelable(true);

        // อ้างอิงถึงวิดเจ็ตต่างๆ ใน dialog
        final TextInputEditText editTokenAuth = dialog.findViewById(R.id.edit_token_auth);
        final TextInputEditText editTokenClaim = dialog.findViewById(R.id.edit_token_claim);
        final TextInputEditText editCitizenId = dialog.findViewById(R.id.edit_citizen_id);
        final Button btnValidate = dialog.findViewById(R.id.btn_validate);
        final Button btnTestApi = dialog.findViewById(R.id.btn_test_api);
        final TextView tvResult = dialog.findViewById(R.id.tv_validation_result);

        // ดึง token ล่าสุดจากฐานข้อมูลมาแสดง (ถ้ามี)
        String[] latestToken = getLatestTokenFromDatabase();
        if (latestToken != null) {
            editTokenAuth.setText(latestToken[0]);  // token_auth
            editTokenClaim.setText(latestToken[1]); // token_claim
        }
//        editCitizenId.setText("1101401424853");
        // เมื่อกดปุ่ม validate
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenAuth = editTokenAuth.getText().toString().trim();
                String tokenClaim = editTokenClaim.getText().toString().trim();

                if (tokenAuth.isEmpty()) {
                    tvResult.setText("กรุณาระบุ Token Auth");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // ตรวจสอบรูปแบบของ token_auth
                if (!TokenValidator.isValidUuidFormat(tokenAuth)) {
                    tvResult.setText("รูปแบบ Token Auth ไม่ถูกต้อง");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                if (tokenClaim.isEmpty()) {
                    tvResult.setText("กรุณาระบุ Token Auth");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                if (!TokenValidator.isValidUuidFormat(tokenClaim)) {
                    tvResult.setText("รูปแบบ Token Claim ไม่ถูกต้อง");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    return;
                }

                // ตรวจสอบผ่าน API (ถ้ามี)
                tvResult.setText("กำลังตรวจสอบ...");
                tvResult.setTextColor(getResources().getColor(android.R.color.black));

                if (TokenValidator.isValidUuidFormat(tokenAuth) && TokenValidator.isValidUuid(tokenClaim)) {
                    // รูปแบบถูกต้อง
                    tvResult.setText("Token ถูกต้องตามรูปแบบ UUID");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    // บันทึก token ลงฐานข้อมูล (ถ้ายังไม่มี)
                    saveTokenToDatabase(tokenAuth, tokenClaim);

                } else {
                    // รูปแบบไม่ถูกต้อง
                    tvResult.setText("รูปแบบ Token ไม่ถูกต้อง ต้องเป็นรูปแบบ UUID เช่น 34913796-e515-4b33-9656-6a2eb64ef569");
                    tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
            }
        });
// เมื่อกดปุ่มทดสอบ API
        btnTestApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenAuth = editTokenAuth.getText().toString().trim();
                String citizenId = editCitizenId.getText().toString().trim();

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
                tvResult.setText("กำลังทดสอบการเชื่อมต่อกับ API...");
                tvResult.setTextColor(getResources().getColor(android.R.color.black));

                // สร้าง instance ของ NhsoApiCaller
                NhsoApiCaller apiCaller = new NhsoApiCaller(MainActivity.this);

                // เรียกใช้ testRealPersonApi
                apiCaller.testRealPersonApi(citizenId, tokenAuth, new NhsoApiCaller.RealPersonApiCallback() {
                    @Override
                    public void onSuccess(String response) {
                        tvResult.setText("ทดสอบ API สำเร็จ: \n" + response);
                        tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    }

                    @Override
                    public void onError(String errorMessage) {
                        tvResult.setText("ทดสอบ API ล้มเหลว: " + errorMessage);
                        tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    }
                });
            }
        });
        dialog.show();
    }
    private void testTokenWithApi(final String token, final String citizenId, final ApiTestCallback callback) {
        final Handler handler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection = null;
                try {
                    // สร้าง URL
                    URL url = new URL(BuildConfig.API_BASE_URL + BuildConfig.API_ENDPOINT_REAL_PERSON +
                            "?SOURCE_ID=" + BuildConfig.API_SOURCE_ID + "&PID=" + citizenId);

//                    URL url = new URL("https://test.nhso.go.th/nhsoendpoint/api/RealPerson?SOURCE_ID=BKKCC&PID=" + citizenId);

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
                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                        StringBuilder errorResponse = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            errorResponse.append(line);
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

}
