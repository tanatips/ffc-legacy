package th.in.ffc.app.form.screening.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;

public class ScreeningExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categories;
    private Map<String, List<String>> subcategories;
    private Map<String, Boolean> completionStatus; // เพิ่มแมพสำหรับเก็บสถานะการกรอกข้อมูล

    // เพิ่มตัวแปรสำหรับเก็บสถานะการแสดงผล
    private boolean showTobaccoScreening = true;
    private boolean showAlcoholScreening = true;

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

    /**
     * เพิ่มเมธอดสำหรับอัปเดตการแสดงผลตามการใช้สารเสพติด
     */
    public void updateMenuVisibility(boolean hasTobaccoUse, boolean hasAlcoholUse) {
        this.showTobaccoScreening = hasTobaccoUse;
        this.showAlcoholScreening = hasAlcoholUse;
        notifyDataSetChanged();
    }

    /**
     * เพิ่มเมธอดสำหรับตรวจสอบว่าควรแสดงรายการหรือไม่
     */
    private boolean shouldShowMenuItem(String menuItem) {
        if (menuItem == null) return false;

        // ตรวจสอบเมนูที่เกี่ยวกับการสูบบุหรี่
        if (menuItem.contains("สูบบุหรี่") || menuItem.contains("ติดบุหรี่") ||
                menuItem.contains("ยาสูบ") || menuItem.contains("บุหรี่")) {
            return showTobaccoScreening;
        }

        // ตรวจสอบเมนูที่เกี่ยวกับการดื่มสุรา
        if (menuItem.contains("สุรา") || menuItem.contains("แอลกอฮอล์") ||
                menuItem.contains("เหล้า") || menuItem.contains("ดื่ม")) {
            return showAlcoholScreening;
        }

        return true; // แสดงรายการอื่นๆ ปกติ
    }

    @Override
    public int getGroupCount() {
        return categories.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String category = categories.get(groupPosition);
        List<String> children = subcategories.get(category);

        if (children == null) return 0;

        // กรองรายการตามสถานะการใช้สารเสพติด
        if ("การคัดกรองสารเสพติด".equals(category)) {
            int count = 0;
            for (String child : children) {
                if (shouldShowMenuItem(child)) {
                    count++;
                }
            }
            return count;
        }

        return children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return categories.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String category = categories.get(groupPosition);
        List<String> children = subcategories.get(category);

        if ("การคัดกรองสารเสพติด".equals(category)) {
            // กรองรายการและส่งคืนตำแหน่งที่ถูกต้อง
            List<String> filteredChildren = new ArrayList<>();
            for (String child : children) {
                if (shouldShowMenuItem(child)) {
                    filteredChildren.add(child);
                }
            }
            if (childPosition < filteredChildren.size()) {
                return filteredChildren.get(childPosition);
            }
        }

        if (childPosition < children.size()) {
            return children.get(childPosition);
        }

        return null;
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

        // ซ่อนรายการที่ไม่ควรแสดง
        if (subcategoryTitle != null && !shouldShowMenuItem(subcategoryTitle)) {
            convertView.setVisibility(View.GONE);
            convertView.setLayoutParams(new AbsListView.LayoutParams(0, 0));
        } else {
            convertView.setVisibility(View.VISIBLE);
            convertView.setLayoutParams(new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
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

    /**
     * เพิ่มเมธอดสำหรับตรวจสอบว่ารายการใดบ้างที่ถูกซ่อน (สำหรับ debugging)
     */
    public List<String> getHiddenItems() {
        List<String> hiddenItems = new ArrayList<>();
        for (String category : categories) {
            List<String> children = subcategories.get(category);
            if (children != null) {
                for (String child : children) {
                    if (!shouldShowMenuItem(child)) {
                        hiddenItems.add(child);
                    }
                }
            }
        }
        return hiddenItems;
    }

    /**
     * เพิ่มเมธอดสำหรับรีเซ็ตการแสดงผลทั้งหมด
     */
    public void resetVisibility() {
        this.showTobaccoScreening = true;
        this.showAlcoholScreening = true;
        notifyDataSetChanged();
    }
}