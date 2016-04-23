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

package th.in.ffc.person;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import th.in.ffc.R;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.provider.DbOpenHelper;
import th.in.ffc.provider.PersonProvider;
import th.in.ffc.provider.PersonProvider.Behavior;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class PersonBehaviorViewFragment extends PersonFragment {

    private static final String[] PROJECTION = new String[]{
            Behavior.ACCIDENT,
            Behavior.EXERCISE,
            Behavior.CIGA,
            Behavior.DRUGBYSELF,
            Behavior.SALT,
            Behavior.SUGAR,
            Behavior.TONIC,
            Behavior.WISKY,
    };

    String mAction;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (DbOpenHelper.isDbExist())
            doShowContent();
    }

    public void doShowContent() {
        mForm.removeAllViewsInLayout();

        Uri uri = Uri.withAppendedPath(PersonProvider.Behavior.CONTENT_URI, getPID());

        ContentResolver cr = getFFCActivity().getContentResolver();

        addTitle(R.string.behavior_data);

        Cursor c = cr.query(uri, PROJECTION, "pcucodeperson=?",
                new String[]{getPcucodePerson()}, Behavior.DEFAULT_SORTING);

        if (c.moveToFirst()) {
            mAction = Action.EDIT;

            addContentWithColorState(R.string.exercise, c.getInt(1), R.array.execise, 1, 3, false);
            addContentWithColorState(R.string.cigarette, c.getInt(2), R.array.ciga, 1, 2, true);
            addContentWithColorState(R.string.wiskey, c.getInt(7), R.array.wiskey, 2, 4, true);
            addContentWithColorState(R.string.tonic, c.getInt(6), R.array.tonic, 1, 2, true);
            addContentWithColorState(R.string.big_accident_ever, c.getInt(0), R.array.no_yes, 0, 1, true);
            addContentWithColorState(R.string.drug_by_self, c.getInt(3), R.array.no_yes, 1, 1, true);
            addContentWithColorState(R.string.sweet, c.getInt(5), R.array.no_yes, 1, 1, true);
            addContentWithColorState(R.string.salt, c.getInt(4), R.array.no_yes, 1, 1, true);
        } else {
            mAction = Action.INSERT;
            addContent(R.string.null_string, getString(R.string.not_available));

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.editable_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:

                Intent intent = new Intent(mAction);
                intent.addCategory(Category.PERSON_BEHAVIOR);
                PersonActivity.startPersonActivity(getActivity(), intent, getPID(), getPcucodePerson());

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
