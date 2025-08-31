package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
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
import th.in.ffc.app.form.screening.dao.SfDrugsDao;
import th.in.ffc.app.form.screening.datalive.DrugsLiveData;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.model.AnswerData;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.person.AssistScoreFragment;
import th.in.ffc.person.PersonScreeningForm15Activity;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainQuestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainQuestionsFragment extends Fragment {

    private static final String TAG = "MainQuestionsFragment";

    private DrugsLiveData drugsLiveData;
    private QuestionOneFragment questionOneFragment;
    private QuestionTwoFragment questionTwoFragment;
    private QuestionThreeFragment questionThreeFragment;
    private QuestionFourFragment questionFourFragment;
    private QuestionFiveFragment questionFiveFragment;
    private QuestionSixFragment questionSixFragment;
    private QuestionSevenFragment questionSevenFragment;
    private QuestionEightFragment questionEightFragment;

    // เพิ่ม AssistScoreFragment
    private AssistScoreFragment assistScoreFragment;

    private QuestionsStateViewModel questionsStateViewModel;

    // Getters สำหรับ fragments
    public QuestionOneFragment getQuestionOneFragment() { return questionOneFragment; }
    public QuestionTwoFragment getQuestionTwoFragment() { return questionTwoFragment; }
    public QuestionThreeFragment getQuestionThreeFragment() { return questionThreeFragment; }
    public QuestionFourFragment getQuestionFourFragment() { return questionFourFragment; }
    public QuestionFiveFragment getQuestionFiveFragment() { return questionFiveFragment; }
    public QuestionSixFragment getQuestionSixFragment() { return questionSixFragment; }
    public QuestionSevenFragment getQuestionSevenFragment() { return questionSevenFragment; }
    public QuestionEightFragment getQuestionEightFragment() { return questionEightFragment; }

    // เพิ่ม getter สำหรับ AssistScoreFragment
    public AssistScoreFragment getAssistScoreFragment() { return assistScoreFragment; }
    private OnDataPass dataPasser;
    private SharedViewModel sharedViewModel;
    private String personId;
    private String visitNo;
    private OnFormDataSavedListener onFormDataSavedListener;

    public interface OnFormDataSavedListener {
        void onMainQuestionsDataSaved();
    }
    public MainQuestionsFragment() {
        // Required empty public constructor
    }

    public static MainQuestionsFragment newInstance(String param1, String param2) {
        MainQuestionsFragment fragment = new MainQuestionsFragment();
        return fragment;
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnDataPass) context;
            onFormDataSavedListener = (OnFormDataSavedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }
    private void notifyDataSaved() {
        if (onFormDataSavedListener != null) {
            onFormDataSavedListener.onMainQuestionsDataSaved();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        questionsStateViewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // สังเกตการเปลี่ยนแปลงของ Question 1 เพื่อกรองสารเสพติดที่แสดงในข้อ 2-7
        observeQuestionOneChanges();

        // สังเกตการเปลี่ยนแปลงของ Question 2 เพื่อซ่อน/แสดงข้อ 3, 4, 5
        observeQuestionTwoChanges();

        // คำนวดความสูงเริ่มต้นหลังจาก View พร้อม
        view.post(() -> {
            forceRecalculateHeight();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_questions, container, false);

        if (savedInstanceState == null) {
            initializeData();

            // เพิ่ม fragment ลงใน container (รวม AssistScoreFragment)
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.question_one_container, questionOneFragment)
                    .add(R.id.question_two_container, questionTwoFragment)
                    .add(R.id.question_three_container, questionThreeFragment)
                    .add(R.id.question_four_container, questionFourFragment)
                    .add(R.id.question_five_container, questionFiveFragment)
                    .add(R.id.question_six_container, questionSixFragment)
                    .add(R.id.question_seven_container, questionSevenFragment)
                    .add(R.id.question_eight_container, questionEightFragment)
                    .add(R.id.assist_summary_container, assistScoreFragment)
                    .commitNow();
        }
        return view;
    }

    private void initializeData() {
        // สร้าง template list หลักที่จะใช้เป็นต้นแบบ
        ArrayList<SubstanceItem> templateList = createSubstanceTemplateList();

        // สร้าง lists สำหรับแต่ละ fragment โดยใช้ template
        Map<String, ArrayList<SubstanceItem>> fragmentLists = new HashMap<>();
        String[] fragmentKeys = {"one", "two", "three", "four", "five", "six", "seven"};

        for (String key : fragmentKeys) {
            fragmentLists.put(key, copySubstanceList(templateList));
        }

        // ตั้งค่าเริ่มต้นให้ ViewModel
        initializeViewModel(templateList);

        // สร้างและตั้งค่า Fragments (รวม AssistScoreFragment)
        createAndSetupFragments(fragmentLists);
    }

    private ArrayList<SubstanceItem> createSubstanceTemplateList() {
        ArrayList<SubstanceItem> templateList = new ArrayList<>();
        templateList.add(new SubstanceItem("a", "a. ผลิตภัณฑ์ยาสูบ", "บุหรี่ ยาเส้นแบบเคี้ยว ซิการ์ ฯลฯ"));
        templateList.add(new SubstanceItem("b", "b. เครื่องดื่มแอลกอฮอล์", "สุรา เบียร์ ไวน์"));
        templateList.add(new SubstanceItem("c", "c. กัญชา", "กัญชาแห้ง ยางกัญชา น้ำกัญชา ฯลฯ"));
        templateList.add(new SubstanceItem("d", "d. โคเคน", "โค้ก แครัก ฯลฯ"));
        templateList.add(new SubstanceItem("e", "e. ยากระตุ้นประสาทกลุ่มแอมเฟตามีน", "ยาบ้า ยาอี ไอซ์ สปีด ยาลดความอ้วน ฯลฯ"));
        templateList.add(new SubstanceItem("f", "f. สารระเหย", "กาว ทินเนอร์ เบนซิน ไนตรัส ฯลฯ"));
        templateList.add(new SubstanceItem("g", "g. ยากล่อมประสาทหรือยานอนหลับ", "วาเลี่ยม โรฮิปนอล ดอมิกุม มาโน โซแลม ฯลฯ"));
        templateList.add(new SubstanceItem("h", "h. ยาหลอนประสาท", "แอลเอสดี แอซิด เห็ดเมา พีซีพี ยาเค ฯลฯ"));
        templateList.add(new SubstanceItem("i", "i. สารกลุ่มกิ่น", "ฝิ่น เฮโรอีน มอร์ฟีน เมทาโดน บูพรีนอฟีน โคเดอีน ฯลฯ"));
        templateList.add(new SubstanceItem("j", "j. สารเสพติดอื่น ๆ", ""));
        return templateList;
    }

    private ArrayList<SubstanceItem> copySubstanceList(ArrayList<SubstanceItem> templateList) {
        ArrayList<SubstanceItem> newList = new ArrayList<>();
        for (SubstanceItem item : templateList) {
            newList.add(new SubstanceItem(item.getId(), item.getName(), item.getDescription()));
        }
        return newList;
    }

    /**
     * สร้าง filtered substance list สำหรับข้อ 2-7 โดยแสดงเฉพาะที่เลือก "เคย" ในข้อ 1
     */
    private ArrayList<SubstanceItem> createFilteredSubstanceList(ArrayList<SubstanceItem> templateList) {
        ArrayList<SubstanceItem> filteredList = new ArrayList<>();

        if (questionOneFragment == null) {
            return copySubstanceList(templateList); // ถ้ายังไม่มีข้อมูลให้แสดงทั้งหมดก่อน
        }

        Map<String, AnswerData> questionOneAnswers = questionOneFragment.getSelectedAnswers();
        if (questionOneAnswers == null || questionOneAnswers.isEmpty()) {
            return copySubstanceList(templateList); // ถ้ายังไม่มีข้อมูลให้แสดงทั้งหมดก่อน
        }

        // กรองเฉพาะสารเสพติดที่เลือก "เคย" ในข้อ 1
        for (SubstanceItem item : templateList) {
            AnswerData answer = questionOneAnswers.get(item.getId());
            if (answer != null && answer.isHasUsed() != null && answer.isHasUsed()) {
                filteredList.add(new SubstanceItem(item.getId(), item.getName(), item.getDescription()));
            }
        }

        Log.d(TAG, "Filtered substance list size: " + filteredList.size());
        return filteredList;
    }

    private void createAndSetupFragments(Map<String, ArrayList<SubstanceItem>> fragmentLists) {
        // สร้าง fragments (รวม AssistScoreFragment)
        questionOneFragment = new QuestionOneFragment();
        questionTwoFragment = new QuestionTwoFragment();
        questionThreeFragment = new QuestionThreeFragment();
        questionFourFragment = new QuestionFourFragment();
        questionFiveFragment = new QuestionFiveFragment();
        questionSixFragment = new QuestionSixFragment();
        questionSevenFragment = new QuestionSevenFragment();
        questionEightFragment = new QuestionEightFragment();

        // สร้าง AssistScoreFragment
        assistScoreFragment = new AssistScoreFragment();

        // ตั้งค่า arguments สำหรับแต่ละ fragment
        questionOneFragment.setArguments(createBundle(fragmentLists.get("one")));

        // สำหรับข้อ 2-7 ให้ใช้ list เต็มก่อน แล้วจะ filter ภายหลังเมื่อมีการเปลี่ยนแปลงในข้อ 1
        questionTwoFragment.setArguments(createBundle(fragmentLists.get("two")));
        questionThreeFragment.setArguments(createBundle(fragmentLists.get("three")));
        questionFourFragment.setArguments(createBundle(fragmentLists.get("four")));

        // ตัดคำถาม a ออกจาก substanceList สำหรับ questionFiveFragment
        ArrayList<SubstanceItem> fiveList = fragmentLists.get("five");
        ArrayList<SubstanceItem> modifiedFiveList = new ArrayList<>();
        for (SubstanceItem item : fiveList) {
            if (!item.getId().equals("a")) {
                modifiedFiveList.add(item);
            }
        }
        questionFiveFragment.setArguments(createBundle(modifiedFiveList));

        questionSixFragment.setArguments(createBundle(fragmentLists.get("six")));
        questionSevenFragment.setArguments(createBundle(fragmentLists.get("seven")));
    }

    private Bundle createBundle(ArrayList<SubstanceItem> list) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("substanceList", list);
        return bundle;
    }

    private void initializeViewModel(ArrayList<SubstanceItem> templateList) {
        // สำหรับ Question One
        if (questionsStateViewModel.getQuestionOneAnswers().getValue() == null) {
            Map<String, AnswerData> initialOneAnswers = new HashMap<>();
            for (SubstanceItem item : templateList) {
                initialOneAnswers.put(item.getId(), new AnswerData(null, ""));
            }
            questionsStateViewModel.initQuestionOneAnswers(initialOneAnswers);
        }

        // สำหรับ Question Two และอื่นๆ
        Map<String, AnswerData> initialAnswers = new HashMap<>();
        Map<String, AnswerFrequencyData> initialFrequencyAnswers = new HashMap<>();
        for (SubstanceItem item : templateList) {
            initialAnswers.put(item.getId(), new AnswerData(null, ""));
        }

        if (questionsStateViewModel.getQuestionTwoAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionTwoAnswers(initialFrequencyAnswers);
        }
        if (questionsStateViewModel.getQuestionThreeAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionThreeAnswers(initialFrequencyAnswers);
        }
        if (questionsStateViewModel.getQuestionFourAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionFourAnswers(initialFrequencyAnswers);
        }
        if (questionsStateViewModel.getQuestionFiveAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionFiveAnswers(initialFrequencyAnswers);
        }
        if (questionsStateViewModel.getQuestionSixAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionSixAnswers(initialFrequencyAnswers);
        }
        if (questionsStateViewModel.getQuestionSevenAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionSevenAnswers(initialFrequencyAnswers);
        }
        if (questionsStateViewModel.getQuestionEightAnswers().getValue() == null) {
            questionsStateViewModel.initQuestionEightAnswers(initialFrequencyAnswers);
        }
    }


    /**
     * อัปเดตรายการสารเสพติดในข้อ 2-7 ตามการเลือกในข้อ 1
     */
    private void updateSubstanceListsForQuestions2To7() {
        ArrayList<SubstanceItem> templateList = createSubstanceTemplateList();
        ArrayList<SubstanceItem> filteredList = createFilteredSubstanceList(templateList);

        Log.d(TAG, "Updating substance lists based on Question 1 answers");
        Log.d(TAG, "Filtered substances count: " + filteredList.size());

        ArrayList<SubstanceItem> filteredListForQ2 = createFilteredListWithCurrentData(filteredList, "Q2");
        ArrayList<SubstanceItem> filteredListForQ3 = createFilteredListWithCurrentData(filteredList, "Q3");
        ArrayList<SubstanceItem> filteredListForQ4 = createFilteredListWithCurrentData(filteredList, "Q4");
//        ArrayList<SubstanceItem> filteredListForQ5 = createFilteredListWithCurrentData(filteredList, "Q5");
        ArrayList<SubstanceItem> filteredListForQ6 = createFilteredListWithCurrentData(filteredList, "Q6");
        ArrayList<SubstanceItem> filteredListForQ7 = createFilteredListWithCurrentData(filteredList, "Q7");


        // อัปเดต Fragment ข้อ 2-7 ด้วยรายการที่กรองแล้ว
        if (questionTwoFragment != null) {
            questionTwoFragment.updateSubstanceList(filteredListForQ2);
        }
        if (questionThreeFragment != null) {
            questionThreeFragment.updateSubstanceList(filteredListForQ3);
        }
        if (questionFourFragment != null) {
            questionFourFragment.updateSubstanceList(filteredListForQ4);
        }

        // สำหรับข้อ 5 ต้องตัด a ออก
        if (questionFiveFragment != null) {
            ArrayList<SubstanceItem> filteredListForQ5 = new ArrayList<>();
            for (SubstanceItem item : filteredList) {
                if (!item.getId().equals("a")) {
                    filteredListForQ5.add(item);
                }
            }
            questionFiveFragment.updateSubstanceList(filteredListForQ5);
        }

        if (questionSixFragment != null) {
            questionSixFragment.updateSubstanceList(filteredListForQ6);
        }
        if (questionSevenFragment != null) {
            questionSevenFragment.updateSubstanceList(filteredListForQ7);
        }

        // ล้างข้อมูลใน ViewModel สำหรับสารเสพติดที่ไม่ได้เลือกในข้อ 1
//        clearUnselectedSubstancesFromViewModel(filteredList);

//        forceRecalculateHeight();
    }
    private Map<String, AnswerFrequencyData> getCurrentDataByQuestionType(String questionType) {
        if (questionsStateViewModel == null) {
            return null;
        }

        switch (questionType) {
            case "Q2":
                return questionsStateViewModel.getQuestionTwoAnswers().getValue();
            case "Q3":
                return questionsStateViewModel.getQuestionThreeAnswers().getValue();
            case "Q4":
                return questionsStateViewModel.getQuestionFourAnswers().getValue();
            case "Q5":
                return questionsStateViewModel.getQuestionFiveAnswers().getValue();
            case "Q6":
                return questionsStateViewModel.getQuestionSixAnswers().getValue();
            case "Q7":
                return questionsStateViewModel.getQuestionSevenAnswers().getValue();
            default:
                Log.w(TAG, "Unknown question type: " + questionType);
                return null;
        }
    }
    private ArrayList<SubstanceItem> createFilteredListWithCurrentData(ArrayList<SubstanceItem> baseFilteredList, String questionType) {
        ArrayList<SubstanceItem> result = new ArrayList<>();

        // ดึงข้อมูลปัจจุบันจาก ViewModel ตาม Question Type
        Map<String, AnswerFrequencyData> currentData = getCurrentDataByQuestionType(questionType);

        for (SubstanceItem baseItem : baseFilteredList) {
            // สร้าง SubstanceItem ใหม่โดยใช้ข้อมูลจาก base
            SubstanceItem newItem = new SubstanceItem(
                    baseItem.getId(),
                    baseItem.getName(),
                    baseItem.getDescription()
            );

            // ใส่ข้อมูลปัจจุบันจาก ViewModel (ถ้ามี)
            if (currentData != null && currentData.containsKey(baseItem.getId())) {
                AnswerFrequencyData data = currentData.get(baseItem.getId());
                if (data != null) {
                    newItem.setFrequency(data.getFrequency());
                    newItem.setOtherDrugs(data.getOtherDrugs());

                    Log.d(TAG, questionType + " - Preserved data for " + baseItem.getId() +
                            ": frequency=" + data.getFrequency() +
                            ", otherDrugs=" + data.getOtherDrugs());
                }
            } else {
                // ใช้ค่าเริ่มต้น
                newItem.setFrequency(-1);
                newItem.setOtherDrugs("");
            }

            result.add(newItem);
        }

        Log.d(TAG, questionType + " filtered list created with " + result.size() + " items");
        return result;
    }
    /**
     * ล้างข้อมูลใน ViewModel สำหรับสารเสพติดที่ไม่ได้เลือกในข้อ 1
     */
    private void clearUnselectedSubstancesFromViewModel(ArrayList<SubstanceItem> selectedSubstances) {
        // สร้าง Set ของ substance IDs ที่ถูกเลือก
        Map<String, Boolean> selectedIds = new HashMap<>();
        for (SubstanceItem item : selectedSubstances) {
            selectedIds.put(item.getId(), true);
        }

        // ล้างข้อมูลใน ViewModel สำหรับสารที่ไม่ได้เลือก
        String[] allSubstanceIds = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};

        for (String substanceId : allSubstanceIds) {
            if (!selectedIds.containsKey(substanceId)) {
                // ล้างข้อมูลสำหรับสารเสพติดที่ไม่ได้เลือก
                clearSubstanceFromAllQuestions(substanceId);
            }
        }
    }

    /**
     * ล้างข้อมูลของสารเสพติดที่ระบุออกจากคำถามทั้งหมด (ข้อ 2-8)
     */
    private void clearSubstanceFromAllQuestions(String substanceId) {
        Log.d(TAG, "Clearing substance " + substanceId + " from all questions");

        // ล้างจาก ViewModel
        Map<String, AnswerFrequencyData> emptyAnswer = new HashMap<>();
        emptyAnswer.put(substanceId, new AnswerFrequencyData(-1, ""));

        // อัปเดต ViewModel โดยเก็บเฉพาะสารที่ไม่ต้องล้าง
        updateViewModelWithoutSubstance(substanceId);

        // ล้างจาก Fragments ถ้ามี method สำหรับล้างข้อมูลเฉพาะสาร
        if (questionTwoFragment != null) {
            questionTwoFragment.clearAnswerForSubstance(substanceId);
        }
        if (questionThreeFragment != null) {
            // questionThreeFragment.clearAnswerForSubstance(substanceId);
        }
        if (questionFourFragment != null) {
            // questionFourFragment.clearAnswerForSubstance(substanceId);
        }
        if (questionFiveFragment != null) {
            // questionFiveFragment.clearAnswerForSubstance(substanceId);
        }
        if (questionSixFragment != null) {
            // questionSixFragment.clearAnswerForSubstance(substanceId);
        }
        if (questionSevenFragment != null) {
            // questionSevenFragment.clearAnswerForSubstance(substanceId);
        }
        if (questionEightFragment != null) {
            // questionEightFragment.clearAnswerForSubstance(substanceId);
        }
    }

    /**
     * อัปเดต ViewModel โดยลบข้อมูลของสารเสพติดที่ระบุออก
     */
    private void updateViewModelWithoutSubstance(String substanceIdToRemove) {
        // สำหรับข้อ 2-7
        Map<String, AnswerFrequencyData> currentQ2 = questionsStateViewModel.getQuestionTwoAnswers().getValue();
        Map<String, AnswerFrequencyData> currentQ3 = questionsStateViewModel.getQuestionThreeAnswers().getValue();
        Map<String, AnswerFrequencyData> currentQ4 = questionsStateViewModel.getQuestionFourAnswers().getValue();
        Map<String, AnswerFrequencyData> currentQ5 = questionsStateViewModel.getQuestionFiveAnswers().getValue();
        Map<String, AnswerFrequencyData> currentQ6 = questionsStateViewModel.getQuestionSixAnswers().getValue();
        Map<String, AnswerFrequencyData> currentQ7 = questionsStateViewModel.getQuestionSevenAnswers().getValue();

        if (currentQ2 != null) {
            currentQ2.put(substanceIdToRemove, new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionTwoAnswers(currentQ2);
        }
        if (currentQ3 != null) {
            currentQ3.put(substanceIdToRemove, new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionThreeAnswers(currentQ3);
        }
        if (currentQ4 != null) {
            Log.d("MainQuestionsFragment", "Q4 BEFORE clear - " + substanceIdToRemove + ": " +
                    (currentQ4.get(substanceIdToRemove) != null ? currentQ4.get(substanceIdToRemove).getFrequency() : "null"));
            currentQ4.put(substanceIdToRemove, new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionFourAnswers(currentQ4);
            Log.d("MainQuestionsFragment", "Q4 AFTER clear - " + substanceIdToRemove + ": -1");
        }
        if (currentQ5 != null) {
            currentQ5.put(substanceIdToRemove, new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionFiveAnswers(currentQ5);
        }
        if (currentQ6 != null) {
            currentQ6.put(substanceIdToRemove, new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionSixAnswers(currentQ6);
        }
        if (currentQ7 != null) {
            Log.d("MainQuestionsFragment", "Q7 BEFORE clear - " + substanceIdToRemove + ": " +
                    (currentQ7.get(substanceIdToRemove) != null ? currentQ7.get(substanceIdToRemove).getFrequency() : "null"));

            currentQ7.put(substanceIdToRemove, new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionSevenAnswers(currentQ7);
            Log.d("MainQuestionsFragment", "Q7 AFTER clear - " + substanceIdToRemove + ": -1");
        }
    }

    /**
     * สังเกตการเปลี่ยนแปลงของ Question 2 เพื่อซ่อน/แสดงข้อ 3, 4, 5
     */
    private void observeQuestionTwoChanges() {
        if (questionsStateViewModel != null) {
            questionsStateViewModel.getQuestionTwoAnswers().observe(getViewLifecycleOwner(), answers -> {
                if (answers != null) {
                    checkAndToggleQuestions345Visibility();
                }
            });
        }
    }

    public void clearQuestionTwoAnswerForSubstance(String substanceId) {
        Log.d("MainQuestionsFragment", "Clearing Q2 answer for substance: " + substanceId);

        // ค้นหา QuestionTwoFragment และล้างคำตอบของสารเสพติดที่ระบุ
        if (questionTwoFragment != null) {
            questionTwoFragment.clearAnswerForSubstance(substanceId);
        }

        // ล้างคำตอบในคำถาม 3-8 สำหรับสารเสพติดนั้นด้วย (ถ้ามี)
        clearSubsequentQuestionsForSubstance(substanceId);
    }

    private void clearSubsequentQuestionsForSubstance(String substanceId) {
        try {
            // ล้างคำตอบในคำถามที่ 3-8 ตามสารเสพติดที่ระบุ

            // คำถามที่ 3 - ล้างคำตอบของสารเสพติดที่ระบุ
            if (questionThreeFragment != null) {
                // สมมติว่า QuestionThreeFragment มีเมธอด clearAnswerForSubstance
                // questionThreeFragment.clearAnswerForSubstance(substanceId);
            }

            // คำถามที่ 4 - ล้างคำตอบของสารเสพติดที่ระบุ
            if (questionFourFragment != null) {
                // questionFourFragment.clearAnswerForSubstance(substanceId);
            }

            // คำถามที่ 5 - ล้างคำตอบของสารเสพติดที่ระบุ
            if (questionFiveFragment != null) {
                // questionFiveFragment.clearAnswerForSubstance(substanceId);
            }

            // คำถามที่ 6 - ล้างคำตอบของสารเสพติดที่ระบุ
            if (questionSixFragment != null) {
                // questionSixFragment.clearAnswerForSubstance(substanceId);
            }

            // คำถามที่ 7 - ล้างคำตอบของสารเสพติดที่ระบุ
            if (questionSevenFragment != null) {
                // questionSevenFragment.clearAnswerForSubstance(substanceId);
            }

            // คำถามที่ 8 - ล้างคำตอบของสารเสพติดที่ระบุ
            if (questionEightFragment != null) {
                // questionEightFragment.clearAnswerForSubstance(substanceId);
            }

            Log.d("MainQuestionsFragment", "Cleared subsequent questions for substance: " + substanceId);

        } catch (Exception e) {
            Log.e("MainQuestionsFragment", "Error clearing subsequent questions for substance " + substanceId + ": " + e.getMessage());
        }
    }

    private void clearAllSubsequentQuestions() {
        try {
            // ล้างคำตอบในคำถามที่ 3
            if (questionThreeFragment != null) {
                // สมมติว่า QuestionThreeFragment มีเมธอด clearAllData
                // questionThreeFragment.clearAllData();
            }

            // ล้างคำตอบในคำถามที่ 4
            if (questionFourFragment != null) {
                // questionFourFragment.clearAllData();
            }

            // ล้างคำตอบในคำถามที่ 5
            if (questionFiveFragment != null) {
                // questionFiveFragment.clearAllData();
            }

            // ล้างคำตอบในคำถามที่ 6
            if (questionSixFragment != null) {
                // questionSixFragment.clearAllData();
            }

            // ล้างคำตอบในคำถามที่ 7
            if (questionSevenFragment != null) {
                // questionSevenFragment.clearAllData();
            }

            // ล้างคำตอบในคำถามที่ 8
            if (questionEightFragment != null) {
                // questionEightFragment.clearAllData();
            }

            Log.d("MainQuestionsFragment", "Cleared all subsequent questions");

        } catch (Exception e) {
            Log.e("MainQuestionsFragment", "Error clearing all subsequent questions: " + e.getMessage());
        }
    }

    /**
     * ตรวจสอบและแสดง/ซ่อนข้อ 3, 4, 5 ตามการเลือกใน Question 2
     */
    private void checkAndToggleQuestions345Visibility() {
        boolean shouldHideQuestions345 = isAllQuestionTwoAnswersNever();

        if (shouldHideQuestions345) {
            hideQuestions345();
//            clearQuestions345Data();
        } else {
            showQuestions345();
        }

        Log.d(TAG, "Questions 3,4,5 visibility: " + (shouldHideQuestions345 ? "HIDDEN" : "VISIBLE"));
    }

    /**
     * ตรวจสอบว่าข้อ 2 เลือก "ไม่เคย" (frequency = 0) ทั้งหมดหรือไม่
     */
    private boolean isAllQuestionTwoAnswersNever() {
        Map<String, AnswerFrequencyData> answers = questionsStateViewModel.getQuestionTwoAnswers().getValue();

        if (answers == null || answers.isEmpty()) {
            Log.d(TAG, "Q2 answers is null or empty - returning false");
            return false;
        }

        // ตรวจสอบว่าทุกรายการในข้อ 2 มีการเลือกแล้วหรือไม่
        for (Map.Entry<String, AnswerFrequencyData> entry : answers.entrySet()) {
            AnswerFrequencyData data = entry.getValue();

            // ถ้ายังไม่มีการเลือก (frequency = -1) ให้ return false
            if (data == null || data.getFrequency() == -1) {
                Log.d(TAG, "Q2 substance " + entry.getKey() + " not answered yet - returning false");
                return false;
            }

            // ถ้ามีการเลือกที่ไม่ใช่ "ไม่เคย" (frequency != 0) ให้ return false
            if (data.getFrequency() != 0) {
                Log.d(TAG, "Q2 substance " + entry.getKey() + " frequency: " + data.getFrequency() + " (not Never) - returning false");
                return false;
            }
        }

        Log.d(TAG, "All Q2 answers are 'Never' (frequency = 0) - returning true");
        return true; // ทุกรายการเลือก "ไม่เคย" (frequency = 0)
    }

    /**
     * ซ่อนข้อ 3, 4, 5
     */
    private void hideQuestions345() {
        View containerView = getView();
        if (containerView != null) {
            View question3Container = containerView.findViewById(R.id.question_three_container);
            View question4Container = containerView.findViewById(R.id.question_four_container);
            View question5Container = containerView.findViewById(R.id.question_five_container);

            if (question3Container != null) {
                question3Container.setVisibility(View.GONE);
            }
            if (question4Container != null) {
                question4Container.setVisibility(View.GONE);
            }
            if (question5Container != null) {
                question5Container.setVisibility(View.GONE);
            }

            forceRecalculateHeight();
            Log.d(TAG, "ซ่อนข้อ 3, 4, 5 แล้ว");
        }
    }

    /**
     * แสดงข้อ 3, 4, 5
     */
    private void showQuestions345() {
        View containerView = getView();
        if (containerView != null) {
            View question3Container = containerView.findViewById(R.id.question_three_container);
            View question4Container = containerView.findViewById(R.id.question_four_container);
            View question5Container = containerView.findViewById(R.id.question_five_container);

            if (question3Container != null) {
                question3Container.setVisibility(View.VISIBLE);
            }
            if (question4Container != null) {
                question4Container.setVisibility(View.VISIBLE);
            }
            if (question5Container != null) {
                question5Container.setVisibility(View.VISIBLE);
            }

            forceRecalculateHeight();
            Log.d(TAG, "แสดงข้อ 3, 4, 5 แล้ว");
        }
    }

    /**
     * ล้างข้อมูลในข้อ 3, 4, 5 เมื่อซ่อน
     */
    private void clearQuestions345Data() {
        Log.d(TAG, "เริ่มล้างข้อมูลข้อ 3, 4, 5");

        // ล้างข้อมูลใน ViewModel
        clearQuestions345ViewModel();

        // ล้างข้อมูลใน Fragments
        clearQuestions345Fragments();

        // ล้างข้อมูลใน Database
        clearQuestions345Database();

        Log.d(TAG, "ล้างข้อมูลข้อ 3, 4, 5 เสร็จสิ้น");
    }

    /**
     * ล้างข้อมูลใน ViewModel สำหรับข้อ 3, 4, 5
     */
    private void clearQuestions345ViewModel() {
        if (questionsStateViewModel != null) {
            Map<String, AnswerFrequencyData> emptyFrequencyData = new HashMap<>();

            // สร้างข้อมูลว่างสำหรับทุก substance
            String[] substanceIds = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
            for (String id : substanceIds) {
                emptyFrequencyData.put(id, new AnswerFrequencyData(-1, ""));
            }

            // ล้างข้อมูลข้อ 3, 4, 5
            questionsStateViewModel.setQuestionThreeAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionFourAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionFiveAnswers(new HashMap<>(emptyFrequencyData));

            Log.d(TAG, "ล้างข้อมูล ViewModel ข้อ 3, 4, 5 เสร็จสิ้น");
        }
    }

    /**
     * ล้างข้อมูลใน Fragments สำหรับข้อ 3, 4, 5
     */
    private void clearQuestions345Fragments() {
        if (questionThreeFragment != null) {
            questionThreeFragment.clearAllData();
        }
        if (questionFourFragment != null) {
            questionFourFragment.clearAllData();
        }
        if (questionFiveFragment != null) {
            questionFiveFragment.clearAllData();
        }

        Log.d(TAG, "ล้างข้อมูล Fragments ข้อ 3, 4, 5 เสร็จสิ้น");
    }

    /**
     * ล้างข้อมูลใน Database สำหรับข้อ 3, 4, 5
     */
    private void clearQuestions345Database() {
        try {
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            PersonInfoLiveData personInfoData = sharedViewModel.getPersonInfoLiveDataMutableLiveData().getValue();
            String personInfoId = personInfoData != null ? personInfoData.getId() : null;

            if (personInfoId != null) {
                // ล้างข้อมูลใน Database
                SfDrugsDao sfDrugsDao = new SfDrugsDao(requireContext());
                String[] questions = {"Q3", "Q4", "Q5"};
                sfDrugsDao.deleteDrugsByPersonIdAndQuestions(personInfoId, questions);

                Log.d(TAG, "ล้างข้อมูล Database ข้อ 3, 4, 5 เสร็จสิ้น");
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการล้างข้อมูล Database ข้อ 3, 4, 5: " + e.getMessage());
        }
    }

    /**
     * ตรวจสอบว่าข้อ 3, 4, 5 ถูกซ่อนอยู่หรือไม่
     */
    public boolean areQuestions345Hidden() {
        View containerView = getView();
        if (containerView != null) {
            View question3Container = containerView.findViewById(R.id.question_three_container);
            return question3Container != null && question3Container.getVisibility() == View.GONE;
        }
        return false;
    }

    /**
     * สังเกตการเปลี่ยนแปลงของ Question 1 เพื่อแสดง/ซ่อน AssistScoreFragment
     */
    private void observeQuestionOneChanges() {
        if (questionsStateViewModel != null) {
            questionsStateViewModel.getQuestionOneAnswers().observe(getViewLifecycleOwner(), answers -> {
                if (answers != null) {
//                    checkAndToggleAssistScoreVisibility();
                    updateSubstanceListsForQuestions2To7();
                }
            });
        }
    }

    /**
     * ตรวจสอบและแสดง/ซ่อน AssistScoreFragment ตามการเลือกใน Question 1
     */
    private void checkAndToggleAssistScoreVisibility() {
        boolean shouldShowAssistScore = shouldShowAssistScoreFragment();

        if (shouldShowAssistScore) {
            showAssistScoreFragment();
        } else {
            hideAssistScoreFragment();
        }

        Log.d(TAG, "AssistScore visibility: " + (shouldShowAssistScore ? "VISIBLE" : "HIDDEN"));
    }

    /**
     * ตรวจสอบว่าควรแสดง AssistScoreFragment หรือไม่
     */
    private boolean shouldShowAssistScoreFragment() {
        // แสดงเมื่อมีการใช้สารเสพติดอย่างน้อย 1 อย่าง และตอบคำถามครบถ้วน
        return hasAnySubstanceUsed() && isAllDataComplete();
    }

    /**
     * แสดง AssistScoreFragment
     */
    public void showAssistScoreFragment() {
        if (assistScoreFragment != null) {
            View containerView = getView();
            if (containerView != null) {
                View assistContainer = containerView.findViewById(R.id.assist_summary_container);
                if (assistContainer != null) {
                    assistContainer.setVisibility(View.VISIBLE);

                    // เรียกใช้เมธอดอัพเดตคะแนนใน AssistScoreFragment
                    if (assistScoreFragment.getView() != null) {
                        assistScoreFragment.refreshScores();
                    }
                    forceRecalculateHeight();
                    Log.d(TAG, "AssistScoreFragment แสดงแล้ว");
                }
            }
        }
    }

    /**
     * ซ่อน AssistScoreFragment
     */
    public void hideAssistScoreFragment() {
        if (assistScoreFragment != null) {
            View containerView = getView();
            if (containerView != null) {
                View assistContainer = containerView.findViewById(R.id.assist_summary_container);
                if (assistContainer != null) {
                    assistContainer.setVisibility(View.GONE);

                    // บังคับคำนวดความสูงใหม่เมื่อซ่อน AssistScoreFragment
                    forceRecalculateHeight();

                    Log.d(TAG, "AssistScoreFragment ซ่อนแล้ว");
                }
            }
        }
    }

    /**
     * บังคับรีเฟรช AssistScoreFragment
     */
    public void refreshAssistScoreFragment() {
        if (assistScoreFragment != null && assistScoreFragment.getView() != null) {
            assistScoreFragment.refreshScores();
//            checkAndToggleAssistScoreVisibility();
            Log.d(TAG, "AssistScoreFragment รีเฟรชแล้ว");
        }
    }

    /**
     * คำนวดความสูงทั้งหมดของ Fragment รวมถึง AssistScoreFragment
     */
    public int calculateTotalHeight() {
        int totalHeight = 0;
        View view = getView();

        if (view == null) return 2000; // ค่าเริ่มต้นที่ปลอดภัย

        // เพิ่มค่า padding ของตัว Fragment หลัก
        totalHeight += view.getPaddingTop() + view.getPaddingBottom();

        // หาความสูงของแต่ละ Fragment container (รวม assist_summary_container)
        int[] fragmentContainerIds = new int[] {
                R.id.question_one_container,
                R.id.question_two_container,
                R.id.question_three_container, // อาจถูกซ่อน
                R.id.question_four_container,  // อาจถูกซ่อน
                R.id.question_five_container,  // อาจถูกซ่อน
                R.id.question_six_container,
                R.id.question_seven_container,
                R.id.question_eight_container,
                R.id.assist_summary_container // เพิ่ม AssistScoreFragment container
        };

        for (int containerId : fragmentContainerIds) {
            View containerView = view.findViewById(containerId);
            if (containerView != null && containerView.getVisibility() == View.VISIBLE) {

                // คำนวดความสูงของ Fragment แต่ละตัว
                int fragmentHeight = calculateFragmentHeight(containerId, containerView);
                totalHeight += fragmentHeight;

                // เพิ่ม margin ระหว่าง containers
                ViewGroup.MarginLayoutParams params =
                        (ViewGroup.MarginLayoutParams) containerView.getLayoutParams();
                if (params != null) {
                    totalHeight += params.topMargin + params.bottomMargin;
                }

                Log.d(TAG, "Container " + getResourceName(containerId) + " height: " + fragmentHeight);
            } else {
                Log.d(TAG, "Container " + getResourceName(containerId) + " is hidden");
            }
        }

        // เพิ่มความสูงขั้นต่ำเพื่อป้องกันความผิดพลาด
        int minHeight = 2000;
        int finalHeight = Math.max(totalHeight, minHeight);

        Log.d(TAG, "Total calculated height: " + finalHeight);
        return finalHeight;
    }

    private int calculateFragmentHeight(int containerId, View containerView) {
        Fragment childFragment = getChildFragmentManager().findFragmentById(containerId);

        if (childFragment == null || childFragment.getView() == null) {
            // ถ้าไม่พบ Fragment ให้ใช้ความสูงพื้นฐาน
            return 200;
        }

        View fragmentView = childFragment.getView();

        // กรณีพิเศษสำหรับ AssistScoreFragment
        if (childFragment instanceof AssistScoreFragment) {
            return calculateAssistScoreFragmentHeight(fragmentView);
        }

        // กรณีทั่วไปสำหรับ Question Fragments
        return calculateQuestionFragmentHeight(childFragment, fragmentView);
    }

    private int calculateAssistScoreFragmentHeight(View fragmentView) {
        if (fragmentView.getVisibility() == View.GONE) {
            return 0;
        }

        fragmentView.measure(
                View.MeasureSpec.makeMeasureSpec(getView().getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        int height = fragmentView.getMeasuredHeight();
        Log.d(TAG, "AssistScoreFragment height: " + height);
        return Math.max(height, 300); // ความสูงขั้นต่ำ
    }

    private int calculateQuestionFragmentHeight(Fragment fragment, View fragmentView) {
        ViewGroup contentLayout = getContentLayoutFromFragment(fragment, fragmentView);
        View headerLayout = getHeaderLayoutFromFragment(fragment, fragmentView);

        int totalFragmentHeight = 0;

        // คำนวดความสูงของ Header (แสดงเสมอ)
        if (headerLayout != null) {
            headerLayout.measure(
                    View.MeasureSpec.makeMeasureSpec(getView().getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            totalFragmentHeight += headerLayout.getMeasuredHeight();
        }

        // คำนวดความสูงของ Content (ถ้าแสดงอยู่)
        if (contentLayout != null && contentLayout.getVisibility() == View.VISIBLE) {
            // Force measure content layout
            contentLayout.measure(
                    View.MeasureSpec.makeMeasureSpec(getView().getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );

            int contentHeight = contentLayout.getMeasuredHeight();
            totalFragmentHeight += contentHeight;

            Log.d(TAG, fragment.getClass().getSimpleName() + " - Content visible, height: " + contentHeight);
        } else {
            Log.d(TAG, fragment.getClass().getSimpleName() + " - Content collapsed");
        }

        // ความสูงขั้นต่ำสำหรับแต่ละ Fragment (header + padding)
        int minFragmentHeight = 100;
        return Math.max(totalFragmentHeight, minFragmentHeight);
    }

    private String getResourceName(int resourceId) {
        try {
            return getResources().getResourceEntryName(resourceId);
        } catch (Exception e) {
            return "unknown_" + resourceId;
        }
    }

    /**
     * บังคับให้คำนวดความสูงใหม่และรีเฟรช ViewPager
     */
    public void forceRecalculateHeight() {
        View view = getView();
        if (view != null) {
            // บังคับให้ทุก Fragment วัดขนาดใหม่
            view.post(() -> {
                forceRemeasureAllFragments();

                // แจ้ง Activity ให้ปรับขนาด ViewPager
                if (getActivity() instanceof PersonScreeningForm15Activity) {
                    new Handler().postDelayed(() -> {
                        // รีเฟรช ViewPager หลังจากวัดขนาดใหม่
                        if( ((PersonScreeningForm15Activity) getActivity())!= null) {
                            ((PersonScreeningForm15Activity) getActivity()).refreshViewPager();
                        }
                    }, 100);
                }
            });
        }
    }

    private void forceRemeasureAllFragments() {
        int[] containerIds = {
                R.id.question_one_container,
                R.id.question_two_container,
                R.id.question_three_container,
                R.id.question_four_container,
                R.id.question_five_container,
                R.id.question_six_container,
                R.id.question_seven_container,
                R.id.question_eight_container,
                R.id.assist_summary_container
        };

        for (int containerId : containerIds) {
            View container = getView().findViewById(containerId);
            if (container != null) {
                container.requestLayout();

                Fragment fragment = getChildFragmentManager().findFragmentById(containerId);
                if (fragment != null && fragment.getView() != null) {
                    fragment.getView().requestLayout();
                }
            }
        }
    }


    /**
     * Helper method เพื่อดึง content layout จาก fragment
     */
    private ViewGroup getContentLayoutFromFragment(Fragment fragment, View fragmentView) {
        if (fragment instanceof QuestionOneFragment) {
            return fragmentView.findViewById(R.id.contentLayoutOne);
        } else if (fragment instanceof QuestionTwoFragment) {
            return fragmentView.findViewById(R.id.contentLayoutTwo);
        } else if (fragment instanceof QuestionThreeFragment) {
            return fragmentView.findViewById(R.id.contentLayoutThree);
        } else if (fragment instanceof QuestionFourFragment) {
            return fragmentView.findViewById(R.id.contentLayoutFour);
        } else if (fragment instanceof QuestionFiveFragment) {
            return fragmentView.findViewById(R.id.contentLayoutFive);
        } else if (fragment instanceof QuestionSixFragment) {
            return fragmentView.findViewById(R.id.contentLayoutSix);
        } else if (fragment instanceof QuestionSevenFragment) {
            return fragmentView.findViewById(R.id.contentLayoutSeven);
        } else if (fragment instanceof QuestionEightFragment) {
            return fragmentView.findViewById(R.id.contentLayoutEight);
        }
        return null;
    }

    /**
     * Helper method เพื่อดึง header layout จาก fragment
     */
    private View getHeaderLayoutFromFragment(Fragment fragment, View fragmentView) {
        if (fragment instanceof QuestionOneFragment) {
            return fragmentView.findViewById(R.id.headerLayoutOne);
        } else if (fragment instanceof QuestionTwoFragment) {
            return fragmentView.findViewById(R.id.headerLayoutTwo);
        } else if (fragment instanceof QuestionThreeFragment) {
            return fragmentView.findViewById(R.id.headerLayoutThree);
        } else if (fragment instanceof QuestionFourFragment) {
            return fragmentView.findViewById(R.id.headerLayoutFour);
        } else if (fragment instanceof QuestionFiveFragment) {
            return fragmentView.findViewById(R.id.headerLayoutFive);
        } else if (fragment instanceof QuestionSixFragment) {
            return fragmentView.findViewById(R.id.headerLayoutSix);
        } else if (fragment instanceof QuestionSevenFragment) {
            return fragmentView.findViewById(R.id.headerLayoutSeven);
        } else if (fragment instanceof QuestionEightFragment) {
            return fragmentView.findViewById(R.id.headerLayoutEight);
        }
        return null;
    }

    public void notifyChildFragmentStateChanged() {
        Log.d(TAG, "Child fragment state changed - recalculating height");

        // บังคับให้ Fragment คำนวดขนาดใหม่
        forceRecalculateHeight();

        // ตรวจสอบการแสดง AssistScoreFragment
//        checkAndToggleAssistScoreVisibility();

        // ตรวจสอบการแสดง/ซ่อนข้อ 3, 4, 5
        checkAndToggleQuestions345Visibility();
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            new Handler().postDelayed(() -> {
                ((PersonScreeningForm15Activity) getActivity()).refreshViewPager();
            }, 200);
        }
    }

    public void clearAllQuestionsWhenNeverUsed() {
        if (isAllSubstancesNeverUsed()) {
            Log.d(TAG, "ตรวจพบการเลือก 'ไม่เคย' ทั้งหมด - ล้างข้อมูลข้อ 2-8");

            // ล้างข้อมูลใน ViewModel ทั้งหมด
            clearViewModelData();

            // ล้างข้อมูลใน Fragment แต่ละตัว
            clearAllFragmentsData();

            // ล้างข้อมูลใน Database
            clearDatabaseData();

            // รีเฟรช AssistScore
            if (assistScoreFragment != null) {
                assistScoreFragment.forceRefreshAllScores();
            }

            Log.d(TAG, "ล้างข้อมูลทั้งหมดเสร็จสิ้น");
        }
    }

    private void clearViewModelData() {
        if (questionsStateViewModel != null) {
            // สร้าง empty data สำหรับแต่ละข้อ
            Map<String, AnswerFrequencyData> emptyFrequencyData = new HashMap<>();

            // สร้างข้อมูลว่างสำหรับทุก substance
            String[] substanceIds = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
            for (String id : substanceIds) {
                emptyFrequencyData.put(id, new AnswerFrequencyData(-1, ""));
            }

            // ล้างข้อมูลทุกข้อ
            questionsStateViewModel.setQuestionTwoAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionThreeAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionFourAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionFiveAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionSixAnswers(new HashMap<>(emptyFrequencyData));
            questionsStateViewModel.setQuestionSevenAnswers(new HashMap<>(emptyFrequencyData));

            // ล้างข้อ 8 (injection)
            Map<String, AnswerFrequencyData> emptyInjectionData = new HashMap<>();
            emptyInjectionData.put("injection", new AnswerFrequencyData(-1, ""));
            questionsStateViewModel.setQuestionEightAnswers(emptyInjectionData);

            Log.d(TAG, "ล้างข้อมูล ViewModel เสร็จสิ้น");
        }
    }

    private void clearAllFragmentsData() {
        // ล้างข้อมูลใน Fragment แต่ละตัว
        if (questionTwoFragment != null) {
            questionTwoFragment.clearAllData();
        }
        if (questionThreeFragment != null) {
            questionThreeFragment.clearAllData();
        }
        if (questionFourFragment != null) {
            questionFourFragment.clearAllData();
        }
        if (questionFiveFragment != null) {
            questionFiveFragment.clearAllData();
        }
        if (questionSixFragment != null) {
            questionSixFragment.clearAllData();
        }
        if (questionSevenFragment != null) {
            questionSevenFragment.clearAllData();
        }
        if (questionEightFragment != null) {
            questionEightFragment.clearAllData();
        }

        Log.d(TAG, "ล้างข้อมูล Fragments เสร็จสิ้น");
    }

    private void clearDatabaseData() {
        try {
            SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
            PersonInfoLiveData personInfoData = sharedViewModel.getPersonInfoLiveDataMutableLiveData().getValue();
            String personInfoId = personInfoData != null ? personInfoData.getId() : null;

            if (personInfoId != null && dataPasser != null) {
                // สร้างข้อมูลว่างสำหรับส่งไปยัง Database
                List<DrugsInfo> emptyDrugsInfoList = createEmptyDrugsInfoList(personInfoId);

                // ส่งข้อมูลว่างไปยัง Database ผ่าน dataPasser
                dataPasser.onDrugsTwoInfo(emptyDrugsInfoList);
                dataPasser.onDrugsThreeInfo(emptyDrugsInfoList);
                dataPasser.onDrugsFourInfo(emptyDrugsInfoList);
                dataPasser.onDrugsFiveInfo(emptyDrugsInfoList);
                dataPasser.onDrugsSixInfo(emptyDrugsInfoList);
                dataPasser.onDrugsSevenInfo(emptyDrugsInfoList);
                dataPasser.onDrugsEightInfo(emptyDrugsInfoList);
                SfDrugsDao sfDrugsDao = new SfDrugsDao(requireContext());
                String[] questions = {"Q2", "Q3", "Q4", "Q5", "Q6", "Q7", "Q8"};
                sfDrugsDao.deleteDrugsByPersonIdAndQuestions(personInfoId, questions);

                Log.d(TAG, "ส่งข้อมูลว่างไปยัง Database เสร็จสิ้น");
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการล้างข้อมูล Database: " + e.getMessage());
        }
    }

    private List<DrugsInfo> createEmptyDrugsInfoList(String personInfoId) {
        List<DrugsInfo> emptyList = new ArrayList<>();
        String[] substanceIds = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "injection"};

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        PersonInfoLiveData personInfoData = sharedViewModel.getPersonInfoLiveDataMutableLiveData().getValue();

        for (String substanceId : substanceIds) {
            DrugsInfo emptyInfo = new DrugsInfo();
            emptyInfo.setPersonInfoId(personInfoId);
            emptyInfo.setIdcard(personInfoData != null ? personInfoData.getIdcard() : "");
            emptyInfo.setSubquestion(substanceId);
            emptyInfo.setAnswer("-1"); // ค่าว่าง
            emptyInfo.setOtherDrugs("");
            emptyInfo.setUpdatedBy("SYSTEM");
            emptyInfo.setUpdatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            emptyList.add(emptyInfo);
        }

        return emptyList;
    }

    // เมธอดเดิมทั้งหมด (แก้ไขเพื่อรองรับการซ่อน/แสดงข้อ 3, 4, 5)
    public String getDetailedValidationMessage() {
        List<String> allMessages = new ArrayList<>();

        // ตรวจสอบแต่ละ Fragment และรวบรวมข้อความ
        if (questionOneFragment != null) {
            String message = questionOneFragment.getValidationMessage();
            if (!message.isEmpty()) {
                allMessages.add(message);
            }
        }

        if (questionTwoFragment != null) {
            String message = questionTwoFragment.getValidationMessage();
            if (!message.isEmpty()) {
                allMessages.add(message);
            }
        }

        // ตรวจสอบข้อ 3, 4, 5 เฉพาะเมื่อไม่ถูกซ่อน
        if (!areQuestions345Hidden()) {
            if (questionThreeFragment != null) {
                String message = questionThreeFragment.getValidationMessage();
                if (!message.isEmpty()) {
                    allMessages.add(message);
                }
            }

            if (questionFourFragment != null) {
                String message = questionFourFragment.getValidationMessage();
                if (!message.isEmpty()) {
                    allMessages.add(message);
                }
            }

            if (questionFiveFragment != null) {
                String message = questionFiveFragment.getValidationMessage();
                if (!message.isEmpty()) {
                    allMessages.add(message);
                }
            }
        }

        if (questionSixFragment != null) {
            String message = questionSixFragment.getValidationMessage();
            if (!message.isEmpty()) {
                allMessages.add(message);
            }
        }

        if (questionSevenFragment != null) {
            String message = questionSevenFragment.getValidationMessage();
            if (!message.isEmpty()) {
                allMessages.add(message);
            }
        }

        if (questionEightFragment != null) {
            String message = questionEightFragment.getValidationMessage();
            if (!message.isEmpty()) {
                allMessages.add(message);
            }
        }

        if (allMessages.isEmpty()) {
            return "";
        }

        return "กรุณากรอกข้อมูลให้ครบถ้วน:\n\n" + String.join("\n\n", allMessages);
    }

    public boolean isAllDataComplete() {
        return getDetailedValidationMessage().isEmpty();
    }

    /**
     * ตรวจสอบว่าผู้ใช้เลือก "ไม่เคย" ใช้สารเสพติดทั้งหมดใน Question 1 หรือไม่
     */
    public boolean isAllSubstancesNeverUsed() {
        if (questionOneFragment == null) {
            return false;
        }

        Map<String, AnswerData> answers = questionOneFragment.getSelectedAnswers();
        if (answers == null || answers.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, AnswerData> entry : answers.entrySet()) {
            AnswerData answer = entry.getValue();

            if (answer == null || answer.isHasUsed() == null) {
                return false;
            }

            if (answer.isHasUsed()) {
                return false;
            }
        }

        return true;
    }

    /**
     * ตรวจสอบว่าผู้ใช้เลือก "เคย" ใช้สารเสพติดอย่างน้อย 1 อย่างใน Question 1 หรือไม่
     */
    public boolean hasAnySubstanceUsed() {
        if (questionOneFragment == null) {
            return false;
        }

        Map<String, AnswerData> answers = questionOneFragment.getSelectedAnswers();
        if (answers == null || answers.isEmpty()) {
            return false;
        }

        for (Map.Entry<String, AnswerData> entry : answers.entrySet()) {
            AnswerData answer = entry.getValue();

            if (answer != null && answer.isHasUsed() != null && answer.isHasUsed()) {
                return true;
            }
        }

        return false;
    }

    /**
     * ตรวจสอบความครบถ้วนของข้อมูลตามเงื่อนไขการใช้สารเสพติด
     */
    public boolean isDataCompleteBasedOnSubstanceUse() {
        if (questionOneFragment == null || !questionOneFragment.validateAllQuestionsAnswered()) {
            return false;
        }

        if (isAllSubstancesNeverUsed()) {
            notifySubstanceUseChanges();
            return true;
        }

        if (hasAnySubstanceUsed()) {
            return isAllDataComplete();
        }

        return false;
    }

    private void notifySubstanceUseChanges() {
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            PersonScreeningForm15Activity activity = (PersonScreeningForm15Activity) getActivity();

            Map<String, AnswerData> answers = questionOneFragment.getSelectedAnswers();
            if (answers != null) {
                boolean currentTobaccoUse = false;
                boolean currentAlcoholUse = false;

                AnswerData tobaccoAnswer = answers.get("a");
                AnswerData alcoholAnswer = answers.get("b");

                if (tobaccoAnswer != null && tobaccoAnswer.isHasUsed() != null) {
                    currentTobaccoUse = tobaccoAnswer.isHasUsed();
                }

                if (alcoholAnswer != null && alcoholAnswer.isHasUsed() != null) {
                    currentAlcoholUse = alcoholAnswer.isHasUsed();
                }

                activity.handleSubstanceUseChange(currentTobaccoUse, currentAlcoholUse);
            }
        }
    }

    public String getValidationMessageBasedOnSubstanceUse() {
        if (questionOneFragment == null || !questionOneFragment.validateAllQuestionsAnswered()) {
            return questionOneFragment != null ? questionOneFragment.getValidationMessage() :
                    "กรุณาตอบคำถามที่ 1 ให้ครบถ้วน";
        }

        if (isAllSubstancesNeverUsed()) {
            StringBuilder message = new StringBuilder();
            message.append("✅ ข้อมูลครบถ้วนแล้ว\n\n");

            Map<String, AnswerData> answers = questionOneFragment.getSelectedAnswers();
            boolean showTobaccoNote = false;
            boolean showAlcoholNote = false;

            if (answers != null) {
                AnswerData tobaccoAnswer = answers.get("a");
                AnswerData alcoholAnswer = answers.get("b");

                if (tobaccoAnswer != null && !tobaccoAnswer.isHasUsed()) {
                    showTobaccoNote = true;
                }

                if (alcoholAnswer != null && !alcoholAnswer.isHasUsed()) {
                    showAlcoholNote = true;
                }
            }

            if (showTobaccoNote || showAlcoholNote) {
                message.append("📝 หมายเหตุ:\n");

                if (showTobaccoNote) {
                    message.append("• ไม่จำเป็นต้องทำแบบประเมินเกี่ยวกับการสูบบุหรี่\n");
                }

                if (showAlcoholNote) {
                    message.append("• ไม่จำเป็นต้องทำแบบประเมินเกี่ยวกับการดื่มสุรา\n");
                }
            }

            return message.toString();
        }

        if (hasAnySubstanceUsed()) {
            StringBuilder message = new StringBuilder();
            message.append("เนื่องจากท่านเลือก \"เคย\" ใช้สารเสพติดอย่างน้อย 1 อย่าง\n");
            message.append("กรุณาทำแบบประเมินเพิ่มเติมดังนี้:\n\n");

            Map<String, AnswerData> answers = questionOneFragment.getSelectedAnswers();
            if (answers != null) {
                AnswerData tobaccoAnswer = answers.get("a");
                AnswerData alcoholAnswer = answers.get("b");

                if (tobaccoAnswer != null && tobaccoAnswer.isHasUsed()) {
                    message.append("🚬 แบบประเมินเกี่ยวกับการสูบบุหรี่:\n");
                    message.append("   • คัดกรองความเสี่ยงจากการสูบบุหรี่\n");
                    message.append("   • แบบทดสอบการติดบุหรี่\n\n");
                }

                if (alcoholAnswer != null && alcoholAnswer.isHasUsed()) {
                    message.append("🍺 แบบประเมินเกี่ยวกับการดื่มสุรา:\n");
                    message.append("   • คัดกรองความเสี่ยงจากการดื่มสุรา\n\n");
                }
            }

            String detailedMessage = getDetailedValidationMessage();
            if (!detailedMessage.isEmpty()) {
                message.append(detailedMessage);
            } else {
                // เพิ่มข้อความเกี่ยวกับการข้ามข้อ 3, 4, 5
                if (areQuestions345Hidden()) {
                    message.append("📌 หมายเหตุ: เนื่องจากข้อ 2 เลือก \"ไม่เคย\" ทั้งหมด จึงข้ามข้อ 3, 4, 5 ไปข้อ 6\n");
                }
            }

            return message.toString();
        }

        return "";
    }

    public SubstanceChangeInfo getSubstanceChangeInfo() {
        if (questionOneFragment == null) {
            return new SubstanceChangeInfo(false, false, false, false);
        }

        Map<String, AnswerData> answers = questionOneFragment.getSelectedAnswers();
        if (answers == null) {
            return new SubstanceChangeInfo(false, false, false, false);
        }

        boolean currentTobaccoUse = false;
        boolean currentAlcoholUse = false;

        AnswerData tobaccoAnswer = answers.get("a");
        AnswerData alcoholAnswer = answers.get("b");

        if (tobaccoAnswer != null && tobaccoAnswer.isHasUsed() != null) {
            currentTobaccoUse = tobaccoAnswer.isHasUsed();
        }

        if (alcoholAnswer != null && alcoholAnswer.isHasUsed() != null) {
            currentAlcoholUse = alcoholAnswer.isHasUsed();
        }

        return new SubstanceChangeInfo(
                currentTobaccoUse,
                currentAlcoholUse,
                true,
                questionOneFragment.validateAllQuestionsAnswered()
        );
    }
    public void onQuestionTwoDataChanged() {
        Log.d(TAG, "Question 2 data changed - checking questions 3,4,5 visibility");

        // ตรวจสอบและซ่อน/แสดงคำถาม 3,4,5 ทันที
        checkAndToggleQuestions345Visibility();

        // เรียก method อื่นๆ ที่เกี่ยวข้อง
        onDataChanged();
    }

    /**
     * เมธอดสำหรับการจัดการ AssistScoreFragment เมื่อข้อมูลเปลี่ยนแปลง
     */
    public void onDataChanged() {
        // รีเฟรช AssistScoreFragment เมื่อข้อมูลเปลี่ยนแปลง
        refreshAssistScoreFragment();

        // แจ้งให้ parent activity ทราบ
        notifyChildFragmentStateChanged();

        notifyDataSaved();

        Log.d(TAG, "ข้อมูลเปลี่ยนแปลง - รีเฟรช AssistScoreFragment");
    }

    public void onFormSaveCompleted() {
        Log.d(TAG, "แบบฟอร์มบันทึกเสร็จสิ้น - แจ้ง Activity เพื่ออัพเดตเมนู");
        notifyDataSaved();
    }

    /**
     * ตรวจสอบสถานะการแสดง AssistScoreFragment
     */
    public boolean isAssistScoreFragmentVisible() {
        View containerView = getView();
        if (containerView != null) {
            View assistContainer = containerView.findViewById(R.id.assist_summary_container);
            return assistContainer != null && assistContainer.getVisibility() == View.VISIBLE;
        }
        return false;
    }

    /**
     * อัพเดตคะแนนใน AssistScoreFragment แบบบังคับ
     */
    public void forceUpdateAssistScores() {
        if (assistScoreFragment != null && assistScoreFragment.getView() != null) {
            assistScoreFragment.forceRefreshAllScores();
            Log.d(TAG, "บังคับอัพเดตคะแนนทั้งหมดใน AssistScoreFragment");
        }
    }

    /**
     * ดึงข้อมูลสรุปจาก AssistScoreFragment
     */
    public String getAssistScoreSummary() {
        if (assistScoreFragment != null && isAssistScoreFragmentVisible()) {
            return assistScoreFragment.getScoreSummary();
        }
        return "ไม่มีข้อมูลสรุปคะแนน";
    }

    public static class SubstanceChangeInfo {
        public final boolean hasTobaccoUse;
        public final boolean hasAlcoholUse;
        public final boolean hasValidData;
        public final boolean isComplete;

        public SubstanceChangeInfo(boolean hasTobaccoUse, boolean hasAlcoholUse,
                                   boolean hasValidData, boolean isComplete) {
            this.hasTobaccoUse = hasTobaccoUse;
            this.hasAlcoholUse = hasAlcoholUse;
            this.hasValidData = hasValidData;
            this.isComplete = isComplete;
        }
    }
}