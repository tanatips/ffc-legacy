package th.in.ffc.app.form.screening.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import th.in.ffc.app.form.screening.QuestionOneFragment;

public class QuestionsStateViewModel extends ViewModel {
    private MutableLiveData<Map<String, QuestionOneFragment.AnswerData>> questionOneAnswers
            = new MutableLiveData<>(new HashMap<>());
    private MutableLiveData<Map<String, Integer>> questionTwoAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, Integer>> questionThreeAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, Integer>> questionFourAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, Integer>> questionFiveAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, Integer>> questionSixAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, Integer>> questionSevenAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, Integer>> questionEightAnswers = new MutableLiveData<>(new HashMap<>());

    public void updateQuestionOneAnswer(String id, boolean hasUsed, String otherSubstance) {
        Map<String, QuestionOneFragment.AnswerData> currentAnswers = questionOneAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        QuestionOneFragment.AnswerData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.isHasUsed() != hasUsed ||
                !Objects.equals(currentAnswer.getOtherSubstance(), otherSubstance)) {

            Map<String, QuestionOneFragment.AnswerData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new QuestionOneFragment.AnswerData(hasUsed, otherSubstance));
            questionOneAnswers.setValue(newAnswers);
        }
    }


    public void updateQuestionTwoAnswer(String id, int frequency) {
        Map<String, Integer> currentAnswers = questionTwoAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }
        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        Integer currentFrequency = currentAnswers.get(id);
        if (currentFrequency == null || currentFrequency != frequency) {
            Map<String, Integer> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, frequency);
            questionTwoAnswers.setValue(newAnswers);
        }

    }

    public void updateQuestionThreeAnswer(String id, int frequency) {
        Map<String, Integer> currentAnswers = questionThreeAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }
        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        Integer currentFrequency = currentAnswers.get(id);
        if (currentFrequency == null || currentFrequency != frequency) {
            Map<String, Integer> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, frequency);
            questionThreeAnswers.setValue(newAnswers);
        }

    }
    public void updateQuestionFourAnswer(String id, int frequency) {
        Map<String, Integer> currentAnswers = questionFourAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }
        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        Integer currentFrequency = currentAnswers.get(id);
        if (currentFrequency == null || currentFrequency != frequency) {
            Map<String, Integer> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, frequency);
            questionFourAnswers.setValue(newAnswers);
        }
    }

    public void updateQuestionFiveAnswer(String id, int frequency) {
        Map<String, Integer> currentAnswers = questionFiveAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }
        currentAnswers.put(id, frequency);
        questionFiveAnswers.setValue(currentAnswers);
    }

    public void updateQuestionSixAnswer(String id, int frequency) {
        Map<String, Integer> currentAnswers = questionSixAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }
        currentAnswers.put(id, frequency);
        questionSixAnswers.setValue(currentAnswers);
    }


    public LiveData<Map<String, QuestionOneFragment.AnswerData>> getQuestionOneAnswers() {
        return questionOneAnswers;
    }

    public LiveData<Map<String, Integer>> getQuestionTwoAnswers() {
        return questionTwoAnswers;
    }
    public LiveData<Map<String, Integer>> getQuestionThreeAnswers() {
        return questionThreeAnswers;
    }

    public MutableLiveData<Map<String, Integer>> getQuestionFourAnswers() {
        return questionFourAnswers;
    }

    public void setQuestionFourAnswers(MutableLiveData<Map<String, Integer>> questionFourAnswers) {
        this.questionFourAnswers = questionFourAnswers;
    }

    public MutableLiveData<Map<String, Integer>> getQuestionFiveAnswers() {
        return questionFiveAnswers;
    }

    public void setQuestionFiveAnswers(MutableLiveData<Map<String, Integer>> questionFiveAnswers) {
        this.questionFiveAnswers = questionFiveAnswers;
    }

    public MutableLiveData<Map<String, Integer>> getQuestionSixAnswers() {
        return questionSixAnswers;
    }

    public void setQuestionSixAnswers(MutableLiveData<Map<String, Integer>> questionSixAnswers) {
        this.questionSixAnswers = questionSixAnswers;
    }

    public MutableLiveData<Map<String, Integer>> getQuestionSevenAnswers() {
        return questionSevenAnswers;
    }

    public void setQuestionSevenAnswers(MutableLiveData<Map<String, Integer>> questionSevenAnswers) {
        this.questionSevenAnswers = questionSevenAnswers;
    }

    public MutableLiveData<Map<String, Integer>> getQuestionEightAnswers() {
        return questionEightAnswers;
    }

    public void setQuestionEightAnswers(MutableLiveData<Map<String, Integer>> questionEightAnswers) {
        this.questionEightAnswers = questionEightAnswers;
    }
    public void initQuestionTwoAnswers(Map<String, Integer> initialAnswers) {
        if (questionTwoAnswers.getValue() == null) {
            questionTwoAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }

    public void initQuestionThreeAnswers(Map<String, Integer> initialAnswers) {
        if (questionThreeAnswers.getValue() == null) {
            questionThreeAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void setQuestionOneAnswers(Map<String, QuestionOneFragment.AnswerData> answers) {
        questionOneAnswers.setValue(new HashMap<>(answers));
    }
    public void initQuestionOneAnswers(Map<String, QuestionOneFragment.AnswerData> initialAnswers) {
        if (questionOneAnswers.getValue() == null || questionOneAnswers.getValue().isEmpty()) {
            questionOneAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }

}
