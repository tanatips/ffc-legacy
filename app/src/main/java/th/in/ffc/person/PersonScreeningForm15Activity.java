package th.in.ffc.person;



import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.ftsafe.Utility;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.app.form.FormDialogFragment;
import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.CardiovascularRiskFragment;
import th.in.ffc.app.form.screening.CounselingSignFragment;
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
import th.in.ffc.app.form.screening.dao.CounselingSignatureDao;
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
import th.in.ffc.app.form.screening.dao.SfTokenDao;
import th.in.ffc.app.form.screening.datalive.CardiovascularRiskLiveData;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.datalive.CounselingLiveData;
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
import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonData;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.app.form.screening.model.VisitDiagInfo;
import th.in.ffc.dao.PersonDao;
import th.in.ffc.dao.PersonVillageDao;
import th.in.ffc.dao.VisitDao;
import th.in.ffc.dao.VisitDiagDao;
import th.in.ffc.model.Person;
import th.in.ffc.provider.CounselingSignatureProvider;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.AgeCalculator;
import th.in.ffc.util.DateConverter;
import th.in.ffc.util.GenerateSeq;
import th.in.ffc.util.ViewPagerAdapter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import org.slf4j.helpers.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PersonScreeningForm15Activity extends AppCompatActivity implements OnDataPass {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private Button btnOk, btnCancel, btnReCreateTable;

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
    CardiovascularRiskInfo cardiovascularRiskInfo;
    private ExpandableListView expandableListView;
    private ScreeningExpandableListAdapter expandableListAdapter;
    private List<String> categoryList; // หัวข้อหลัก
    private Map<String, List<String>> subcategoryMap; // หัวข้อย่อย
    private Map<String, Fragment> fragmentMap; // Fragment สำหรับแต่ละหัวข้อย่อย

    private CounselingInfo counselingInfo;

    private AssistScore assistScoreInfo;

    private SharedViewModel sharedViewModel;

    private boolean isUpdatingPersonData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        // เพิ่มโค้ดสำหรับการทำ collapse สำหรับ PersonInfoFragment
        View personInfoHeader = findViewById(R.id.personInfoHeader);
        final FrameLayout personInfoContainer = findViewById(R.id.personInfoContainer);
        final ImageView personInfoExpandIcon = findViewById(R.id.personInfoExpandIcon);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

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
        btnReCreateTable = findViewById(R.id.btnReCreateTable);
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
                    if (personInfo.getId() != null) {
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
                        saveCounseling();
                        saveVisit();
                        saveVisitDiag();
                        // เพิ่มการตรวจสอบข้อมูลหลังบันทึกเสร็จ
                        checkExistingData(personInfo.getId());

                        // อัพเดต PersonData หลังจากบันทึกข้อมูลทั้งหมดแล้ว
                        // updatePersonDataFromCurrentInfo();

                        Toast.makeText(getBaseContext(), "บันทึกข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
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
        btnReCreateTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScreeningFormProvider.ReCreateTable(mContext);
                CounselingSignatureProvider.ReCreateTable(mContext);
                Toast.makeText(getBaseContext(), "รีเซ็ตข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
            }
        });
//        SfTokenDao tokenDao = new SfTokenDao(getBaseContext());
//        tokenDao.insertDefaultTokenIfEmpty();
        getPersonInfoDetail();
        if (this.personInfo != null) {
            if (this.personInfo.getSend_to_claim().equals(1)) {
                btnOk.setEnabled(false);
            } else {
                btnOk.setEnabled(true);
            }
        }
    }

    public void updatePersonDataAfterFormSave() {
        updatePersonDataFromCurrentInfo();
    }

    // เพิ่มเมธอดใหม่สำหรับแสดง Dialog
    private void showFormDialog(String formName) {
        Fragment fragment = fragmentMap.get(formName);
        if (fragment != null) {
            if (this.personInfo != null) {
                FormDialogFragment dialogFragment = FormDialogFragment.newInstance(formName, fragment, this.personInfo.getSend_to_claim());
                dialogFragment.show(getSupportFragmentManager(), "FormDialog");
            } else {
                Toast.makeText(this, "โปรดกรอกข้อมูลผู้ส่วนตัวก่อนทำแบบคัดกรอง", Toast.LENGTH_SHORT).show();
            }

            // หน่วงเวลาเพิ่มขึ้นเพื่อให้ Dialog แสดงก่อน
//            new Handler().postDelayed(() -> {
//                Dialog dialog = dialogFragment.getDialog();
//                if (dialog != null) {
//                    View focusView = dialog.getCurrentFocus();
//                    if (focusView instanceof EditText) {
//                        focusView.requestFocus();
//                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                        if (imm != null) {
//                            imm.showSoftInput(focusView, InputMethodManager.SHOW_IMPLICIT);
//                        }
//                    }
//                }
//            }, 700); // ลองเพิ่มเวลารอเป็น 700ms
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

    private void getPersonInfoDetail() {

        String personId = getIntent().getStringExtra("person_id");
        String visitId = getIntent().getStringExtra("visit_id");
        if (personId != null) {
            // ใน Activity
            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            QuestionsStateViewModel questionsStateViewModel = new ViewModelProvider(this).get(QuestionsStateViewModel.class);
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

            CounselingLiveData counselingLiveData = new CounselingLiveData();
            counselingLiveData.setPersonId(personId);
            counselingLiveData.setVisitId(visitId);

            viewModel.setCounselingLiveData(counselingLiveData);

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

        // ตรวจสอบความครบถ้วนของข้อมูล
        boolean suicide8qComplete = false;
        if (!suicideAssessment8qs.isEmpty()) {
            SuicideAssessment8qInfo data = suicideAssessment8qs.get(0);
            suicide8qComplete = isSuicideAssessment8qDataComplete(data);
        }
        formStatus.put("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", suicide8qComplete);


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
        formStatus.put("สรุปคะแนนแบบคัดกรอง ASSIST", !drugs.isEmpty());

        // ตรวจสอบข้อมูลการให้คำปรึกษาและลายเซ็น
        CounselingSignatureDao counselingDao = new CounselingSignatureDao(mContext);
        List<CounselingInfo> counselingInfos = counselingDao.getCounselingByPersonId(personId);
        formStatus.put("ให้คำปรึกษาและแนะนำ", !counselingInfos.isEmpty());

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
        categoryList.add("สรุปผลการคัดกรอง");

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

        // 4. หมวดหมู่ สรุปการคัดกรอง
        List<String> sfSummary = new ArrayList<>();
        sfSummary.add("ให้คำปรึกษาและแนะนำ");

        subcategoryMap.put("สรุปผลการคัดกรอง", sfSummary);

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

        // 4. สรุปการคัดกรอง
        fragmentMap.put("ให้คำปรึกษาและแนะนำ", new CounselingSignFragment());

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

    private String savePerson() {
        SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(mContext);
        UserSessionManager userSessionManager = new UserSessionManager(mContext);
        if (this.personInfo.getId() == null) {

            this.personInfo.setCreated_by(userSessionManager.getUser());
            this.personInfo.setCreated_date(DateConverter.getCurrentWesternDateTime());
            String id = sfPersonInfoDao.insert(this.personInfo);
            this.personInfo.setId(id);
        } else {
            this.personInfo.setUpdated_date(DateConverter.getCurrentWesternDateTime());
            this.personInfo.setUpdated_by(userSessionManager.getUser());
            sfPersonInfoDao.update(this.personInfo);

        }
        List<PersonInfo> personInfos1 = sfPersonInfoDao.getSfPersonInfoById(Integer.parseInt(this.personInfo.getId()));
        System.out.println("===== Start get data by id ======");
        for (PersonInfo p : personInfos1) {
            System.out.println(p.getId() + " " + p.getFname());
        }
        System.out.println("===== End get data by id ======");
        return this.personInfo.getId();

    }

    private void saveSmoker() {
        SfSmokerInfoDao sfSmokerInfoDao = new SfSmokerInfoDao(mContext);
        if (smokerInfo != null) {
            smokerInfo.setIdcard(this.personInfo.getIdcard());
            smokerInfo.setPersonId(this.personInfo.getId());
            smokerInfo.setCreated_by("SYSTEM");
            smokerInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            if (this.smokerInfo.getId() == null) {
                String id = sfSmokerInfoDao.insert(smokerInfo);
                this.smokerInfo.setId(id);
            } else {
                sfSmokerInfoDao.update(this.smokerInfo);
            }
            SmokerInfo smokerInfo = sfSmokerInfoDao.getById(Integer.parseInt(this.smokerInfo.getId()));
            if (smokerInfo != null) {
                System.out.println("smoker:" + smokerInfo.getId() + " " + smokerInfo.getPersonId());
                // อัปเดตสถานะหลังบันทึกข้อมูลสำเร็จ
                updateFormStatus("คัดกรองความเสี่ยงจากการสูบบุหรี่", true);
            }
        }
    }

    private void saveDrinking() {
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(mContext);
        if (drinkingInfo != null) {
            drinkingInfo.setIdcard(this.personInfo.getIdcard());
            drinkingInfo.setPersonId(this.personInfo.getId());
            drinkingInfo.setCreated_by("SYSTEM");
            drinkingInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            if (this.drinkingInfo.getId() == null) {
                String id = sfDrinkingInfoDao.insert(drinkingInfo);
                this.drinkingInfo.setId(id);
            } else {
                sfDrinkingInfoDao.update(drinkingInfo);
            }
            DrinkingInfo drinkingInfo = sfDrinkingInfoDao.getById(Integer.parseInt(this.drinkingInfo.getId()));
            if (drinkingInfo != null) {
                System.out.println("drinking:" + drinkingInfo.getId() + " " + drinkingInfo.getPersonId());
            }
        }
    }

    private void saveNicotine() {
        SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(mContext);
        if (nicotineInfo != null) {
            nicotineInfo.setIdcard(this.personInfo.getIdcard());
            nicotineInfo.setPersonId(this.personInfo.getId());
            nicotineInfo.setCreated_by("SYSTEM");
            nicotineInfo.setCreated_date(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            if (this.nicotineInfo.getId() == null) {
                String id = sfNicotineInfoDao.insert(nicotineInfo);
                this.nicotineInfo.setId(id);
            } else {
                sfNicotineInfoDao.update(nicotineInfo);
            }
            NicotineInfo nicotineInfo = sfNicotineInfoDao.getById(Integer.parseInt(this.nicotineInfo.getId()));
            if (nicotineInfo != null) {
                System.out.println("smoker:" + nicotineInfo.getId() + " " + nicotineInfo.getPersonId());
            }
        }
    }

    private void saveStressDepression() {
        if (stressDepressionInfo != null) {
            SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(mContext);
            stressDepressionInfo.setPersonId(this.personInfo.getId());
            stressDepressionInfo.setIdcard(this.personInfo.getIdcard());

            if (this.stressDepressionInfo.getId() == null) {
                String id = sfStressDepressionInfoDao.insert(stressDepressionInfo);
                this.stressDepressionInfo.setId(id);
            } else {
                sfStressDepressionInfoDao.update(stressDepressionInfo);
            }

            StressDepressionInfo stressDepressionInfo = sfStressDepressionInfoDao.getById(Integer.parseInt(this.stressDepressionInfo.getId()));
            if (stressDepressionInfo != null) {
                System.out.println("stress depression:" + stressDepressionInfo.getId() + " " + stressDepressionInfo.getPersonId());
                // อัปเดตสถานะหลังบันทึกข้อมูลสำเร็จ
                updateFormStatus("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", true);
            }
        }
    }

    // เพิ่มเมธอดใหม่สำหรับตรวจสอบสถานะ StressDepressionFragment
    public void updateStressDepressionStatus() {
        // ค้นหา Fragment จากหน้าจอปัจจุบัน
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof StressDepressionFragment) {
                StressDepressionFragment stressFragment = (StressDepressionFragment) fragment;
                boolean isComplete = stressFragment.isFormComplete();

                // อัปเดตสถานะใน expandableListAdapter
                if (expandableListAdapter != null) {
                    expandableListAdapter.updateCompletionStatus("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", isComplete);
                }

                break;
            }
        }
    }

    // ปรับปรุงเมธอด saveStressDepression2q() เพื่อเพิ่มการอัปเดตสถานะ
    private void saveStressDepression2q() {
        if (stressDepression2qInfo != null) {
            SfStressDepression2qInfoDao sfStressDepression2qInfoDao = new SfStressDepression2qInfoDao(mContext);
            stressDepression2qInfo.setPersonId(this.personInfo.getId());
            stressDepression2qInfo.setIdcard(personInfo.getIdcard());
            if (this.stressDepression2qInfo.getId() == null) {
                String id = sfStressDepression2qInfoDao.insert(stressDepression2qInfo);
                stressDepression2qInfo.setId(id);
            } else {
                sfStressDepression2qInfoDao.update(stressDepression2qInfo);
            }
            StressDepression2qInfo stressDepression2qInfo = sfStressDepression2qInfoDao.getById(Integer.parseInt(this.stressDepression2qInfo.getId()));
            if (stressDepression2qInfo != null) {
                System.out.println("stress depression 2q:" + stressDepression2qInfo.getId() + " " + stressDepression2qInfo.getPersonId());
                // อัปเดตสถานะหลังบันทึกข้อมูลสำเร็จ
                updateFormStatus("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", true);
            }
        }
    }

    private void saveStressDepression9q() {
        if (this.stressDepression9qInfo != null) {
            SfStressDepression9qInfoDao sfStressDepression9qInfoDao = new SfStressDepression9qInfoDao(mContext);
            this.stressDepression9qInfo.setPersonId(this.personInfo.getId());
            this.stressDepression9qInfo.setIdcard(this.personInfo.getIdcard());
            if (stressDepression9qInfo.getId() == null) {
                String id = sfStressDepression9qInfoDao.insert(this.stressDepression9qInfo);
                this.stressDepression9qInfo.setId(id);
            } else {
                sfStressDepression9qInfoDao.update(this.stressDepression9qInfo);
            }
            StressDepression9qInfo stressDepression9qInfo = sfStressDepression9qInfoDao.getById(Integer.parseInt(this.stressDepression9qInfo.getId()));
            if (stressDepression9qInfo != null) {
                System.out.println("stress depression 9q:" + stressDepression9qInfo.getId() + " " + stressDepression9qInfo.getPersonId());
                // อัปเดตสถานะหลังบันทึกข้อมูลสำเร็จ
                updateFormStatus("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", true);
            }
        }
    }

    private void saveSuicideAssessment8q() {
        if (this.suicideAssessment8qInfo != null) {
            SfSuicideAssessment8qInfoDao sfSuicideAssessment8qInfoDao = new SfSuicideAssessment8qInfoDao(mContext);
            this.suicideAssessment8qInfo.setPersonId(this.personInfo.getId());
            this.suicideAssessment8qInfo.setIdcard(this.personInfo.getIdcard());
            if (this.suicideAssessment8qInfo.getId() == null) {
                String id = sfSuicideAssessment8qInfoDao.insert(this.suicideAssessment8qInfo);
                this.suicideAssessment8qInfo.setId(id);
            } else {
                sfSuicideAssessment8qInfoDao.update(this.suicideAssessment8qInfo);
            }
            SuicideAssessment8qInfo suicideAssessment8qInfo = sfSuicideAssessment8qInfoDao.getById(Integer.parseInt(this.suicideAssessment8qInfo.getId()));
            if (suicideAssessment8qInfo != null) {
                System.out.println("Suicide Assessment 8q:" + suicideAssessment8qInfo.getId() + " " + suicideAssessment8qInfo.getPersonId());
                // อัปเดตสถานะหลังบันทึกข้อมูลสำเร็จ
                updateFormStatus("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", true);
            }
        }
    }

    private void saveHealthRiskAssessment() {
        if (this.healthRiskAssessmentInfo != null) {
            SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(mContext);
            this.healthRiskAssessmentInfo.setPersonId(this.personInfo.getId());
            this.healthRiskAssessmentInfo.setIdcard(this.personInfo.getIdcard());
            if (this.healthRiskAssessmentInfo.getId() == null) {
                String id = sfHealthRiskAssessmentInfoDao.insert(this.healthRiskAssessmentInfo);
                this.healthRiskAssessmentInfo.setId(id);
            } else {
                sfHealthRiskAssessmentInfoDao.update(this.healthRiskAssessmentInfo);
            }
            HealthRiskAssessmentInfo healthRiskAssessmentInfo = sfHealthRiskAssessmentInfoDao.getById(Integer.parseInt(this.healthRiskAssessmentInfo.getId()));
            if (healthRiskAssessmentInfo != null) {
                System.out.println("save HealthRiskAssessment:" + healthRiskAssessmentInfo.getId() + " " + healthRiskAssessmentInfo.getPersonId());
            }
        }
    }

    private void saveDrugsOne() {
        if (this.drugsOneInfos != null) {
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
        if (this.drugsTwoInfos != null) {
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
        if (this.drugsThreeInfos != null) {
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
        if (this.drugsFourInfos != null) {
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
        if (this.drugsFiveInfos != null) {
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
        if (this.drugsSixInfos != null) {
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
        if (this.drugsSevenInfos != null) {
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
        if (this.drugsEightInfos != null) {
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

    private void saveCounseling() {
        CounselingSignatureDao counselingDao = new CounselingSignatureDao(getBaseContext());
        if (counselingInfo != null) {
            if (personInfo != null) {
                counselingInfo.setPersonId(personInfo.getId());
                counselingInfo.setVisitId(personInfo.getVisitId());
            }

            UserSessionManager sessionManager = new UserSessionManager(getBaseContext());

            if (counselingInfo.getId() == 0) {
                // กรณีบันทึกใหม่
                counselingInfo.setCreatedBy(sessionManager.getUsername());
                long newId = counselingDao.saveCounseling(counselingInfo);

                if (newId > 0) {
                    counselingInfo.setId(newId);
                    System.out.println("บันทึกข้อมูลการให้คำปรึกษาสำเร็จ ID: " + newId);
                    updateFormStatus("ให้คำปรึกษาและแนะนำ", true);
                } else {
                    System.out.println("ไม่สามารถบันทึกข้อมูลการให้คำปรึกษาได้");
                }
            } else {
                // กรณีอัพเดต
                counselingInfo.setUpdatedBy(sessionManager.getUsername());
                int rowsUpdated = counselingDao.updateCounseling(counselingInfo);

                if (rowsUpdated > 0) {
                    System.out.println("อัพเดตข้อมูลการให้คำปรึกษาสำเร็จ ID: " + counselingInfo.getId());
                    updateFormStatus("ให้คำปรึกษาและแนะนำ", true);
                } else {
                    System.out.println("ไม่สามารถอัพเดตข้อมูลการให้คำปรึกษาได้");
                }
            }
        }
    }

    private void saveVisit() {
        UserSessionManager userSessionManager = new UserSessionManager(getBaseContext());
        VisitDao visitDao = new VisitDao(getContentResolver());
        PersonDao personDao = new PersonDao(getBaseContext());
        Person person = personDao.findByIdCard(this.personInfo.getIdcard());
        if (this.personInfo.getVisitId() == null) { // insert
            String visitDate = DateConverter.getCurrentWesternDate(); //  personInfo.getCreated_date()!=null?personInfo.getCreated_date().split(" ")[0]:DateConverter.getCurrentWesternDate();
            String pressure = ((int)personInfo.getSystolic_pressure())+"/"+ ((int)personInfo.getDiastolic_pressure());
            Integer pluse = personInfo.getBp() != null && !personInfo.getBp().isEmpty() ? Integer.valueOf(personInfo.getBp()) : 0;
            long visitId = visitDao.saveNewVisitWithVitalSigns(
                    userSessionManager.getPcuCode(),                     // pcucode
                    userSessionManager.getPcuCode(),                     // pcucodePerson
                    person.getPid(),                              // pid
                    visitDate,                        // visitDate
                    (float) personInfo.getWeight(),                       // weight
                    (float) personInfo.getHeight(),                       // height
                    pressure,                                   // pressure
                    (float) personInfo.getTemperature(),                  // temperature
                    pluse,                               // pluse
                    (float) personInfo.getWaist_size(),                   // waist
                    "",                 // systolic
                    "",                // diastolic                               // diagnote
                    userSessionManager.getUsername()                     // username
            );
            if (visitId > 0) {
                this.personInfo.setVisitId(String.valueOf(visitId));
                String seq = GenerateSeq.generateSeq(userSessionManager.getPcuCode());
                SfPersonInfoDao.updateVisitInfo(this.personInfo.getId(), String.valueOf(visitId), seq);
            }
        } else {
            String pressure = ((int)personInfo.getSystolic_pressure())+"/"+ ((int)personInfo.getDiastolic_pressure());
            Integer pluse = personInfo.getBp() != null && !personInfo.getBp().isEmpty() ? Integer.valueOf(personInfo.getBp()) : 0;
            visitDao.updateVisit(
                    Long.parseLong(personInfo.getVisitId()),            // visitNo
                    (float) personInfo.getWeight(),                      // weight
                    (float) personInfo.getHeight(),                      // height
                    pressure, // pressure
                    (float) personInfo.getTemperature(),                 // temperature
                    pluse, // pulse
                    (float) personInfo.getWaist_size(),                  // waist
                    "",   // symptoms (ในที่นี้ใช้ systolic แทน)
                    ""   // diagnote (ในที่นี้ใช้ diastolic แทน)
            );

        }
    }

    private List<DiagCode> getDiagCode() {
        int age = th.in.ffc.util.AgeCalculator.calculateAge(personInfo.getBirthday());
        SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(mContext);
        SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(mContext);
        List<CardiovascularRiskInfo> cardiovascularRiskInfos = sfCardiovascularRiskInfoDao.getByPersonId(Integer.parseInt(personInfo.getId()));
        List<HealthRiskAssessmentInfo> healthRiskAssessmentInfos = sfHealthRiskAssessmentInfoDao.getByPersonId(Integer.parseInt(personInfo.getId()));
        double fpg = 0.0;
        double choresteral = 0.0;
        if (!healthRiskAssessmentInfos.isEmpty()) {
            if (healthRiskAssessmentInfos.size() > 0) {
                if (healthRiskAssessmentInfos.get(0).getFpg() != null && !healthRiskAssessmentInfos.get(0).getFpg().isEmpty()) {
                    fpg = Double.parseDouble(healthRiskAssessmentInfos.get(0).getFpg());
                }
            }
        }
        if (cardiovascularRiskInfos.isEmpty()) {
            if (cardiovascularRiskInfos.size() > 0) {
                if (cardiovascularRiskInfos.get(0).getCholesterol() != null && !cardiovascularRiskInfos.get(0).getCholesterol().isEmpty()) {
                    choresteral = Double.parseDouble(cardiovascularRiskInfos.get(0).getCholesterol() != null ? cardiovascularRiskInfos.get(0).getCholesterol() : "0");
                }
            }
        }
        List<DiagCode> diagCodes = new ArrayList<>();
        String diagCode = "";
        if (15 <= age && age <= 34) {  // DX=Z13.3, Z13.6
//            diagCode = "DX=Z13.3,Z13.6";
            diagCodes.add(new DiagCode("Z13.3", "01", "ผ")); // Z13.3 - ผ
            diagCodes.add(new DiagCode("Z13.6", "04", "ช")); // Z13.6 - ช
            // "Z13.3" dxtype= 01   conti = ผ
            // "Z13.6" dxtype= 04   conti = ช

        } else if (35 <= age && age <= 59) { // DX=Z13.1, Z13.3
            diagCodes.add(new DiagCode("Z13.1", "01", "ผ"));
            diagCodes.add(new DiagCode("Z13.3", "04", "ช"));
            if (fpg > 0 ) {
                diagCodes.add(new DiagCode("Z13.1", "04", "ผ"));
            }
            if (choresteral > 0) {
                diagCodes.add(new DiagCode("Z13.1", "04", "ผ"));
            }
        }
        return diagCodes;
    }
    class DiagCode {
        String code;
        String dxtype;
        String conti;

        public DiagCode(String code, String dxtype, String conti) {
            this.code = code;
            this.dxtype = dxtype;
            this.conti = conti;
        }

        public String getCode() {
            return code;
        }

        public String getDxtype() {
            return dxtype;
        }

        public String getConti() {
            return conti;
        }
    }
    private void saveVisitDiag() {
        UserSessionManager userSessionManager = new UserSessionManager(getBaseContext());
        List<DiagCode> diagCodes = getDiagCode();

        if( !diagCodes.isEmpty()) {
            VisitDiagDao visitDiagDao = new VisitDiagDao(getBaseContext());
            long result =  visitDiagDao.deleteAllByVisitNo(this.personInfo.getVisitId());
            for (DiagCode diagCode : diagCodes) {

                VisitDiagInfo visitDiagInfo = new VisitDiagInfo();
                visitDiagInfo.setPcucode(userSessionManager.getPcuCode());
                visitDiagInfo.setVisitno(this.personInfo.getVisitId());
                visitDiagInfo.setDiagcode(diagCode.getCode());
                visitDiagInfo.setDxtype(diagCode.getDxtype());
                visitDiagInfo.setConti(diagCode.getConti());
                visitDiagDao.insert(visitDiagInfo);
//                if (!visitDiagInfo.getVisitno().isEmpty() && !visitDiagInfo.getPcucode().isEmpty()) {
//                    VisitDiagInfo savedVisitDiagInfo = visitDiagDao.getVisitDiagByVisitNoAndPcucode(visitDiagInfo.getVisitno(), visitDiagInfo.getPcucode());
//                    if (savedVisitDiagInfo != null) {
//                        // ถ้ามีข้อมูลอยู่แล้ว ให้ทำการอัพเดต
//                        visitDiagDao.update(visitDiagInfo);
//
//                    } else {
//                        // ถ้ายังไม่มีข้อมูล ให้สร้างใหม่
//                        visitDiagDao.insert(visitDiagInfo);
//                    }
//                }
            }
        } else {
            System.out.println("No DiagCode found for the person.");
        }


    }
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
        this.personInfo = data;

        // ป้องกัน infinite loop - อัพเดต PersonData เฉพาะเมื่อไม่ใช่การ auto-select
        if (sharedViewModel != null && !isUpdatingPersonData) {
            isUpdatingPersonData = true;

            try {
                // คำนวณอายุจากวันเกิด
                Integer age = null;
                if (data.getBirthday() != null && !data.getBirthday().isEmpty()) {
                    age = AgeCalculator.calculateAge(data.getBirthday());
                }

                // คำนวณ BMI จากน้ำหนักและส่วนสูง
                Double bmi = null;
                if (data.getWeight() > 0 && data.getHeight() > 0) {
                    double heightInMeters = data.getHeight() / 100.0; // แปลงจาก cm เป็น m
                    bmi = data.getWeight() / (heightInMeters * heightInMeters);
                }

                // ตรวจสอบความดันโลหิตสูง
                Boolean hasHypertension = null;
                if (data.getSystolic_pressure() > 0 && data.getDiastolic_pressure() > 0) {
                    // เกณฑ์ความดันโลหิตสูง: Systolic >= 140 หรือ Diastolic >= 90
                    hasHypertension = (data.getSystolic_pressure() >= 140 || data.getDiastolic_pressure() >= 90);
                }

                // อัพเดต PersonData ใน SharedViewModel
                sharedViewModel.updatePersonDataFromScreening(
                        age,                              // age
                        data.getGender(),                 // gender
                        bmi,                             // bmi
                        (double) data.getWaist_size(),   // waistCircumference
                        hasHypertension,                 // hasHypertension
                        null,                            // hasFamilyDiabetesHistory (จะได้จาก HealthRiskAssessment)
                        null,                            // fcbg (จะได้จาก HealthRiskAssessment)
                        null                             // fpg (จะได้จาก HealthRiskAssessment)
                );
            } catch (Exception e) {
                System.out.println("Error updating PersonData from PersonInfo: " + e.getMessage());
            } finally {
                isUpdatingPersonData = false;
            }
        }

        if (this.personInfo.getSend_to_claim() != null) {
            btnOk.setEnabled(!this.personInfo.getSend_to_claim().equals(1));
        }
    }

    @Override
    public void onValidationStatusChanged(boolean isValid) {

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

    // ปรับปรุงเมธอด onStressDepression() เพื่อเพิ่มการตรวจสอบข้อมูล
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

        this.stressDepressionInfo = data;

        // ตรวจสอบความครบถ้วนของข้อมูล
        boolean isComplete = isStressDepressionDataComplete(data);

        // อัปเดตสถานะการกรอกข้อมูล
        updateFormStatus("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", isComplete);

        System.out.println(msg);
    }
    // เพิ่มเมธอดสำหรับตรวจสอบความครบถ้วนของข้อมูล StressDepression
    private boolean isStressDepressionDataComplete(StressDepressionInfo data) {
        if (data == null) return false;

        // ตรวจสอบว่าตอบครบทุกคำถาม (Q1-Q5)
        return !data.getQ1().equals("0") &&
                !data.getQ2().equals("0") &&
                !data.getQ3().equals("0") &&
                !data.getQ4().equals("0") &&
                !data.getQ5().equals("0");
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

        this.stressDepression2qInfo = data;

        // ตรวจสอบความครบถ้วนของข้อมูล
        boolean isComplete = isStressDepression2qDataComplete(data);

        // อัปเดตสถานะการกรอกข้อมูล
        updateFormStatus("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", isComplete);

        System.out.println(msg);
    }
    // เพิ่มเมธอดสำหรับตรวจสอบความครบถ้วนของข้อมูล StressDepression2q
    private boolean isStressDepression2qDataComplete(StressDepression2qInfo data) {
        if (data == null) return false;

        // ตรวจสอบว่าตอบครบทุกคำถาม (Q1-Q2)
        return !data.getQ1().equals("0") && !data.getQ2().equals("0");
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

        this.stressDepression9qInfo = data;

        // ตรวจสอบความครบถ้วนของข้อมูล
        boolean isComplete = isStressDepression9qDataComplete(data);

        // อัปเดตสถานะการกรอกข้อมูล
        updateFormStatus("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", isComplete);

        System.out.println(msg);
       // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }
    // เพิ่มเมธอดสำหรับตรวจสอบความครบถ้วนของข้อมูล StressDepression9q
    private boolean isStressDepression9qDataComplete(StressDepression9qInfo data) {
        if (data == null) return false;

        // ตรวจสอบว่าตอบครบทุกคำถาม (Q1-Q9)
        return !data.getQ1().equals("0") && !data.getQ2().equals("0") &&
                !data.getQ3().equals("0") && !data.getQ4().equals("0") &&
                !data.getQ5().equals("0") && !data.getQ6().equals("0") &&
                !data.getQ7().equals("0") && !data.getQ8().equals("0") &&
                !data.getQ9().equals("0");
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

        // ตรวจสอบความครบถ้วนของข้อมูล
        boolean isComplete = isSuicideAssessment8qDataComplete(data);

        // อัปเดตสถานะการกรอกข้อมูล
        updateFormStatus("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", isComplete);

        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

    }
    /**
     * ตรวจสอบความครบถ้วนของข้อมูล SuicideAssessment8q
     */
    private boolean isSuicideAssessment8qDataComplete(SuicideAssessment8qInfo data) {
        if (data == null) return false;

        // ตรวจสอบคำถามหลัก Q1-Q8
        boolean mainQuestionsComplete = !data.getQ1().equals("0") &&
                !data.getQ2().equals("0") &&
                !data.getQ3().equals("0") &&
                !data.getQ4().equals("0") &&
                !data.getQ5().equals("0") &&
                !data.getQ6().equals("0") &&
                !data.getQ7().equals("0") &&
                !data.getQ8().equals("0");

        // ตรวจสอบคำถามย่อย Q3_2_1 (หากจำเป็น)
        boolean subQuestionComplete = true;
        if (data.getQ3().equals("2")) { // หากตอบ "มี" ในคำถาม Q3
            subQuestionComplete = !data.getQ3_2_1().equals("0");
        }

        return mainQuestionsComplete && subQuestionComplete;
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

        // ป้องกัน infinite loop - อัพเดต PersonData เฉพาะเมื่อไม่ใช่การ auto-select
        if (sharedViewModel != null && !isUpdatingPersonData) {
            isUpdatingPersonData = true;

            try {
                // แปลงค่า Q6 (ประวัติครอบครัว) เป็น Boolean
                Boolean hasFamilyDiabetesHistory = null;
                if (data.getHealthRiskQ6() != null && !data.getHealthRiskQ6().equals("0")) {
                    hasFamilyDiabetesHistory = data.getHealthRiskQ6().equals("2"); // "2" = มี, "1" = ไม่มี
                }

                // แปลงค่า FCBG และ FPG
                Double fcbg = null;
                Double fpg = null;
                try {
                    if (data.getFcbg() != null && !data.getFcbg().isEmpty()) {
                        fcbg = Double.parseDouble(data.getFcbg());
                    }
                    if (data.getFpg() != null && !data.getFpg().isEmpty()) {
                        fpg = Double.parseDouble(data.getFpg());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing glucose values: " + e.getMessage());
                }

                sharedViewModel.updatePersonDataFromScreening(
                        null,                        // age (ไม่เปลี่ยน)
                        null,                        // gender (ไม่เปลี่ยน)
                        null,                        // bmi (ไม่เปลี่ยน)
                        null,                        // waistCircumference (ไม่เปลี่ยน)
                        null,                        // hasHypertension (ไม่เปลี่ยน)
                        hasFamilyDiabetesHistory,    // hasFamilyDiabetesHistory
                        fcbg,                        // fcbg
                        fpg                          // fpg
                );
            } catch (Exception e) {
                System.out.println("Error updating PersonData from HealthRiskAssessment: " + e.getMessage());
            } finally {
                isUpdatingPersonData = false;
            }
        }

        System.out.println(msg);
    }
    /**
     * เมธอดสำหรับตรวจสอบสถานะ SuicideAssessment8qFragment
     */
    public void updateSuicideAssessment8qStatus() {
        // ค้นหา Fragment จากหน้าจอปัจจุบัน
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof SuicideAssessment8qFragment) {
                SuicideAssessment8qFragment suicide8qFragment = (SuicideAssessment8qFragment) fragment;
                boolean isComplete = suicide8qFragment.isFormComplete();

                // อัปเดตสถานะใน expandableListAdapter
                if (expandableListAdapter != null) {
                    expandableListAdapter.updateCompletionStatus("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", isComplete);
                }

                break;
            }
        }
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
        displayData(data);
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsThreeInfo(List<DrugsInfo> data) {
        this.drugsThreeInfos = data;
        displayData(data);
    }

    @Override
    public void onDrugsFourInfo(List<DrugsInfo> data) {
        this.drugsFourInfos = data;
        displayData(data);
    }

    @Override
    public void onDrugsFiveInfo(List<DrugsInfo> data) {
        this.drugsFiveInfos = data;
        displayData(data);
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsSixInfo(List<DrugsInfo> data) {
        this.drugsSixInfos = data;
        displayData(data);
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsSevenInfo(List<DrugsInfo> data) {
        this.drugsSevenInfos = data;
        displayData(data);
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDrugsEightInfo(List<DrugsInfo> data) {
        this.drugsEightInfos = data;
        displayData(data);
//        String msg = "====> "+data.size();
//        System.out.println(msg);
//        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardiovascularRiskInfo(CardiovascularRiskInfo data) {
        this.cardiovascularRiskInfo = data;

        // ป้องกัน infinite loop - อัพเดต PersonData เฉพาะเมื่อไม่ใช่การ auto-select
        if (sharedViewModel != null && data != null && !isUpdatingPersonData) {
            isUpdatingPersonData = true;

            try {
                // ตรวจสอบความดันโลหิตจาก CardiovascularRisk
                Boolean hasHypertension = null;
                if (data.getBloodPressure() != null && !data.getBloodPressure().isEmpty()) {
                    // แปลงข้อมูลความดันโลหิตตามรูปแบบที่เก็บใน CardiovascularRisk
                    try {
                        String[] bpParts = data.getBloodPressure().split("/");
                        if (bpParts.length == 2) {
                            int systolic = Integer.parseInt(bpParts[0]);
                            int diastolic = Integer.parseInt(bpParts[1]);
                            hasHypertension = (systolic >= 140 || diastolic >= 90);
                        }
                    } catch (Exception e) {
                        System.out.println("Error parsing blood pressure from CardiovascularRisk: " + e.getMessage());
                    }
                }

                if (hasHypertension != null) {
                    sharedViewModel.updatePersonDataFromScreening(
                            null,           // age (ไม่เปลี่ยน)
                            null,           // gender (ไม่เปลี่ยน)
                            null,           // bmi (ไม่เปลี่ยน)
                            null,           // waistCircumference (ไม่เปลี่ยน)
                            hasHypertension, // hasHypertension (อัพเดตจาก CardiovascularRisk)
                            null,           // hasFamilyDiabetesHistory (ไม่เปลี่ยน)
                            null,           // fcbg (ไม่เปลี่ยน)
                            null            // fpg (ไม่เปลี่ยน)
                    );
                }
            } catch (Exception e) {
                System.out.println("Error updating PersonData from CardiovascularRisk: " + e.getMessage());
            } finally {
                isUpdatingPersonData = false;
            }
        }

        String msg = "====> "+data;
        System.out.println(msg);
    }

    @Override
    public void onAssistScoreInfo(AssistScore data) {
         this.assistScoreInfo = data;

//        System.out.println(msg);
        // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCounselingDataPass(CounselingInfo counselingInfo) {
        this.counselingInfo = counselingInfo;
        System.out.println("ID: " + counselingInfo.getId());
        System.out.println("Person Info ID: " + counselingInfo.getPersonId());
        System.out.println("Counseling Type: " + counselingInfo.getCounselingType());

        // บันทึกลายเซ็นเป็นไฟล์รูปภาพ
        try {
            // ตรวจสอบลายเซ็นผู้รับบริการ
            byte[] patientSignature = counselingInfo.getPatientSignature();
            if (patientSignature != null && patientSignature.length > 0) {
                System.out.println("PatientSignature size: " + patientSignature.length + " bytes");
                saveSignatureToFile(patientSignature, "patient_signature.png");
            } else {
                System.out.println("PatientSignature: NULL or EMPTY");
            }

            // ตรวจสอบลายเซ็นผู้ให้บริการ
            byte[] providerSignature = counselingInfo.getProviderSignature();
            if (providerSignature != null && providerSignature.length > 0) {
                System.out.println("ProviderSignature size: " + providerSignature.length + " bytes");
                saveSignatureToFile(providerSignature, "provider_signature.png");
            } else {
                System.out.println("ProviderSignature: NULL or EMPTY");
            }
        } catch (Exception e) {
            System.out.println("Error saving signature files: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // เพิ่มเมธอดใหม่สำหรับอัพเดตข้อมูลทั้งหมดใน PersonData
    private void updatePersonDataFromCurrentInfo() {
        if (sharedViewModel == null || isUpdatingPersonData) return;

        isUpdatingPersonData = true;

        try {
            PersonData personData = new PersonData();

            // ข้อมูลจาก PersonInfo
            if (personInfo != null) {
                // อายุ
                if (personInfo.getBirthday() != null && !personInfo.getBirthday().isEmpty()) {
                    Integer age = AgeCalculator.calculateAge(personInfo.getBirthday());
                    personData.setAge(age);
                }

                // เพศ
                personData.setGender(personInfo.getGender());

                // BMI
                if (personInfo.getWeight() > 0 && personInfo.getHeight() > 0) {
                    double heightInMeters = personInfo.getHeight() / 100.0;
                    Double bmi = personInfo.getWeight() / (heightInMeters * heightInMeters);
                    personData.setBmi(bmi);
                }

                // รอบเอว
                if (personInfo.getWaist_size() > 0) {
                    personData.setWaistCircumference((double) personInfo.getWaist_size());
                }

                // ความดันโลหิต
                if (personInfo.getSystolic_pressure() > 0 && personInfo.getDiastolic_pressure() > 0) {
                    Boolean hasHypertension = (personInfo.getSystolic_pressure() >= 140 ||
                            personInfo.getDiastolic_pressure() >= 90);
                    personData.setHasHypertension(hasHypertension);
                }
            }

            // ข้อมูลจาก HealthRiskAssessmentInfo
            if (healthRiskAssessmentInfo != null) {
                // ประวัติครอบครัว
                if (healthRiskAssessmentInfo.getHealthRiskQ6() != null &&
                        !healthRiskAssessmentInfo.getHealthRiskQ6().equals("0")) {
                    Boolean hasFamilyDiabetes = healthRiskAssessmentInfo.getHealthRiskQ6().equals("2");
                    personData.setHasFamilyDiabetesHistory(hasFamilyDiabetes);
                }

                // FCBG
                try {
                    if (healthRiskAssessmentInfo.getFcbg() != null &&
                            !healthRiskAssessmentInfo.getFcbg().isEmpty()) {
                        Double fcbg = Double.parseDouble(healthRiskAssessmentInfo.getFcbg());
                        personData.setFcbg(fcbg);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing FCBG: " + e.getMessage());
                }

                // FPG
                try {
                    if (healthRiskAssessmentInfo.getFpg() != null &&
                            !healthRiskAssessmentInfo.getFpg().isEmpty()) {
                        Double fpg = Double.parseDouble(healthRiskAssessmentInfo.getFpg());
                        personData.setFpg(fpg);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error parsing FPG: " + e.getMessage());
                }
            }

            // ส่งข้อมูลไปยัง SharedViewModel
            sharedViewModel.setPersonData(personData);

            System.out.println("PersonData updated: " + personData.toString());

        } catch (Exception e) {
            System.out.println("Error in updatePersonDataFromCurrentInfo: " + e.getMessage());
        } finally {
            isUpdatingPersonData = false;
        }
    }
    /**
     * บันทึกลายเซ็นเป็นไฟล์รูปภาพในโฟลเดอร์ของแอป
     * @param signatureBytes ข้อมูลลายเซ็นแบบ byte array
     * @param filename ชื่อไฟล์ที่ต้องการบันทึก
     */
    private void saveSignatureToFile(byte[] signatureBytes, String filename) {
        try {
            // ตรวจสอบข้อมูล
            if (signatureBytes == null || signatureBytes.length == 0) {
                System.out.println("Cannot save empty signature to file: " + filename);
                return;
            }

            // แปลง byte array เป็น Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(signatureBytes, 0, signatureBytes.length);
            if (bitmap == null) {
                System.out.println("Failed to decode signature as bitmap for file: " + filename);
                return;
            }

            // บันทึกลงไฟล์ในโฟลเดอร์ Internal Storage ของแอป
            File dir = getBaseContext().getFilesDir();
            File file = new File(dir, filename);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            System.out.println("Signature saved to file: " + file.getAbsolutePath());

            // ส่ง broadcast เพื่อให้ Gallery อัพเดท (เฉพาะ External Storage)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // สำหรับ Android 10+
                try {
                    // บันทึกลงใน Pictures เพื่อให้เห็นได้ง่าย
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
                    values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Signatures");
                    values.put(MediaStore.Images.Media.IS_PENDING, 1);

                    ContentResolver resolver = getBaseContext().getContentResolver();
                    Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    if (uri != null) {
                        OutputStream os = resolver.openOutputStream(uri);
                        if (os != null) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                            os.close();

                            values.clear();
                            values.put(MediaStore.Images.Media.IS_PENDING, 0);
                            resolver.update(uri, values, null, null);

                            System.out.println("Signature also saved to gallery: " + uri.toString());
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Error saving to gallery: " + e.getMessage());
                }
            } else {
                // สำหรับ Android 9 และต่ำกว่า
                try {
                    String externalDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                    File extDir = new File(externalDir + "/Signatures");
                    if (!extDir.exists()) {
                        extDir.mkdirs();
                    }

                    File extFile = new File(extDir, filename);
                    FileOutputStream extFos = new FileOutputStream(extFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, extFos);
                    extFos.flush();
                    extFos.close();

                    // อัพเดท Gallery
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(extFile);
                    mediaScanIntent.setData(contentUri);
                    getBaseContext().sendBroadcast(mediaScanIntent);

                    System.out.println("Signature also saved to gallery: " + extFile.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("Error saving to gallery: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving signature to file: " + e.getMessage());
            e.printStackTrace();
        }
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
            updateStressDepressionStatus();
        }
        else if (formData instanceof HealthRiskAssessmentInfo) {
            healthRiskAssessmentInfo = (HealthRiskAssessmentInfo) formData;
            saveHealthRiskAssessment();
            // ไม่ต้องเรียก updatePersonDataFromCurrentInfo() ที่นี่ เพราะจะทำใน onHealthRiskAssessmentInfo แล้ว
        }
        else if (formData instanceof NicotineInfo) {
            nicotineInfo = (NicotineInfo) formData;
            saveNicotine();
        }
        else if (formData instanceof StressDepression2qInfo) {
            stressDepression2qInfo = (StressDepression2qInfo) formData;
            saveStressDepression2q();
            updateStressDepression2qStatus();
        }
        else if (formData instanceof StressDepression9qInfo) {
            stressDepression9qInfo = (StressDepression9qInfo) formData;
            saveStressDepression9q();
            updateStressDepression9qStatus();
        }
        else if (formData instanceof SuicideAssessment8qInfo) {
            suicideAssessment8qInfo = (SuicideAssessment8qInfo) formData;
            saveSuicideAssessment8q();
            updateSuicideAssessment8qStatus();
        }
        else if (formData instanceof CardiovascularRiskInfo) {
            cardiovascularRiskInfo = (CardiovascularRiskInfo) formData;
            saveCardiovascularRisk();
            // ไม่ต้องเรียก updatePersonDataFromCurrentInfo() ที่นี่ เพราะจะทำใน onCardiovascularRiskInfo แล้ว
        }
        else if (formData instanceof AssistScore) {
            assistScoreInfo = (AssistScore) formData;
        } else if (formData instanceof CounselingInfo) {
            counselingInfo = (CounselingInfo) formData;
        }

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
    // เพิ่มเมธอดนี้ใน PersonScreeningForm15Activity.java
    public void updateMainQuestionsStatus() {
        // ค้นหา Fragment จากหน้าจอปัจจุบัน
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof MainQuestionsFragment) {
                MainQuestionsFragment mainFragment = (MainQuestionsFragment) fragment;
                boolean isComplete = mainFragment.isAllDataComplete();

                // อัปเดตสถานะใน expandableListAdapter
                if (expandableListAdapter != null) {
                    expandableListAdapter.updateCompletionStatus("แบบคัดกรองการใช้สารเสพติด", isComplete);
                }

                break;
            }
        }
    }
    // เพิ่มเมธอดใหม่สำหรับตรวจสอบสถานะ StressDepression2qFragment
    public void updateStressDepression2qStatus() {
        // ค้นหา Fragment จากหน้าจอปัจจุบัน
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof StressDepression2qFragment) {
                StressDepression2qFragment stress2qFragment = (StressDepression2qFragment) fragment;
                boolean isComplete = stress2qFragment.isFormComplete();

                // อัปเดตสถานะใน expandableListAdapter
                if (expandableListAdapter != null) {
                    expandableListAdapter.updateCompletionStatus("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", isComplete);
                }

                break;
            }
        }
    }
    // เพิ่มเมธอดใหม่สำหรับตรวจสอบสถานะ StressDepression9qFragment
    public void updateStressDepression9qStatus() {
        // ค้นหา Fragment จากหน้าจอปัจจุบัน
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof StressDepression9qFragment) {
                StressDepression9qFragment stress9qFragment = (StressDepression9qFragment) fragment;
                boolean isComplete = stress9qFragment.isFormComplete();

                // อัปเดตสถานะใน expandableListAdapter
                if (expandableListAdapter != null) {
                    expandableListAdapter.updateCompletionStatus("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", isComplete);
                }

                break;
            }
        }
    }


}