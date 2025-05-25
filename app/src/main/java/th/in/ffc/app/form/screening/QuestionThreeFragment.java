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

import th.in.ffc.R;
import th.in.ffc.app.form.screening.adapter.SubstanceThreeAdapter;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.person.PersonScreeningForm15Activity;


public class QuestionThreeFragment extends Fragment implements OnFrequencySelectedListener {

    private RecyclerView recyclerView;
    private SubstanceThreeAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Map<String, AnswerFrequencyData> selectedFrequencies = new HashMap<>();
    private Observer<Map<String, AnswerFrequencyData>> answersObserver;
    final boolean[] isUpdating = {false}; // เพิ่มตัวแปรเพื่อป้องกันการอัปเดตซ้ำซ้อน

    // เพิ่มตัวแปรต่อไปนี้ใน QuestionThreeFragment
    private Map<String, DrugsInfo> drugsInfoMap = new HashMap<>();
    private List<DrugsInfo> drugsInfos = new ArrayList<>();
    private OnDataPass dataPasser;
    private boolean isDataLoaded = false;
    private boolean isFirstLoad = true;
    LinearLayout headerLayout ;
    LinearLayout contentLayout ;
    ImageView expandIcon  ;

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
        if (getArguments() != null) {
            substanceList = getArguments().getParcelableArrayList("substanceList");
        }
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_three, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewThree);
        headerLayout = view.findViewById(R.id.headerLayoutThree);
        contentLayout = view.findViewById(R.id.contentLayoutThree);
        expandIcon = view.findViewById(R.id.expandIconThree);

        // ตั้งค่า RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceThreeAdapter(substanceList, this);
        recyclerView.setAdapter(adapter);

        // ตั้งค่าเริ่มต้น - แสดงเนื้อหา
        contentLayout.setVisibility(View.GONE);
        expandIcon.setImageResource(R.drawable.ic_expand_more);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle visibility
                if (contentLayout.getVisibility() == View.VISIBLE) {
                    contentLayout.setVisibility(View.GONE);
                    expandIcon.setImageResource(R.drawable.ic_expand_more);
                    notifyParentOfChange();
                    calculateAndSetContentHeight();
                } else {
                    contentLayout.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.ic_expand_less);
                    notifyParentOfChange();
                    calculateAndSetContentHeight();
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
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // เริ่มต้นค่าเริ่มต้นสำหรับทุก item
        for (SubstanceItem item : substanceList) {
            selectedFrequencies.put(item.getId(), new AnswerFrequencyData(0, "")); // กำหนดค่าเริ่มต้นเป็น 0 (ไม่เคย)
        }

        // ตรวจสอบว่ามีข้อมูลใน ViewModel หรือไม่
        Map<String, AnswerFrequencyData> viewModelAnswers = viewModel.getQuestionThreeAnswers().getValue();
        if (viewModelAnswers != null && !viewModelAnswers.isEmpty()) {
            // ถ้ามีข้อมูลใน ViewModel ให้ใช้ข้อมูลนั้น
            selectedFrequencies = new HashMap<>(viewModelAnswers);
            // อัพเดตค่าใน SubstanceItems เพื่อเก็บค่าไว้
            updateSubstanceItems(selectedFrequencies);
            updateUI(selectedFrequencies);
            isDataLoaded = true;
        }

        if (savedInstanceState != null) {
            Map<String, AnswerFrequencyData> savedFrequencies = (Map<String, AnswerFrequencyData>) savedInstanceState.getSerializable("selectedFrequencies");
            if (savedFrequencies != null && !savedFrequencies.isEmpty()) {
                selectedFrequencies = new HashMap<>(savedFrequencies);
                updateSubstanceItems(selectedFrequencies);
                updateUI(selectedFrequencies);
                isDataLoaded = true;
            }
        }

        answersObserver = answers -> {
            if (answers != null && isAdded() && !isUpdating[0]) {
                isUpdating[0] = true;
                try {
                    // อัพเดต UI ด้วยค่าปัจจุบัน
                    updateUI(selectedFrequencies);
                } finally {
                    isUpdating[0] = false;
                }
            }
        };
        viewModel.getQuestionThreeAnswers().observe(getViewLifecycleOwner(), answersObserver);

        // โหลดข้อมูลจาก DB เฉพาะครั้งแรกเท่านั้น
//        if (isFirstLoad && !isDataLoaded) {
            loadData();
//            isFirstLoad = false;
//        }
    }

    private void updateSubstanceItems(Map<String, AnswerFrequencyData> frequencies) {
        for (SubstanceItem item : substanceList) {
            AnswerFrequencyData data = frequencies.get(item.getId());
            if (data != null) {
                item.setFrequency(data.getFrequency());
                item.setOtherDrugs(data.getOtherDrugs());
            }
        }
    }
    // เพิ่มเมธอด loadData
    private void loadData() {
        SfDrugsDao sfDrugsDao = new SfDrugsDao(getContext());
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getDrugsLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.valueOf(data.getPersonId()));
                this.drugsInfos = drugsInfos;
                Map<String, AnswerFrequencyData> frequencies = new HashMap<>();
                drugsInfoMap.clear();

                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q3")) {  // เปลี่ยนจาก Q2 เป็น Q3
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                    }
                }

                for (SubstanceItem item : substanceList) {
                    // ค้นหา DrugsInfo ที่ตรงกับ substance id นี้
                    DrugsInfo matchingDrug = null;
                    for (DrugsInfo drug : drugsInfos) {
                        if (drug.getQuestion().equals("Q3") &&  // เปลี่ยนจาก Q2 เป็น Q3
                                drug.getSubquestion().equals(item.getId())) {
                            matchingDrug = drug;
                            break;
                        }
                    }

                    if (matchingDrug != null) {
                        // ถ้าพบข้อมูล ใช้ค่าจากฐานข้อมูล
                        int frequency = Integer.parseInt(matchingDrug.getAnswer());
                        String otherDrugs = matchingDrug.getOtherDrugs() != null ? matchingDrug.getOtherDrugs() : "";

                        // สำคัญ: อัปเดตทั้ง frequencies และ SubstanceItem
                        frequencies.put(item.getId(), new AnswerFrequencyData(frequency, otherDrugs));

                        // อัปเดต state ของ SubstanceItem ให้ครบทั้ง frequency และ otherDrugs
                        item.setFrequency(frequency);
                        item.setOtherDrugs(otherDrugs);

                        Log.d("QuestionThreeFragment", "Loaded item: " + item.getId() +
                                " frequency: " + frequency +
                                " otherDrugs: " + otherDrugs);
                    } else {
                        // ถ้าไม่พบข้อมูล ใช้ค่าเริ่มต้น
                        frequencies.put(item.getId(), new AnswerFrequencyData(-1, ""));

                        // รีเซ็ต state ของ SubstanceItem ด้วย
                        item.setFrequency(0);
                        item.setOtherDrugs("");
                    }
                }

                if (!frequencies.isEmpty()) {
                    selectedFrequencies = frequencies;
                    viewModel.setQuestionThreeAnswers(frequencies);

                    // เพิ่ม Log เพื่อตรวจสอบว่า item "j" มีค่า otherDrugs หรือไม่
                    for (Map.Entry<String, AnswerFrequencyData> entry : frequencies.entrySet()) {
                        if (entry.getKey().equals("j")) {
                            Log.d("QuestionThreeFragment", "Item j otherDrugs: " + entry.getValue().getOtherDrugs());
                        }
                    }

                    updateUI(selectedFrequencies);
                    isDataLoaded = true;
                }

                Log.d("QuestionThreeFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            }
        });
    }

    // เพิ่มเมธอด updateUI ที่รับพารามิเตอร์ excludeId
    private void updateUI(Map<String, AnswerFrequencyData> answers, String excludeId) {
        if (adapter != null && recyclerView != null) {
            // เพิ่ม Log เพื่อตรวจสอบค่า otherDrugs ของ item "j"
            AnswerFrequencyData itemJ = answers.get("j");
            if (itemJ != null) {
                Log.d("QuestionThreeFragment", "updateUI - Item j otherDrugs: " + itemJ.getOtherDrugs());
            }

            recyclerView.post(() -> {
                if (isAdded()) {
                    adapter.updateAnswers(answers, excludeId);
                }
            });
        }
    }
    private void updateUI(Map<String, AnswerFrequencyData> answers) {
        updateUI(answers, null);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ยกเลิก observer เมื่อ view ถูกทำลาย
        if (viewModel != null && answersObserver != null) {
            viewModel.getQuestionThreeAnswers().removeObserver(answersObserver);
        }
        recyclerView = null;
        adapter = null;
    }

    @Override
    public void onFrequencySelected(String id, int frequency, String otherDrugs) {
        if (!isAdded() || isUpdating[0]) return;

        isUpdating[0] = true;
        try {
            // เช็คว่าค่าเปลี่ยนแปลงจริงๆ
            AnswerFrequencyData currentData = selectedFrequencies.get(id);
            String currentOtherDrugs = currentData != null ? currentData.getOtherDrugs() : "";

            if (currentData == null || currentData.getFrequency() != frequency || !currentOtherDrugs.equals(otherDrugs)) {
                // อัพเดตค่าใน selectedFrequencies
                selectedFrequencies.put(id, new AnswerFrequencyData(frequency, otherDrugs));

                // อัพเดตค่าใน ViewModel
                viewModel.updateQuestionThreeAnswer(id, frequency, otherDrugs);

                // อัพเดตค่าใน SubstanceItem เพื่อเก็บไว้ใช้ต่อ
                for (SubstanceItem item : substanceList) {
                    if (item.getId().equals(id)) {
                        item.setFrequency(frequency);
                        item.setOtherDrugs(otherDrugs);
                        break;
                    }
                }

                // สร้าง DrugsInfo สำหรับส่งไปยัง database
                prepareDrugsInfoForUpdate(id, frequency, otherDrugs);

                // สำหรับการพิมพ์ที่ช่อง "j" (ระบุสารเสพติดอื่นๆ) ไม่จำเป็นต้องอัปเดต UI ทั้งหมด
                if (id.equals("j")) {
                    updateUI(selectedFrequencies, id);
                } else {
                    updateUI(selectedFrequencies);
                }
            }

            Log.d("QuestionThree", "Item " + id + " frequency: " + frequency);
        } finally {
            isUpdating[0] = false;
        }
    }
    private void prepareDrugsInfoForUpdate(String id, int frequency, String otherDrugs) {
        List<DrugsInfo> drugsInfosToUpdate = new ArrayList<>();

        // ดึง PersonId จาก PersonInfoLiveData
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        PersonInfoLiveData personInfoData = sharedViewModel.getPersonInfoLiveDataMutableLiveData().getValue();
        String personInfoId = personInfoData != null ? personInfoData.getId() : null;

        // ตรวจสอบว่า id เป็น "j" หรือไม่ และ log ค่า otherDrugs
        if (id.equals("j")) {
            Log.d("QuestionThreeFragment", "Preparing item j with otherDrugs: " + otherDrugs);
        }

        for (Map.Entry<String, AnswerFrequencyData> entry : selectedFrequencies.entrySet()) {
            DrugsInfo drugsInfo = new DrugsInfo();
            String currentId = entry.getKey();
            AnswerFrequencyData data = entry.getValue();

            // ใช้ค่าที่ได้รับมาเฉพาะสำหรับ item ที่มีการเปลี่ยนแปลง
            if (currentId.equals(id)) {
                data = new AnswerFrequencyData(frequency, otherDrugs);
            }

            // ตรวจสอบข้อมูลเดิมจาก drugsInfos
            boolean found = false;
            if (drugsInfoMap != null) {
                DrugsInfo existingInfo = drugsInfoMap.get(currentId);
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

            if (drugsInfo.getPersonInfoId() == null) {
                drugsInfo.setPersonInfoId(personInfoId);
                drugsInfo.setIdcard(personInfoData != null ? personInfoData.getIdcard() : "");
                drugsInfo.setQuestion("Q3");  // เปลี่ยนจาก Q2 เป็น Q3 สำหรับ QuestionThreeFragment
                drugsInfo.setSubquestion(currentId);
            }

            drugsInfo.setAnswer(String.valueOf(data.getFrequency()));

            // สำคัญ: ตั้งค่า otherDrugs อย่างถูกต้อง
            drugsInfo.setOtherDrugs(data.getOtherDrugs());

            drugsInfo.setUpdatedBy("SYSTEM");
            drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // Log เพื่อตรวจสอบค่า otherDrugs ที่จะบันทึก
            if (currentId.equals("j")) {
                Log.d("QuestionThreeFragment", "Saving item j with otherDrugs: " + drugsInfo.getOtherDrugs());
            }

            drugsInfosToUpdate.add(drugsInfo);
        }

        // ส่งข้อมูลไปยัง dataPasser
        if (dataPasser != null) {
            dataPasser.onDrugsThreeInfo(drugsInfosToUpdate);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                Map<String, AnswerFrequencyData> currentAnswers = viewModel.getQuestionThreeAnswers().getValue();
                if (currentAnswers != null && !currentAnswers.equals(selectedFrequencies)) {
                    selectedFrequencies = new HashMap<>(currentAnswers);
                    updateUI(selectedFrequencies);
                }
            } finally {
                isUpdating[0] = false;
            }
        }
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedFrequencies", new HashMap<>(selectedFrequencies));
    }
    private void calculateAndSetContentHeight() {
        if (recyclerView == null || adapter == null) return;

        // คำนวณความสูงตามจำนวน items
        int itemCount = adapter.getItemCount();
        int estimatedItemHeight = (int) (60 * getResources().getDisplayMetrics().density); // ประมาณความสูงต่อ item
        int totalHeight = itemCount * estimatedItemHeight;

        // บวกเพิ่ม padding
        totalHeight += recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();

        // กำหนดความสูงขั้นต่ำและสูงสุด
        int minHeight = (int) (200 * getResources().getDisplayMetrics().density);
        int maxHeight = (int) (600 * getResources().getDisplayMetrics().density);
        totalHeight = Math.max(minHeight, Math.min(totalHeight, maxHeight));

        // กำหนดความสูงให้กับ contentLayout
        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        params.height =  (int) Math.round(totalHeight*6.1);
        contentLayout.setLayoutParams(params);
    }
    public boolean validateAllQuestionsAnswered() {
        // ตรวจสอบว่าทุกคำถามมีคำตอบครบหรือไม่
        if (selectedFrequencies == null || selectedFrequencies.isEmpty()) {
            return false;
        }
        // ตรวจสอบว่าทุกรายการมีการเลือกความถี่
        for (SubstanceItem item : substanceList) {
            AnswerFrequencyData data = selectedFrequencies.get(item.getId());
            if (data == null) {
                return false;
            } else if(data != null) {
                if(data.getFrequency() == -1) {
                    return false;
                }
            }

            // ตรวจสอบกรณีเฉพาะของรายการ "อื่นๆ" (j)
            if (item.getId().equals("j") && data.getFrequency() > 0 &&
                    (data.getOtherDrugs() == null || data.getOtherDrugs().trim().isEmpty())) {
                return false;
            }
        }

        return true;
    }
}