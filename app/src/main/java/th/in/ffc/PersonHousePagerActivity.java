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

package th.in.ffc;

import android.content.Intent;
import android.os.Bundle;
import com.actionbarsherlock.view.MenuItem;
import th.in.ffc.app.FFCActionBarTabsPagerActivity;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.building.house.HouseListFragment;
import th.in.ffc.person.PersonListFragment;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class PersonHousePagerActivity extends FFCActionBarTabsPagerActivity {

    /**
     * extra for set default start page use AS Integer parameter with PAGE_ constant
     */
    public static final String EXTRA_PAGE = "page";
    public static final int PAGE_PERSON = 0;
    public static final int PAGE_HOUSE = 1;

    ActionBarTabPagersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getString(R.string.app_lable));
        this.setContentView(R.layout.default_actionbar_tabs_pager);

        adapter = new ActionBarTabPagersAdapter(this,
                getSupportFragmentManager(),
                getSupportActionBar(),
                getViewPager());

        adapter.addTab(getString(R.string.person), PersonListFragment.class, null);
        adapter.addTab(getString(R.string.house), HouseListFragment.class, null);

        this.setTabPagerAdapter(adapter);

        int page = getIntent().getIntExtra(EXTRA_PAGE, -1);
        if (page > -1) {
            getViewPager().setCurrentItem(page);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startHomeActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DummyActivity extends FFCFragmentActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            Intent ph = new Intent(this, PersonHousePagerActivity.class);
            ph.putExtra(PersonHousePagerActivity.EXTRA_PAGE,
                    PersonHousePagerActivity.PAGE_HOUSE);
            startActivity(ph);

            DummyActivity.this.finish();
        }
    }

}
