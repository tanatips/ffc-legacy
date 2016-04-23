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

package th.in.ffc.widget;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import th.in.ffc.R;

/**
 * add description here!
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since 1.0
 */
public class CursorBaseAdapter extends BaseAdapter {
    private Context mContext;
    protected Cursor mCursor;
    private String[] mFrom;
    private int[] mTo;
    private int mLayout;
    private String mKeyWord;
    private int mColor;

    public CursorBaseAdapter(Context context, int layout, Cursor c,
                             String[] from, int[] to) {
        mContext = context;
        mLayout = layout;
        mCursor = c;

        mFrom = from;
        mTo = to;

        mColor = mContext.getResources().getColor(R.color.holo_green_light);
    }

    @Override
    public int getCount() {
        if (mCursor != null)
            return mCursor.getCount();
        return 0;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    public void swapCursor(Cursor c) {

        swapCursor(c, null);
    }

    public void swapCursor(Cursor c, String keyWord) {
        mKeyWord = keyWord;
        if (!TextUtils.isEmpty(mKeyWord)) {
            mKeyWord = mKeyWord.toLowerCase();
        }

        mCursor = c;

        this.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mLayout, null);
        }

        for (int i = 0; i < mFrom.length; i++) {
            TextView tv = (TextView) convertView.findViewById(mTo[i]);
            if (tv != null) {
                if (mCursor.moveToPosition(position)) {
                    String text = mCursor.getString(mCursor
                            .getColumnIndex(mFrom[i]));

                    TextViewHighLighter.highLight(tv, text, mKeyWord, mColor);

                }
            }
        }

        return convertView;
    }

}
