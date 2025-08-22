package th.in.ffc.app.form.screening.adapter;

import android.text.Editable;
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

    /**
     * อัปเดตรายการสารเสพติดที่แสดงใน Adapter
     * เรียกใช้เมื่อมีการเปลี่ยนแปลงในคำถามที่ 1
     */
    public void updateSubstanceList(ArrayList<SubstanceItem> newSubstanceList) {
        Log.d("SubstanceTwoAdapter", "Updating substance list. Old size: " + substanceList.size() +
                ", New size: " + newSubstanceList.size());

        // บันทึกรายการเดิมสำหรับเปรียบเทียบ
        List<SubstanceItem> oldList = new ArrayList<>(substanceList);

        // อัปเดตรายการใหม่
        this.substanceList.clear();
        this.substanceList.addAll(newSubstanceList);

        // แจ้ง RecyclerView ว่าข้อมูลเปลี่ยนแปลง
        notifyDataSetChanged();

        // Log รายการที่เหลืออยู่
        for (SubstanceItem item : newSubstanceList) {
            Log.d("SubstanceTwoAdapter", "Substance in list: " + item.getId() + " - " + item.getName());
        }
    }

    /**
     * ล้างข้อมูลของสารเสพติดที่ระบุ
     */
    public void clearAnswerForSubstance(String substanceId) {
        Log.d("SubstanceTwoAdapter", "Clearing answer for substance: " + substanceId);

        for (int i = 0; i < substanceList.size(); i++) {
            SubstanceItem item = substanceList.get(i);
            if (item.getId().equals(substanceId)) {
                // รีเซ็ตค่าเป็นค่าเริ่มต้น
                item.setFrequency(-1);
                item.setOtherDrugs("");
                // แจ้ง RecyclerView ให้อัปเดต item นี้
                notifyItemChanged(i);
                Log.d("SubstanceTwoAdapter", "Cleared substance: " + substanceId);
                break;
            }
        }
    }

    /**
     * ล้างข้อมูลทั้งหมด
     */
    public void clearAllData() {
        Log.d("SubstanceTwoAdapter", "Clearing all data");

        for (int i = 0; i < substanceList.size(); i++) {
            SubstanceItem item = substanceList.get(i);
            item.setFrequency(-1);
            item.setOtherDrugs("");
        }

        // แจ้ง RecyclerView ให้อัปเดตทั้งหมด
        notifyDataSetChanged();
        Log.d("SubstanceTwoAdapter", "All data cleared");
    }

    /**
     * ตรวจสอบว่ามีสารเสพติดที่ระบุในรายการหรือไม่
     */
    public boolean hasSubstance(String substanceId) {
        for (SubstanceItem item : substanceList) {
            if (item.getId().equals(substanceId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ดึงข้อมูล SubstanceItem ตาม ID
     */
    public SubstanceItem getSubstanceById(String substanceId) {
        for (SubstanceItem item : substanceList) {
            if (item.getId().equals(substanceId)) {
                return item;
            }
        }
        return null;
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
        private TextInputLayout otherSubstanceLayout;
        private TextInputEditText otherSubstanceEdit;
        private RadioButton radioNever;
        private RadioButton radio1to2;
        private RadioButton radioMonthly;
        private RadioButton radioWeekly;
        private RadioButton radioDaily;

        public FrequencyViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            descriptionText = itemView.findViewById(R.id.descriptionText);
            frequencyGroup = itemView.findViewById(R.id.frequencyGroup);
            otherSubstanceLayout = itemView.findViewById(R.id.otherSubstanceLayout);
            otherSubstanceEdit = itemView.findViewById(R.id.otherSubstanceEdit);
            radioNever = itemView.findViewById(R.id.radioNever);
            radio1to2 = itemView.findViewById(R.id.radio1to2);
            radioMonthly = itemView.findViewById(R.id.radioMonthly);
            radioWeekly = itemView.findViewById(R.id.radioWeekly);
            radioDaily = itemView.findViewById(R.id.radioDaily);

            radioNever.setText(radioNever.getText().toString() + " (" + getFrequencyForRadioId(R.id.radioNever) + " คะแนน)");
            radio1to2.setText(radio1to2.getText().toString() + " (" + getFrequencyForRadioId(R.id.radio1to2) + " คะแนน)");
            radioMonthly.setText(radioMonthly.getText().toString() + " (" + getFrequencyForRadioId(R.id.radioMonthly) + " คะแนน)");
            radioWeekly.setText(radioWeekly.getText().toString() + " (" + getFrequencyForRadioId(R.id.radioWeekly) + " คะแนน)");
            radioDaily.setText(radioDaily.getText().toString() + " (" + getFrequencyForRadioId(R.id.radioDaily) + " คะแนน)");
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
            Log.d("SubstanceTwoAdapter", "bind: " + item.getId() + " frequency: " + frequency);

            // แก้ไขตรงนี้โดยเรียกใช้ getRadioIdForFrequency ที่แก้ไขแล้ว
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

        // แก้ไขเมธอดนี้เพื่อให้แปลงค่า frequency เป็น RadioButton ID ที่ถูกต้อง
        private int getRadioIdForFrequency(int frequency) {
            Log.d("SubstanceTwoAdapter", "getRadioIdForFrequency: " + frequency);
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