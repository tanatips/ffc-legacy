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

package th.in.ffc.test;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * add class description here
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since 1.0
 */

public class testTrackingFragment extends FFCFragmentActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FileOutputStream fos = this.openFileOutput("Whatnew.txt",
                    Context.MODE_PRIVATE);
            fos.write("WhatNow".getBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


//		Cursor c = managedQuery(User.CONTENT_URI,new String[] { User.USERNAME } , null, null, null);
//		if(c.moveToFirst()){
//			do{
//				//Log.d("FFC", c.getString(0));	
//
//			}while(c.moveToNext());
//		}


        setContentView(R.layout.main);


    }


    @Override
    public boolean onSearchRequested() {

        ProgressDialog dia = new ProgressDialog(this);
        dia.show();
        return super.onSearchRequested();
    }
}
