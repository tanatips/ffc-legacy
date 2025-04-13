package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.adapter.SubstanceSevenAdapter;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.person.PersonScreeningForm15Activity;

public class QuestionEightFragment extends Fragment {

    private QuestionsStateViewModel viewModel;
    private RadioGroup radioGroupInjection;
    private int selectedOption = 0; // 0 = ไม่เคย, 1 = ภายใน 3 เดือน, 2 = ก่อน 3 เดือน
    private final String INJECTION_KEY = "injection";

    // เพิ่มตัวแปรที่จำเป็น
    private OnDataPass dataPasser;
    private Map<String, DrugsInfo> drugsInfoMap = new HashMap<>();
    private List<DrugsInfo> drugsInfos = new ArrayList<>();
    private boolean isDataLoaded = false;
    private boolean isFirstLoad = true;
    private final boolean[] isUpdating = {false};

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_eight, container, false);
        radioGroupInjection = view.findViewById(R.id.radioGroupInjection);
        LinearLayout headerLayout = view.findViewById(R.id.headerLayoutEight);
        final LinearLayout contentLayout = view.findViewById(R.id.contentLayoutEight);
        final ImageView expandIcon = view.findViewById(R.id.expandIconEight);

        // ตั้งค่าเริ่มต้น - แสดงเนื้อหา
        contentLayout.setVisibility(View.VISIBLE);
        expandIcon.setImageResource(R.drawable.ic_expand_less);

        // ตั้งค่า Click Listener สำหรับ Header เพื่อ Toggle การแสดงเนื้อหา
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility
                if (contentLayout.getVisibility() == View.VISIBLE) {
                    contentLayout.setVisibility(View.GONE);
                    expandIcon.setImageResource(R.drawable.ic_expand_more);
                    notifyParentOfChange();
                } else {
                    contentLayout.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.ic_expand_less);
                    notifyParentOfChange();
                }
            }
        });
        return view;
    }
    private void notifyParentOfChange() {
        // วิธีที่ 1: แจ้ง parent fragment (MainQuestionsFragment) โดยตรง
        Fragment parentFragment = getParentFragment();
        if (parentFragment instanceof MainQuestionsFragment) {
            ((MainQuestionsFragment) parentFragment).notifyChildFragmentStateChanged();
        }

        // วิธีที่ 2: ถ้าไม่มี notifyChildFragmentStateChanged() ใน MainQuestionsFragment
        // หรือไม่สามารถเข้าถึง MainQuestionsFragment ได้ ให้แจ้ง activity โดยตรง
//        if (getActivity() instanceof PersonScreeningForm15Activity) {
//            // หน่วงเวลาเล็กน้อยเพื่อให้ layout ได้อัปเดตก่อน
//            new Handler().postDelayed(() -> {
//                ((PersonScreeningForm15Activity) getActivity()).refreshViewPager();
//            }, 200);
//        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore saved state if exists
        if (savedInstanceState != null) {
            selectedOption = savedInstanceState.getInt("selectedOption", 0);
            updateRadioSelection();
        }

        // Observe ViewModel data
        viewModel.getQuestionEightAnswers().observe(getViewLifecycleOwner(), new Observer<Map<String, AnswerFrequencyData>>() {
            @Override
            public void onChanged(Map<String, AnswerFrequencyData> answer) {
                if (!isUpdating[0] && answer != null && answer.containsKey(INJECTION_KEY)) {
                    isUpdating[0] = true;
                    try {
                        selectedOption = answer.get(INJECTION_KEY).getFrequency();
                        updateRadioSelection();
                    } finally {
                        isUpdating[0] = false;
                    }
                }
            }
        });


        // Set up radio group listener
        radioGroupInjection.setOnCheckedChangeListener((group, checkedId) -> {
            if (isUpdating[0]) return;

            isUpdating[0] = true;
            try {
                if (checkedId == R.id.radioNever) {
                    selectedOption = 0;
                } else if (checkedId == R.id.radioWithinThreeMonths) {
                    selectedOption = 1;
                } else if (checkedId == R.id.radioBeforeThreeMonths) {
                    selectedOption = 2;
                }

                // Update ViewModel
                viewModel.updateQuestionEightAnswer(INJECTION_KEY, selectedOption, "");

                // Prepare and save to database
                prepareDrugsInfoForUpdate(selectedOption);
            } finally {
                isUpdating[0] = false;
            }
        });
        // โหลดข้อมูลจาก DB เฉพาะครั้งแรกเท่านั้น
        if (isFirstLoad && !isDataLoaded) {
            loadData();
            isFirstLoad = false;
        } else {
            // ถ้ามีข้อมูลใน ViewModel ให้ใช้ข้อมูลนั้น
            Map<String, AnswerFrequencyData> viewModelAnswers = viewModel.getQuestionEightAnswers().getValue();
            if (viewModelAnswers != null && viewModelAnswers.containsKey(INJECTION_KEY)) {
                selectedOption = viewModelAnswers.get(INJECTION_KEY).getFrequency();
                Log.d("QuestionEightFragment", "Using ViewModel data: " + selectedOption);
                updateRadioSelection();
            }
        }
    }

    private void loadData() {
        Log.d("QuestionEightFragment", "Starting to load data...");
        SfDrugsDao sfDrugsDao = new SfDrugsDao(getContext());
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getDrugsLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                Log.d("QuestionEightFragment", "PersonId: " + data.getPersonId());

                List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.valueOf(data.getPersonId()));
                this.drugsInfos = drugsInfos;
                drugsInfoMap.clear();

                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q8")) {
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                        Log.d("QuestionEightFragment", "Found Q8 data: " + drug.getSubquestion() + " = " + drug.getAnswer());
                    }
                }

                // ค้นหา DrugsInfo ที่ตรงกับ injection
                DrugsInfo matchingDrug = drugsInfoMap.get(INJECTION_KEY);

                if (matchingDrug != null) {
                    // ถ้าพบข้อมูล ใช้ค่าจากฐานข้อมูล
                    try {
                        selectedOption = Integer.parseInt(matchingDrug.getAnswer());
                        Log.d("QuestionEightFragment", "Loaded injection data: " + selectedOption);

                        // อัปเดต UI ในเธรดหลัก
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                if (isAdded()) {
                                    // อัปเดต ViewModel
                                    Map<String, AnswerFrequencyData> answerMap = new HashMap<>();
                                    answerMap.put(INJECTION_KEY, new AnswerFrequencyData(selectedOption, ""));
                                    viewModel.setQuestionEightAnswers(answerMap);

                                    // อัปเดต UI
                                    updateRadioSelection();
                                    isDataLoaded = true;
                                }
                            });
                        }
                    } catch (NumberFormatException e) {
                        Log.e("QuestionEightFragment", "Error parsing answer: " + matchingDrug.getAnswer(), e);
                    }
                } else {
                    Log.d("QuestionEightFragment", "No matching drug info found for key: " + INJECTION_KEY);
                }

                Log.d("QuestionEightFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            } else {
                Log.d("QuestionEightFragment", "No person ID available");
            }
        });
    }

    private void prepareDrugsInfoForUpdate(int selectedValue) {
        List<DrugsInfo> drugsInfosToUpdate = new ArrayList<>();

        // ดึง PersonId จาก PersonInfoLiveData
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        PersonInfoLiveData personInfoData = sharedViewModel.getPersonInfoLiveDataMutableLiveData().getValue();
        String personInfoId = personInfoData != null ? personInfoData.getId() : null;

        if (personInfoId == null) {
            Log.e("QuestionEightFragment", "Cannot update - person info ID is null");
            return;
        }

        DrugsInfo drugsInfo = new DrugsInfo();

        // ตรวจสอบข้อมูลเดิมจาก drugsInfoMap
        boolean found = false;
        if (drugsInfoMap != null) {
            DrugsInfo existingInfo = drugsInfoMap.get(INJECTION_KEY);
            if (existingInfo != null) {
                drugsInfo.setId(existingInfo.getId());
                drugsInfo.setCreatedDate(existingInfo.getCreatedDate());
                drugsInfo.setCreatedBy(existingInfo.getCreatedBy());
                drugsInfo.setPersonInfoId(existingInfo.getPersonInfoId());
                drugsInfo.setIdcard(existingInfo.getIdcard());
                drugsInfo.setQuestion(existingInfo.getQuestion());
                drugsInfo.setSubquestion(existingInfo.getSubquestion());
                drugsInfo.setAnswer(existingInfo.getAnswer());
                drugsInfo.setOtherDrugs(existingInfo.getOtherDrugs());
                drugsInfo.setUpdatedBy(existingInfo.getUpdatedBy());
                drugsInfo.setUpdatedDate(existingInfo.getUpdatedDate());
                found = true;
            }
        }

        // กำหนดค่าใหม่
        if (!found) {
            drugsInfo.setCreatedBy("SYSTEM");
            drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }

        drugsInfo.setPersonInfoId(personInfoId);
        drugsInfo.setIdcard(personInfoData != null ? personInfoData.getIdcard() : "");
        drugsInfo.setQuestion("Q8");  // ใช้ Q8 สำหรับ QuestionEightFragment
        drugsInfo.setSubquestion(INJECTION_KEY);
        drugsInfo.setAnswer(String.valueOf(selectedValue));
        drugsInfo.setOtherDrugs("");  // ไม่มี otherDrugs สำหรับคำถามนี้
        drugsInfo.setUpdatedBy("SYSTEM");
        drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        drugsInfosToUpdate.add(drugsInfo);

        // ส่งข้อมูลไปยัง dataPasser
        if (dataPasser != null) {
            dataPasser.onDrugsEightInfo(drugsInfosToUpdate);
            Log.d("QuestionEightFragment", "Saving injection data: " + selectedValue + " to person ID: " + personInfoId);
        } else {
            Log.e("QuestionEightFragment", "dataPasser is null - cannot save data");
        }
    }
    private void updateRadioSelection() {
        if (radioGroupInjection == null) {
            Log.e("QuestionEightFragment", "radioGroupInjection is null");
            return;
        }

        Log.d("QuestionEightFragment", "Updating radio selection to: " + selectedOption);

        switch (selectedOption) {
            case 0:
                radioGroupInjection.check(R.id.radioNever);
                break;
            case 1:
                radioGroupInjection.check(R.id.radioWithinThreeMonths);
                break;
            case 2:
                radioGroupInjection.check(R.id.radioBeforeThreeMonths);
                break;
            default:
                radioGroupInjection.clearCheck();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedOption", selectedOption);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                Map<String, AnswerFrequencyData> currentAnswers = viewModel.getQuestionEightAnswers().getValue();
                if (currentAnswers != null && currentAnswers.containsKey(INJECTION_KEY)) {
                    selectedOption = currentAnswers.get(INJECTION_KEY).getFrequency();
                    Log.d("QuestionEightFragment", "onResume updating to: " + selectedOption);
                    updateRadioSelection();
                }
            } finally {
                isUpdating[0] = false;
            }
        }
    }
}