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

import android.text.TextUtils;
import android.util.Log;

public class ThaiCitizenID {

    public static final String TAG = "ThaiCitID";

    public static boolean Validate(String id) {
        final int[] multiplierTable = {13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        if (id.length() != 13 || !TextUtils.isDigitsOnly(id))
            return false;
        int checkDigit = Character.digit(id.charAt(12), 10),
                sum = 0,
                result = 0;

        for (int i = 0; i < 12; i++) {
            sum += Character.digit(id.charAt(i), 10) * multiplierTable[i];
        }

        result = (11 - (sum % 11)) % 10;
        if (result == checkDigit)
            return true;
        else
            return false;

    }

    public static String parse(String idcard) {
        String CitizenID;
        if (idcard != null) {
            if (idcard.length() >= 13) {
                idcard.trim();
                try {
                    CitizenID = "" + idcard.charAt(0);
                    CitizenID += "-" + idcard.substring(1, 5);
                    CitizenID += "-" + idcard.substring(5, 10);
                    CitizenID += "-" + idcard.substring(10, 12);
                    CitizenID += "-" + idcard.charAt(12);
                    return CitizenID;
                } catch (IndexOutOfBoundsException ex) {
                    ex.printStackTrace();
                    Log.e(ThaiCitizenID.TAG, "idCard error while parsing");
                    return null;
                }
            } else {
                Log.e(ThaiCitizenID.TAG, "idCard lenght is less than 13");
            }
        }
        return null;
    }
}
