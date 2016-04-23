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
import android.view.View;
import android.widget.Button;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class StateDateNotUpdate extends FFCFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnableAutoReLogin(false);
        setEnableCheckMediaState(false);
        setEnableCheckDatabase(false);

        setContentView(R.layout.state_date);
        Button pa = (Button) findViewById(R.id.ok);
        pa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StateDateNotUpdate.this.finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        doCheckDateSetting(new OnCheckDateUpdateListener() {

            @Override
            public void onFinish(boolean result) {
                if (result) {
                    StateDateNotUpdate.this.finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }


}
