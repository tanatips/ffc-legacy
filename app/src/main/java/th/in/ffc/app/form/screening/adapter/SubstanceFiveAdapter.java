package th.in.ffc.app.form.screening.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.text.TextUtils;

import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.SubstanceItem;
public class SubstanceFiveAdapter extends RecyclerView.Adapter<SubstanceFiveAdapter.ViewHolder> {
    private ArrayList<SubstanceItem> substanceList;
    private OnFrequencySelectedListener listener;
    private TextInputLayout otherSubstanceLayout; // เพิ่ม
    private TextInputEditText otherSubstanceEdit; // เพิ่ม

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

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private RadioGroup frequencyGroup;

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

            // เช็คค่าที่เคยเลือกไว้ (ถ้ามี)
            if (item.getFrequency() > 0) {
                int radioId = getRadioIdForFrequency(item.getFrequency());
                if (radioId != -1) {
                    frequencyGroup.check(radioId);
                }
            }
            if (item.getId().equals("j")) {
                otherSubstanceLayout.setVisibility(View.VISIBLE);
                otherSubstanceEdit.setText(item.getOtherSubstance());

                // ตั้งค่า TextWatcher สำหรับข้อความที่กรอก
                otherSubstanceEdit.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        item.setOtherSubstance(s.toString());
                    }
                });
            } else {
                otherSubstanceLayout.setVisibility(View.GONE);
            }
            frequencyGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int frequency;
                if (checkedId == R.id.radioNever) frequency = 0;
                else if (checkedId == R.id.radio1to2) frequency = 5;
                else if (checkedId == R.id.radioMonthly) frequency = 6;
                else if (checkedId == R.id.radioWeekly) frequency = 7;
                else if (checkedId == R.id.radioDaily) frequency = 8;
                else frequency = 0;

                if (listener != null) {
                    listener.onFrequencySelected(item.getId(), frequency);
                }
            });
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
    }
}
