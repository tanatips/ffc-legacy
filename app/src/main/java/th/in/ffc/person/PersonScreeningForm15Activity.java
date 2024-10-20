package th.in.ffc.person;



import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.AssessmentOfObesityFragment;
import th.in.ffc.app.form.screening.FagerstromNicotineFragment;
import th.in.ffc.app.form.screening.FragmentTabInfo;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SmookingFragment;
import th.in.ffc.app.form.screening.StressDepression2qFragment;
import th.in.ffc.app.form.screening.StressDepression9qFragment;
import th.in.ffc.app.form.screening.SuicideAssessment8qFragment;
import th.in.ffc.app.form.screening.model.DataCenterInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.ViewPagerAdapter;

public class PersonScreeningForm15Activity extends AppCompatActivity implements OnDataPass {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Button btnOk;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnOk = findViewById(R.id.btnOK);
        mContext = getBaseContext();
        ArrayList<FragmentTabInfo> fragmentTabInfos = new ArrayList<>();
//        submitButton = findViewById(R.id.submitButton);
//        resultTextView = findViewById(R.id.resultTextView);

//        fragmentTabInfos.add(new FragmentTabInfo(new BmiFragment(),"ดัชนีมวลกาย(BMI)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new BloodPressureFragment(),"ตรวจวัดความดันโลหิต"));
        fragmentTabInfos.add(new FragmentTabInfo(new SmookingFragment(),"คัดกรองความเสี่ยงจากการสูบบุหรี่"));
        fragmentTabInfos.add(new FragmentTabInfo(new AlcoholFragment(),"คัดกรองความเสี่ยงจากการดื่มสุรา"));
        fragmentTabInfos.add(new FragmentTabInfo(new FagerstromNicotineFragment(),"แบบทดสอบการติดบุหรี่"));
//        fragmentTabInfos.add(new FragmentTabInfo(new SummaryOfAssistFragment(),"สรุปคะแนนแบบคัดกรอง ASSIST"));
        fragmentTabInfos.add(new FragmentTabInfo(new AssessmentOfObesityFragment(),"ประเมินภาวะเครียด-ซึมเศร้า"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression2qFragment(),"คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression9qFragment(),"คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new SuicideAssessment8qFragment(),"การประเมินการฆ่าตัวตายด้วย 8 คําถาม (8Q)"));
        viewPagerAdapter = new ViewPagerAdapter(this,fragmentTabInfos);

        viewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(fragmentTabInfos.get(position).getTabTitle());

        }).attach();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
////                layoutParams.height = 5000;
//                viewPager.setLayoutParams(layoutParams);
                viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(),viewPager,viewPagerAdapter));

            }
        });
        viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(),viewPager,viewPagerAdapter));
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(mContext);
                    PersonInfo personInfo = new PersonInfo();
                    personInfo.setIdcard("1234567890123");
                    personInfo.setFname("John");
                    personInfo.setLname("Doe");
                    personInfo.setBirthday("1990-01-01");
                    personInfo.setGender("Male");
                    personInfo.setPhone("0812345678");
                    personInfo.setHn("HN001234");
                    personInfo.setAuthen_date("2024-03-01");
                    personInfo.setAuthen_code("AUTH001");

                    personInfo.setWeight(70.0);
                    personInfo.setHeight(175.0);
                    personInfo.setWaist_size(32.0);

                    personInfo.setBp("120/80");

                    personInfo.setSystolic_pressure(120.0);
                    personInfo.setDiastolic_pressure(80.0);

                    personInfo.setCreate_by("admin");
                    personInfo.setCreate_date("2024-03-01");
                    personInfo.setUpdate_by("admin");
                    personInfo.setUpdate_date("2024-03-02");
                    personInfo.setSend_to_claim(1);
                    sfPersonInfoDao.add(personInfo);
                    List<PersonInfo> personInfos = sfPersonInfoDao.getSfPersonInfoAll();
                    for(PersonInfo p: personInfos){
                        System.out.println(p.getId()+" "+p.getFname());
                    }
                    List<PersonInfo> personInfos1 = sfPersonInfoDao.getSfPersonInfoById(17);
                    System.out.println("===== Start get data by id ======");
                    for(PersonInfo p: personInfos1){
                        System.out.println(p.getId()+" "+p.getFname());
                    }
                    System.out.println("===== End get data by id ======");
                    Toast.makeText(getBaseContext(), "Test==>", Toast.LENGTH_LONG).show();
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void adjustViewPagerHeight(int position, ViewPager2 viewPager, ViewPagerAdapter adapter){
        Fragment fragment = adapter.getFragmentAt(position);
        if(fragment != null && fragment.getView() != null){
            fragment.getView().post(() -> {
                int width = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                fragment.getView().measure(width, height);
                int measuredHeight = fragment.getView().getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
                layoutParams.height = measuredHeight;
                viewPager.setLayoutParams(layoutParams);
            });
        }
    }

//    @Override
//    public void onDataPass(DataCenterInfo data) {
//        PersonInfo personInfo = data.getPersonInfo();
//        String msg = "====> "+personInfo.getIdcard()+ " "+personInfo.getFname()+" "+personInfo.getLname()+" "+personInfo.getGender();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
//    }

    @Override
    public void onPersonInfo(PersonInfo personInfo) {

        String msg = "====> "+personInfo.getIdcard()+ " "+personInfo.getFname()+" "+personInfo.getLname()+" "+personInfo.getGender();
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSmokerInfo(SmokerInfo smokerInfo) {
        String msg = "====> "+smokerInfo.getSmokerGroup()+" "+smokerInfo.getSmokerAssist()+" "+smokerInfo.getSmokerRegularly();
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onDrinkingInfo(DrinkingInfo drinkingInfo) {
        String msg = "====> "+drinkingInfo.getDrinking()+" "+drinkingInfo.getDrinkingFrequency()+" "+drinkingInfo.getDrinkingAlway();
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
    }
}