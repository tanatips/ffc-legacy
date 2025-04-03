package th.in.ffc.app.form.screening.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.model.VillagePersonComparison;

public class VillageComparisonAdapter extends RecyclerView.Adapter<VillageComparisonAdapter.ViewHolder> {

    private List<VillagePersonComparison> comparisonList;

    public VillageComparisonAdapter(List<VillagePersonComparison> comparisonList) {
        this.comparisonList = comparisonList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.village_comparison_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VillagePersonComparison comparison = comparisonList.get(position);

        holder.villageNameTextView.setText("หมู่บ้าน: " + comparison.getVillageName() + " (หมู่ " + comparison.getVillageNo() + ")");
        holder.personCountTextView.setText(String.valueOf(comparison.getPersonCount()) + " คน");
        holder.sfPersonCountTextView.setText(String.valueOf(comparison.getSfPersonCount()) + " คน");
        holder.percentageTextView.setText(String.format("%.2f%%", comparison.getPercentage()));

        // กำหนดค่าให้กับ ProgressBar
        holder.coverageProgressBar.setProgress((int) comparison.getPercentage());

        // กำหนดสีให้ percentageTextView ตามเปอร์เซ็นต์
        if (comparison.getPercentage() < 50) {
            holder.percentageTextView.setTextColor(0xFFE53935); // สีแดง
        } else if (comparison.getPercentage() < 80) {
            holder.percentageTextView.setTextColor(0xFFFFA000); // สีส้ม
        } else {
            holder.percentageTextView.setTextColor(0xFF43A047); // สีเขียว
        }
    }

    @Override
    public int getItemCount() {
        return comparisonList != null ? comparisonList.size() : 0;
    }

    public void updateData(List<VillagePersonComparison> newData) {
        this.comparisonList = newData;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView villageNameTextView;
        TextView personCountTextView;
        TextView sfPersonCountTextView;
        TextView percentageTextView;
        ProgressBar coverageProgressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            villageNameTextView = itemView.findViewById(R.id.villageNameTextView);
            personCountTextView = itemView.findViewById(R.id.personCountTextView);
            sfPersonCountTextView = itemView.findViewById(R.id.sfPersonCountTextView);
            percentageTextView = itemView.findViewById(R.id.percentageTextView);
            coverageProgressBar = itemView.findViewById(R.id.coverageProgressBar);
        }
    }
}