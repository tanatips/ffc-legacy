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

public class BMILevel {

    static final int kFat = 5;
    static final int kNearlyFat = 4;
    static final int kNormal = 3;
    static final int kNearlyThin = 2;
    static final int kThin = 1;

    public static int calculateBMILevel(float weight, float height) {
        int bmiLevelValue;

        double bmi = weight / ((height * 0.01f) * (height * 0.01f));
        if (bmi < 18.5f) {
            bmiLevelValue = kThin;
        } else if (bmi <= 25) {
            bmiLevelValue = kNormal;
        } else {
            bmiLevelValue = kFat;
        }

        return bmiLevelValue;
    }

    public static int calculatePregnancyBMI(float weight, float height) {
        int bmiLevelValue;

        float heightM = height * 0.01f;

        double bmi = (weight * 100) / ((heightM * heightM) * 21);
        if (bmi < 18.5f) {
            bmiLevelValue = kThin;
        } else if (bmi <= 25) {
            bmiLevelValue = kNormal;
        } else {
            bmiLevelValue = kFat;
        }

        return bmiLevelValue;
    }

    public static String MappingBMImeaning(Context context, int BMILevel) {
        int mapped;
        switch (BMILevel) {
            case kFat:
                mapped = R.string.fat;
                break;
            case kThin:
                mapped = R.string.thin;
                break;
            case kNormal:
                mapped = R.string.normal;
                break;
            default:
                mapped = R.string.not_available;
                break;
        }
        return context.getString(mapped);
    }
}
