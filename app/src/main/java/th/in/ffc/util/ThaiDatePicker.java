/* ***********************************************************************
 *                                                                 _ _ _
 *                                                               ( _ _  |
 *                                                           _ _ _ _  | |
 *                                                          (_ _ _  | |_|
 *  _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | | 
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (    
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _ 
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _| 
 *  a member of NSTDA, @Thailand
 *  
 * ***********************************************************************
 *
 *
 * FFC-Plus Project
 *
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc.util;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import th.in.ffc.util.DateTime.Date;

import java.util.Locale;

public class ThaiDatePicker extends LinearLayout {

    Spinner mSpnYear;
    Spinner mSpnMonth;
    Spinner mSpnDay;
    Context mContext;

    public static final int START_YEAR = 2400;
    public static final int YEAR_LENGHT = 300;

    private static final int[] mDayOfMonth = {31, 28, 31, 30, 31, 30, 31, 31,
            30, 31, 30, 31};

    private static final String[] mThaiMonthArray = {"มกราคม", "กุมภาพันธ์",
            "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน", "กรกฎาคม", "สิงหาคม",
            "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม",};

    private static final String[] mEngMonthArray = {"January", "February",
            "March", "April", "May", "June", "July", "August", "September",
            "October", "November", "December",};

    private static Integer[] mThaiYearArray;
    private static Integer[] mEngYearArray;

    static {
        int lenght = YEAR_LENGHT;
        int startYear = START_YEAR;
        int engStartYear = START_YEAR - 543;
        mThaiYearArray = new Integer[lenght + 1];
        mEngYearArray = new Integer[lenght + 1];
        for (int i = 0; i <= lenght; i++) {
            mThaiYearArray[i] = startYear + i;
            mEngYearArray[i] = engStartYear + i;
        }
    }

    public ThaiDatePicker(Context context) {
        super(context);
        this.addSpinner(context, null);
    }

    public ThaiDatePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.addSpinner(context, null);
    }

    private void addSpinner(Context context, Locale locale) {
        mContext = context;
        mSpnYear = new Spinner(context);
        mSpnMonth = new Spinner(context);
        mSpnDay = new Spinner(context);

        String[] monthArray = mThaiMonthArray;
        Integer[] yearArray = mThaiYearArray;

        // Choose what language to use
        if (locale == null)
            locale = context.getResources().getConfiguration().locale;

        if (locale.toString().contains(Locale.ENGLISH.toString())) {
            monthArray = mEngMonthArray;
            yearArray = mEngYearArray;
        }

        // initialize for day spinner
        LayoutParams IntegerParam = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        this.addView(mSpnDay, IntegerParam);

        // initialize for month spinner
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_item, monthArray);
        monthAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnMonth.setAdapter(monthAdapter);
        this.addView(mSpnMonth); // default param is wrap_content then not need
        // explicit set

        // initialize for year spinner
        if (!locale.toString().contains(Locale.ENGLISH.toString())) {
            TextView textview = new TextView(mContext);
            textview.setText("พ.ศ.");
            this.addView(textview, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(context,
                android.R.layout.simple_spinner_item, yearArray);
        yearAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnYear.setAdapter(yearAdapter);
        this.addView(mSpnYear, IntegerParam);

        mSpnMonth.setOnItemSelectedListener(mYearMonthSelectedListener);
        mSpnYear.setOnItemSelectedListener(mYearMonthSelectedListener);

    }

    public static final int LOCALE_ENGLISH = 0;
    public static final int LOCALE_THAI = 1;

    public void setLocale(int i) {
        this.removeAllViews();
        addSpinner(mContext, Locale.JAPANESE);
    }

    OnItemSelectedListener mYearMonthSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            // TODO get Current select year month then update day spinner
            int month = mSpnMonth.getSelectedItemPosition();
            int th_year = mSpnYear.getSelectedItemPosition() + START_YEAR;
            int en_year = th_year - 543;

            ThaiDatePicker.this.updateDaySpinner(en_year, month);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };

    private synchronized int updateDaySpinner(int year, int month) {

        // Create array list of day of select month and year (for February)
        Integer[] day;
        int lenght = (month == 1) ? LeapDay.february(year) : mDayOfMonth[month];
        day = new Integer[lenght];
        for (int i = 0; i < lenght; i++)
            day[i] = i + 1;

        // Create ArrayAdapter for DaySpinner by array created above
        ArrayAdapter<Integer> dayAdapter = new ArrayAdapter<Integer>(mContext,
                android.R.layout.simple_spinner_item, day);
        dayAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Save last index
        int lastPosition = mSpnDay.getSelectedItemPosition();
        // Set New Adapter
        mSpnDay.setAdapter(dayAdapter);
        // Check last index out of new Adapter bound
        if (lastPosition > day.length - 1)
            lastPosition = day.length - 1;
        // Restore index
        mSpnDay.setSelection(lastPosition);

        if (mDateUpdateListener != null)
            mDateUpdateListener.onDateUpdate(getDate());

        return day.length;
    }

    public void setOnDateUpdateListner(OnDateUpdateListener listener) {
        this.mDateUpdateListener = listener;
    }

    private OnDateUpdateListener mDateUpdateListener;

    public static interface OnDateUpdateListener {
        public void onDateUpdate(Date date);
    }

    private synchronized void updateDaySpinner(int year, int month,
                                               int dayOfMonth) {

        int maxday = this.updateDaySpinner(year, month);

        // update day of month
        if (dayOfMonth > maxday)
            mSpnDay.setSelection(maxday - 1);
        else if (dayOfMonth <= 0)
            mSpnDay.setSelection(0);
        else
            mSpnDay.setSelection(dayOfMonth - 1);
    }

    /**
     * @param year       in eng format
     * @param month      index of month base-zero [0-11]
     * @param dayOfMonth number of day that want to be set
     * @since Family Folder Collector Plus
     */
    public void updateDate(int year, int month, int dayOfMonth) {

        // update year
        int yearIndex = (year + 543) - START_YEAR;

        yearIndex = (yearIndex > YEAR_LENGHT) ? YEAR_LENGHT : yearIndex;
        yearIndex = (yearIndex < 0) ? 0 : yearIndex;
//		if (yearIndex > YEAR_LENGHT || yearIndex < 0)
//			throw new IndexOutOfBoundsException("Update year out of bound ["
//					+ (START_YEAR - 543) + "-"
//					+ (START_YEAR + YEAR_LENGHT - 543) + "]");
//		else
        mSpnYear.setSelection(yearIndex);

        // update month
        // if (month > 11 || month < 0)
        // throw new IndexOutOfBoundsException(
        // "Update month out of bound [0-11]");
        // else
        // mSpnMonth.setSelection(month);

        if (month > 11)
            month = 11;
        if (month < 0)
            month = 0;
        mSpnMonth.setSelection(month);

        // update day of month
        updateDaySpinner(year, month, dayOfMonth);

    }

    public void updateDate(Date date) {
        updateDate(date.year, date.month - 1, date.day);
    }

    /**
     * @return selected year in eng format
     * @since Family Folder Collector Plus
     */
    public int getYear() {
        return mSpnYear.getSelectedItemPosition() + START_YEAR - 543;
    }

    /**
     * @return Selected year in thai format
     * @since Family Folder Collector Plus
     */
    public int getThaiYear() {
        return mSpnYear.getSelectedItemPosition() + START_YEAR;
    }

    /**
     * @return current selected month index [ Jan = 1, Feb = 2 ]
     * @since Family Folder Collector 1.7
     */
    public int getMonth() {
        return mSpnMonth.getSelectedItemPosition() + 1;
    }

    /**
     * @return current selected day of month
     * @since Family Folder Collector Plus
     */
    public int getDayOfMonth() {
        return mSpnDay.getSelectedItemPosition() + 1;
    }

    public void setEnabled(boolean enabled) {
        mSpnDay.setEnabled(enabled);
        mSpnMonth.setEnabled(enabled);
        mSpnYear.setEnabled(enabled);
    }

    public Date getDate() {
        Date date = new Date(getYear(), getMonth(), getDayOfMonth());
        return date;
    }

    @Override
    public String toString() {
        return this.getYear() + "-" + this.getMonth() + "-"
                + this.getDayOfMonth();
    }

    public static class LeapDay {

        public static int february(int year) {
            return LeapDay.isLeapYear(year) ? 29 : 28;
        }

        public static boolean isLeapYear(int year) {
            if ((year % 4 == 0) && ((year % 100 != 0) || (year % 400 == 0)))
                return true; // 29 days
            else
                return false; // 28 days
        }

    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        ss.day = this.getDayOfMonth();
        ss.month = this.getMonth();
        ss.year = this.getYear();

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.updateDate(ss.year, ss.month - 1, ss.day);
    }

    static class SavedState extends BaseSavedState {
        int day;
        int month;
        int year;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.day = in.readInt();
            this.month = in.readInt();
            this.year = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.day);
            out.writeInt(this.month);
            out.writeInt(this.year);
        }

        // required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

}
