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

public class ChargeItemSummaryAdapter extends RecyclerView.Adapter<ChargeItemSummaryAdapter.ViewHolder> {

    private final Context context;
    private List<Map<String, Object>> chargeItemData;
    private final NumberFormat numberFormat;
    private final NumberFormat currencyFormat;
    private final NumberFormat percentFormat;

    public ChargeItemSummaryAdapter(Context context, List<Map<String, Object>> chargeItemData) {
        this.context = context;
        this.chargeItemData = chargeItemData;

        this.numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));

        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        this.currencyFormat.setMaximumFractionDigits(2);
        this.currencyFormat.setMinimumFractionDigits(2);

        this.percentFormat = NumberFormat.getPercentInstance(new Locale("th", "TH"));
        this.percentFormat.setMaximumFractionDigits(1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_charge_item_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> itemData = chargeItemData.get(position);

        // ตั้งค่าข้อมูลที่จะแสดง
        holder.textChargeItem.setText(String.valueOf(itemData.get("chrgitem")));

        // จำนวนรายการ
        int count = (int) itemData.get("count");
        holder.textCount.setText(numberFormat.format(count));

        // จำนวนเงิน
        double amount = (double) itemData.get("amount");
        holder.textAmount.setText(currencyFormat.format(amount).replace("฿", ""));

        // ร้อยละ
        if (itemData.containsKey("percentage")) {
            double percentage = (double) itemData.get("percentage");
            double percentValue = percentage / 100; // แปลงจาก percentage เป็น proportion
            holder.textPercentage.setText(percentFormat.format(percentValue));
        } else {
            holder.textPercentage.setText("-");
        }

        // สีพื้นหลังแบบสลับ
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.list_item_even));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.list_item_odd));
        }
    }

    @Override
    public int getItemCount() {
        return chargeItemData != null ? chargeItemData.size() : 0;
    }

    public void updateData(List<Map<String, Object>> newData) {
        this.chargeItemData = newData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textChargeItem;
        TextView textCount;
        TextView textAmount;
        TextView textPercentage;

        ViewHolder(View itemView) {
            super(itemView);
            textChargeItem = itemView.findViewById(R.id.text_charge_item);
            textCount = itemView.findViewById(R.id.text_count);
            textAmount = itemView.findViewById(R.id.text_amount);
            textPercentage = itemView.findViewById(R.id.text_percentage);
        }
    }
}