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
import th.in.ffc.app.form.screening.adapter.SubstanceTwoAdapter;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerData;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.person.PersonScreeningForm15Activity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionTwoFragment extends Fragment implements OnFrequencySelectedListener {

    private RecyclerView recyclerView;
    private SubstanceTwoAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private Map<String, AnswerFrequencyData> selectedFrequencies = new HashMap<>();
    private QuestionsStateViewModel viewModel;
    private Observer<Map<String, AnswerFrequencyData>> answersObserver;
    private OnDataPass dataPasser;
    private Map<String, DrugsInfo> drugsInfoMap = new HashMap<>();
    final boolean[] isUpdating = {false};
    List<DrugsInfo> drugsInfos = new ArrayList<>();

    private boolean isDataLoaded = false;
    private boolean isFirstLoad = true;
    LinearLayout headerLayout;
    LinearLayout contentLayout;
    ImageView expandIcon;

    public QuestionTwoFragment() {
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

    public static QuestionTwoFragment newInstance(String param1, String param2) {
        QuestionTwoFragment fragment = new QuestionTwoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_question_two, container, false);

        // ค้นหา Views
        recyclerView = view.findViewById(R.id.recyclerViewTwo);
        LinearLayout headerLayout = view.findViewById(R.id.headerLayoutTwo);
        contentLayout = view.findViewById(R.id.contentLayoutTwo);
        expandIcon = view.findViewById(R.id.expandIconTwo);

        // ตั้งค่า RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceTwoAdapter(substanceList, this);
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
                    calculateAndSetContentHeight();
                } else {
                    contentLayout.setVisibility(View.VISIBLE);
                    expandIcon.setImageResource(R.drawable.ic_expand_less);
                    calculateAndSetContentHeight();
                    notifyParentOfChange();
                }
            }
        });
        return view;
    }
    public void clearAllData() {
        try {
            isUpdating[0] = true;

            // ล้างข้อมูลใน selectedFrequencies
            for (String key : selectedFrequencies.keySet()) {
                selectedFrequencies.put(key, new AnswerFrequencyData(-1, ""));
            }

            // ล้างข้อมูลใน SubstanceItems
            for (SubstanceItem item : substanceList) {
                item.setFrequency(0);
                item.setOtherDrugs("");
            }

            // อัพเดต ViewModel
            viewModel.setQuestionTwoAnswers(new HashMap<>(selectedFrequencies));

            // อัพเดต UI
            updateUI(selectedFrequencies);

            Log.d("QuestionTwoFragment", "ล้างข้อมูลทั้งหมดเสร็จสิ้น");
        } catch (Exception e) {
            Log.e("QuestionTwoFragment", "เกิดข้อผิดพลาดในการล้างข้อมูล: " + e.getMessage());
        } finally {
            isUpdating[0] = false;
        }
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

        // เริ่มต้นเมื่อ fragment ถูกสร้าง
        Log.d("QuestionTwoFragment", "onViewCreated - Starting");

        // เริ่มต้นค่าเริ่มต้นสำหรับทุก item จาก SubstanceItem
        if (selectedFrequencies.isEmpty()) {
            for (SubstanceItem item : substanceList) {
                selectedFrequencies.put(item.getId(), new AnswerFrequencyData(item.getFrequency(), ""));
            }
        }

        // ตรวจสอบว่ามีข้อมูลจาก savedInstanceState ก่อน
        if (savedInstanceState != null) {
            Map<String, AnswerFrequencyData> savedFrequencies = (Map<String, AnswerFrequencyData>) savedInstanceState.getSerializable("selectedFrequencies");
            if (savedFrequencies != null && !savedFrequencies.isEmpty()) {
                Log.d("QuestionTwoFragment", "onViewCreated - Restoring from savedInstanceState");
                selectedFrequencies = new HashMap<>(savedFrequencies);
                updateSubstanceItems(selectedFrequencies);
                updateUI(selectedFrequencies);
                isDataLoaded = true;
            }
        }

        // ถ้าไม่มีข้อมูลจาก savedInstanceState ให้ตรวจสอบว่ามีข้อมูลใน ViewModel หรือไม่
        if (!isDataLoaded) {
            Map<String, AnswerFrequencyData> viewModelAnswers = viewModel.getQuestionTwoAnswers().getValue();
            if (viewModelAnswers != null && !viewModelAnswers.isEmpty()) {
                // ถ้ามีข้อมูลใน ViewModel ให้ใช้ข้อมูลนั้น
                Log.d("QuestionTwoFragment", "onViewCreated - Using data from ViewModel");
                selectedFrequencies = new HashMap<>(viewModelAnswers);

                // Log ข้อมูลที่ได้จาก ViewModel
                for (Map.Entry<String, AnswerFrequencyData> entry : viewModelAnswers.entrySet()) {
                    Log.d("QuestionTwoFragment", "ViewModel data - item " + entry.getKey() +
                            " frequency: " + entry.getValue().getFrequency() +
                            " otherDrugs: " + entry.getValue().getOtherDrugs());
                }

                // อัพเดตค่าใน SubstanceItems เพื่อเก็บค่าไว้
                updateSubstanceItems(selectedFrequencies);
                updateUI(selectedFrequencies);
                isDataLoaded = true;
            }
        }

        // ตั้งค่า Observer เพื่อสังเกตการเปลี่ยนแปลงข้อมูลใน ViewModel
        answersObserver = answers -> {
            if (answers != null && isAdded() && !isUpdating[0]) {
                isUpdating[0] = true;
                try {
                    Log.d("QuestionTwoFragment", "Observer triggered - Updating UI");
                    selectedFrequencies = new HashMap<>(answers);
                    // อัพเดต UI ด้วยค่าปัจจุบัน
                    updateUI(selectedFrequencies);
                } finally {
                    isUpdating[0] = false;
                }
            }
        };
        viewModel.getQuestionTwoAnswers().observe(getViewLifecycleOwner(), answersObserver);

        // โหลดข้อมูลจาก DB เฉพาะครั้งแรกเท่านั้น
//        if (isFirstLoad && !isDataLoaded) {
//            Log.d("QuestionTwoFragment", "onViewCreated - Loading data from database");
            loadData();
//            isFirstLoad = false;
//        }

        // เพิ่ม log เพื่อตรวจสอบค่าสุดท้าย
        for (SubstanceItem item : substanceList) {
            Log.d("QuestionTwoFragment", "Final state - item " + item.getId() +
                    " frequency: " + item.getFrequency() +
                    " otherDrugs: " + item.getOtherDrugs());
        }
    }
    // แก้ไขเมธอด updateSubstanceItems เพื่อให้มั่นใจว่า substanceList จะถูกอัพเดตอย่างถูกต้อง
    private void updateSubstanceItems(Map<String, AnswerFrequencyData> frequencies) {
        for (SubstanceItem item : substanceList) {
            AnswerFrequencyData data = frequencies.get(item.getId());
            if (data != null) {
                // แก้ไขส่วนนี้เพื่อเพิ่ม log
                Log.d("QuestionTwoFragment", "updateSubstanceItems - Updating item " +
                        item.getId() + " frequency from " + item.getFrequency() +
                        " to " + data.getFrequency());

                // อัพเดตทั้ง frequency และ otherDrugs
                item.setFrequency(data.getFrequency());
                item.setOtherDrugs(data.getOtherDrugs());
            }
        }
    }

    private void loadData() {
        SfDrugsDao sfDrugsDao = new SfDrugsDao(getContext());
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getDrugsLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && data.getPersonId() != null) {
                List<DrugsInfo> drugsInfos = sfDrugsDao.getSfDrugsByPersonInfoId(Integer.valueOf(data.getPersonId()));
                this.drugsInfos = drugsInfos;
                Map<String, AnswerFrequencyData> frequencies = new HashMap<>();
                drugsInfoMap.clear();

                // เก็บข้อมูลจากฐานข้อมูลลงใน drugsInfoMap
                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q2")) {
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                        Log.d("QuestionTwoFragment", "Found drug info: " + drug.getSubquestion() +
                                " with answer: " + drug.getAnswer() +
                                " otherDrugs: " + drug.getOtherDrugs());
                    }
                }

                for (SubstanceItem item : substanceList) {
                    // ค้นหา DrugsInfo ที่ตรงกับ substance id นี้
                    DrugsInfo matchingDrug = drugsInfoMap.get(item.getId());

                    if (matchingDrug != null) {
                        // ถ้าพบข้อมูล ใช้ค่าจากฐานข้อมูล
                        // แก้ไขตรงนี้ - ตรวจสอบให้มั่นใจว่ามีการแปลงข้อมูลที่ถูกต้อง
                        int frequency = 0;
                        try {
                            frequency = Integer.parseInt(matchingDrug.getAnswer());
                            // ตรวจสอบว่าค่า frequency เป็นค่าที่ถูกต้อง (0, 2, 3, 4, 6)
                            if (frequency != 0 && frequency != 2 && frequency != 3 &&
                                    frequency != 4 && frequency != 6) {
                                Log.w("QuestionTwoFragment", "Invalid frequency value: " + frequency +
                                        " for item: " + item.getId() + ", setting to 0");
                                frequency = 0;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("QuestionTwoFragment", "Error parsing frequency: " +
                                    matchingDrug.getAnswer() + " for item: " + item.getId(), e);
                            frequency = 0;
                        }

                        String otherDrugs = matchingDrug.getOtherDrugs() != null ? matchingDrug.getOtherDrugs() : "";

                        // สำคัญ: อัปเดตทั้ง frequencies และ SubstanceItem
                        frequencies.put(item.getId(), new AnswerFrequencyData(frequency, otherDrugs));

                        // อัปเดต state ของ SubstanceItem ให้ครบทั้ง frequency และ otherDrugs
                        item.setFrequency(frequency);
                        item.setOtherDrugs(otherDrugs);

                        Log.d("QuestionTwoFragment", "Loaded item: " + item.getId() +
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

                    // อัปเดต ViewModel ด้วยข้อมูลที่โหลดมา
                    viewModel.setQuestionTwoAnswers(frequencies);

                    // เพิ่ม Log เพื่อตรวจสอบว่า item "i" และ "j" มีค่า frequency และ otherDrugs ถูกต้องหรือไม่
                    for (Map.Entry<String, AnswerFrequencyData> entry : frequencies.entrySet()) {
                        if (entry.getKey().equals("i") || entry.getKey().equals("j")) {
                            Log.d("QuestionTwoFragment", "Item " + entry.getKey() +
                                    " frequency: " + entry.getValue().getFrequency() +
                                    " otherDrugs: " + entry.getValue().getOtherDrugs());
                        }
                    }

                    // อัพเดต UI ด้วยข้อมูลที่โหลดมา
                    updateUI(selectedFrequencies);
                    isDataLoaded = true;
                }

                Log.d("QuestionTwoFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            }
        });
    }

    private void updateUI(Map<String, AnswerFrequencyData> answers, String excludeId) {
        if (adapter != null) {
            // เพิ่มการ log ทุก entry เพื่อตรวจสอบค่า
            for (Map.Entry<String, AnswerFrequencyData> entry : answers.entrySet()) {
                Log.d("QuestionTwoFragment", "updateUI - Item " + entry.getKey() +
                        " frequency: " + entry.getValue().getFrequency() +
                        " otherDrugs: " + entry.getValue().getOtherDrugs());
            }

            recyclerView.post(() -> {
                if (isAdded()) {
                    // ในบางครั้ง adapter อาจไม่ได้อัพเดตข้อมูลตัวเอง แม้ selectedFrequencies จะมีค่าถูกต้อง
                    // ให้อัพเดตค่าใน substanceList ก่อนเรียก adapter.updateAnswers
                    updateSubstanceItems(answers);

                    // แล้วจึงเรียก adapter.updateAnswers เพื่ออัพเดต UI
                    adapter.updateAnswers(answers, excludeId);
                }
            });
        }
    }
    // เพิ่ม overload สำหรับความเข้ากันได้กับโค้ดเดิม
    private void updateUI(Map<String, AnswerFrequencyData> answers) {
        updateUI(answers, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ยกเลิก observer เมื่อ view ถูกทำลาย
        if (viewModel != null && answersObserver != null) {
            viewModel.getQuestionTwoAnswers().removeObserver(answersObserver);
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
                viewModel.updateQuestionTwoAnswer(id, frequency, otherDrugs);

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

                // เมื่อมีการแก้ไขช่อง "j" (ระบุสารเสพติดอื่นๆ) ไม่ให้อัปเดตช่องนั้น
                if (id.equals("j")) {
                    updateUI(selectedFrequencies, id);
                } else {
                    updateUI(selectedFrequencies);
                }
            }

            Log.d("Question Two", "Item " + id + " frequency: " + frequency);
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
            Log.d("QuestionTwoFragment", "Preparing item j with otherDrugs: " + otherDrugs);
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
            for (DrugsInfo drugsInfo1 : this.drugsInfos) {
                if (drugsInfo1.getSubquestion().equals(currentId) && "Q2".equals(drugsInfo1.getQuestion())) {
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
                    found = true;
                    break;
                }
            }

            // ค้นหาข้อมูลเดิมจาก drugsInfoMap
            if (!found) {
                DrugsInfo existingInfo = drugsInfoMap.get(currentId);
                if (existingInfo != null) {
                    drugsInfo.setId(existingInfo.getId());
                    drugsInfo.setCreatedDate(existingInfo.getCreatedDate());
                    drugsInfo.setCreatedBy(existingInfo.getCreatedBy());
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
                drugsInfo.setQuestion("Q2");
                drugsInfo.setSubquestion(currentId);
            }

            drugsInfo.setAnswer(String.valueOf(data.getFrequency()));

            // สำคัญ: ตั้งค่า otherDrugs อย่างถูกต้อง
            drugsInfo.setOtherDrugs(data.getOtherDrugs());

            drugsInfo.setUpdatedBy("SYSTEM");
            drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // Log เพื่อตรวจสอบค่า otherDrugs ที่จะบันทึก
            if (currentId.equals("j")) {
                Log.d("QuestionTwoFragment", "Saving item j with otherDrugs: " + drugsInfo.getOtherDrugs());
            }

            drugsInfosToUpdate.add(drugsInfo);
        }

        dataPasser.onDrugsTwoInfo(drugsInfosToUpdate);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                Map<String, AnswerFrequencyData> currentAnswers = viewModel.getQuestionTwoAnswers().getValue();
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
    public String getValidationMessage() {
        List<String> missingAnswers = new ArrayList<>();
        List<String> missingDetails = new ArrayList<>();

        for (SubstanceItem item : substanceList) {
            AnswerFrequencyData data = selectedFrequencies.get(item.getId());
            String itemName = getItemDisplayName(item.getId());

            if (data == null || data.getFrequency() == -1) {
                missingAnswers.add(itemName);
            } else if (item.getId().equals("j") && data.getFrequency() > 0 &&
                    (data.getOtherDrugs() == null || data.getOtherDrugs().trim().isEmpty())) {
                missingDetails.add(itemName + " (ต้องระบุชื่อสารเสพติด)");
            }
        }

        StringBuilder message = new StringBuilder();
        if (!missingAnswers.isEmpty() || !missingDetails.isEmpty()) {
            message.append("คำถามที่ 2: ");

            if (!missingAnswers.isEmpty()) {
                message.append("ยังไม่ได้เลือกความถี่: ");
                message.append(String.join(", ", missingAnswers));
            }

            if (!missingDetails.isEmpty()) {
                if (!missingAnswers.isEmpty()) {
                    message.append("; ");
                }
                message.append("ต้องกรอกข้อมูลเพิ่มเติม: ");
                message.append(String.join(", ", missingDetails));
            }
        }

        return message.toString();
    }

    private String getItemDisplayName(String id) {
        switch (id) {
            case "a": return "a. ผลิตภัณฑ์ยาสูบ";
            case "b": return "b. เครื่องดื่มแอลกอฮอล์";
            case "c": return "c. กัญชา";
            case "d": return "d. โคเคน";
            case "e": return "e. ยากระตุ้นประสาทกลุ่มแอมเฟตามีน";
            case "f": return "f. สารระเหย";
            case "g": return "g. ยากล่อมประสาทหรือยานอนหลับ";
            case "h": return "h. ยาหลอนประสาท";
            case "i": return "i. สารกลุ่มฝิ่น";
            case "j": return "j. สารเสพติดอื่น ๆ";
            default: return id;
        }
    }
}

