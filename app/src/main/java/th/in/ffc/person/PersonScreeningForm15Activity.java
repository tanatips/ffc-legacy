package th.in.ffc.person;



import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.tabs.TabLayout;

import th.in.ffc.R;
import th.in.ffc.util.ViewPagerAdapter;

public class PersonScreeningForm15Activity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
//        submitButton = findViewById(R.id.submitButton);
//        resultTextView = findViewById(R.id.resultTextView);
        viewPagerAdapter = new ViewPagerAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("ดัชนีมวลกาย(BMI)");
                    break;
                case 1:
                    tab.setText("ตรวจวัดความดันโลหิต");
                    break;
                case 2:
                    tab.setText("สุรา");
                    break;
                case 3:
                    tab.setText("แบบทดสอบการติดสุรา");
                    break;
                case 4:
                    tab.setText("สรุปคะแนนแบบคัดกรอง");
                    break;
                case 5:
                    tab.setText("ประเมินภาวะเครียด-ซึมเศร้า");
                    break;
                case 6:
                    tab.setText("Tab 6");
                    break;
                case 7:
                    tab.setText("Tab 7");
                    break;
                case 8:
                    tab.setText("Tab 8");
                    break;
                case 9:
                    tab.setText("Tab 9");
                    break;
                case 10:
                    tab.setText("Tab 10");
                    break;
            }
        }).attach();
    }
}