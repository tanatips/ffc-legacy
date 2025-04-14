package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import th.in.ffc.R;

import th.in.ffc.app.form.screening.adapter.SubstanceOneAdapter;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.listener.OnSubstanceSelectionListener;
import th.in.ffc.app.form.screening.model.AnswerData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.person.PersonScreeningForm15Activity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionOneFragment extends Fragment implements OnSubstanceSelectionListener {

    private Map<String, DrugsInfo> drugsInfoMap = new HashMap<>();
    private OnDataPass dataPasser;
//    private SharedViewModel viewModel;
    private Map<String, AnswerData> selectedAnswers = new HashMap<>();
    private RecyclerView recyclerView;
    private SubstanceOneAdapter adapter;
    private List<SubstanceItem> substanceList;
//    private QuestionsStateViewModel viewModel;
    private Observer<Map<String, AnswerData>> answersObserver;
    private QuestionsStateViewModel questionsViewModel;
    final boolean[] isUpdating = {false};
    private boolean isDataLoaded = false;
    private boolean isExpanded = false; // เก็บสถานะปัจจุบัน

    List<DrugsInfo> drugsInfos = new ArrayList<>();


    public QuestionOneFragment() {
        // Required empty public constructor
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            substanceList = getArguments().getParcelableArrayList("substanceList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_one, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOne);
        LinearLayout headerLayout = view.findViewById(R.id.headerLayoutOne);
        final LinearLayout contentLayout = view.findViewById(R.id.contentLayoutOne);
        final ImageView expandIcon = view.findViewById(R.id.expandIconOne);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceOneAdapter(substanceList, this);
        recyclerView.setAdapter(adapter);

        // ตั้งค่าเริ่มต้น - แสดงเนื้อหา
        contentLayout.setVisibility(View.GONE);
        expandIcon.setImageResource(R.drawable.ic_expand_more);

        // ตั้งค่า Click Listener สำหรับ Header เพื่อ Toggle การแสดงเนื้อหา
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility
                if (contentLayout.getVisibility() == View.VISIBLE) {
                    contentLayout.setVisibility(View.GONE);
                    expandIcon.setImageResource(R.drawable.ic_expand_more);
                    contentLayout.requestLayout();
                    recyclerView.requestLayout();
                    isExpanded = true;
                    notifyParentOfChange();

                } else {
                    contentLayout.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.ic_expand_less);
                    contentLayout.requestLayout();
                    recyclerView.requestLayout();
                    isExpanded = false;
                    notifyParentOfChange();
                }
            }


        });
        // ตั้งค่าเริ่มต้นสำหรับทุก item
//        for (SubstanceItem item : substanceList) {
//            selectedAnswers.put(item.getId(), new AnswerData(false, ""));
//        }

//        initializeData();
//        adapter = new SubstanceOneAdapter(substanceList,this);
//        for (SubstanceItem item : substanceList) {
//            selectedAnswers.put(item.getId(), new AnswerData(false, ""));
//        }
//        recyclerView.setAdapter(adapter);

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

        questionsViewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
//        viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // สร้าง map สำหรับเก็บค่าเริ่มต้น
        for (SubstanceItem item : substanceList) {
            selectedAnswers.put(item.getId(), new AnswerData(item.isHasUsed(), item.getOtherDrugs()));
        }

        // ตรวจสอบว่ามีข้อมูลใน ViewModel หรือไม่
        Map<String, AnswerData> viewModelAnswers = questionsViewModel.getQuestionOneAnswers().getValue();
        if (viewModelAnswers != null && !viewModelAnswers.isEmpty()) {
            // ถ้ามีข้อมูลใน ViewModel ให้ใช้ข้อมูลนั้น
            selectedAnswers = new HashMap<>(viewModelAnswers);
            updateUI(selectedAnswers);
            isDataLoaded = true;
        }

        answersObserver = answers -> {
            if (answers != null && isAdded() && !isUpdating[0]) {
                isUpdating[0] = true;
                try {
//                  // ไม่อัพเดต selectedAnswers จาก ViewModel แล้ว
//                  // เพราะเราต้องการเก็บค่าที่ผู้ใช้เลือกไว้ระหว่างสลับแท็บ
                    updateUI(selectedAnswers);
                } finally {
                    isUpdating[0] = false;
                }
            }
        };
        questionsViewModel.getQuestionOneAnswers().observe(getViewLifecycleOwner(), answersObserver);

        if (!isDataLoaded) {
            loadData();
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ยกเลิก observer เมื่อ view ถูกทำลาย
        if (questionsViewModel != null && answersObserver != null) {
            questionsViewModel.getQuestionOneAnswers().removeObserver(answersObserver);
        }
        recyclerView = null;
        adapter = null;
    }
    private void updateUI(Map<String, AnswerData> answers) {
        if (adapter != null) {
            recyclerView.post(() -> adapter.updateAnswers(answers));
        }

    }

    public static QuestionOneFragment newInstance(String param1, String param2) {
        QuestionOneFragment fragment = new QuestionOneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private void printCurrentSelections() {
    StringBuilder result = new StringBuilder("Current selections:\n");
    for (Map.Entry<String, AnswerData> entry : selectedAnswers.entrySet()) {
        result.append(entry.getKey())
                .append(": hasUsed=")
                .append(entry.getValue().isHasUsed());
        if (entry.getKey().equals("j")) {
            result.append(", otherSubstance=")
                    .append(entry.getValue().getOtherDrugs());
        }
        result.append("\n");
    }
    Log.d("QuestionOneFragment", result.toString());
}
    public Map<String, AnswerData> getSelectedAnswers() {
        return new HashMap<>(selectedAnswers);
    }

    // เมธอดสำหรับตรวจสอบว่าตอบครบทุกข้อหรือยัง
    public boolean isAllQuestionsAnswered() {
        return selectedAnswers.size() == substanceList.size();
    }

    @Override
    public void onAnswerChanged(String id, boolean hasUsed, String otherSubstance) {
        if (!isAdded() || isUpdating[0]) return;

        isUpdating[0] = true;
        try {
            AnswerData newAnswer = new AnswerData(hasUsed, otherSubstance);
            AnswerData currentAnswer = selectedAnswers.get(id);

            if (currentAnswer == null ||
                    currentAnswer.isHasUsed() != hasUsed ||
                    !Objects.equals(currentAnswer.getOtherDrugs(), otherSubstance)) {

                selectedAnswers.put(id, newAnswer);
                questionsViewModel.updateQuestionOneAnswer(id, hasUsed, otherSubstance);
                // อัพเดต SubstanceItem เพื่อเก็บสถานะ
                for (SubstanceItem item : substanceList) {
                    if (item.getId().equals(id)) {
                        item.setHasUsed(hasUsed);
                        if (id.equals("j")) {
                            item.setOtherDrugs(otherSubstance);
                        }
                        break;
                    }
                }
                List<DrugsInfo> drugsInfos = new ArrayList<>();

                // ดึง PersonId จาก PersonInfoLiveData แทน
                SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
                PersonInfoLiveData personInfoData = sharedViewModel.getPersonInfoLiveDataMutableLiveData().getValue();
                String personInfoId = personInfoData != null ? personInfoData.getId() : null;

                for (Map.Entry<String, AnswerData> entry : selectedAnswers.entrySet()) {
                    DrugsInfo drugsInfo = new DrugsInfo();

                    for(DrugsInfo drugsInfo1 : this.drugsInfos){
                        if(drugsInfo1.getSubquestion().equals(entry.getKey())){
                            drugsInfo.setId(drugsInfo1.getId());
                            drugsInfo.setCreatedDate(drugsInfo1.getCreatedDate());
                            drugsInfo.setCreatedBy(drugsInfo1.getCreatedBy());
                            drugsInfo.setPersonInfoId(drugsInfo1.getPersonInfoId());
                            drugsInfo.setIdcard(drugsInfo1.getIdcard());
                            drugsInfo.setQuestion(drugsInfo1.getQuestion());
                            drugsInfo.setSubquestion(drugsInfo1.getSubquestion());
                            drugsInfo.setAnswer(drugsInfo1.getAnswer());
                            drugsInfo.setOtherDrugs(drugsInfo1.getOtherDrugs());
                            drugsInfo.setUpdatedBy(drugsInfo1.getUpdatedBy());
                            drugsInfo.setUpdatedDate(drugsInfo1.getUpdatedDate());
//                            break;
                        }
                    }
                    // ค้นหาข้อมูลเดิมจาก drugsInfoMap
                    DrugsInfo existingInfo = drugsInfoMap.get(entry.getKey());
                    if (existingInfo != null) {
                        drugsInfo.setId(existingInfo.getId());
                        drugsInfo.setCreatedDate(existingInfo.getCreatedDate());
                        drugsInfo.setCreatedBy(existingInfo.getCreatedBy());
                    } else {
                        drugsInfo.setCreatedBy("SYSTEM");
                        drugsInfo.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    }

                    // กำหนดค่าใหม่
                    if (drugsInfo.getPersonInfoId() == null) {
                        drugsInfo.setPersonInfoId(personInfoId);
                        drugsInfo.setIdcard(personInfoData != null ? personInfoData.getIdcard() : "");
                        drugsInfo.setQuestion("Q1");
                        drugsInfo.setSubquestion(entry.getKey());
                    }
                    drugsInfo.setAnswer(entry.getValue().isHasUsed() ? "1" : "0");
                    drugsInfo.setOtherDrugs(entry.getValue().getOtherDrugs());
                    drugsInfo.setUpdatedBy("SYSTEM");
                    drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                    drugsInfos.add(drugsInfo);
                }

                dataPasser.onDrugsOneInfo(drugsInfos);
            }
        } finally {
            isUpdating[0] = false;
        }
        printCurrentSelections();
    }
     private void loadData() {
        SfDrugsDao sfDrugsDao = new SfDrugsDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel.getDrugsLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.valueOf(data.getPersonId()));
                this.drugsInfos = drugsInfos;
                Map<String, AnswerData> answers = new HashMap<>();
                drugsInfoMap.clear();
                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q1")) {
                        // ใช้ subquestion เป็น key เพื่อจับคู่กับ substance
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                    }
                }
                for (SubstanceItem item : substanceList) {
                    // ค้นหา DrugsInfo ที่ตรงกับ substance id นี้
                    DrugsInfo matchingDrug = null;
                    for (DrugsInfo drug : drugsInfos) {
                        if (drug.getQuestion().equals("Q1") &&
                                drug.getSubquestion().equals(item.getId())) {
                            matchingDrug = drug;
                            break;
                        }
                    }

                    if (matchingDrug != null) {
                        // ถ้าพบข้อมูล ใช้ค่าจากฐานข้อมูล
                        boolean hasUsed = "1".equals(matchingDrug.getAnswer());
                        String otherDrugs = matchingDrug.getOtherDrugs();
                        answers.put(item.getId(), new AnswerData(hasUsed, otherDrugs));

                        // อัพเดต state ของ SubstanceItem ด้วย
                        item.setHasUsed(hasUsed);
                        if (item.getId().equals("j")) {
                            item.setOtherDrugs(otherDrugs);
                        }
                    } else {
                        // ถ้าไม่พบข้อมูล ใช้ค่าเริ่มต้น
                        answers.put(item.getId(), new AnswerData(false, ""));
                    }
                }

                if (!answers.isEmpty()) {
                    selectedAnswers = answers;
                    questionsViewModel.setQuestionOneAnswers(answers);
                    adapter.updateAnswers(answers);
                    dataPasser.onDrugsOneInfo(drugsInfos);
                    isDataLoaded = true;
                }

                Log.d("QuestionOneFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (questionsViewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                // ตรวจสอบสถานะใน ViewModel
                Map<String, AnswerData> viewModelAnswers = questionsViewModel.getQuestionOneAnswers().getValue();
                if (viewModelAnswers != null && !viewModelAnswers.isEmpty()) {
                    // เลือกใช้ข้อมูลจาก ViewModel ถ้าไม่ได้โหลดข้อมูลจาก DB มาแล้ว
                    if (!isDataLoaded) {
                        selectedAnswers = new HashMap<>(viewModelAnswers);
                    }
                }
                updateUI(selectedAnswers);
            } finally {
                isUpdating[0] = false;
            }
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable("selectedFrequencies", new HashMap<>(selectedFrequencies));
        outState.putBoolean("isExpanded", isExpanded);
    }

}