package th.in.ffc.person;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import th.in.ffc.R;

public class BmiInfoDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.bmi_info_dialog, null);

        return new AlertDialog.Builder(requireContext())
                .setTitle("เกณฑ์การแปลผลค่า BMI สำหรับคนเอเชีย")
                .setView(dialogView)
                .setPositiveButton("ปิด", (dialog, which) -> dialog.dismiss())
                .create();
    }
}
