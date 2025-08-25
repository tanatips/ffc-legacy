package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

    private static final String ARG_VISIT_NO = "visitNo";
    private static final String ARG_PERSON_ID = "person_id";

    private String visitNo;
    private String personId;

    private NestedScrollView scrollView;
    private RadioGroup radioGroupCounseling;
    private RadioButton radioButtonProvideConsult;
    private RadioButton radioButtonSendToDoctor;
    private EditText editTextConsultDetail;

    private EditText editTextReferralDetail;

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

    private boolean isRadioButtonFixed = false;
    private boolean first = true;

    public CounselingSignFragment() {
        // Required empty public constructor
    }

    public static CounselingSignFragment newInstance(String visitNo, String personId) {
        CounselingSignFragment fragment = new CounselingSignFragment();
        Bundle args = new Bundle();
        args.putString(ARG_VISIT_NO, visitNo);
        args.putString(ARG_PERSON_ID, personId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            visitNo = getArguments().getString(ARG_VISIT_NO);
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
            if (visitNo != null) {
                currentCounseling.setVisitNo(visitNo);
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

        initializeViews(view);
        setupListeners();
        setupSignatureViewBehavior();

        // แก้ไข RadioButton เฉพาะครั้งแรกเท่านั้น
        if (!isRadioButtonFixed) {
            view.post(() -> {
                fixRadioButtonIssue();
                isRadioButtonFixed = true; // ตั้งค่าให้ไม่เรียกซ้ำ

                // ทดสอบ RadioButton เฉพาะครั้งแรกเท่านั้น (ถ้าต้องการ)
                // testRadioButtonFunctionality(); // แสดงความเห็น (comment) เพื่อไม่ให้รบกวน

                Log.d("CounselingSign", "RadioButton fix completed once");
            });
        }

        loadDataWithDelay();
        adjustViewPagerHeight(view);
        startSignatureMonitoring();
    }
    private void loadDataWithDelay() {
        Log.d("CounselingSign", "=== Starting loadDataWithDelay ===");

        // โหลดข้อมูลจาก ViewModel ก่อน
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCounselingLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                Log.d("CounselingSign", "Received data from ViewModel - PersonId: " + data.getPersonId() + ", VisitNo: " + data.getVisitNo());
                counselingLiveData = data;
                personId = data.getPersonId();
                visitNo = data.getVisitNo();

                // เมื่อได้ข้อมูลจาก ViewModel แล้ว ให้โหลดจากฐานข้อมูลทันที
                if(first) {
                    loadFromDatabaseWithRetry();
                    first=false;
                }
            }
        });

        // หากไม่มีข้อมูลจาก ViewModel ก็โหลดจากพารามิเตอร์
        if (visitNo != null && !visitNo.isEmpty()) {
            if(first) {
                loadFromDatabaseWithRetry();
                first=false;
            }
        }

        Log.d("CounselingSign", "=== End loadDataWithDelay ===");
    }
    private void loadFromDatabaseWithRetry() {
        loadFromDatabaseWithRetry(0);
    }
    private void loadFromDatabaseWithRetry(int retryCount) {
        Log.d("CounselingSign", "Loading from database, retry count: " + retryCount);

        if (visitNo == null || visitNo.isEmpty()) {
            Log.w("CounselingSign", "VisitId is null or empty, cannot load data");
            return;
        }

        // โหลดข้อมูลใน background thread
        new Thread(() -> {
            try {
                CounselingInfo existingCounseling = counselingDao.getCounselingWithSignaturesByVisitId(visitNo);

                // กลับมา UI thread เพื่อแสดงข้อมูล
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                    if (existingCounseling != null) {
                        currentCounseling = existingCounseling;
                        Log.d("CounselingSign", "Found existing record with ID: " + currentCounseling.getId());

                        // รอให้ View พร้อมก่อนแสดงข้อมูล
                        waitForViewsAndPopulate(existingCounseling, retryCount);
                    } else {
                        Log.d("CounselingSign", "No existing record found for visitNo: " + visitNo);

                        // ถ้าไม่มีข้อมูลและยังลองไม่ถึง 3 ครั้ง ให้ลองใหม่
                        if (retryCount < 2) {
                            new android.os.Handler().postDelayed(() -> {
                                loadFromDatabaseWithRetry(retryCount + 1);
                            }, 1000); // รอ 1 วินาทีแล้วลองใหม่
                        }
                    }
                });

            } catch (Exception e) {
                Log.e("CounselingSign", "Error loading data from database", e);

                // หากเกิด error และยังลองไม่ถึง 3 ครั้ง ให้ลองใหม่
                if (retryCount < 2) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                        loadFromDatabaseWithRetry(retryCount + 1);
                    }, 2000); // รอ 2 วินาทีแล้วลองใหม่
                }
            }
        }).start();
    }
    private void waitForViewsAndPopulate(CounselingInfo counseling, int retryCount) {
        if (signatureViewPatient == null || signatureViewProvider == null) {
            Log.w("CounselingSign", "SignatureViews are null, retrying...");
            if (retryCount < 3) {
                new android.os.Handler().postDelayed(() -> {
                    waitForViewsAndPopulate(counseling, retryCount + 1);
                }, 500);
            }
            return;
        }

        // ตรวจสอบว่า Views มีขนาดแล้วหรือยัง
        if (signatureViewPatient.getWidth() <= 0 || signatureViewPatient.getHeight() <= 0) {
            Log.d("CounselingSign", "Views not sized yet, waiting... (retry: " + retryCount + ")");

            // รอให้ Views มีขนาด
            signatureViewPatient.post(() -> {
                if (signatureViewPatient.getWidth() > 0 && signatureViewPatient.getHeight() > 0) {
                    Log.d("CounselingSign", "Views are now sized, populating form");
                    populateFormSafely(counseling);
                } else if (retryCount < 5) {
                    // ถ้ายังไม่มีขนาด ให้ลองใหม่
                    new android.os.Handler().postDelayed(() -> {
                        waitForViewsAndPopulate(counseling, retryCount + 1);
                    }, 300);
                } else {
                    Log.w("CounselingSign", "Views still not sized after multiple retries, populating anyway");
                    populateFormSafely(counseling);
                }
            });
        } else {
            Log.d("CounselingSign", "Views are sized, populating immediately");
            populateFormSafely(counseling);
        }
    }
    // แก้ไข populateFormSafely() ให้ไม่ใช้ post() มากเกินไป
    private void populateFormSafely(CounselingInfo counseling) {
        Log.d("CounselingSign", "=== Starting populateFormSafely ===");
        Log.d("CounselingSign", "Counseling ID: " + counseling.getId());
        Log.d("CounselingSign", "Counseling Type: " + counseling.getCounselingType());
        Log.d("CounselingSign", "Detail: " + counseling.getDetail());

        try {
            // ปิด listener ชั่วคราวเพื่อป้องกันการเรียกซ้ำ
            radioGroupCounseling.setOnCheckedChangeListener(null);

            // แสดงข้อมูลที่มีอยู่ในฟอร์ม
            if (counseling.getCounselingType() == 1) {
                radioButtonProvideConsult.setChecked(true);
                editTextConsultDetail.setText(counseling.getDetail());
                editTextConsultDetail.setVisibility(View.VISIBLE);
                editTextReferralDetail.setVisibility(View.GONE);
                Log.d("CounselingSign", "Set radio to provide consult");
            } else if (counseling.getCounselingType() == 2) {
                radioButtonSendToDoctor.setChecked(true);
                editTextReferralDetail.setText(counseling.getReferralDetail());
                editTextConsultDetail.setVisibility(View.GONE);
                editTextReferralDetail.setVisibility(View.VISIBLE);
                Log.d("CounselingSign", "Set radio to send to doctor");
            }

            // เปิด listener กลับ
            setupRadioGroupListener();

            // โหลดลายเซ็นแยกต่างหาก
            loadSignatureDataSafely(counseling);

        } catch (Exception e) {
            Log.e("CounselingSign", "Error in populateFormSafely: " + e.getMessage(), e);
        }

        Log.d("CounselingSign", "=== End populateFormSafely ===");
    }

    // เมธอดแยกสำหรับตั้งค่า RadioGroup Listener
    private void setupRadioGroupListener() {
        radioGroupCounseling.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("CounselingSign", "RadioGroup checked changed: " + checkedId);

            if (checkedId == R.id.radioButtonProvideConsult) {
                editTextConsultDetail.setVisibility(View.VISIBLE);
                currentCounseling.setCounselingType(1);
                Log.d("CounselingSign", "Selected: Provide consult");

                // อัพเดท detail ที่มีอยู่ใน EditText
                updateConsultDetail();
            } else if (checkedId == R.id.radioButtonSendToDoctor) {
                editTextConsultDetail.setVisibility(View.GONE);
                editTextConsultDetail.setText("");
                currentCounseling.setCounselingType(2);
                currentCounseling.setDetail("");
                Log.d("CounselingSign", "Selected: Send to doctor");

                // ล้างข้อมูล detail
                counselingLiveData.setConsultDetail("");
                sharedViewModel.setCounselingLiveData(counselingLiveData);
                dataPasser.onCounselingDataPass(currentCounseling);
            } else {
                return;
            }

            // อัพเดทข้อมูล
//            counselingLiveData.setCounselingType(currentCounseling.getCounselingType());
//            counselingLiveData.setConsultDetail(currentCounseling.getDetail());
            counselingLiveData.setCounselingType(currentCounseling.getCounselingType());
            sharedViewModel.setCounselingLiveData(counselingLiveData);
        });
    }

    private void loadSignatureDataSafely(CounselingInfo counseling) {
        Log.d("CounselingSign", "=== Starting loadSignatureDataSafely ===");

        // โหลดลายเซ็นผู้รับบริการ
        if (counseling.getPatientSignature() != null && counseling.getPatientSignature().length > 0) {
            Log.d("CounselingSign", "Loading patient signature, size: " + counseling.getPatientSignature().length);

            // รอ 500ms แล้วค่อยโหลด
            signatureViewPatient.postDelayed(() -> {
                try {
                    signatureViewPatient.setSignatureSafely(counseling.getPatientSignature());
                    Log.d("CounselingSign", "Patient signature loaded successfully");

                    // ตรวจสอบหลังจากโหลดแล้ว 1 วินาที
                    signatureViewPatient.postDelayed(() -> {
                        if (signatureViewPatient.isEmpty()) {
                            Log.w("CounselingSign", "Patient signature disappeared, retrying...");
                            signatureViewPatient.setSignatureSafely(counseling.getPatientSignature());
                        }
                    }, 1000);

                } catch (Exception e) {
                    Log.e("CounselingSign", "Error loading patient signature: " + e.getMessage(), e);
                }
            }, 500);
        } else {
            Log.d("CounselingSign", "No patient signature to load");
        }

        // โหลดลายเซ็นผู้ให้บริการ
        if (counseling.getProviderSignature() != null && counseling.getProviderSignature().length > 0) {
            Log.d("CounselingSign", "Loading provider signature, size: " + counseling.getProviderSignature().length);

            // รอ 700ms แล้วค่อยโหลด (เพื่อไม่ให้ชนกับ patient signature)
            signatureViewProvider.postDelayed(() -> {
                try {
                    signatureViewProvider.setSignatureSafely(counseling.getProviderSignature());
                    Log.d("CounselingSign", "Provider signature loaded successfully");

                    // ตรวจสอบหลังจากโหลดแล้ว 1 วินาที
                    signatureViewProvider.postDelayed(() -> {
                        if (signatureViewProvider.isEmpty()) {
                            Log.w("CounselingSign", "Provider signature disappeared, retrying...");
                            signatureViewProvider.setSignatureSafely(counseling.getProviderSignature());
                        }
                    }, 1000);

                } catch (Exception e) {
                    Log.e("CounselingSign", "Error loading provider signature: " + e.getMessage(), e);
                }
            }, 700);
        } else {
            Log.d("CounselingSign", "No provider signature to load");
        }

        Log.d("CounselingSign", "=== End loadSignatureDataSafely ===");
    }

    private void startSignatureMonitoring() {
        Log.d("CounselingSign", "Starting signature monitoring");

        // ตรวจสอบทุก 5 วินาที
        android.os.Handler handler = new android.os.Handler();
        Runnable signatureChecker = new Runnable() {
            @Override
            public void run() {
                // ตรวจสอบเฉพาะเมื่อ Fragment visible และมีข้อมูลลายเซ็นที่ควรแสดง
                if (isVisible() && getUserVisibleHint() && currentCounseling != null) {

                    // ตรวจสอบลายเซ็นผู้รับบริการ
                    if (currentCounseling.getPatientSignature() != null &&
                            currentCounseling.getPatientSignature().length > 0 &&
                            signatureViewPatient.isEmpty()) {

                        Log.w("CounselingSign", "Patient signature missing detected - auto fixing");
                        signatureViewPatient.setSignatureSafely(currentCounseling.getPatientSignature());
                    }

                    // ตรวจสอบลายเซ็นผู้ให้บริการ
                    if (currentCounseling.getProviderSignature() != null &&
                            currentCounseling.getProviderSignature().length > 0 &&
                            signatureViewProvider.isEmpty()) {

                        Log.w("CounselingSign", "Provider signature missing detected - auto fixing");
                        signatureViewProvider.setSignatureSafely(currentCounseling.getProviderSignature());
                    }
                }

                // ตรวจสอบอีกครั้งใน 5 วินาที
                handler.postDelayed(this, 5000);
            }
        };

        // เริ่มตรวจสอบหลังจาก 2 วินาที
        handler.postDelayed(signatureChecker, 2000);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("CounselingSign", "Fragment onDestroyView - stopping signature monitoring");

        // หยุดการตรวจสอบ (handler จะถูกล้างเมื่อ view ถูกทำลาย)
    }
    private void testSaveOrUpdateMethod() {
        Log.d("CounselingSign", "=== TESTING SAVE OR UPDATE METHOD ===");

        if (visitNo != null && !visitNo.isEmpty()) {
            // ตรวจสอบว่า visitId มีอยู่หรือไม่
            boolean exists = counselingDao.isVisitIdExists(visitNo);
            Log.d("CounselingSign", "visitNo " + visitNo + " exists: " + exists);

            if (exists) {
                Log.d("CounselingSign", "Will perform UPDATE operation");
            } else {
                Log.d("CounselingSign", "Will perform INSERT operation");
            }

            // ทดสอบการสร้าง CounselingInfo object สำหรับทดสอบ
            CounselingInfo testCounseling = new CounselingInfo();
            testCounseling.setVisitNo(visitNo);
            testCounseling.setPersonId(personId);
            testCounseling.setCounselingType(1);
            testCounseling.setDetail("Test detail - " + System.currentTimeMillis());
            testCounseling.setCreatedBy(sessionManager.getUsername());

            // ลองเพิ่มลายเซ็นทดสอบ (ถ้ามี)
            if (!signatureViewPatient.isEmpty()) {
                testCounseling.setPatientSignature(signatureViewPatient.getSignatureAsByteArray());
            }
            if (!signatureViewProvider.isEmpty()) {
                testCounseling.setProviderSignature(signatureViewProvider.getSignatureAsByteArray());
            }

            Log.d("CounselingSign", "Testing saveOrUpdateCounseling...");
            long result = counselingDao.saveOrUpdateCounseling(testCounseling);
            Log.d("CounselingSign", "Test result: " + result);

            if (result > 0) {
                Toast.makeText(getContext(), "Test successful! ID: " + result, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Test failed!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.w("CounselingSign", "Cannot test - visitId is null or empty");
            Toast.makeText(getContext(), "Cannot test - visitId is null or empty", Toast.LENGTH_SHORT).show();
        }

        Log.d("CounselingSign", "=== END TESTING SAVE OR UPDATE METHOD ===");
    }

    // เพิ่มเมธอดสำหรับบังคับดึงและบันทึกลายเซ็น (ย้ายมาจาก artifact ก่อนหน้า)
    private void forceGetAndSaveSignatures() {
        Log.d("CounselingSign", "=== FORCE GET AND SAVE SIGNATURES ===");

        try {
            // บังคับดึงลายเซ็นโดยไม่ตรวจสอบ isEmpty()
            byte[] patientSig = signatureViewPatient.getSignatureAsByteArray();
            byte[] providerSig = signatureViewProvider.getSignatureAsByteArray();

            Log.d("CounselingSign", "Force Patient signature: " +
                    (patientSig != null ? patientSig.length + " bytes" : "null"));
            Log.d("CounselingSign", "Force Provider signature: " +
                    (providerSig != null ? providerSig.length + " bytes" : "null"));

            // ตั้งค่าข้อมูลพื้นฐาน
            if (radioButtonProvideConsult.isChecked()) {
                currentCounseling.setCounselingType(1);
                currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
            } else {
                currentCounseling.setCounselingType(2);
                currentCounseling.setDetail("");
            }

            // บังคับบันทึกลายเซ็น
            currentCounseling.setPatientSignature(patientSig);
            currentCounseling.setProviderSignature(providerSig);

            // ตั้งค่าข้อมูลอื่นๆ
            if (personId != null) currentCounseling.setPersonId(personId);
            if (visitNo != null) currentCounseling.setVisitNo(visitNo);
            currentCounseling.setCreatedBy(sessionManager.getUsername());

            // บันทึกลงฐานข้อมูล
            long resultId = counselingDao.saveOrUpdateCounseling(currentCounseling);

            if (resultId > 0) {
                currentCounseling.setId(resultId);
                Toast.makeText(getContext(), "Force save successful! ID: " + resultId, Toast.LENGTH_SHORT).show();
                Log.d("CounselingSign", "Force save successful with ID: " + resultId);

                // อัพเดท LiveData
                counselingLiveData.setPatientSignature(patientSig);
                counselingLiveData.setProviderSignature(providerSig);
                sharedViewModel.setCounselingLiveData(counselingLiveData);
                dataPasser.onCounselingDataPass(currentCounseling);

                // รีโหลดข้อมูลเพื่อยืนยัน
                reloadDataFromDatabase();
            } else {
                Toast.makeText(getContext(), "Force save failed!", Toast.LENGTH_SHORT).show();
                Log.e("CounselingSign", "Force save failed");
            }

        } catch (Exception e) {
            Log.e("CounselingSign", "Error in force save: " + e.getMessage(), e);
            Toast.makeText(getContext(), "Error in force save: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        Log.d("CounselingSign", "=== END FORCE GET AND SAVE SIGNATURES ===");
    }

    // เพิ่มเมธอดสำหรับลบข้อมูลทั้งหมด (ย้ายมาจาก artifact ก่อนหน้า)
    private void clearAllData() {
        Log.d("CounselingSign", "=== CLEAR ALL DATA ===");

        // แสดง Dialog ยืนยัน
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Clear All Data")
                .setMessage("Are you sure you want to clear all data?\n\nThis will:\n- Clear all signatures\n- Reset form\n- Delete database records\n\nThis action cannot be undone!")
                .setPositiveButton("Yes, Clear All", (dialog, which) -> {
                    try {
                        // ลบลายเซ็น
                        signatureViewPatient.clear();
                        signatureViewProvider.clear();

                        // รีเซ็ตฟอร์ม
                        radioGroupCounseling.clearCheck();
                        editTextConsultDetail.setText("");
                        editTextConsultDetail.setVisibility(View.GONE);

                        // ลบข้อมูลจากฐานข้อมูล (ระวัง!)
                        if (visitNo != null && !visitNo.isEmpty()) {
                            int deleted = counselingDao.deleteCounselingByVisitId(visitNo);
                            Log.d("CounselingSign", "Deleted " + deleted + " records for visitNo: " + visitNo);
                        }

                        // รีเซ็ต currentCounseling
                        currentCounseling = new CounselingInfo();
                        if (personId != null) currentCounseling.setPersonId(personId);
                        if (visitNo != null) currentCounseling.setVisitNo(visitNo);

                        // ล้าง LiveData
                        counselingLiveData = new CounselingLiveData();
                        sharedViewModel.setCounselingLiveData(counselingLiveData);
                        dataPasser.onCounselingDataPass(currentCounseling);

                        Toast.makeText(getContext(), "All data cleared!", Toast.LENGTH_SHORT).show();
                        Log.d("CounselingSign", "All data cleared successfully");

                    } catch (Exception e) {
                        Log.e("CounselingSign", "Error clearing data: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Error clearing data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

        Log.d("CounselingSign", "=== END CLEAR ALL DATA ===");
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
        editTextReferralDetail = view.findViewById(R.id.editTextReferralDetail);
        signatureViewPatient = view.findViewById(R.id.signatureViewPatient);
        signatureViewProvider = view.findViewById(R.id.signatureViewProvider);
        btnClearPatientSignature = view.findViewById(R.id.btnClearPatientSignature);
        btnClearProviderSignature = view.findViewById(R.id.btnClearProviderSignature);

    }

    private void setupListeners() {
        // RadioGroup Listener - แก้ไขให้ไม่กลับไปเลือกอันเดิม
        radioGroupCounseling.setOnCheckedChangeListener(null); // ล้าง listener เดิมก่อน
        radioGroupCounseling.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d("CounselingSign", "RadioGroup checked changed: " + checkedId);

            if (checkedId == R.id.radioButtonProvideConsult) {
                editTextConsultDetail.setVisibility(View.VISIBLE);
                currentCounseling.setCounselingType(1);
                currentCounseling.setReferralDetail("");
                editTextReferralDetail.setVisibility(View.GONE);
                Log.d("CounselingSign", "Selected: Provide consult");
            } else if (checkedId == R.id.radioButtonSendToDoctor) {
                editTextConsultDetail.setVisibility(View.GONE);
                editTextConsultDetail.setText(""); // ล้างข้อความเมื่อเลือกส่งต่อแพทย์
                currentCounseling.setCounselingType(2);
                currentCounseling.setDetail("");
                editTextReferralDetail.setVisibility(View.VISIBLE);
                Log.d("CounselingSign", "Selected: Send to doctor");
                updateReferralDetail();
            } else {
                Log.w("CounselingSign", "Unknown or no selection: " + checkedId);
                return;
            }

            // อัพเดทข้อมูล
            counselingLiveData.setCounselingType(currentCounseling.getCounselingType());
            counselingLiveData.setConsultDetail(currentCounseling.getDetail());
            sharedViewModel.setCounselingLiveData(counselingLiveData);
            dataPasser.onCounselingDataPass(currentCounseling);
        });
        editTextReferralDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                updateReferralDetail();
            }
        });

        editTextReferralDetail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                updateReferralDetail();
            }
        });

        // ลบ Individual RadioButton OnClickListeners ออกทั้งหมด
        // เพราะมันขัดแย้งกับ RadioGroup listener

        // ปุ่มล้างลายเซ็น - แก้ไขให้ทำงานชัดเจน
        if (btnClearPatientSignature != null) {
            btnClearPatientSignature.setOnClickListener(null); // ล้าง listener เดิม
            btnClearPatientSignature.setOnClickListener(v -> {
                Log.d("CounselingSign", "Clear patient signature button clicked");

                if (signatureViewPatient != null) {
                    signatureViewPatient.clear();
                    currentCounseling.setPatientSignature(null);

                    // อัพเดท LiveData
                    counselingLiveData.setPatientSignature(null);
                    sharedViewModel.setCounselingLiveData(counselingLiveData);
                    dataPasser.onCounselingDataPass(currentCounseling);

                    Toast.makeText(getContext(), "ลายเซ็นผู้รับบริการถูกล้างแล้ว", Toast.LENGTH_SHORT).show();
                    Log.d("CounselingSign", "Patient signature cleared");
                }
            });
        }

        if (btnClearProviderSignature != null) {
            btnClearProviderSignature.setOnClickListener(null); // ล้าง listener เดิม
            btnClearProviderSignature.setOnClickListener(v -> {
                Log.d("CounselingSign", "Clear provider signature button clicked");

                if (signatureViewProvider != null) {
                    signatureViewProvider.clear();
                    currentCounseling.setProviderSignature(null);

                    // อัพเดท LiveData
                    counselingLiveData.setProviderSignature(null);
                    sharedViewModel.setCounselingLiveData(counselingLiveData);
                    dataPasser.onCounselingDataPass(currentCounseling);

                    Toast.makeText(getContext(), "ลายเซ็นผู้ให้บริการถูกล้างแล้ว", Toast.LENGTH_SHORT).show();
                    Log.d("CounselingSign", "Provider signature cleared");
                }
            });
        }

        // EditText listeners
        editTextConsultDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                // ไม่ต้องบันทึกทุกครั้งที่พิมพ์
                String detail = editable.toString().trim();
                currentCounseling.setDetail(detail);

                // ส่งข้อมูลไปยัง LiveData และ dataPasser ทันที
                counselingLiveData.setConsultDetail(detail);
                sharedViewModel.setCounselingLiveData(counselingLiveData);
                dataPasser.onCounselingDataPass(currentCounseling);

                Log.d("CounselingSign", "EditText changed, detail updated: " + detail);
            }
        });

        editTextConsultDetail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                // เมื่อ lose focus ให้อัพเดทข้อมูลอีกครั้งเพื่อให้แน่ใจ
                String detail = editTextConsultDetail.getText().toString().trim();
                currentCounseling.setDetail(detail);
                counselingLiveData.setConsultDetail(detail);
                sharedViewModel.setCounselingLiveData(counselingLiveData);
                dataPasser.onCounselingDataPass(currentCounseling);

                Log.d("CounselingSign", "EditText lost focus, detail updated: " + detail);
            }
        });
    }
    private void updateReferralDetail() {
        String referralDetail = editTextReferralDetail.getText().toString().trim();
        currentCounseling.setReferralDetail(referralDetail);

        // อัพเดท LiveData
        counselingLiveData.setReferralDetail(referralDetail);
        sharedViewModel.setCounselingLiveData(counselingLiveData);

        // ส่งข้อมูลไปยัง Activity ผ่าน dataPasser
        dataPasser.onCounselingDataPass(currentCounseling);

        Log.d("CounselingSign", "Referral detail updated: " + referralDetail);
    }
    // เพิ่มเมธอดสำหรับอัพเดทข้อมูล Detail เมื่อมีการเปลี่ยนแปลง
    private void updateConsultDetail() {
        String detail = editTextConsultDetail.getText().toString().trim();
        currentCounseling.setDetail(detail);

        // อัพเดท LiveData
        counselingLiveData.setConsultDetail(detail);
        sharedViewModel.setCounselingLiveData(counselingLiveData);

        // ส่งข้อมูลไปยัง Activity ผ่าน dataPasser
        dataPasser.onCounselingDataPass(currentCounseling);

        Log.d("CounselingSign", "Consult detail updated: " + detail);
    }
    public void testRadioButtonInteraction() {
        Log.d("CounselingSign", "=== TESTING RADIO BUTTON INTERACTION ===");

        // ทดสอบการเปลี่ยน RadioButton แบบ step-by-step
        radioButtonProvideConsult.post(() -> {
            Log.d("CounselingSign", "Step 1: Setting Provide Consult");
            radioButtonProvideConsult.setChecked(true);

            // รอ 2 วินาทีแล้วเปลี่ยน
            radioButtonProvideConsult.postDelayed(() -> {
                Log.d("CounselingSign", "Step 2: Setting Send To Doctor");
                radioButtonSendToDoctor.setChecked(true);

                // รอ 2 วินาทีแล้วเปลี่ยนกลับ
                radioButtonSendToDoctor.postDelayed(() -> {
                    Log.d("CounselingSign", "Step 3: Setting back to Provide Consult");
                    radioButtonProvideConsult.setChecked(true);

                    Log.d("CounselingSign", "Test completed");
                    Toast.makeText(getContext(), "RadioButton test completed", Toast.LENGTH_SHORT).show();
                }, 2000);
            }, 2000);
        });

        Log.d("CounselingSign", "=== END TESTING RADIO BUTTON INTERACTION ===");
    }
    public void testClearSignatureButtons() {
        Log.d("CounselingSign", "=== TESTING CLEAR SIGNATURE BUTTONS ===");

        // ตรวจสอบสถานะปุ่มล้างลายเซ็น
        if (btnClearPatientSignature != null) {
            Log.d("CounselingSign", "Patient clear button:");
            Log.d("CounselingSign", "  - isEnabled: " + btnClearPatientSignature.isEnabled());
            Log.d("CounselingSign", "  - isClickable: " + btnClearPatientSignature.isClickable());
            Log.d("CounselingSign", "  - visibility: " + btnClearPatientSignature.getVisibility());

            // ทดสอบการคลิก
            btnClearPatientSignature.performClick();
        }

        if (btnClearProviderSignature != null) {
            Log.d("CounselingSign", "Provider clear button:");
            Log.d("CounselingSign", "  - isEnabled: " + btnClearProviderSignature.isEnabled());
            Log.d("CounselingSign", "  - isClickable: " + btnClearProviderSignature.isClickable());
            Log.d("CounselingSign", "  - visibility: " + btnClearProviderSignature.getVisibility());

            // รอ 2 วินาทีแล้วทดสอบการคลิก
            btnClearProviderSignature.postDelayed(() -> {
                btnClearProviderSignature.performClick();
            }, 2000);
        }

        Log.d("CounselingSign", "=== END TESTING CLEAR SIGNATURE BUTTONS ===");
    }

    public void debugFirstLoadIssue() {
        Log.d("CounselingSign", "=== DEBUG FIRST LOAD ISSUE ===");
        Log.d("CounselingSign", "Fragment isVisible: " + isVisible());
        Log.d("CounselingSign", "Fragment getUserVisibleHint: " + getUserVisibleHint());
        Log.d("CounselingSign", "visitNo: " + visitNo);
        Log.d("CounselingSign", "PersonId: " + personId);

        if (signatureViewPatient != null) {
            Log.d("CounselingSign", "Patient SignatureView size: " +
                    signatureViewPatient.getWidth() + "x" + signatureViewPatient.getHeight());
            Log.d("CounselingSign", "Patient SignatureView visibility: " + signatureViewPatient.getVisibility());
        }

        if (signatureViewProvider != null) {
            Log.d("CounselingSign", "Provider SignatureView size: " +
                    signatureViewProvider.getWidth() + "x" + signatureViewProvider.getHeight());
            Log.d("CounselingSign", "Provider SignatureView visibility: " + signatureViewProvider.getVisibility());
        }

        if (currentCounseling != null) {
            Log.d("CounselingSign", "Current counseling has patient signature: " +
                    (currentCounseling.getPatientSignature() != null));
            Log.d("CounselingSign", "Current counseling has provider signature: " +
                    (currentCounseling.getProviderSignature() != null));
        }

        Log.d("CounselingSign", "=== END DEBUG FIRST LOAD ISSUE ===");
    }
    private void setupSignatureViewBehavior() {
        // จัดการ Touch Events สำหรับ SignatureView ผู้รับบริการ
        signatureViewPatient.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSigningPatient = true;
                    disableScrolling();
                    Log.d("CounselingSign", "Patient signature: Started drawing");
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isSigningPatient = false;
                    enableScrolling();

                    // เพิ่มเวลารอและตรวจสอบอย่างละเอียด
                    signatureViewPatient.postDelayed(() -> {
                        boolean isEmpty = signatureViewPatient.isEmpty();
                        byte[] signature = signatureViewPatient.getSignatureAsByteArray();

                        Log.d("CounselingSign", "Patient signature check:");
                        Log.d("CounselingSign", "  - isEmpty(): " + isEmpty);
                        Log.d("CounselingSign", "  - signature bytes: " + (signature != null ? signature.length : "null"));

                        // ใช้เงื่อนไขที่เข้มงวดกว่า
                        if (!isEmpty || (signature != null && signature.length > 0)) {
                            currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
                            currentCounseling.setPatientSignature(signature);

                            Log.d("CounselingSign", "Patient signature saved: " +
                                    (signature != null ? signature.length + " bytes" : "null"));

                            // อัพเดท LiveData
                            counselingLiveData.setPatientSignature(signature);
                            sharedViewModel.setCounselingLiveData(counselingLiveData);
                            dataPasser.onCounselingDataPass(currentCounseling);

                            // บันทึกทันทีเพื่อป้องกันการสูญหาย
                            saveSignatureToDatabase();
                        } else {
                            Log.w("CounselingSign", "Patient signature is empty - not saving");
                        }
                    }, 300); // เพิ่มเวลารอเป็น 300ms
                    break;
            }
            return false;
        });

        // จัดการ Touch Events สำหรับ SignatureView ผู้ให้บริการ
        signatureViewProvider.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isSigningProvider = true;
                    disableScrolling();
                    Log.d("CounselingSign", "Provider signature: Started drawing");
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    isSigningProvider = false;
                    enableScrolling();

                    // เพิ่มเวลารอและตรวจสอบอย่างละเอียด
                    signatureViewProvider.postDelayed(() -> {
                        boolean isEmpty = signatureViewProvider.isEmpty();
                        byte[] signature = signatureViewProvider.getSignatureAsByteArray();

                        Log.d("CounselingSign", "Provider signature check:");
                        Log.d("CounselingSign", "  - isEmpty(): " + isEmpty);
                        Log.d("CounselingSign", "  - signature bytes: " + (signature != null ? signature.length : "null"));

                        // ใช้เงื่อนไขที่เข้มงวดกว่า
                        if (!isEmpty || (signature != null && signature.length > 0)) {
                            currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
                            currentCounseling.setProviderSignature(signature);

                            Log.d("CounselingSign", "Provider signature saved: " +
                                    (signature != null ? signature.length + " bytes" : "null"));

                            // อัพเดท LiveData
                            counselingLiveData.setProviderSignature(signature);
                            sharedViewModel.setCounselingLiveData(counselingLiveData);
                            dataPasser.onCounselingDataPass(currentCounseling);

                            // บันทึกทันทีเพื่อป้องกันการสูญหาย
                            saveSignatureToDatabase();
                        } else {
                            Log.w("CounselingSign", "Provider signature is empty - not saving");
                        }
                    }, 300); // เพิ่มเวลารอเป็น 300ms
                    break;
            }
            return false;
        });
    }
    private void saveSignatureToDatabase() {
        // บันทึกแบบ background เพื่อไม่ให้กระทบประสิทธิภาพ
        new Thread(() -> {
            try {
                if (currentCounseling != null && visitNo != null && !visitNo.isEmpty()) {
                    // กำหนดข้อมูลพื้นฐาน
                    if (personId != null) currentCounseling.setPersonId(personId);
                    if (visitNo != null) currentCounseling.setVisitNo(visitNo);
                    currentCounseling.setCreatedBy(sessionManager.getUsername());

                    // บันทึกลงฐานข้อมูล
                    long resultId = counselingDao.saveOrUpdateCounseling(currentCounseling);

                    if (resultId > 0) {
                        currentCounseling.setId(resultId);
                        Log.d("CounselingSign", "Auto-saved signature to database with ID: " + resultId);
                    } else {
                        Log.e("CounselingSign", "Failed to auto-save signature to database");
                    }
                }
            } catch (Exception e) {
                Log.e("CounselingSign", "Error auto-saving signature: " + e.getMessage(), e);
            }
        }).start();
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


    private void populateForm(CounselingInfo counseling) {
        Log.d("CounselingSign", "=== Starting populateForm ===");
        Log.d("CounselingSign", "Counseling ID: " + counseling.getId());
        Log.d("CounselingSign", "Counseling Type: " + counseling.getCounselingType());
        Log.d("CounselingSign", "Detail: " + counseling.getDetail());

        // แสดงข้อมูลที่มีอยู่ในฟอร์ม
        if (counseling.getCounselingType() == 1) {
            radioButtonProvideConsult.setChecked(true);
            editTextConsultDetail.setText(counseling.getDetail());
            editTextConsultDetail.setVisibility(View.VISIBLE);
            Log.d("CounselingSign", "Set radio to provide consult");
        } else if (counseling.getCounselingType() == 2) {
            radioButtonSendToDoctor.setChecked(true);
            editTextConsultDetail.setVisibility(View.GONE);
            Log.d("CounselingSign", "Set radio to send to doctor");
        }

        // *** ไม่ล้างลายเซ็นเดิม ให้ตั้งค่าทับไปเลย ***
        // signatureViewPatient.clear();  // ลบบรรทัดนี้
        // signatureViewProvider.clear(); // ลบบรรทัดนี้

        // โหลดลายเซ็นถ้ามี
        loadSignatureData(counseling);

        Log.d("CounselingSign", "=== End populateForm ===");
    }
    /**
     * เมธอดแยกสำหรับโหลดลายเซ็นเพื่อป้องกันการชนกัน
     */
    private void loadSignatureData(CounselingInfo counseling) {
        // รอให้ View พร้อมก่อนโหลดลายเซ็น
        signatureViewPatient.post(() -> {
            // ถ้ามีลายเซ็นผู้รับบริการ
            if (counseling.getPatientSignature() != null && counseling.getPatientSignature().length > 0) {
                Log.d("CounselingSign", "Setting patient signature, size: " + counseling.getPatientSignature().length);
                try {
                    signatureViewPatient.setSignatureSafely(counseling.getPatientSignature());
                    Log.d("CounselingSign", "Patient signature set successfully");
                } catch (Exception e) {
                    Log.e("CounselingSign", "Error setting patient signature: " + e.getMessage(), e);
                }
            } else {
                Log.d("CounselingSign", "No patient signature to display");
            }
        });

        // โหลดลายเซ็นผู้ให้บริการแยกต่างหาก
        signatureViewProvider.post(() -> {
            if (counseling.getProviderSignature() != null && counseling.getProviderSignature().length > 0) {
                Log.d("CounselingSign", "Setting provider signature, size: " + counseling.getProviderSignature().length);
                try {
                    signatureViewProvider.setSignatureSafely(counseling.getProviderSignature());
                    Log.d("CounselingSign", "Provider signature set successfully");
                } catch (Exception e) {
                    Log.e("CounselingSign", "Error setting provider signature: " + e.getMessage(), e);
                }
            } else {
                Log.d("CounselingSign", "No provider signature to display");
            }
        });
    }
    private void loadData() {
        Log.d("CounselingSign", "=== Starting loadData ===");
//        counselingDao.deleteAllCounseling();
        // โหลดข้อมูลจาก ViewModel ก่อน
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCounselingLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                Log.d("CounselingSign", "Received data from ViewModel - PersonId: " + data.getPersonId() + ", visitNo: " + data.getVisitNo());
                counselingLiveData = data;
                personId = data.getPersonId();
                visitNo = data.getVisitNo();
            }
        });

        // โหลดข้อมูลจากฐานข้อมูลตรงๆ
        if (visitNo != null && !visitNo.isEmpty()) {
            Log.d("CounselingSign", "Loading data for visitNo: " + visitNo);

            // ใช้เมธอดใหม่ที่รับประกันได้ว่าจะได้ข้อมูลพร้อมลายเซ็น
            CounselingInfo existingCounseling = counselingDao.getCounselingWithSignaturesByVisitId(visitNo);

            if (existingCounseling != null) {
                currentCounseling = existingCounseling;
                Log.d("CounselingSign", "Found existing record with ID: " + currentCounseling.getId());
                populateForm(currentCounseling);
            } else {
                Log.d("CounselingSign", "No existing record found for visitNo: " + visitNo);
                // ใช้ข้อมูลเริ่มต้นที่สร้างไว้แล้วใน onCreate()
            }

        } else if (personId != null && !personId.isEmpty()) {
            Log.d("CounselingSign", "Loading data for personId: " + personId);
            List<CounselingInfo> counselingList = counselingDao.getCounselingByPersonId(personId);
            Log.d("CounselingSign", "Found " + counselingList.size() + " records");

            if (!counselingList.isEmpty()) {
                currentCounseling = counselingList.get(0);
                populateForm(currentCounseling);
            }
        }

        Log.d("CounselingSign", "=== End loadData ===");
    }
    public void checkDataStatus() {
        Log.d("CounselingSign", "=== Checking Data Status ===");

        if (visitNo != null && !visitNo.isEmpty()) {
            boolean exists = counselingDao.isVisitIdExists(visitNo);
            Log.d("CounselingSign", "visitNo " + visitNo + " exists in database: " + exists);

            if (exists) {
                CounselingInfo existing = counselingDao.getCounselingWithSignaturesByVisitId(visitNo);
                if (existing != null) {
                    Log.d("CounselingSign", "Existing record details:");
                    Log.d("CounselingSign", "  - ID: " + existing.getId());
                    Log.d("CounselingSign", "  - Type: " + existing.getCounselingType());
                    Log.d("CounselingSign", "  - Detail: " + existing.getDetail());
                    Log.d("CounselingSign", "  - Patient signature: " +
                            (existing.getPatientSignature() != null ? existing.getPatientSignature().length + " bytes" : "null"));
                    Log.d("CounselingSign", "  - Provider signature: " +
                            (existing.getProviderSignature() != null ? existing.getProviderSignature().length + " bytes" : "null"));
                }
            }
        } else {
            Log.w("CounselingSign", "VisitNo is null or empty - cannot check status");
        }

        Log.d("CounselingSign", "=== End Checking Data Status ===");
    }
    // เพิ่ม public method สำหรับ save (เรียกจากภายนอก)
    public boolean saveData() {
        Log.d("CounselingSign", "Public saveData() called");
        if (validateForm()) {
            saveFormData();
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("CounselingSign", "Fragment onResume");

        // รอสักครู่แล้วค่อย refresh ลายเซ็น
        if (signatureViewPatient != null && signatureViewProvider != null) {
            signatureViewPatient.postDelayed(() -> {
                Log.d("CounselingSign", "Refreshing signatures in onResume");
                signatureViewPatient.refreshSignature();
                signatureViewProvider.refreshSignature();
            }, 200);
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d("CounselingSign", "Fragment onPause");

        // บันทึกลายเซ็นปัจจุบันก่อน pause เพื่อป้องกันการสูญหาย
        if (signatureViewPatient != null && !signatureViewPatient.isEmpty()) {
            byte[] patientSig = signatureViewPatient.getSignatureAsByteArray();
            if (patientSig != null && patientSig.length > 0) {
                currentCounseling.setPatientSignature(patientSig);
                Log.d("CounselingSign", "Saved patient signature on pause");
            }
        }

        if (signatureViewProvider != null && !signatureViewProvider.isEmpty()) {
            byte[] providerSig = signatureViewProvider.getSignatureAsByteArray();
            if (providerSig != null && providerSig.length > 0) {
                currentCounseling.setProviderSignature(providerSig);
                Log.d("CounselingSign", "Saved provider signature on pause");
            }
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d("CounselingSign", "setUserVisibleHint: " + isVisibleToUser);

        if (isVisibleToUser && signatureViewPatient != null && signatureViewProvider != null) {
            // รอให้ View เสถียรก่อนค่อย refresh
            signatureViewPatient.postDelayed(() -> {
                Log.d("CounselingSign", "Refreshing signatures in setUserVisibleHint");
                signatureViewPatient.refreshSignature();
                signatureViewProvider.refreshSignature();
            }, 300);
        }
    }
    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d("CounselingSign", "onViewStateRestored");

        // รอให้ View พร้อมแล้วค่อย restore ลายเซ็น
        if (currentCounseling != null) {
            signatureViewPatient.postDelayed(() -> {
                if (currentCounseling.getPatientSignature() != null && currentCounseling.getPatientSignature().length > 0) {
                    Log.d("CounselingSign", "Restoring patient signature after view state restored");
                    signatureViewPatient.setSignatureSafely(currentCounseling.getPatientSignature());
                }

                if (currentCounseling.getProviderSignature() != null && currentCounseling.getProviderSignature().length > 0) {
                    Log.d("CounselingSign", "Restoring provider signature after view state restored");
                    signatureViewProvider.setSignatureSafely(currentCounseling.getProviderSignature());
                }
            }, 500);
        }
    }
    public void debugSignatureViews() {
        Log.d("CounselingSign", "=== DEBUG SIGNATURE VIEWS ===");

        if (signatureViewPatient != null) {
            Log.d("CounselingSign", "Patient SignatureView:");
            Log.d("CounselingSign", "  - Size: " + signatureViewPatient.getWidth() + "x" + signatureViewPatient.getHeight());
            Log.d("CounselingSign", "  - isEmpty: " + signatureViewPatient.isEmpty());

            // เรียก debug method ของ SignatureView
            signatureViewPatient.debugSignatureState();

            byte[] patientSig = signatureViewPatient.getSignatureAsByteArray();
            Log.d("CounselingSign", "  - Signature bytes: " + (patientSig != null ? patientSig.length : "null"));
        } else {
            Log.d("CounselingSign", "Patient SignatureView is null");
        }

        if (signatureViewProvider != null) {
            Log.d("CounselingSign", "Provider SignatureView:");
            Log.d("CounselingSign", "  - Size: " + signatureViewProvider.getWidth() + "x" + signatureViewProvider.getHeight());
            Log.d("CounselingSign", "  - isEmpty: " + signatureViewProvider.isEmpty());

            // เรียก debug method ของ SignatureView
            signatureViewProvider.debugSignatureState();

            byte[] providerSig = signatureViewProvider.getSignatureAsByteArray();
            Log.d("CounselingSign", "  - Signature bytes: " + (providerSig != null ? providerSig.length : "null"));
        } else {
            Log.d("CounselingSign", "Provider SignatureView is null");
        }

        Log.d("CounselingSign", "Current counseling data:");
        Log.d("CounselingSign", "  - ID: " + currentCounseling.getId());
        Log.d("CounselingSign", "  - PersonID: " + currentCounseling.getPersonId());
        Log.d("CounselingSign", "  - VisitNo: " + currentCounseling.getVisitNo());
        Log.d("CounselingSign", "  - Type: " + currentCounseling.getCounselingType());
        Log.d("CounselingSign", "  - Patient sig stored: " + (currentCounseling.getPatientSignature() != null ? currentCounseling.getPatientSignature().length : "null"));
        Log.d("CounselingSign", "  - Provider sig stored: " + (currentCounseling.getProviderSignature() != null ? currentCounseling.getProviderSignature().length : "null"));

        Log.d("CounselingSign", "=== END DEBUG ===");
    }
    // เพิ่มเมธอดสำหรับบังคับ restore ลายเซ็น
    public void forceRestoreSignatures() {
        Log.d("CounselingSign", "=== FORCE RESTORE SIGNATURES ===");

        if (currentCounseling == null) {
            Log.w("CounselingSign", "Cannot restore - currentCounseling is null");
            return;
        }

        // Force restore patient signature
        if (currentCounseling.getPatientSignature() != null && currentCounseling.getPatientSignature().length > 0) {
            Log.d("CounselingSign", "Force restoring patient signature");
            signatureViewPatient.postDelayed(() -> {
                signatureViewPatient.setSignatureSafely(currentCounseling.getPatientSignature());
            }, 100);
        }

        // Force restore provider signature
        if (currentCounseling.getProviderSignature() != null && currentCounseling.getProviderSignature().length > 0) {
            Log.d("CounselingSign", "Force restoring provider signature");
            signatureViewProvider.postDelayed(() -> {
                signatureViewProvider.setSignatureSafely(currentCounseling.getProviderSignature());
            }, 200);
        }

        Log.d("CounselingSign", "=== END FORCE RESTORE SIGNATURES ===");
    }
    public void checkAndFixMissingSignatures() {
        Log.d("CounselingSign", "=== CHECK AND FIX MISSING SIGNATURES ===");

        if (currentCounseling == null) {
            Log.w("CounselingSign", "Cannot check - currentCounseling is null");
            return;
        }

        // ตรวจสอบลายเซ็นผู้รับบริการ
        if (currentCounseling.getPatientSignature() != null && currentCounseling.getPatientSignature().length > 0) {
            if (signatureViewPatient.isEmpty()) {
                Log.w("CounselingSign", "Patient signature missing from view, restoring...");
                signatureViewPatient.setSignatureSafely(currentCounseling.getPatientSignature());
            }
        }

        // ตรวจสอบลายเซ็นผู้ให้บริการ
        if (currentCounseling.getProviderSignature() != null && currentCounseling.getProviderSignature().length > 0) {
            if (signatureViewProvider.isEmpty()) {
                Log.w("CounselingSign", "Provider signature missing from view, restoring...");
                signatureViewProvider.setSignatureSafely(currentCounseling.getProviderSignature());
            }
        }

        Log.d("CounselingSign", "=== END CHECK AND FIX MISSING SIGNATURES ===");
    }
    private android.graphics.Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        try {
            return android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        } catch (Exception e) {
            Log.e("CounselingSign", "Error converting byte array to bitmap: " + e.getMessage(), e);
            return null;
        }
    }

    private boolean validateForm() {
        Log.d("CounselingSign", "=== VALIDATE FORM DEBUG ===");

        // Debug ข้อมูลปัจจุบัน
        debugSignatureViews();

        // ตรวจสอบความถูกต้องของข้อมูล
        if (radioGroupCounseling.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getContext(), "กรุณาเลือกประเภทการให้บริการ", Toast.LENGTH_SHORT).show();
            Log.w("CounselingSign", "Validation failed: No radio button selected");
            return false;
        }
        // ตรวจสอบ detail ตามประเภทที่เลือก
        if (radioButtonProvideConsult.isChecked()) {
            if (editTextConsultDetail.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "กรุณากรอกรายละเอียดคำแนะนำ", Toast.LENGTH_SHORT).show();
                editTextConsultDetail.requestFocus();
                return false;
            }
        } else if (radioButtonSendToDoctor.isChecked()) {
            if (editTextReferralDetail.getText().toString().trim().isEmpty()) {
                Toast.makeText(getContext(), "กรุณากรอกรายละเอียดการส่งต่อ", Toast.LENGTH_SHORT).show();
                editTextReferralDetail.requestFocus();
                return false;
            }
        }

        if (radioButtonProvideConsult.isChecked() && editTextConsultDetail.getText().toString().trim().isEmpty()) {
            Toast.makeText(getContext(), "กรุณากรอกรายละเอียดคำแนะนำ", Toast.LENGTH_SHORT).show();
            editTextConsultDetail.requestFocus();
            Log.w("CounselingSign", "Validation failed: No detail text");
            return false;
        }

        // ตรวจสอบลายเซ็นผู้รับบริการ
        boolean patientSigEmpty = signatureViewPatient.isEmpty();
        byte[] patientSigBytes = signatureViewPatient.getSignatureAsByteArray();
        boolean hasPatientSignature = !patientSigEmpty || (patientSigBytes != null && patientSigBytes.length > 0);

        Log.d("CounselingSign", "Patient signature validation:");
        Log.d("CounselingSign", "  - isEmpty(): " + patientSigEmpty);
        Log.d("CounselingSign", "  - signature bytes: " + (patientSigBytes != null ? patientSigBytes.length : "null"));
        Log.d("CounselingSign", "  - hasSignature: " + hasPatientSignature);

        if (!hasPatientSignature) {
            Toast.makeText(getContext(), "กรุณาลงลายมือชื่อผู้รับบริการ", Toast.LENGTH_SHORT).show();
            Log.w("CounselingSign", "Validation failed: Patient signature empty");
            return false;
        }

        // ตรวจสอบลายเซ็นผู้ให้บริการ
        boolean providerSigEmpty = signatureViewProvider.isEmpty();
        byte[] providerSigBytes = signatureViewProvider.getSignatureAsByteArray();
        boolean hasProviderSignature = !providerSigEmpty || (providerSigBytes != null && providerSigBytes.length > 0);

        Log.d("CounselingSign", "Provider signature validation:");
        Log.d("CounselingSign", "  - isEmpty(): " + providerSigEmpty);
        Log.d("CounselingSign", "  - signature bytes: " + (providerSigBytes != null ? providerSigBytes.length : "null"));
        Log.d("CounselingSign", "  - hasSignature: " + hasProviderSignature);

        if (!hasProviderSignature) {
            Toast.makeText(getContext(), "กรุณาลงลายมือชื่อผู้ให้บริการ", Toast.LENGTH_SHORT).show();
            Log.w("CounselingSign", "Validation failed: Provider signature empty");
            return false;
        }

        Log.d("CounselingSign", "Validation passed");
        Log.d("CounselingSign", "=== END VALIDATE FORM DEBUG ===");
        return true;
    }

    private void saveFormData() {
        Log.d("CounselingSign", "=== Starting saveFormData ===");

        // กำหนดข้อมูลจากฟอร์ม
        if (radioButtonProvideConsult.isChecked()) {
            currentCounseling.setCounselingType(1);
            currentCounseling.setDetail(editTextConsultDetail.getText().toString().trim());
            currentCounseling.setReferralDetail("");
            Log.d("CounselingSign", "Counseling type: 1, Detail: " + currentCounseling.getDetail());
        } else {
            currentCounseling.setCounselingType(2);
            currentCounseling.setDetail("");
            currentCounseling.setReferralDetail(editTextReferralDetail.getText().toString().trim());
            Log.d("CounselingSign", "Counseling type: 2");
        }

        // บันทึกลายเซ็นผู้รับบริการ - ใช้การตรวจสอบที่แม่นยำกว่า
        boolean patientSigEmpty = signatureViewPatient.isEmpty();
        byte[] patientSignature = signatureViewPatient.getSignatureAsByteArray();
        boolean hasPatientSignature = !patientSigEmpty || (patientSignature != null && patientSignature.length > 0);

        Log.d("CounselingSign", "Patient signature analysis:");
        Log.d("CounselingSign", "  - isEmpty(): " + patientSigEmpty);
        Log.d("CounselingSign", "  - signature bytes: " + (patientSignature != null ? patientSignature.length : "null"));
        Log.d("CounselingSign", "  - hasSignature: " + hasPatientSignature);

        if (hasPatientSignature) {
            currentCounseling.setPatientSignature(patientSignature);
            Log.d("CounselingSign", "Patient signature captured and saved");
        } else {
            Log.w("CounselingSign", "Patient signature is empty!");
            currentCounseling.setPatientSignature(null);
        }

        // บันทึกลายเซ็นผู้ให้บริการ - ใช้การตรวจสอบที่แม่นยำกว่า
        boolean providerSigEmpty = signatureViewProvider.isEmpty();
        byte[] providerSignature = signatureViewProvider.getSignatureAsByteArray();
        boolean hasProviderSignature = !providerSigEmpty || (providerSignature != null && providerSignature.length > 0);

        Log.d("CounselingSign", "Provider signature analysis:");
        Log.d("CounselingSign", "  - isEmpty(): " + providerSigEmpty);
        Log.d("CounselingSign", "  - signature bytes: " + (providerSignature != null ? providerSignature.length : "null"));
        Log.d("CounselingSign", "  - hasSignature: " + hasProviderSignature);

        if (hasProviderSignature) {
            currentCounseling.setProviderSignature(providerSignature);
            Log.d("CounselingSign", "Provider signature captured and saved");
        } else {
            Log.w("CounselingSign", "Provider signature is empty!");
            currentCounseling.setProviderSignature(null);
        }

        // กำหนดค่าสำหรับบันทึก
        if (personId != null) {
            currentCounseling.setPersonId(personId);
        }
        if (visitNo != null) {
            currentCounseling.setVisitNo(visitNo);
        }

        // กำหนด username สำหรับการบันทึก (ใช้เป็นทั้ง created_by และ updated_by)
        String username = sessionManager.getUsername();
        currentCounseling.setCreatedBy(username);

        Log.d("CounselingSign", "PersonId: " + personId + ", visitNo: " + visitNo);
        Log.d("CounselingSign", "Username: " + username);

        // Debug: ตรวจสอบข้อมูลใน currentCounseling ก่อนบันทึก
        Log.d("CounselingSign", "=== Data before save ===");
        Log.d("CounselingSign", "Patient signature in object: " +
                (currentCounseling.getPatientSignature() != null ?
                        currentCounseling.getPatientSignature().length + " bytes" : "null"));
        Log.d("CounselingSign", "Provider signature in object: " +
                (currentCounseling.getProviderSignature() != null ?
                        currentCounseling.getProviderSignature().length + " bytes" : "null"));

        // อัปเดต LiveData
        counselingLiveData.setCounselingType(currentCounseling.getCounselingType());
        counselingLiveData.setConsultDetail(currentCounseling.getDetail());
        counselingLiveData.setPatientSignature(currentCounseling.getPatientSignature());
        counselingLiveData.setProviderSignature(currentCounseling.getProviderSignature());
        sharedViewModel.setCounselingLiveData(counselingLiveData);

        // ส่งข้อมูลไปยัง Activity หลัก
        dataPasser.onCounselingDataPass(currentCounseling);

        try {
            // ใช้เมธอดใหม่ที่ตรวจสอบ visitId และตัดสินใจ insert/update เอง
            Log.d("CounselingSign", "Using saveOrUpdateCounseling method");

            long resultId = counselingDao.saveOrUpdateCounseling(currentCounseling);
            Log.d("CounselingSign", "SaveOrUpdate result ID: " + resultId);

            if (resultId > 0) {
                // บันทึกสำเร็จ
                currentCounseling.setId(resultId); // อัพเดท ID ใน object
                Toast.makeText(getContext(), "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();

                Log.d("CounselingSign", "Save/Update successful with ID: " + resultId);

                // รีโหลดข้อมูลเพื่อยืนยัน
                reloadDataFromDatabase();

            } else {
                // บันทึกไม่สำเร็จ
                Log.e("CounselingSign", "Save/Update failed");
                Toast.makeText(getContext(), "ไม่สามารถบันทึกข้อมูลได้", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Log.e("CounselingSign", "Error saving counseling: " + e.getMessage(), e);
            Toast.makeText(getContext(), "เกิดข้อผิดพลาด: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        Log.d("CounselingSign", "=== End saveFormData ===");
    }
    private void reloadDataFromDatabase() {
        Log.d("CounselingSign", "=== Starting reloadDataFromDatabase ===");

        if (visitNo != null && !visitNo.isEmpty()) {
            List<CounselingInfo> counselingList = counselingDao.getCounselingByVisitId(visitNo);
            Log.d("CounselingSign", "Found " + counselingList.size() + " records for visitNo: " + visitNo);

            if (!counselingList.isEmpty()) {
                currentCounseling = counselingList.get(0);
                Log.d("CounselingSign", "Reloaded counseling ID: " + currentCounseling.getId());
                Log.d("CounselingSign", "Patient signature size: " +
                        (currentCounseling.getPatientSignature() != null ? currentCounseling.getPatientSignature().length : "null"));
                Log.d("CounselingSign", "Provider signature size: " +
                        (currentCounseling.getProviderSignature() != null ? currentCounseling.getProviderSignature().length : "null"));

                // รีเซ็ตฟอร์มและแสดงข้อมูลใหม่
                populateForm(currentCounseling);
            } else {
                Log.w("CounselingSign", "No records found after save/update");
            }
        }

        Log.d("CounselingSign", "=== End reloadDataFromDatabase ===");
    }
    public CounselingInfo getCounselingInfo() {
        return currentCounseling;
    }

    public void setCounselingInfo(CounselingInfo info) {
        this.currentCounseling = info;
        populateForm(info);
    }

    // เพิ่มเมธอดสำหรับแก้ไข RadioButton ที่กดไม่ได้
    public void fixRadioButtonIssue() {
        Log.d("CounselingSign", "=== FIXING RADIO BUTTON ISSUE ===");

        if (radioButtonSendToDoctor != null) {
            radioButtonSendToDoctor.setEnabled(true);
            radioButtonSendToDoctor.setClickable(true);
            radioButtonSendToDoctor.setFocusable(true);
            radioButtonSendToDoctor.requestLayout();
            radioButtonSendToDoctor.invalidate();
            Log.d("CounselingSign", "Fixed RadioButton Send To Doctor");
        }

        if (radioButtonProvideConsult != null) {
            radioButtonProvideConsult.setEnabled(true);
            radioButtonProvideConsult.setClickable(true);
            radioButtonProvideConsult.setFocusable(true);
            radioButtonProvideConsult.requestLayout();
            radioButtonProvideConsult.invalidate();
            Log.d("CounselingSign", "Fixed RadioButton Provide Consult");
        }

        if (radioGroupCounseling != null) {
            radioGroupCounseling.setEnabled(true);
            radioGroupCounseling.requestLayout();
            radioGroupCounseling.invalidate();
            Log.d("CounselingSign", "Fixed RadioGroup");
        }

        Log.d("CounselingSign", "=== END FIXING RADIO BUTTON ISSUE ===");
    }
    public boolean isFormComplete() {
        Log.d("CounselingSign", "=== CHECKING FORM COMPLETENESS ===");

        // ตรวจสอบว่าเลือก radio button หรือไม่
        if (radioGroupCounseling.getCheckedRadioButtonId() == -1) {
            Log.w("CounselingSign", "Form incomplete: No counseling type selected");
            return false;
        }

        // ตรวจสอบ detail ตามประเภทที่เลือก
        if (radioButtonProvideConsult.isChecked()) {
            String consultDetail = editTextConsultDetail.getText().toString().trim();
            if (consultDetail.isEmpty()) {
                Log.w("CounselingSign", "Form incomplete: Consult detail is required but empty");
                return false;
            }
        } else if (radioButtonSendToDoctor.isChecked()) {
            String referralDetail = editTextReferralDetail.getText().toString().trim();
            if (referralDetail.isEmpty()) {
                Log.w("CounselingSign", "Form incomplete: Referral detail is required but empty");
                return false;
            }
        }

        // ตรวจสอบลายเซ็นผู้รับบริการ
        boolean patientSigEmpty = signatureViewPatient.isEmpty();
        byte[] patientSigBytes = signatureViewPatient.getSignatureAsByteArray();
        boolean hasPatientSignature = !patientSigEmpty || (patientSigBytes != null && patientSigBytes.length > 0);

        if (!hasPatientSignature) {
            Log.w("CounselingSign", "Form incomplete: Patient signature is required but empty");
            return false;
        }

        // ตรวจสอบลายเซ็นผู้ให้บริการ
        boolean providerSigEmpty = signatureViewProvider.isEmpty();
        byte[] providerSigBytes = signatureViewProvider.getSignatureAsByteArray();
        boolean hasProviderSignature = !providerSigEmpty || (providerSigBytes != null && providerSigBytes.length > 0);

        if (!hasProviderSignature) {
            Log.w("CounselingSign", "Form incomplete: Provider signature is required but empty");
            return false;
        }

        Log.d("CounselingSign", "Form is complete");
        return true;
    }
    public String getDetailedValidationMessage() {
        Log.d("CounselingSign", "=== GETTING DETAILED VALIDATION MESSAGE ===");

        StringBuilder validationMessage = new StringBuilder();

        // ตรวจสอบการเลือกประเภทการให้บริการ
        if (radioGroupCounseling.getCheckedRadioButtonId() == -1) {
            validationMessage.append("❌ กรุณาเลือกประเภทการให้บริการ:\n");
            validationMessage.append("   • ให้คำแนะนำ\n");
            validationMessage.append("   • ส่งต่อแพทย์/รับบริการตามสิทธิ\n\n");
        } else {
            // ตรวจสอบรายละเอียดตามประเภทที่เลือก
            if (radioButtonProvideConsult.isChecked()) {
                String consultDetail = editTextConsultDetail.getText().toString().trim();
                if (consultDetail.isEmpty()) {
                    validationMessage.append("❌ กรุณากรอกรายละเอียดคำแนะนำ:\n");
                    validationMessage.append("   • ระบุคำแนะนำที่ให้แก่ผู้รับบริการ\n");
                    validationMessage.append("   • คำแนะนำควรชัดเจนและเป็นประโยชน์\n\n");
                } else {
                    validationMessage.append("✅ ประเภทการให้บริการ: ให้คำแนะนำ\n");
                    validationMessage.append("✅ รายละเอียดคำแนะนำ: ครบถ้วน\n\n");
                }
            } else if (radioButtonSendToDoctor.isChecked()) {
                String referralDetail = editTextReferralDetail.getText().toString().trim();
                if (referralDetail.isEmpty()) {
                    validationMessage.append("❌ กรุณากรอกรายละเอียดการส่งต่อ:\n");
                    validationMessage.append("   • ระบุโรงพยาบาล/แผนก/หน่วยบริการที่ส่งต่อ\n");
                    validationMessage.append("   • เหตุผลในการส่งต่อ\n");
                    validationMessage.append("   • ข้อมูลเพิ่มเติมที่จำเป็น\n\n");
                } else {
                    validationMessage.append("✅ ประเภทการให้บริการ: ส่งต่อแพทย์/รับบริการตามสิทธิ\n");
                    validationMessage.append("✅ รายละเอียดการส่งต่อ: ครบถ้วน\n\n");
                }
            }
        }

        // ตรวจสอบลายเซ็นผู้รับบริการ
        boolean patientSigEmpty = signatureViewPatient.isEmpty();
        byte[] patientSigBytes = signatureViewPatient.getSignatureAsByteArray();
        boolean hasPatientSignature = !patientSigEmpty || (patientSigBytes != null && patientSigBytes.length > 0);

        if (!hasPatientSignature) {
            validationMessage.append("❌ กรุณาลงลายมือชื่อผู้รับบริการ:\n");
            validationMessage.append("   • ให้ผู้รับบริการลงลายมือชื่อในช่องที่กำหนด\n");
            validationMessage.append("   • ลายเซ็นเป็นการยืนยันการรับบริการ\n\n");
        } else {
            validationMessage.append("✅ ลายเซ็นผู้รับบริการ: ครบถ้วน\n\n");
        }

        // ตรวจสอบลายเซ็นผู้ให้บริการ
        boolean providerSigEmpty = signatureViewProvider.isEmpty();
        byte[] providerSigBytes = signatureViewProvider.getSignatureAsByteArray();
        boolean hasProviderSignature = !providerSigEmpty || (providerSigBytes != null && providerSigBytes.length > 0);

        if (!hasProviderSignature) {
            validationMessage.append("❌ กรุณาลงลายมือชื่อผู้ให้บริการ:\n");
            validationMessage.append("   • ให้ผู้ให้บริการลงลายมือชื่อในช่องที่กำหนด\n");
            validationMessage.append("   • ลายเซ็นเป็นการยืนยันการให้บริการ\n\n");
        } else {
            validationMessage.append("✅ ลายเซ็นผู้ให้บริการ: ครบถ้วน\n\n");
        }

        // สรุปผลการตรวจสอบ
        if (isFormComplete()) {
            validationMessage.append("🎉 ข้อมูลครบถ้วนแล้ว พร้อมบันทึก");
        } else {
            validationMessage.append("⚠️ กรุณาแก้ไขข้อมูลที่ขาดหายไปก่อนบันทึก");
        }

        String result = validationMessage.toString();
        Log.d("CounselingSign", "Validation message: " + result);

        return result;
    }
    public void showDetailedValidationMessage() {
        String message = getDetailedValidationMessage();

        if (!isFormComplete()) {
            // แสดง AlertDialog สำหรับข้อความแจ้งเตือน
            new android.app.AlertDialog.Builder(getContext())
                    .setTitle("ข้อมูลไม่ครบถ้วน")
                    .setMessage(message)
                    .setPositiveButton("เข้าใจแล้ว", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            Toast.makeText(getContext(), "ข้อมูลครบถ้วนแล้ว", Toast.LENGTH_SHORT).show();
        }
    }
    public void showCompletionStatus() {
        Log.d("CounselingSign", "=== SHOWING COMPLETION STATUS ===");

        boolean isComplete = isFormComplete();
        String statusMessage;

        if (isComplete) {
            statusMessage = "✅ ข้อมูลการให้คำปรึกษาครบถ้วนแล้ว";
            Toast.makeText(getContext(), statusMessage, Toast.LENGTH_SHORT).show();
        } else {
            statusMessage = "⚠️ ข้อมูลการให้คำปรึกษายังไม่ครบถ้วน";
            // แสดง detailed message
            showDetailedValidationMessage();
        }

        Log.d("CounselingSign", "Completion status: " + statusMessage);
    }
    public boolean isConsultDetailRequired() {
        return radioButtonProvideConsult.isChecked();
    }

    public boolean isReferralDetailRequired() {
        return radioButtonSendToDoctor.isChecked();
    }

    public boolean hasValidConsultDetail() {
        if (!isConsultDetailRequired()) return true;
        return !editTextConsultDetail.getText().toString().trim().isEmpty();
    }

    public boolean hasValidReferralDetail() {
        if (!isReferralDetailRequired()) return true;
        return !editTextReferralDetail.getText().toString().trim().isEmpty();
    }

    public boolean hasValidPatientSignature() {
        boolean patientSigEmpty = signatureViewPatient.isEmpty();
        byte[] patientSigBytes = signatureViewPatient.getSignatureAsByteArray();
        return !patientSigEmpty || (patientSigBytes != null && patientSigBytes.length > 0);
    }

    public boolean hasValidProviderSignature() {
        boolean providerSigEmpty = signatureViewProvider.isEmpty();
        byte[] providerSigBytes = signatureViewProvider.getSignatureAsByteArray();
        return !providerSigEmpty || (providerSigBytes != null && providerSigBytes.length > 0);
    }
    private boolean validateFormEnhanced() {
        Log.d("CounselingSign", "=== ENHANCED VALIDATE FORM ===");

        // ใช้ isFormComplete() แทน
        if (!isFormComplete()) {
            // แสดงข้อความแจ้งเตือนแบบละเอียด
            showDetailedValidationMessage();
            return false;
        }

        Log.d("CounselingSign", "Enhanced validation passed");
        return true;
    }
}