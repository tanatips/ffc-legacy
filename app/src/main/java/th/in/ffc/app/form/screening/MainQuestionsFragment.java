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
        questionsStateViewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // สังเกตการเปลี่ยนแปลงของ Question 1 เพื่อแสดง/ซ่อน AssistScoreFragment
        observeQuestionOneChanges();
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

            // ซ่อน AssistScoreFragment ในตอนเริ่มต้น
            hideAssistScoreFragment();
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
     * สังเกตการเปลี่ยนแปลงของ Question 1 เพื่อแสดง/ซ่อน AssistScoreFragment
     */
    private void observeQuestionOneChanges() {
        if (questionsStateViewModel != null) {
            questionsStateViewModel.getQuestionOneAnswers().observe(getViewLifecycleOwner(), answers -> {
                if (answers != null) {
                    checkAndToggleAssistScoreVisibility();
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
            checkAndToggleAssistScoreVisibility();
            Log.d(TAG, "AssistScoreFragment รีเฟรชแล้ว");
        }
    }

    /**
     * คำนวณความสูงทั้งหมดของ Fragment รวมถึง AssistScoreFragment
     */
    public int calculateTotalHeight() {
        int totalHeight = 0;
        View view = getView();

        if (view == null) return totalHeight;

        // เพิ่มค่า padding ของตัว Fragment หลัก
        totalHeight += view.getPaddingTop() + view.getPaddingBottom();

        // หาความสูงของแต่ละ Fragment container (รวม assist_summary_container)
        int[] fragmentContainerIds = new int[] {
                R.id.question_one_container,
                R.id.question_two_container,
                R.id.question_three_container,
                R.id.question_four_container,
                R.id.question_five_container,
                R.id.question_six_container,
                R.id.question_seven_container,
                R.id.question_eight_container,
                R.id.assist_summary_container // เพิ่ม AssistScoreFragment container
        };

        for (int containerId : fragmentContainerIds) {
            View containerView = view.findViewById(containerId);
            if (containerView != null && containerView.getVisibility() == View.VISIBLE) {
                // คำนวณขนาดของ container
                containerView.measure(
                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );

                // เข้าถึง Fragment ที่อยู่ภายใน container
                Fragment childFragment = getChildFragmentManager().findFragmentById(containerId);
                if (childFragment != null && childFragment.getView() != null) {
                    View fragmentView = childFragment.getView();

                    // ตรวจสอบความสูงของ content สำหรับ AssistScoreFragment
                    if (childFragment instanceof AssistScoreFragment) {
                        fragmentView.measure(
                                View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        );
                        totalHeight += fragmentView.getMeasuredHeight();
                        Log.d(TAG, "AssistScoreFragment height: " + fragmentView.getMeasuredHeight());
                    } else {
                        // ตรวจสอบความสูงของ content ที่อาจถูก expand/collapse สำหรับ fragment อื่นๆ
                        ViewGroup contentLayout = getContentLayoutFromFragment(childFragment, fragmentView);

                        if (contentLayout != null && contentLayout.getVisibility() == View.VISIBLE) {
                            contentLayout.measure(
                                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );

                            int contentHeight = contentLayout.getMeasuredHeight();
                            View headerLayout = getHeaderLayoutFromFragment(childFragment, fragmentView);

                            int headerHeight = 0;
                            if (headerLayout != null) {
                                headerLayout.measure(
                                        View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                                );
                                headerHeight = headerLayout.getMeasuredHeight();
                            }

                            totalHeight += headerHeight + contentHeight;
                        } else {
                            fragmentView.measure(
                                    View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                            );
                            totalHeight += fragmentView.getMeasuredHeight();
                        }
                    }
                } else {
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
        int minHeight = 1500;
        return Math.max(totalHeight, minHeight);
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
        // บังคับให้ Fragment คำนวณขนาดใหม่
        View view = getView();
        if (view != null) {
            view.requestLayout();
        }

        // ตรวจสอบการแสดง AssistScoreFragment
        checkAndToggleAssistScoreVisibility();

        // แจ้ง Activity ให้ปรับขนาด ViewPager
        if (getActivity() instanceof PersonScreeningForm15Activity) {
            new Handler().postDelayed(() -> {
                ((PersonScreeningForm15Activity) getActivity()).refreshViewPager();
            }, 300);
        }
    }

    // เมธอดเดิมทั้งหมด (ไม่เปลี่ยนแปลง)
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

    /**
     * เมธอดสำหรับการจัดการ AssistScoreFragment เมื่อข้อมูลเปลี่ยนแปลง
     */
    public void onDataChanged() {
        // รีเฟรช AssistScoreFragment เมื่อข้อมูลเปลี่ยนแปลง
        refreshAssistScoreFragment();

        // แจ้งให้ parent activity ทราบ
        notifyChildFragmentStateChanged();

        Log.d(TAG, "ข้อมูลเปลี่ยนแปลง - รีเฟรช AssistScoreFragment");
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