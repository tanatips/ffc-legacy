package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.CounselingSignatureDao;
import th.in.ffc.app.form.screening.datalive.CounselingLiveData;
import th.in.ffc.app.form.screening.model.CounselingInfo;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.widget.SignatureView;

public class CounselingSignFragment extends Fragment {

    private static final String ARG_VISIT_ID = "visit_id";
    private static final String ARG_PERSON_ID = "person_id";

    private String visitId;
    private String personId;

    private NestedScrollView scrollView;
    private RadioGroup radioGroupCounseling;
    private RadioButton radioButtonProvideConsult;
    private RadioButton radioButtonSendToDoctor;
    private EditText editTextConsultDetail;
    private SignatureView signatureViewPatient;
    private SignatureView signatureViewProvider;
    private Button btnClearPatientSignature;
    private Button btnClearProviderSignature;


    private CounselingSignatureDao counselingDao;
    private UserSessionManager sessionManager;
    private CounselingInfo currentCounseling;

    // เพิ่มตัวแปรสำหรับการส่งข้อมูล
    private OnDataPass dataPasser;
    private CounselingLiveData counselingLiveData;
    private SharedViewModel sharedViewModel;

    // ตัวแปรสำหรับเก็บสถานะว่ากำลังวาดลายเซ็นอยู่หรือไม่
    private boolean isSigningPatient = false;
    private boolean isSigningProvider = false;

    public CounselingSignFragment() {
        // Required empty public constructor
    }

    public static CounselingSignFragment newInstance(String visitId, String personId) {
        CounselingSignFragment fragment = new CounselingSignFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VISIT_ID, visitId);
        args.putString(ARG_PERSON_ID, personId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            visitId = getArguments().getString(ARG_VISIT_ID);
            personId = getArguments().getString(ARG_PERSON_ID);
        }
        counselingDao = new CounselingSignatureDao(getContext());
        sessionManager = new UserSessionManager(getContext());

        // สร้าง LiveData และ ViewModel
        counselingLiveData = new CounselingLiveData();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // ตั้งค่าข้อมูลเริ่มต้น
        if (currentCounseling == null) {
            currentCounseling = new CounselingInfo();
            if (personId != null) {
                currentCounseling.setPersonId(personId);
            }
            if (visitId != null) {
                currentCounseling.setVisitId(visitId);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_counseling_sign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // เชื่อม View elements
        initializeViews(view);

        // ตั้งค่า listeners
        setupListeners();

        // แก้ไขปัญหา scroll ขณะเซ็นชื่อ
        setupSignatureViewBehavior();

        // โหลดข้อมูลที่มีอยู่ (ถ้ามี)
        loadData();

        // ปรับความสูงของ ViewPager (ถ้ามี)
        adjustViewPagerHeight(view);
    }

    private void adjustViewPagerHeight(View view) {
        View parentViewPager = (View) view.getParent();
        if (parentViewPager != null) {
            parentViewPager.post(() -> {
                int height = view.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = parentViewPager.getLayoutParams();
                layoutParams.height = height;
                parentViewPager.setLayoutParams(layoutParams);
            });
        }
    }

    private void initializeViews(View view) {
        // หา NestedScrollView หลัก (ถ้ามี)
        scrollView = view.findViewById(R.id.mainScrollView);
        if (scrollView == null) {
            // ถ้ายังไม่มี scrollView ใน XML เราสามารถหา parent ที่เป็น scrollable ได้
            ViewGroup parent = (ViewGroup) view.getParent();
            while (parent != null) {
                if (parent instanceof NestedScrollView || parent instanceof android.widget.ScrollView) {
                    break;
                }
                if (parent.getParent() instanceof ViewGroup) {
                    parent = (ViewGroup) parent.getParent();
                } else {
                    parent = null;
                }
            }

            if (parent instanceof NestedScrollView) {
                scrollView = (NestedScrollView) parent;
            }
        }

        radioGroupCounseling = view.findViewById(R.id.radioGroupCounseling);
        radioButtonProvideConsult = view.findViewById(R.id.radioButtonProvideConsult);
        radioButtonSendToDoctor = view.findViewById(R.id.radioButtonSendToDoctor);
        editTextConsultDetail = view.findViewById(R.id.editTextConsultDetail);
        signatureViewPatient = view.findViewById(R.id.signatureViewPatient);
        signatureViewProvider = view.findViewById(R.id.signatureViewProvider);
        btnClearPatientSignature = view.findViewById(R.id.btnClearPatientSignature);
        btnClearProviderSignature = view.findViewById(R.id.btnClearProviderSignature);

    }

    private void setupListeners() {
        radioGroupCounseling.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonProvideConsult) {
                editTextConsultDetail.setVisibility(View.VISIBLE);
                currentCounseling.setCounselingType(1);
            } else {
                editTextConsultDetail.setVisibility(View.GONE);
                currentCounseling.setCounselingType(2);
                currentCounseling.setDetail("");
            }

            // บันทึกค่าลงใน LiveData และส่งข้อมูลไปยัง Activity หลัก
            counselingLiveData.setCounselingType(currentCounseling.getCounselingType());
            sharedViewModel.setCounselingLiveData(counselingLiveData);
            dataPasser.onCounselingDataPass(currentCounseling);
        });

        btnClearPatientSignature.setOnClickListener(v -> {
            signatureViewPatient.clear();
            currentCounseling.setPatientSignature(null);
        });

        btnClearProviderSignature.setOnClickListener(v -> {
            signatureViewProvider.clear();
            currentCounseling.setProviderSignature(null);
        });

        editTextConsultDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                String detail = editTextConsultDetail.getText().toString().trim();
//
//                currentCounseling.setDetail(detail);
//                currentCounseling.setPatientSignature(signatureViewPatient.getSignatureAsByteArray());
//                currentCounseling.setProviderSignature(signatureViewProvider.getSignatureAsByteArray());
//
//                counselingLiveData.setConsultDetail(detail);
//                counselingLiveData.setPatientSignature(currentCounseling.getPatientSignature());
//                counselingLiveData.setProviderSignature(currentCounseling.getProviderSignature());
//
//                sharedViewModel.setCounselingLiveData(counselingLiveData);
//                dataPasser.onCounselingDataPass(currentCounseling);
            }
        });

        editTextConsultDetail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String detail = editTextConsultDetail.getText().toString().trim();
                currentCounseling.setDetail(detail);
                counselingLiveData.setConsultDetail(detail);
                sharedViewModel.setCounselingLiveData(counselingLiveData);
                dataPasser.onCounselingDataPass(currentCounseling);
            }
        });
    }

    private void setupSignatureViewBehavior() {
        // จัดการ Touch Events สำหรับ SignatureView ผู้รับบริการ
        signatureViewPatient.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // เริ่มวาดลายเซ็น
                    isSigningPatient = true;
                    disableScrolling();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // หยุดวาดลายเซ็น
                    isSigningPatient = false;
                    enableScrolling();
                    // บันทึกลายเซ็นเมื่อวาดเสร็จ
                    if (!signatureViewPatient.isEmpty()) {

                        currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
                        currentCounseling.setPatientSignature(signatureViewPatient.getSignatureAsByteArray());
                        dataPasser.onCounselingDataPass(currentCounseling);
                    }
                    break;
            }
            return false; // ให้ event ส่งต่อไปยัง SignatureView ปกติ
        });

        // จัดการ Touch Events สำหรับ SignatureView ผู้ให้บริการ
        signatureViewProvider.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // เริ่มวาดลายเซ็น
                    isSigningProvider = true;
                    disableScrolling();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    // หยุดวาดลายเซ็น
                    isSigningProvider = false;
                    enableScrolling();
                    // บันทึกลายเซ็นเมื่อวาดเสร็จ
                    if (!signatureViewProvider.isEmpty()) {
                        currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
                        currentCounseling.setProviderSignature(signatureViewProvider.getSignatureAsByteArray());
                        dataPasser.onCounselingDataPass(currentCounseling);
                    }
                    break;
            }
            return false; // ให้ event ส่งต่อไปยัง SignatureView ปกติ
        });
    }

    private void disableScrolling() {
        if (scrollView != null) {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }
    }

    private void enableScrolling() {
        if (scrollView != null && !isSigningPatient && !isSigningProvider) {
            scrollView.requestDisallowInterceptTouchEvent(false);
        }
    }

    private void loadData() {
        // โหลดข้อมูลจาก ViewModel
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCounselingLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                counselingLiveData = data;
                personId = data.getPersonId();
                visitId = data.getVisitId();

                // โหลดข้อมูลที่มีอยู่แล้วจากฐานข้อมูล (ถ้ามี)
                if (visitId != null && !visitId.isEmpty()) {
                    List<CounselingInfo> counselingList = counselingDao.getCounselingByVisitId(visitId);
                    if (!counselingList.isEmpty()) {
                        // ใช้ข้อมูลล่าสุด
                        currentCounseling = counselingList.get(0);
                        populateForm(currentCounseling);
                    } else if (personId != null && !personId.isEmpty()) {
                        // ถ้าไม่พบข้อมูลตาม visitId ให้ลองค้นหาตาม personId
                        counselingList = counselingDao.getCounselingByPersonId(personId);
                        if (!counselingList.isEmpty()) {
                            currentCounseling = counselingList.get(0);
                            populateForm(currentCounseling);
                        }
                    }
                }
            }
        });
    }

    private void populateForm(CounselingInfo counseling) {
        // แสดงข้อมูลที่มีอยู่ในฟอร์ม
        if (counseling.getCounselingType() == 1) {
            radioButtonProvideConsult.setChecked(true);
            editTextConsultDetail.setText(counseling.getDetail());
            editTextConsultDetail.setVisibility(View.VISIBLE);
        } else if (counseling.getCounselingType() == 2) {
            radioButtonSendToDoctor.setChecked(true);
            editTextConsultDetail.setVisibility(View.GONE);
        }

        // ถ้ามีลายเซ็นให้แสดง
        if (counseling.getPatientSignature() != null) {
            signatureViewPatient.setSignatureSafely(counseling.getPatientSignature());
        }

        if (counseling.getProviderSignature() != null) {
            signatureViewProvider.setSignatureSafely(counseling.getProviderSignature());
        }
    }

    private boolean validateForm() {
        // ตรวจสอบความถูกต้องของข้อมูล
        if (radioGroupCounseling.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "กรุณาเลือกประเภทการให้บริการ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (radioButtonProvideConsult.isChecked() && editTextConsultDetail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "กรุณากรอกรายละเอียดคำแนะนำ", Toast.LENGTH_SHORT).show();
            editTextConsultDetail.requestFocus();
            return false;
        }

        if (signatureViewPatient.isEmpty()) {
            Toast.makeText(getContext(), "กรุณาลงลายมือชื่อผู้รับบริการ", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (signatureViewProvider.isEmpty()) {
            Toast.makeText(getContext(), "กรุณาลงลายมือชื่อผู้ให้บริการ", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveFormData() {
        // กำหนดข้อมูลจากฟอร์ม
        if (radioButtonProvideConsult.isChecked()) {
            currentCounseling.setCounselingType(1);
            currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
        } else {
            currentCounseling.setCounselingType(2);
            currentCounseling.setDetail("");
        }

        // บันทึกลายเซ็นอีกครั้งเพื่อความแน่ใจ (จุดสำคัญ)
        if (!signatureViewPatient.isEmpty()) {
            byte[] patientSignature = signatureViewPatient.getSignatureAsByteArray();
            currentCounseling.setPatientSignature(patientSignature);
            System.out.println("Patient signature size: " + (patientSignature != null ? patientSignature.length : 0));
        }

        if (!signatureViewProvider.isEmpty()) {
            byte[] providerSignature = signatureViewProvider.getSignatureAsByteArray();
            currentCounseling.setProviderSignature(providerSignature);
            System.out.println("Provider signature size: " + (providerSignature != null ? providerSignature.length : 0));
        }
        // กำหนดค่าสำหรับบันทึก
        if (personId != null) {
            currentCounseling.setPersonId(personId);
        }
        if (visitId != null) {
            currentCounseling.setVisitId(visitId);
        }

        // อัปเดต LiveData และส่งข้อมูลไปยัง Activity หลัก
        counselingLiveData.setCounselingType(currentCounseling.getCounselingType());
        counselingLiveData.setConsultDetail(currentCounseling.getDetail());
        counselingLiveData.setPatientSignature(currentCounseling.getPatientSignature());
        counselingLiveData.setProviderSignature(currentCounseling.getProviderSignature());
        sharedViewModel.setCounselingLiveData(counselingLiveData);

        // ส่งข้อมูลไปยัง Activity หลัก
        dataPasser.onCounselingDataPass(currentCounseling);

        try {
            // กรณีแก้ไขข้อมูล
            if (currentCounseling.getId() != 0) {
                currentCounseling.setUpdatedBy(sessionManager.getUsername());
                int rowsUpdated = counselingDao.updateCounseling(currentCounseling);
                System.out.println("Update result: " + rowsUpdated);
                if (rowsUpdated > 0) {
                    Toast.makeText(getContext(), "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "ไม่สามารถบันทึกข้อมูลได้", Toast.LENGTH_SHORT).show();
                }
            } else {
                // กรณีเพิ่มข้อมูลใหม่
                currentCounseling.setCreatedBy(sessionManager.getUsername());
                long newId = counselingDao.saveCounseling(currentCounseling);
                System.out.println("Insert result: " + newId);
                if (newId > 0) {
                    Toast.makeText(getContext(), "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
                    currentCounseling.setId(newId);
                } else {
                    Toast.makeText(getContext(), "ไม่สามารถบันทึกข้อมูลได้", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            System.out.println("Error saving counseling: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(getContext(), "เกิดข้อผิดพลาด: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public CounselingInfo getCounselingInfo() {
        return currentCounseling;
    }

    public void setCounselingInfo(CounselingInfo info) {
        this.currentCounseling = info;
        populateForm(info);
    }
}