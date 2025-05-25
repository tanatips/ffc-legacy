package th.in.ffc.app.form.screening.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.listener.OnSubstanceSelectionListener;
import th.in.ffc.app.form.screening.model.AnswerData;
import th.in.ffc.app.form.screening.model.SubstanceItem;

public class SubstanceOneAdapter extends RecyclerView.Adapter<SubstanceOneAdapter.SubstanceViewHolder> {
    private List<SubstanceItem> substanceList;
    private OnSubstanceSelectionListener listener;

    private boolean isUpdating = false;
    private RecyclerView recyclerView; // เพิ่มตัวแปรนี้


    public SubstanceOneAdapter(List<SubstanceItem> substanceList, OnSubstanceSelectionListener listener) {
        this.listener = listener;
        this.substanceList = substanceList;
    }
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView; // เก็บ reference ไว้
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }
    public void updateAnswers(Map<String, AnswerData> answers) {
        if (isUpdating) return;
        isUpdating = true;

        try {
            for (SubstanceItem item : substanceList) {
                AnswerData answer = answers.get(item.getId());
                if (answer != null) {
                    // อัพเดต state ของ item โดยตรง
                    item.setHasUsed(answer.isHasUsed());
                    item.setOtherDrugs(answer.getOtherDrugs());
                }
            }
            notifyDataSetChanged();
            requestLayout();

        } finally {
            isUpdating = false;
        }
    }
    // เพิ่มเมธอดใหม่สำหรับบังคับให้ RecyclerView วัดขนาดใหม่
    public void requestLayout() {
        if (recyclerView != null) {
            recyclerView.post(() -> {
                // บังคับให้วัดขนาดแต่ละ item ใหม่

                for (int i = 0; i < getItemCount(); i++) {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    if (viewHolder != null) {
                        View itemView = viewHolder.itemView;
                        itemView.requestLayout();
                    }
                }

                // บังคับให้ RecyclerView วัดขนาดใหม่
                recyclerView.requestLayout();

                // อาจเพิ่ม callback เพื่อแจ้ง Fragment ว่ามีการเปลี่ยนแปลงขนาด
                if (listener instanceof RecyclerViewLayoutChangeListener) {
                    ((RecyclerViewLayoutChangeListener) listener).onRecyclerViewLayoutChanged();
                }
            });
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
    // Interface สำหรับแจ้งเมื่อ RecyclerView เปลี่ยนขนาด (เพิ่มใหม่)
    public interface RecyclerViewLayoutChangeListener {
        void onRecyclerViewLayoutChanged();
    }
    class SubstanceViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText;
        private TextView descriptionText;
        private RadioGroup radioGroup;
        private TextInputLayout otherSubstanceLayout; // เพิ่ม
        private TextInputEditText otherSubstanceEdit; // เพิ่ม

        private TextWatcher textWatcher; // เพิ่มตัวแปรนี้เพื่อเก็บ reference

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
            if(item.isHasUsed()!=null){
                radioGroup.check(item.isHasUsed() ? R.id.radioUsed : R.id.radioNotUsed);
            }
            // ตั้งค่า listener
            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (!isUpdating) {
                    boolean isUsed = checkedId == R.id.radioUsed;
                    item.setHasUsed(isUsed);
                    if (listener != null) {
                        listener.onAnswerChanged(item.getId(), isUsed, item.getOtherDrugs());
                    }
                    requestLayout(); // ขอให้ปรับขนาดเมื่อมีการเปลี่ยนแปลง
                }
            });

            if (item.getId().equals("j")) {
                otherSubstanceLayout.setVisibility(View.VISIBLE);
                // ลบ TextWatcher เดิมก่อน
                if (textWatcher != null) {
                    otherSubstanceEdit.removeTextChangedListener(textWatcher);
                }
                otherSubstanceEdit.removeTextChangedListener(textWatcher); // ลบ listener เดิมก่อน
                otherSubstanceEdit.setText(item.getOtherDrugs());

                // เพิ่มบรรทัดนี้เพื่อให้แน่ใจว่า EditText สามารถรับ input ได้
                otherSubstanceEdit.setEnabled(true);
                otherSubstanceEdit.setFocusable(true);
                otherSubstanceEdit.setFocusableInTouchMode(true);

                // เตรียม requestFocus เมื่อคลิก
                otherSubstanceEdit.setOnClickListener(v -> {
                    otherSubstanceEdit.requestFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(otherSubstanceEdit, InputMethodManager.SHOW_IMPLICIT);
                });

                // สร้าง TextWatcher ใหม่
                textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!isUpdating) {
                            String newText = s.toString();
                            item.setOtherDrugs(newText);
                            // เพิ่มการเรียก listener
                            if (listener != null) {
                                listener.onAnswerChanged(item.getId(), item.isHasUsed(), newText);
                            }
                            requestLayout(); // ขอให้ปรับขนาด
                        }
                    }
                };
                otherSubstanceEdit.addTextChangedListener(textWatcher);


            } else {
                otherSubstanceLayout.setVisibility(View.GONE);
            }
        }
    }
}

