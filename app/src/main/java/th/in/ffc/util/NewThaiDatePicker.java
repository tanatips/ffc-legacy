package th.in.ffc.util;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.DatePicker;

public class NewThaiDatePicker extends DatePicker {
    private int yearOffset = 543; // ส่วนต่างระหว่าง พ.ศ. กับ ค.ศ.

    public NewThaiDatePicker(Context context) {
        super(context);
        init();
    }

    public NewThaiDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NewThaiDatePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // รับช่วงปีเดิม
        int year = getYear();
        // แปลงเป็นปี พ.ศ.
        setYearOffset(year + yearOffset);
    }

    public void setYearOffset(int yearOffset) {
        this.yearOffset = yearOffset-yearOffset;
    }

    @Override
    public int getYear() {
        return super.getYear()+yearOffset;
    }


}