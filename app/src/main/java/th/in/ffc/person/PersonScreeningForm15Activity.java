package th.in.ffc.person;



import static java.security.AccessController.getContext;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import th.in.ffc.intent.Action;
import th.in.ffc.model.Person;
import th.in.ffc.provider.CounselingSignatureProvider;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.security.CryptographerService;
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
import java.util.Objects;
import android.widget.Switch;
import android.widget.CompoundButton;

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

    private boolean hasTobaccoUse = false;
    private boolean hasAlcoholUse = false;

    private boolean previousTobaccoUse = false;
    private boolean previousAlcoholUse = false;
    private boolean isInitialLoad = true;


    // เพิ่มในส่วน Declaration ของ PersonScreeningForm15Activity.java
    private LinearLayout counselingInfoContainer;
    private TextView textCounselingType;
    private TextView textCounselingDetail;
    private Switch switchValidationMode;

    private LinearLayout validationModeHeader;
    private FrameLayout validationModeContainer;
    private ImageView validationModeExpandIcon;
    private TextView textCurrentValidationMode;
    private boolean isPersonAgeValid = false;
    private int personAge = 0;
    private String personAgeGroup = "";
    private FrameLayout personInfoContainer;
    private ImageView personInfoExpandIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        initializeCounselingInfo();
        mContext = this;
        setupValidationModeSwitch();
        // เปลี่ยนจาก getBaseContext() เป็น this
        View personInfoHeader = findViewById(R.id.personInfoHeader);
        personInfoContainer = findViewById(R.id.personInfoContainer);
        personInfoExpandIcon = findViewById(R.id.personInfoExpandIcon);
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // ตั้งค่าการคลิกเพื่อขยาย/ย่อ
        personInfoHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (personInfoContainer.getVisibility() == View.VISIBLE) {
                    personInfoContainer.setVisibility(View.GONE);
                    personInfoExpandIcon.setImageResource(R.drawable.ic_expand_more_black);
                } else {
                    personInfoContainer.setVisibility(View.VISIBLE);
                    personInfoExpandIcon.setImageResource(R.drawable.ic_expand_less_black);
                }
            }
        });

        // เรียก getPersonInfoDetail() ก่อน
        getPersonInfoDetail();

        // แก้ไข: หน่วงเวลาเพิ่มขึ้นและเปลี่ยนลำดับการทำงาน
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // ตรวจสอบข้อมูลการใช้สารเสพติดก่อน
                checkSubstanceUseAndUpdateMenu();

                // เตรียมข้อมูลเมนูหลังจากได้ข้อมูลการใช้สารเสพติดแล้ว
                prepareListData();

                // ค้นหาและกำหนดค่า ExpandableListView
                expandableListView = findViewById(R.id.expandableListView);
                expandableListAdapter = new ScreeningExpandableListAdapter(PersonScreeningForm15Activity.this, categoryList, subcategoryMap);
                expandableListView.setAdapter(expandableListAdapter);

                // ตั้งค่า listeners
                setupExpandableListViewListeners();

                // ขยายรายการทั้งหมดแบบอัตโนมัติ
                expandAllGroups();

                // ตรวจสอบข้อมูลที่มีอยู่แล้วหลังจากสร้างเมนูเสร็จ
                if (personInfo != null && personInfo.getId() != null) {
                    checkExistingData(personInfo.getId());
                }
            }
        }, 1000); // เพิ่มเวลาจาก 0 เป็น 1000ms

        // ตั้งค่าปุ่มต่างๆ
        setupButtons();

        // ตรวจสอบสถานะการส่งข้อมูล
        checkSendStatus();
    }
    private boolean validateAgeForAssessment(String formName) {
        // ตรวจสอบอายุจาก PersonInfo
        if (personInfo == null || personInfo.getBirthday() == null || personInfo.getBirthday().isEmpty()) {
            showAgeRequiredDialog();
            return false;
        }

        // คำนวณและตรวจสอบอายุ
        updatePersonAgeInfo();

        if (!isPersonAgeValid) {
            showInvalidAgeDialog(formName);
            return false;
        }

        return true;
    }
    private void showInvalidAgeDialog(String formName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View titleView = createWarningTitleView("อายุไม่อยู่ในเกณฑ์", R.drawable.ic_warning);

        String message = "ไม่สามารถทำแบบประเมิน \"" + formName + "\" ได้\n\n" +
                "📊 อายุปัจจุบัน: " + personAge + " ปี\n\n" +
                "📋 เกณฑ์อายุสำหรับแบบประเมิน:\n" +
                "✅ อายุ 15-34 ปี: การคัดกรองพื้นฐาน\n" +
                "✅ อายุ 35-59 ปี: การคัดกรองแบบละเอียด\n\n" +
                "กรุณาตรวจสอบข้อมูลวันเกิด หรือปรึกษาเจ้าหน้าที่";

        builder.setCustomTitle(titleView)
                .setMessage(message)
                .setPositiveButton("แก้ไขวันเกิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // ขยายส่วนข้อมูลบุคคลหากยังไม่ขยาย
                        if (personInfoContainer != null && personInfoContainer.getVisibility() != View.VISIBLE) {
                            personInfoContainer.setVisibility(View.VISIBLE);
                            personInfoExpandIcon.setImageResource(R.drawable.ic_expand_less_black);
                        }

                        scrollToPersonInfo();
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("ดูข้อมูลอายุ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDetailedAgeInfo();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

                if (positiveButton != null) {
                    positiveButton.setTextColor(Color.parseColor("#FF9800"));
                    positiveButton.setTypeface(null, Typeface.BOLD);
                }

                if (neutralButton != null) {
                    neutralButton.setTextColor(Color.parseColor("#2196F3"));
                }

                if (negativeButton != null) {
                    negativeButton.setTextColor(Color.parseColor("#757575"));
                }
            }
        });

        dialog.show();
    }
    private View createWarningTitleView(String title, int iconRes) {
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(24, 16, 24, 16);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setBackgroundColor(Color.parseColor("#FFF3E0"));

        ImageView iconView = new ImageView(this);
        iconView.setImageResource(iconRes);
        iconView.setColorFilter(Color.parseColor("#FF9800"));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24)
        );
        iconParams.setMargins(0, 0, dpToPx(12), 0);
        titleLayout.addView(iconView, iconParams);

        TextView titleTextView = new TextView(this);
        titleTextView.setText(title);
        titleTextView.setTextColor(Color.parseColor("#FF9800"));
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleLayout.addView(titleTextView);

        return titleLayout;
    }
    private void showDetailedAgeInfo() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_detailed_age_info, null);
        TextView tvCurrentAge = dialogView.findViewById(R.id.tvCurrentAge);
        TextView tvAgeGroup = dialogView.findViewById(R.id.tvAgeGroup);
        TextView tvAssessmentInfo = dialogView.findViewById(R.id.tvAssessmentInfo);
        TextView tvRecommendation = dialogView.findViewById(R.id.tvRecommendation);

        tvCurrentAge.setText("อายุปัจจุบัน: " + personAge + " ปี");

        String ageGroupText;
        String assessmentText;
        String recommendationText;

        if (personAge < 15) {
            ageGroupText = "กลุ่มอายุ: น้อยกว่า 15 ปี";
            assessmentText = "❌ ไม่อยู่ในเกณฑ์สำหรับแบบประเมินนี้";
            recommendationText = "💡 แนะนำ: ปรึกษาเจ้าหน้าที่เพื่อขอคำแนะนำเกี่ยวกับการดูแลสุขภาพที่เหมาะสมกับวัย";
        } else if (personAge >= 15 && personAge <= 34) {
            ageGroupText = "กลุ่มอายุ: 15-34 ปี (วัยหนุ่มสาว)";
            assessmentText = "✅ เหมาะสำหรับการคัดกรองพื้นฐาน\n" +
                    "📋 แบบประเมินที่แนะนำ:\n" +
                    "• การใช้สารเสพติด\n" +
                    "• ภาวะเครียดและซึมเศร้า\n" +
                    "• ความเสี่ยงด้านสุขภาพทั่วไป";
            recommendationText = "💡 แนะนำ: เน้นการป้องกันและสร้างพฤติกรรมสุขภาพที่ดี";
        } else if (personAge >= 35 && personAge <= 59) {
            ageGroupText = "กลุ่มอายุ: 35-59 ปี (วัยกลางคน)";
            assessmentText = "✅ เหมาะสำหรับการคัดกรองแบบละเอียด\n" +
                    "📋 แบบประเมินที่แนะนำ:\n" +
                    "• การใช้สารเสพติด\n" +
                    "• ภาวะเครียดและซึมเศร้า\n" +
                    "• ความเสี่ยงโรคเบาหวาน\n" +
                    "• ความเสี่ยงโรคหัวใจและหลอดเลือด";
            recommendationText = "💡 แนะนำ: เน้นการคัดกรองโรคเรื้อรังและการดูแลสุขภาพเชิงป้องกัน";
        } else {
            ageGroupText = "กลุ่มอายุ: มากกว่า 59 ปี";
            assessmentText = "❌ ไม่อยู่ในเกณฑ์สำหรับแบบประเมินนี้";
            recommendationText = "💡 แนะนำ: ปรึกษาแพทย์เพื่อขอคำแนะนำเกี่ยวกับการดูแลสุขภาพที่เหมาะสมกับวัย";
        }

        tvAgeGroup.setText(ageGroupText);
        tvAssessmentInfo.setText(assessmentText);
        tvRecommendation.setText(recommendationText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View titleView = createInfoTitleView("ข้อมูลอายุและแบบประเมิน", R.drawable.ic_info);

        builder.setCustomTitle(titleView)
                .setView(dialogView)
                .setPositiveButton("เข้าใจแล้ว", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private View createInfoTitleView(String title, int iconRes) {
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(24, 16, 24, 16);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setBackgroundColor(Color.parseColor("#E3F2FD"));

        ImageView iconView = new ImageView(this);
        iconView.setImageResource(iconRes);
        iconView.setColorFilter(Color.parseColor("#2196F3"));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24)
        );
        iconParams.setMargins(0, 0, dpToPx(12), 0);
        titleLayout.addView(iconView, iconParams);

        TextView titleTextView = new TextView(this);
        titleTextView.setText(title);
        titleTextView.setTextColor(Color.parseColor("#2196F3"));
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleLayout.addView(titleTextView);

        return titleLayout;
    }

    private void showAgeRequiredDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View titleView = createInfoTitleView("ข้อมูลไม่ครบถ้วน", R.drawable.ic_info);

        builder.setCustomTitle(titleView)
                .setMessage("กรุณากรอกข้อมูลวันเกิดในส่วนข้อมูลบุคคลก่อน\nเพื่อให้ระบบตรวจสอบอายุสำหรับการทำแบบประเมิน")
                .setPositiveButton("ไปกรอกข้อมูล", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // ขยายส่วนข้อมูลบุคคลหากยังไม่ขยาย
                        if (personInfoContainer != null && personInfoContainer.getVisibility() != View.VISIBLE) {
                            personInfoContainer.setVisibility(View.VISIBLE);
                            personInfoExpandIcon.setImageResource(R.drawable.ic_expand_less_black);
                        }

                        // Focus ไปที่ช่องวันเกิด (ถ้าเป็นไปได้)
                        scrollToPersonInfo();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void scrollToPersonInfo() {
        new Handler().postDelayed(() -> {
            View personInfoHeader = findViewById(R.id.personInfoHeader);
            if (personInfoHeader != null) {
                personInfoHeader.requestFocus();

                // เอฟเฟกต์กะพริบเบาๆ เพื่อดึงดูดความสนใจ
                ObjectAnimator fadeOut = ObjectAnimator.ofFloat(personInfoHeader, "alpha", 1f, 0.3f);
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(personInfoHeader, "alpha", 0.3f, 1f);

                fadeOut.setDuration(300);
                fadeIn.setDuration(300);

                fadeOut.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fadeIn.start();
                    }
                });

                fadeOut.start();
            }
        }, 500);
    }

    private void updatePersonAgeInfo() {
        if (personInfo != null && personInfo.getBirthday() != null && !personInfo.getBirthday().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                Date birth = sdf.parse(personInfo.getBirthday());

                if (birth != null) {
                    Calendar birthCal = Calendar.getInstance();
                    birthCal.setTime(birth);

                    Calendar today = Calendar.getInstance();

                    int age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);

                    if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                        age--;
                    }

                    personAge = age;
                    isPersonAgeValid = (age >= 15 && age <= 59);

                    if (age >= 15 && age <= 34) {
                        personAgeGroup = "15-34";
                    } else if (age >= 35 && age <= 59) {
                        personAgeGroup = "35-59";
                    } else {
                        personAgeGroup = "INVALID";
                    }

                    Log.d("PersonScreeningForm15Activity",
                            "Age updated - Age: " + age + ", Valid: " + isPersonAgeValid + ", Group: " + personAgeGroup);
                }
            } catch (Exception e) {
                Log.e("PersonScreeningForm15Activity", "Error calculating age: " + e.getMessage());
                personAge = 0;
                isPersonAgeValid = false;
                personAgeGroup = "INVALID";
            }
        }
    }
    private void setupValidationModeSwitch() {
        // ค้นหา views ที่เกี่ยวข้อง
        validationModeHeader = findViewById(R.id.validationModeHeader);
        validationModeContainer = findViewById(R.id.validationModeContainer);
        validationModeExpandIcon = findViewById(R.id.validationModeExpandIcon);
        textCurrentValidationMode = findViewById(R.id.textCurrentValidationMode);
        switchValidationMode = findViewById(R.id.switchValidationMode);

        // ตั้งค่าการคลิกเพื่อขยาย/ย่อ
        validationModeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleValidationModeVisibility();
            }
        });

        // ตั้งค่าเริ่มต้นตามสถานะปัจจุบัน
        switchValidationMode.setChecked(PersonAdapter.isPartialMode());
        updateValidationModeDisplay();

        // ตั้งค่า listener
        switchValidationMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // เปลี่ยนโหมดการตรวจสอบ
                PersonAdapter.setValidationMode(isChecked);

                // อัปเดตการแสดงผล
                updateValidationModeDisplay();

                // แสดงข้อความแจ้งเตือน
                String modeText = isChecked ? "โหมดบางส่วน" : "โหมดปกติ";
                String message = "เปลี่ยนเป็น " + modeText + " แล้ว";
                Toast.makeText(PersonScreeningForm15Activity.this, message, Toast.LENGTH_SHORT).show();

                Log.d("VALIDATION_MODE", "Changed to: " + modeText);

                // ปิด container หลังจากเปลี่ยนโหมดแล้ว (เลือกใช้หรือไม่)
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (validationModeContainer.getVisibility() == View.VISIBLE) {
                            toggleValidationModeVisibility();
                        }
                    }
                }, 1500); // ปิดหลังจาก 1.5 วินาที
            }
        });
    }
    private void updateValidationModeDisplay() {
        boolean isPartialMode = PersonAdapter.isPartialMode();
        String currentMode = isPartialMode ? "โหมดบางส่วน" : "โหมดปกติ";
        textCurrentValidationMode.setText(currentMode);

        // เปลี่ยนสีตามโหมด
        int textColor = isPartialMode ? Color.parseColor("#FF9800") : Color.parseColor("#4CAF50");
        textCurrentValidationMode.setTextColor(textColor);
    }
    private void toggleValidationModeVisibility() {
        if (validationModeContainer.getVisibility() == View.VISIBLE) {
            // ซ่อน
            validationModeContainer.setVisibility(View.GONE);
            validationModeExpandIcon.setImageResource(R.drawable.ic_expand_more_black);

            // Animation สำหรับการหมุนไอคอน
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(validationModeExpandIcon, "rotation", 180f, 0f);
            rotateAnimator.setDuration(200);
            rotateAnimator.start();

        } else {
            // แสดง
            validationModeContainer.setVisibility(View.VISIBLE);
            validationModeExpandIcon.setImageResource(R.drawable.ic_expand_less_black);

            // Animation สำหรับการหมุนไอคอน
            ObjectAnimator rotateAnimator = ObjectAnimator.ofFloat(validationModeExpandIcon, "rotation", 0f, 180f);
            rotateAnimator.setDuration(200);
            rotateAnimator.start();
        }
    }
    private void initializeCounselingInfo() {
        counselingInfoContainer = findViewById(R.id.counselingInfoContainer);
        textCounselingType = findViewById(R.id.textCounselingType);
        textCounselingDetail = findViewById(R.id.textCounselingDetail);
        setupValidationModeCollapsible();
    }
    private void setupValidationModeCollapsible() {
        validationModeHeader = findViewById(R.id.validationModeHeader);
        validationModeContainer = findViewById(R.id.validationModeContainer);
        validationModeExpandIcon = findViewById(R.id.validationModeExpandIcon);
        textCurrentValidationMode = findViewById(R.id.textCurrentValidationMode);

        // ตั้งค่าการคลิกเพื่อขยาย/ย่อ
        validationModeHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleValidationModeVisibility();
            }
        });

        // ตั้งค่าเริ่มต้น
        updateValidationModeDisplay();
    }
    private void checkSendStatus() {
        if (this.personInfo != null) {
            if (this.personInfo.getSend_to_claim().equals(1)) {
                btnOk.setEnabled(false);
            } else {
                btnOk.setEnabled(true);
            }
        }
    }
    private void setupExpandableListViewListeners() {
        expandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) -> {
            String category = categoryList.get(groupPosition);
            String subcategory = subcategoryMap.get(category).get(childPosition);
            showFormDialog(subcategory);
            return true;
        });

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                adjustExpandableListViewHeight();
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                adjustExpandableListViewHeight();
            }
        });
    }
    private void setupButtons() {
        btnOk = findViewById(R.id.btnOK);
        btnReCreateTable = findViewById(R.id.btnReCreateTable);
        btnCancel = findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (personInfo != null) {
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
                            if (personInfo.getId() != null) {
                                checkExistingData(personInfo.getId());
                            }

                            Toast.makeText(getBaseContext(), "บันทึกข้อมูลแล้ว", Toast.LENGTH_SHORT).show();
                        }
                    }
                    } catch(Exception e){
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
    }

    // แยกการขยายรายการออกมาเป็นเมธอดแยก
    private void expandAllGroups() {
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }
    }

    public void handleSubstanceUseChange(boolean currentTobaccoUse, boolean currentAlcoholUse) {
        // ข้ามการตรวจสอบในการโหลดครั้งแรก
        if (isInitialLoad) {
            previousTobaccoUse = currentTobaccoUse;
            previousAlcoholUse = currentAlcoholUse;
            isInitialLoad = false;
            return;
        }

        boolean tobaccoChanged = (previousTobaccoUse != currentTobaccoUse);
        boolean alcoholChanged = (previousAlcoholUse != currentAlcoholUse);

        // แจ้งเตือนเมื่อมีการเปลี่ยนแปลง
        if (tobaccoChanged || alcoholChanged) {
            showSubstanceChangeDialog(
                    tobaccoChanged, alcoholChanged,
                    previousTobaccoUse, previousAlcoholUse,
                    currentTobaccoUse, currentAlcoholUse
            );
        }

        // อัปเดตสถานะ
        previousTobaccoUse = currentTobaccoUse;
        previousAlcoholUse = currentAlcoholUse;
        hasTobaccoUse = currentTobaccoUse;
        hasAlcoholUse = currentAlcoholUse;
    }
    private void showSubstanceChangeDialog(boolean tobaccoChanged, boolean alcoholChanged,
                                           boolean prevTobacco, boolean prevAlcohol,
                                           boolean currTobacco, boolean currAlcohol) {

        StringBuilder message = new StringBuilder();
        message.append("🔄 ตรวจพบการเปลี่ยนแปลงคำตอบ\n\n");

        if (tobaccoChanged) {
            if (prevTobacco && !currTobacco) {
                message.append("🚬 ยาสูบ: เปลี่ยนจาก \"เคย\" เป็น \"ไม่เคย\"\n");
                message.append("   → แบบประเมินการสูบบุหรี่จะถูกซ่อน\n");
                message.append("   → ข้อมูลที่เคยกรอกจะยังคงอยู่\n\n");
            } else if (!prevTobacco && currTobacco) {
                message.append("🚬 ยาสูบ: เปลี่ยนจาก \"ไม่เคย\" เป็น \"เคย\"\n");
                message.append("   → แบบประเมินการสูบบุหรี่จะปรากฏขึ้น\n");
                message.append("   → แนะนำให้ทำแบบประเมินเพิ่มเติม\n\n");
            }
        }

        if (alcoholChanged) {
            if (prevAlcohol && !currAlcohol) {
                message.append("🍺 แอลกอฮอล์: เปลี่ยนจาก \"เคย\" เป็น \"ไม่เคย\"\n");
                message.append("   → แบบประเมินการดื่มสุราจะถูกซ่อน\n");
                message.append("   → ข้อมูลที่เคยกรอกจะยังคงอยู่\n\n");
            } else if (!prevAlcohol && currAlcohol) {
                message.append("🍺 แอลกอฮอล์: เปลี่ยนจาก \"ไม่เคย\" เป็น \"เคย\"\n");
                message.append("   → แบบประเมินการดื่มสุราจะปรากฏขึ้น\n");
                message.append("   → แนะนำให้ทำแบบประเมินเพิ่มเติม\n\n");
            }
        }

        message.append("📝 หมายเหตุ: การเปลี่ยนแปลงนี้จะมีผลทันที\n");
        message.append("ข้อมูลที่เคยกรอกจะไม่หายไป และสามารถเข้าถึงได้\n");
        message.append("เมื่อเปลี่ยนคำตอบกลับมาเป็น \"เคย\" อีกครั้ง");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // สร้าง custom title
        View titleView = createChangeNotificationTitleView();

        builder.setCustomTitle(titleView)
                .setMessage(message.toString())
                .setPositiveButton("รับทราบ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // อัปเดตเมนูทันที
                        updateMenuAfterSubstanceChange();
                    }
                })
                .setNeutralButton("ดูรายการแบบประเมิน", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        updateMenuAfterSubstanceChange();
                        // เลื่อนไปยังส่วนรายการแบบประเมิน
                        scrollToScreeningList();
                    }
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                if (positiveButton != null) {
                    positiveButton.setTextColor(getResources().getColor(R.color.primary_color));
                    positiveButton.setTypeface(null, Typeface.BOLD);
                }

                if (neutralButton != null) {
                    neutralButton.setTextColor(getResources().getColor(R.color.accent_color));
                }
            }
        });

        dialog.show();
    }
    private View createChangeNotificationTitleView() {
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(24, 16, 24, 16);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setBackgroundColor(Color.parseColor("#E3F2FD")); // น้ำเงินอ่อน

        // เพิ่มไอคอนแจ้งเตือน
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.drawable.ic_sync);
        iconView.setColorFilter(Color.parseColor("#1976D2")); // น้ำเงิน
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24)
        );
        iconParams.setMargins(0, 0, dpToPx(12), 0);
        titleLayout.addView(iconView, iconParams);

        // เพิ่ม TextView สำหรับ title
        TextView titleTextView = new TextView(this);
        titleTextView.setText("การเปลี่ยนแปลงคำตอบ");
        titleTextView.setTextColor(Color.parseColor("#1976D2")); // น้ำเงิน
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleLayout.addView(titleTextView);

        return titleLayout;
    }
    private void scrollToScreeningList() {
        if (expandableListView != null) {
            // เลื่อนไปยัง section การคัดกรองสารเสพติด
            expandableListView.smoothScrollToPosition(0);

            // เน้น section ที่เกี่ยวข้อง
            new Handler().postDelayed(() -> {
                View firstGroupView = expandableListView.getChildAt(0);
                if (firstGroupView != null) {
                    // เอฟเฟกต์กะพริบเบาๆ
                    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(firstGroupView, "alpha", 1f, 0.3f);
                    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(firstGroupView, "alpha", 0.3f, 1f);

                    fadeOut.setDuration(200);
                    fadeIn.setDuration(200);

                    fadeOut.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            fadeIn.start();
                        }
                    });

                    fadeOut.start();
                }
            }, 500);
        }
    }
    private void checkSubstanceUseAndUpdateMenu() {
        String personId = getIntent().getStringExtra("person_id");
        if (personId != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.valueOf(personId));

            // รีเซ็ตค่าเริ่มต้น
            hasTobaccoUse = false;
            hasAlcoholUse = false;

            // ตรวจสอบการใช้ยาสูบ (a) และเครื่องดื่มแอลกอฮอล์ (b)
            for (DrugsInfo drug : drugsInfos) {
                if (drug.getQuestion().equals("Q1")) {
                    if (drug.getSubquestion().equals("a")) { // ผลิตภัณฑ์ยาสูบ
                        hasTobaccoUse = "1".equals(drug.getAnswer());
                        System.out.println("Found tobacco use data: " + hasTobaccoUse);
                    } else if (drug.getSubquestion().equals("b")) { // เครื่องดื่มแอลกอฮอล์
                        hasAlcoholUse = "1".equals(drug.getAnswer());
                        System.out.println("Found alcohol use data: " + hasAlcoholUse);
                    }
                }
            }

            // ตั้งค่าให้ previousTobaccoUse และ previousAlcoholUse
            previousTobaccoUse = hasTobaccoUse;
            previousAlcoholUse = hasAlcoholUse;
            isInitialLoad = false; // ตั้งค่าให้เป็น false หลังจากโหลดข้อมูลเสร็จ

            System.out.println("checkSubstanceUseAndUpdateMenu - Tobacco: " + hasTobaccoUse + ", Alcohol: " + hasAlcoholUse);
        }
    }
    private void updateMenuAfterSubstanceChange() {
        // เตรียมข้อมูลเมนูใหม่
        prepareListData();

        // อัปเดต adapter
        expandableListAdapter = new ScreeningExpandableListAdapter(this, categoryList, subcategoryMap);
        expandableListView.setAdapter(expandableListAdapter);

        // ขยายรายการทั้งหมดอีกครั้ง
        for (int i = 0; i < expandableListAdapter.getGroupCount(); i++) {
            expandableListView.expandGroup(i);
        }

        // ตรวจสอบสถานะการกรอกข้อมูลใหม่
        if (personInfo != null && personInfo.getId() != null) {
            checkExistingData(personInfo.getId());
        }

        // แสดงข้อความแจ้งให้ทราบ
        showQuickToast("🔄 รายการแบบประเมินได้รับการอัปเดตแล้ว");
    }
    private void showQuickToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
    private void updateMenuVisibility() {
        if (expandableListAdapter != null) {
            ((ScreeningExpandableListAdapter) expandableListAdapter).updateMenuVisibility(hasTobaccoUse, hasAlcoholUse);
        }
    }
    public void updatePersonDataAfterFormSave() {
        updatePersonDataFromCurrentInfo();
    }

    // เพิ่มเมธอดใหม่สำหรับแสดง Dialog
    private void showFormDialog(String formName) {
        // ตรวจสอบเงื่อนไขการใช้สารเสพติดก่อนแสดงฟอร์ม
        if (!shouldShowForm(formName)) {
            String message = getFormRestrictionMessage(formName);
            showRestrictionDialog(message);
            return;
        }
        if (!validateAgeForAssessment(formName)) {
            return; // หยุดการทำงานถ้าอายุไม่อยู่ในเกณฑ์
        }
        Fragment fragment = fragmentMap.get(formName);
        if (fragment != null) {
            if (this.personInfo != null) {
                FormDialogFragment dialogFragment = FormDialogFragment.newInstance(formName, fragment, this.personInfo.getSend_to_claim());
                dialogFragment.show(getSupportFragmentManager(), "FormDialog");
            } else {
                Toast.makeText(this, "โปรดกรอกข้อมูลผู้ส่วนตัวก่อนทำแบบคัดกรอง", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFormRestrictionMessage(String formName) {
        if (formName.contains("สูบบุหรี่") || formName.contains("ติดบุหรี่")) {
            return "ไม่สามารถทำแบบประเมิน \"" + formName + "\" ได้\n\n" +
                    "เนื่องจากท่านเลือก \"ไม่เคย\" ใช้ผลิตภัณฑ์ยาสูบ\n" +
                    "ในแบบคัดกรองการใช้สารเสพติด (คำถามที่ 1)\n\n" +
                    "หากต้องการทำแบบประเมินนี้ กรุณาแก้ไขคำตอบ\n" +
                    "ในแบบคัดกรองการใช้สารเสพติดก่อน";
        }

        if (formName.contains("สุรา") || formName.contains("แอลกอฮอล์")) {
            return "ไม่สามารถทำแบบประเมิน \"" + formName + "\" ได้\n\n" +
                    "เนื่องจากท่านเลือก \"ไม่เคย\" ดื่มเครื่องดื่มแอลกอฮอล์\n" +
                    "ในแบบคัดกรองการใช้สารเสพติด (คำถามที่ 1)\n\n" +
                    "หากต้องการทำแบบประเมินนี้ กรุณาแก้ไขคำตอบ\n" +
                    "ในแบบคัดกรองการใช้สารเสพติดก่อน";
        }

        return "ไม่สามารถเข้าถึงแบบประเมินนี้ได้ในขณะนี้";
    }
    private boolean shouldShowForm(String formName) {
        // ตรวจสอบการใช้ยาสูบ
        if (formName.contains("สูบบุหรี่") || formName.contains("ติดบุหรี่")) {
            return hasTobaccoUse;
        }

        // ตรวจสอบการดื่มแอลกอฮอล์
        if (formName.contains("สุรา") || formName.contains("แอลกอฮอล์")) {
            return hasAlcoholUse;
        }

        return true; // แสดงฟอร์มอื่นๆ ปกติ
    }
    private void showRestrictionDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // สร้าง custom title view
        View titleView = createRestrictionTitleView();

        builder.setCustomTitle(titleView)
                .setMessage(message)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("แก้ไขคำตอบ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // เปิดแบบคัดกรองการใช้สารเสพติดเพื่อให้แก้ไขคำตอบ
                        showFormDialog("แบบคัดกรองการใช้สารเสพติด");
                    }
                })
                .setCancelable(true);

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);

                if (positiveButton != null) {
                    positiveButton.setTextColor(Color.parseColor("#2196F3"));
                }

                if (neutralButton != null) {
                    neutralButton.setTextColor(Color.parseColor("#FF9800"));
                    neutralButton.setTypeface(null, Typeface.BOLD);
                }
            }
        });

        dialog.show();
    }
    private View createRestrictionTitleView() {
        LinearLayout titleLayout = new LinearLayout(getBaseContext());
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(24, 16, 24, 16);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setBackgroundColor(Color.parseColor("#FFF3E0")); // ส้มอ่อน

        // เพิ่มไอคอนแจ้งเตือน
        ImageView iconView = new ImageView(getBaseContext());
        iconView.setImageResource(R.drawable.ic_info);
        iconView.setColorFilter(Color.parseColor("#FF9800")); // ส้ม
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24)
        );
        iconParams.setMargins(0, 0, dpToPx(12), 0);
        titleLayout.addView(iconView, iconParams);

        // เพิ่ม TextView สำหรับ title
        TextView titleTextView = new TextView(getBaseContext());
        titleTextView.setText("ไม่สามารถเข้าถึงแบบประเมินได้");
        titleTextView.setTextColor(Color.parseColor("#FF9800")); // ส้ม
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleLayout.addView(titleTextView);

        return titleLayout;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
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
            // ตั้งค่า ViewModels
            SharedViewModel viewModel = new ViewModelProvider(this).get(SharedViewModel.class);
            QuestionsStateViewModel questionsStateViewModel = new ViewModelProvider(this).get(QuestionsStateViewModel.class);
            PersonInfoLiveData personInfoLiveData = new PersonInfoLiveData();
            personInfoLiveData.setId(personId);
            personInfoLiveData.setVisitId(visitId);
            viewModel.setPersonInfoLiveDataMutableLiveData(personInfoLiveData);

            // ตั้งค่า LiveData อื่นๆ...
            SmookingLiveData smookingLiveData = new SmookingLiveData();
            smookingLiveData.setPersonId(personId);
            smookingLiveData.setVisitId(visitId);
            viewModel.setSmookingMutableLiveData(smookingLiveData);

            CigaretteAddictionTestLiveData cigaretteAddictionTestLiveData = new CigaretteAddictionTestLiveData();
            cigaretteAddictionTestLiveData.setPersonId(personId);
            cigaretteAddictionTestLiveData.setVisitId(visitId);
            viewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTestLiveData);

            StressDepressionLiveData stressDepressionLiveData = new StressDepressionLiveData();
            stressDepressionLiveData.setPersonId(personId);
            stressDepressionLiveData.setVisitId(visitId);
            viewModel.setStressDepressionLiveDataMutableLiveData(stressDepressionLiveData);

            StressDepression2qLiveData stressDepression2qLiveData = new StressDepression2qLiveData();
            stressDepression2qLiveData.setPersonId(personId);
            stressDepression2qLiveData.setVisitId(visitId);
            viewModel.setStressDepression2qLiveDataModelMutableLiveData(stressDepression2qLiveData);

            StressDepression9qLiveData stressDepression9qLiveData = new StressDepression9qLiveData();
            stressDepression9qLiveData.setPersonId(personId);
            stressDepression9qLiveData.setVisitId(visitId);
            viewModel.setStressDepression9qLiveDataModelMutableLiveData(stressDepression9qLiveData);

            SuicideAssessment8qLiveData suicideAssessment8qLiveData = new SuicideAssessment8qLiveData();
            suicideAssessment8qLiveData.setPersonId(personId);
            suicideAssessment8qLiveData.setVisitId(visitId);
            viewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);

            HealthRiskAssessmentLiveData healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
            healthRiskAssessmentLiveData.setPersonId(personId);
            healthRiskAssessmentLiveData.setVisitId(visitId);
            viewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

            CardiovascularRiskLiveData cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
            cardiovascularRiskLiveData.setPersonId(personId);
            cardiovascularRiskLiveData.setVisitId(visitId);
            viewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);

            DrugsLiveData drugsLiveData = new DrugsLiveData();
            drugsLiveData.setPersonId(personId);
            drugsLiveData.setVisitId(visitId);
            viewModel.setDrugsLiveDataMutableLiveData(drugsLiveData);

            CounselingLiveData counselingLiveData = new CounselingLiveData();
            counselingLiveData.setPersonId(personId);
            counselingLiveData.setVisitId(visitId);
            viewModel.setCounselingLiveData(counselingLiveData);

            // แก้ไข: ลบการเรียก checkExistingData และ checkSubstanceUseAndUpdateMenu ออกจากที่นี่
            // เพราะจะเรียกใน Handler แทน
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

        loadExistingCounselingData();

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
//        addictionScreening.add("สรุปคะแนนแบบคัดกรอง ASSIST");

        // เพิ่มเมนูการสูบบุหรี่เฉพาะเมื่อมีการใช้ยาสูบ
        if (hasTobaccoUse) {
            addictionScreening.add("คัดกรองความเสี่ยงจากการสูบบุหรี่");
            addictionScreening.add("แบบทดสอบการติดบุหรี่");
        }

        // เพิ่มเมนูการดื่มสุราเฉพาะเมื่อมีการดื่มแอลกอฮอล์
        if (hasAlcoholUse) {
            addictionScreening.add("คัดกรองความเสี่ยงจากการดื่มสุรา");
        }

        subcategoryMap.put("การคัดกรองสารเสพติด", addictionScreening);

        // 2. หมวดหมู่ ภาวะเครียด-ซึมเศร้า (คงเดิม)
        List<String> mentalHealth = new ArrayList<>();
        mentalHealth.add("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)");
        mentalHealth.add("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)");
        mentalHealth.add("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)");
        mentalHealth.add("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)");
        subcategoryMap.put("ภาวะเครียด-ซึมเศร้า", mentalHealth);

        // 3. หมวดหมู่ ความเสี่ยงด้านสุขภาพ (คงเดิม)
        List<String> healthRisks = new ArrayList<>();
        healthRisks.add("แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน");
        healthRisks.add("คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด");
        subcategoryMap.put("ความเสี่ยงด้านสุขภาพ", healthRisks);

        // 4. หมวดหมู่ สรุปการคัดกรอง (คงเดิม)
        List<String> sfSummary = new ArrayList<>();
        sfSummary.add("ให้คำปรึกษาและแนะนำ");
        subcategoryMap.put("สรุปผลการคัดกรอง", sfSummary);

        // เพิ่ม Fragment ที่เกี่ยวข้องทั้งหมด
        fragmentMap.put("แบบคัดกรองการใช้สารเสพติด", new MainQuestionsFragment());
        fragmentMap.put("สรุปคะแนนแบบคัดกรอง ASSIST", new AssistScoreFragment());

        // เพิ่ม Fragment เฉพาะเมื่อมีการใช้ยาสูบ
        if (hasTobaccoUse) {
            fragmentMap.put("คัดกรองความเสี่ยงจากการสูบบุหรี่", new SmookingFragment());
            fragmentMap.put("แบบทดสอบการติดบุหรี่", new FagerstromNicotineFragment());
        }

        // เพิ่ม Fragment เฉพาะเมื่อมีการดื่มแอลกอฮอล์
        if (hasAlcoholUse) {
            fragmentMap.put("คัดกรองความเสี่ยงจากการดื่มสุรา", new AlcoholFragment());
        }

        // ภาวะเครียด-ซึมเศร้า (คงเดิม)
        fragmentMap.put("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", new StressDepressionFragment());
        fragmentMap.put("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", new StressDepression2qFragment());
        fragmentMap.put("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", new StressDepression9qFragment());
        fragmentMap.put("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", new SuicideAssessment8qFragment());

        // ความเสี่ยงด้านสุขภาพ (คงเดิม)
        fragmentMap.put("แบบประเมินความเสี่ยงการเกิดโรคเบาหวาน", new HealthRiskAssessmentFragment());
        fragmentMap.put("คัดกรองความเสี่ยงโรคหัวใจและหลอดเลือด", new CardiovascularRiskFragment());

        // สรุปการคัดกรอง (คงเดิม)
        fragmentMap.put("ให้คำปรึกษาและแนะนำ", new CounselingSignFragment());
    }

    public void updateFormStatus(String formName, boolean status) {
        if (expandableListAdapter != null) {
            ((ScreeningExpandableListAdapter) expandableListAdapter).updateCompletionStatus(formName, status);
        }
    }

    private String savePerson() {
        SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(mContext);
        UserSessionManager userSessionManager = new UserSessionManager(mContext);
        if(this.personInfo == null) {return "";}
        if (this.personInfo.getId() == null || Objects.equals(this.personInfo.getId(), "")){
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
                if(drugsInfo.getQuestion()!=null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
    }

    private void saveDrugsThree() {
        if (this.drugsThreeInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsThreeInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if(drugsInfo.getQuestion()!=null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
    }

    private void saveDrugsFour() {
        if (this.drugsFourInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsFourInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if(drugsInfo.getQuestion()!=null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
    }

    private void saveDrugsFive() {
        if (this.drugsFiveInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsFiveInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getQuestion() != null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
    }

    private void saveDrugsSix() {
        if (this.drugsSixInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsSixInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if(drugsInfo.getQuestion()!=null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
    }

    private void saveDrugsSeven() {
        if (this.drugsSevenInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsSevenInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if (drugsInfo.getQuestion() != null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
    }

    private void saveDrugsEight() {
        if (this.drugsEightInfos != null) {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            for (DrugsInfo drugsInfo : this.drugsEightInfos) {
                // กำหนดค่าที่จำเป็น
                drugsInfo.setIdcard(this.personInfo.getIdcard());
                drugsInfo.setPersonInfoId(this.personInfo.getId());
                if(drugsInfo.getQuestion()!=null) {
                    if (drugsInfo.getId() == null || drugsInfo.getId().isEmpty()) {
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
                    // อัพเดทการแสดงผล
                    updateCounselingDisplay(counselingInfo);
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
                    // อัพเดทการแสดงผล
                    updateCounselingDisplay(counselingInfo);
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

        updatePersonAgeInfo();

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

    public boolean isPersonAgeValid() {
        return isPersonAgeValid;
    }

    public int getPersonAge() {
        return personAge;
    }

    public String getPersonAgeGroup() {
        return personAgeGroup;
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
//        if(this.suicideAssessment8qInfo==null){
            this.suicideAssessment8qInfo = data;
//        }

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
         this.healthRiskAssessmentInfo = data;

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

        // ตรวจสอบการเปลี่ยนแปลงในการใช้สารเสพติดเฉพาะเมื่อไม่ใช่การโหลดครั้งแรก
        if (!isInitialLoad) {
            boolean currentTobaccoUse = false;
            boolean currentAlcoholUse = false;

            for (DrugsInfo drug : data) {
                if (drug.getQuestion().equals("Q1")) {
                    if (drug.getSubquestion().equals("a")) { // ผลิตภัณฑ์ยาสูบ
                        currentTobaccoUse = "1".equals(drug.getAnswer());
                    } else if (drug.getSubquestion().equals("b")) { // เครื่องดื่มแอลกอฮอล์
                        currentAlcoholUse = "1".equals(drug.getAnswer());
                    }
                }
            }

            // เรียกใช้ handleSubstanceUseChange แทนการจัดการเอง
            handleSubstanceUseChange(currentTobaccoUse, currentAlcoholUse);
        }

        displayData(data);
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

        // อัพเดทการแสดงผลในหน้าจอหลัก
        updateCounselingDisplay(counselingInfo);

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
    private void updateCounselingDisplay(CounselingInfo counselingInfo) {
        if (counselingInfo == null) {
            counselingInfoContainer.setVisibility(View.GONE);
            return;
        }

        // ตรวจสอบว่ามีข้อมูลการให้คำปรึกษาหรือไม่
        boolean hasCounselingData = (counselingInfo.getCounselingType() > 0) ||
                (counselingInfo.getDetail() != null && !counselingInfo.getDetail().trim().isEmpty()) ||
                (counselingInfo.getReferralDetail() != null && !counselingInfo.getReferralDetail().trim().isEmpty());

        if (hasCounselingData) {
            counselingInfoContainer.setVisibility(View.VISIBLE);

            // แสดงประเภทการให้คำปรึกษา
            String counselingTypeText = "";
            String detailText = "";

            if (counselingInfo.getCounselingType() == 1) {
                counselingTypeText = "ให้คำแนะนำ";
                detailText = counselingInfo.getDetail() != null ? counselingInfo.getDetail() : "-";
            } else if (counselingInfo.getCounselingType() == 2) {
                counselingTypeText = "ส่งต่อแพทย์/รับบริการตามสิทธิ";
                detailText = counselingInfo.getReferralDetail() != null ? counselingInfo.getReferralDetail() : "-";
            } else {
                counselingTypeText = "-";
                detailText = "-";
            }

            textCounselingType.setText(counselingTypeText);
            textCounselingDetail.setText(detailText);

            System.out.println("Updated counseling display: " + counselingTypeText + " - " + detailText);
        } else {
            counselingInfoContainer.setVisibility(View.GONE);
        }
    }

    // เพิ่มเมธอดสำหรับโหลดข้อมูลการให้คำปรึกษาที่มีอยู่แล้ว
    private void loadExistingCounselingData() {
        if (personInfo != null && personInfo.getVisitId() != null && !personInfo.getVisitId().isEmpty()) {
            CounselingSignatureDao counselingDao = new CounselingSignatureDao(mContext);
            List<CounselingInfo> existingCounseling = counselingDao.getCounselingByVisitId(personInfo.getVisitId());

            if (!existingCounseling.isEmpty()) {
                CounselingInfo counseling = existingCounseling.get(0);
                updateCounselingDisplay(counseling);
                this.counselingInfo = counseling;
                System.out.println("Loaded existing counseling data for visitId: " + personInfo.getVisitId());
            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Intent encrypter = new Intent(getBaseContext(), CryptographerService.class);
//        encrypter.setAction(Action.ENCRYPT);
//        startService(encrypter);
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("PersonScreeningForm15Activity", "onResume - Refreshing data like first time");

        // โหลดข้อมูลใหม่เหมือนตอนเปิดหน้าจอครั้งแรก
        refreshAllDataOnResume();
    }

    /**
     * รีเฟรชข้อมูลทั้งหมดเหมือนตอนเปิดหน้าจอครั้งแรก
     */
    private void refreshAllDataOnResume() {
        try {
            Log.d("PersonScreeningForm15Activity", "เริ่มรีเฟรชข้อมูลทั้งหมด");

            // 1. รีเซ็ตสถานะต่างๆ
            resetInitialStates();

            // 2. โหลดข้อมูลบุคคลใหม่
            reloadPersonInfoData();

            // 3. ตรวจสอบการใช้สารเสพติดและอัพเดตเมนู
            new Handler().postDelayed(() -> {
                checkSubstanceUseAndUpdateMenu();
                prepareListData();
                updateExpandableListAdapter();

                // 4. ตรวจสอบข้อมูลที่มีอยู่แล้ว
                if (personInfo != null && personInfo.getId() != null) {
                    checkExistingData(personInfo.getId());
                }

                // 5. รีเฟรชการแสดงผลข้อมูลที่เกี่ยวข้อง
                refreshDisplayData();

                Log.d("PersonScreeningForm15Activity", "รีเฟรชข้อมูลเสร็จสิ้น");

            }, 500); // หน่วงเวลาเล็กน้อยเพื่อให้ UI พร้อม

        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการรีเฟรชข้อมูล: " + e.getMessage());
        }
    }

    /**
     * รีเซ็ตสถานะเริ่มต้นต่างๆ
     */
    private void resetInitialStates() {
        // รีเซ็ตสถานะการโหลดครั้งแรก
        isInitialLoad = true;
        isUpdatingPersonData = false;

        // รีเซ็ตสถานะการใช้สารเสพติด
        previousTobaccoUse = false;
        previousAlcoholUse = false;
        hasTobaccoUse = false;
        hasAlcoholUse = false;

        // รีเซ็ตสถานะอายุ
        isPersonAgeValid = false;
        personAge = 0;
        personAgeGroup = "";

        Log.d("PersonScreeningForm15Activity", "รีเซ็ตสถานะเริ่มต้นเสร็จสิ้น");
    }

    /**
     * โหลดข้อมูลบุคคลใหม่
     */
    private void reloadPersonInfoData() {
        String personId = getIntent().getStringExtra("person_id");
        String visitId = getIntent().getStringExtra("visit_id");

        if (personId != null) {
            // โหลดข้อมูลจากฐานข้อมูลใหม่
            reloadPersonInfoFromDatabase(personId);

            // ตั้งค่า ViewModels ใหม่
            setupViewModelsAgain(personId, visitId);

            Log.d("PersonScreeningForm15Activity", "โหลดข้อมูลบุคคลใหม่สำหรับ ID: " + personId);
        }
    }

    /**
     * โหลดข้อมูลบุคคลจากฐานข้อมูลใหม่
     */
    private void reloadPersonInfoFromDatabase(String personId) {
        try {
            SfPersonInfoDao sfPersonInfoDao = new SfPersonInfoDao(mContext);
            List<PersonInfo> personInfos = sfPersonInfoDao.getSfPersonInfoById(Integer.parseInt(personId));

            if (!personInfos.isEmpty()) {
                this.personInfo = personInfos.get(0);

                // อัพเดตข้อมูลอายุ
                updatePersonAgeInfo();

                // อัพเดตข้อมูลใน SharedViewModel
                updatePersonDataFromCurrentInfo();

                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูลบุคคลจากฐานข้อมูลสำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูลบุคคล: " + e.getMessage());
        }
    }

    /**
     * ตั้งค่า ViewModels ใหม่
     */
    private void setupViewModelsAgain(String personId, String visitId) {
        if (sharedViewModel != null) {
            // ตั้งค่า PersonInfoLiveData ใหม่
            PersonInfoLiveData personInfoLiveData = new PersonInfoLiveData();
            personInfoLiveData.setId(personId);
            personInfoLiveData.setVisitId(visitId);
            sharedViewModel.setPersonInfoLiveDataMutableLiveData(personInfoLiveData);

            // ตั้งค่า LiveData อื่นๆ ใหม่
            setupOtherLiveDataAgain(personId, visitId);

            Log.d("PersonScreeningForm15Activity", "ตั้งค่า ViewModels ใหม่เสร็จสิ้น");
        }
    }

    /**
     * ตั้งค่า LiveData อื่นๆ ใหม่
     */
    private void setupOtherLiveDataAgain(String personId, String visitId) {
        // SmookingLiveData
        SmookingLiveData smookingLiveData = new SmookingLiveData();
        smookingLiveData.setPersonId(personId);
        smookingLiveData.setVisitId(visitId);
        sharedViewModel.setSmookingMutableLiveData(smookingLiveData);

        // CigaretteAddictionTestLiveData
        CigaretteAddictionTestLiveData cigaretteAddictionTestLiveData = new CigaretteAddictionTestLiveData();
        cigaretteAddictionTestLiveData.setPersonId(personId);
        cigaretteAddictionTestLiveData.setVisitId(visitId);
        sharedViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTestLiveData);

        // StressDepressionLiveData
        StressDepressionLiveData stressDepressionLiveData = new StressDepressionLiveData();
        stressDepressionLiveData.setPersonId(personId);
        stressDepressionLiveData.setVisitId(visitId);
        sharedViewModel.setStressDepressionLiveDataMutableLiveData(stressDepressionLiveData);

        // StressDepression2qLiveData
        StressDepression2qLiveData stressDepression2qLiveData = new StressDepression2qLiveData();
        stressDepression2qLiveData.setPersonId(personId);
        stressDepression2qLiveData.setVisitId(visitId);
        sharedViewModel.setStressDepression2qLiveDataModelMutableLiveData(stressDepression2qLiveData);

        // StressDepression9qLiveData
        StressDepression9qLiveData stressDepression9qLiveData = new StressDepression9qLiveData();
        stressDepression9qLiveData.setPersonId(personId);
        stressDepression9qLiveData.setVisitId(visitId);
        sharedViewModel.setStressDepression9qLiveDataModelMutableLiveData(stressDepression9qLiveData);

        // SuicideAssessment8qLiveData
        SuicideAssessment8qLiveData suicideAssessment8qLiveData = new SuicideAssessment8qLiveData();
        suicideAssessment8qLiveData.setPersonId(personId);
        suicideAssessment8qLiveData.setVisitId(visitId);
        sharedViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);

        // HealthRiskAssessmentLiveData
        HealthRiskAssessmentLiveData healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
        healthRiskAssessmentLiveData.setPersonId(personId);
        healthRiskAssessmentLiveData.setVisitId(visitId);
        sharedViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

        // CardiovascularRiskLiveData
        CardiovascularRiskLiveData cardiovascularRiskLiveData = new CardiovascularRiskLiveData();
        cardiovascularRiskLiveData.setPersonId(personId);
        cardiovascularRiskLiveData.setVisitId(visitId);
        sharedViewModel.setCardiovascularRiskLiveDataMutableLiveData(cardiovascularRiskLiveData);

        // DrugsLiveData
        DrugsLiveData drugsLiveData = new DrugsLiveData();
        drugsLiveData.setPersonId(personId);
        drugsLiveData.setVisitId(visitId);
        sharedViewModel.setDrugsLiveDataMutableLiveData(drugsLiveData);

        // CounselingLiveData
        CounselingLiveData counselingLiveData = new CounselingLiveData();
        counselingLiveData.setPersonId(personId);
        counselingLiveData.setVisitId(visitId);
        sharedViewModel.setCounselingLiveData(counselingLiveData);
    }

    /**
     * อัพเดต ExpandableListAdapter
     */
    private void updateExpandableListAdapter() {
        if (expandableListView != null) {
            expandableListAdapter = new ScreeningExpandableListAdapter(
                    PersonScreeningForm15Activity.this,
                    categoryList,
                    subcategoryMap
            );
            expandableListView.setAdapter(expandableListAdapter);

            // ตั้งค่า listeners ใหม่
            setupExpandableListViewListeners();

            // ขยายรายการทั้งหมดแบบอัตโนมัติ
            expandAllGroups();

            Log.d("PersonScreeningForm15Activity", "อัพเดต ExpandableListAdapter เสร็จสิ้น");
        }
    }

    /**
     * รีเฟรชการแสดงผลข้อมูลต่างๆ
     */
    private void refreshDisplayData() {
        // รีเฟรชการแสดงผลข้อมูลการให้คำปรึกษา
        loadExistingCounselingData();

        // รีเฟรชการแสดงผลโหมด validation
        updateValidationModeDisplay();

        // ตรวจสอบสถานะการส่งข้อมูล
        checkSendStatus();

        // อัพเดตการแสดงผลข้อมูลบุคคลในส่วนหัว
        refreshPersonInfoDisplay();

        Log.d("PersonScreeningForm15Activity", "รีเฟรชการแสดงผลข้อมูลเสร็จสิ้น");
    }

    /**
     * รีเฟรชการแสดงผลข้อมูลบุคคลในส่วนหัว
     */
    private void refreshPersonInfoDisplay() {
        if (personInfo != null) {
            // อัพเดตข้อมูลที่แสดงในส่วนหัว (ถ้ามี)
            // สามารถเพิ่มการอัพเดต UI อื่นๆ ได้ที่นี่

            // ตัวอย่าง: อัพเดตชื่อในหัวเรื่อง
            String title = "แบบคัดกรองสุขภาพ";
            if (personInfo.getFname() != null && personInfo.getLname() != null) {
                title += " - " + personInfo.getFname() + " " + personInfo.getLname();
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(title);
            }

            Log.d("PersonScreeningForm15Activity", "รีเฟรชการแสดงผลข้อมูลบุคคลเสร็จสิ้น");
        }
    }

    /**
     * โหลดข้อมูลต่างๆ จากฐานข้อมูลใหม่
     */
    private void reloadAllRelatedData(String personId) {
        try {
            Integer iPersonId = Integer.valueOf(personId);

            // โหลดข้อมูลการสูบบุหรี่
            reloadSmokerData(iPersonId);

            // โหลดข้อมูลการดื่มสุรา
            reloadDrinkingData(iPersonId);

            // โหลดข้อมูล Nicotine
            reloadNicotineData(iPersonId);

            // โหลดข้อมูล Stress Depression
            reloadStressDepressionData(iPersonId);

            // โหลดข้อมูล Health Risk Assessment
            reloadHealthRiskAssessmentData(iPersonId);

            // โหลดข้อมูล Cardiovascular Risk
            reloadCardiovascularRiskData(iPersonId);

            // โหลดข้อมูล Drugs
            reloadDrugsData(iPersonId);

            Log.d("PersonScreeningForm15Activity", "โหลดข้อมูลที่เกี่ยวข้องทั้งหมดเสร็จสิ้น");

        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูลที่เกี่ยวข้อง: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูลการสูบบุหรี่ใหม่
     */
    private void reloadSmokerData(Integer personId) {
        try {
            SfSmokerInfoDao sfSmokerInfoDao = new SfSmokerInfoDao(mContext);
            List<SmokerInfo> smokers = sfSmokerInfoDao.getByPersonId(personId);

            if (!smokers.isEmpty()) {
                this.smokerInfo = smokers.get(0);
                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูลการสูบบุหรี่ใหม่สำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูลการสูบบุหรี่: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูลการดื่มสุราใหม่
     */
    private void reloadDrinkingData(Integer personId) {
        try {
            SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(mContext);
            List<DrinkingInfo> drinkings = sfDrinkingInfoDao.getByPersonId(personId);

            if (!drinkings.isEmpty()) {
                this.drinkingInfo = drinkings.get(0);
                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูลการดื่มสุราใหม่สำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูลการดื่มสุรา: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูล Nicotine ใหม่
     */
    private void reloadNicotineData(Integer personId) {
        try {
            SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(mContext);
            List<NicotineInfo> nicotines = sfNicotineInfoDao.getByPersonId(personId);

            if (!nicotines.isEmpty()) {
                this.nicotineInfo = nicotines.get(0);
                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูล Nicotine ใหม่สำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูล Nicotine: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูล Stress Depression ใหม่
     */
    private void reloadStressDepressionData(Integer personId) {
        try {
            // Stress Depression
            SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(mContext);
            List<StressDepressionInfo> stressDepressions = sfStressDepressionInfoDao.getByPersonId(personId);
            if (!stressDepressions.isEmpty()) {
                this.stressDepressionInfo = stressDepressions.get(0);
            }

            // Stress Depression 2Q
            SfStressDepression2qInfoDao sfStressDepression2qInfoDao = new SfStressDepression2qInfoDao(mContext);
            List<StressDepression2qInfo> stressDepression2qs = sfStressDepression2qInfoDao.getByPersonId(personId);
            if (!stressDepression2qs.isEmpty()) {
                this.stressDepression2qInfo = stressDepression2qs.get(0);
            }

            // Stress Depression 9Q
            SfStressDepression9qInfoDao sfStressDepression9qInfoDao = new SfStressDepression9qInfoDao(mContext);
            List<StressDepression9qInfo> stressDepression9qs = sfStressDepression9qInfoDao.getByPersonId(personId);
            if (!stressDepression9qs.isEmpty()) {
                this.stressDepression9qInfo = stressDepression9qs.get(0);
            }

            // Suicide Assessment 8Q
            SfSuicideAssessment8qInfoDao sfSuicideAssessment8qInfoDao = new SfSuicideAssessment8qInfoDao(mContext);
            List<SuicideAssessment8qInfo> suicideAssessment8qs = sfSuicideAssessment8qInfoDao.getByPersonId(personId);
            if (!suicideAssessment8qs.isEmpty()) {
                this.suicideAssessment8qInfo = suicideAssessment8qs.get(0);
            }

            Log.d("PersonScreeningForm15Activity", "โหลดข้อมูล Stress Depression ใหม่สำเร็จ");

        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูล Stress Depression: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูล Health Risk Assessment ใหม่
     */
    private void reloadHealthRiskAssessmentData(Integer personId) {
        try {
            SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(mContext);
            List<HealthRiskAssessmentInfo> healthRisks = sfHealthRiskAssessmentInfoDao.getByPersonId(personId);

            if (!healthRisks.isEmpty()) {
                this.healthRiskAssessmentInfo = healthRisks.get(0);
                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูล Health Risk Assessment ใหม่สำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูล Health Risk Assessment: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูล Cardiovascular Risk ใหม่
     */
    private void reloadCardiovascularRiskData(Integer personId) {
        try {
            SfCardiovascularRiskInfoDao sfCardiovascularRiskInfoDao = new SfCardiovascularRiskInfoDao(mContext);
            List<CardiovascularRiskInfo> cardiovascularRisks = sfCardiovascularRiskInfoDao.getByPersonId(personId);

            if (!cardiovascularRisks.isEmpty()) {
                this.cardiovascularRiskInfo = cardiovascularRisks.get(0);
                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูล Cardiovascular Risk ใหม่สำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูล Cardiovascular Risk: " + e.getMessage());
        }
    }

    /**
     * โหลดข้อมูล Drugs ใหม่
     */
    private void reloadDrugsData(Integer personId) {
        try {
            SfDrugsDao sfDrugsDao = new SfDrugsDao(mContext);
            List<DrugsInfo> drugs = sfDrugsDao.getSfDrugsByPersonInfoId(personId);

            if (!drugs.isEmpty()) {
                // แยกข้อมูล Drugs ตามคำถาม
                this.drugsOneInfos = new ArrayList<>();
                this.drugsTwoInfos = new ArrayList<>();
                this.drugsThreeInfos = new ArrayList<>();
                this.drugsFourInfos = new ArrayList<>();
                this.drugsFiveInfos = new ArrayList<>();
                this.drugsSixInfos = new ArrayList<>();
                this.drugsSevenInfos = new ArrayList<>();
                this.drugsEightInfos = new ArrayList<>();

                for (DrugsInfo drug : drugs) {
                    switch (drug.getQuestion()) {
                        case "Q1":
                            this.drugsOneInfos.add(drug);
                            break;
                        case "Q2":
                            this.drugsTwoInfos.add(drug);
                            break;
                        case "Q3":
                            this.drugsThreeInfos.add(drug);
                            break;
                        case "Q4":
                            this.drugsFourInfos.add(drug);
                            break;
                        case "Q5":
                            this.drugsFiveInfos.add(drug);
                            break;
                        case "Q6":
                            this.drugsSixInfos.add(drug);
                            break;
                        case "Q7":
                            this.drugsSevenInfos.add(drug);
                            break;
                        case "Q8":
                            this.drugsEightInfos.add(drug);
                            break;
                    }
                }

                Log.d("PersonScreeningForm15Activity", "โหลดข้อมูล Drugs ใหม่สำเร็จ");
            }
        } catch (Exception e) {
            Log.e("PersonScreeningForm15Activity", "เกิดข้อผิดพลาดในการโหลดข้อมูล Drugs: " + e.getMessage());
        }
    }

    /**
     * รีเฟรชข้อมูลแบบครบถ้วน (สำหรับเรียกใช้จากภายนอก)
     */
    public void forceRefreshAllData() {
        Log.d("PersonScreeningForm15Activity", "forceRefreshAllData - เริ่มรีเฟรชข้อมูลแบบบังคับ");
        refreshAllDataOnResume();
    }
}