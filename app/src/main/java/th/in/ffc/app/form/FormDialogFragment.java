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

    private void saveDataAndCallActivityButton() {
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();
            // เพิ่มการตรวจสอบข้อมูลสำหรับ MainQuestionsFragment
            if (contentFragment instanceof MainQuestionsFragment) {
                MainQuestionsFragment mainFragment = (MainQuestionsFragment) contentFragment;
                // ตรวจสอบความครบถ้วนของข้อมูล
                if (!mainFragment.isAllDataComplete()) {
                    // แสดงข้อความแจ้งเตือน
                    validated = false;
//                    Toast.makeText(getContext(), "กรุณากรอกข้อมูลให้ครบถ้วนในทุกคำถาม", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("ข้อมูลไม่ครบถ้วน")
                            .setMessage("กรุณากรอกข้อมูลให้ครบถ้วนในทุกคำถาม")
                            .setPositiveButton("ตกลง", null)
                            .setCancelable(false)
                            .show();
                    return; // ไม่ดำเนินการบันทึกต่อ
                }
                else {
                    validated = true;
                }

            }

            // จำลองการกดปุ่ม btnOk
            Button btnOk = activity.findViewById(R.id.btnOK);
            if (btnOk != null) {
                btnOk.performClick();
            }
            // อัปเดตสถานะการกรอกข้อมูล
            if (formTitle.equals("แบบคัดกรองการใช้สารเสพติด")) {
                activity.updateMainQuestionsStatus();
            } else {
                activity.updateFormStatus(formTitle, true);
            }


            // ปิด Dialog
            dismiss();
        }
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
                // เอา ScrollView ออกจาก Fragment ก่อนเพิ่มลงใน container
//                if (contentFragment instanceof FagerstromNicotineFragment) {
//                    // อาจต้องปรับ layout ของ Fragment โดยเฉพาะ
//                    // หรือใช้เทคนิคอื่นๆ เช่น setMaxHeight ให้กับ container
//                }

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
            // ตรวจสอบกรณี MainQuestionsFragment
            if (contentFragment instanceof MainQuestionsFragment && getActivity() instanceof PersonScreeningForm15Activity) {
                MainQuestionsFragment mainFragment = (MainQuestionsFragment) contentFragment;
                PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();

                // อัปเดตสถานะหลังจากที่ Fragment ถูกโหลดเรียบร้อย
                new Handler().postDelayed(() -> {
                    boolean isComplete = mainFragment.isAllDataComplete();
                    activity.updateFormStatus("แบบคัดกรองการใช้สารเสพติด", isComplete);
                }, 500); // รอสักครู่เพื่อให้ Fragment ถูกโหลดเรียบร้อย
            }
            // เพิ่มปุ่มเลื่อนขึ้นด้านบนหรือปุ่มเลื่อนกลับ หากต้องการ
        });

        return dialog;
    }
    private void validateAndSave() {
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();

            // เพิ่มการตรวจสอบข้อมูลสำหรับ MainQuestionsFragment
            if (contentFragment instanceof MainQuestionsFragment) {
                MainQuestionsFragment mainFragment = (MainQuestionsFragment) contentFragment;

                // ตรวจสอบความครบถ้วนของข้อมูล
                if (!mainFragment.isAllDataComplete()) {
                    // แสดงข้อความแจ้งเตือน
                    validated = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("ข้อมูลไม่ครบถ้วน")
                            .setMessage("กรุณากรอกข้อมูลให้ครบถ้วนในทุกคำถาม")
                            .setPositiveButton("ตกลง", null)
                            .setCancelable(false)
                            .show();
                    return; // ไม่ดำเนินการบันทึกต่อ และไม่ปิด Dialog
                }
                else {
                    validated = true;
                }
            }

            // จำลองการกดปุ่ม btnOk
            Button btnOk = activity.findViewById(R.id.btnOK);
            if (btnOk != null) {
                btnOk.performClick();
            }

            // อัปเดตสถานะการกรอกข้อมูล
            if (formTitle.equals("แบบคัดกรองการใช้สารเสพติด")) {
                activity.updateMainQuestionsStatus();
            } else {
                activity.updateFormStatus(formTitle, true);
            }

            // ปิด Dialog เมื่อข้อมูลถูกต้อง
            dismiss();
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

//                int dialogHeight = (int)(metrics.heightPixels * 0.9); // ใช้ 90% ของความสูงหน้าจอ
//                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight);

                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            }
        }
    }
}