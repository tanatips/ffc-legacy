package th.in.ffc.app.form.screening.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import th.in.ffc.R;

import th.in.ffc.app.form.nhso.model.NHSOCHAInfo;

public class ClaimAdapter extends RecyclerView.Adapter<ClaimAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private List<NHSOCHAInfo> originalClaimList;
    private List<NHSOCHAInfo> filteredClaimList;
    private final OnItemClickListener listener;

    private final SimpleDateFormat displayDateFormat;
    private final NumberFormat currencyFormat;

    private String filterText = "";
    private String filterType = "all";

    // เพิ่ม parameter สำหรับการกรอง
    private String currentFilterType = "ทั้งหมด";
    private String currentFilterText = "";

    public interface OnItemClickListener {
        void onItemClick(NHSOCHAInfo claim, int position);
    }

    public ClaimAdapter(Context context, List<NHSOCHAInfo> claimList, OnItemClickListener listener) {
        this.context = context;
        this.originalClaimList = claimList;
        this.filteredClaimList = new ArrayList<>(claimList);
        this.listener = listener;

        this.displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("th", "TH"));
        this.currencyFormat.setMaximumFractionDigits(2);
        this.currencyFormat.setMinimumFractionDigits(2);
    }
//    public ClaimAdapter(Context context, List<NHSOCHAInfo> claimList, OnItemClickListener listener) {
//        this.context = context;
//        this.originalClaimList = claimList;
//        this.filteredClaimList = new ArrayList<>(claimList);
//        this.listener = listener;
//    }
    public void setFilter(String filterType, String filterText) {
        this.currentFilterType = filterType;
        this.currentFilterText = filterText;
        getFilter().filter(filterText);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_monthly_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NHSOCHAInfo claim = filteredClaimList.get(position);

        // ตั้งค่าวันที่
        if (claim.getDate() != null) {
            holder.textDate.setText(displayDateFormat.format(claim.getDate()));
        } else {
            holder.textDate.setText("-");
        }

        // ตั้งค่ารายการ
        holder.textChargeItem.setText(claim.getChrgitem() != null ? claim.getChrgitem() : "-");

        // ตั้งค่าเลขที่ใบแจ้งหนี้
        holder.textInvoiceNo.setText(claim.getInvoiceNo() != null ? claim.getInvoiceNo() : "-");

        // ตั้งค่าจำนวนเงิน
        double amount = claim.getAmount() != null ? claim.getAmount() : 0.0;
        holder.textAmount.setText(currencyFormat.format(amount).replace("฿", ""));

        // ตั้งค่าการคลิก
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(claim, holder.getAdapterPosition());
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
        return filteredClaimList.size();
    }

    public void updateData(List<NHSOCHAInfo> newData) {
        this.originalClaimList = newData;
        getFilter().filter(filterText);
    }

    public void setFilterType(String filterType) {
        this.currentFilterType = filterType;
        // เมื่อเปลี่ยนประเภท ให้กรองด้วยข้อความค้นหาปัจจุบัน
        getFilter().filter(currentFilterText);
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterText = constraint != null ? constraint.toString().toLowerCase() : "";
                String filterType = currentFilterType;

                List<NHSOCHAInfo> filteredList = new ArrayList<>();

                for (NHSOCHAInfo claim : originalClaimList) {
                    // ตรวจสอบว่าตรงกับข้อความค้นหาหรือไม่
                    boolean matchesText = filterText.isEmpty() ||
                            (claim.getChrgitem() != null && claim.getChrgitem().toLowerCase().contains(filterText)) ||
                            (claim.getInvoiceNo() != null && claim.getInvoiceNo().toLowerCase().contains(filterText));

                    // ตรวจสอบว่าตรงกับประเภทที่เลือกหรือไม่
                    boolean matchesType;

                    // ถ้าเลือก "ทั้งหมด" ให้แสดงทุกรายการ
                    if ("ทั้งหมด".equals(filterType)) {
                        matchesType = true;
                    } else {
                        // กรองเฉพาะตาม invoice number เท่านั้น ไม่ใช้ chrgitem
                        matchesType = (claim.getInvoiceNo() != null && filterType.equals(claim.getInvoiceNo()));
                    }

                    // เพิ่มรายการที่ตรงกับเงื่อนไขทั้งหมดลงในลิสต์ผลลัพธ์
                    if (matchesText && matchesType) {
                        filteredList.add(claim);
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredClaimList = (List<NHSOCHAInfo>) results.values;
                notifyDataSetChanged();
            }
        };
    }
  static class ViewHolder extends RecyclerView.ViewHolder {
      TextView textDate;
      TextView textChargeItem;
      TextView textInvoiceNo;
      TextView textAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textDate = itemView.findViewById(R.id.text_date);
            textChargeItem = itemView.findViewById(R.id.text_charge_item);
            textInvoiceNo = itemView.findViewById(R.id.text_invoice_no);
            textAmount = itemView.findViewById(R.id.text_amount);
        }
    }
}
