package th.in.ffc.app.form.screening;

import th.in.ffc.app.form.screening.model.DataCenterInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
public interface OnDataPass {
//    void onDataPass(PersonInfo data);
    void onPersonInfo(PersonInfo data);
    void onSmokerInfo(SmokerInfo data);
    void onDrinkingInfo(DrinkingInfo data);
}