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

package th.in.ffc.person.disability;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import th.in.ffc.R;
import th.in.ffc.app.FFCTabsPagerActivity;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.util.ThaiCitizenID;

/**
 * add description here! please
 *
 * @author piruin panichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class PersonunableMainActivity extends FFCTabsPagerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        setContentView(R.layout.personunable_main_activity);
        setShowPagerTitleStrip(true);
        setShowTabWidget(false);
        Uri data = getIntent().getData();


        Bundle personArgs = new Bundle();
        personArgs.putString("pcucodeperson", getPcuCode());
        personArgs.putString("pcucode", getPcuCode());
        personArgs.putString("pid", data.getLastPathSegment());

        TabsPagerAdapter adapter = new TabsPagerAdapter(this, getTabHost(), getViewPager());
        adapter.addTab("type", PersonunableTypeView.class, personArgs);
        adapter.addTab("prob", PersonunableProbView.class, personArgs);
        adapter.addTab("need", PersonunableNeedView.class, personArgs);
        adapter.addTab("help", PersonunableHelpView.class, personArgs);
        setTabsPagerAdapter(adapter);

        doSetupActionBar(data.getLastPathSegment());
    }

    private void doSetupActionBar(String pid) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        Uri personUri = Uri.withAppendedPath(Person.CONTENT_URI, pid);

        String[] projection = new String[]{Person.PID, Person.CITIZEN_ID, Person.FULL_NAME};
        Cursor c1 = getContentResolver().query(personUri, projection, null, null, Person.DEFAULT_SORTING);
        if (c1.moveToFirst()) {
            if (!TextUtils.isEmpty(c1.getString(c1.getColumnIndex(Person.FULL_NAME))))
                getSupportActionBar().setTitle(c1.getString(c1.getColumnIndex(Person.FULL_NAME)));
            if (!TextUtils.isEmpty(c1.getString(c1.getColumnIndex(Person.CITIZEN_ID))))
                getSupportActionBar().setSubtitle(ThaiCitizenID.parse(c1.getString(c1.getColumnIndex(Person.CITIZEN_ID))));
        }

    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
