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
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class ImageFilesAdapter extends BaseAdapter {

    private static final int ITEM_WIDTH = 72;
    private static final int ITEM_HEIGHT = 72;
    private float mDensity;

    private File[] mImages;
    private Context mContext;

    public ImageFilesAdapter(Context context, File[] images) {
        this.mImages = images;
        this.mContext = context;
        this.mDensity = context.getResources().getDisplayMetrics().density;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public Object getItem(int arg0) {

        return Drawable.createFromPath(mImages[arg0].getAbsolutePath());
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            convertView = new ImageView(mContext);

            imageView = (ImageView) convertView;
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new LayoutParams(
                    (int) (ITEM_WIDTH * mDensity + 0.5f),
                    (int) (ITEM_HEIGHT * mDensity + 0.5f)));

            // The preferred Gallery item background

        } else {
            imageView = (ImageView) convertView;
        }

        if (position < mImages.length)
            imageView.setImageDrawable(Drawable.createFromPath(mImages[position].getAbsolutePath()));

        return imageView;
    }

}
