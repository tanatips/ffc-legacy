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

/**
 * utility class for help to easy disable of enable Log
 *
 * @author piruin panichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class Log {

    public static final String TAG = "FFC";
    public static final boolean mDebug = true;

    public static int d(String tag, String msg) {
        if (mDebug)
            return android.util.Log.d(tag, msg);
        else
            return 0;
    }

    public static int e(String tag, String msg) {
        if (mDebug)
            return android.util.Log.e(tag, msg);
        else
            return 0;
    }

    public static int w(String tag, String msg) {
        if (mDebug)
            return android.util.Log.w(tag, msg);
        else
            return 0;
    }

    public static int v(String tag, String msg) {
        if (mDebug)
            return android.util.Log.v(tag, msg);
        else
            return 0;
    }

}
