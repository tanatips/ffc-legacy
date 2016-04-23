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

import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.app.FFCActionBarTabsPagerActivity;
import th.in.ffc.building.house.HouseListFragment;
import th.in.ffc.person.PersonListFragment;
import th.in.ffc.security.LoginFragment;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class TestActionBarTabPager extends FFCActionBarTabsPagerActivity {

    ActionBarTabPagersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.default_actionbar_tabs_pager);

        adapter = new ActionBarTabPagersAdapter(this,
                getSupportFragmentManager(),
                getSupportActionBar(),
                getViewPager());

        adapter.addTab("Person", PersonListFragment.class, null);
        adapter.addTab("Login", LoginFragment.class, null);
        adapter.addTab("House", HouseListFragment.class, null);

        this.setTabPagerAdapter(adapter);
        getViewPager().setCurrentItem(1);
    }

}
