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

import static android.Manifest.permission.MANAGE_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import th.in.ffc.FamilyFolderCollector;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.util.Log;

import java.io.File;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class StateNotFoundDatabase extends FFCFragmentActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS_EXTERNAL_STORAGE = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SDK_INT >= Build.VERSION_CODES.R) {
            checkExternalStoragePermission();
        }
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

    //    @RequiresApi(api = Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.R)
    private boolean checkExternalStoragePermission() {

                int hasWriteStoragePermission = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
                int hasReadStoragePermission = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
                int hasManageStoragePermission = ContextCompat.checkSelfPermission(this, MANAGE_EXTERNAL_STORAGE);
                if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED ||
                        hasReadStoragePermission != PackageManager.PERMISSION_GRANTED ||
                        hasManageStoragePermission != PackageManager.PERMISSION_GRANTED
                ) {
                    if (
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE) ||
                                    !ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE) ||
                                    !ActivityCompat.shouldShowRequestPermissionRationale(this, MANAGE_EXTERNAL_STORAGE)
                    ) {
                        requestPermissions(new String[]{
                                        WRITE_EXTERNAL_STORAGE,
                                        READ_EXTERNAL_STORAGE,
                                        MANAGE_EXTERNAL_STORAGE
                                },
                                REQUEST_CODE_ASK_PERMISSIONS_EXTERNAL_STORAGE);
                        return false;
                    }
                    return false;
                } else return true;

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
