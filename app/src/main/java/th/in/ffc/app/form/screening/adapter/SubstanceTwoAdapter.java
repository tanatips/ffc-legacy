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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerData;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.SubstanceItem;
import th.in.ffc.util.Log;

public class SubstanceTwoAdapter extends RecyclerView.Adapter<SubstanceTwoAdapter.FrequencyViewHolder> {
    private List<SubstanceItem> substanceList;
    private OnFrequencySelectedListener listener;
    private boolean isUpdating = false;

    // เพิ่ม TextWatcher เป็น field เพื่อให้สามารถถอดออกได้
    private TextWatcher textWatcher;

    public SubstanceTwoAdapter(ArrayList<SubstanceItem> substanceList, OnFrequencySelectedListener listener) {
        this.substanceList = substanceList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FrequencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_substance_problem, parent, false);
        return new FrequencyViewHolder(view);
    }

    // แก้ไขเมธอด updateAnswers ในคลาส SubstanceTwoAdapter
    // เพิ่มเมธอด updateAnswers ที่รับพารามิเตอร์ excludeId
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

    // ปรับปรุงเมธอด updateAnswers เดิมให้เรียกใช้เมธอดใหม่
    public void updateAnswers(Map<String, AnswerFrequencyData> answers) {
        updateAnswers(answers, null);
    }
    @Override
    public void onBindViewHolder(@NonNull FrequencyViewHolder holder, int position) {
        SubstanceItem item = substanceList.get(position);
        holder.bind(item);
    }
    @Override
    public int getItemCount() {
        return substanceList.size();
    }

    class FrequencyViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private RadioGroup frequencyGroup;

        private TextInputLayout otherSubstanceLayout; // เพิ่ม
        private TextInputEditText otherSubstanceEdit; // เพิ่ม

        public FrequencyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            frequencyGroup = itemView.findViewById(R.id.frequencyGroup);
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

            // ล้างและตั้งค่า OnCheckedChangeListener ก่อน
            frequencyGroup.setOnCheckedChangeListener(null);
            frequencyGroup.clearCheck();

            // ตั้งค่าการเลือกตามค่าที่มีอยู่
            int frequency = item.getFrequency();
            Log.d("SubstanceTwoAdapter", "bind: " + item.getId() + " " + frequency);
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

            if (item.getId().equals("j")) {
                otherSubstanceLayout.setVisibility(View.VISIBLE);

                // ลบ TextWatcher เดิมก่อนตั้งค่า text ใหม่
                if (textWatcher != null) {
                    otherSubstanceEdit.removeTextChangedListener(textWatcher);
                }

                // Log เพื่อตรวจสอบค่า otherDrugs
                Log.d("SubstanceTwoAdapter", "Item j otherDrugs: " + item.getOtherDrugs());

                // ตั้งค่าข้อความโดยไม่กระทบ cursor position
                // เช็คว่าข้อความเปลี่ยนหรือไม่ก่อนเรียก setText
                // เพิ่มเช็คว่า null หรือไม่
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

                        // อัพเดตค่าใน SubstanceItem
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
        private int getRadioIdForFrequency(int frequency) {
            switch (frequency) {
                case 0: return R.id.radioNever;
                case 2: return R.id.radio1to2;
                case 3: return R.id.radioMonthly;
                case 4: return R.id.radioWeekly;
                case 6: return R.id.radioDaily;
                default: return -1;
            }
        }

        private int getFrequencyForRadioId(int radioId) {
            if (radioId == R.id.radioNever) return 0;
            if (radioId == R.id.radio1to2) return 2;
            if (radioId == R.id.radioMonthly) return 3;
            if (radioId == R.id.radioWeekly) return 4;
            if (radioId == R.id.radioDaily) return 6;
            return 0;
        }
    }
}
