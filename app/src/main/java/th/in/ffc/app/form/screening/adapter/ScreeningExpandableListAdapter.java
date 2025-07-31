package th.in.ffc.app.form.screening.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.R;

public class ScreeningExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> categories;
    private Map<String, List<String>> subcategories;
    private Map<String, Boolean> completionStatus; // เก็บสถานะการกรอกข้อมูล

    // เพิ่มตัวแปรสำหรับเก็บสถานะการแสดงผล
    private boolean showTobaccoScreening = true;
    private boolean showAlcoholScreening = true;

    // เพิ่มตัวแปรสำหรับเก็บสถานะเงื่อนไข 2Q และอายุ
    private boolean has2QAbnormalResult = false;
    private int personAge = 0;

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
     * เพิ่มเมธอดสำหรับอัปเดตสถานะเงื่อนไข 2Q และอายุ
     */
    public void updateConditionStatus(boolean has2QAbnormalResult, int personAge) {
        this.has2QAbnormalResult = has2QAbnormalResult;
        this.personAge = personAge;
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

    /**
     * ตรวจสอบว่าสามารถเข้าถึงแบบประเมินได้หรือไม่
     */
    private boolean isFormAccessible(String formName) {
        if (formName == null) return true;

        // ตรวจสอบเงื่อนไข 9Q
        if (formName.contains("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม") || formName.contains("9Q")) {
            return has2QAbnormalResult;
        }

        // ตรวจสอบเงื่อนไข 8Q
        if (formName.contains("การประเมินการฆ่าตัวตาย") || formName.contains("8Q")) {
            return (personAge >= 35) || has2QAbnormalResult;
        }

        // ตรวจสอบเงื่อนไขการใช้สารเสพติด
        if (formName.contains("สูบบุหรี่") || formName.contains("ติดบุหรี่") ||
                formName.contains("ยาสูบ") || formName.contains("บุหรี่")) {
            return showTobaccoScreening;
        }

        if (formName.contains("สุรา") || formName.contains("แอลกอฮอล์") ||
                formName.contains("เหล้า") || formName.contains("ดื่ม")) {
            return showAlcoholScreening;
        }

        return true; // แบบประเมินอื่นๆ สามารถเข้าถึงได้
    }

    /**
     * ดึงข้อความเงื่อนไขสำหรับแสดงผล
     */
    private String getConditionText(String formName) {
        if (formName == null) return "";

        // เงื่อนไข 9Q
        if (formName.contains("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม") || formName.contains("9Q")) {
            if (has2QAbnormalResult) {
                return "✅ ต้องทำ (2Q ผิดปกติ)";
            } else {
                return "❌ ไม่ต้องทำ (2Q ปกติ)";
            }
        }

        // เงื่อนไข 8Q
        if (formName.contains("การประเมินการฆ่าตัวตาย") || formName.contains("8Q")) {
            boolean canDo8Q = (personAge >= 35) || has2QAbnormalResult;
            if (canDo8Q) {
                if (personAge >= 35 && has2QAbnormalResult) {
                    return "✅ ต้องทำ (อายุ 35+ และ 2Q ผิดปกติ)";
                } else if (personAge >= 35) {
                    return "✅ ต้องทำ (อายุ 35+)";
                } else if (has2QAbnormalResult) {
                    return "✅ ต้องทำ (2Q ผิดปกติ)";
                }
            } else {
                return "❌ ไม่ต้องทำ (อายุ < 35 และ 2Q ปกติ)";
            }
        }

        // เงื่อนไขการใช้สารเสพติด
        if (formName.contains("สูบบุหรี่") || formName.contains("ติดบุหรี่")) {
            if (!showTobaccoScreening) {
                return "❌ ไม่ต้องทำ (ไม่เคยใช้ยาสูบ)";
            }
        }

        if (formName.contains("สุรา") || formName.contains("แอลกอฮอล์")) {
            if (!showAlcoholScreening) {
                return "❌ ไม่ต้องทำ (ไม่เคยดื่มแอลกอฮอล์)";
            }
        }

        return ""; // ไม่มีเงื่อนไขพิเศษ
    }

    /**
     * จัดการการคลิกรายการที่มีข้อจำกัด
     */
    public boolean handleRestrictedFormClick(String formName, Context context) {
        if (!isFormAccessible(formName)) {
            String message = getRestrictionMessage(formName);
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            return true; // บอกว่าได้จัดการแล้ว
        }
        return false; // ไม่ได้จัดการ ให้ดำเนินการปกติ
    }

    /**
     * ดึงข้อความเตือนสำหรับรายการที่มีข้อจำกัด
     */
    private String getRestrictionMessage(String formName) {
        if (formName == null) return "ไม่สามารถเข้าถึงแบบประเมินนี้ได้";

        if (formName.contains("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม") || formName.contains("9Q")) {
            return "ไม่สามารถทำแบบประเมิน 9Q ได้\n" +
                    "เหตุผล: ผลการประเมิน 2Q เป็น 'ปกติ'\n" +
                    "กรุณาตรวจสอบผลการประเมิน 2Q";
        }

        if (formName.contains("การประเมินการฆ่าตัวตาย") || formName.contains("8Q")) {
            return "ไม่สามารถทำแบบประเมิน 8Q ได้\n" +
                    "เงื่อนไข: อายุ 35+ หรือผล 2Q ผิดปกติ\n" +
                    "สถานะ: อายุ " + personAge + " ปี, 2Q " +
                    (has2QAbnormalResult ? "ผิดปกติ" : "ปกติ");
        }

        if (formName.contains("สูบบุหรี่") || formName.contains("ติดบุหรี่")) {
            return "ไม่สามารถทำแบบประเมินการสูบบุหรี่ได้\n" +
                    "เหตุผล: ท่านเลือก 'ไม่เคย' ใช้ผลิตภัณฑ์ยาสูบ\n" +
                    "ในแบบคัดกรองการใช้สารเสพติด";
        }

        if (formName.contains("สุรา") || formName.contains("แอลกอฮอล์")) {
            return "ไม่สามารถทำแบบประเมินการดื่มสุราได้\n" +
                    "เหตุผล: ท่านเลือก 'ไม่เคย' ดื่มเครื่องดื่มแอลกอฮอล์\n" +
                    "ในแบบคัดกรองการใช้สารเสพติด";
        }

        return "ไม่สามารถเข้าถึงแบบประเมินนี้ได้ในขณะนี้";
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

        // ตรวจสอบว่าสามารถเข้าถึงได้หรือไม่
        boolean isAccessible = isFormAccessible(subcategoryTitle);
        String conditionText = getConditionText(subcategoryTitle);

        // แสดงสถานะการเข้าถึงผ่านสีและ alpha
//        if (!isAccessible) {
//            textViewChild.setTextColor(Color.parseColor("#9E9E9E")); // สีเทา
//            convertView.setAlpha(0.6f); // ทำให้ดูเป็น disabled
//        } else {
//            textViewChild.setTextColor(Color.parseColor("#212121")); // สีปกติ
//            convertView.setAlpha(1.0f);
//        }

        // แสดงข้อความเงื่อนไขใน hint หรือ subtitle (ถ้ามี TextView เพิ่มเติม)
        // สำหรับตอนนี้จะแสดงใน Toast เมื่อคลิก

        // ตรวจสอบสถานะการกรอกข้อมูล
        boolean isCompleted = completionStatus.containsKey(subcategoryTitle) ?
                completionStatus.get(subcategoryTitle) : false;

        // แสดงไอคอนตามสถานะการกรอกข้อมูล
        completionIndicator.setVisibility(View.VISIBLE);

        if (isCompleted) {
            // มีข้อมูลแล้ว - แสดงไอคอนเครื่องหมายถูกสีเขียว
            completionIndicator.setImageResource(R.drawable.ic_check_circle);
//            if (isAccessible) {
                completionIndicator.setColorFilter(Color.parseColor("#4CAF50")); // เขียว
//            } else {
//                completionIndicator.setColorFilter(Color.parseColor("#9E9E9E")); // เทา
//            }
        } else {
            // ยังไม่มีข้อมูล
//            if (isAccessible) {
                completionIndicator.setImageResource(R.drawable.ic_info_circle);
                completionIndicator.setColorFilter(Color.parseColor("#FF9800")); // ส้ม
//            } else {
//                completionIndicator.setImageResource(R.drawable.ic_block);
//                completionIndicator.setColorFilter(Color.parseColor("#F44336")); // แดง
//            }
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
     */
    public void updateCompletionStatus(String formName, boolean isCompleted) {
        if (completionStatus.containsKey(formName)) {
            completionStatus.put(formName, isCompleted);
            notifyDataSetChanged();
        }
    }

    /**
     * อัปเดตสถานะการกรอกข้อมูลสำหรับหลายแบบฟอร์มพร้อมกัน
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
     * ตรวจสอบรายการที่ถูกซ่อน (สำหรับ debugging)
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
     * ตรวจสอบรายการที่ไม่สามารถเข้าถึงได้ (สำหรับ debugging)
     */
    public List<String> getRestrictedItems() {
        List<String> restrictedItems = new ArrayList<>();
        for (String category : categories) {
            List<String> children = subcategories.get(category);
            if (children != null) {
                for (String child : children) {
                    if (shouldShowMenuItem(child) && !isFormAccessible(child)) {
                        restrictedItems.add(child);
                    }
                }
            }
        }
        return restrictedItems;
    }

    /**
     * รีเซ็ตการแสดงผลทั้งหมด
     */
    public void resetVisibility() {
        this.showTobaccoScreening = true;
        this.showAlcoholScreening = true;
        this.has2QAbnormalResult = false;
        this.personAge = 0;
        notifyDataSetChanged();
    }

    /**
     * ดึงข้อมูলสถิติการแสดงผล (สำหรับ debugging)
     */
    public String getDisplayStatistics() {
        int totalItems = 0;
        int hiddenItems = 0;
        int restrictedItems = 0;
        int completedItems = 0;

        for (String category : categories) {
            List<String> children = subcategories.get(category);
            if (children != null) {
                for (String child : children) {
                    totalItems++;

                    if (!shouldShowMenuItem(child)) {
                        hiddenItems++;
                    } else if (!isFormAccessible(child)) {
                        restrictedItems++;
                    }

                    if (completionStatus.containsKey(child) && completionStatus.get(child)) {
                        completedItems++;
                    }
                }
            }
        }

        return "📊 สถิติการแสดงผล:\n" +
                "รายการทั้งหมด: " + totalItems + "\n" +
                "รายการที่ซ่อน: " + hiddenItems + "\n" +
                "รายการที่จำกัดการเข้าถึง: " + restrictedItems + "\n" +
                "รายการที่กรอกข้อมูลแล้ว: " + completedItems + "\n" +
                "สถานะ 2Q: " + (has2QAbnormalResult ? "ผิดปกติ" : "ปกติ") + "\n" +
                "อายุ: " + personAge + " ปี";
    }
}