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
import android.database.Cursor;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import th.in.ffc.R;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class StringArrayUtils {

    public static ArrayAdapter<String> getAdapter(Context context, Cursor c,
                                                  int index) {

        String[] array = getArray(c, index);
        if (array != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                    R.layout.default_spinner_item, array);
            return adapter;
        } else {
            return null;
        }
    }

    public static String[] getArray(Cursor c, int index) {
        String[] array = null;
        if (c.moveToFirst()) {
            array = new String[c.getCount()];
            int count = 0;
            do {
                array[count++] = c.getString(index);

            } while (c.moveToNext());
        }
        return array;
    }

    public static ArrayList<String> getArrayList(Cursor c, int index) {
        ArrayList<String> list = new ArrayList<String>();
        if (c.moveToFirst()) {
            do {
                list.add(c.getString(index));
            } while (c.moveToNext());
        }
        return list;
    }

    public static String getArrayFormat(Context context, int arrayId, String id) {
        String[] array = context.getResources().getStringArray(arrayId);
        if (!TextUtils.isEmpty(id)) {
            for (int i = 0; i < array.length; i++) {
                String item = array[i];
                String k = item.substring(0, item.indexOf(":"));
                if (k.equals(id)) {
                    return item.substring(item.indexOf(":") + 1);
                }
            }
        }
        return null;
    }

    /**
     * @param codeList
     * @param code
     * @return index of code or -1 if not found
     * @since Family Folder Collector Plus
     */
    public static int indexOf(ArrayList<String> codeList, String code) {
        int index = -1;
        for (int i = 0; i < codeList.size(); i++) {

            if (code.equals(codeList.get(i)))
                return i;
        }
        return index;
    }

    public static int extract(String[] array, ArrayList<String> code, ArrayList<String> message) {
        int count = 0;
        for (String row : array) {
            if (!row.matches("\\.*:\\.*")) {
                throw new InvalidParameterException("Array item format incorrect \\.*:\\.*");
            }
            code.add(row.substring(0, row.indexOf(":")));
            message.add(row.substring(row.indexOf(":") + 1));
        }
        return count;
    }


}
