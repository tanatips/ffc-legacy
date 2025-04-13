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
    private boolean isExpanded = false; // เก็บสถานะปัจจุบัน

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
        expandIcon = view.findViewById(R.id.expandIconOne);

        // ตั้งค่า RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceTwoAdapter(substanceList, this);
        recyclerView.setAdapter(adapter);

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

        // ตั้งค่าเริ่มต้นสำหรับทุก item
//        for (SubstanceItem item : substanceList) {
//            selectedAnswers.put(item.getId(), new AnswerData(false, ""));
//        }
//

        recyclerView = view.findViewById(R.id.recyclerViewTwo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SubstanceTwoAdapter(substanceList, this::onFrequencySelected);
        recyclerView.setAdapter(adapter);

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

        // เริ่มต้นค่าเริ่มต้นสำหรับทุก item จาก SubstanceItem
        if (selectedFrequencies.isEmpty()) {
            for (SubstanceItem item : substanceList) {
                selectedFrequencies.put(item.getId(), new AnswerFrequencyData(item.getFrequency(), ""));
            }
        }

        // ตรวจสอบว่ามีข้อมูลใน ViewModel หรือไม่
        Map<String, AnswerFrequencyData> viewModelAnswers = viewModel.getQuestionTwoAnswers().getValue();
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
                isExpanded = savedInstanceState.getBoolean("isExpanded", true);
                // กำหนดสถานะ expand/collapse ตามที่บันทึกไว้
                contentLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                expandIcon.setImageResource(isExpanded ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
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
        viewModel.getQuestionTwoAnswers().observe(getViewLifecycleOwner(), answersObserver);
        // โหลดข้อมูลจาก DB เฉพาะครั้งแรกเท่านั้น
        if (isFirstLoad && !isDataLoaded) {
            loadData();
            isFirstLoad = false;
        }
    }
    private void updateSubstanceItems(Map<String, AnswerFrequencyData> frequencies) {
        for (SubstanceItem item : substanceList) {
            AnswerFrequencyData data = frequencies.get(item.getId());
            if (data != null) {
                item.setFrequency(data.getFrequency());
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

                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q2")) {
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                    }
                }

                for (SubstanceItem item : substanceList) {
                    // ค้นหา DrugsInfo ที่ตรงกับ substance id นี้
                    DrugsInfo matchingDrug = null;
                    for (DrugsInfo drug : drugsInfos) {
                        if (drug.getQuestion().equals("Q2") &&
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

                        Log.d("QuestionTwoFragment", "Loaded item: " + item.getId() +
                                " frequency: " + frequency +
                                " otherDrugs: " + otherDrugs);
                    } else {
                        // ถ้าไม่พบข้อมูล ใช้ค่าเริ่มต้น
                        frequencies.put(item.getId(), new AnswerFrequencyData(0, ""));

                        // รีเซ็ต state ของ SubstanceItem ด้วย
                        item.setFrequency(0);
                        item.setOtherDrugs("");
                    }
                }

                if (!frequencies.isEmpty()) {
                    selectedFrequencies = frequencies;
                    viewModel.setQuestionTwoAnswers(frequencies);

                    // เพิ่ม Log เพื่อตรวจสอบว่า item "j" มีค่า otherDrugs หรือไม่
                    for (Map.Entry<String, AnswerFrequencyData> entry : frequencies.entrySet()) {
                        if (entry.getKey().equals("j")) {
                            Log.d("QuestionTwoFragment", "Item j otherDrugs: " + entry.getValue().getOtherDrugs());
                        }
                    }

                    updateUI(selectedFrequencies);
                    isDataLoaded = true;
                }

                Log.d("QuestionTwoFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            }
        });
    }
    private void updateUI(Map<String, AnswerFrequencyData> answers, String excludeId) {
        if (adapter != null) {

            AnswerFrequencyData itemJ = answers.get("j");
            if (itemJ != null) {
                Log.d("QuestionTwoFragment", "updateUI - Item j otherDrugs: " + itemJ.getOtherDrugs());
            }

            recyclerView.post(() -> {
                if (isAdded()) {
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
    private void printCurrentSelections() {
        StringBuilder result = new StringBuilder("Current frequency selections:\n");
        for (Map.Entry<String, AnswerFrequencyData> entry : selectedFrequencies.entrySet()) {
            result.append(entry.getKey())
                    .append(": frequency=")
                    .append(entry.getValue().getFrequency())
                    .append("\n");
        }
        Log.d("QuestionTwoFragment", result.toString());
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedFrequencies", new HashMap<>(selectedFrequencies));
        outState.putBoolean("isExpanded", isExpanded);
    }

    // เมธอดสำหรับเรียกดูข้อมูลที่เลือก
    public Map<String, AnswerFrequencyData> getSelectedFrequencies() {
        return new HashMap<>(selectedFrequencies);
    }
    // เมธอดสำหรับตรวจสอบว่าตอบครบทุกข้อหรือยัง
    public boolean isAllQuestionsAnswered() {
        return selectedFrequencies.size() == substanceList.size();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                // ดึงข้อมูลจาก ViewModel
                Map<String, AnswerFrequencyData> viewModelData = viewModel.getQuestionTwoAnswers().getValue();

                // ใช้ข้อมูลจาก substanceList เนื่องจาก substanceList เก็บค่าที่ user เลือกไว้
                Map<String, AnswerFrequencyData> currentSelections = new HashMap<>();
                for (SubstanceItem item : substanceList) {
                    currentSelections.put(item.getId(), new AnswerFrequencyData(item.getFrequency(), ""));
                }

                // ถ้ามีข้อมูลใน ViewModel ให้ใช้ข้อมูลจาก ViewModel เพื่อให้ค่า otherDrugs ถูกต้อง
                if (viewModelData != null && !viewModelData.isEmpty()) {
                    for (Map.Entry<String, AnswerFrequencyData> entry : viewModelData.entrySet()) {
                        AnswerFrequencyData substanceData = currentSelections.get(entry.getKey());
                        if (substanceData != null) {
                            // เก็บค่า frequency จาก SubstanceItem แต่เก็บค่า otherDrugs จาก ViewModel
                            currentSelections.put(entry.getKey(),
                                    new AnswerFrequencyData(substanceData.getFrequency(), entry.getValue().getOtherDrugs()));
                        }
                    }
                }

                // อัพเดต selectedFrequencies และ UI
                selectedFrequencies = currentSelections;
                updateUI(selectedFrequencies);

                // อัพเดต ViewModel ด้วยค่าล่าสุด
                viewModel.setQuestionTwoAnswers(selectedFrequencies);
            } finally {
                isUpdating[0] = false;
            }
        }
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
    public void notifyChildFragmentStateChanged() {
        // บังคับให้ Fragment คำนวณขนาดใหม่
        View view = getView();
        if (view != null) {
            view.requestLayout();
        }

        // แจ้ง Activity ให้ปรับขนาด ViewPager
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            new Handler().postDelayed(() -> {
                ((PersonScreeningForm15Activity) getActivity()).refreshViewPager();
            }, 200); // delay เล็กน้อยเพื่อให้ Fragment ย่อยได้คำนวณขนาดก่อน
        }
    }

}