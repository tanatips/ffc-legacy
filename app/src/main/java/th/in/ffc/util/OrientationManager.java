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

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;

public class OrientationManager {

    public static final String PREFS_NAME = "OrientationManger";
    public static final String REQUEST_ORIENTATION = "request";
    public static final String TAG = "OrienMng";

    public static void setOrientation(Activity act) {

        int request = OrientationManager.getRequest(act);
        //Log.d(OrientationManager.TAG, OrientationManager.TAG +" request = "+ request);

        //set Orientation of act Activity
        act.setRequestedOrientation(request);

        //Release all pointer
        act = null;
    }

    public static void setRequest(Activity act, int orientation) {
        //Save request orientation code to sharedPreferences
        SharedPreferences prefsOrientation = act.getSharedPreferences(
                OrientationManager.PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefsOrientation.edit();
        editor.putInt(OrientationManager.REQUEST_ORIENTATION, orientation);
        editor.commit();
        Log.d(OrientationManager.TAG, OrientationManager.TAG + " commit = " + orientation);

        //Refresh Activity Orientation for make sure user do not forget
        //to refresh their page's Orientation;
        OrientationManager.setOrientation(act);

        //Release all pointer
        prefsOrientation = null;
        editor = null;
        act = null;
    }

    public static int getRequest(Activity act) {
        //Read orientation code from sharedPreferences
        SharedPreferences prefsOrientation = act.getSharedPreferences(
                OrientationManager.PREFS_NAME, Context.MODE_PRIVATE);
        int request = prefsOrientation.getInt(
                OrientationManager.REQUEST_ORIENTATION,
                ActivityInfo.SCREEN_ORIENTATION_SENSOR);

        prefsOrientation = null;
        act = null;

        return request;
    }

    public static int getCurrentOrientation(Activity act) {
        int orientation = act.getResources().getConfiguration().orientation;
        orientation = (orientation == Configuration.ORIENTATION_PORTRAIT) ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        act = null;
        return orientation;
    }

    public static void lockScreenOrientation(Activity act) {
        int current = OrientationManager.getCurrentOrientation(act);
        act.setRequestedOrientation(current);
        act = null;
    }

    public static void unlockScreenOrientation(Activity act) {
        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        act = null;
    }
}
