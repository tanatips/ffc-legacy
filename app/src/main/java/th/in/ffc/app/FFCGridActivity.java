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

package th.in.ffc.app;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.GridView;
import android.widget.ListAdapter;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public abstract class FFCGridActivity extends FFCFragmentActivity {

    private GridView mGrid;
    private ListAdapter mAdapter;

    private boolean settedContentView = false;


    @Override
    public void setContentView(int layoutResId) {
        super.setContentView(layoutResId);
        settedContentView = true;
        mGrid = this.getGridView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        settedContentView = true;
        mGrid = this.getGridView();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        settedContentView = true;
        mGrid = this.getGridView();
    }

    @Override
    protected void onStart() {
        // TODO add default GridView within ScrollView if
        // 		user dosen't already setContentView() at onCreate()
        super.onStart();

        if (!settedContentView) {

            mGrid = this.getGridView();
            if (mAdapter != null)
                mGrid.setAdapter(this.mAdapter);


            setContentView(mGrid);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mAdapter = null;
        mGrid = null;
    }

    public final GridView getGridView() {
        if (mGrid == null) {
            if (!settedContentView)
                //this will happen if user call this method in onCreate for something
                mGrid = new GridView(this);
            else {
                //if contentView was set, find GridView
                mGrid = (GridView) this.findViewById(android.R.id.list);
                if (mGrid == null)
                    throw new IllegalArgumentException("SetContent not have GridView with ID \'@android:id/list\'");
            }
        }
        return mGrid;
    }

    public final void setGridAdapter(ListAdapter adapter) {
        if (mGrid != null)
            this.mGrid.setAdapter(adapter);
        else {
            this.mAdapter = adapter;
        }
    }


}
