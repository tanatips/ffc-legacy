package th.in.ffc.app.form.screening;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.adapter.VillageComparisonAdapter;
import th.in.ffc.model.VillagePersonComparison;
import th.in.ffc.service.VillageComparisonService;

public class VillageComparisonActivity extends AppCompatActivity {

    private VillageComparisonService comparisonService;
    private TextView comparisonTextView;
    private TextView overallPercentageTextView;
    private TextView totalPersonTextView;
    private TextView totalSfPersonTextView;
    private BarChart barChart;
    private PieChart pieChart;
    private RecyclerView villageRecyclerView;
    private VillageComparisonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_comparison);

        // ค้นหา View ต่างๆ
        comparisonTextView = findViewById(R.id.comparisonTextView);
        overallPercentageTextView = findViewById(R.id.overallPercentageTextView);
        totalPersonTextView = findViewById(R.id.totalPersonTextView);
        totalSfPersonTextView = findViewById(R.id.totalSfPersonTextView);
        barChart = findViewById(R.id.barChart);
        pieChart = findViewById(R.id.pieChart);
        villageRecyclerView = findViewById(R.id.villageRecyclerView);

        // สร้าง Service
        comparisonService = new VillageComparisonService(this);

        // ตั้งค่า RecyclerView
        villageRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // แสดงข้อมูลทั้งหมด
        displayComparisonData();
    }

    private void displayComparisonData() {
        // รับข้อมูลเปรียบเทียบ
        List<VillagePersonComparison> comparisonList = comparisonService.getVillageComparisonData();

        // คำนวณตัวเลขรวม
        int totalPerson = 0;
        int totalSfPerson = 0;

        for (VillagePersonComparison comparison : comparisonList) {
            totalPerson += comparison.getPersonCount();
            totalSfPerson += comparison.getSfPersonCount();
        }

        double overallPercentage = 0;
        if (totalPerson > 0) {
            overallPercentage = ((double) totalSfPerson / totalPerson) * 100;
        }

        // แสดงข้อมูลภาพรวม
        NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));
        overallPercentageTextView.setText(String.format("%.2f%%", overallPercentage));
        totalPersonTextView.setText(numberFormat.format(totalPerson) + " คน");
        totalSfPersonTextView.setText(numberFormat.format(totalSfPerson) + " คน");

        // แสดงข้อมูลใน RecyclerView
        adapter = new VillageComparisonAdapter(comparisonList);
        villageRecyclerView.setAdapter(adapter);

        // แสดงกราฟแท่ง
        setupBarChart(comparisonList);

        // แสดงกราฟวงกลม
        setupPieChart(comparisonList);

        // สร้างข้อความสรุปใน TextView
        StringBuilder summaryText = new StringBuilder();
        summaryText.append("รายละเอียดข้อมูลเปรียบเทียบ:\n\n");

        for (VillagePersonComparison comparison : comparisonList) {
            summaryText.append(comparison.toString()).append("\n\n");
        }

        summaryText.append("อัตราความครอบคลุมรวมทั้งหมด: ").append(String.format("%.2f", overallPercentage)).append("%");

        comparisonTextView.setText(summaryText.toString());
    }

    private void setupBarChart(List<VillagePersonComparison> comparisonList) {
        List<BarEntry> personEntries = new ArrayList<>();
        List<BarEntry> sfPersonEntries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < comparisonList.size(); i++) {
            VillagePersonComparison comparison = comparisonList.get(i);
            personEntries.add(new BarEntry(i, comparison.getPersonCount()));
            sfPersonEntries.add(new BarEntry(i, comparison.getSfPersonCount()));

            // เปลี่ยนจาก "หมู่ " + comparison.getVillageNo()
            // เป็นการใช้ชื่อหมู่บ้านโดยตรง
            labels.add(comparison.getVillageName()); // สมมติว่ามี method getVillageName()

            // หรือหากไม่มี method getVillageName() ให้ใช้
            // labels.add(comparison.getVillageNo()); // แสดงเฉพาะเลขหมู่บ้าน
        }

        BarDataSet personDataSet = new BarDataSet(personEntries, "ประชากรทั้งหมด");
        personDataSet.setColor(Color.rgb(76, 175, 80)); // สีเขียว

        BarDataSet sfPersonDataSet = new BarDataSet(sfPersonEntries, "ผู้รับบริการ");
        sfPersonDataSet.setColor(Color.rgb(33, 150, 243)); // สีฟ้า

        float groupSpace = 0.1f;
        float barSpace = 0.02f;
        float barWidth = 0.43f;

        BarData barData = new BarData(personDataSet, sfPersonDataSet);
        barData.setBarWidth(barWidth);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        // กำหนดค่าแกน X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);

        // แก้ปัญหาชื่อยาวทับกัน
        xAxis.setLabelRotationAngle(-90f); // หมุน 90 องศา (แนวตั้ง)
        xAxis.setTextSize(8f); // ลดขนาดตัวอักษร
        xAxis.setLabelCount(labels.size()); // กำหนดจำนวน label ที่แสดง
        xAxis.setAvoidFirstLastClipping(true); // หลีกเลี่ยงการตัดขอบ

        // เพิ่มพื้นที่ด้านล่างสำหรับแสดงชื่อ
        barChart.setExtraBottomOffset(50f);

        barChart.getAxisLeft().setGranularity(1f);
        barChart.getAxisRight().setEnabled(false);

        // กำหนดขอบเขตแกน X
        barChart.getXAxis().setAxisMinimum(0);
        barChart.getXAxis().setAxisMaximum(comparisonList.size());

        // จัดกลุ่มแท่ง
        barChart.groupBars(0, groupSpace, barSpace);

        barChart.animateY(1000);
        barChart.invalidate();
    }
    private void setupPieChart(List<VillagePersonComparison> comparisonList) {
        List<PieEntry> entries = new ArrayList<>();

        for (VillagePersonComparison comparison : comparisonList) {
            if (comparison.getPercentage() > 0) {
                entries.add(new PieEntry((float) comparison.getPercentage(),  comparison.getVillageName()));
            }
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.WHITE);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        pieData.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(pieChart));

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(35f);
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setCenterText("ความครอบคลุม");
        pieChart.setCenterTextSize(16f);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        pieChart.animateY(1000);
        pieChart.invalidate();
    }
}