/* 
 * MaxProfit Android Project
 *
 * Copyright (C) 2010-2013 Leonidlab
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc.util;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 */
public class HouseNumberInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            Character c = source.charAt(i);

            if (!Character.isDigit(c) && !c.equals('/')) {
                return "";
            }
        }
        return null;
    }

}
