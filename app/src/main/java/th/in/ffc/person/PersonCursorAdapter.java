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
 * th.in.ffc.security.LoginActivity Project
 *
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * 
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 * 
 */

package th.in.ffc.person;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import th.in.ffc.FamilyFolderCollector;
import th.in.ffc.R;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.widget.HighLightCursorAdapter;

import java.io.File;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector Plus
 */
public class PersonCursorAdapter extends HighLightCursorAdapter {

    /**
     * @param context
     * @param layout
     * @param c
     * @param from
     * @param to
     */
    public PersonCursorAdapter(Context context, int layout, Cursor c,
                               String[] from, int[] to) {
        super(context, layout, c, from, to);

    }

    public static final String PHOTO_TAIL = ".jpg";

    @Override
    public void bindView(View view, Context context, Cursor c) {

        super.bindView(view, context, c);

        ImageView image = (ImageView) view.findViewById(R.id.image);
        if (image != null) {

            String pcucode = c.getString(c.getColumnIndex(Person.PCUPERSONCODE));
            String pid = c.getString(c.getColumnIndex(Person._ID));
            String name = pcucode.concat(pid).concat(PHOTO_TAIL);

            File pic = new File(FamilyFolderCollector.PHOTO_DIRECTORY_PERSON, name);
            if (pic.exists()) {
                Log.d("Person", name);
                image.setImageDrawable(Drawable.createFromPath(pic.getAbsolutePath()));
            } else {
                image.setImageDrawable(mContext.getResources().getDrawable(R.drawable.person_default_background));
            }
        }


    }


}
