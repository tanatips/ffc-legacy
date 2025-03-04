package th.in.ffc.app.form.screening.listener;

public interface OnSubstanceSelectionListener {
//    void onSubstanceSelectionChanged(String id, boolean hasUsed);
    void onAnswerChanged(String id, boolean hasUsed, String otherSubstance);
}
