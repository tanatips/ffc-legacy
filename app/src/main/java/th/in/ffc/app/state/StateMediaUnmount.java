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

package th.in.ffc.app.state;

import android.os.Bundle;
import android.os.Environment;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class StateMediaUnmount extends FFCFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnableAutoReLogin(false);
        setEnableCheckDatabase(false);
        setEnableCheckMediaState(false);

        setContentView(R.layout.state_media);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sdcardstate = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdcardstate)) {
            this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        String sdcardstate = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(sdcardstate)) {
            this.finish();
        }
    }

}
