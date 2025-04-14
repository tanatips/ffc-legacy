package th.in.ffc.app.form.screening.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;

public class ScreeningExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categories;
    private Map<String, List<String>> subcategories;
    private Map<String, Boolean> completionStatus; // เพิ่มแมพสำหรับเก็บสถานะการกรอกข้อมูล

    public ScreeningExpandableListAdapter(Context context, List<String> categories, Map<String, List<String>> subcategories) {
        this.context = context;
        this.categories = categories;
        this.subcategories = subcategories;
        this.completionStatus = new HashMap<>();

        // เริ่มต้นให้ทุกแบบฟอร์มยังไม่ได้กรอกข้อมูล
        for (String category : categories) {
            List<String> subcategoryList = subcategories.get(category);
            if (subcategoryList != null) {
                for (String subcategory : subcategoryList) {
                    completionStatus.put(subcategory, false);
                }
            }
        }
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return subcategories.get(categories.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return subcategories.get(categories.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String categoryTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView textViewGroup = convertView.findViewById(R.id.listGroupTitle);
        ImageView expandIcon = convertView.findViewById(R.id.expandIcon);

        textViewGroup.setText(categoryTitle);

        // ปรับไอคอนการขยาย/ยุบตามสถานะ
        if (isExpanded) {
            expandIcon.setImageResource(R.drawable.ic_expand_less_black);
        } else {
            expandIcon.setImageResource(R.drawable.ic_expand_more_black);
        }

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String subcategoryTitle = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView textViewChild = convertView.findViewById(R.id.listItemTitle);
        ImageView completionIndicator = convertView.findViewById(R.id.completionIndicator);

        textViewChild.setText(subcategoryTitle);

        // ตรวจสอบสถานะการกรอกข้อมูล
        boolean isCompleted = completionStatus.containsKey(subcategoryTitle) ? completionStatus.get(subcategoryTitle) : false;

        // แสดงไอคอนตามสถานะการกรอกข้อมูล
        completionIndicator.setVisibility(View.VISIBLE); // แสดงไอคอนเสมอ

        if (isCompleted) {
            // มีข้อมูลแล้ว - แสดงไอคอนเครื่องหมายถูกสีเขียว
            completionIndicator.setImageResource(R.drawable.ic_check_circle);
        } else {
            // ยังไม่มีข้อมูล - แสดงไอคอนเครื่องหมาย "!"
            completionIndicator.setImageResource(R.drawable.ic_info_circle);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * อัปเดตสถานะการกรอกข้อมูลของแบบฟอร์ม
     * @param formName ชื่อแบบฟอร์ม
     * @param isCompleted สถานะการกรอกข้อมูล (true = กรอกแล้ว, false = ยังไม่กรอก)
     */
    public void updateCompletionStatus(String formName, boolean isCompleted) {
        if (completionStatus.containsKey(formName)) {
            completionStatus.put(formName, isCompleted);
            notifyDataSetChanged();
        }
    }

    /**
     * อัปเดตสถานะการกรอกข้อมูลสำหรับหลายแบบฟอร์มพร้อมกัน
     * @param statusMap แมพของสถานะการกรอกข้อมูล (key = ชื่อแบบฟอร์ม, value = สถานะ)
     */
    public void updateAllCompletionStatus(Map<String, Boolean> statusMap) {
        for (Map.Entry<String, Boolean> entry : statusMap.entrySet()) {
            if (completionStatus.containsKey(entry.getKey())) {
                completionStatus.put(entry.getKey(), entry.getValue());
            }
        }
        notifyDataSetChanged();
    }
}