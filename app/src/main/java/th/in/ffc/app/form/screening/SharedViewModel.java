package th.in.ffc.app.form.screening;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import th.in.ffc.app.form.screening.datalive.CardiovascularRiskLiveData;
import th.in.ffc.app.form.screening.datalive.CounselingLiveData;
import th.in.ffc.app.form.screening.datalive.DrugsLiveData;
import th.in.ffc.app.form.screening.datalive.HealthRiskAssessmentLiveData;
import th.in.ffc.app.form.screening.datalive.PersonInfoLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepressionLiveData;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.datalive.DrinkingLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.app.form.screening.model.PersonData;
import th.in.ffc.util.Log;

public class SharedViewModel extends ViewModel {

    private MutableLiveData<PersonInfoLiveData> personInfoLiveDataMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<StressDepressionLiveData> stressDepressionLiveDataMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StressDepression9qLiveData> stressDepression9qLiveDataModelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StressDepression2qLiveData> stressDepression2qLiveDataModelMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<SmookingLiveData> smookingLiveDataMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<DrinkingLiveData>  drinkingMutableLiveData = new MutableLiveData<>();


    private  MutableLiveData<CigaretteAddictionTestLiveData>  cigaretteAddictionTestLiveDataMutableLiveData = new MutableLiveData<>();


    private  MutableLiveData<StressDepressionLiveData>  assessmentOfObesityLiveDataMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<SuicideAssessment8qLiveData>  suicideAssessment8qLiveDataMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<HealthRiskAssessmentLiveData>  healthRiskAssessmentLiveDataMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<AssistScore> assistScoreMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<DrugsLiveData> drugsLiveDataMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardiovascularRiskLiveData> cardiovascularRiskLiveDataMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<CounselingLiveData> counselingLiveData = new MutableLiveData<>();

    private MutableLiveData<PersonData> personDataMutableLiveData = new MutableLiveData<>();
    public void setCounselingLiveData(CounselingLiveData data) {
        counselingLiveData.setValue(data);
    }

    public LiveData<CounselingLiveData> getCounselingLiveData() {
        return counselingLiveData;
    }
    public void setStressDepression9qLiveData(StressDepression9qLiveData value) {
        stressDepression9qLiveDataModelMutableLiveData.setValue(value);
    }

    public LiveData<StressDepression9qLiveData> getStressDepression9qLiveData() {
        return stressDepression9qLiveDataModelMutableLiveData;
    }

    public void setStressDepression2qLiveData(StressDepression2qLiveData value) {
        stressDepression2qLiveDataModelMutableLiveData.setValue(value);
    }

    public LiveData<StressDepression2qLiveData> getStressDepression2qLiveData() {
        return stressDepression2qLiveDataModelMutableLiveData;
    }


    public void setSmookingMutableLiveData(SmookingLiveData value) {
        smookingLiveDataMutableLiveData.setValue(value);
    }

    public LiveData<SmookingLiveData> getSmookingMutableLiveData() {
        return smookingLiveDataMutableLiveData;
    }

    public void setDrinkingMutableLiveData(DrinkingLiveData value) {
        drinkingMutableLiveData.setValue(value);
    }

    public LiveData<DrinkingLiveData> getDrinkingMutableLiveData() {
        return drinkingMutableLiveData;
    }

    public void setCigatetteAddictionTestMutableLiveData(CigaretteAddictionTestLiveData value) {
        cigaretteAddictionTestLiveDataMutableLiveData.setValue(value);
    }

    public LiveData<CigaretteAddictionTestLiveData> getCigatetteAddictionTestMutableLiveData() {
        return cigaretteAddictionTestLiveDataMutableLiveData;
    }

    // assessmentOfObesityLiveDataMutableLiveData
    public void setAssessmentOfObesityLiveDataMutableLiveData(StressDepressionLiveData value) {
        assessmentOfObesityLiveDataMutableLiveData.setValue(value);
    }

    public LiveData<StressDepressionLiveData> getAssessmentOfObesityLiveDataMutableLiveData() {
        return assessmentOfObesityLiveDataMutableLiveData;
    }

    // suicideAssessment8qLiveDataMutableLiveData

    public void setSuicideAssessment8qMutableLiveData(SuicideAssessment8qLiveData value) {
        suicideAssessment8qLiveDataMutableLiveData.setValue(value);
    }

    public LiveData<SuicideAssessment8qLiveData> getSuicideAssessment8qMutableLiveData() {
        return suicideAssessment8qLiveDataMutableLiveData;
    }

    public MutableLiveData<HealthRiskAssessmentLiveData> getHealthRiskAssessmentLiveDataMutableLiveData() {
        return healthRiskAssessmentLiveDataMutableLiveData;
    }

    public void setHealthRiskAssessmentLiveDataMutableLiveData(HealthRiskAssessmentLiveData value) {
        this.healthRiskAssessmentLiveDataMutableLiveData.setValue(value);
    }

    public MutableLiveData<PersonInfoLiveData> getPersonInfoLiveDataMutableLiveData() {
        return personInfoLiveDataMutableLiveData;
    }

    public void setPersonInfoLiveDataMutableLiveData(PersonInfoLiveData value) {
        this.personInfoLiveDataMutableLiveData.setValue(value);
    }

    public MutableLiveData<StressDepressionLiveData> getStressDepressionLiveDataMutableLiveData() {
        return stressDepressionLiveDataMutableLiveData;
    }

    public void setStressDepressionLiveDataMutableLiveData(StressDepressionLiveData value) {
        this.stressDepressionLiveDataMutableLiveData.setValue(value);
    }

    public MutableLiveData<StressDepression2qLiveData> getStressDepression2qLiveDataModelMutableLiveData() {
        return stressDepression2qLiveDataModelMutableLiveData;
    }

    public void setStressDepression2qLiveDataModelMutableLiveData(StressDepression2qLiveData value) {
        this.stressDepression2qLiveDataModelMutableLiveData.setValue(value);
    }

    public MutableLiveData<StressDepression9qLiveData> getStressDepression9qLiveDataModelMutableLiveData() {
        return stressDepression9qLiveDataModelMutableLiveData;
    }

    public void setStressDepression9qLiveDataModelMutableLiveData( StressDepression9qLiveData value) {
        this.stressDepression9qLiveDataModelMutableLiveData.setValue(value);
    }

    public MutableLiveData<AssistScore> getAssistScoreMutableLiveData() {
        return assistScoreMutableLiveData;
    }
    public void setAssistScoreMutableLiveData ( AssistScore value) {
        this.assistScoreMutableLiveData.setValue(value);
    }
    public MutableLiveData<DrugsLiveData> getDrugsLiveDataMutableLiveData() {
        return drugsLiveDataMutableLiveData;
    }
    public void setDrugsLiveDataMutableLiveData(DrugsLiveData value) {
        this.drugsLiveDataMutableLiveData.setValue(value);
    }
    public MutableLiveData<CardiovascularRiskLiveData> getCardiovascularRiskLiveDataMutableLiveData() {
        return cardiovascularRiskLiveDataMutableLiveData;
    }

    public void setCardiovascularRiskLiveDataMutableLiveData(CardiovascularRiskLiveData value) {
        if( value.getPersonId() == null) {
            Log.e("SharedViewModel", "setCardiovascularRiskLiveDataMutableLiveData: value is null");
        }
        this.cardiovascularRiskLiveDataMutableLiveData.setValue(value);
    }
    public MutableLiveData<PersonData> getPersonDataLiveData() {
        return personDataMutableLiveData;
    }

    public void setPersonData(PersonData personData) {
        personDataMutableLiveData.setValue(personData);
    }

    /**
     * Method สำหรับอัพเดตข้อมูลบุคคลจากหน้าต่างๆ
     */
    public void updatePersonDataFromScreening(Integer age, String gender, Double bmi,
                                              Double waistCircumference, Boolean hasHypertension,
                                              Boolean hasFamilyDiabetesHistory, Double fcbg, Double fpg) {
        PersonData currentData = personDataMutableLiveData.getValue();
        if (currentData == null) {
            currentData = new PersonData();
        }

        if (age != null) currentData.setAge(age);
        if (gender != null) currentData.setGender(gender);
        if (bmi != null) currentData.setBmi(bmi);
        if (waistCircumference != null) currentData.setWaistCircumference(waistCircumference);
        if (hasHypertension != null) currentData.setHasHypertension(hasHypertension);
        if (hasFamilyDiabetesHistory != null) currentData.setHasFamilyDiabetesHistory(hasFamilyDiabetesHistory);
        if (fcbg != null) currentData.setFcbg(fcbg);
        if (fpg != null) currentData.setFpg(fpg);

        personDataMutableLiveData.setValue(currentData);
    }

//    public void updateDrugsInfo(DrugsInfo drugsInfo) {
//        DrugsLiveData currentValue = drugsLiveDataMutableLiveData.getValue();
//        if (currentValue != null) {
//            currentValue.setDrugsInfo(drugsInfo);
//            drugsLiveDataMutableLiveData.setValue(currentValue);
//        } else {
//            DrugsLiveData newValue = new DrugsLiveData();
//            newValue.setDrugsInfo(drugsInfo);
//            drugsLiveDataMutableLiveData.setValue(newValue);
//        }
//    }
//    public DrugsInfo getCurrentDrugsInfo() {
//        DrugsLiveData currentValue = drugsLiveDataMutableLiveData.getValue();
//        return currentValue != null ? currentValue.getDrugsInfo() : null;
//    }

}
