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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * extended class of BaseAdapter for easy create listAdapter of Activity Intent.
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class IntentBaseAdapter extends BaseAdapter {
    Context mContext;
    Intent mIntent;
    PackageManager pm;
    List<ResolveInfo> IntentList;
    boolean[] mDisable;

    int iTemLayout;
    int ImageId;
    int TextId;

    private boolean showIcon = true;
    private boolean showLable = true;

    /**
     * @param context    to create this adapter
     * @param intent     with Intent-filter for call your wanted activity
     * @param itemLayout ID of base item layout
     * @param ImageId    ID of ImageView in base item adapter will show Activity's Icon
     *                   on this View
     * @param TextId     ID of TextView in base item adapter will show Activity's Label
     *                   on this View
     */
    public IntentBaseAdapter(Context context, Intent intent, int itemLayout,
                             int ImageId, int TextId) {
        mContext = context;
        mIntent = intent;

        pm = mContext.getPackageManager();
        IntentList = pm.queryIntentActivities(mIntent, 0);

        if (IntentList.size() == 0)
            throw new InvalidParameterException(
                    "Not Found any Activity match with parameter Intent.");
        mDisable = new boolean[IntentList.size()];
        this.iTemLayout = itemLayout;
        this.ImageId = ImageId;
        this.TextId = TextId;
    }

    public void setShowIcon(boolean icon) {
        this.showIcon = icon;
    }

    public void setShowIcon(boolean icon, int iconId) {
        this.showIcon = icon;
        this.ImageId = iconId;
    }

    public void setShowLable(boolean lable) {
        this.showLable = lable;
    }

    public void setShowLable(boolean lable, int lableId) {
        this.showLable = lable;
        this.TextId = lableId;
    }

    public void setIntent(Intent intent) {
        IntentList = pm.queryIntentActivities(mIntent, 0);

        if (IntentList.size() == 0)
            throw new InvalidParameterException(
                    "Not Found any Activity match with parameter Intent.");
    }

    @Override
    public int getCount() {
        return IntentList.size();
    }

    @Override
    public Object getItem(int position) {
        return IntentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDisable(int position) {
        mDisable[position] = true;
        this.notifyDataSetChanged();
    }

    public void setDisable(int... position) {
        for (int pos : position) {
            mDisable[pos] = true;
        }
        this.notifyDataSetChanged();
    }

    public class ViewHolder {
        ImageView image;
        TextView title;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(iTemLayout, null);
            ViewHolder holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(this.ImageId);
            holder.title = (TextView) view.findViewById(this.TextId);
            view.setTag(holder);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        ResolveInfo info = IntentList.get(position);

        // set intent ICON
        if (this.showIcon) {
            holder.image.setImageDrawable(info.loadIcon(this.pm));
        }
        // set intent LABLE
        if (this.showLable) {
            holder.title.setText(info.loadLabel(this.pm));
        }

        if (mDisable[position]) {
            view.setVisibility(View.GONE);
        }

        return view;
    }

    public void close() {
        mContext = null;
        mIntent = null;
        pm = null;
        IntentList = null;
    }

    /**
     * Get OnItemClickListener that will automatic start Activity that user
     * click. you can custom it by implement interface
     * IntentBaseAdapter.OnItemClickListener.
     *
     * @param listener manual implement or null for default implement
     * @return AdapterView.OnItemClickListener for use with setAdapter()
     */
    public AdapterView.OnItemClickListener getOnItemClickListener(
            OnItemClickListener listener) {
        DefaultOnItemClick click = new DefaultOnItemClick(mContext, IntentList);
        if (listener != null)
            click.setListener(listener);
        return click;
    }

    /**
     * Get OnItemClickListener that will automatic start Activity that user
     * click.
     *
     * @return default implemented AdapterView.OnItemClickListener for use with
     * setAdapter()
     */
    public AdapterView.OnItemClickListener getOnItemClickListener() {
        return this.getOnItemClickListener(null);
    }

    /**
     * Default implement class of OnItemClickListener
     *
     * @author Piruin Panichphol
     * @version 1.0
     * @since Family Folder Collector 2.0
     */
    private static class DefaultOnItemClick implements
            AdapterView.OnItemClickListener {

        Context mContext;
        List<ResolveInfo> mIntentList;
        OnItemClickListener mCustomListener;

        public DefaultOnItemClick(Context context, List<ResolveInfo> intentList) {
            mContext = context;
            mIntentList = intentList;
        }

        public void setListener(OnItemClickListener listener) {
            mCustomListener = listener;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            ResolveInfo info = mIntentList.get(position);
            String name = info.activityInfo.name;
            String packageName = info.activityInfo.packageName;

            if (mCustomListener != null)
                mCustomListener.onItemClick(parent, view, position, id, name,
                        packageName);
            else {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, name));
                mContext.startActivity(intent);
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id, String name, String packageName);
    }

}
