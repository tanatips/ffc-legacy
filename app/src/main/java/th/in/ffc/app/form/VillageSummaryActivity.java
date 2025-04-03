package th.in.ffc.app.form;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import th.in.ffc.R;
import th.in.ffc.model.VillageSummary;
import th.in.ffc.service.PersonVillageService;
import th.in.ffc.service.SfPersonVillageService;

import java.util.List;

public class VillageSummaryActivity extends AppCompatActivity {

    private PersonVillageService personVillageService;
    private SfPersonVillageService sfPersonVillageService;
    private TextView summaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_summary);

        summaryTextView = findViewById(R.id.summaryTextView);
        personVillageService = new PersonVillageService(this);
        sfPersonVillageService = new SfPersonVillageService(this);

        displayVillageSummary();
    }

    private void displayVillageSummary() {
        // รับข้อมูลสรุปจำนวนคนตามหมู่บ้าน
        List<VillageSummary> summaries = personVillageService.getPersonCountByVillage();

        // จำนวนคนทั้งหมด
        int totalPersons = personVillageService.getTotalPersonCount();

        // สร้างข้อความสรุป
        StringBuilder summaryText = new StringBuilder();
        summaryText.append("สรุปจำนวนประชากรตามหมู่บ้าน\n\n");

        for (VillageSummary summary : summaries) {
            summaryText.append(summary.toString()).append("\n");
        }

        summaryText.append("\nรวมประชากรทั้งหมด: ").append(totalPersons).append(" คน");

        // แสดงผล
        summaryTextView.setText(summaryText.toString());
    }
}