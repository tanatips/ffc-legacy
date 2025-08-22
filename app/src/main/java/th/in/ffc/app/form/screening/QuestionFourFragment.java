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
import java.util.Locale;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.adapter.SubstanceFourAdapter;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.person.PersonScreeningForm15Activity;
import th.in.ffc.session.UserSessionManager;
import th.in.ffc.util.ContentHeightCalculator;
import th.in.ffc.util.DateConverter;


public class QuestionFourFragment extends Fragment implements OnFrequencySelectedListener {

    private RecyclerView recyclerView;
    private SubstanceFourAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Map<String, AnswerFrequencyData> selectedFrequencies = new HashMap<>();
    private Observer<Map<String, AnswerFrequencyData>> answersObserver;
    final boolean[] isUpdating = {false}; // เพิ่มตัวแปรเพื่อป้องกันการอัปเดตซ้ำซ้อน

    // เพิ่มตัวแปรต่อไปนี้ใน QuestionFourFragment
    private Map<String, DrugsInfo> drugsInfoMap = new HashMap<>();
    private List<DrugsInfo> drugsInfos = new ArrayList<>();
    private OnDataPass dataPasser;
    private boolean isDataLoaded = false;
    private boolean isFirstLoad = true;
    LinearLayout headerLayout;
    LinearLayout contentLayout;
    ImageView expandIcon;
    private QuestionsStateViewModel questionsStateViewModel;

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
        // ไม่ต้องกำหนด questionsStateViewModel
        questionsStateViewModel = viewModel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question_four, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewFour);
        headerLayout = view.findViewById(R.id.headerLayoutFour);
        contentLayout = view.findViewById(R.id.contentLayoutFour);
        expandIcon = view.findViewById(R.id.expandIconFour);

        // ตั้งค่า RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceFourAdapter(substanceList, this);
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

    public void updateSubstanceList(ArrayList<SubstanceItem> newSubstanceList) {
        Log.d("QuestionFourFragment", "=== updateSubstanceList START ===");
        Log.d("QuestionFourFragment", "Old list size: " + substanceList.size() + ", New list size: " + newSubstanceList.size());

        Map<String, AnswerFrequencyData> currentData = new HashMap<>();
        if (questionsStateViewModel != null) {
            Map<String, AnswerFrequencyData> viewModelData = questionsStateViewModel.getQuestionFourAnswers().getValue();
            if (viewModelData != null) {
                currentData.putAll(viewModelData);
                Log.d("QuestionFourFragment", "Current ViewModel data preserved");
            }
        }
        // อัปเดตรายการสารเสพติด
        this.substanceList = newSubstanceList;

        // รีเฟรช UI
        if (adapter != null) {
            adapter.updateSubstanceList(newSubstanceList);
            adapter.notifyDataSetChanged();
            recyclerView.post(() -> {
                ContentHeightCalculator.calculateAutoHeight(recyclerView, contentLayout);
            });
        }

        // ✅ อัปเดต ViewModel โดยรักษาข้อมูลเดิม
        updateViewModelForNewSubstanceList(newSubstanceList);

        Log.d("QuestionFourFragment", "=== updateSubstanceList END ===");

//        // อัปเดตรายการสารเสพติดที่แสดง
//        this.substanceList = newSubstanceList;
//
//        // รีเฟรช UI
//        if (adapter != null) {
//            adapter.updateSubstanceList(newSubstanceList);
//            adapter.notifyDataSetChanged();
//        }
//
//
//        updateViewModelForNewSubstanceList(newSubstanceList);
//        validateViewModelData();
    }
    private void updateViewModelForNewSubstanceList(ArrayList<SubstanceItem> newSubstanceList) {
        if (questionsStateViewModel != null) {
            // ✅ ใช้ข้อมูลที่มาจาก SubstanceItem โดยตรง (ที่ได้ถูกใส่ข้อมูลปัจจุบันมาแล้ว)
            Map<String, AnswerFrequencyData> updatedAnswers = new HashMap<>();

            for (SubstanceItem item : newSubstanceList) {
                String substanceId = item.getId();

                // ✅ ใช้ข้อมูลจาก SubstanceItem ที่ได้รับมา (ที่มีข้อมูลปัจจุบันอยู่แล้ว)
                AnswerFrequencyData data = new AnswerFrequencyData(
                        item.getFrequency(),
                        item.getOtherDrugs() != null ? item.getOtherDrugs() : ""
                );

                updatedAnswers.put(substanceId, data);

                Log.d("QuestionFourFragment", "Updated substance " + substanceId +
                        " with frequency: " + item.getFrequency() +
                        ", otherDrugs: " + item.getOtherDrugs());
            }

            // อัปเดต ViewModel ด้วยข้อมูลที่เตรียมไว้
            questionsStateViewModel.setQuestionFourAnswers(updatedAnswers);

            Log.d("QuestionFourFragment", "ViewModel updated with " + updatedAnswers.size() + " substances (from SubstanceItem data)");
        }
    }

    public void clearAllData() {
        Log.d("QuestionFourFragment", "Clearing all data");

        // ล้างใน Adapter
        if (adapter != null) {
            adapter.clearAllData();
        }

        // ✅ แก้ไข: ล้างใน ViewModel - ใช้ questionsStateViewModel
        if (questionsStateViewModel != null) {
            Map<String, AnswerFrequencyData> emptyAnswers = new HashMap<>();

            // สร้างข้อมูลว่างสำหรับทุกสารเสพติดในรายการปัจจุบัน
            if (substanceList != null) {
                for (SubstanceItem item : substanceList) {
                    emptyAnswers.put(item.getId(), new AnswerFrequencyData(-1, ""));
                }
            }

            questionsStateViewModel.setQuestionFourAnswers(emptyAnswers);
        }

        Log.d("QuestionFourFragment", "All data cleared");
    }

    /**
     * เมธอดใหม่สำหรับล้างคำตอบของสารเสพติดที่ระบุ
     */
    public void clearAnswerForSubstance(String substanceId) {
        Log.d("QuestionFourFragment", "Clearing answer for substance: " + substanceId);

        // ล้างใน Adapter
        if (adapter != null) {
            adapter.clearAnswerForSubstance(substanceId);
        }

        // ✅ แก้ไข: ล้างใน ViewModel - ใช้ questionsStateViewModel และ QuestionFourAnswers
        if (questionsStateViewModel != null) {
            Map<String, AnswerFrequencyData> currentAnswers = questionsStateViewModel.getQuestionFourAnswers().getValue();
            if (currentAnswers != null && currentAnswers.containsKey(substanceId)) {
                currentAnswers.put(substanceId, new AnswerFrequencyData(-1, ""));
                questionsStateViewModel.setQuestionFourAnswers(currentAnswers);
            }
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
        Log.d("QuestionFourFragment", "onViewCreated - Starting");

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
                Log.d("QuestionFourFragment", "onViewCreated - Restoring from savedInstanceState");
                selectedFrequencies = new HashMap<>(savedFrequencies);
                updateSubstanceItems(selectedFrequencies);
                updateUI(selectedFrequencies);
                isDataLoaded = true;
            }
        }

        // ถ้าไม่มีข้อมูลจาก savedInstanceState ให้ตรวจสอบว่ามีข้อมูลใน ViewModel หรือไม่
        if (!isDataLoaded) {
            Map<String, AnswerFrequencyData> viewModelAnswers = viewModel.getQuestionFourAnswers().getValue();
            if (viewModelAnswers != null && !viewModelAnswers.isEmpty()) {
                // ถ้ามีข้อมูลใน ViewModel ให้ใช้ข้อมูลนั้น
                Log.d("QuestionFourFragment", "onViewCreated - Using data from ViewModel");
                selectedFrequencies = new HashMap<>(viewModelAnswers);

                // Log ข้อมูลที่ได้จาก ViewModel
                for (Map.Entry<String, AnswerFrequencyData> entry : viewModelAnswers.entrySet()) {
                    Log.d("QuestionFourFragment", "ViewModel data - item " + entry.getKey() +
                            " frequency: " + entry.getValue().getFrequency() +
                            " otherDrugs: " + entry.getValue().getOtherDrugs());
                }

                // อัปเดตค่าใน SubstanceItems เพื่อเก็บค่าไว้
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
                    Log.d("QuestionFourFragment", "=== Observer triggered ===");
                    validateViewModelData();
                    // ✅ ตรวจสอบว่าข้อมูลเป็นของ Q4 จริงหรือไม่
                    boolean isValidQ4Data = true;
                    for (Map.Entry<String, AnswerFrequencyData> entry : answers.entrySet()) {
                        int freq = entry.getValue().getFrequency();
                        // Q4 ควรมีเฉพาะ: -1, 0, 4, 5, 6, 7
                        if (freq != -1 && freq != 0 && freq != 4 && freq != 5 && freq != 6 && freq != 7) {
                            Log.w("QuestionFourFragment", "INVALID Q4 DATA DETECTED! Item " + entry.getKey() +
                                    " has frequency " + freq + " which is not valid for Q4");
                            isValidQ4Data = false;
                        }
                    }

                    if (isValidQ4Data) {
                        selectedFrequencies = new HashMap<>(answers);
                        updateUI(selectedFrequencies);
                    } else {
                        Log.e("QuestionFourFragment", "BLOCKING INVALID DATA UPDATE for Q4");
                    }
                } finally {
                    isUpdating[0] = false;
                }
            }
        };
        viewModel.getQuestionFourAnswers().observe(getViewLifecycleOwner(), answersObserver);

        // โหลดข้อมูลจาก DB
        loadData();

        // เพิ่ม log เพื่อตรวจสอบค่าสุดท้าย
        for (SubstanceItem item : substanceList) {
            Log.d("QuestionFourFragment", "Final state - item " + item.getId() +
                    " frequency: " + item.getFrequency() +
                    " otherDrugs: " + item.getOtherDrugs());
        }
    }

    // แก้ไขเมธอด updateSubstanceItems เพื่อให้มั่นใจว่า substanceList จะถูกอัปเดตอย่างถูกต้อง
    private void updateSubstanceItems(Map<String, AnswerFrequencyData> frequencies) {
        for (SubstanceItem item : substanceList) {
            AnswerFrequencyData data = frequencies.get(item.getId());
            if (data != null) {
                // แก้ไขส่วนนี้เพื่อเพิ่ม log
                Log.d("QuestionFourFragment", "updateSubstanceItems - Updating item " +
                        item.getId() + " frequency from " + item.getFrequency() +
                        " to " + data.getFrequency());

                // อัปเดตทั้ง frequency และ otherDrugs
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
                List<DrugsInfo> drugFourInfo = new ArrayList<>();
                Map<String, AnswerFrequencyData> frequencies = new HashMap<>();
                drugsInfoMap.clear();

                // เก็บข้อมูลจากฐานข้อมูลลงใน drugsInfoMap
                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q4")) {
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                        drugFourInfo.add(drug);
                        Log.d("QuestionFourFragment", "Found drug info: " + drug.getSubquestion() +
                                " with answer: " + drug.getAnswer() +
                                " otherDrugs: " + drug.getOtherDrugs());
                    }
                }

                for (SubstanceItem item : substanceList) {
                    // ค้นหา DrugsInfo ที่ตรงกับ substance id นี้
                    DrugsInfo matchingDrug = drugsInfoMap.get(item.getId());

                    if (matchingDrug != null) {
                        // ถ้าพบข้อมูล ใช้ค่าจากฐานข้อมูล
                        int frequency = 0;
                        try {
                            frequency = Integer.parseInt(matchingDrug.getAnswer());
                            // ตรวจสอบว่าค่า frequency เป็นค่าที่ถูกต้อง (0, 4, 5, 6, 7)
                            if (frequency != 0 && frequency != 4 && frequency != 5 &&
                                    frequency != 6 && frequency != 7) {
                                Log.w("QuestionFourFragment", "Invalid frequency value: " + frequency +
                                        " for item: " + item.getId() + ", setting to -1");
                                frequency = -1;
                            }
                        } catch (NumberFormatException e) {
                            Log.e("QuestionFourFragment", "Error parsing frequency: " +
                                    matchingDrug.getAnswer() + " for item: " + item.getId(), e);
                            frequency = -1;
                        }

                        String otherDrugs = matchingDrug.getOtherDrugs() != null ? matchingDrug.getOtherDrugs() : "";

                        // สำคัญ: อัปเดตทั้ง frequencies และ SubstanceItem
                        frequencies.put(item.getId(), new AnswerFrequencyData(frequency, otherDrugs));

                        // อัปเดต state ของ SubstanceItem ให้ครบทั้ง frequency และ otherDrugs
                        item.setFrequency(frequency);
                        item.setOtherDrugs(otherDrugs);

                        Log.d("QuestionFourFragment", "Loaded item: " + item.getId() +
                                " frequency: " + frequency +
                                " otherDrugs: " + otherDrugs);
                    } else {
                        // ถ้าไม่พบข้อมูล ใช้ค่าเริ่มต้น
                        frequencies.put(item.getId(), new AnswerFrequencyData(-1, ""));

                        // รีเซ็ต state ของ SubstanceItem ด้วย
                        item.setFrequency(-1);
                        item.setOtherDrugs("");
                    }
                }

                if (!frequencies.isEmpty()) {
                    selectedFrequencies = frequencies;

                    // อัปเดต ViewModel ด้วยข้อมูลที่โหลดมา
                    viewModel.setQuestionFourAnswers(frequencies);

                    // เพิ่ม Log เพื่อตรวจสอบว่า item "i" และ "j" มีค่า frequency และ otherDrugs ถูกต้องหรือไม่
                    for (Map.Entry<String, AnswerFrequencyData> entry : frequencies.entrySet()) {
                        if (entry.getKey().equals("i") || entry.getKey().equals("j")) {
                            Log.d("QuestionFourFragment", "Item " + entry.getKey() +
                                    " frequency: " + entry.getValue().getFrequency() +
                                    " otherDrugs: " + entry.getValue().getOtherDrugs());
                        }
                    }
                    validateViewModelData();
                    // อัปเดต UI ด้วยข้อมูลที่โหลดมา
                    updateUI(selectedFrequencies);
                    isDataLoaded = true;
                }
                dataPasser.onDrugsFourInfo(drugFourInfo);
                Log.d("QuestionFourFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            } else {
                // ⌛ ปัญหาที่ 4: กรณีไม่มี personId ก็ต้องกำหนดค่าเริ่มต้นเป็น -1
                Log.d("QuestionFourFragment", "No person data available, initializing with -1 values");
                Map<String, AnswerFrequencyData> emptyFrequencies = new HashMap<>();
                for (SubstanceItem item : substanceList) {
                    emptyFrequencies.put(item.getId(), new AnswerFrequencyData(-1, ""));
                    item.setFrequency(-1);
                    item.setOtherDrugs("");
                }
                selectedFrequencies = emptyFrequencies;
                viewModel.setQuestionFourAnswers(emptyFrequencies);
                updateUI(selectedFrequencies);
            }

        });
    }

    private void updateUI(Map<String, AnswerFrequencyData> answers, String excludeId) {
        if (adapter != null) {
            // เพิ่มการ log ทุก entry เพื่อตรวจสอบค่า
            for (Map.Entry<String, AnswerFrequencyData> entry : answers.entrySet()) {
                Log.d("QuestionFourFragment", "updateUI - Item " + entry.getKey() +
                        " frequency: " + entry.getValue().getFrequency() +
                        " otherDrugs: " + entry.getValue().getOtherDrugs());
            }

            recyclerView.post(() -> {
                if (isAdded()) {
                    // ในบางครั้ง adapter อาจไม่ได้อัปเดตข้อมูลตัวเอง แม้ selectedFrequencies จะมีค่าถูกต้อง
                    // ให้อัปเดตค่าใน substanceList ก่อนเรียก adapter.updateAnswers
                    updateSubstanceItems(answers);

                    // แล้วจึงเรียก adapter.updateAnswers เพื่ออัปเดต UI
                    adapter.updateAnswers(answers, excludeId);

                    Log.d("QuestionFourFragment", "=== updateUI AFTER UPDATE ===");
                    for (SubstanceItem item : substanceList) {
                        Log.d("QuestionFourFragment", "updateUI RESULT - Item " + item.getId() +
                                " frequency: " + item.getFrequency() +
                                " otherDrugs: " + item.getOtherDrugs());
                    }
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
            viewModel.getQuestionFourAnswers().removeObserver(answersObserver);
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
                // อัปเดตค่าใน selectedFrequencies
                selectedFrequencies.put(id, new AnswerFrequencyData(frequency, otherDrugs));

                // อัปเดตค่าใน ViewModel
                viewModel.updateQuestionFourAnswer(id, frequency, otherDrugs);
                validateViewModelData();
                // อัปเดตค่าใน SubstanceItem เพื่อเก็บไว้ใช้ต่อ
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

            Log.d("Question Four", "Item " + id + " frequency: " + frequency);
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
            Log.d("QuestionFourFragment", "Preparing item j with otherDrugs: " + otherDrugs);
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
                if (drugsInfo1.getSubquestion().equals(currentId) && "Q4".equals(drugsInfo1.getQuestion())) {
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

            UserSessionManager sessionManager = new UserSessionManager(getContext());
            String userCreate = sessionManager.getUser();
            // กำหนดค่าใหม่
            if (!found) {
                drugsInfo.setCreatedBy(userCreate);
                drugsInfo.setCreatedDate(DateConverter.getCurrentThaiBuddhistDateTime());
            }

            if (drugsInfo.getPersonInfoId() == null) {
                drugsInfo.setPersonInfoId(personInfoId);
                drugsInfo.setIdcard(personInfoData != null ? personInfoData.getIdcard() : "");
                drugsInfo.setQuestion("Q4");
                drugsInfo.setSubquestion(currentId);
            }

            drugsInfo.setAnswer(String.valueOf(data.getFrequency()));

            // สำคัญ: ตั้งค่า otherDrugs อย่างถูกต้อง
            drugsInfo.setOtherDrugs(data.getOtherDrugs());

            drugsInfo.setUpdatedBy(userCreate);
            drugsInfo.setUpdatedDate(DateConverter.getCurrentThaiBuddhistDateTime());

            // Log เพื่อตรวจสอบค่า otherDrugs ที่จะบันทึก
            if (currentId.equals("j")) {
                Log.d("QuestionFourFragment", "Saving item j with otherDrugs: " + drugsInfo.getOtherDrugs());
            }

            drugsInfosToUpdate.add(drugsInfo);
        }

        dataPasser.onDrugsFourInfo(drugsInfosToUpdate);
    }
    private void validateViewModelData() {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(new Date());

        Map<String, AnswerFrequencyData> q4Data = viewModel.getQuestionFourAnswers().getValue();
        Map<String, AnswerFrequencyData> q7Data = viewModel.getQuestionSevenAnswers().getValue();

        Log.d("QuestionFourFragment", "=== ViewModel Validation [" + timestamp + "] ===");

        if (q4Data != null) {
            for (Map.Entry<String, AnswerFrequencyData> entry : q4Data.entrySet()) {
                if (entry.getKey().equals("a") || entry.getKey().equals("b")) {
                    Log.d("QuestionFourFragment", "Q4 ViewModel - " + entry.getKey() + ": " + entry.getValue().getFrequency());
                }
            }
        }

        if (q7Data != null) {
            for (Map.Entry<String, AnswerFrequencyData> entry : q7Data.entrySet()) {
                if (entry.getKey().equals("a") || entry.getKey().equals("b")) {
                    Log.d("QuestionFourFragment", "Q7 ViewModel - " + entry.getKey() + ": " + entry.getValue().getFrequency());
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        validateViewModelData();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                Map<String, AnswerFrequencyData> currentAnswers = viewModel.getQuestionFourAnswers().getValue();
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
        ContentHeightCalculator.calculateAutoHeight(recyclerView, contentLayout);
    }

    public boolean validateAllQuestionsAnswered() {
        // ตรวจสอบว่าทุกคำถามมีคำตอบครบหรือไม่
        return getValidationMessage().isEmpty();
    }

    public String getValidationMessage() {
        if (substanceList == null || substanceList.isEmpty()) {
            return ""; // ถ้าไม่มีสารเสพติดให้ตรวจสอบ ถือว่าผ่าน
        }

        List<String> unansweredSubstances = new ArrayList<>();
        List<String> missingDetails = new ArrayList<>();

        // ตรวจสอบแต่ละสารเสพติดในรายการ
        for (SubstanceItem item : substanceList) {
            if (item.getFrequency() == -1) { // -1 หมายถึงยังไม่ได้เลือกคำตอบ
                unansweredSubstances.add(item.getName());
            } else if (item.getId().equals("j") && item.getFrequency() > 0 &&
                    (item.getOtherDrugs() == null || item.getOtherDrugs().trim().isEmpty())) {
                // ตรวจสอบกรณีเฉพาะของรายการ "อื่นๆ" (j)
                missingDetails.add(item.getName() + " (ต้องระบุชื่อสารเสพติด)");
            }
        }

        StringBuilder message = new StringBuilder();
        if (!unansweredSubstances.isEmpty() || !missingDetails.isEmpty()) {
            message.append("คำถามที่ 4: ");

            if (!unansweredSubstances.isEmpty()) {
                message.append("กรุณาตอบความถี่การใช้สารเสพติดต่อไปนี้:\n• ");
                message.append(String.join("\n• ", unansweredSubstances));
            }

            if (!missingDetails.isEmpty()) {
                if (!unansweredSubstances.isEmpty()) {
                    message.append("\n\n");
                }
                message.append("ต้องกรอกข้อมูลเพิ่มเติม:\n• ");
                message.append(String.join("\n• ", missingDetails));
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
            case "i": return "i. สารกลุ่มกิ่น";
            case "j": return "j. สารเสพติดอื่น ๆ";
            default: return id;
        }
    }
}