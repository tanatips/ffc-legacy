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

package th.in.ffc.widget;

import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

/**
 * add description here!
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since 1.0
 */
public class TextViewHighLighter {

    public static TextView highLight(TextView tv, String text, String keyWord, int color) {
        if (tv == null)
            return null;


        if (!TextUtils.isEmpty(keyWord) && !TextUtils.isEmpty(text)) {
            tv.setText(text, TextView.BufferType.SPANNABLE);
            Spannable spantext = (Spannable) tv.getText();
            String lowerText = text.toLowerCase();
            int start = lowerText.indexOf(keyWord);
            if (start > -1) {
                int end = start + keyWord.length();
                spantext.setSpan(new ForegroundColorSpan(color),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(spantext);
            }
        } else {
            tv.setText(text);
        }
        return tv;
    }
}
