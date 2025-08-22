package th.in.ffc.util;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Utility class สำหรับคำนวณและตั้งค่าความสูงของ Content Layout
 * ใช้สำหรับ Fragment ต่างๆ ในระบบ Screening
 */
public class ContentHeightCalculator {

    private static final String TAG = "ContentHeightCalculator";

    // ค่าเริ่มต้นสำหรับการคำนวณ
    private static final int DEFAULT_ITEM_HEIGHT_DP = 60;
    private static final int DEFAULT_MIN_HEIGHT_DP = 200;
    private static final int DEFAULT_MAX_HEIGHT_DP = 600;
    private static final double DEFAULT_HEIGHT_MULTIPLIER = 6.1;

    /**
     * คำนวณและตั้งค่าความสูงของ Content Layout แบบพื้นฐาน
     *
     * @param recyclerView RecyclerView ที่ต้องการคำนวณ
     * @param contentLayout Layout ที่ต้องการตั้งค่าความสูง
     * @param resources Resources สำหรับดึง DisplayMetrics
     */
    public static void calculateAndSetContentHeight(RecyclerView recyclerView,
                                                    ViewGroup contentLayout,
                                                    Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                DEFAULT_ITEM_HEIGHT_DP, DEFAULT_MIN_HEIGHT_DP,
                DEFAULT_MAX_HEIGHT_DP, DEFAULT_HEIGHT_MULTIPLIER);
    }

    /**
     * คำนวณและตั้งค่าความสูงของ Content Layout แบบกำหนดค่าเอง
     *
     * @param recyclerView RecyclerView ที่ต้องการคำนวณ
     * @param contentLayout Layout ที่ต้องการตั้งค่าความสูง
     * @param resources Resources สำหรับดึง DisplayMetrics
     * @param itemHeightDp ความสูงประมาณการต่อ item (dp)
     * @param minHeightDp ความสูงขั้นต่ำ (dp)
     * @param maxHeightDp ความสูงสูงสุด (dp)
     * @param heightMultiplier ตัวคูณสำหรับความสูงสุดท้าย
     */
    public static void calculateAndSetContentHeight(RecyclerView recyclerView,
                                                    ViewGroup contentLayout,
                                                    Resources resources,
                                                    int itemHeightDp,
                                                    int minHeightDp,
                                                    int maxHeightDp,
                                                    double heightMultiplier) {
        if (recyclerView == null || contentLayout == null || resources == null) {
            return;
        }

        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }

        // คำนวณความสูงตามจำนวน items
        int itemCount = adapter.getItemCount();
        int estimatedItemHeight = (int) (itemHeightDp * resources.getDisplayMetrics().density);
        int totalHeight = itemCount * estimatedItemHeight;

        // บวกเพิ่ม padding
        totalHeight += recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();

        // กำหนดความสูงขั้นต่ำและสูงสุด
        int minHeight = (int) (minHeightDp * resources.getDisplayMetrics().density);
        int maxHeight = (int) (maxHeightDp * resources.getDisplayMetrics().density);
        totalHeight = Math.max(minHeight, Math.min(totalHeight, maxHeight));

        // กำหนดความสูงให้กับ contentLayout
        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        params.height = (int) Math.round(totalHeight * heightMultiplier);
        contentLayout.setLayoutParams(params);

        android.util.Log.d(TAG, "Content height calculated: " + params.height +
                " (items: " + itemCount + ", base height: " + totalHeight + ")");
    }

    /**
     * คำนวณความสูงสำหรับ Question One (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionOneHeight(RecyclerView recyclerView,
                                                  ViewGroup contentLayout,
                                                  Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                50, 180, 500, 5.8);
    }

    /**
     * คำนวณความสูงสำหรับ Question Two (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionTwoHeight(RecyclerView recyclerView,
                                                  ViewGroup contentLayout,
                                                  Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                65, 220, 650, 6.2);
    }

    /**
     * คำนวณความสูงสำหรับ Question Three (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionThreeHeight(RecyclerView recyclerView,
                                                    ViewGroup contentLayout,
                                                    Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                58, 200, 600, 6.0);
    }

    /**
     * คำนวณความสูงสำหรับ Question Four (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionFourHeight(RecyclerView recyclerView,
                                                   ViewGroup contentLayout,
                                                   Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                60, 200, 600, 6.1);
    }

    /**
     * คำนวณความสูงสำหรับ Question Five (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionFiveHeight(RecyclerView recyclerView,
                                                   ViewGroup contentLayout,
                                                   Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                62, 210, 620, 6.15);
    }

    /**
     * คำนวณความสูงสำหรับ Question Six (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionSixHeight(RecyclerView recyclerView,
                                                  ViewGroup contentLayout,
                                                  Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                55, 190, 580, 5.9);
    }

    /**
     * คำนวณความสูงสำหรับ Question Seven (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionSevenHeight(RecyclerView recyclerView,
                                                    ViewGroup contentLayout,
                                                    Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                60, 200, 600, 6.1);
    }

    /**
     * คำนวณความสูงสำหรับ Question Eight (ใช้ค่าเฉพาะ)
     */
    public static void calculateQuestionEightHeight(RecyclerView recyclerView,
                                                    ViewGroup contentLayout,
                                                    Resources resources) {
        calculateAndSetContentHeight(recyclerView, contentLayout, resources,
                70, 150, 400, 5.5);
    }

    /**
     * ตรวจสอบว่า RecyclerView และ Adapter พร้อมใช้งานหรือไม่
     */
    public static boolean isRecyclerViewReady(RecyclerView recyclerView) {
        return recyclerView != null && recyclerView.getAdapter() != null;
    }

    /**
     * คำนวณความสูงแบบ Custom สำหรับกรณีพิเศษ
     */
    public static void calculateCustomHeight(ViewGroup contentLayout,
                                             Resources resources,
                                             int itemCount,
                                             int itemHeightDp,
                                             int paddingDp,
                                             double multiplier) {
        if (contentLayout == null || resources == null) {
            return;
        }

        float density = resources.getDisplayMetrics().density;
        int totalHeight = (int) ((itemCount * itemHeightDp + paddingDp) * density);

        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        params.height = (int) Math.round(totalHeight * multiplier);
        contentLayout.setLayoutParams(params);

        android.util.Log.d(TAG, "Custom height calculated: " + params.height +
                " (items: " + itemCount + ", multiplier: " + multiplier + ")");
    }
    /**
     * คำนวณและตั้งค่าความสูงของ Content Layout โดยวัดจากขนาดจริงของ RecyclerView
     * ไม่ต้องกำหนด itemHeightDp, minHeightDp, maxHeightDp หรือ heightMultiplier
     */
    public static void calculateAutoHeight(final RecyclerView recyclerView,
                                                      final ViewGroup contentLayout) {
        if (recyclerView == null || contentLayout == null) {
            return;
        }

        recyclerView.post(() -> {
            RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (adapter == null || layoutManager == null) {
                return;
            }

            int itemCount = adapter.getItemCount();
            if (itemCount == 0) {
                return;
            }

            int totalHeight = 0;

            // วัดเฉพาะ child views ที่ถูกแสดงจริง
            for (int i = 0; i < layoutManager.getChildCount(); i++) {
                View child = layoutManager.getChildAt(i);
                if (child != null) {
                    totalHeight += child.getMeasuredHeight();
                }
            }

            // ถ้าแสดงไม่ครบทุก item ให้คำนวณอัตราส่วนและประเมินความสูงรวม
            int visibleCount = layoutManager.getChildCount();
            if (visibleCount > 0 && visibleCount < itemCount) {
                totalHeight = totalHeight * itemCount / visibleCount;
            }

            // บวก padding
            totalHeight += recyclerView.getPaddingTop() + recyclerView.getPaddingBottom();

            // ตั้งค่าความสูงให้ contentLayout เท่ากับความสูง RecyclerView จริง
            ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
            params.height = totalHeight;
            contentLayout.setLayoutParams(params);

            android.util.Log.d(TAG, "Content height from actual views: " + params.height +
                    " (items: " + itemCount + ", visible: " + visibleCount + ")");
        });
    }

}