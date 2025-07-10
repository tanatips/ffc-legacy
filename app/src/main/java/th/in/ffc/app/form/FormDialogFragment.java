package th.in.ffc.app.form;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.FagerstromNicotineFragment;
import th.in.ffc.app.form.screening.MainQuestionsFragment;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SmookingFragment;
import th.in.ffc.app.form.screening.StressDepressionFragment;
import th.in.ffc.app.form.screening.StressDepression2qFragment;
import th.in.ffc.app.form.screening.StressDepression9qFragment;
import th.in.ffc.app.form.screening.SuicideAssessment8qFragment;
import th.in.ffc.app.form.screening.HealthRiskAssessmentFragment;
import th.in.ffc.app.form.screening.CardiovascularRiskFragment;
import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.person.PersonScreeningForm15Activity;

public class FormDialogFragment extends DialogFragment {
    private Fragment contentFragment;
    private String formTitle;
    private Integer send_to_claim;
    private OnDataPass dataPassListener;

    private boolean validated = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnDataPass) {
            dataPassListener = (OnDataPass) context;
        } else {
            throw new RuntimeException(context.toString() + " ต้อง implement OnDataPass");
        }
    }

    private void saveFormData() {
        // รวบรวมข้อมูลจาก Fragment ต่างๆ (สามารถเพิ่มเติมได้ตามต้องการ)
    }

    public static FormDialogFragment newInstance(String title, Fragment content,Integer send_to_claim) {
        FormDialogFragment fragment = new FormDialogFragment();
        fragment.formTitle = title;
        fragment.contentFragment = content;
        fragment.send_to_claim = send_to_claim;
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // เพิ่มโค้ดเพื่อให้ Dialog รองรับการแสดง keyboard
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // คุณอาจเพิ่มฟังก์ชันนี้เพื่อป้องกันการปิด dialog เมื่อแตะนอกพื้นที่
        getDialog().setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // ใช้ AlertDialog.Builder สร้าง dialog แบบพื้นฐาน
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        // Inflate layout สำหรับ dialog
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_form, null);

        // สร้าง Spannable text สำหรับ title
//        SpannableString spannableTitle = new SpannableString(formTitle);
//
//        // กำหนดสีตามประเภทของแบบฟอร์ม
//        int titleColor = getTitleColor(formTitle);
//        spannableTitle.setSpan(new ForegroundColorSpan(titleColor), 0, formTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        spannableTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, formTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // สร้าง custom title view
        View customTitleView = createCustomTitleView(inflater);

        // ตั้งค่าหัวเรื่องและ view
        builder.setCustomTitle(customTitleView)
                .setView(view)
                .setPositiveButton("บันทึก", null)
                .setNegativeButton("ยกเลิก",null);

        // สร้าง AlertDialog
        AlertDialog dialog = builder.create();

        // หลังจากสร้าง dialog แล้ว ให้เพิ่ม fragment ลงไป
        dialog.setOnShowListener(dialogInterface -> {
            // ต้องเรียก getChildFragmentManager() เพื่อจัดการ Fragment ใน Dialog
            FrameLayout container = view.findViewById(R.id.dialogFragmentContainer);
            if (contentFragment != null && container != null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.dialogFragmentContainer, contentFragment)
                        .commit();
            }

            // สำคัญ: เข้าถึงปุ่มที่ถูกสร้างโดย AlertDialog และเปลี่ยน listener
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

            // กำหนดการทำงานสำหรับปุ่ม "บันทึก"
            if (positiveButton != null) {
                positiveButton.setOnClickListener(v -> {
                    validateAndSave();
                });
            }

            // กำหนดการทำงานสำหรับปุ่ม "ยกเลิก"
            if (negativeButton != null) {
                negativeButton.setOnClickListener(v -> {
                    dismiss();
                });
            }

            if (send_to_claim != null && send_to_claim == 1) {
                if (positiveButton != null) {
                    positiveButton.setEnabled(false);
                }
            }

            // ตรวจสอบกรณี Fragment ต่างๆ และอัปเดตสถานะ
            if (getActivity() instanceof PersonScreeningForm15Activity) {
                PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();

                // อัปเดตสถานะหลังจากที่ Fragment ถูกโหลดเรียบร้อย
                new Handler().postDelayed(() -> {
                    updateFragmentStatus(activity);
                }, 500); // รอสักครู่เพื่อให้ Fragment ถูกโหลดเรียบร้อย
            }
        });

        return dialog;
    }
    private View createCustomTitleView(LayoutInflater inflater) {
        // สร้าง layout สำหรับ custom title
        LinearLayout titleLayout = new LinearLayout(getContext());
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(24, 16, 24, 16);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);

        // กำหนดสีพื้นหลัง
        int backgroundColor = getTitleBackgroundColor(formTitle);
        titleLayout.setBackgroundColor(backgroundColor);

        // เพิ่มไอคอน
        ImageView iconView = new ImageView(getContext());
        iconView.setImageResource(getTitleIcon(formTitle));
        iconView.setColorFilter(getTitleColor(formTitle));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24)
        );
        iconParams.setMargins(0, 0, dpToPx(12), 0);
        titleLayout.addView(iconView, iconParams);

        // เพิ่ม TextView สำหรับ title
        TextView titleTextView = new TextView(getContext());
        titleTextView.setText(formTitle);
        titleTextView.setTextColor(getTitleColor(formTitle));
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleLayout.addView(titleTextView);

        return titleLayout;
    }

    private int getTitleIcon(String title) {
        if (title.contains("ซึมเศร้า")) {
            return R.drawable.ic_psychology; // ไอคอนจิตใจ
        } else if (title.contains("เครียด")) {
            return R.drawable.ic_mood_bad; // ไอคอนอารมณ์
        } else if (title.contains("ฆ่าตัวตาย")) {
            return R.drawable.ic_warning; // ไอคอนเตือนสำหรับการประเมินการฆ่าตัวตาย
        } else if (title.contains("สารเสพติด")) {
            return R.drawable.ic_warning; // ไอคอนเตือน
        } else if (title.contains("หัวใจ")) {
            return R.drawable.ic_favorite; // ไอคอนหัวใจ
        } else if (title.contains("เบาหวาน")) {
            return R.drawable.ic_local_hospital; // ไอคอนโรงพยาบาล
        } else if (title.contains("บุหรี่")) {
            return R.drawable.ic_smoke_free; // ไอคอนเลิกบุหรี่
        } else if (title.contains("สุรา")) {
            return R.drawable.ic_no_drinks; // ไอคอนเลิกเหล้า
        } else {
            return R.drawable.ic_assignment; // ไอคอนแบบฟอร์ม
        }
    }


    /**
     * อัปเดตสถานะของ Fragment ต่างๆ
     */
    private void updateFragmentStatus(PersonScreeningForm15Activity activity) {
        boolean isComplete = false;

        if (contentFragment instanceof MainQuestionsFragment) {
            MainQuestionsFragment mainFragment = (MainQuestionsFragment) contentFragment;

            // ใช้เมธอดใหม่ที่ตรวจสอบตามเงื่อนไขการใช้สารเสพติด
            isComplete = mainFragment.isDataCompleteBasedOnSubstanceUse();

            activity.updateFormStatus("แบบคัดกรองการใช้สารเสพติด", isComplete);

            // แสดงข้อมูลสถานะเพิ่มเติมใน Log สำหรับการ debug
            if (mainFragment.getQuestionOneFragment() != null &&
                    mainFragment.getQuestionOneFragment().validateAllQuestionsAnswered()) {

                if (mainFragment.isAllSubstancesNeverUsed()) {
                    Log.d("FormDialogFragment", "ผู้ใช้ไม่เคยใช้สารเสพติดทั้งหมด - ไม่ต้องตอบคำถามเพิ่มเติม");
                } else if (mainFragment.hasAnySubstanceUsed()) {
                    Log.d("FormDialogFragment", "ผู้ใช้เคยใช้สารเสพติด - ต้องตอบคำถามเพิ่มเติม");
                }
            }

        } else if (contentFragment instanceof StressDepressionFragment) {
            StressDepressionFragment stressFragment = (StressDepressionFragment) contentFragment;
            isComplete = stressFragment.isFormComplete();
            activity.updateFormStatus("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", isComplete);
        } else if (contentFragment instanceof StressDepression2qFragment) {
            StressDepression2qFragment stress2qFragment = (StressDepression2qFragment) contentFragment;
            isComplete = stress2qFragment.isFormComplete();
            activity.updateFormStatus("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)", isComplete);
        } else if (contentFragment instanceof StressDepression9qFragment) {
            StressDepression9qFragment stress9qFragment = (StressDepression9qFragment) contentFragment;
            isComplete = stress9qFragment.isFormComplete();
            activity.updateFormStatus("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)", isComplete);
        } else if (contentFragment instanceof SuicideAssessment8qFragment) {
            SuicideAssessment8qFragment suicide8qFragment = (SuicideAssessment8qFragment) contentFragment;
            isComplete = suicide8qFragment.isFormComplete();
            activity.updateFormStatus("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)", isComplete);
        } else if (contentFragment instanceof SmookingFragment) {
            SmookingFragment smokingFragment = (SmookingFragment) contentFragment;
            isComplete = smokingFragment.isFormComplete();
            activity.updateFormStatus("แบบประเมินความเสี่ยงจากการสูบบุหรี่", isComplete);
        } else if (contentFragment instanceof FagerstromNicotineFragment) {
            FagerstromNicotineFragment fagerstromFragment = (FagerstromNicotineFragment) contentFragment;
            isComplete = fagerstromFragment.isFormComplete();
            activity.updateFormStatus("แบบประเมินการติดนิโคติน Fagerstrom", isComplete);
        } else if (contentFragment instanceof AlcoholFragment) {
            AlcoholFragment alcoholFragment = (AlcoholFragment) contentFragment;
            isComplete = alcoholFragment.isFormComplete();
            activity.updateFormStatus("แบบประเมินการดื่มสุรา", isComplete);
        } else if (contentFragment instanceof HealthRiskAssessmentFragment) {
            HealthRiskAssessmentFragment healthFragment = (HealthRiskAssessmentFragment) contentFragment;
            isComplete = healthFragment.isFormComplete();
            activity.updateFormStatus("แบบประเมินความเสี่ยงโรคเบาหวาน", isComplete);
        }

        // เพิ่ม Fragment อื่นๆ ที่มีการตรวจสอบข้อมูลในอนาคต
    }

    /**
     * ตรวจสอบความถูกต้องของข้อมูลและบันทึก
     */
    private void validateAndSave() {
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();
            boolean isFormValid = true;
            String errorMessage = "";

            // ตรวจสอบข้อมูลสำหรับ MainQuestionsFragment
            if (contentFragment instanceof MainQuestionsFragment) {
                MainQuestionsFragment mainFragment = (MainQuestionsFragment) contentFragment;

                // ใช้เมธอดใหม่ที่ตรวจสอบตามเงื่อนไขการใช้สารเสพติด
                if (!mainFragment.isDataCompleteBasedOnSubstanceUse()) {
                    isFormValid = false;

                    // ตรวจสอบกรณีพิเศษต่างๆ
                    if (mainFragment.getQuestionOneFragment() == null ||
                            !mainFragment.getQuestionOneFragment().validateAllQuestionsAnswered()) {
                        errorMessage = "กรุณาตอบคำถามที่ 1 ให้ครบถ้วนก่อน\n\n" +
                                mainFragment.getQuestionOneFragment().getValidationMessage();
                    } else if (mainFragment.isAllSubstancesNeverUsed()) {
                        // กรณีนี้ไม่ควรเกิดขึ้น เพราะ isDataCompleteBasedOnSubstanceUse()
                        // ควรคืนค่า true แล้ว
                        isFormValid = true;
                        errorMessage = "";
                    } else if (mainFragment.hasAnySubstanceUsed()) {
                        // เคยใช้สารเสพติดแต่ยังกรอกข้อมูลไม่ครบ
                        String detailedMessage = mainFragment.getValidationMessageBasedOnSubstanceUse();
                        if (!detailedMessage.isEmpty()) {
                            errorMessage = "เนื่องจากท่านเลือก \"เคย\" ใช้สารเสพติดอย่างน้อย 1 อย่าง\n" +
                                    "กรุณาตอบคำถามเพิ่มเติมให้ครบถ้วน:\n\n" + detailedMessage;
                        } else {
                            isFormValid = true;
                            errorMessage = "";
                        }
                    } else {
                        // กรณีอื่นๆ ที่ไม่ชัดเจน
                        errorMessage = "กรุณาตรวจสอบและกรอกข้อมูลให้ครบถ้วน";
                    }
                }
            }
            // ตรวจสอบข้อมูลสำหรับ StressDepressionFragment
            else if (contentFragment instanceof StressDepressionFragment) {
                StressDepressionFragment stressFragment = (StressDepressionFragment) contentFragment;
                if (!stressFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = stressFragment.getDetailedValidationMessage();
                }
            }
            // เพิ่มการตรวจสอบสำหรับ Fragment อื่นๆ ตามต้องการ
            else if (contentFragment instanceof StressDepression2qFragment) {
                StressDepression2qFragment stress2qFragment = (StressDepression2qFragment) contentFragment;
                if (!stress2qFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = stress2qFragment.getDetailedValidationMessage();
                }
            }
            else if (contentFragment instanceof StressDepression9qFragment) {
                StressDepression9qFragment stress9qFragment = (StressDepression9qFragment) contentFragment;
                if (!stress9qFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = stress9qFragment.getDetailedValidationMessage();
                }
            }
            else if (contentFragment instanceof SuicideAssessment8qFragment) {
                SuicideAssessment8qFragment suicide8qFragment = (SuicideAssessment8qFragment) contentFragment;
                if (!suicide8qFragment.isFormComplete()) {
                    isFormValid = false;
                    // ใช้ข้อความรายละเอียดจาก getIncompleteQuestions()
                    String incompleteQuestions = suicide8qFragment.getIncompleteQuestions();
                    if (!incompleteQuestions.isEmpty()) {
                        errorMessage = incompleteQuestions;
                    } else {
                        errorMessage = "กรุณาตอบคำถามให้ครบถ้วนทุกข้อ (8 ข้อ)";
                    }
                } else {
                    // ตรวจสอบและแสดงการเตือนหากมีความเสี่ยงสูง
                    suicide8qFragment.checkHighRiskAlert();
                }
            }
            else if (contentFragment instanceof HealthRiskAssessmentFragment) {
                HealthRiskAssessmentFragment healthFragment = (HealthRiskAssessmentFragment) contentFragment;
                if (!healthFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = healthFragment.getDetailedValidationMessage();
                }
            }
            else if (contentFragment instanceof CardiovascularRiskFragment) {
                CardiovascularRiskFragment cardioFragment = (CardiovascularRiskFragment) contentFragment;
                // สามารถเพิ่มการตรวจสอบรายละเอียดได้ในอนาคต
                // if (!cardioFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = cardioFragment.getDetailedValidationMessage();
                // }
            }
            else if (contentFragment instanceof AlcoholFragment) {
                AlcoholFragment alcoholFragment = (AlcoholFragment) contentFragment;
                if (!alcoholFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = alcoholFragment.getDetailedValidationMessage();
                } else {
                    // ตรวจสอบความสอดคล้องของข้อมูลกับคะแนน
                    if (!alcoholFragment.isDataConsistentWithScore()) {
                        isFormValid = false;
                        errorMessage = "ข้อมูลที่เลือกไม่สอดคล้องกับคะแนนประเมิน\nกรุณาตรวจสอบและแก้ไขให้ถูกต้อง";
                    }
                }
            }
            else if (contentFragment instanceof SmookingFragment) {
                SmookingFragment smokingFragment = (SmookingFragment) contentFragment;
                if (!smokingFragment.isFormComplete()) {
                    isFormValid = false;
                    // ใช้ข้อความรายละเอียด
                    String detailedMessage = smokingFragment.getDetailedValidationMessage();
                    if (!detailedMessage.isEmpty()) {
                        errorMessage = detailedMessage;
                    } else {
                        errorMessage = "กรุณากรอกข้อมูลการสูบบุหรี่ให้ครบถ้วน";
                    }
                }
            }
            else if (contentFragment instanceof FagerstromNicotineFragment) {
                FagerstromNicotineFragment fagerstromFragment = (FagerstromNicotineFragment) contentFragment;
                if (!fagerstromFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = fagerstromFragment.getDetailedValidationMessage();
                }
            }

            // ถ้าข้อมูลไม่ถูกต้อง แสดงข้อความแจ้งเตือนแบบละเอียด
            if (!isFormValid) {
                validated = false;
                showDetailedValidationDialog(errorMessage);
                return; // ไม่ดำเนินการบันทึกต่อ และไม่ปิด Dialog
            } else {
                validated = true;
            }

            // จำลองการกดปุ่ม btnOk
            Button btnOk = activity.findViewById(R.id.btnOK);
            if (btnOk != null) {
                btnOk.performClick();
            }

            // อัปเดตสถานะการกรอกข้อมูล
            updateFormStatusAfterSave(activity);

            // ปิด Dialog เมื่อข้อมูลถูกต้อง
            dismiss();
        }
    }
    private void showDetailedValidationDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // สร้าง custom title view
        View titleView = createValidationTitleView();

        builder.setCustomTitle(titleView)
                .setMessage(message)
                .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // สามารถเพิ่มการเลื่อนไปยังข้อที่ยังไม่ได้กรอกได้ที่นี่
                    }
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();

        // ปรับแต่งการแสดงผล
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                if (button != null) {
                    button.setTextColor(Color.parseColor("#E74C3C")); // สีแดง
                    button.setTypeface(null, Typeface.BOLD);
                }
            }
        });

        dialog.show();
    }
    private View createValidationTitleView() {
        LinearLayout titleLayout = new LinearLayout(getContext());
        titleLayout.setOrientation(LinearLayout.HORIZONTAL);
        titleLayout.setPadding(24, 16, 24, 16);
        titleLayout.setGravity(Gravity.CENTER_VERTICAL);
        titleLayout.setBackgroundColor(Color.parseColor("#FFEBEE")); // พื้นหลังแดงอ่อน

        // เพิ่มไอคอนเตือน
        ImageView iconView = new ImageView(getContext());
        iconView.setImageResource(R.drawable.ic_warning);
        iconView.setColorFilter(Color.parseColor("#E74C3C")); // สีแดง
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(
                dpToPx(24), dpToPx(24)
        );
        iconParams.setMargins(0, 0, dpToPx(12), 0);
        titleLayout.addView(iconView, iconParams);

        // เพิ่ม TextView สำหรับ title
        TextView titleTextView = new TextView(getContext());
        titleTextView.setText("ข้อมูลไม่ครบถ้วน");
        titleTextView.setTextColor(Color.parseColor("#E74C3C")); // สีแดง
        titleTextView.setTextSize(18);
        titleTextView.setTypeface(null, Typeface.BOLD);
        titleLayout.addView(titleTextView);

        return titleLayout;
    }
    /**
     * อัปเดตสถานะหลังการบันทึกข้อมูล
     */
    private void updateFormStatusAfterSave(PersonScreeningForm15Activity activity) {
        if (formTitle.equals("แบบคัดกรองการใช้สารเสพติด")) {
            activity.updateMainQuestionsStatus();
        } else if (formTitle.equals("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)")) {
            if (contentFragment instanceof StressDepressionFragment) {
                StressDepressionFragment stressFragment = (StressDepressionFragment) contentFragment;
                boolean isComplete = stressFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะและคำแนะนำ
                stressFragment.showCompletionStatus();

                // ตรวจสอบความเสี่ยงสูง
                if (stressFragment.isHighRisk()) {
                    Log.w("StressDepression", "พบผู้มีความเครียดระดับสูง!");
                }
            }
        } else if (formTitle.equals("คัดกรองโรคซึมเศร้าด้วย 2 คำถาม(2Q)")) {
            // อัปเดตสถานะสำหรับ StressDepression2qFragment
            if (contentFragment instanceof StressDepression2qFragment) {
                StressDepression2qFragment stress2qFragment = (StressDepression2qFragment) contentFragment;
                boolean isComplete = stress2qFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะและคำแนะนำ
                stress2qFragment.showCompletionStatus();

                // ตรวจสอบว่าควรทำ 9Q ต่อหรือไม่
                if (stress2qFragment.shouldDo9QAssessment()) {
                    Log.w("StressDepression2q", stress2qFragment.get9QRecommendationText());
                    // สามารถแสดง Dialog แนะนำให้ทำ 9Q ต่อได้ที่นี่
                }
            }
        } else if (formTitle.equals("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q)")) {
            if (contentFragment instanceof StressDepression9qFragment) {
                StressDepression9qFragment stress9qFragment = (StressDepression9qFragment) contentFragment;
                boolean isComplete = stress9qFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะและคำแนะนำ
                stress9qFragment.showCompletionStatus();

                // ตรวจสอบความเสี่ยงสูง
                if (stress9qFragment.isHighRisk()) {
                    Log.w("StressDepression9q", "พบผู้มีความเสี่ยงสูง!");

                    // ตรวจสอบความเสี่ยงการฆ่าตัวตาย
                    if (stress9qFragment.hasSuicidalRisk()) {
                        Log.e("StressDepression9q", "⚠️ พบความเสี่ยงการฆ่าตัวตาย!");
                        // แสดง Alert Dialog เตือน
                    }
                }

                // ตรวจสอบว่าควรทำ 8Q ต่อหรือไม่
                if (stress9qFragment.shouldDo8QAssessment()) {
                    Log.w("StressDepression9q", stress9qFragment.get8QRecommendationText());
                    // สามารถแสดง Dialog แนะนำให้ทำ 8Q ต่อได้ที่นี่
                }
            }
        } else if (formTitle.equals("การประเมินการฆ่าตัวตายด้วย 8 คําถาม(8Q)")) {
            // อัปเดตสถานะสำหรับ SuicideAssessment8qFragment
            if (contentFragment instanceof SuicideAssessment8qFragment) {
                SuicideAssessment8qFragment suicide8qFragment = (SuicideAssessment8qFragment) contentFragment;
                boolean isComplete = suicide8qFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะการกรอกข้อมูล
                suicide8qFragment.showCompletionStatus();
            }
        } else if (formTitle.equals("แบบประเมินความเสี่ยงจากการสูบบุหรี่")) {
            if (contentFragment instanceof SmookingFragment) {
                SmookingFragment smokingFragment = (SmookingFragment) contentFragment;
                boolean isComplete = smokingFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะการกรอกข้อมูล
                smokingFragment.showCompletionStatus();
            }
        } else if (formTitle.equals("แบบประเมินการติดนิโคติน Fagerstrom")) {
            if (contentFragment instanceof FagerstromNicotineFragment) {
                FagerstromNicotineFragment fagerstromFragment = (FagerstromNicotineFragment) contentFragment;
                boolean isComplete = fagerstromFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะการกรอกข้อมูล
                fagerstromFragment.showCompletionStatus();
            }
        } else if (formTitle.equals("แบบประเมินความเสี่ยงโรคเบาหวาน")) {
            if (contentFragment instanceof HealthRiskAssessmentFragment) {
                HealthRiskAssessmentFragment healthFragment = (HealthRiskAssessmentFragment) contentFragment;
                boolean isComplete = healthFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะและคำแนะนำ
                healthFragment.showCompletionStatus();

                // ตรวจสอบความเสี่ยงสูง
                if (healthFragment.isHighRisk()) {
                    Log.w("HealthRiskAssessment", "พบผู้มีความเสี่ยงสูงต่อโรคเบาหวาน!");

                    // ตรวจสอบค่าน้ำตาล
                    if (healthFragment.hasAbnormalGlucose()) {
                        String glucoseStatus = healthFragment.getGlucoseStatus();
                        Log.w("HealthRiskAssessment", "พบค่าน้ำตาลผิดปกติ: " + glucoseStatus);
                    }
                }
            }
        } else if (formTitle.equals("แบบประเมินการดื่มสุรา")) {
            if (contentFragment instanceof AlcoholFragment) {
                AlcoholFragment alcoholFragment = (AlcoholFragment) contentFragment;
                boolean isComplete = alcoholFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);

                // แสดงสถานะการกรอกข้อมูล
                alcoholFragment.showCompletionStatus();

                // แสดงระดับความเสี่ยง
                String riskLevel = alcoholFragment.getRiskLevelFromScore();
                Log.d("FormDialogFragment", "ระดับความเสี่ยงจากการดื่มสุรา: " + riskLevel);
            }
        } else {
            // อัปเดตสถานะสำหรับ Fragment อื่นๆ
            activity.updateFormStatus(formTitle, true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // กำหนดขนาดของ dialog ให้กว้างขึ้น
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT; // เปลี่ยนเป็น MATCH_PARENT เพื่อให้แสดงเต็มหน้าจอ
            dialog.getWindow().setLayout(width, height);

            // เพิ่มการตั้งค่าเพื่อให้ dialog สามารถขยายได้เต็มที่และเลื่อนได้
            Window window = dialog.getWindow();
            if (window != null) {
                window.setGravity(Gravity.CENTER);

                // เพิ่มการตั้งค่าเพื่อให้ใช้พื้นที่ส่วนใหญ่ของหน้าจอแต่ไม่เต็มทั้งหมด
                DisplayMetrics metrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        }
    }
     // เพิ่มเมธอดสำหรับกำหนดสีตามประเภทแบบฟอร์ม
    private int getTitleColor(String title) {
        if (title.contains("ซึมเศร้า")) {
            return Color.parseColor("#673AB7"); // ม่วง สำหรับโรคซึมเศร้า
        } else if (title.contains("เครียด")) {
            return Color.parseColor("#8E44AD"); // ม่วงเข้ม สำหรับความเครียด
        } else if (title.contains("ฆ่าตัวตาย")) {
            return Color.parseColor("#C0392B"); // แดงเข้ม สำหรับการประเมินการฆ่าตัวตาย
        } else if (title.contains("สารเสพติด")) {
            return Color.parseColor("#E74C3C"); // แดง สำหรับสารเสพติด
        } else if (title.contains("หัวใจ") || title.contains("เบาหวาน")) {
            return Color.parseColor("#2E86C1"); // น้ำเงิน สำหรับโรคเรื้อรัง
        } else if (title.contains("บุหรี่") || title.contains("สุรา")) {
            return Color.parseColor("#D68910"); // ส้ม สำหรับสารเสพติด
        } else {
            return Color.parseColor("#2C3E50"); // เทาเข้ม สำหรับอื่นๆ
        }
    }
    private int getTitleBackgroundColor(String title) {
        if(!title.isEmpty()) {
            if (title.contains("ซึมเศร้า")) {
                return Color.parseColor("#F3E5F5"); // ม่วงอ่อน
            } else if (title.contains("เครียด")) {
                return Color.parseColor("#EBF3FD"); // น้ำเงินอ่อน
            } else if (title.contains("ฆ่าตัวตาย")) {
                return Color.parseColor("#FFEBEE"); // แดงอ่อน สำหรับการประเมินการฆ่าตัวตาย
            } else if (title.contains("สารเสพติด")) {
                return Color.parseColor("#FFEBEE"); // แดงอ่อน
            } else {
                return Color.parseColor("#F8F9FA"); // เทาอ่อน
            }
        }
        return Color.parseColor("#F8F9FA"); // เทาอ่อน;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}