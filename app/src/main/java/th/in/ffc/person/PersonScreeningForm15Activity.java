package th.in.ffc.person;



import android.content.Context;
import android.os.Build;
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
import th.in.ffc.app.form.screening.HealthRiskAssessmentFragment;
import th.in.ffc.app.form.screening.StressDepressionFragment;
import th.in.ffc.app.form.screening.FagerstromNicotineFragment;
import th.in.ffc.app.form.screening.FragmentTabInfo;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SmookingFragment;
import th.in.ffc.app.form.screening.StressDepression2qFragment;
import th.in.ffc.app.form.screening.StressDepression9qFragment;
import th.in.ffc.app.form.screening.SuicideAssessment8qFragment;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.ViewPagerAdapter;

public class PersonScreeningForm15Activity extends AppCompatActivity implements OnDataPass {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Button btnOk;

    private Context mContext;

    PersonInfo personInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnOk = findViewById(R.id.btnOK);
        mContext = getBaseContext();
        ArrayList<FragmentTabInfo> fragmentTabInfos = new ArrayList<>();
        personInfo = new PersonInfo();
        personInfo.setBirthday("1990-01-01");
//        submitButton = findViewById(R.id.submitButton);
//        resultTextView = findViewById(R.id.resultTextView);

//        fragmentTabInfos.add(new FragmentTabInfo(new BmiFragment(),"ดัชนีมวลกาย(BMI)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new BloodPressureFragment(),"ตรวจวัดความดันโลหิต"));
        fragmentTabInfos.add(new FragmentTabInfo(new SmookingFragment(),"คัดกรองความเสี่ยงจากการสูบบุหรี่"));
        fragmentTabInfos.add(new FragmentTabInfo(new FagerstromNicotineFragment(),"แบบทดสอบการติดบุหรี่"));
        fragmentTabInfos.add(new FragmentTabInfo(new AlcoholFragment(),"คัดกรองความเสี่ยงจากการดื่มสุรา"));
//        fragmentTabInfos.add(new FragmentTabInfo(new SummaryOfAssistFragment(),"สรุปคะแนนแบบคัดกรอง ASSIST"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepressionFragment(),"ประเมินภาวะเครียด-ซึมเศร้า"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression2qFragment(),"คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression9qFragment(),"คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new SuicideAssessment8qFragment(),"การประเมินการฆ่าตัวตายด้วย 8 คําถาม (8Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new HealthRiskAssessmentFragment(),"แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน"));
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
                    Toast.makeText(getBaseContext(), "Test==>", Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onPersonInfo(PersonInfo personInfo) {

        String msg = "====> "+personInfo.getIdcard()+ " "+personInfo.getFname()+" "+personInfo.getLname()+" "+personInfo.getGender();
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSmokerInfo(SmokerInfo smokerInfo) {
        String msg = "====> "+smokerInfo.getSmokerGroup()+" "+smokerInfo.getSmokerAssist()+" "+smokerInfo.getSmokerRegularly();
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onDrinkingInfo(DrinkingInfo drinkingInfo) {
        String msg = "====> "+drinkingInfo.getDrinking()+" "+drinkingInfo.getDrinkingFrequency()+" "+drinkingInfo.getDrinkingAlway();
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNicotineInfo(NicotineInfo nicotineInfo) {
        final int[] sum = {0};
        // ต้องเป็น final array เพราะใช้ใน lambda
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nicotineInfo.getPoints().forEach(num -> sum[0] += num);
        }

        String msg = "====> "+nicotineInfo.getNicotine1()
                +" "+nicotineInfo.getNicotine2()
                +" "+nicotineInfo.getNicotine3()
                +" "+nicotineInfo.getNicotine4()
                +" "+nicotineInfo.getNicotine5()
                +" "+nicotineInfo.getNicotine6()
                +" คะแนน: "+ sum[0]
                ;
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStressDepression(StressDepressionInfo data) {

        String msg = "====> "+data.getQ1()
                +" "+data.getQ2()
                +" "+data.getQ3()
                +" "+data.getQ4()
                +" "+data.getQ5()
                +" คะแนน: "+data.getSum()
                +"  "+data.getResultCode()
                +"  "+data.getResultDescription()
                ;

        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStressDepression2q(StressDepression2qInfo data) {
        data.analyze();
        String msg = "====> "+data.getQ1()
                +" "+data.getQ2()
                +" คะแนน : "+data.getSum()
                +" "+data.getResultCode()
                +" "+data.getResultDescription()
                ;
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStressDepression9q(StressDepression9qInfo data) {
        Integer age = AgeCalculator.calculateAge(personInfo.getBirthday());
        data.analyze(age);
        String msg = "====> "
                +data.getQ1()
                +" "+data.getQ2()
                +" "+data.getQ3()
                +" "+data.getQ4()
                +" "+data.getQ5()
                +" "+data.getQ6()
                +" "+data.getQ7()
                +" "+data.getQ8()
                +" "+data.getQ9()
                +" คะแนน: "+data.getSum()
                +"  "+data.getResultCode()
                +"  "+data.getResultDescription()
                ;
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuicideAssessment8q(SuicideAssessment8qInfo data) {
        String msg = "====> "
                +data.getQ1()
                +" "+data.getQ2()
                +" "+data.getQ3()
                +" "+data.getQ3_2_1()
                +" "+data.getQ4()
                +" "+data.getQ5()
                +" "+data.getQ6()
                +" "+data.getQ7()
                +" "+data.getQ8()
                ;
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onHealthRiskAssessmentInfo(HealthRiskAssessmentInfo data) {
        String msg = "====> "+data.getHealthRiskQ1()
                +" "+data.getHealthRiskQ2()
                +" "+data.getHealthRiskQ3()
                +" "+data.getHealthRiskQ4()
                +" "+data.getHealthRiskQ5()
                +" "+data.getHealthRiskQ6()
                ;
        System.out.println(msg);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}