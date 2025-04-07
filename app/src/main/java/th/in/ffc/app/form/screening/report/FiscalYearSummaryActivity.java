package th.in.ffc.app.form.screening.report;

import android.content.Intent;
import android.graphics.Color;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;
import th.in.ffc.app.form.screening.adapter.ChargeItemSummaryAdapter;

public class FiscalYearSummaryActivity extends AppCompatActivity {

    private Spinner spinnerFiscalYear;
    private TextView textDateRange;
    private TextView textTotalAmount;
    private TextView textTotalClaims;
    private RecyclerView recyclerChargeItems;
    private PieChart pieChart;
    private Button buttonMonthlyDetail;
    private Button buttonExport;

    private NHSOCHADao nhsochaDao;
    private ChargeItemSummaryAdapter adapter;

    private String currentFiscalYear;
    private NumberFormat currencyFormat;
    private NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiscal_year_summary);

        // ตั้งค่า Toolbar
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // เตรียม formatter
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));

        // สร้าง DAO
        nhsochaDao = new NHSOCHADao(this);

        // เชื่อมต่อ Views
        spinnerFiscalYear = findViewById(R.id.spinner_fiscal_year);
        textDateRange = findViewById(R.id.text_date_range);
        textTotalAmount = findViewById(R.id.text_total_amount);
        textTotalClaims = findViewById(R.id.text_total_claims);
        recyclerChargeItems = findViewById(R.id.recycler_charge_items);
        pieChart = findViewById(R.id.pie_chart);
        buttonMonthlyDetail = findViewById(R.id.button_monthly_detail);
        buttonExport = findViewById(R.id.button_export);

        // ตั้งค่า RecyclerView
        recyclerChargeItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerChargeItems.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // ตั้งค่า PieChart
        setupPieChart();

        // สร้างปีงบประมาณสำหรับ spinner
        setupFiscalYearSpinner();

        // ตั้งค่า listener สำหรับ spinner
        spinnerFiscalYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentFiscalYear = (String) parent.getItemAtPosition(position);
                loadYearlySummaryData(currentFiscalYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ไม่ต้องทำอะไร
            }
        });

        // ตั้งค่า listener สำหรับปุ่ม
        buttonMonthlyDetail.setOnClickListener(v -> {
            Intent intent = new Intent(FiscalYearSummaryActivity.this, MonthlySummaryActivity.class);
            intent.putExtra("fiscalYear", currentFiscalYear);
            startActivity(intent);
        });

        buttonExport.setOnClickListener(v -> {
            // ทำการส่งออกรายงานเป็น Excel (จะทำต่อในอนาคต)
            Toast.makeText(this, "กำลังส่งออกรายงานปีงบประมาณ " + currentFiscalYear, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("สัดส่วนรายการ");
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
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

    private void loadYearlySummaryData(String fiscalYear) {
        // โหลดข้อมูลสรุปรายปีจาก DAO
        Map<String, Object> yearSummary = nhsochaDao.getYearlySummaryByFiscalYear(fiscalYear);

        // แสดงช่วงเวลาของปีงบประมาณ
        if (yearSummary.containsKey("startDate") && yearSummary.containsKey("endDate")) {
            String startDate = (String) yearSummary.get("startDate");
            String endDate = (String) yearSummary.get("endDate");
            textDateRange.setText("ช่วงเวลา: " + startDate + " - " + endDate);
        }

        // แสดงข้อมูลสรุปรวม
        double totalAmount = (double) yearSummary.get("totalAmount");
        int totalClaims = (int) yearSummary.get("totalCount");

        textTotalAmount.setText(currencyFormat.format(totalAmount).replace("฿", "") + " บาท");
        textTotalClaims.setText(numberFormat.format(totalClaims) + " รายการ");

        // แสดงข้อมูลสรุปตามประเภทรายการ
        List<Map<String, Object>> chargeItemSummary = (List<Map<String, Object>>) yearSummary.get("chargeItemSummary");
        if (chargeItemSummary != null) {
            // สร้างหรืออัปเดต adapter
            if (adapter == null) {
                adapter = new ChargeItemSummaryAdapter(this, chargeItemSummary);
                recyclerChargeItems.setAdapter(adapter);
            } else {
                adapter.updateData(chargeItemSummary);
            }

            // อัปเดต PieChart
            updatePieChart(chargeItemSummary);
        }
    }

    private void updatePieChart(List<Map<String, Object>> chargeItemSummary) {
        List<PieEntry> entries = new ArrayList<>();

        // สร้างข้อมูลสำหรับแผนภูมิวงกลม
        for (Map<String, Object> item : chargeItemSummary) {
            String chrgitem = (String) item.get("chrgitem");
            double amount = (double) item.get("amount");

            if (amount > 0) {
                entries.add(new PieEntry((float) amount, chrgitem));
            }
        }

        if (entries.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            return;
        } else {
            pieChart.setVisibility(View.VISIBLE);
        }

        // สร้างชุดข้อมูลพร้อมสี
        PieDataSet dataSet = new PieDataSet(entries, "ประเภทรายการ");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // กำหนดสี
        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }
        dataSet.setColors(colors);

        // สร้างข้อมูลและตั้งค่า formatter
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);

        // ตั้งค่าข้อมูลและอัปเดต
        pieChart.setData(data);
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1000);
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