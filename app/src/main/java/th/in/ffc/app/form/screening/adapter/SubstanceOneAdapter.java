package th.in.ffc.app.form.screening.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.OnSubstanceSelectionListener;
import th.in.ffc.app.form.screening.QuestionOneFragment;
import th.in.ffc.app.form.screening.model.SubstanceItem;

public class SubstanceOneAdapter extends RecyclerView.Adapter<SubstanceOneAdapter.SubstanceViewHolder> {
    private List<SubstanceItem> substanceList;
    private OnSubstanceSelectionListener listener;

    private boolean isUpdating = false;

    public SubstanceOneAdapter(List<SubstanceItem> substanceList, OnSubstanceSelectionListener listener) {
        this.listener = listener;
        this.substanceList = substanceList;
    }
    public void updateAnswers(Map<String, QuestionOneFragment.AnswerData> answers) {
        if (isUpdating) return;
        isUpdating = true;

        try {
            for (SubstanceItem item : substanceList) {
                QuestionOneFragment.AnswerData answer = answers.get(item.getId());
                if (answer != null) {
                    // อัพเดต state ของ item โดยตรง
                    item.setHasUsed(answer.isHasUsed());
                    item.setOtherSubstance(answer.getOtherSubstance());
                }
            }
            // แจ้ง adapter ให้ update ทุกครั้ง
            notifyDataSetChanged();
        } finally {
            isUpdating = false;
        }
    }
    @NonNull
    @Override
    public SubstanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_substance, parent, false);
        return new SubstanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubstanceViewHolder holder, int position) {
        SubstanceItem item = substanceList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return substanceList.size();
    }

    class SubstanceViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private RadioGroup radioGroup;
        private TextInputLayout otherSubstanceLayout; // เพิ่ม
        private TextInputEditText otherSubstanceEdit; // เพิ่ม

        public SubstanceViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            radioGroup = itemView.findViewById(R.id.radioGroup);
            otherSubstanceLayout = itemView.findViewById(R.id.otherSubstanceLayout);
            otherSubstanceEdit = itemView.findViewById(R.id.otherSubstanceEdit);
        }

        public void bind(SubstanceItem item) {
            titleText.setText(item.getName());
            if (!item.getDescription().isEmpty()) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setText(item.getDescription());
            } else {
                descriptionText.setVisibility(View.GONE);
            }

            // เคลียร์ listener ก่อน
            radioGroup.setOnCheckedChangeListener(null);

            // Set radio button state
            radioGroup.clearCheck();
            radioGroup.check(item.isHasUsed() ? R.id.radioUsed : R.id.radioNotUsed);
            // ตั้งค่า listener
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (!isUpdating) {
                    boolean isUsed = checkedId == R.id.radioUsed;
                    item.setHasUsed(isUsed);
                    if (listener != null) {
                        listener.onAnswerChanged(item.getId(), isUsed, item.getOtherSubstance());
                    }
                }
            });

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
                        String newText = s.toString();
                        item.setOtherSubstance(newText);
                        // เพิ่มการเรียก listener เพื่อ update ViewModel
                        if (listener != null) {
                            listener.onAnswerChanged(item.getId(), item.isHasUsed(), newText);
                        }
                    }
                });
            } else {
                otherSubstanceLayout.setVisibility(View.GONE);
            }

        }
    }
}

