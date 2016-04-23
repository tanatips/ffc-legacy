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

package th.in.ffc.app.form;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragment;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since 1.0
 */
public class ViewFormFragment extends FFCFragment {

    protected LinearLayout mForm;
    private TextView mConvertView;
    protected LayoutParams lp;
    protected Context mContext;

    protected static final int PADDING_TOP = 14;
    protected static final int PADDING = 6;
    protected static final int GAP = 2;
    protected static int mTopPadding;
    protected static int mPadding;
    protected static int mGap;
    protected static float mDesity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.default_view_fragment, container, false);
        findFromLayout(view);
        mProgessLayout = (LinearLayout) view.findViewById(android.R.id.progress);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        mDesity = getResources().getDisplayMetrics().density;
        lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        mPadding = (int) (PADDING * mDesity);
        mTopPadding = (int) (PADDING_TOP * mDesity);
        mGap = (int) (GAP * mDesity);
    }

    protected LinearLayout findFromLayout(View parent) {
        return findFormLayout(parent, android.R.id.list);
    }

    protected LinearLayout findFormLayout(View parent, int id) {
        return mForm = (LinearLayout) parent.findViewById(id);
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

    protected TextView addTitle(String title) {
        if (mForm == null || TextUtils.isEmpty(title))
            return null;
        TextView textTitle = new TextView(mContext);
        textTitle.setText(title);
        textTitle.setTextAppearance(getActivity(), R.style.FFC_TextView_Title);
        textTitle.setBackgroundResource(R.drawable.textview_title);
        textTitle.setPadding(mPadding, mTopPadding, mPadding, mPadding);

        mForm.addView(textTitle, lp);

        TextView view = new TextView(mContext);
        view.setText(R.string.not_available);
        view.setTextAppearance(getActivity(), R.style.FFC_TextView_Subject);
        view.setPadding(mPadding, mPadding, mPadding, mPadding);
        mForm.addView(view, lp);
        mConvertView = view;

        return textTitle;
    }

    protected void clearConvertView() {
        mConvertView = null;
    }

    protected TextView addTitle(int title) {
        return addTitle(getString(title));
    }

    protected TextView addSubject(String subject) {
        if (!TextUtils.isEmpty(subject)) {
            boolean createNew = false;
            TextView textSubject = mConvertView;
            mConvertView = null;
            if (textSubject == null) {
                createNew = true;
                textSubject = new TextView(mContext);
            }

            textSubject.setText(subject);
            textSubject.setTextAppearance(getActivity(), R.style.FFC_TextView_Subject);
            textSubject.setPadding(mPadding, mPadding, mPadding, mGap);
            if (createNew)
                mForm.addView(textSubject, lp);
            return textSubject;
        }
        return null;
    }

    protected TextView addSubject(int subject) {
        return addSubject(getString(subject));
    }

    protected TextView addContent(String subject, String content, String subContent) {
        if (mForm == null)
            return null;

        if (TextUtils.isEmpty(content) || content.equalsIgnoreCase("null") || content.trim().length() == 0)
            return null;

        addSubject(subject);


        TextView textContent = mConvertView;
        mConvertView = null;
        boolean createNew = false;
        if (textContent == null) {
            textContent = new TextView(getActivity());
            createNew = true;
        }
        textContent.setText(content);
        textContent.setTextAppearance(getActivity(), R.style.FFC_TextView_Content);
        textContent.setPadding(mPadding, mGap, mPadding, mPadding);
        if (createNew)
            mForm.addView(textContent);

        addSubContent(subContent);

        return textContent;
    }

    protected TextView addSubContent(String subContent) {
        if (mForm == null || TextUtils.isEmpty(subContent))
            return null;

        TextView textContent = new TextView(getActivity());
        textContent.setText(subContent);
        textContent.setTextAppearance(getActivity(), R.style.FFC_TextView_Content_Sub);
        textContent.setPadding(mPadding, 0, mPadding, mPadding);
        mForm.addView(textContent);
        return textContent;
    }

    protected TextView addContent(int subject, String content) {
        return addContent(getString(subject), content, null);
    }


    protected TextView addContent(String subject, int content, int array) {
        return addContent(subject, getResources().getStringArray(array)[content], null);
    }

    protected TextView addContentNonZeroBased(int subject, int content, int array) {
        return addContent(getString(subject), getResources().getStringArray(array)[content], null);
    }

    protected TextView addContent(int subject, int content, int array) {

        return addContent(getString(subject), content, array);
    }

    protected TextView addContentWithUnit(int subject, String content, String unit) {
        if (!TextUtils.isEmpty(content) && content.trim().length() > 0)
            content += " " + unit;
        return addContent(getString(subject), content, null);
    }


    protected void addContentWithColorState(String subject, int content, int array, int midOffset, int highOffset, boolean inverse) {
        if (midOffset > highOffset)
            throw new IndexOutOfBoundsException("midOffset is more than highOffset");

        String[] contentArray = getResources().getStringArray(array);

        TextView text = addContent(subject, contentArray[content], null);

        if (text != null) {
            if (content < midOffset) {
                text.setTextColor(getResources().getColor(!inverse ? R.color.holo_red_light : R.color.holo_green_light));
            } else if (content >= highOffset) {
                text.setTextColor(getResources().getColor(!inverse ? R.color.holo_green_light : R.color.holo_red_light));
            } else {
                //text.setTextColor(getResources().getColor(R.color.holo_orange_light));
            }
        }
    }

    protected void addContentWithColorStateForArrayFormatSpinner(String subject, int content, int array, int midOffset, int highOffset, boolean inverse) {
        if (midOffset > highOffset)
            throw new IndexOutOfBoundsException("midOffset is more than highOffset");

        TextView text = addContentArrayFormat(subject, content + "", array);
        if (text != null) {
            if (content == 9) {
                text.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else if (content < midOffset) {
                text.setTextColor(getResources().getColor(!inverse ? R.color.holo_red_light : R.color.holo_green_light));
            } else if (content >= highOffset) {
                text.setTextColor(getResources().getColor(!inverse ? R.color.holo_green_light : R.color.holo_red_light));
            } else {
                //text.setTextColor(getResources().getColor(R.color.holo_orange_light));
            }
        }
    }

    protected void addContentWithColorState(int subject, int content, int array, int midOffset, int highOffset, boolean inverse) {
        addContentWithColorState(getString(subject), content, array, midOffset, highOffset, inverse);
    }


    protected TextView addContentQuery(int subject, String columnName, Uri uri, String subContent) {
        return addContentQuery(getString(subject), columnName, uri, subContent);
    }

    protected TextView addContentQuery(String subject, String columnName, Uri uri, String subContent) {
        TextView view = null;
        Cursor c = mContext.getContentResolver().query(uri, new String[]{columnName}, null, null, null);
        if (c.moveToFirst()) {
            view = addContent(subject, c.getString(0), subContent);
        }
        c.close();
        return view;
    }

    protected TextView addContentArrayFormat(String subject, String content,
                                             int array) {
        String[] strArray = getResources().getStringArray(array);
        if (!TextUtils.isEmpty(content)) {
            for (int i = 0; i < strArray.length; i++) {
                String item = strArray[i];
                String k = item.substring(0, item.indexOf(":"));
                if (k.equals(content)) {
                    return addContent(subject,
                            item.substring(item.indexOf(":") + 1), null);
                }
            }
        }
        return null;
    }

}
