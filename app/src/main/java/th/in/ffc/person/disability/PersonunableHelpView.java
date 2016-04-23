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
import th.in.ffc.person.PersonActivity;
import th.in.ffc.person.PersonFragment;
import th.in.ffc.provider.CodeProvider.PersonHelp;
import th.in.ffc.provider.DbOpenHelper;
import th.in.ffc.provider.PersonProvider.PersonColumns;
import th.in.ffc.provider.PersonProvider.PersonunableHelp;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class PersonunableHelpView extends PersonFragment {

    private static final String[] PROJECTION = new String[]{
            PersonunableHelp.HELPCODE,
            PersonunableHelp.PROVIDER,
            PersonunableHelp.DATEHELP
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

        Uri uri = PersonunableHelp.CONTENT_URI;

        ContentResolver cr = getFFCActivity().getContentResolver();

        addTitle(R.string.pu_help);

        Cursor c = cr.query(uri, PROJECTION, "pid=? AND pcucodeperson=?",
                new String[]{getPID(), getPcucodePerson()}, PersonunableHelp.DEFAULT_SORTING);

        if (c.moveToFirst()) {
            mAction = Action.EDIT;
            do {
                addContentQuery(R.string.pu_helpcode, PersonHelp.NAME, Uri.withAppendedPath(PersonHelp.CONTENT_URI, c.getString(0)), null);
                addContentArrayFormat(getString(R.string.pu_provider), c.getString(1), R.array.unableprovider);
                addContent(R.string.pu_datehelp, c.getString(2));
                addSubject(" ");
            } while (c.moveToNext());
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
                intent.addCategory(Category.PERSON_DISABLE);
                intent.addCategory(Category.DISABLE_HELP);
                intent.putExtra(PersonColumns._PID, getPID());
                intent.putExtra(PersonColumns._PCUCODEPERSON, getPcucodePerson());
                PersonActivity.startPersonActivity(getActivity(), intent, getPID(), getPcucodePerson());

                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
