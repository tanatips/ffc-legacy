package th.in.ffc.person;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TableRow;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Objects;

import th.in.ffc.R;

public class BmiInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi_info);
        // เพิ่มปุ่มกลับที่ ActionBar
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setTitle("เกณฑ์การแปลผลค่า BMI");
//        }
//        String bmi = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("bmi_value")).toString();
//        double bmiValue = Double.valueOf(getIntent().getStringExtra("bmi_value"));
//        highlightBMIRow(Float.valueOf(bmi));
    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    // สร้างฟังก์ชันสำหรับเปลี่ยนสี row ตามค่า BMI
    private void highlightBMIRow(double bmi) {
        // เก็บ reference ของทุก TableRow (ยกเว้น header row)
        TableRow rowUnderweight = (TableRow) findViewById(R.id.row_underweight);    // BMI < 18.5
        TableRow rowNormal = (TableRow) findViewById(R.id.row_normal);              // BMI 18.5-22.9
        TableRow rowOverweight = (TableRow) findViewById(R.id.row_overweight);      // BMI 23.0-24.9
        TableRow rowObese = (TableRow) findViewById(R.id.row_obese);               // BMI 25.0-29.9
        TableRow rowExtremeObese = (TableRow) findViewById(R.id.row_extreme_obese); // BMI >= 30

        // คืนค่าสีพื้นหลังเป็นค่าเริ่มต้นก่อน
        rowUnderweight.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rowNormal.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rowOverweight.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rowObese.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        rowExtremeObese.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        // ตรวจสอบค่า BMI และเปลี่ยนสีพื้นหลังของ row ที่เกี่ยวข้อง
        if (bmi < 18.5) {
            rowUnderweight.setBackgroundColor(getResources().getColor(R.color.yellow_light));
        } else if (bmi >= 18.5 && bmi <= 22.9) {
            rowNormal.setBackgroundColor(getResources().getColor(R.color.green_light));
        } else if (bmi >= 23.0 && bmi <= 24.9) {
            rowOverweight.setBackgroundColor(getResources().getColor(R.color.orange_light));
        } else if (bmi >= 25.0 && bmi <= 29.9) {
            rowObese.setBackgroundColor(getResources().getColor(R.color.orange_dark));
        } else {
            rowExtremeObese.setBackgroundColor(getResources().getColor(R.color.red_light));
        }
    }
}