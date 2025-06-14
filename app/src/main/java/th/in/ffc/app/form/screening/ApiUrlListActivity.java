package th.in.ffc.app.form.screening;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import th.in.ffc.R;
import th.in.ffc.api.nhso.ApiUrlHelper;
import th.in.ffc.app.form.screening.dao.SfApiUrlDao;
import th.in.ffc.app.form.screening.model.SfApiUrl;
import th.in.ffc.provider.ApiUrlProvider;

/**
 * กิจกรรมสำหรับแสดงรายการและจัดการ API URL
 */
public class ApiUrlListActivity extends AppCompatActivity {

    private static final int REQUEST_ADD_API_URL = 1;
    private static final int REQUEST_EDIT_API_URL = 2;

    private ListView listView;
    private SimpleCursorAdapter adapter;
    private SfApiUrlDao apiUrlDao;
    private Switch switchEnvironment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_url_list);

        // ตั้งค่า Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("จัดการ API URL");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ตั้งค่า DAO
        apiUrlDao = new SfApiUrlDao(this);

        // ตรวจสอบและเพิ่ม APIs ที่ขาดหายไป (เผื่อมีการอัปเดตแอป)
        ensureAllApisExist();

        // ตั้งค่า ListView
        listView = findViewById(R.id.list_api_urls);
        setupListView();

        // ตั้งค่า FAB สำหรับเพิ่ม API URL ใหม่
        FloatingActionButton fab = findViewById(R.id.fab_add_api_url);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApiUrlListActivity.this, ApiUrlEditActivity.class);
                startActivityForResult(intent, REQUEST_ADD_API_URL);
            }
        });

        // ตั้งค่า Switch สำหรับเลือกสภาพแวดล้อม
        switchEnvironment = findViewById(R.id.switch_environment);
        setupEnvironmentSwitch();
    }

    /**
     * ตรวจสอบและเพิ่ม APIs ที่ขาดหายไป
     */
    private void ensureAllApisExist() {
        try {
            // ใช้ ApiUrlHelper ตรวจสอบ
            if (!ApiUrlHelper.areAllApisReady(this)) {
                // ถ้ามี API ขาดหายไป ให้เพิ่มเข้าไป
                apiUrlDao.syncApis();
                Toast.makeText(this, "อัปเดต API URLs เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "เกิดข้อผิดพลาดในการตรวจสอบ APIs", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ตั้งค่า ListView สำหรับแสดงรายการ API URL
     */
    private void setupListView() {
        // ดึงข้อมูลด้วย Content Provider
        String[] projection = {
                "id AS _id",  // จำเป็นสำหรับ SimpleCursorAdapter
                "id",
                "api_code",
                "api_name",
                "method",
                "test_url",
                "prod_url",
                "env_type"
        };

        Cursor cursor = getContentResolver().query(
                ApiUrlProvider.CONTENT_URI,
                projection,
                null,
                null,
                "api_name ASC"
        );

        // กำหนดคอลัมน์ที่จะแสดงใน ListView
        String[] fromColumns = {
                "api_name",
                "method",
                "test_url",
                "prod_url",
                "env_type"
        };

        // กำหนด View ที่จะใช้แสดงข้อมูล
        int[] toViews = {
                R.id.text_api_name,
                R.id.text_method,
                R.id.text_test_url,
                R.id.text_prod_url,
                R.id.text_env_type
        };

        // สร้าง adapter
        adapter = new SimpleCursorAdapter(
                this,
                R.layout.item_api_url,
                cursor,
                fromColumns,
                toViews,
                0
        );

        // กำหนด ViewBinder เพื่อแปลงค่า env_type เป็นข้อความ
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.text_env_type) {
                    int envType = cursor.getInt(columnIndex);
                    String envText = envType == SfApiUrl.ENV_PRODUCTION ? "Production" : "Test";
                    ((android.widget.TextView) view).setText(envText);

                    // เปลี่ยนสีตามสภาพแวดล้อม
                    if (envType == SfApiUrl.ENV_PRODUCTION) {
                        ((android.widget.TextView) view).setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    } else {
                        ((android.widget.TextView) view).setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
                    }
                    return true;
                }
                return false;
            }
        });

        // ตั้งค่า adapter ให้กับ ListView
        listView.setAdapter(adapter);

        // ตั้งค่า OnItemClickListener สำหรับแก้ไข API URL
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ApiUrlListActivity.this, ApiUrlEditActivity.class);
                intent.putExtra("api_url_id", id);
                startActivityForResult(intent, REQUEST_EDIT_API_URL);
            }
        });

        // ตั้งค่า OnItemLongClickListener สำหรับลบ API URL
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteConfirmationDialog(id);
                return true;
            }
        });
    }

    /**
     * ตั้งค่า Switch สำหรับเลือกสภาพแวดล้อม
     */
    private void setupEnvironmentSwitch() {
        // ตรวจสอบสภาพแวดล้อมปัจจุบัน
        boolean isProduction = false;
        Cursor cursor = getContentResolver().query(
                ApiUrlProvider.CONTENT_URI,
                new String[] { "env_type" },
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int envType = cursor.getInt(0);
            isProduction = envType == SfApiUrl.ENV_PRODUCTION;
            cursor.close();
        }

        // ตั้งค่าสถานะเริ่มต้นของ Switch
        switchEnvironment.setChecked(isProduction);
        switchEnvironment.setText(isProduction ? "Production" : "Test");

        // ตั้งค่า OnCheckedChangeListener
        switchEnvironment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchEnvironment.setText(isChecked ? "Production" : "Test");

                // ใช้ ApiUrlHelper เพื่อเปลี่ยน environment
                ApiUrlHelper.switchAllEnvironments(ApiUrlListActivity.this, isChecked);

                // รีเฟรชข้อมูลใน ListView
                refreshListView();

                // แสดงข้อความแจ้งเตือน
                String message = "เปลี่ยนสภาพแวดล้อมเป็น " + (isChecked ? "Production" : "Test") + " แล้ว";
                Toast.makeText(ApiUrlListActivity.this, message, Toast.LENGTH_SHORT).show();

                // แสดงการตั้งค่าปัจจุบันใน Log
                ApiUrlHelper.logCurrentConfiguration(ApiUrlListActivity.this);
            }
        });
    }

    /**
     * แสดง Dialog ยืนยันการลบ API URL
     */
    private void showDeleteConfirmationDialog(final long id) {
        // ตรวจสอบว่าเป็น API ที่สำคัญหรือไม่
        SfApiUrl apiUrl = apiUrlDao.findById(id);
        if (apiUrl != null && isSystemApi(apiUrl.getApiCode())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("ไม่สามารถลบได้");
            builder.setMessage("API นี้เป็น API หลักของระบบ ไม่สามารถลบได้");
            builder.setPositiveButton("ตกลง", null);
            builder.show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ยืนยันการลบ");
        builder.setMessage("คุณต้องการลบ API URL นี้หรือไม่?");
        builder.setPositiveButton("ลบ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteApiUrl(id);
            }
        });
        builder.setNegativeButton("ยกเลิก", null);
        builder.show();
    }

    /**
     * ตรวจสอบว่าเป็น System API หรือไม่
     */
    private boolean isSystemApi(String apiCode) {
        String[] systemApis = {
                "AUTHEN_CODE",
                "CREATE_FS_DATA",
                "REAL_PERSON",
                "STATUS_TRACKS",
                "STATUS_TRACKS_V2"
        };

        for (String systemApi : systemApis) {
            if (systemApi.equals(apiCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ลบ API URL จากฐานข้อมูล
     */
    private void deleteApiUrl(long id) {
        int rowsDeleted = getContentResolver().delete(
                ApiUrlProvider.CONTENT_URI,
                "id = ?",
                new String[] { String.valueOf(id) }
        );

        if (rowsDeleted > 0) {
            Toast.makeText(this, "ลบ API URL สำเร็จ", Toast.LENGTH_SHORT).show();
            refreshListView();
        } else {
            Toast.makeText(this, "ไม่สามารถลบ API URL ได้", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * รีเฟรชข้อมูลใน ListView
     */
    private void refreshListView() {
        if (adapter != null) {
            Cursor newCursor = getContentResolver().query(
                    ApiUrlProvider.CONTENT_URI,
                    new String[] {
                            "id AS _id",
                            "id",
                            "api_code",
                            "api_name",
                            "method",
                            "test_url",
                            "prod_url",
                            "env_type"
                    },
                    null,
                    null,
                    "api_name ASC"
            );

            adapter.swapCursor(newCursor);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == REQUEST_ADD_API_URL || requestCode == REQUEST_EDIT_API_URL)) {
            refreshListView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_api_url_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            refreshListView();
            return true;
        } else if (id == R.id.action_sync_apis) {
            // เพิ่ม menu item ใหม่สำหรับ sync APIs
            syncAllApis();
            return true;
        } else if (id == R.id.action_test_apis) {
            // เพิ่ม menu item ใหม่สำหรับทดสอบ APIs
            testCurrentApis();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * ซิงค์ APIs ทั้งหมด
     */
    private void syncAllApis() {
        try {
            apiUrlDao.syncApis();
            refreshListView();
            Toast.makeText(this, "ซิงค์ APIs เรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
            ApiUrlHelper.logCurrentConfiguration(this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "เกิดข้อผิดพลาดในการซิงค์ APIs", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ทดสอบ APIs ปัจจุบัน
     */
    private void testCurrentApis() {
        StringBuilder result = new StringBuilder();
        result.append("การตรวจสอบ APIs:\n\n");

        String[] testApis = {
                "AUTHEN_CODE",
                "CREATE_FS_DATA",
                "REAL_PERSON",
                "STATUS_TRACKS",
                "STATUS_TRACKS_V2"
        };

        for (String apiCode : testApis) {
            String url = ApiUrlHelper.getCurrentApiUrl(this, apiCode);
            boolean isProduction = ApiUrlHelper.isProductionEnvironment(this, apiCode);

            result.append(apiCode).append(":\n");
            result.append("  URL: ").append(url != null ? url : "ไม่พบ").append("\n");
            result.append("  ENV: ").append(isProduction ? "Production" : "Test").append("\n\n");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ผลการตรวจสอบ APIs");
        builder.setMessage(result.toString());
        builder.setPositiveButton("ตกลง", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (apiUrlDao != null) {
            apiUrlDao.close();
        }
    }
}