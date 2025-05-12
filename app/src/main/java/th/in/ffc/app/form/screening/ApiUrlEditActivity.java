package th.in.ffc.app.form.screening;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfApiUrlDao;
import th.in.ffc.app.form.screening.model.SfApiUrl;
import th.in.ffc.provider.ApiUrlProvider;

/**
 * กิจกรรมสำหรับเพิ่มหรือแก้ไข API URL
 */
public class ApiUrlEditActivity extends AppCompatActivity {

    private EditText editApiCode;
    private EditText editApiName;
    private EditText editDescription;
    private Spinner spinnerMethod;
    private EditText editTestUrl;
    private EditText editProdUrl;
    private EditText editParams;
    private EditText editRequestFormat;
    private Button btnSave;

    private SfApiUrlDao apiUrlDao;
    private long apiUrlId = -1;  // -1 หมายถึงเพิ่มใหม่
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_url_edit);

        // ตั้งค่า Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // ตั้งค่า DAO
        apiUrlDao = new SfApiUrlDao(this);

        // ตรวจสอบโหมดการทำงาน (เพิ่มหรือแก้ไข)
        if (getIntent().hasExtra("api_url_id")) {
            apiUrlId = getIntent().getLongExtra("api_url_id", -1);
            isEditMode = true;
            getSupportActionBar().setTitle("แก้ไข API URL");
        } else {
            getSupportActionBar().setTitle("เพิ่ม API URL");
        }

        // อ้างอิงถึง View ต่างๆ
        initViews();

        // ตั้งค่า Method Spinner
        setupMethodSpinner();

        // โหลดข้อมูลถ้าอยู่ในโหมดแก้ไข
        if (isEditMode) {
            loadApiUrlData();
        }

        // ตั้งค่าปุ่มบันทึก
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveApiUrl();
            }
        });
    }

    /**
     * อ้างอิงถึง View ต่างๆ ใน layout
     */
    private void initViews() {
        editApiCode = findViewById(R.id.edit_api_code);
        editApiName = findViewById(R.id.edit_api_name);
        editDescription = findViewById(R.id.edit_description);
        spinnerMethod = findViewById(R.id.spinner_method);
        editTestUrl = findViewById(R.id.edit_test_url);
        editProdUrl = findViewById(R.id.edit_prod_url);
        editParams = findViewById(R.id.edit_params);
        editRequestFormat = findViewById(R.id.edit_request_format);
        btnSave = findViewById(R.id.btn_save);
    }

    /**
     * ตั้งค่า Spinner สำหรับเลือก HTTP Method
     */
    private void setupMethodSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.http_methods,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMethod.setAdapter(adapter);
    }

    /**
     * โหลดข้อมูล API URL จากฐานข้อมูล
     */
    private void loadApiUrlData() {
        Cursor cursor = getContentResolver().query(
                ApiUrlProvider.CONTENT_URI,
                null,
                "id = ?",
                new String[] { String.valueOf(apiUrlId) },
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            editApiCode.setText(cursor.getString(cursor.getColumnIndex("api_code")));
            editApiName.setText(cursor.getString(cursor.getColumnIndex("api_name")));
            editDescription.setText(cursor.getString(cursor.getColumnIndex("description")));
            editTestUrl.setText(cursor.getString(cursor.getColumnIndex("test_url")));
            editProdUrl.setText(cursor.getString(cursor.getColumnIndex("prod_url")));
            editParams.setText(cursor.getString(cursor.getColumnIndex("params")));
            editRequestFormat.setText(cursor.getString(cursor.getColumnIndex("request_format")));

            // ตั้งค่า Method Spinner
            String method = cursor.getString(cursor.getColumnIndex("method"));
            ArrayAdapter adapter = (ArrayAdapter) spinnerMethod.getAdapter();
            int position = adapter.getPosition(method);
            if (position >= 0) {
                spinnerMethod.setSelection(position);
            }

            // ไม่ให้แก้ไข API Code ในโหมดแก้ไข
            editApiCode.setEnabled(false);

            cursor.close();
        } else {
            Toast.makeText(this, "ไม่พบข้อมูล API URL", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * บันทึกข้อมูล API URL ลงฐานข้อมูล
     */
    private void saveApiUrl() {
        // ตรวจสอบข้อมูลที่จำเป็น
        String apiCode = editApiCode.getText().toString().trim();
        String apiName = editApiName.getText().toString().trim();
        String method = spinnerMethod.getSelectedItem().toString();
        String testUrl = editTestUrl.getText().toString().trim();
        String prodUrl = editProdUrl.getText().toString().trim();

        if (TextUtils.isEmpty(apiCode)) {
            editApiCode.setError("กรุณาระบุรหัส API");
            editApiCode.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(apiName)) {
            editApiName.setError("กรุณาระบุชื่อ API");
            editApiName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(testUrl)) {
            editTestUrl.setError("กรุณาระบุ Test URL");
            editTestUrl.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(prodUrl)) {
            editProdUrl.setError("กรุณาระบุ Production URL");
            editProdUrl.requestFocus();
            return;
        }

        // สร้าง ContentValues สำหรับบันทึกข้อมูล
        ContentValues values = new ContentValues();
        values.put("api_code", apiCode);
        values.put("api_name", apiName);
        values.put("description", editDescription.getText().toString().trim());
        values.put("method", method);
        values.put("test_url", testUrl);
        values.put("prod_url", prodUrl);
        values.put("params", editParams.getText().toString().trim());
        values.put("request_format", editRequestFormat.getText().toString().trim());
        values.put("updated_at", System.currentTimeMillis());

        // บันทึกข้อมูล
        if (isEditMode) {
            // อัปเดตข้อมูลที่มีอยู่
            int rowsUpdated = getContentResolver().update(
                    ApiUrlProvider.CONTENT_URI,
                    values,
                    "id = ?",
                    new String[] { String.valueOf(apiUrlId) }
            );

            if (rowsUpdated > 0) {
                Toast.makeText(this, "อัปเดต API URL สำเร็จ", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                Toast.makeText(this, "ไม่สามารถอัปเดต API URL ได้", Toast.LENGTH_SHORT).show();
            }
        } else {
            // เพิ่มข้อมูลใหม่
            values.put("is_active", 1);
            values.put("env_type", SfApiUrl.ENV_TEST);  // เริ่มต้นเป็น Test Environment
            values.put("created_at", System.currentTimeMillis());

            // ตรวจสอบว่ามี API Code นี้อยู่แล้วหรือไม่
            Cursor cursor = getContentResolver().query(
                    ApiUrlProvider.CONTENT_URI,
                    new String[] { "id" },
                    "api_code = ?",
                    new String[] { apiCode },
                    null
            );

            if (cursor != null && cursor.getCount() > 0) {
                cursor.close();
                editApiCode.setError("รหัส API นี้มีอยู่แล้ว");
                editApiCode.requestFocus();
                return;
            }

            if (cursor != null) {
                cursor.close();
            }

            // เพิ่มข้อมูลใหม่
            getContentResolver().insert(ApiUrlProvider.CONTENT_URI, values);
            Toast.makeText(this, "เพิ่ม API URL สำเร็จ", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (apiUrlDao != null) {
            apiUrlDao.close();
        }
    }
}