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

/**
 * Adapter สำหรับแสดงสรุปข้อมูลรายการค่าใช้จ่ายตามประเภท
 * รองรับการแสดงจำนวนเงินที่ส่งเบิกและจำนวนเงินที่เบิกได้
 */
public class ChargeItemSummaryAdapter extends RecyclerView.Adapter<ChargeItemSummaryAdapter.ViewHolder> {

    private Context mContext;
    private List<Map<String, Object>> mData;
    private NumberFormat currencyFormat;
    private NumberFormat numberFormat;

    public ChargeItemSummaryAdapter(Context context, List<Map<String, Object>> data) {
        this.mContext = context;
        this.mData = data;

        // เตรียม formatter
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        this.currencyFormat.setMaximumFractionDigits(2);
        this.currencyFormat.setMinimumFractionDigits(2);

        this.numberFormat = NumberFormat.getNumberInstance(new Locale("th", "TH"));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_charge_summary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, Object> item = mData.get(position);

        // ข้อมูลพื้นฐาน
        String chrgitem = (String) item.get("chrgitem");
        int count = (int) item.get("count");
        double chargeAmount = (double) (item.get("amount")!=null? item.get("amount") :0.0);  // จำนวนเงินที่ส่งเบิก
        double claimAmount = item.containsKey("claimAmount") ?
                (double) item.get("claimAmount") : 0.0;           // จำนวนเงินที่เบิกได้
        double percentage = (double) (item.get("percentage")!=null?
                item.get("percentage") : 0.0);             // เปอร์เซ็นต์ที่เบิกได้

        // แสดงข้อมูล
        holder.tvChrgItem.setText(chrgitem != null ? chrgitem : "-");
        holder.tvCount.setText(numberFormat.format(count));

        // จำนวนเงินที่ส่งเบิก
        String formattedChargeAmount = currencyFormat.format(chargeAmount).replace("฿", "");
        holder.tvChargeAmount.setText(formattedChargeAmount);

        // จำนวนเงินที่เบิกได้
        String formattedClaimAmount = currencyFormat.format(claimAmount).replace("฿", "");
        holder.tvClaimAmount.setText(formattedClaimAmount);

        // แสดงเปอร์เซ็นต์ (คำนวณจากจำนวนเงินที่เบิกได้)
        holder.tvPercentage.setText(String.format("%.1f%%", percentage));

        // เปลี่ยนสีตามสถานะ
        if (claimAmount < chargeAmount) {
            // ถ้าเบิกได้น้อยกว่าที่ส่งเบิก แสดงเป็นสีแดง
            holder.tvClaimAmount.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_dark));
        } else if (claimAmount == chargeAmount) {
            // ถ้าเบิกได้เท่ากับที่ส่งเบิก แสดงเป็นสีเขียว
            holder.tvClaimAmount.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            // ถ้าเบิกได้มากกว่าที่ส่งเบิก แสดงเป็นสีน้ำเงิน
            holder.tvClaimAmount.setTextColor(mContext.getResources().getColor(android.R.color.holo_blue_dark));
        }
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    /**
     * อัปเดตข้อมูลใหม่
     */
    public void updateData(List<Map<String, Object>> newData) {
        this.mData = newData;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder สำหรับรายการสรุป
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvChrgItem;      // ประเภทรายการ
        TextView tvCount;         // จำนวนรายการ
        TextView tvChargeAmount;  // จำนวนเงินที่ส่งเบิก
        TextView tvClaimAmount;   // จำนวนเงินที่เบิกได้
        TextView tvPercentage;    // เปอร์เซ็นต์

        ViewHolder(View itemView) {
            super(itemView);
            tvChrgItem = itemView.findViewById(R.id.tv_chrg_item);
            tvCount = itemView.findViewById(R.id.tv_count);
            tvChargeAmount = itemView.findViewById(R.id.tv_charge_amount);
            tvClaimAmount = itemView.findViewById(R.id.tv_claim_amount);
            tvPercentage = itemView.findViewById(R.id.tv_percentage);
        }
    }
}