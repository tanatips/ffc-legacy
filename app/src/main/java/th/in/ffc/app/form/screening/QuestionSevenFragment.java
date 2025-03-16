package th.in.ffc.app.form.screening;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.adapter.SubstanceFiveAdapter;
import th.in.ffc.app.form.screening.adapter.SubstanceSevenAdapter;
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.listener.OnConcernSelectedListener;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

public class QuestionSevenFragment extends Fragment implements OnFrequencySelectedListener {

    private RecyclerView recyclerView;
    private SubstanceSevenAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Map<String, AnswerFrequencyData> selectedFrequencies = new HashMap<>();

    private Observer<Map<String, AnswerFrequencyData>> answersObserver;
    // เพิ่มตัวแปร isUpdating เป็น array เพื่อให้เข้าถึงได้ใน lambda
    final boolean[] isUpdating = {false};

    // เพิ่มตัวแปรอื่นๆ ที่จำเป็น
    private Map<String, DrugsInfo> drugsInfoMap = new HashMap<>();
    private List<DrugsInfo> drugsInfos = new ArrayList<>();
    private OnDataPass dataPasser;
    private boolean isDataLoaded = false;
    private boolean isFirstLoad = true;

    public QuestionSevenFragment() {
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
        View view = inflater.inflate(R.layout.fragment_question_seven, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceSevenAdapter(substanceList, this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // เริ่มต้นค่าเริ่มต้นสำหรับทุก item
        for (SubstanceItem item : substanceList) {
            selectedFrequencies.put(item.getId(), new AnswerFrequencyData(0,""));
        }

        // ตรวจสอบว่ามีข้อมูลใน ViewModel หรือไม่
        Map<String, AnswerFrequencyData> viewModelAnswers = viewModel.getQuestionSevenAnswers().getValue();
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
        viewModel.getQuestionSevenAnswers().observe(getViewLifecycleOwner(), answersObserver);

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

                for (DrugsInfo drug : drugsInfos) {
                    if (drug.getQuestion().equals("Q7")) {  // ใช้ Q7 สำหรับ QuestionSevenFragment
                        drugsInfoMap.put(drug.getSubquestion(), drug);
                    }
                }

                for (SubstanceItem item : substanceList) {
                    // ค้นหา DrugsInfo ที่ตรงกับ substance id นี้
                    DrugsInfo matchingDrug = null;
                    for (DrugsInfo drug : drugsInfos) {
                        if (drug.getQuestion().equals("Q7") &&  // ใช้ Q7 สำหรับ QuestionSevenFragment
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

                        Log.d("QuestionSevenFragment", "Loaded item: " + item.getId() +
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
                    viewModel.setQuestionSevenAnswers(frequencies);

                    // เพิ่ม Log เพื่อตรวจสอบว่า item "j" มีค่า otherDrugs หรือไม่
                    for (Map.Entry<String, AnswerFrequencyData> entry : frequencies.entrySet()) {
                        if (entry.getKey().equals("j")) {
                            Log.d("QuestionSevenFragment", "Item j otherDrugs: " + entry.getValue().getOtherDrugs());
                        }
                    }

                    updateUI(selectedFrequencies);
                    isDataLoaded = true;
                }

                Log.d("QuestionSevenFragment", "Loaded drugs info: " + drugsInfos.size() + " items");
            }
        });
    }

    // เพิ่มเมธอด updateUI ที่รับพารามิเตอร์ excludeId
    private void updateUI(Map<String, AnswerFrequencyData> answers, String excludeId) {
        if (adapter != null && recyclerView != null) {
            // เพิ่ม Log เพื่อตรวจสอบค่า otherDrugs ของ item "j"
            AnswerFrequencyData itemJ = answers.get("j");
            if (itemJ != null) {
                Log.d("QuestionSevenFragment", "updateUI - Item j otherDrugs: " + itemJ.getOtherDrugs());
            }

            recyclerView.post(() -> {
                if (isAdded()) {
                    adapter.updateAnswers(answers, excludeId);
                }
            });
        }
    }

    // เมธอด overload สำหรับการเรียกใช้แบบเดิม
    private void updateUI(Map<String, AnswerFrequencyData> answers) {
        updateUI(answers, null);
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
                viewModel.updateQuestionSevenAnswer(id, frequency, otherDrugs);

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

            Log.d("QuestionSeven", "Item " + id + " frequency: " + frequency);
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
            Log.d("QuestionSevenFragment", "Preparing item j with otherDrugs: " + otherDrugs);
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
                drugsInfo.setQuestion("Q7");  // ใช้ Q7 สำหรับ QuestionSevenFragment
                drugsInfo.setSubquestion(currentId);
            }

            drugsInfo.setAnswer(String.valueOf(data.getFrequency()));

            // สำคัญ: ตั้งค่า otherDrugs อย่างถูกต้อง
            drugsInfo.setOtherDrugs(data.getOtherDrugs());

            drugsInfo.setUpdatedBy("SYSTEM");
            drugsInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // Log เพื่อตรวจสอบค่า otherDrugs ที่จะบันทึก
            if (currentId.equals("j")) {
                Log.d("QuestionSevenFragment", "Saving item j with otherDrugs: " + drugsInfo.getOtherDrugs());
            }

            drugsInfosToUpdate.add(drugsInfo);
        }

        // ส่งข้อมูลไปยัง dataPasser
        if (dataPasser != null) {
            dataPasser.onDrugsSevenInfo(drugsInfosToUpdate);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                Map<String, AnswerFrequencyData> currentAnswers = viewModel.getQuestionSevenAnswers().getValue();
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
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null && answersObserver != null) {
            viewModel.getQuestionFiveAnswers().removeObserver(answersObserver);
        }
        recyclerView = null;
        adapter = null;
    }

}