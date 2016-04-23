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
import th.in.ffc.FamilyFolderCollector;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;

import java.io.File;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class StateNotFoundDatabase extends FFCFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnableAutoReLogin(false);
        setEnableCheckMediaState(true);
        setEnableCheckDatabase(false);

        setContentView(R.layout.state_database);
    }

    @Override
    protected void onResume() {
        super.onResume();
        File userDb = new File(FamilyFolderCollector.PATH_USER_DATABASE);
        File dataDb = new File(FamilyFolderCollector.PATH_ENCRYPTED_DATABASE);
        if (userDb.exists() && dataDb.exists()) {
            this.finish();
        }

    }


    @Override
    public void onBackPressed() {
        File userDb = new File(FamilyFolderCollector.PATH_USER_DATABASE);
        File dataDb = new File(FamilyFolderCollector.PATH_ENCRYPTED_DATABASE);
        if (userDb.exists() && dataDb.exists()) {
            this.finish();
        }
    }

}
