package th.in.ffc.app.form.screening.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.SubstanceItem;

public class SubstanceSevenAdapter extends RecyclerView.Adapter<SubstanceSevenAdapter.ViewHolder> {
    private ArrayList<SubstanceItem> substanceList;
    private OnFrequencySelectedListener listener;


    private boolean isUpdating = false;

    public SubstanceSevenAdapter(ArrayList<SubstanceItem> substanceList, OnFrequencySelectedListener listener) {
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

    public void updateAnswers(Map<String, AnswerFrequencyData> answers, String excludeId) {
        if (isUpdating) return;
        isUpdating = true;

        try {
            for (int i = 0; i < substanceList.size(); i++) {
                SubstanceItem item = substanceList.get(i);

                // ข้ามการอัปเดตสำหรับ item ที่ระบุใน excludeId
                if (excludeId != null && item.getId().equals(excludeId)) {
                    continue;
                }

                AnswerFrequencyData answer = answers.get(item.getId());
                if (answer != null) {
                    boolean needUpdate = false;

                    // ตรวจสอบว่าค่าเปลี่ยนแปลงหรือไม่
                    if (item.getFrequency() != answer.getFrequency()) {
                        item.setFrequency(answer.getFrequency());
                        needUpdate = true;
                    }

                    // อัปเดต otherDrugs ยกเว้นช่องกรอกข้อความ
                    if (!item.getId().equals("j") && !item.getOtherDrugs().equals(answer.getOtherDrugs())) {
                        item.setOtherDrugs(answer.getOtherDrugs());
                        needUpdate = true;
                    }

                    // อัปเดตเฉพาะ item ที่มีการเปลี่ยนแปลง
                    if (needUpdate) {
                        notifyItemChanged(i);
                    }
                }
            }
        } finally {
            isUpdating = false;
        }
    }
    // เมธอด overload สำหรับการเรียกใช้แบบเดิม
    public void updateAnswers(Map<String, AnswerFrequencyData> answers) {
        updateAnswers(answers, null);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private RadioGroup answerGroup;

        private TextInputLayout otherSubstanceLayout; // เพิ่ม
        private TextInputEditText otherSubstanceEdit; // เพิ่ม

        // เพิ่ม TextWatcher เป็น field เพื่อให้สามารถถอดออกได้
        private TextWatcher textWatcher;

        private RadioButton radioNever;
        private RadioButton radioWithin;
        private RadioButton radioBefore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            answerGroup = itemView.findViewById(R.id.answerGroup);
            otherSubstanceLayout = itemView.findViewById(R.id.otherSubstanceLayout);
            otherSubstanceEdit = itemView.findViewById(R.id.otherSubstanceEdit);
            radioNever = itemView.findViewById(R.id.radioNever);
            radioWithin = itemView.findViewById(R.id.radioWithin);
            radioBefore = itemView.findViewById(R.id.radioBefore);

            radioNever.setText(radioNever.getText().toString()+" ("+getValueForRadioId(R.id.radioNever)+" คะแนน)");
            radioWithin.setText(radioWithin.getText().toString()+" ("+getValueForRadioId(R.id.radioWithin)+" คะแนน)");
            radioBefore.setText(radioBefore.getText().toString()+" ("+getValueForRadioId(R.id.radioBefore)+" คะแนน)");

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
            answerGroup.setOnCheckedChangeListener(null);
            answerGroup.clearCheck();

            // ตั้งค่าการเลือกตามค่าที่มีอยู่
            int frequency = item.getFrequency();
            int radioId = getRadioIdForValue(frequency);
            if (radioId != -1) {
                answerGroup.check(radioId);
            }
            // ตั้งค่า listener หลังจากตั้งค่าการเลือกแล้ว
            answerGroup.setOnCheckedChangeListener((group, checkedId) -> {
                int newFrequency = getValueForRadioId(checkedId);
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

                // ถอด TextWatcher เดิมก่อนตั้งค่าข้อความใหม่
                if (textWatcher != null) {
                    otherSubstanceEdit.removeTextChangedListener(textWatcher);
                }

                // ตรวจสอบและตั้งค่าข้อความโดยไม่กระทบ cursor position
                String currentText = otherSubstanceEdit.getText() != null ? otherSubstanceEdit.getText().toString() : "";
                String newText = item.getOtherDrugs() != null ? item.getOtherDrugs() : "";

                if (!currentText.equals(newText)) {
                    otherSubstanceEdit.setText(newText);
                }

                // สร้าง TextWatcher ใหม่
                textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        String updatedText = s.toString();
                        // กันกรณี null
                        if (updatedText == null) {
                            updatedText = "";
                        }
                        item.setOtherDrugs(updatedText);

                        // เรียก listener เพื่อ update ViewModel
                        if (listener != null) {
                            listener.onFrequencySelected(item.getId(), item.getFrequency(), updatedText);
                        }
                    }
                };
                // เพิ่ม TextWatcher ใหม่
                otherSubstanceEdit.addTextChangedListener(textWatcher);
            } else {
                otherSubstanceLayout.setVisibility(View.GONE);
            }
        }

        private int getRadioIdForValue(int value) {
            switch (value) {
                case 0: return R.id.radioNever;
                case 6: return R.id.radioWithin;
                case 3: return R.id.radioBefore;
                default: return -1;
            }
        }
        private int getValueForRadioId(int radioId) {
            if (radioId == R.id.radioNever) return 0;
            if (radioId == R.id.radioWithin) return 6;
            if (radioId == R.id.radioBefore) return 3;
            return 0;
        }

    }
}
