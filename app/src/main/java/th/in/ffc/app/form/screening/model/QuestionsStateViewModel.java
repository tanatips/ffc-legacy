package th.in.ffc.app.form.screening.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import th.in.ffc.app.form.screening.QuestionOneFragment;
import th.in.ffc.app.form.screening.QuestionTwoFragment;
import th.in.ffc.app.form.screening.QuestionThreeFragment;
import th.in.ffc.app.form.screening.QuestionFourFragment;
import th.in.ffc.app.form.screening.QuestionFiveFragment;
import th.in.ffc.app.form.screening.QuestionSixFragment;
import th.in.ffc.app.form.screening.QuestionSevenFragment;
public class QuestionsStateViewModel extends ViewModel {
    private MutableLiveData<Map<String, AnswerData>> questionOneAnswers
            = new MutableLiveData<>(new HashMap<>());
    private MutableLiveData<Map<String,  AnswerFrequencyData>> questionTwoAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, AnswerFrequencyData>> questionThreeAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, AnswerFrequencyData>> questionFourAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, AnswerFrequencyData>> questionFiveAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, AnswerFrequencyData>> questionSixAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, AnswerFrequencyData>> questionSevenAnswers = new MutableLiveData<>(new HashMap<>());

    private MutableLiveData<Map<String, AnswerFrequencyData>> questionEightAnswers = new MutableLiveData<>(new HashMap<>());

    public void updateQuestionOneAnswer(String id, boolean hasUsed, String otherDrugs) {
        Map<String, AnswerData> currentAnswers = questionOneAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.isHasUsed() != hasUsed ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerData(hasUsed, otherDrugs));
            questionOneAnswers.setValue(newAnswers);
        }
    }


    public void updateQuestionTwoAnswer(String id,int frequency, String otherDrugs) {

        Map<String, AnswerFrequencyData> currentAnswers = questionTwoAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerFrequencyData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.getFrequency() != frequency ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerFrequencyData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerFrequencyData(frequency, otherDrugs));
            questionTwoAnswers.setValue(newAnswers);
        }

    }

    public void updateQuestionThreeAnswer(String id,int  frequency, String otherDrugs) {
        Map<String, AnswerFrequencyData> currentAnswers = questionThreeAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerFrequencyData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.getFrequency() != frequency ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerFrequencyData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerFrequencyData(frequency, otherDrugs));
            questionThreeAnswers.setValue(newAnswers);
        }
    }

    public void updateQuestionFourAnswer(String id,int  frequency, String otherDrugs) {
        Map<String, AnswerFrequencyData> currentAnswers = questionFourAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerFrequencyData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.getFrequency() != frequency ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerFrequencyData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerFrequencyData(frequency, otherDrugs));
            questionFourAnswers.setValue(newAnswers);
        }
    }
    public void updateQuestionFiveAnswer(String id,int frequency, String otherDrugs) {

        Map<String, AnswerFrequencyData> currentAnswers = questionFiveAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerFrequencyData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.getFrequency() != frequency ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerFrequencyData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerFrequencyData(frequency, otherDrugs));
            questionFourAnswers.setValue(newAnswers);
        }
    }

    public void updateQuestionSixAnswer(String id,int frequency, String otherDrugs) {
        Map<String, AnswerFrequencyData> currentAnswers = questionSixAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerFrequencyData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.getFrequency() != frequency ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerFrequencyData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerFrequencyData(frequency, otherDrugs));
            questionSixAnswers.setValue(newAnswers);
        }
    }
    public void updateQuestionEightAnswer(String id,int frequency, String otherDrugs) {
        Map<String, AnswerFrequencyData> currentAnswers = questionEightAnswers.getValue();
        if (currentAnswers == null) {
            currentAnswers = new HashMap<>();
        }

        // ตรวจสอบว่าค่าเปลี่ยนแปลงจริงๆ หรือไม่
        AnswerFrequencyData currentAnswer = currentAnswers.get(id);
        if (currentAnswer == null ||
                currentAnswer.getFrequency() != frequency ||
                !Objects.equals(currentAnswer.getOtherDrugs(), otherDrugs)) {

            Map<String, AnswerFrequencyData> newAnswers = new HashMap<>(currentAnswers);
            newAnswers.put(id, new AnswerFrequencyData(frequency, otherDrugs));
            questionEightAnswers.setValue(newAnswers);
        }
    }

    public LiveData<Map<String, AnswerData>> getQuestionOneAnswers() {
        return questionOneAnswers;
    }

    public LiveData<Map<String, AnswerFrequencyData>> getQuestionTwoAnswers() {
        return questionTwoAnswers;
    }
    public LiveData<Map<String, AnswerFrequencyData>> getQuestionThreeAnswers() {
        return questionThreeAnswers;
    }

    public MutableLiveData<Map<String, AnswerFrequencyData>> getQuestionFourAnswers() {
        return questionFourAnswers;
    }

    public void setQuestionFourAnswers(MutableLiveData<Map<String, AnswerFrequencyData>> questionFourAnswers) {
        this.questionFourAnswers = questionFourAnswers;
    }

    public MutableLiveData<Map<String, AnswerFrequencyData>> getQuestionFiveAnswers() {
        return questionFiveAnswers;
    }

    public void setQuestionFiveAnswers(MutableLiveData<Map<String, AnswerFrequencyData>> questionFiveAnswers) {
        this.questionFiveAnswers = questionFiveAnswers;
    }

    public MutableLiveData<Map<String, AnswerFrequencyData>> getQuestionSixAnswers() {
        return questionSixAnswers;
    }

    public void setQuestionSixAnswers(MutableLiveData<Map<String, AnswerFrequencyData>> questionSixAnswers) {
        this.questionSixAnswers = questionSixAnswers;
    }

    public MutableLiveData<Map<String, AnswerFrequencyData>> getQuestionSevenAnswers() {
        return questionSevenAnswers;
    }

    public void setQuestionSevenAnswers(MutableLiveData<Map<String, AnswerFrequencyData>> questionSevenAnswers) {
        this.questionSevenAnswers = questionSevenAnswers;
    }

    public MutableLiveData<Map<String, AnswerFrequencyData>> getQuestionEightAnswers() {
        return questionEightAnswers;
    }

    public void setQuestionEightAnswers(MutableLiveData<Map<String, AnswerFrequencyData>> questionEightAnswers) {
        this.questionEightAnswers = questionEightAnswers;
    }
    public void initQuestionTwoAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionTwoAnswers.getValue() == null) {
            questionTwoAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }

    public void initQuestionThreeAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionThreeAnswers.getValue() == null) {
            questionThreeAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void initQuestionFourAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionFourAnswers.getValue() == null) {
            questionFourAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void initQuestionFiveAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionFiveAnswers.getValue() == null) {
            questionFiveAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void initQuestionSixAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionSixAnswers.getValue() == null) {
            questionSixAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void initQuestionSevenAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionSevenAnswers.getValue() == null) {
            questionSevenAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void initQuestionEightAnswers(Map<String, AnswerFrequencyData> initialAnswers) {
        if (questionEightAnswers.getValue() == null) {
            questionEightAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }
    public void setQuestionOneAnswers(Map<String, AnswerData> answers) {
        questionOneAnswers.setValue(new HashMap<>(answers));
    }

    public void setQuestionTwoAnswers(Map<String, AnswerFrequencyData> answers) {
        questionTwoAnswers.setValue(new HashMap<>(answers));
    }

    public void setQuestionThreeAnswers(Map<String, AnswerFrequencyData> answers) {
        questionThreeAnswers.setValue(new HashMap<>(answers));
    }
    public void setQuestionFourAnswers(Map<String, AnswerFrequencyData> answers) {
        questionFourAnswers.setValue(new HashMap<>(answers));
    }
    public void setQuestionFiveAnswers(Map<String, AnswerFrequencyData> answers) {
        questionFiveAnswers.setValue(new HashMap<>(answers));
    }
    public void setQuestionSixAnswers(Map<String, AnswerFrequencyData> answers) {
        questionSixAnswers.setValue(new HashMap<>(answers));
    }
    public void setQuestionSevenAnswers(Map<String, AnswerFrequencyData> answers) {
        questionSevenAnswers.setValue(new HashMap<>(answers));
    }
    public void setQuestionEightAnswers(Map<String, AnswerFrequencyData> answers) {
        questionEightAnswers.setValue(new HashMap<>(answers));
    }
    public void initQuestionOneAnswers(Map<String, AnswerData> initialAnswers) {
        if (questionOneAnswers.getValue() == null || questionOneAnswers.getValue().isEmpty()) {
            questionOneAnswers.setValue(new HashMap<>(initialAnswers));
        }
    }

}
