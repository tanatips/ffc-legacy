package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainQuestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainQuestionsFragment extends Fragment {

    private ArrayList<SubstanceItem> substanceListOne;
    private ArrayList<SubstanceItem> substanceListTwo;
    private ArrayList<SubstanceItem> substanceListThree;
    private ArrayList<SubstanceItem> substanceListFour;
    private ArrayList<SubstanceItem> substanceListFive;
    private ArrayList<SubstanceItem> substanceListSix;
    private ArrayList<SubstanceItem> substanceListSeven;
    private QuestionOneFragment questionOneFragment;
    private QuestionTwoFragment questionTwoFragment;

    private QuestionThreeFragment questionThreeFragment;

    private QuestionFourFragment questionFourFragment;

    private QuestionFiveFragment questionFiveFragment;

    private QuestionSixFragment questionSixFragment;

    private QuestionsStateViewModel viewModel;

    public MainQuestionsFragment() {
        // Required empty public constructor
    }


    public static MainQuestionsFragment newInstance(String param1, String param2) {
        MainQuestionsFragment fragment = new MainQuestionsFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_questions, container, false);


        if (savedInstanceState == null) {
            // สร้าง fragment ของคำถามที่ 1 และ 2

            initializeData();

            // เพิ่ม fragment ลงใน container
            getChildFragmentManager()
                    .beginTransaction()
//                    .add(R.id.question_one_container, questionOneFragment)
//                    .add(R.id.question_two_container, questionTwoFragment)
//                    .add(R.id.question_three_container, questionThreeFragment)
                    .add(R.id.question_four_container, questionFourFragment)
//                    .add(R.id.question_five_container, questionFiveFragment)
//                    .add(R.id.question_six_container, questionSixFragment)
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

        // สร้างและตั้งค่า Fragments
        createAndSetupFragments(fragmentLists);
    }
    private ArrayList<SubstanceItem> createSubstanceTemplateList() {
        ArrayList<SubstanceItem> templateList = new ArrayList<>();
        templateList.add(new SubstanceItem("a", "ผลิตภัณฑ์ยาสูบ", "บุหรี่ ยาเส้นแบบเคี้ยว ซิการ์ ฯลฯ"));
        templateList.add(new SubstanceItem("b", "เครื่องดื่มแอลกอฮอล์", "สุรา เบียร์ ไวน์"));
        templateList.add(new SubstanceItem("c", "กัญชา", "กัญชาแห้ง ยางกัญชา น้ำกัญชา ฯลฯ"));
        templateList.add(new SubstanceItem("d", "โคเคน", "โค้ก แครัก ฯลฯ"));
        templateList.add(new SubstanceItem("e", "ยากระตุ้นประสาทกลุ่มแอมเฟตามีน", "ยาบ้า ยาอี ไอซ์ สปีด ยาลดความอ้วน ฯลฯ"));
        templateList.add(new SubstanceItem("f", "สารระเหย", "กาว ทินเนอร์ เบนซิน ไนตรัส ฯลฯ"));
        templateList.add(new SubstanceItem("g", "ยากล่อมประสาทหรือยานอนหลับ", "วาเลี่ยม โรฮิปนอล ดอมิกุม มาโน โซแลม ฯลฯ"));
        templateList.add(new SubstanceItem("h", "ยาหลอนประสาท", "แอลเอสดี แอซิด เห็ดเมา พีซีพี ยาเค ฯลฯ"));
        templateList.add(new SubstanceItem("i", "สารกลุ่มฝิ่น", "ฝิ่น เฮโรอีน มอร์ฟีน เมทาโดน บูพรีนอฟีน โคเดอีน ฯลฯ"));
        templateList.add(new SubstanceItem("j", "สารเสพติดอื่น ๆ", ""));
        return templateList;
    }
    private ArrayList<SubstanceItem> copySubstanceList(ArrayList<SubstanceItem> templateList) {
        ArrayList<SubstanceItem> newList = new ArrayList<>();
        for (SubstanceItem item : templateList) {
            newList.add(new SubstanceItem(item.getId(), item.getName(), item.getDescription()));
        }
        return newList;
    }
    private void createAndSetupFragments(Map<String, ArrayList<SubstanceItem>> fragmentLists) {
        // สร้าง fragments
        questionOneFragment = new QuestionOneFragment();
        questionTwoFragment = new QuestionTwoFragment();
        questionThreeFragment = new QuestionThreeFragment();
        questionFourFragment = new QuestionFourFragment();
        questionFiveFragment = new QuestionFiveFragment();
        questionSixFragment = new QuestionSixFragment();

        // ตั้งค่า arguments สำหรับแต่ละ fragment
        questionOneFragment.setArguments(createBundle(fragmentLists.get("one")));
        questionTwoFragment.setArguments(createBundle(fragmentLists.get("two")));
        questionThreeFragment.setArguments(createBundle(fragmentLists.get("three")));
        questionFourFragment.setArguments(createBundle(fragmentLists.get("four")));
        questionFiveFragment.setArguments(createBundle(fragmentLists.get("five")));
        questionSixFragment.setArguments(createBundle(fragmentLists.get("six")));
    }
    private Bundle createBundle(ArrayList<SubstanceItem> list) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("substanceList", list);
        return bundle;
    }
    private void initializeViewModel(ArrayList<SubstanceItem> templateList) {

        // สำหรับ Question One
        if (viewModel.getQuestionOneAnswers().getValue() == null) {
            Map<String, QuestionOneFragment.AnswerData> initialOneAnswers = new HashMap<>();
            for (SubstanceItem item : templateList) {
                initialOneAnswers.put(item.getId(), new QuestionOneFragment.AnswerData(false, ""));
            }
            viewModel.initQuestionOneAnswers(initialOneAnswers);
        }

        // สำหรับ Question Two และ Three (คงเดิม)
        Map<String, Integer> initialAnswers = new HashMap<>();
        for (SubstanceItem item : templateList) {
            initialAnswers.put(item.getId(), 0);
        }

        if (viewModel.getQuestionTwoAnswers().getValue() == null) {
            viewModel.initQuestionTwoAnswers(initialAnswers);
        }
        if (viewModel.getQuestionThreeAnswers().getValue() == null) {
            viewModel.initQuestionThreeAnswers(initialAnswers);
        }
    }
    private boolean isAllQuestionsAnswered(Map<String, Boolean> answers) {
        return answers != null && answers.size() == 10; // a ถึง j
    }

    private boolean hasAnySubstanceUse(Map<String, Boolean> answers) {
        return answers.containsValue(true);
    }


}
