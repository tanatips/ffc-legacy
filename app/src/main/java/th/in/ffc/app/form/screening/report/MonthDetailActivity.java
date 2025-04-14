package th.in.ffc.app.form.screening.report;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import th.in.ffc.R;

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
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;
import th.in.ffc.app.form.screening.adapter.ClaimAdapter;
import java.util.Collections;
import android.widget.Filter; // สำหรับคลาส Filter ทั่วไป
import androidx.recyclerview.widget.RecyclerView.Adapter;

public class MonthDetailActivity extends AppCompatActivity {

    private TextView textMonthTitle;
    private TextView textDateRange;
    private TextView textTotalClaims;
    private TextView textTotalAmount;
    private SearchView searchView;
    private Spinner spinnerFilter;
    private RecyclerView recyclerClaims;
    private Button buttonExport;
    private Button buttonChart;

    private NHSOCHADao nhsochaDao;
    private ClaimAdapter adapter;
    private List<NHSOCHAInfo> claimsList;

    private String fiscalYear;
    private int fiscalMonth;
    private String monthName;

    private NumberFormat currencyFormat;
    private NumberFormat numberFormat;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_month_detail);
        // รับข้อมูลที่ส่งมา
        fiscalYear = getIntent().getStringExtra("fiscalYear");
        fiscalMonth = getIntent().getIntExtra("fiscalMonth", 1);
        monthName = getIntent().getStringExtra("monthName");

        if (fiscalYear == null) {
            // ถ้าไม่มีข้อมูลที่ส่งมา ให้ปิดหน้าจอนี้
            Toast.makeText(this, "ไม่พบข้อมูลที่ต้องการแสดง", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // เตรียม formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));
        dateFormat = new SimpleDateFormat("d MMMM yyyy", new Locale("th", "TH"));

        // สร้าง DAO
        nhsochaDao = new NHSOCHADao(this);

        // เชื่อมต่อ Views
        textMonthTitle = findViewById(R.id.text_month_title);
        textDateRange = findViewById(R.id.text_date_range);
        textTotalClaims = findViewById(R.id.text_total_claims);
        textTotalAmount = findViewById(R.id.text_total_amount);
        searchView = findViewById(R.id.search_view);
        spinnerFilter = findViewById(R.id.spinner_filter);
        recyclerClaims = findViewById(R.id.recycler_claims);
        buttonExport = findViewById(R.id.button_export);
        buttonChart = findViewById(R.id.button_chart);

        // ตั้งค่า RecyclerView
        recyclerClaims.setLayoutManager(new LinearLayoutManager(this));
        recyclerClaims.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // โหลดข้อมูล
        loadMonthDetail();

        // ตั้งค่า SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return true;
            }
        });

        // ตั้งค่า Spinner Filter
        spinnerFilter.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                String currentSearchText = searchView.getQuery().toString();

                // ปรับปรุงการเรียกใช้ setFilterType
                adapter.setFilterType(selectedFilter);

                // ใช้ currentSearchText เพื่อให้การค้นหายังคงทำงานร่วมกับตัวกรอง
                adapter.getFilter().filter(currentSearchText);
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                // ไม่ต้องทำอะไร
            }
        });

        // ตั้งค่าปุ่ม
        buttonExport.setOnClickListener(v -> {
            // ส่งออกข้อมูลเป็น Excel (จะพัฒนาต่อในอนาคต)
            Toast.makeText(this, "กำลังส่งออกข้อมูลเดือน " + monthName, Toast.LENGTH_SHORT).show();
        });

        buttonChart.setOnClickListener(v -> {
            // แสดงกราฟ (จะพัฒนาต่อในอนาคต)
            Toast.makeText(this, "กำลังเตรียมกราฟข้อมูลเดือน " + monthName, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMonthDetail() {
        // โหลดข้อมูลเดือนจาก DAO
        Map<String, Object> monthSummary = nhsochaDao.getMonthSummary(fiscalYear, fiscalMonth);

        // ตั้งค่าหัวข้อเดือน
        textMonthTitle.setText("เดือน" + monthName + " " + fiscalYear);

        // ตั้งค่าช่วงเวลา
        if (monthSummary.containsKey("startDate") && monthSummary.containsKey("endDate")) {
            String startDate = (String) monthSummary.get("startDate");
            String endDate = (String) monthSummary.get("endDate");
            textDateRange.setText("ช่วงเวลา: " + startDate + " - " + endDate);
        }

        // ตั้งค่าข้อมูลสรุป
        double totalAmount = (double) monthSummary.get("totalAmount");
        int totalCount = (int) monthSummary.get("totalCount");

        textTotalAmount.setText(currencyFormat.format(totalAmount).replace("฿", "") + " บาท");
        textTotalClaims.setText(numberFormat.format(totalCount) + " รายการ");

        // โหลดรายการเบิกจ่าย
        if (monthSummary.containsKey("details")) {
            claimsList = (List<NHSOCHAInfo>) monthSummary.get("details");

            // สร้าง adapter
            adapter = new ClaimAdapter(this, claimsList, (claim, position) -> {
                // แสดงรายละเอียดรายการเบิกจ่าย (จะพัฒนาต่อในอนาคต)
                Toast.makeText(this, "รายการ: " + claim.getChrgitem() + "\nจำนวนเงิน: " +
                                currencyFormat.format(claim.getAmount()).replace("฿", "") + " บาท",
                        Toast.LENGTH_SHORT).show();
            });

            recyclerClaims.setAdapter(adapter);

            // ตั้งค่า filter
            setupFilterSpinner(claimsList);
        }
    }
    private void setupFilterSpinner(List<NHSOCHAInfo> claims) {
        // สร้างรายการตัวเลือกสำหรับ filter
        List<String> filterList = new ArrayList<>();
        filterList.add("ทั้งหมด");

        // สร้าง Set เพื่อเก็บค่า invoice numbers ที่ไม่ซ้ำกัน
        Set<String> uniqueInvoices = new HashSet<>();

        for (NHSOCHAInfo claim : claims) {
            // เก็บเฉพาะ invoice numbers
            if (claim.getInvoiceNo() != null && !claim.getInvoiceNo().isEmpty()) {
                uniqueInvoices.add(claim.getInvoiceNo());
            }
        }

        // เพิ่มค่าจาก Set ลงในลิสต์และเรียงลำดับ
        List<String> sortedInvoices = new ArrayList<>(uniqueInvoices);
        Collections.sort(sortedInvoices); // เรียงลำดับแบบธรรมดา

        // เพิ่มลงในตัวกรอง
        filterList.addAll(sortedInvoices);

        // สร้าง adapter สำหรับ spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                filterList
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(spinnerAdapter);

        // ตั้งค่า listener สำหรับ spinner
        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                String currentSearchText = searchView.getQuery().toString();

                // ใช้เมธอด setFilterType และส่งค่าว่าง เพื่อให้แสดงทั้งหมด
                if ("ทั้งหมด".equals(selectedFilter)) {
                    adapter.setFilterType("ทั้งหมด");
                    adapter.getFilter().filter("");
                } else {
                    adapter.setFilterType(selectedFilter);
                    adapter.getFilter().filter(currentSearchText);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ไม่ต้องทำอะไร
            }
        });

        // ปรับปรุง searchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return true;
            }

            private Filter getFilter() {
                return adapter.getFilter();
            }
        });
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