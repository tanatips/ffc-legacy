package th.in.ffc.app.form.screening.report;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.screening.adapter.MonthlySummaryAdapter;

public class MonthlySummaryActivity extends AppCompatActivity {

    private Spinner spinnerFiscalYear;
    private TextView textDateRange;
    private TextView textTotalAmount;
    private TextView textTotalClaims;
    private RecyclerView recyclerMonthlyData;
    private Button buttonViewChart;
    private Button buttonExport;

    private NHSOCHADao nhsochaDao;
    private MonthlySummaryAdapter adapter;
    private List<Map<String, Object>> monthlyData;

    private String currentFiscalYear;
    private NumberFormat currencyFormat;
    private NumberFormat numberFormat;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summary);

        // ตั้งค่า Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // เตรียม formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));
        dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("th", "TH"));

        // สร้าง DAO
        nhsochaDao = new NHSOCHADao(this);

        // เชื่อมต่อ Views
        spinnerFiscalYear = findViewById(R.id.spinner_fiscal_year);
        textDateRange = findViewById(R.id.text_date_range);
        textTotalAmount = findViewById(R.id.text_total_amount);
        textTotalClaims = findViewById(R.id.text_total_claims);
        recyclerMonthlyData = findViewById(R.id.recycler_monthly_data);
        buttonViewChart = findViewById(R.id.button_view_chart);
        buttonExport = findViewById(R.id.button_export);

        // ตั้งค่า RecyclerView
        recyclerMonthlyData.setLayoutManager(new LinearLayoutManager(this));
        recyclerMonthlyData.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // สร้างปีงบประมาณสำหรับ spinner
        setupFiscalYearSpinner();

        // ตั้งค่า listener สำหรับ spinner
        spinnerFiscalYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFiscalYear = (String) parent.getItemAtPosition(position);
                loadMonthlySummaryData(currentFiscalYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ไม่ต้องทำอะไร
            }
        });

        // ตั้งค่า listener สำหรับปุ่ม
        buttonViewChart.setOnClickListener(v -> {
            Intent intent = new Intent(MonthlySummaryActivity.this, MonthlySummaryChartActivity.class);
            intent.putExtra("fiscalYear", currentFiscalYear);
            startActivity(intent);
        });

        buttonExport.setOnClickListener(v -> {
            // ทำการส่งออกรายงาน (จะทำต่อในอนาคต)
            Toast.makeText(this, "กำลังส่งออกรายงานปีงบประมาณ " + currentFiscalYear, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupFiscalYearSpinner() {
        // สร้างรายการปีงบประมาณย้อนหลัง 5 ปี และล่วงหน้า 1 ปี
        List<String> fiscalYears = new ArrayList<>();

        // หาปีงบประมาณปัจจุบัน
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR); // ค.ศ.
        int currentMonth = cal.get(Calendar.MONTH); // 0-11

        // ในประเทศไทย ปีงบประมาณเริ่มต้น 1 ตุลาคม ถึง 30 กันยายนของปีถัดไป
        // ถ้าเดือนปัจจุบันเป็นตุลาคมหรือหลังจากนั้น ปีงบประมาณจะเป็นปีถัดไป
        int currentFiscalYear;
        if (currentMonth >= Calendar.OCTOBER) {
            currentFiscalYear = currentYear + 1;
        } else {
            currentFiscalYear = currentYear;
        }

        // สร้างรายการปีงบประมาณ
        for (int i = -5; i <= 1; i++) {
            fiscalYears.add(String.valueOf(currentFiscalYear + i));
        }

        // สร้าง adapter สำหรับ spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, fiscalYears);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiscalYear.setAdapter(adapter);

        // ตั้งค่าให้แสดงปีงบประมาณปัจจุบัน
        int currentYearPosition = fiscalYears.indexOf(String.valueOf(currentFiscalYear));
        if (currentYearPosition != -1) {
            spinnerFiscalYear.setSelection(currentYearPosition);
        }
    }

    private void loadMonthlySummaryData(String fiscalYear) {
        // โหลดข้อมูลสรุปรายเดือนจาก DAO
        monthlyData = nhsochaDao.getMonthlySummaryByFiscalYear(fiscalYear);

        // แสดงช่วงเวลาของปีงบประมาณ
        updateDateRangeText(fiscalYear);

        // คำนวณยอดรวม
        double totalAmount = 0;
        int totalClaims = 0;

        for (Map<String, Object> monthData : monthlyData) {
            totalAmount += (double) monthData.get("totalAmount");
            totalClaims += (int) monthData.get("totalCount");
        }

        // แสดงยอดรวม
        textTotalAmount.setText(currencyFormat.format(totalAmount).replace("฿", "") + " บาท");
        textTotalClaims.setText(numberFormat.format(totalClaims) + " รายการ");

        // สร้างหรืออัปเดต adapter
        if (adapter == null) {
            adapter = new MonthlySummaryAdapter(this, monthlyData, (monthData, position) -> {
                // เมื่อคลิกที่รายการเดือน เปิดหน้าแสดงรายละเอียดของเดือนนั้น
                int fiscalMonth = (int) monthData.get("fiscalMonth");
                String monthName = (String) monthData.get("monthName");

                Intent intent = new Intent(MonthlySummaryActivity.this, MonthDetailActivity.class);
                intent.putExtra("fiscalYear", currentFiscalYear);
                intent.putExtra("fiscalMonth", fiscalMonth);
                intent.putExtra("monthName", monthName);
                startActivity(intent);
            });

            recyclerMonthlyData.setAdapter(adapter);
        } else {
            adapter.updateData(monthlyData);
        }
    }

    private void updateDateRangeText(String fiscalYear) {
        int year = Integer.parseInt(fiscalYear) - 1;

        Calendar calStart = Calendar.getInstance();
        calStart.set(year, Calendar.OCTOBER, 1);
        Date startDate = calStart.getTime();

        Calendar calEnd = Calendar.getInstance();
        calEnd.set(year + 1, Calendar.SEPTEMBER, 30);
        Date endDate = calEnd.getTime();

        String dateRangeText = "ช่วงเวลา: " + dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        textDateRange.setText(dateRangeText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}