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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import th.in.ffc.R;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.person.PersonActivity;
import th.in.ffc.person.PersonFragment;
import th.in.ffc.provider.CodeProvider.Diagnosis;
import th.in.ffc.provider.CodeProvider.PersonIncomplete;
import th.in.ffc.provider.DbOpenHelper;
import th.in.ffc.provider.PersonProvider.PersonColumns;
import th.in.ffc.provider.PersonProvider.PersonunableType;
import th.in.ffc.util.DateTime;
import th.in.ffc.util.DateTime.Date;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class PersonunableTypeView extends PersonFragment {

    private static final String[] PROJECTION = new String[]{
            PersonunableType.TYPECODE,
            PersonunableType.UNABLELEVEL,
            PersonunableType.DISABCAUSE,
            PersonunableType.DIAGCAUSE,
            PersonunableType.DATESTART,
            PersonunableType.DATEFOUND,
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

        Uri uri = PersonunableType.CONTENT_URI;

        ContentResolver cr = getFFCActivity().getContentResolver();

        addTitle(R.string.pu_type);

        Cursor c = cr.query(uri, PROJECTION, "pid=? AND pcucodeperson=?",
                new String[]{getPID(), getPcucodePerson()}, PersonunableType.DEFAULT_SORTING);

        if (c.moveToFirst()) {
            mAction = Action.EDIT;
            do {
                addContentQuery(R.string.pu_type, PersonIncomplete.NAME, Uri.withAppendedPath(PersonIncomplete.CONTENT_URI, c.getString(0)), null);
                addContentArrayFormat(getString(R.string.pu_disabcause), c.getString(2), R.array.disabcause);
                if (c.getString(2).equals("3"))
                    addContentQuery(R.string.pu_diag, Diagnosis.NAME_TH, Uri.withAppendedPath(Diagnosis.CONTENT_URI, c.getString(3)), null);
                addContentArrayFormat(getString(R.string.pu_unablelevel), c.getString(1), R.array.unablelevel);

                String sDateStart = c.getString(c.getColumnIndex(PersonunableType.DATESTART));
                Date dDateStart = Date.newInstance(sDateStart);
                addContent(R.string.pu_datestart, DateTime.getFullFormatThai(getActivity(), dDateStart));

                String sDateFound = c.getString(c.getColumnIndex(PersonunableType.DATEFOUND));
                Date dDateFound = Date.newInstance(sDateFound);
                addContent(R.string.pu_datefound, DateTime.getFullFormatThai(getActivity(), dDateFound));

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
                intent.addCategory(Category.DISABLE_TYPE);
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
