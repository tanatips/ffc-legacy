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
import android.text.TextUtils;
import th.in.ffc.R;

import java.util.Calendar;

/**
 * Util class for handle about FFC date & time
 *
 * @author Piruin Panichphol
 * @version 1.51
 * @since Family Folder Collector Plus
 */
public class DateTime {

    private static final int[] mDayOfMonth = {31, 28, 31, 30, 31, 30, 31, 31,
            30, 31, 30, 31};

    public static String getCurrentDate() {
        try {
            Calendar current = Calendar.getInstance();
            int currentYear = current.get(Calendar.YEAR);
            int currentMonth = current.get(Calendar.MONTH) + 1;
            int currentDay = current.get(Calendar.DAY_OF_MONTH);
            String month = (currentMonth < 10) ? "0" + currentMonth : ""
                    + currentMonth;
            String day = (currentDay < 10) ? "0" + currentDay : "" + currentDay;
            return currentYear + "-" + month + "-" + day;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getCurrentTime() {
        try {
            Calendar current = Calendar.getInstance();
            int currentHour = current.get(Calendar.HOUR_OF_DAY);
            int currentMinute = current.get(Calendar.MINUTE);
            int currentSecond = current.get(Calendar.SECOND);
            String Hour = (currentHour < 10) ? "0" + currentHour : ""
                    + currentHour;
            String Minute = (currentMinute < 10) ? "0" + currentMinute : ""
                    + currentMinute;
            String Second = (currentSecond < 10) ? "0" + currentSecond : ""
                    + currentSecond;
            return Hour + ":" + Minute + ":" + Second + ".0";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

    public static String getCurrentDateTime() {
        String datetime = getCurrentDate() + " " + getCurrentTime();
        return datetime;

    }

    public static String getMonthName(Context context, int monthIndex) {
        if (monthIndex < 1 || monthIndex > 12)
            throw new IndexOutOfBoundsException();
        String[] month = context.getResources().getStringArray(R.array.month);
        return month[monthIndex - 1];
    }

    public static String getFullFormat(Context context, String date) {
        Date d = Date.newInstance(date);
        return getFullFormatThai(context, d);
    }

    public static String getFullFormatThai(Context context, String date) {
        Date d = Date.newInstance(date);
        return getFullFormatThai(context, d);
    }

    public static String getFullFormatThai(Context context, Date d) {
        if (d != null)
            return d.day + " " + getMonthName(context, d.month) + " "
                    + (d.year + 543);
        return null;
    }

    public static String getFullFormat(Context context, Date d) {
        if (d != null)
            return d.year + " " + getMonthName(context, d.month) + " " + d.day;
        return null;
    }

    /**
     * Object of date use this for replace of Calendar
     *
     * @author Piruin Panichphol
     * @version 1.0
     * @since Family Folder Collector Plus
     */
    public static class Date implements Comparable<Date> {
        public int year = 0, month = 0, day = 0;

        public Date(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }

        public Date() {
            this.year = 0;
            this.month = 0;
            this.day = 0;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            // TODO Auto-generated method stub
            return super.clone();
        }

        /**
         * return date in format yyyy-mm-dd
         *
         * @since 1.5
         */
        @Override
        public String toString() {
            String month = (this.month < 10) ? "0" + this.month : ""
                    + this.month;
            String day = (this.day < 10) ? "0" + this.day : "" + this.day;
            return this.year + "-" + month + "-" + day;
        }

        /**
         * @param day number of day that want to increase
         * @throws IllegalArgumentException if day < 1
         * @since 1.5
         */
        public void increaseDay(int day) {
            if (day < 1)
                throw new IllegalArgumentException("increase day more than 0");

            int leftDay = getMaxDayOfMonth() - this.day;
            if (day - leftDay <= 0) {
                this.day += day;
            } else {
                this.day = 0;
                increaseMonth(1);
                day -= leftDay;

                while (day - getMaxDayOfMonth() > 0) {
                    day -= getMaxDayOfMonth();
                    increaseMonth(1);
                }
                this.day += day;
            }
        }

        /**
         * @param month number of month that want to increase
         * @throws IllegalArgumentException if month < 1
         * @since 1.5
         */
        public void increaseMonth(int month) {
            if (month < 1)
                throw new IllegalArgumentException(
                        "increase month must more than 0");

            int monthIndex = this.month - 1;
            monthIndex = monthIndex + month;
            if (monthIndex > 11) {
                int incYear = monthIndex / 11;
                this.year += incYear;
                monthIndex = (monthIndex - incYear) % 11;
            }
            this.month = monthIndex + 1;
        }

        /**
         * @return get possible day of Date month
         * @since 1.5
         */
        public int getMaxDayOfMonth() {
            int maxDay = 0;
            if (this.month == 2) {
                maxDay = LeapDay.february(this.year);
            } else {
                maxDay = DateTime.mDayOfMonth[this.month - 1];
            }
            return maxDay;
        }

        /**
         * @param date String of date to create Date Object [yyyy-mm-dd]
         * @return Date object
         * @since 1.0
         */
        public static Date newInstance(String date) {
            if (TextUtils.isEmpty(date))
                return null;
            int day = Integer.parseInt(date.substring(8, 10));
            int month = Integer.parseInt(date.substring(5, 7));
            int year = Integer.parseInt(date.substring(0, 4));
            return new Date(year, month, day);

        }

        /**
         * @param another Date to compare must newer than this caller date
         * @return distance of day to another Date
         * @since 1.25
         */
        public int distanceTo(Date another) {
            if (this.compareTo(another) != LESS_THAN)
                return 0;
            Date distance = new AgeCalculator(another, this).calulate();
            int dist = 0;
            dist += (distance.year * 365);
            dist += (distance.month * 30);
            dist += distance.day;
            return dist;

        }

        public static final int MORETHAN = 1;
        public static final int LESS_THAN = -1;
        public static final int EQUAL = 0;

        @Override
        public int compareTo(Date another) {
            if (this.year == another.year) {
                if (this.month == another.month) {
                    if (this.day == another.day)
                        return EQUAL;
                    else if (this.day > another.day)
                        return MORETHAN;
                    else
                        return LESS_THAN;
                } else if (this.month > another.month) {
                    return MORETHAN;
                } else {
                    return LESS_THAN;
                }
            } else if (this.year > another.year) {
                return MORETHAN;
            } else {
                return LESS_THAN;
            }
        }
    }
}
