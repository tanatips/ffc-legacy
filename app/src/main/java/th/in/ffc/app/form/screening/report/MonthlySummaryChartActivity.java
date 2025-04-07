package th.in.ffc.app.form.screening.report;


import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.nhso.dao.NHSOCHADao;

public class MonthlySummaryChartActivity extends AppCompatActivity {

    private TextView textFiscalYear;
    private BarChart barChart;
    private RadioGroup radioGroupChartType;
    private RadioButton radioAmount;
    private RadioButton radioCount;
    private Button buttonStatusChart;

    private NHSOCHADao nhsochaDao;
    private String fiscalYear;
    private List<Map<String, Object>> monthlyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_summary_chart);

        // รับค่าปีงบประมาณที่ส่งมา
        fiscalYear = getIntent().getStringExtra("fiscalYear");
        if (fiscalYear == null) {
            // ถ้าไม่ได้รับค่ามา ให้ใช้ปีปัจจุบัน
            fiscalYear = "2567";
        }

        // สร้าง DAO
        nhsochaDao = new NHSOCHADao(this);

        // เชื่อมต่อ Views
        textFiscalYear = findViewById(R.id.text_fiscal_year);
        barChart = findViewById(R.id.bar_chart);
        radioGroupChartType = findViewById(R.id.radio_group_chart_type);
        radioAmount = findViewById(R.id.radio_amount);
        radioCount = findViewById(R.id.radio_count);
        buttonStatusChart = findViewById(R.id.button_status_chart);

        // ตั้งค่าข้อความแสดงปีงบประมาณ
        textFiscalYear.setText("ปีงบประมาณ: " + fiscalYear);

        // โหลดข้อมูล
        loadMonthlyData();

        // ตั้งค่า listener สำหรับปุ่มเลือกประเภทกราฟ
        radioGroupChartType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_amount) {
                setupAmountChart();
            } else if (checkedId == R.id.radio_count) {
                setupCountChart();
            }
        });

        // ตั้งค่า listener สำหรับปุ่มดูกราฟแยกตามสถานะ
        buttonStatusChart.setOnClickListener(v -> {
            // จะเพิ่มฟังก์ชันนี้ในอนาคต
            Toast.makeText(this, "การแสดงกราฟแยกตามสถานะจะเพิ่มในอนาคต", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadMonthlyData() {
        // โหลดข้อมูลสรุปรายเดือนจาก DAO
        monthlyData = nhsochaDao.getMonthlySummaryByFiscalYear(fiscalYear);

        // แสดงกราฟยอดเงินตั้งต้น
        setupAmountChart();
    }

    private void setupAmountChart() {
        // สร้างข้อมูลสำหรับกราฟยอดเงิน
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // เพิ่มข้อมูลกราฟ
        for (int i = 0; i < monthlyData.size(); i++) {
            Map<String, Object> monthData = monthlyData.get(i);
            float amount = ((Double) monthData.get("totalAmount")).floatValue();
            entries.add(new BarEntry(i, amount));
            labels.add((String) monthData.get("monthName"));
        }

        // สร้าง BarDataSet และตั้งค่า
        BarDataSet dataSet = new BarDataSet(entries, "ยอดเงิน (บาท)");
        dataSet.setColor(Color.rgb(65, 105, 225)); // สีน้ำเงิน
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // สร้าง BarData
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        // ตั้งค่ากราฟ
        setupBarChart(barData, labels, "ยอดเงินรายเดือน (บาท)");
    }

    private void setupCountChart() {
        // สร้างข้อมูลสำหรับกราฟจำนวนรายการ
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();

        // เพิ่มข้อมูลกราฟ
        for (int i = 0; i < monthlyData.size(); i++) {
            Map<String, Object> monthData = monthlyData.get(i);
            float count = ((Integer) monthData.get("totalCount")).floatValue();
            entries.add(new BarEntry(i, count));
            labels.add((String) monthData.get("monthName"));
        }

        // สร้าง BarDataSet และตั้งค่า
        BarDataSet dataSet = new BarDataSet(entries, "จำนวนรายการ");
        dataSet.setColor(Color.rgb(46, 139, 87)); // สีเขียว
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        // สร้าง BarData
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.7f);

        // ตั้งค่ากราฟ
        setupBarChart(barData, labels, "จำนวนรายการรายเดือน");
    }

    private void setupBarChart(BarData barData, final List<String> labels, String description) {
        // ตั้งค่า X Axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });

        // ตั้งค่า Y Axis
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);

        // ตั้งค่า Description
        Description desc = new Description();
        desc.setText(description);
        desc.setTextSize(14f);
        barChart.setDescription(desc);

        // ตั้งค่าอื่นๆ
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
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