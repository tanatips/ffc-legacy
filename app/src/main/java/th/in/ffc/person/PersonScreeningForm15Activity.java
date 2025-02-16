package th.in.ffc.person;



import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.HealthRiskAssessmentFragment;
import th.in.ffc.app.form.screening.MainQuestionsFragment;
import th.in.ffc.app.form.screening.PersonInfoFragment;
import th.in.ffc.app.form.screening.QuestionOneFragment;
import th.in.ffc.app.form.screening.SharedViewModel;
import th.in.ffc.app.form.screening.StressDepressionFragment;
import th.in.ffc.app.form.screening.FagerstromNicotineFragment;
import th.in.ffc.app.form.screening.FragmentTabInfo;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SmookingFragment;
import th.in.ffc.app.form.screening.StressDepression2qFragment;
import th.in.ffc.app.form.screening.StressDepression9qFragment;
import th.in.ffc.app.form.screening.SuicideAssessment8qFragment;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression9qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepressionInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.datalive.HealthRiskAssessmentLiveData;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepressionLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.AssistScore;
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
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.ViewPagerAdapter;

public class PersonScreeningForm15Activity extends AppCompatActivity implements OnDataPass {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Button btnOk, btnCancel;

    private Context mContext;

    PersonInfo personInfo;
    SmokerInfo smokerInfo;

    DrinkingInfo drinkingInfo;

    NicotineInfo nicotineInfo;

    StressDepressionInfo stressDepressionInfo;

    StressDepression2qInfo stressDepression2qInfo;

    StressDepression9qInfo stressDepression9qInfo;

    SuicideAssessment8qInfo suicideAssessment8qInfo;

    HealthRiskAssessmentInfo healthRiskAssessmentInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnOk = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);
        mContext = getBaseContext();
        ArrayList<FragmentTabInfo> fragmentTabInfos = new ArrayList<>();
//        personInfo = new PersonInfo();
//        smokerInfo = new SmokerInfo();
//        stressDepressionInfo = new StressDepressionInfo();
//        personInfo.setBirthday("1990-01-01");
//        submitButton = findViewById(R.id.submitButton);
//        resultTextView = findViewById(R.id.resultTextView);

//        fragmentTabInfos.add(new FragmentTabInfo(new BmiFragment(),"ดัชนีมวลกาย(BMI)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new BloodPressureFragment(),"ตรวจวัดความดันโลหิต"));
        fragmentTabInfos.add(new FragmentTabInfo(new MainQuestionsFragment(),"แบบคัดกรองการใช้สารเสพติด"));
//        fragmentTabInfos.add(new FragmentTabInfo(new QuestionOneFragment(),"แบบคัดกรองการดึมสุรา สูบบุหรี่ และใช้สารเสพติด"));
        fragmentTabInfos.add(new FragmentTabInfo(new SmookingFragment(),"คัดกรองความเสี่ยงจากการสูบบุหรี่"));
        fragmentTabInfos.add(new FragmentTabInfo(new FagerstromNicotineFragment(),"แบบทดสอบการติดบุหรี่"));
        fragmentTabInfos.add(new FragmentTabInfo(new AlcoholFragment(),"คัดกรองความเสี่ยงจากการดื่มสุรา"));
//        fragmentTabInfos.add(new FragmentTabInfo(new SummaryOfAssistFragment(),"สรุปคะแนนแบบคัดกรอง ASSIST"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepressionFragment(),"ประเมินภาวะเครียด-ซึมเศร้า(ST 5)"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression2qFragment(),"คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression9qFragment(),"คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new SuicideAssessment8qFragment(),"การประเมินการฆ่าตัวตายด้วย 8 คําถาม (8Q)"));
        fragmentTabInfos.add(new FragmentTabInfo(new HealthRiskAssessmentFragment(),"แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน"));
        fragmentTabInfos.add(new FragmentTabInfo(new CardiovascularRiskFragment() ,"คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด"));
        fragmentTabInfos.add(new FragmentTabInfo(new AssistScoreFragment(),"สรุปคะแนนแบบคัดกรอง ASSIST"));
        viewPagerAdapter = new ViewPagerAdapter(this,fragmentTabInfos);

        viewPager.setAdapter(viewPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(fragmentTabInfos.get(position).getTabTitle());

        }).attach();
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(),viewPager,viewPagerAdapter));
            }
        });
        viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(),viewPager,viewPagerAdapter));
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    savePerson();
                    if(personInfo.getId()!=null) {
                        saveSmoker();
                        saveStressDepression();
                        saveNicotine();
                        saveDrinking();
                        saveStressDepression2q();
                        saveStressDepression9q();
                        saveSuicideAssessment8q();
                        saveHealthRiskAssessment();
                        Toast.makeText(getBaseContext(), "บันทึกข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getPersonInfoDetail();
    }
    private void getPersonInfoDetail(){

        String personId = getIntent().getStringExtra("person_id");
        if(personId!=null) {
            // ใน Activity
            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);

            PersonInfoLiveData personInfoLiveData = new PersonInfoLiveData();
            personInfoLiveData.setId(personId);
            viewModel.setPersonInfoLiveDataMutableLiveData(personInfoLiveData);

            SmookingLiveData smookingLiveData = new SmookingLiveData();
            smookingLiveData.setPersonId(personId);
            viewModel.setSmookingMutableLiveData(smookingLiveData);

            CigaretteAddictionTestLiveData cigaretteAddictionTestLiveData = new CigaretteAddictionTestLiveData();
            cigaretteAddictionTestLiveData.setPersonId(personId);
            viewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTestLiveData);

            StressDepressionLiveData stressDepressionLiveData = new StressDepressionLiveData();
            stressDepressionLiveData.setPersonId(personId);
            viewModel.setStressDepressionLiveDataMutableLiveData(stressDepressionLiveData);

            StressDepression2qLiveData stressDepression2qLiveData = new StressDepression2qLiveData();
            stressDepression2qLiveData.setPersonId(personId);
            viewModel.setStressDepression2qLiveDataModelMutableLiveData(stressDepression2qLiveData);

            StressDepression9qLiveData stressDepression9qLiveData = new StressDepression9qLiveData();
            stressDepression9qLiveData.setPersonId(personId);
            viewModel.setStressDepression9qLiveDataModelMutableLiveData(stressDepression9qLiveData);


            SuicideAssessment8qLiveData suicideAssessment8qLiveData = new SuicideAssessment8qLiveData();
            suicideAssessment8qLiveData.setPersonId(personId);
            viewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);


            HealthRiskAssessmentLiveData healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
            healthRiskAssessmentLiveData.setPersonId(personId);
            viewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
        }
    }
    private String savePerson(){
        SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(mContext);
        if(this.personInfo.getId()==null) {
            String id = sfPersonInfoDao.insert(this.personInfo);
            this.personInfo.setId(id);
        } else {
            sfPersonInfoDao.update(this.personInfo);
        }
        List<PersonInfo> personInfos1 = sfPersonInfoDao.getSfPersonInfoById(Integer.parseInt(this.personInfo.getId()));
        System.out.println("===== Start get data by id ======");
        for(PersonInfo p: personInfos1){
            System.out.println(p.getId()+" "+p.getFname());
        }
        System.out.println("===== End get data by id ======");
        return this.personInfo.getId();

    }
    private void saveSmoker(){
        SfSmokerInfoDao sfSmokerInfoDao = new SfSmokerInfoDao(mContext);
        if(smokerInfo!=null) {
            smokerInfo.setIdcard(this.personInfo.getIdcard());
            smokerInfo.setPersonId(this.personInfo.getId());
            smokerInfo.setCreated_by("SYSTEM");
            smokerInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            if(this.smokerInfo.getId()==null) {
                String id = sfSmokerInfoDao.insert(smokerInfo);
                this.smokerInfo.setId(id);
            } else {
                sfSmokerInfoDao.update(this.smokerInfo);
            }
            SmokerInfo smokerInfo = sfSmokerInfoDao.getById(Integer.parseInt(this.smokerInfo.getId()));
            if(smokerInfo!=null){
                System.out.println("smoker:"+smokerInfo.getId()+" "+smokerInfo.getPersonId());
            }
        }
    }
    private void saveDrinking(){
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(mContext);
        if(drinkingInfo!=null) {
            drinkingInfo.setIdcard(this.personInfo.getIdcard());
            drinkingInfo.setPersonId(this.personInfo.getId());
            drinkingInfo.setCreated_by("SYSTEM");
            drinkingInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            if(this.drinkingInfo.getId()==null) {
                String id = sfDrinkingInfoDao.insert(drinkingInfo);
                this.drinkingInfo.setId(id);
            } else {
                sfDrinkingInfoDao.update(drinkingInfo);
            }
            DrinkingInfo drinkingInfo = sfDrinkingInfoDao.getById(Integer.parseInt(this.drinkingInfo.getId()));
            if(drinkingInfo!=null){
                System.out.println("drinking:"+drinkingInfo.getId()+" "+drinkingInfo.getPersonId());
            }
        }
    }
    private void saveNicotine(){
        SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(mContext);
        if(nicotineInfo!=null) {
            nicotineInfo.setIdcard(this.personInfo.getIdcard());
            nicotineInfo.setPersonId(this.personInfo.getId());
            nicotineInfo.setCreated_by("SYSTEM");
            nicotineInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            if(this.nicotineInfo.getId()==null) {
                String id = sfNicotineInfoDao.insert(nicotineInfo);
                this.nicotineInfo.setId(id);
            } else {
                sfNicotineInfoDao.update(nicotineInfo);
            }
            NicotineInfo nicotineInfo = sfNicotineInfoDao.getById(Integer.parseInt(this.nicotineInfo.getId()));
            if(nicotineInfo!=null){
                System.out.println("smoker:"+nicotineInfo.getId()+" "+nicotineInfo.getPersonId());
            }
        }
    }
    private void saveStressDepression(){
        if(stressDepressionInfo!=null) {
            SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(mContext);
            stressDepressionInfo.setPersonId(this.personInfo.getId());
            stressDepressionInfo.setIdcard(this.personInfo.getIdcard());
            if(this.stressDepressionInfo.getId()==null) {
                String id = sfStressDepressionInfoDao.insert(stressDepressionInfo);
                this.stressDepressionInfo.setId(id);
            }
            else {
                sfStressDepressionInfoDao.update(stressDepressionInfo);
            }
            StressDepressionInfo stressDepressionInfo = sfStressDepressionInfoDao.getById(Integer.parseInt(this.stressDepressionInfo.getId()));
            if (stressDepressionInfo != null) {
                System.out.println("stress depression:" + stressDepressionInfo.getId() + " " + stressDepressionInfo.getPersonId());
            }
        }
    }

    private void saveStressDepression2q(){
        if(stressDepression2qInfo!=null) {
            SfStressDepression2qInfoDao sfStressDepression2qInfoDao = new SfStressDepression2qInfoDao(mContext);
            stressDepression2qInfo.setPersonId(this.personInfo.getId());
            stressDepression2qInfo.setIdcard(personInfo.getIdcard());
            if(this.stressDepression2qInfo.getId()==null) {
                String id = sfStressDepression2qInfoDao.insert(stressDepression2qInfo);
                stressDepression2qInfo.setId(id);
            }else {
                sfStressDepression2qInfoDao.update(stressDepression2qInfo);
            }
            StressDepression2qInfo stressDepression2qInfo = sfStressDepression2qInfoDao.getById(Integer.parseInt(this.stressDepression2qInfo.getId()));
            if (stressDepression2qInfo != null) {
                System.out.println("stress depression 2q:" + stressDepression2qInfo.getId() + " " + stressDepression2qInfo.getPersonId());
            }
        }
    }
    private void saveStressDepression9q(){
        if(this.stressDepression9qInfo!=null) {
            SfStressDepression9qInfoDao sfStressDepression9qInfoDao = new SfStressDepression9qInfoDao(mContext);
            this.stressDepression9qInfo.setPersonId(this.personInfo.getId());
            this.stressDepression9qInfo.setIdcard(this.personInfo.getIdcard());
            if(stressDepression9qInfo.getId()==null) {
                String id = sfStressDepression9qInfoDao.insert(this.stressDepression9qInfo);
                this.stressDepression9qInfo.setId(id);
            }
            else {
                sfStressDepression9qInfoDao.update(this.stressDepression9qInfo);
            }
            StressDepression9qInfo stressDepression9qInfo = sfStressDepression9qInfoDao.getById(Integer.parseInt(this.stressDepression9qInfo.getId()));
            if (stressDepression9qInfo != null) {
                System.out.println("stress depression 9q:" + stressDepression9qInfo.getId() + " " + stressDepression9qInfo.getPersonId());
            }
        }
    }
    private void saveSuicideAssessment8q(){
        if(this.suicideAssessment8qInfo!=null) {
            SfSuicideAssessment8qInfoDao sfSuicideAssessment8qInfoDao = new SfSuicideAssessment8qInfoDao(mContext);
            this.suicideAssessment8qInfo.setPersonId(this.personInfo.getId());
            this.suicideAssessment8qInfo.setIdcard(this.personInfo.getIdcard());
            if(this.suicideAssessment8qInfo.getId()==null) {
                String id = sfSuicideAssessment8qInfoDao.insert(this.suicideAssessment8qInfo);
                this.suicideAssessment8qInfo.setId(id);
            }
            else {
                sfSuicideAssessment8qInfoDao.update(this.suicideAssessment8qInfo);
            }
            SuicideAssessment8qInfo suicideAssessment8qInfo = sfSuicideAssessment8qInfoDao.getById(Integer.parseInt(this.suicideAssessment8qInfo.getId()));
            if (suicideAssessment8qInfo != null) {
                System.out.println("Suicide Assessment 8q:" + suicideAssessment8qInfo.getId() + " " + suicideAssessment8qInfo.getPersonId());
            }
        }
    }
    private void saveHealthRiskAssessment(){
        if(this.healthRiskAssessmentInfo!=null) {
            SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(mContext);
            this.healthRiskAssessmentInfo.setPersonId(this.personInfo.getId());
            this.healthRiskAssessmentInfo.setIdcard(this.personInfo.getIdcard());
            if(this.healthRiskAssessmentInfo.getId()==null) {
                String id = sfHealthRiskAssessmentInfoDao.insert(this.healthRiskAssessmentInfo);
                this.healthRiskAssessmentInfo.setId(id);
            }
            else {
                sfHealthRiskAssessmentInfoDao.update(this.healthRiskAssessmentInfo);
            }
            HealthRiskAssessmentInfo healthRiskAssessmentInfo = sfHealthRiskAssessmentInfoDao.getById(Integer.parseInt(this.healthRiskAssessmentInfo.getId()));
            if (healthRiskAssessmentInfo != null) {
                System.out.println("save HealthRiskAssessment:" + healthRiskAssessmentInfo.getId() + " " + healthRiskAssessmentInfo.getPersonId());
            }
        }
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

    @Override
    public void onPersonInfo(PersonInfo data) {
        String msg = "====> "+data.getBirthday()+" "+data.getIdcard()+ " "+data.getFname()+" "+data.getLname()+" "+data.getGender();
        System.out.println(msg);
//        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_SHORT).show();
        this.personInfo = data;
    }

    @Override
    public void onSmokerInfo(SmokerInfo data) {
        this.smokerInfo = data;
        this.smokerInfo.setSmokerRegularly(data.getSmokerRegularly());
        this.smokerInfo.setSmokerGroup(data.getSmokerGroup());
        this.smokerInfo.setSmokerAssist(data.getSmokerAssist());
    }
    @Override
    public void onDrinkingInfo(DrinkingInfo data) {
        this.drinkingInfo = data;
    }
    @Override
    public void onNicotineInfo(NicotineInfo data) {
        final int[] sum = {0};
        if(this.nicotineInfo==null){
            this.nicotineInfo = data;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                nicotineInfo.getPoints().forEach(num -> sum[0] += num);
            }
            this.nicotineInfo.setSum(sum[0]);
        }
        String msg = "====> "+data.getNicotine1()
                +" "+data.getNicotine2()
                +" "+data.getNicotine3()
                +" "+data.getNicotine4()
                +" "+data.getNicotine5()
                +" "+data.getNicotine6()
                +" คะแนน: "+ sum[0]
                ;
        System.out.println(msg);
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
//        if(this.stressDepressionInfo==null){
            this.stressDepressionInfo = data;
//        }
        System.out.println(msg);
       // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
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
//        if(this.stressDepression2qInfo==null){
            this.stressDepression2qInfo = data;
//        }

        System.out.println(msg);
       // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

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
        if(this.stressDepression9qInfo==null){
            this.stressDepression9qInfo = data;
        }
        System.out.println(msg);
       // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
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
        if(this.suicideAssessment8qInfo==null){
            this.suicideAssessment8qInfo = data;
        }
        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

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
        if(this.healthRiskAssessmentInfo==null){
            this.healthRiskAssessmentInfo = data;
        }
        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAssistScoreInfo(AssistScore data) {
        String msg = "====> "+data.getNicotineScore();
        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}