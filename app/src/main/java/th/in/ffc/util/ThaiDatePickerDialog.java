package th.in.ffc.util;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;
import java.util.Locale;

public class ThaiDatePickerDialog extends DatePickerDialog {

    private final Calendar calendar;
    private final int yearOffset = 543;
    private Context context;

    public ThaiDatePickerDialog(@NonNull Context context,
                                @Nullable OnDateSetListener listener,
                                Calendar calendar) {
        super(context, listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        this.context = context;
        this.calendar = calendar;

        // ตั้งค่าช่วงปีที่เลือกได้ (ย้อนหลัง 100 ปี และไปข้างหน้า 100 ปี)
        DatePicker datePicker = getDatePicker();
        Calendar minDate = Calendar.getInstance();
        Calendar maxDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -100);
        maxDate.add(Calendar.YEAR, 100);
        datePicker.setMinDate(minDate.getTimeInMillis());
        datePicker.setMaxDate(maxDate.getTimeInMillis());

        // แปลง Title เป็นปี พ.ศ.
        updateTitle(calendar);
    }

    @Override
    public void onDateChanged(@NonNull DatePicker view, int year, int month, int dayOfMonth) {
        super.onDateChanged(view, year, month, dayOfMonth);
        calendar.set(year, month, dayOfMonth);
        updateTitle(calendar);
    }

    private void updateTitle(Calendar calendar) {
        String title = String.format(new Locale("th", "TH"), "เลือกวันที่ %d %s %d",
                calendar.get(Calendar.DAY_OF_MONTH),
                getThaiMonth(calendar.get(Calendar.MONTH)),
                calendar.get(Calendar.YEAR) + yearOffset);
        setTitle(title);
    }

    private String getThaiMonth(int month) {
        String[] THAI_MONTHS = new String[]{
                "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน",
                "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",
                "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
        };
        return THAI_MONTHS[month];
    }

    public static class Builder {
        private final Context context;
        private OnDateSetListener listener;
        private Calendar calendar;

        public Builder(Context context) {
            this.context = context;
            this.calendar = Calendar.getInstance();
        }

        public Builder setDate(int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);
            return this;
        }

        public Builder setListener(OnDateSetListener listener) {
            this.listener = listener;
            return this;
        }

        public ThaiDatePickerDialog build() {
            if (listener == null) {
                listener = (view, year, month, dayOfMonth) -> {
                    // Default listener - do nothing
                };
            }
            return new ThaiDatePickerDialog(context, listener, calendar);
        }
    }
}