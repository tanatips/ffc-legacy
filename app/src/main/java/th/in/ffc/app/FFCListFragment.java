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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import com.actionbarsherlock.app.SherlockListFragment;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class FFCListFragment extends SherlockListFragment {

    protected static final String TAG = "FFC-listFragment";
    private boolean mRemoved = false;

    /**
     * Like onFinish() in FFCFragmentActivity, this method will be called when
     * fragment removing. should close all connection and set null all object.
     */
    protected void onRemove() {
        mRemoved = true;
    }


    @Override
    public void startActivity(Intent intent) {

        getFFCActivity().startActivity(intent);
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {

        getFFCActivity().startActivityForResult(intent, requestCode);
    }

    public void onSearchRequest() {

    }


    private LinearLayout mProgessLayout;

    public void showProgess(boolean show) {
        if (mProgessLayout != null) {
            if (show)
                mProgessLayout.setVisibility(View.VISIBLE);
            else
                mProgessLayout.setVisibility(View.GONE);
        }
    }

    public void findProgessLayout(View parent) {
        this.findProgessLayout(parent, android.R.id.progress);
    }

    public void findProgessLayout(View parent, int id) {
        mProgessLayout = (LinearLayout) parent.findViewById(id);
    }


    /**
     * this method use to send Refresh command from activity to Fragment overall
     * project
     *
     * @param bundle to argument for fragment
     */
    protected void onRefresh(Bundle bundle) {
        // to do something
    }

    public FFCFragmentActivity getFFCActivity() {
        return (FFCFragmentActivity) getSherlockActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (this.isRemoving()) {
            this.onRemove();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (!mRemoved)
            this.onRemove();
        super.onDestroy();
    }

}
