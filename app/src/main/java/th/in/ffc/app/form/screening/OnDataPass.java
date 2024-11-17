package th.in.ffc.app.form.screening;

import th.in.ffc.app.form.screening.model.DataCenterInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;

public interface OnDataPass {
//    void onDataPass(PersonInfo data);
    void onPersonInfo(PersonInfo data);
    void onSmokerInfo(SmokerInfo data);
    void onDrinkingInfo(DrinkingInfo data);
    void onNicotineInfo(NicotineInfo data);
    void onStressDepression(StressDepressionInfo data);

    void onStressDepression2q(StressDepression2qInfo data);
    void onStressDepression9q(StressDepression9qInfo data);
    void onSuicideAssessment8q(SuicideAssessment8qInfo data);

    void onHealthRiskAssessmentInfo(HealthRiskAssessmentInfo data);
}