package th.in.ffc.app.form.screening.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import th.in.ffc.R;

//import th.in.ffc.app.R;

public class MonthlySummaryAdapter extends RecyclerView.Adapter<MonthlySummaryAdapter.ViewHolder> {

    private final Context context;
    private List<Map<String, Object>> monthlyData;
    private final OnItemClickListener listener;
    private final NumberFormat numberFormat;
    private final NumberFormat currencyFormat;

    public interface OnItemClickListener {
        void onItemClick(Map<String, Object> monthData, int position);
    }

    public MonthlySummaryAdapter(Context context, List<Map<String, Object>> monthlyData, OnItemClickListener listener) {
        this.context = context;
        this.monthlyData = monthlyData;
        this.listener = listener;

        // สร้าง formatter สำหรับแสดงตัวเลข
        this.numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        this.currencyFormat.setMaximumFractionDigits(2);
        this.currencyFormat.setMinimumFractionDigits(2);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monthly_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> monthData = monthlyData.get(position);

        // ตั้งค่าข้อมูลที่จะแสดง
        holder.textMonthName.setText(String.valueOf(monthData.get("monthName")));

        // แสดงจำนวนรายการ
        int totalCount = (int) monthData.get("totalCount");
        holder.textClaimCount.setText(numberFormat.format(totalCount));

        // แสดงจำนวนเงิน
        double totalAmount = (double) monthData.get("totalAmount");
        holder.textAmount.setText(currencyFormat.format(totalAmount).replace("฿", ""));

        // ตั้งค่า clickListener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(monthData, position);
            }
        });

        // กำหนดสีพื้นหลังแบบสลับ
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.list_item_even));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.list_item_odd));
        }
    }

    @Override
    public int getItemCount() {
        return monthlyData != null ? monthlyData.size() : 0;
    }

    public void updateData(List<Map<String, Object>> newData) {
        this.monthlyData = newData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textMonthName;
        TextView textClaimCount;
        TextView textAmount;

        ViewHolder(View itemView) {
            super(itemView);
            textMonthName = itemView.findViewById(R.id.text_month_name);
            textClaimCount = itemView.findViewById(R.id.text_claim_count);
            textAmount = itemView.findViewById(R.id.text_amount);
        }
    }
}
