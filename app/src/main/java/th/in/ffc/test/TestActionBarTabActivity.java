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
import th.in.ffc.app.FFCActionBarTabsActivity;
import th.in.ffc.app.FFCActionBarTabsActivity.ActionBarTabsAdapter.TabInfo;
import th.in.ffc.building.house.HouseListFragment;
import th.in.ffc.person.PersonListFragment;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class TestActionBarTabActivity extends FFCActionBarTabsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        ActionBarTabsAdapter adapter = new ActionBarTabsAdapter(this.getSupportActionBar());
        adapter.addTab(new TabInfo(PersonListFragment.class, "PERSON", null));
        adapter.addTab(new TabInfo(HouseListFragment.class, "HOUSE", null));

        super.setTabAdapter(adapter);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onSearchRequested() {
        // TODO Auto-generated method stub
        int index = getSupportActionBar().getSelectedNavigationIndex();
        if (index == 0) {
            PersonListFragment fg = (PersonListFragment) getSupportFragmentManager().findFragmentByTag("PERSON");
            fg.onSearchRequest();
        }
        return super.onSearchRequested();
    }


}
