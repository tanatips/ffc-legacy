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
import th.in.ffc.R;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.util.DateTime.Date;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AgeCalculator {

    public static final int max_year = 12;
    private int[] mDayOfMonth;

    private Date current, born, age;


    public AgeCalculator(Date current, Date born) {

        int february = LeapDay.february(current.year);
        this.mDayOfMonth = new int[]{31, february, 31, 30, 31, 30, 31, 31,
                30, 31, 30, 31};
        this.current = current;
        this.born = born;
    }

    public Date calulate() {
        if (born == null || current == null)
            throw new NullPointerException("Born or Current is Null");

        age = new Date();
        // Case: 0
        if ((born.year > current.year)
                || (born.year == current.year && born.month > current.month)
                || (born.year == current.year && born.month > current.month && born.day > current.day)) {
            // Do Nothing
        }
        // Case:1
        else if ((current.month == born.month) && (current.day == born.day)) {
            age.year = current.year - born.year;
            age.month = 0;
            age.day = 0;
        }
        // Case:2
        else if ((current.month == born.month) && (current.day > born.day)) {
            age.year = current.year - born.year;
            age.month = 0;
            age.day = current.day - born.day;
        }
        // Case:3
        else if ((current.month == born.month) && (current.day < born.day)) {
            age.year = current.year - born.year - 1;
            age.month = max_year - (current.month - born.month) - 1;
            age.day = mDayOfMonth[current.month - 1] - (born.day - current.day);
        }
        // Case:4
        else if ((current.month > born.month) && (current.day == born.day)) {
            age.year = current.year - born.year;
            age.month = current.month - born.month;
            age.day = 0;
        }
        // Case:5
        else if ((current.month > born.month) && (current.day > born.day)) {
            age.year = current.year - born.year;
            age.month = current.month - born.month;
            age.day = current.day - born.day;
        }
        // Case:6
        else if ((current.month > born.month) && (current.day < born.day)) {
            age.year = current.year - born.year;
            age.month = (current.month - born.month - 1) % mDayOfMonth.length;
            age.day = mDayOfMonth[current.month - 1] - (born.day - current.day);
        }
        // Case:7
        else if ((current.month < born.month) && (current.day == born.day)) {
            age.year = current.year - born.year - 1;
            age.month = max_year - (born.month - current.month);
            age.day = 0;
        }
        // Case:8
        else if ((current.month < born.month) && (current.day > born.day)) {
            age.year = current.year - born.year - 1;
            age.month = max_year - (born.month - current.month);
            age.day = current.day - born.day;
        }
        // Case:9
        else if ((current.month < born.month) && (current.day < born.day)) {
            age.year = current.year - born.year - 1;
            age.month = max_year - (born.month - current.month) - 1;
            age.day = mDayOfMonth[current.month - 1] - (born.day - current.day);
        }

        return age;
    }

    @Override
    public String toString() {
        return age.year + "  " + age.month + " " + age.day + " ";
    }

    public static String toAgeFormat(Context context, Date date) {

        return date.year + " " + context.getString(R.string.year) + " "
                + date.month + " " + context.getString(R.string.month) + " "
                + date.day + " " + context.getString(R.string.day);
    }

    public static int calculateAge(String birthDateStr) {
        try {
            // แปลง string เป็น Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date birthDate = sdf.parse(birthDateStr);

            // หาวันที่ปัจจุบัน
            Calendar today = Calendar.getInstance();
            Calendar birthDay = Calendar.getInstance();
            birthDay.setTime(birthDate);

            // คำนวณอายุ
            int age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);

            // ตรวจสอบว่าผ่านวันเกิดปีนี้หรือยัง
            if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            return age;

        } catch (Exception e) {
            // กรณีที่ format วันที่ไม่ถูกต้อง
            return -1;
        }
    }

    /**
     * Calculate service cost based on age and service type
     *
     * @param age Person's age
      * @return Service cost in Thai Baht
     */
    public static double calculateServiceCost(int age,double fpg, double cholesterol) {
        // Service costs based on the images provided
        if (fpg == 0 && cholesterol ==0 && age >= 15 && age <= 34) {
            return 100.0; // ค่าบริการเหมาจ่าย 100 บาท สำหรับการคัดกรองฯ อายุ 15-34 ปี
        } else if (fpg == 0 && cholesterol ==0 && age >= 35 && age <= 59) {
            return 150.0; // ค่าบริการเหมาจ่าย 150 บาท สำหรับการคัดกรองฯ อายุ 35-59 ปี
        } else if (fpg>0 && cholesterol == 0 && age >= 35 && age <= 59) {
            return 40.0; // ค่าบริการเหมาจ่าย 40 บาท สำหรับตรวจ FPG อายุ 35-59 ปี
        } else if (fpg == 0 && cholesterol>0 && age >= 45 && age <= 59) {
            return 160.0; // ค่าบริการเหมาจ่าย 160 บาท สำหรับตรวจ Cholesterol อายุ 45-59 ปี
        } else {
            return 0.0; // Service not applicable for the given age
        }
    }
}
