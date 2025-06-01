package th.in.ffc.app.form;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
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

        // ตั้งค่าหัวเรื่องและ view
        builder.setView(view)
                .setTitle(formTitle)
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

    /**
     * อัปเดตสถานะของ Fragment ต่างๆ
     */
    private void updateFragmentStatus(PersonScreeningForm15Activity activity) {
        boolean isComplete = false;

        if (contentFragment instanceof MainQuestionsFragment) {
            MainQuestionsFragment mainFragment = (MainQuestionsFragment) contentFragment;
            isComplete = mainFragment.isAllDataComplete();
            activity.updateFormStatus("แบบคัดกรองการใช้สารเสพติด", isComplete);
        } else if (contentFragment instanceof StressDepressionFragment) {
            StressDepressionFragment stressFragment = (StressDepressionFragment) contentFragment;
            isComplete = stressFragment.isFormComplete();
            activity.updateFormStatus("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)", isComplete);
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
                if (!mainFragment.isAllDataComplete()) {
                    isFormValid = false;
                    errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วนในทุกคำถาม";
                }
            }
            // ตรวจสอบข้อมูลสำหรับ StressDepressionFragment
            else if (contentFragment instanceof StressDepressionFragment) {
                StressDepressionFragment stressFragment = (StressDepressionFragment) contentFragment;
                if (!stressFragment.isFormComplete()) {
                    isFormValid = false;
                    errorMessage = "กรุณาตอบคำถามให้ครบถ้วนทุกข้อ (5 ข้อ)";
                }
            }
            // เพิ่มการตรวจสอบสำหรับ Fragment อื่นๆ ตามต้องการ
            else if (contentFragment instanceof StressDepression2qFragment) {
                StressDepression2qFragment stress2qFragment = (StressDepression2qFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน StressDepression2qFragment
                // if (!stress2qFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณาตอบคำถามให้ครบถ้วนทุกข้อ (2 ข้อ)";
                // }
            }
            else if (contentFragment instanceof StressDepression9qFragment) {
                StressDepression9qFragment stress9qFragment = (StressDepression9qFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน StressDepression9qFragment
                // if (!stress9qFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณาตอบคำถามให้ครบถ้วนทุกข้อ (9 ข้อ)";
                // }
            }
            else if (contentFragment instanceof SuicideAssessment8qFragment) {
                SuicideAssessment8qFragment suicide8qFragment = (SuicideAssessment8qFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน SuicideAssessment8qFragment
                // if (!suicide8qFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณาตอบคำถามให้ครบถ้วนทุกข้อ (8 ข้อ)";
                // }
            }
            else if (contentFragment instanceof HealthRiskAssessmentFragment) {
                HealthRiskAssessmentFragment healthFragment = (HealthRiskAssessmentFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน HealthRiskAssessmentFragment
                // if (!healthFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วนทุกข้อ";
                // }
            }
            else if (contentFragment instanceof CardiovascularRiskFragment) {
                CardiovascularRiskFragment cardioFragment = (CardiovascularRiskFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน CardiovascularRiskFragment
                // if (!cardioFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณากรอกข้อมูลให้ครบถ้วนทุกข้อ";
                // }
            }
            else if (contentFragment instanceof AlcoholFragment) {
                AlcoholFragment alcoholFragment = (AlcoholFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน AlcoholFragment
                // if (!alcoholFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณาตอบคำถามเกี่ยวกับการดื่มสุราให้ครบถ้วน";
                // }
            }
            else if (contentFragment instanceof SmookingFragment) {
                SmookingFragment smokingFragment = (SmookingFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน SmookingFragment
                // if (!smokingFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณาตอบคำถามเกี่ยวกับการสูบบุหรี่ให้ครบถ้วน";
                // }
            }
            else if (contentFragment instanceof FagerstromNicotineFragment) {
                FagerstromNicotineFragment fagerstromFragment = (FagerstromNicotineFragment) contentFragment;
                // สมมติว่ามีเมธอด isFormComplete() ใน FagerstromNicotineFragment
                // if (!fagerstromFragment.isFormComplete()) {
                //     isFormValid = false;
                //     errorMessage = "กรุณาตอบคำถามการติดนิโคตินให้ครบถ้วนทุกข้อ (6 ข้อ)";
                // }
            }

            // ถ้าข้อมูลไม่ถูกต้อง แสดงข้อความแจ้งเตือน
            if (!isFormValid) {
                validated = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("ข้อมูลไม่ครบถ้วน")
                        .setMessage(errorMessage)
                        .setPositiveButton("ตกลง", null)
                        .setCancelable(false)
                        .show();
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

    /**
     * อัปเดตสถานะหลังการบันทึกข้อมูล
     */
    private void updateFormStatusAfterSave(PersonScreeningForm15Activity activity) {
        if (formTitle.equals("แบบคัดกรองการใช้สารเสพติด")) {
            activity.updateMainQuestionsStatus();
        } else if (formTitle.equals("ประเมินภาวะเครียด-ซึมเศร้า(ST 5)")) {
            // อัปเดตสถานะสำหรับ StressDepressionFragment
            if (contentFragment instanceof StressDepressionFragment) {
                StressDepressionFragment stressFragment = (StressDepressionFragment) contentFragment;
                boolean isComplete = stressFragment.isFormComplete();
                activity.updateFormStatus(formTitle, isComplete);
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
}