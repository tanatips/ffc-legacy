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
import th.in.ffc.app.form.screening.listener.OnConcernSelectedListener;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.SubstanceItem;
public class SubstanceSixAdapter extends RecyclerView.Adapter<SubstanceSixAdapter.ViewHolder> {
    private ArrayList<SubstanceItem> substanceList;
    private OnConcernSelectedListener listener;

    public SubstanceSixAdapter(ArrayList<SubstanceItem> substanceList, OnConcernSelectedListener listener) {
        this.substanceList = substanceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_substance_concern, parent, false);
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
        private RadioGroup answerGroup;
        private TextInputLayout otherSubstanceLayout;
        private TextInputEditText otherSubstanceEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            answerGroup = itemView.findViewById(R.id.answerGroup);
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

            // แสดง TextBox สำหรับข้อ j
            if (item.getId().equals("j")) {
                otherSubstanceLayout.setVisibility(View.VISIBLE);
                otherSubstanceEdit.setText(item.getOtherSubstance());

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

            // เช็คค่าที่เคยเลือกไว้
            if (item.getConcern() > 0) {
                int radioId = getRadioIdForValue(item.getConcern());
                if (radioId != -1) {
                    answerGroup.check(radioId);
                }
            }

            answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int value;
                if (checkedId == R.id.radioNever) value = 0;
                else if (checkedId == R.id.radioWithin) value = 6;
                else if (checkedId == R.id.radioBefore) value = 3;
                else value = 0;

                if (listener != null) {
                    listener.onConcernSelected(item.getId(), value);
                }
            });
        }

        private int getRadioIdForValue(int value) {
            switch (value) {
                case 0: return R.id.radioNever;
                case 6: return R.id.radioWithin;
                case 3: return R.id.radioBefore;
                default: return -1;
            }
        }
    }
}
