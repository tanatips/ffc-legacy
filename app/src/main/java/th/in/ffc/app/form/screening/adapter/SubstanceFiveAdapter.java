package th.in.ffc.app.form.screening.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

import android.text.TextUtils;

import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.SubstanceItem;
public class SubstanceFiveAdapter extends RecyclerView.Adapter<SubstanceFiveAdapter.ViewHolder> {
    private ArrayList<SubstanceItem> substanceList;
    private OnFrequencySelectedListener listener;
    private TextInputLayout otherSubstanceLayout; // เพิ่ม
    private TextInputEditText otherSubstanceEdit; // เพิ่ม

    private boolean isUpdating = false;

    public SubstanceFiveAdapter(ArrayList<SubstanceItem> substanceList, OnFrequencySelectedListener listener) {
        this.substanceList = substanceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_substance_problem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(substanceList.get(position));
    }

    @Override
    public int getItemCount() {
        return substanceList.size();
    }

    public void updateAnswers(Map<String, AnswerFrequencyData> answers) {
        if (isUpdating) return;
        isUpdating = true;
        try {
            for (SubstanceItem item : substanceList) {
                AnswerFrequencyData frequency = answers.get(item.getId());
                if (frequency != null) {
                    item.setFrequency(frequency.getFrequency());
                }
            }
            notifyDataSetChanged();
        } finally {
            isUpdating = false;
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private RadioGroup frequencyGroup;

        private TextInputLayout otherSubstanceLayout; // เพิ่ม
        private TextInputEditText otherSubstanceEdit; // เพิ่ม

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            frequencyGroup = itemView.findViewById(R.id.frequencyGroup);
            otherSubstanceLayout = itemView.findViewById(R.id.otherSubstanceLayout);
            otherSubstanceEdit = itemView.findViewById(R.id.otherSubstanceEdit);
        }

        public void bind(SubstanceItem item) {
            titleText.setText(item.getName());
            if (!TextUtils.isEmpty(item.getDescription())) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setText(item.getDescription());
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            // ล้างและตั้งค่า OnCheckedChangeListener ก่อน
            frequencyGroup.setOnCheckedChangeListener(null);
            frequencyGroup.clearCheck();

            // ตั้งค่าการเลือกตามค่าที่มีอยู่
            int frequency = item.getFrequency();
            int radioId = getRadioIdForFrequency(frequency);
            if (radioId != -1) {
                frequencyGroup.check(radioId);
            }
            // ตั้งค่า listener หลังจากตั้งค่าการเลือกแล้ว
            frequencyGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int newFrequency = getFrequencyForRadioId(checkedId);
                if (newFrequency != item.getFrequency()) {
                    item.setFrequency(newFrequency);
                    if (listener != null) {
                        listener.onFrequencySelected(item.getId(), newFrequency, item.getOtherDrugs());
                    }
                }
            });

            // จัดการ TextInput สำหรับข้อ j
            if (item.getId().equals("j")) {
                otherSubstanceLayout.setVisibility(View.VISIBLE);
                otherSubstanceEdit.setText(item.getOtherDrugs());

                // ตั้งค่า TextWatcher สำหรับข้อความที่กรอก
                otherSubstanceEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        String newText = s.toString();
                        item.setOtherDrugs(newText);
                    }
                });
            } else {
                otherSubstanceLayout.setVisibility(View.GONE);
            }
        }

        private int getRadioIdForFrequency(int frequency) {
            switch (frequency) {
                case 0: return R.id.radioNever;
                case 5: return R.id.radio1to2;
                case 6: return R.id.radioMonthly;
                case 7: return R.id.radioWeekly;
                case 8: return R.id.radioDaily;
                default: return -1;
            }
        }
        private int getFrequencyForRadioId(int radioId) {
            if (radioId == R.id.radioNever) return 0;
            if (radioId == R.id.radio1to2) return 5;
            if (radioId == R.id.radioMonthly) return 6;
            if (radioId == R.id.radioWeekly) return 7;
            if (radioId == R.id.radioDaily) return 8;
            return 0;
        }
    }
}
