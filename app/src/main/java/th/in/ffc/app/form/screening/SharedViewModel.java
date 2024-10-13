package th.in.ffc.app.form.screening;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import th.in.ffc.app.form.screening.datalive.AssessmentOfObesityLiveData;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.datalive.DrinkingLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<StressDepression9qLiveData> stressDepression9qLiveDataModelMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StressDepression2qLiveData> stressDepression2qLiveDataModelMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<SmookingLiveData> smookingLiveDataMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<DrinkingLiveData>  drinkingMutableLiveData = new MutableLiveData<>();


    private  MutableLiveData<CigaretteAddictionTestLiveData>  cigaretteAddictionTestLiveDataMutableLiveData = new MutableLiveData<>();


    private  MutableLiveData<AssessmentOfObesityLiveData>  assessmentOfObesityLiveDataMutableLiveData = new MutableLiveData<>();

    private  MutableLiveData<SuicideAssessment8qLiveData>  suicideAssessment8qLiveDataMutableLiveData = new MutableLiveData<>();

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
    public void setAssessmentOfObesityLiveDataMutableLiveData(AssessmentOfObesityLiveData value) {
        assessmentOfObesityLiveDataMutableLiveData.setValue(value);
    }

    public LiveData<AssessmentOfObesityLiveData> getAssessmentOfObesityLiveDataMutableLiveData() {
        return assessmentOfObesityLiveDataMutableLiveData;
    }

    // suicideAssessment8qLiveDataMutableLiveData

    public void setSuicideAssessment8qMutableLiveData(SuicideAssessment8qLiveData value) {
        suicideAssessment8qLiveDataMutableLiveData.setValue(value);
    }

    public LiveData<SuicideAssessment8qLiveData> getSuicideAssessment8qMutableLiveData() {
        return suicideAssessment8qLiveDataMutableLiveData;
    }
}
