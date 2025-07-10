package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.DrugsLiveData;
import th.in.ffc.app.form.screening.model.AnswerData;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.person.PersonScreeningForm15Activity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainQuestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainQuestionsFragment extends Fragment {

//    private ArrayList<SubstanceItem> substanceListOne;
//    private ArrayList<SubstanceItem> substanceListTwo;
//    private ArrayList<SubstanceItem> substanceListThree;
//    private ArrayList<SubstanceItem> substanceListFour;
//    private ArrayList<SubstanceItem> substanceListFive;
//    private ArrayList<SubstanceItem> substanceListSix;
//    private ArrayList<SubstanceItem> substanceListSeven;
    private DrugsLiveData drugsLiveData;
//    private SharedViewModel viewModel;
    private QuestionOneFragment questionOneFragment;
    private QuestionTwoFragment questionTwoFragment;

    private QuestionThreeFragment questionThreeFragment;

    private QuestionFourFragment questionFourFragment;

    private QuestionFiveFragment questionFiveFragment;

    private QuestionSixFragment questionSixFragment;
    private QuestionSevenFragment questionSevenFragment;

    public QuestionOneFragment getQuestionOneFragment() {
        return questionOneFragment;
    }

    public QuestionTwoFragment getQuestionTwoFragment() {
        return questionTwoFragment;
    }

    public QuestionThreeFragment getQuestionThreeFragment() {
        return questionThreeFragment;
    }

    public QuestionFourFragment getQuestionFourFragment() {
        return questionFourFragment;
    }

    public QuestionFiveFragment getQuestionFiveFragment() {
        return questionFiveFragment;
    }

    public QuestionSixFragment getQuestionSixFragment() {
        return questionSixFragment;
    }

    public QuestionSevenFragment getQuestionSevenFragment() {
        return questionSevenFragment;
    }

    public QuestionEightFragment getQuestionEightFragment() {
        return questionEightFragment;
    }

    private QuestionEightFragment questionEightFragment;

    private QuestionsStateViewModel questionsStateViewModel;

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
//        questionsStateViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        questionsStateViewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
                    .add(R.id.question_one_container, questionOneFragment)
                    .add(R.id.question_two_container, questionTwoFragment)
                    .add(R.id.question_three_container, questionThreeFragment)
                    .add(R.id.question_four_container, questionFourFragment)
                    .add(R.id.question_five_container, questionFiveFragment)
                    .add(R.id.question_six_container, questionSixFragment)
                    .add(R.id.question_seven_container, questionSevenFragment)
                    .add(R.id.question_eight_container, questionEightFragment)
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
        templateList.add(new SubstanceItem("a", "a. ผลิตภัณฑ์ยาสูบ", "บุหรี่ ยาเส้นแบบเคี้ยว ซิการ์ ฯลฯ"));
        templateList.add(new SubstanceItem("b", "b. เครื่องดื่มแอลกอฮอล์", "สุรา เบียร์ ไวน์"));
        templateList.add(new SubstanceItem("c", "c. กัญชา", "กัญชาแห้ง ยางกัญชา น้ำกัญชา ฯลฯ"));
        templateList.add(new SubstanceItem("d", "d. โคเคน", "โค้ก แครัก ฯลฯ"));
        templateList.add(new SubstanceItem("e", "e. ยากระตุ้นประสาทกลุ่มแอมเฟตามีน", "ยาบ้า ยาอี ไอซ์ สปีด ยาลดความอ้วน ฯลฯ"));
        templateList.add(new SubstanceItem("f", "f. สารระเหย", "กาว ทินเนอร์ เบนซิน ไนตรัส ฯลฯ"));
        templateList.add(new SubstanceItem("g", "g. ยากล่อมประสาทหรือยานอนหลับ", "วาเลี่ยม โรฮิปนอล ดอมิกุม มาโน โซแลม ฯลฯ"));
        templateList.add(new SubstanceItem("h", "h. ยาหลอนประสาท", "แอลเอสดี แอซิด เห็ดเมา พีซีพี ยาเค ฯลฯ"));
        templateList.add(new SubstanceItem("i", "i. สารกลุ่มฝิ่น", "ฝิ่น เฮโรอีน มอร์ฟีน เมทาโดน บูพรีนอฟีน โคเดอีน ฯลฯ"));
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
    private void createAndSetupFragments(Map<String, ArrayList<SubstanceItem>> fragmentLists) {
        // สร้าง fragments
        questionOneFragment = new QuestionOneFragment();
        questionTwoFragment = new QuestionTwoFragment();
        questionThreeFragment = new QuestionThreeFragment();
        questionFourFragment = new QuestionFourFragment();
        questionFiveFragment = new QuestionFiveFragment();
        questionSixFragment = new QuestionSixFragment();
        questionSevenFragment = new QuestionSevenFragment();
        questionEightFragment = new QuestionEightFragment();

        // ตั้งค่า arguments สำหรับแต่ละ fragment
        questionOneFragment.setArguments(createBundle(fragmentLists.get("one")));
        questionTwoFragment.setArguments(createBundle(fragmentLists.get("two")));
        questionThreeFragment.setArguments(createBundle(fragmentLists.get("three")));
        questionFourFragment.setArguments(createBundle(fragmentLists.get("four")));
        // ตัดคำถาม a ออกจาก substanceList สำหรับ questionFiveFragment
        ArrayList<SubstanceItem> fiveList = fragmentLists.get("five");
        // สร้าง list ใหม่ที่ไม่มีคำถาม a
        ArrayList<SubstanceItem> modifiedFiveList = new ArrayList<>();
        for (SubstanceItem item : fiveList) {
            if (!item.getId().equals("a")) {
                modifiedFiveList.add(item);
            }
        }
        questionFiveFragment.setArguments(createBundle(modifiedFiveList));

//        questionFiveFragment.setArguments(createBundle(fragmentLists.get("five")));
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

        // สำหรับ Question Two และ Three (คงเดิม)
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
    private boolean isAllQuestionsAnswered(Map<String, Boolean> answers) {
        return answers != null && answers.size() == 10; // a ถึง j
    }

    private boolean hasAnySubstanceUse(Map<String, Boolean> answers) {
        return answers.containsValue(true);
    }
    /**
     * คำนวณความสูงทั้งหมดของ Fragment รวมถึง Fragment ย่อยทั้งหมดภายใน
     * ใช้สำหรับการปรับ ViewPager2 height ให้ถูกต้อง
     *
     * @return ความสูงรวมทั้งหมดเป็นพิกเซล
     */
    public int calculateTotalHeight() {
        int totalHeight = 0;
        View view = getView();

        if (view == null) return totalHeight;

        // เพิ่มค่า padding ของตัว Fragment หลัก
        totalHeight += view.getPaddingTop() + view.getPaddingBottom();

        // หาความสูงของแต่ละ Fragment container
        int[] fragmentContainerIds = new int[] {
                R.id.question_one_container,
                R.id.question_two_container,
                R.id.question_three_container,
                R.id.question_four_container,
                R.id.question_five_container,
                R.id.question_six_container,
                R.id.question_seven_container,
                R.id.question_eight_container
        };

        for (int containerId : fragmentContainerIds) {
            View containerView = view.findViewById(containerId);
            if (containerView != null) {
                // คำนวณขนาดของ container
                containerView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );

                // เข้าถึง Fragment ที่อยู่ภายใน container
                Fragment childFragment = getChildFragmentManager().findFragmentById(containerId);
                if (childFragment != null && childFragment.getView() != null) {
                    View fragmentView = childFragment.getView();

                    // ตรวจสอบความสูงของ content ที่อาจถูก expand/collapse
                    ViewGroup contentLayout = null;

                    // ค้นหา content layout ตามรูปแบบที่ใช้ใน Fragment ย่อย
                    if (childFragment instanceof QuestionOneFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutOne);
                    } else if (childFragment instanceof QuestionTwoFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutTwo);
                    } else if (childFragment instanceof QuestionThreeFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutThree);
                    } else if (childFragment instanceof QuestionFourFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutFour);
                    } else if (childFragment instanceof QuestionFiveFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutFive);
                    } else if (childFragment instanceof QuestionSixFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutSix);
                    } else if (childFragment instanceof QuestionSevenFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutSeven);
                    } else if (childFragment instanceof QuestionEightFragment) {
                        contentLayout = fragmentView.findViewById(R.id.contentLayoutEight);
                    }

                    // ถ้าเจอ content layout และมันกำลังแสดงอยู่
                    if (contentLayout != null && contentLayout.getVisibility() == View.VISIBLE) {
                        // คำนวณความสูงของ content
                        contentLayout.measure(
                                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        );

                        int contentHeight = contentLayout.getMeasuredHeight();

                        // เพิ่มความสูงของส่วนหัว (header) ของแต่ละ Fragment ย่อย
                        View headerLayout = null;
                        if (childFragment instanceof QuestionOneFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutOne);
                        } else if (childFragment instanceof QuestionTwoFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutTwo);
                        } else if (childFragment instanceof QuestionThreeFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutThree);
                        } else if (childFragment instanceof QuestionFourFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutFour);
                        } else if (childFragment instanceof QuestionFiveFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutFive);
                        } else if (childFragment instanceof QuestionSixFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutSix);
                        } else if (childFragment instanceof QuestionSevenFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutSeven);
                        } else if (childFragment instanceof QuestionEightFragment) {
                            headerLayout = fragmentView.findViewById(R.id.headerLayoutEight);
                        }
                        // ทำแบบเดียวกันสำหรับ Fragment อื่นๆ

                        int headerHeight = 0;
                        if (headerLayout != null) {
                            headerLayout.measure(
                                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );
                            headerHeight = headerLayout.getMeasuredHeight();
                        }

                        // รวมความสูงทั้งหมดของ Fragment นี้
                        totalHeight += headerHeight + contentHeight;
                    } else {
                        // ถ้า content ถูก collapse ให้ใช้ความสูงของ Fragment ทั้งหมด
                        fragmentView.measure(
                                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        );
                        totalHeight += fragmentView.getMeasuredHeight();
                    }
                } else {
                    // ถ้าไม่มี child fragment ให้ใช้ความสูงของ container
                    totalHeight += containerView.getMeasuredHeight();
                }

                // เพิ่ม margin ระหว่าง containers
                ViewGroup.MarginLayoutParams params =
                        (ViewGroup.MarginLayoutParams) containerView.getLayoutParams();
                if (params != null) {
                    totalHeight += params.topMargin + params.bottomMargin;
                }
            }
        }

        // เพิ่มความสูงขั้นต่ำเพื่อป้องกันความผิดพลาด
        int minHeight = 1500; // ปรับตามความเหมาะสม
        return Math.max(totalHeight, minHeight);
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
            }, 300); // delay เล็กน้อยเพื่อให้ Fragment ย่อยได้คำนวณขนาดก่อน
        }
    }
    // เพิ่มเมธอดนี้ใน MainQuestionsFragment.java
//    public boolean isAllDataComplete() {
//        boolean isComplete = true;
//
//        // ตรวจสอบความครบถ้วนของแต่ละ Fragment
//        if (questionOneFragment != null && !questionOneFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionTwoFragment != null && !questionTwoFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionThreeFragment != null && !questionThreeFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionFourFragment != null && !questionFourFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionFiveFragment != null && !questionFiveFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionSixFragment != null && !questionSixFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionSevenFragment != null && !questionSevenFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        if (questionEightFragment != null && !questionEightFragment.validateAllQuestionsAnswered()) {
//            isComplete = false;
//        }
//
//        return isComplete;
//    }
//
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
            return ""; // ไม่มีข้อผิดพลาด
        }

        return "กรุณากรอกข้อมูลให้ครบถ้วน:\n\n" + String.join("\n\n", allMessages);
    }

    public boolean isAllDataComplete() {
        return getDetailedValidationMessage().isEmpty();
    }

    // เพิ่มเมธอดนี้ใน MainQuestionsFragment.java

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

        // ตรวจสอบว่าทุกสารเสพติดถูกเลือกเป็น "ไม่เคย" (false) หรือไม่
        for (Map.Entry<String, AnswerData> entry : answers.entrySet()) {
            AnswerData answer = entry.getValue();

            // ถ้าไม่มีการตอบหรือยังไม่ได้เลือก
            if (answer == null || answer.isHasUsed() == null) {
                return false;
            }

            // ถ้ามีสารเสพติดใดที่เลือก "เคย" ใช้
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

        // ตรวจสอบว่ามีสารเสพติดใดที่เลือก "เคย" ใช้หรือไม่
        for (Map.Entry<String, AnswerData> entry : answers.entrySet()) {
            AnswerData answer = entry.getValue();

            // ถ้ามีสารเสพติดใดที่เลือก "เคย" ใช้
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
        // ก่อนอื่นต้องตรวจสอบว่า Question 1 ได้รับการตอบครบถ้วนหรือไม่
        if (questionOneFragment == null || !questionOneFragment.validateAllQuestionsAnswered()) {
            return false;
        }

        // ถ้าไม่เคยใช้สารเสพติดเลย ถือว่าข้อมูลครบถ้วนแล้ว
        if (isAllSubstancesNeverUsed()) {
            return true;
        }

        // ถ้าเคยใช้สารเสพติด ต้องตรวจสอบความครบถ้วนของคำถามอื่นๆ
        if (hasAnySubstanceUsed()) {
            return isAllDataComplete();
        }

        // กรณีอื่นๆ ที่ไม่ควรเกิดขึ้น
        return false;
    }

    /**
     * รับข้อความแจ้งเตือนที่เหมาะสมตามสถานะการใช้สารเสพติด
     */
    public String getValidationMessageBasedOnSubstanceUse() {
        // ตรวจสอบ Question 1 ก่อน
        if (questionOneFragment == null || !questionOneFragment.validateAllQuestionsAnswered()) {
            return questionOneFragment != null ? questionOneFragment.getValidationMessage() :
                    "กรุณาตอบคำถามที่ 1 ให้ครบถ้วน";
        }

        // ถ้าไม่เคยใช้สารเสพติดเลย ไม่ต้องตอบคำถามอื่น
        if (isAllSubstancesNeverUsed()) {
            return ""; // ไม่มีข้อผิดพลาด
        }

        // ถ้าเคยใช้สารเสพติด ต้องตอบคำถามอื่นๆ ให้ครบ
        if (hasAnySubstanceUsed()) {
            return getDetailedValidationMessage();
        }

        return "";
    }
}
