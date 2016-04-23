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

package th.in.ffc.person;


import android.content.Context;
import android.text.TextUtils;
import th.in.ffc.R;
import th.in.ffc.util.DateTime.Date;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class PersonUtils {

    public static final int SEX_MALE = 1;
    public static final int SEX_FEMALE = 2;
    public static final int STATUS_SINGLE = 1;

    public static String getPreName(Context context, int sex, Date age,
                                    int marriageStatus) {
        switch (sex) {
            case SEX_MALE:
                if (age.year < 15)
                    return null;// context.getString(R.string.boy);
                else
                    return context.getString(R.string.mister);
            case SEX_FEMALE:
                if (age.year < 15)
                    return null;// context.getString(R.string.girl);
                else if (marriageStatus != STATUS_SINGLE)
                    return context.getString(R.string.missis);
                else
                    return context.getString(R.string.miss);
            default:
                throw new IllegalArgumentException();

        }
    }

    public static String getIncome(Context context, float income) {
        if (income > 0.0f) {
            NumberFormat df = DecimalFormat.getInstance();
            df.setMinimumFractionDigits(2);
            df.setMaximumFractionDigits(2);
            return df.format(income) + " " + (context.getString(R.string.bath));
        } else {
            return null;
        }
    }

    public static String getBlood(String group, String rh) {
        if (TextUtils.isEmpty(group)) {
            return null;
        }
        String RH = null;
        if (!TextUtils.isEmpty(rh)) {
            RH = (rh.equalsIgnoreCase("P") ? "+" : "-");
        } else {
            RH = "";
        }

        String GROUP = null;
        if (group.equalsIgnoreCase("A")) {
            GROUP = "A";
        } else if (group.equalsIgnoreCase("B")) {
            GROUP = "B";
        } else if (group.equalsIgnoreCase("AB")) {
            GROUP = "AB";
        } else {
            GROUP = "O";
        }


        return GROUP + " " + RH;
    }
}
