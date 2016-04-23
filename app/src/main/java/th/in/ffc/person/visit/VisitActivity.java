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

package th.in.ffc.person.visit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.app.form.EditFormActivity;
import th.in.ffc.provider.PersonProvider.PersonColumns;
import th.in.ffc.provider.PersonProvider.Visit;

/**
 * VisitActivity extends EditFormActivity
 * <p/>
 * Base EditActivity for Visit that
 * automatic check all require parameter
 * <p/>
 * getVisitNo()
 * getPcucodePerson()
 * getPid();
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class VisitActivity extends EditFormActivity {

    private String mPid;
    private String mPcucodePerson;
    private String mVN;

    private boolean mRetriveVisit = true;
    private boolean mRetrivePerson = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mRetriveVisit) {
            Uri data = getIntent().getData();
            if (data != null)
                mVN = data.getLastPathSegment();

            if (TextUtils.isEmpty(mVN))
                throw new IllegalStateException("VisitNo was null");
        }
        if (mRetrivePerson) {
            mPid = getIntent().getStringExtra(PersonColumns._PID);
            mPcucodePerson = getIntent().getStringExtra(
                    PersonColumns._PCUCODEPERSON);

            if (TextUtils.isEmpty(mPid) || TextUtils.isEmpty(mPcucodePerson))
                throw new IllegalStateException("pid or pcucodeperson was null");
        }
    }

    public void setRetriveVisitData(boolean enable) {
        mRetriveVisit = enable;
    }

    public void setRetrivePersonData(boolean enable) {
        mRetrivePerson = enable;
    }

    public void startVisitActivityForResult(Intent intent, int requestCode) {
        if (!TextUtils.isEmpty(mVN))
            intent.setData(Uri.withAppendedPath(Visit.CONTENT_URI, mVN));
        intent.putExtra(PersonColumns._PID, mPid);
        intent.putExtra(PersonColumns._PCUCODEPERSON, mPcucodePerson);
        startActivityForResult(intent, requestCode);
    }

    public void startVisitActivity(Intent intent) {
        if (!TextUtils.isEmpty(mVN))
            intent.setData(Uri.withAppendedPath(Visit.CONTENT_URI, mVN));
        intent.putExtra(PersonColumns._PID, mPid);
        intent.putExtra(PersonColumns._PCUCODEPERSON, mPcucodePerson);
        startActivity(intent);
    }

    public String getVisitNo() {
        return mVN;
    }

    public void setVisitNo(String vn) {
        if (!TextUtils.isEmpty(vn))
            this.mVN = vn;
    }

    public String getPid() {
        return mPid;
    }

    public String getPcucodePerson() {
        return mPcucodePerson;
    }

    public static void startVisitActivity(FFCFragmentActivity activity,
                                          Intent intent, String vn, String pid, String pcuCodePerson) {
        if (!TextUtils.isEmpty(vn))
            intent.setData(Uri.withAppendedPath(Visit.CONTENT_URI, vn));
        intent.putExtra(PersonColumns._PID, pid);
        intent.putExtra(PersonColumns._PCUCODEPERSON, pcuCodePerson);
        activity.startActivity(intent);
    }

    public static void startVisitActivityForResult(FFCFragmentActivity activity,
                                                   Intent intent, int requestCode, String vn, String pid,
                                                   String pcuCodePerson) {
        if (!TextUtils.isEmpty(vn))
            intent.setData(Uri.withAppendedPath(Visit.CONTENT_URI, vn));
        intent.putExtra(PersonColumns._PID, pid);
        intent.putExtra(PersonColumns._PCUCODEPERSON, pcuCodePerson);
        activity.startActivityForResult(intent, requestCode);
    }
}
