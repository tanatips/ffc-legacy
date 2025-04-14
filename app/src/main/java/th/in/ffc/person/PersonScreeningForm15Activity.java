package th.in.ffc.person;



import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.FormDialogFragment;
import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.CardiovascularRiskFragment;
import th.in.ffc.app.form.screening.HealthRiskAssessmentFragment;
import th.in.ffc.app.form.screening.MainQuestionsFragment;
import th.in.ffc.app.form.screening.SharedViewModel;
import th.in.ffc.app.form.screening.StressDepressionFragment;
import th.in.ffc.app.form.screening.FagerstromNicotineFragment;
import th.in.ffc.app.form.screening.FragmentTabInfo;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SmookingFragment;
import th.in.ffc.app.form.screening.StressDepression2qFragment;
import th.in.ffc.app.form.screening.StressDepression9qFragment;
import th.in.ffc.app.form.screening.SuicideAssessment8qFragment;
import th.in.ffc.app.form.screening.adapter.ScreeningExpandableListAdapter;
import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression9qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepressionInfoDao;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.datalive.CardiovascularRiskLiveData;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.datalive.DrugsLiveData;
import th.in.ffc.app.form.screening.datalive.HealthRiskAssessmentLiveData;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepressionLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
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
    private List<DrugsInfo> drugsOneInfos;
    private List<DrugsInfo> drugsTwoInfos;

    private List<DrugsInfo> drugsThreeInfos;

    private List<DrugsInfo> drugsFourInfos;
    private List<DrugsInfo> drugsFiveInfos;
    private List<DrugsInfo> drugsSixInfos;
    private List<DrugsInfo> drugsSevenInfos;
    private List<DrugsInfo> drugsEightInfos;
    CardiovascularRiskInfo  cardiovascularRiskInfo;
    private ExpandableListView expandableListView;
    private ScreeningExpandableListAdapter expandableListAdapter;
    private List<String> categoryList; // หัวข้อหลัก
    private Map<String, List<String>> subcategoryMap; // หัวข้อย่อย
    private Map<String, Fragment> fragmentMap; // Fragment สำหรับแต่ละหัวข้อย่อย



    private AssistScore assistScoreInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        // เพิ่มโค้ดสำหรับการทำ collapse สำหรับ PersonInfoFragment
        View personInfoHeader = findViewById(R.id.personInfoHeader);
        final FrameLayout personInfoContainer = findViewById(R.id.personInfoContainer);
        final ImageView personInfoExpandIcon = findViewById(R.id.personInfoExpandIcon);

        // ตั้งค่าการคลิกเพื่อขยาย/ย่อ
        personInfoHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // สลับสถานะการแสดงผล
                if (personInfoContainer.getVisibility() == View.VISIBLE) {
                    // ย่อ
                    personInfoContainer.setVisibility(View.GONE);
                    personInfoExpandIcon.setImageResource(R.drawable.ic_expand_more_black);
                } else {
                    // ขยาย
                    personInfoContainer.setVisibility(View.VISIBLE);
                    personInfoExpandIcon.setImageResource(R.drawable.ic_expand_less_black);
                }
            }
        });

        // เตรียมข้อมูล
        prepareListData();

        // ค้นหาและกำหนดค่า ExpandableListView
        expandableListView = findViewById(R.id.expandableListView);
        expandableListAdapter = new ScreeningExpandableListAdapter(this, categoryList, subcategoryMap);
        expandableListView.setAdapter(expandableListAdapter);


        // เมื่อคลิกที่รายการย่อย
//        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
//            String category = categoryList.get(groupPosition);
//            String subcategory = subcategoryMap.get(category).get(childPosition);
//
//            // แสดง Fragment ที่เกี่ยวข้อง
//            Fragment fragment = fragmentMap.get(subcategory);
//            if (fragment != null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragmentContainer, fragment)
//                        .commit();
//            }
//
//            return true;
//        });
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String category = categoryList.get(groupPosition);
            String subcategory = subcategoryMap.get(category).get(childPosition);

            // สร้างและแสดง Dialog แทนการใช้ Fragment
            showFormDialog(subcategory);

            return true;
        });

        // เพิ่มโค้ดนี้ในเมธอด onCreate หลังจากตั้งค่า OnChildClickListener
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                // ปรับความสูงตามเนื้อหาเมื่อขยายกลุ่ม
                adjustExpandableListViewHeight();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                // ปรับความสูงตามเนื้อหาเมื่อยุบกลุ่ม
                adjustExpandableListViewHeight();
            }
        });

        // เพิ่มการกำหนดให้ขยายรายการไว้ที่นี่ เพื่อให้เห็นทุกหมวดหมู่แบบอัตโนมัติเมื่อเปิด
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }

//        tabLayout = findViewById(R.id.tabLayout);
//        viewPager = findViewById(R.id.viewPager);
        btnOk = findViewById(R.id.btnOK);
        btnCancel = findViewById(R.id.btnCancel);
        mContext = getBaseContext();
//        ArrayList<FragmentTabInfo> fragmentTabInfos = new ArrayList<>();
//        fragmentTabInfos.add(new FragmentTabInfo(new MainQuestionsFragment(),"แบบคัดกรองการใช้สารเสพติด"));
//        fragmentTabInfos.add(new FragmentTabInfo(new AssistScoreFragment(),"สรุปคะแนนแบบคัดกรอง ASSIST"));
//        fragmentTabInfos.add(new FragmentTabInfo(new SmookingFragment(),"คัดกรองความเสี่ยงจากการสูบบุหรี่"));
//        fragmentTabInfos.add(new FragmentTabInfo(new FagerstromNicotineFragment(),"แบบทดสอบการติดบุหรี่"));
//        fragmentTabInfos.add(new FragmentTabInfo(new AlcoholFragment(),"คัดกรองความเสี่ยงจากการดื่มสุรา"));
//        fragmentTabInfos.add(new FragmentTabInfo(new StressDepressionFragment(),"ประเมินภาวะเครียด-ซึมเศร้า(ST 5)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression2qFragment(),"คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new StressDepression9qFragment(),"คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new SuicideAssessment8qFragment(),"การประเมินการฆ่าตัวตายด้วย 8 คําถาม (8Q)"));
//        fragmentTabInfos.add(new FragmentTabInfo(new HealthRiskAssessmentFragment(),"แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน"));
//        fragmentTabInfos.add(new FragmentTabInfo(new CardiovascularRiskFragment() ,"คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด"));
//
//        viewPagerAdapter = new ViewPagerAdapter(this,fragmentTabInfos);
//
//        viewPager.setAdapter(viewPagerAdapter);
//        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
//            tab.setText(fragmentTabInfos.get(position).getTabTitle());
//
//        }).attach();
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(),viewPager,viewPagerAdapter));
//            }
//        });
//        viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(),viewPager,viewPagerAdapter));

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
                        saveCardiovascularRisk();
                        saveDrugsOne();
                        saveDrugsTwo();
                        saveDrugsThree();
                        saveDrugsFour();
                        saveDrugsFive();
                        saveDrugsSix();
                        saveDrugsSeven();
                        saveDrugsEight();

                        // เพิ่มการตรวจสอบข้อมูลหลังบันทึกเสร็จ
                        checkExistingData(personInfo.getId());

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
    // เพิ่มเมธอดใหม่สำหรับแสดง Dialog
    private void showFormDialog(String formName) {
        // หา Fragment ที่เกี่ยวข้อง
        Fragment fragment = fragmentMap.get(formName);

        if (fragment != null) {
            // สร้าง DialogFragment ใหม่ที่ใช้ Fragment นี้
            FormDialogFragment dialogFragment = FormDialogFragment.newInstance(formName, fragment);
            dialogFragment.show(getSupportFragmentManager(), "FormDialog");
        }
    }
    private void adjustExpandableListViewHeight() {
        ViewGroup.LayoutParams params = expandableListView.getLayoutParams();
        int totalHeight = 0;
        int count = expandableListAdapter.getGroupCount();

        for (int i = 0; i < count; i++) {
            View groupView = expandableListAdapter.getGroupView(i, false, null, expandableListView);
            groupView.measure(0, 0);
            totalHeight += groupView.getMeasuredHeight();

            if (expandableListView.isGroupExpanded(i)) {
                int childCount = expandableListAdapter.getChildrenCount(i);
                for (int j = 0; j < childCount; j++) {
                    View childView = expandableListAdapter.getChildView(i, j, false, null, expandableListView);
                    childView.measure(0, 0);
                    totalHeight += childView.getMeasuredHeight();
                }
            }
        }

        params.height = totalHeight + (expandableListView.getDividerHeight() * count);
        expandableListView.setLayoutParams(params);
        expandableListView.requestLayout();
    }
    private void getPersonInfoDetail(){

        String personId = getIntent().getStringExtra("person_id");
        if(personId!=null) {
            // ใน Activity
            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            QuestionsStateViewModel questionsStateViewModel =  new ViewModelProvider(this).get(QuestionsStateViewModel.class);
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

            CardiovascularRiskLiveData cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
            cardiovascularRiskLiveData.setPersonId(personId);
            viewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);

            DrugsLiveData drugsLiveData = new DrugsLiveData();
            drugsLiveData.setPersonId(personId);
            viewModel.setDrugsLiveDataMutableLiveData(drugsLiveData);

            // ตรวจสอบข้อมูลที่มีอยู่แล้วและอัปเดตสถานะ
            checkExistingData(personId);
        }
    }
    private void checkExistingData(String personId) {
        // สร้าง Map เพื่อเก็บสถานะการกรอกข้อมูล
        Map<String, Boolean> formStatus = new HashMap<>();
        Integer iPersonId = Integer.valueOf(personId);
        // ตรวจสอบข้อมูลการสูบบุหรี่
        SfSmokerInfoDao sfSmokerInfoDao = new SfSmokerInfoDao(mContext);
        List<SmokerInfo> smokers = sfSmokerInfoDao.getByPersonId(iPersonId);
        formStatus.put("คัดกรองความเสี่ยงจากการสูบบุหรี่", !smokers.isEmpty());

        // ตรวจสอบข้อมูลการดื่มสุรา
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(mContext);
        List<DrinkingInfo> drinkings = sfDrinkingInfoDao.getByPersonId(iPersonId);
        formStatus.put("คัดกรองความเสี่ยงจากการดื่มสุรา", !drinkings.isEmpty());

        // ตรวจสอบข้อมูลการติดนิโคติน
        SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(mContext);
        List<NicotineInfo> nicotines = sfNicotineInfoDao.getByPersonId(iPersonId);
        formStatus.put("แบบทดสอบการติดบุหรี่", !nicotines.isEmpty());

        // ตรวจสอบข้อมูลภาวะเครียด-ซึมเศร้า (ST5)
        SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(mContext);
        List<StressDepressionInfo> stressDepressions = sfStressDepressionInfoDao.getByPersonId(iPersonId);
        formStatus.put("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", !stressDepressions.isEmpty());

        // ตรวจสอบข้อมูลคัดกรองโรคซึมเศร้า 2Q
        SfStressDepression2qInfoDao sfStressDepression2qInfoDao = new SfStressDepression2qInfoDao(mContext);
        List<StressDepression2qInfo> stressDepression2qs = sfStressDepression2qInfoDao.getByPersonId(iPersonId);
        formStatus.put("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", !stressDepression2qs.isEmpty());

        // ตรวจสอบข้อมูลคัดกรองโรคซึมเศร้า 9Q
        SfStressDepression9qInfoDao sfStressDepression9qInfoDao = new SfStressDepression9qInfoDao(mContext);
        List<StressDepression9qInfo> stressDepression9qs = sfStressDepression9qInfoDao.getByPersonId(iPersonId);
        formStatus.put("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", !stressDepression9qs.isEmpty());

        // ตรวจสอบข้อมูลการประเมินฆ่าตัวตาย 8Q
        SfSuicideAssessment8qInfoDao sfSuicideAssessment8qInfoDao = new SfSuicideAssessment8qInfoDao(mContext);
        List<SuicideAssessment8qInfo> suicideAssessment8qs = sfSuicideAssessment8qInfoDao.getByPersonId(iPersonId);
        formStatus.put("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", !suicideAssessment8qs.isEmpty());

        // ตรวจสอบข้อมูลประเมินความเสี่ยงโรคเบาหวาน
        SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(mContext);
        List<HealthRiskAssessmentInfo> healthRisks = sfHealthRiskAssessmentInfoDao.getByPersonId(iPersonId);
        formStatus.put("แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน", !healthRisks.isEmpty());

        // ตรวจสอบข้อมูลคัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด
        SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(mContext);
        List<CardiovascularRiskInfo> cardiovascularRisks = sfCardiovascularRiskInfoDao.getByPersonId(iPersonId);
        formStatus.put("คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด", !cardiovascularRisks.isEmpty());

        // ตรวจสอบข้อมูลการใช้สารเสพติด (ASSIST)
        SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
        List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(iPersonId);
        formStatus.put("แบบคัดกรองการใช้สารเสพติด", !drugs.isEmpty());

        // คำนวณคะแนน ASSIST
        boolean hasAssistScores = false;
        // ตรวจสอบว่าทุกหมวดมีข้อมูลแล้วหรือไม่
//        if (drugs != null && !drugs.isEmpty()) {
//            // ตรวจสอบโดยละเอียด (ถ้าต้องการ)
//            // hasAssistScores = ...
//        }
        formStatus.put("สรุปคะแนนแบบคัดกรอง ASSIST", hasAssistScores);

        // อัปเดตสถานะในไอคอน
        if (expandableListAdapter != null) {
            ((ScreeningExpandableListAdapter) expandableListAdapter).updateAllCompletionStatus(formStatus);
        }
    }

    private void prepareListData() {
        categoryList = new ArrayList<>();
        subcategoryMap = new HashMap<>();
        fragmentMap = new HashMap<>();

        // เพิ่มหมวดหมู่
        categoryList.add("การคัดกรองสารเสพติด");
        categoryList.add("ภาวะเครียด-ซึมเศร้า");
        categoryList.add("ความเสี่ยงด้านสุขภาพ");

        // 1. หมวดหมู่ การคัดกรองสารเสพติด
        List<String> addictionScreening = new ArrayList<>();
        addictionScreening.add("แบบคัดกรองการใช้สารเสพติด");
        addictionScreening.add("สรุปคะแนนแบบคัดกรอง ASSIST");
        addictionScreening.add("คัดกรองความเสี่ยงจากการสูบบุหรี่");
        addictionScreening.add("แบบทดสอบการติดบุหรี่");
        addictionScreening.add("คัดกรองความเสี่ยงจากการดื่มสุรา");
        subcategoryMap.put("การคัดกรองสารเสพติด", addictionScreening);

        // 2. หมวดหมู่ ภาวะเครียด-ซึมเศร้า
        List<String> mentalHealth = new ArrayList<>();
        mentalHealth.add("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)");
        mentalHealth.add("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)");
        mentalHealth.add("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)");
        mentalHealth.add("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)");
        subcategoryMap.put("ภาวะเครียด-ซึมเศร้า", mentalHealth);

        // 3. หมวดหมู่ ความเสี่ยงด้านสุขภาพ
        List<String> healthRisks = new ArrayList<>();
        healthRisks.add("แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน");
        healthRisks.add("คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด");
        subcategoryMap.put("ความเสี่ยงด้านสุขภาพ", healthRisks);

        // เพิ่ม Fragment ที่เกี่ยวข้องทั้งหมด
        // 1. การคัดกรองสารเสพติด
        fragmentMap.put("แบบคัดกรองการใช้สารเสพติด", new MainQuestionsFragment());
        fragmentMap.put("สรุปคะแนนแบบคัดกรอง ASSIST", new AssistScoreFragment());
        fragmentMap.put("คัดกรองความเสี่ยงจากการสูบบุหรี่", new SmookingFragment());
        fragmentMap.put("แบบทดสอบการติดบุหรี่", new FagerstromNicotineFragment());
        fragmentMap.put("คัดกรองความเสี่ยงจากการดื่มสุรา", new AlcoholFragment());

        // 2. ภาวะเครียด-ซึมเศร้า
        fragmentMap.put("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", new StressDepressionFragment());
        fragmentMap.put("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", new StressDepression2qFragment());
        fragmentMap.put("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", new StressDepression9qFragment());
        fragmentMap.put("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", new SuicideAssessment8qFragment());

        // 3. ความเสี่ยงด้านสุขภาพ
        fragmentMap.put("แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน", new HealthRiskAssessmentFragment());
        fragmentMap.put("คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด", new CardiovascularRiskFragment());

        // *** ลบบรรทัดเหล่านี้ออก เพราะจะทำใน onCreate แทน ***
        // expandableListView = findViewById(R.id.expandableListView);
        // expandableListAdapter = new ScreeningExpandableListAdapter(this, categoryList, subcategoryMap);
        // expandableListView.setAdapter(expandableListAdapter);
    }
    public void updateFormStatus(String formName, boolean status) {
        if (expandableListAdapter != null) {
            ((ScreeningExpandableListAdapter) expandableListAdapter).updateCompletionStatus(formName, status);
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
                // อัปเดตสถานะหลังบันทึกข้อมูลสำเร็จ
                updateFormStatus("คัดกรองความเสี่ยงจากการสูบบุหรี่", true);
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
    private void saveDrugsOne() {
        if(this.drugsOneInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsOneInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
               if (drugsInfo.getId() == null) {
                   drugsInfo.setCreatedBy("SYSTEM");
                   drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                   // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                   String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsTwo() {
        if(this.drugsTwoInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsTwoInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsThree() {
        if(this.drugsThreeInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsThreeInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsFour() {
        if(this.drugsFourInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsFourInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsFive() {
        if(this.drugsFiveInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsFiveInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsSix() {
        if(this.drugsSixInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsSixInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsSeven() {
        if(this.drugsSevenInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsSevenInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }

    private void saveDrugsEight() {
        if(this.drugsEightInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsEightInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getId() == null) {
                    drugsInfo.setCreatedBy("SYSTEM");
                    drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    // ตรวจสอบว่าเป็นการบันทึกใหม่หรืออัพเดต
                    String id = sfDrugsDao.insert(drugsInfo);
                    drugsInfo.setId(id);
                } else {
                    // กำหนดค่าสำหรับการอัพเดต
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    sfDrugsDao.update(drugsInfo);
                }

                // Debug log
                List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.parseInt(drugsInfo.getId()));
                if (drugs != null && !drugs.isEmpty()) {
                    System.out.println("drugs:" + drugsInfo.getId() + " " + drugsInfo.getPersonInfoId());
                }
            }
        }
    }
    private void saveCardiovascularRisk() {
        if (this.cardiovascularRiskInfo != null) {
            // เพิ่ม import สำหรับ SfCardiovascularRiskInfoDao
            // import th.in.ffc.app.form.screening.dao.SfCardiovascularRiskInfoDao;

            SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(mContext);
            this.cardiovascularRiskInfo.setPersonId(this.personInfo.getId());
            this.cardiovascularRiskInfo.setIdcard(this.personInfo.getIdcard());
            this.cardiovascularRiskInfo.setCreated_by("SYSTEM");
            this.cardiovascularRiskInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            if (this.cardiovascularRiskInfo.getId() == null) {
                // กรณีบันทึกใหม่
                String id = sfCardiovascularRiskInfoDao.insert(this.cardiovascularRiskInfo);
                this.cardiovascularRiskInfo.setId(id);
            } else {
                // กรณีอัพเดต
                this.cardiovascularRiskInfo.setUpdated_by("SYSTEM");
                this.cardiovascularRiskInfo.setUpdated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                sfCardiovascularRiskInfoDao.update(this.cardiovascularRiskInfo);
            }

            CardiovascularRiskInfo savedInfo = sfCardiovascularRiskInfoDao.getById(Integer.parseInt(this.cardiovascularRiskInfo.getId()));
            if (savedInfo != null) {
                System.out.println("Cardiovascular Risk: " + savedInfo.getId() + " " + savedInfo.getPersonId());
            }
        }
    }
//    private void adjustViewPagerHeight(int position, ViewPager2 viewPager, ViewPagerAdapter adapter){
//        Fragment fragment = adapter.getFragmentAt(position);
//        if(fragment != null && fragment.getView() != null){
//            fragment.getView().post(() -> {
//                int width = View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY);
//                int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//
//                fragment.getView().measure(width, height);
//                int measuredHeight = fragment.getView().getMeasuredHeight();
//                ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
//                layoutParams.height = measuredHeight;
//                viewPager.setLayoutParams(layoutParams);
//
//            });
//        }
//    }
    private void adjustViewPagerHeight(int position, ViewPager2 viewPager, ViewPagerAdapter adapter) {
        Fragment fragment = adapter.getFragmentAt(position);
        if (fragment != null && fragment.getView() != null) {
            // ต้องใช้ Handler และ delay เล็กน้อยเพื่อให้แน่ใจว่า View ได้ถูกวาดแล้ว
            new Handler().postDelayed(() -> {
                if (fragment.isAdded()) {
                    View view = fragment.getView();
                    if (view != null) {
                        view.measure(
                                View.MeasureSpec.makeMeasureSpec(viewPager.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        );

                        // กำหนดความสูงขั้นต่ำเพื่อป้องกันการคำนวณที่ผิดพลาด
                        int minimumHeight = 1000; // ปรับตามความเหมาะสม
                        int measuredHeight = Math.max(view.getMeasuredHeight(), minimumHeight);

                        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
                        layoutParams.height = measuredHeight;
                        viewPager.setLayoutParams(layoutParams);
                        viewPager.requestLayout();
                    }
                }
            }, 300); // delay 300ms เพื่อให้ View ได้ render
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
    private void displayData(List<DrugsInfo> data){
        for (DrugsInfo drugsInfo : data) {
            System.out.println("ID: " + drugsInfo.getId());
            System.out.println("Person Info ID: " + drugsInfo.getPersonInfoId());
            System.out.println("Question: " + drugsInfo.getQuestion());
            System.out.println("Subquestion: " + drugsInfo.getSubquestion());
            System.out.println("Other Drugs: " + drugsInfo.getOtherDrugs());
            System.out.println("Answer: " + drugsInfo.getAnswer());
            System.out.println("Created By: " + drugsInfo.getCreatedBy());
            System.out.println("Created Date: " + drugsInfo.getCreatedDate());
            System.out.println("Updated By: " + drugsInfo.getUpdatedBy());
            System.out.println("Updated Date: " + drugsInfo.getUpdatedDate());
            System.out.println("ID Card: " + drugsInfo.getIdcard());
            System.out.println("===================================");
        }
    }
    @Override
    public void onDrugsOneInfo(List<DrugsInfo> data) {
        this.drugsOneInfos = data;
        displayData(data);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsTwoInfo(List<DrugsInfo> data) {
        this.drugsTwoInfos = data;
//        displayData(data);
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsThreeInfo(List<DrugsInfo> data) {
        this.drugsThreeInfos = data;
//        displayData(data);
    }

    @Override
    public void onDrugsFourInfo(List<DrugsInfo> data) {
        this.drugsFourInfos = data;
//        displayData(data);
    }

    @Override
    public void onDrugsFiveInfo(List<DrugsInfo> data) {
        this.drugsFiveInfos = data;
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsSixInfo(List<DrugsInfo> data) {
        this.drugsSixInfos = data;
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsSevenInfo(List<DrugsInfo> data) {
        this.drugsSevenInfos = data;
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsEightInfo(List<DrugsInfo> data) {
        this.drugsEightInfos = data;
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardiovascularRiskInfo(CardiovascularRiskInfo data) {
        this.cardiovascularRiskInfo = data;
        String msg = "====> "+data;
        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAssistScoreInfo(AssistScore data) {
         this.assistScoreInfo = data;

//        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFormSaved(String formName, Object formData) {
        // จัดการข้อมูลที่ส่งกลับมาตามประเภท
        if (formData instanceof SmokerInfo) {
            smokerInfo = (SmokerInfo) formData;
            saveSmoker();
        } else if (formData instanceof DrinkingInfo) {
            drinkingInfo = (DrinkingInfo) formData;
            saveDrinking();
        } else if (formData instanceof StressDepressionInfo) {
            stressDepressionInfo = (StressDepressionInfo) formData;
            saveStressDepression();
        }
        else if (formData instanceof SuicideAssessment8qInfo) {
            suicideAssessment8qInfo = (SuicideAssessment8qInfo) formData;
            saveSuicideAssessment8q();
        }
        else if (formData instanceof HealthRiskAssessmentInfo) {
            healthRiskAssessmentInfo = (HealthRiskAssessmentInfo) formData;
            saveHealthRiskAssessment();
        }
        else if (formData instanceof NicotineInfo) {
            nicotineInfo = (NicotineInfo) formData;
            saveNicotine();
        }
        else if (formData instanceof StressDepression2qInfo) {
            stressDepression2qInfo = (StressDepression2qInfo) formData;
            saveStressDepression2q();
        }
        else if (formData instanceof StressDepression9qInfo) {
            stressDepression9qInfo = (StressDepression9qInfo) formData;
            saveStressDepression9q();
        }
        else if (formData instanceof CardiovascularRiskInfo) {
            cardiovascularRiskInfo = (CardiovascularRiskInfo) formData;
            saveCardiovascularRisk();
        }
        else if (formData instanceof AssistScore) {
            assistScoreInfo = (AssistScore) formData;
            // บันทึกข้อมูล AssistScore
            //saveAssistScore();
        }

        // และอื่นๆ ตามประเภทข้อมูล...

        // อัปเดตสถานะการกรอกข้อมูล
        updateFormStatus(formName, true);

        // แจ้งให้ผู้ใช้ทราบ
        Toast.makeText(this, "บันทึกข้อมูล " + formName + " แล้ว", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    public void refreshViewPager() {
        if (viewPager != null) {
            viewPager.post(() -> adjustViewPagerHeight(viewPager.getCurrentItem(), viewPager, viewPagerAdapter));
        }
    }
}