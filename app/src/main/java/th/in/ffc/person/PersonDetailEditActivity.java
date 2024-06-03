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

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.content.CursorLoader;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import th.in.ffc.R;
import th.in.ffc.intent.Action;
import th.in.ffc.provider.CodeProvider;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.provider.PersonProvider.PersonColumns;

/**
 * add description here!
 *
 * @author Piruin Panichphol
 * @version 1.0
 * @since Family Folder Collector plus
 */
public class PersonDetailEditActivity extends PersonActivity {


    List<MyItem> provinces = new ArrayList<>();
    List<MyItem> districts = new ArrayList<>();
    List<MyItem> tambons = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getAction().equals(Action.INSERT))
            setCheckData(false);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);


        setSupportProgressBarIndeterminateVisibility(false);

        setContentView(R.layout.default_scrollable_activity);

        Bundle args = new Bundle();
        args.putString(PersonColumns._PCUCODEPERSON, getPcucodePerson());
        args.putString(PersonColumns._PID, getPid());
        args.putString("action", getIntent().getAction());
        args.putString(Person.HCODE, getIntent().getStringExtra(Person.HCODE));

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment pd = fm.findFragmentByTag("detail");
        if (pd == null) {
            pd = Fragment.instantiate(this, PersonDetailEditFragment.class.getName(), args);
            ft.add(R.id.content, pd, "detail");
            ft.commit();
        }
        loadProvince();
        loadDistrict();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                doSave();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void doSave() {
        String action = getIntent().getAction();
        FragmentManager fm = getSupportFragmentManager();
        PersonDetailEditFragment f = (PersonDetailEditFragment) fm.findFragmentByTag("detail");
        if (f != null) {
            EditTransaction et = beginTransaction();
            if (f.onSave(et)) {
                if (action.equals(Action.INSERT)) {
                    et.getContentValues().put(Person.FAMILY_NO, 1);
                    et.getContentValues().put(Person.FAMILY_POSITION, 0);
                    Uri uri = et.commit(Person.CONTENT_URI);
                    Log.d(TAG, uri.toString());

                } else if (action.equals(Action.EDIT)) {
                    String[] selectionArgs = new String[]{
                            getPid(),
                            getPcucodePerson(),
                    };
                    String selection = "pid=? AND pcucodeperson=?";
                    int update = et.commit(Person.CONTENT_URI, selection, selectionArgs);
                    Log.d(TAG, "update=" + update);
                }
                this.finish();
            } else {

            }

        }
    }

    public interface Saveable {
        /**
         * @param et
         * @return true if all data OK, false if something error
         * @since Family Folder Collector Plus
         */
        public boolean onSave(EditTransaction et);
    }

    void loadProvince(){
        String selection = null;
        String[] selectionArgs = null;
        Cursor c =getApplication().getContentResolver()
                .query(CodeProvider.Province.CONTENT_URI,
                        new String[]{
                                CodeProvider.Province.PROVCODE,
                                 CodeProvider.Province.NAME}, selection, selectionArgs,
                        CodeProvider.Province.DEFAULT_SORTING);

        if (c.moveToFirst()) {
            do {
                String code = c.getString(c.getColumnIndexOrThrow(CodeProvider.Province.PROVCODE));
                String name = c.getString(c.getColumnIndexOrThrow(CodeProvider.Province.NAME));
                Log.d("province", "procode: " + code + ", name: " + name );
                provinces.add(new MyItem(code,name));
            }  while (c.moveToNext());
        }
    }
    void loadDistrict(){
        String selection = null;
        String[] selectionArgs = null;
        Cursor c =getApplication().getContentResolver()
                .query(CodeProvider.District.CONTENT_URI,
                        new String[]{
                                CodeProvider.District.DISTCODE,
                                CodeProvider.District.NAME}, selection, selectionArgs,
                        CodeProvider.District.DEFAULT_SORTING);

        if (c.moveToFirst()) {
            do {
                String code = c.getString(c.getColumnIndexOrThrow(CodeProvider.District.DISTCODE));
                String name = c.getString(c.getColumnIndexOrThrow(CodeProvider.District.NAME));
                Log.d("district", "distcode: " + code + ", name: " + name );
                districts.add(new MyItem(code,name));
            }  while (c.moveToNext());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        PersonDetailEditFragment f = (PersonDetailEditFragment) fm.findFragmentByTag("detail");
        if (f != null) {
            if (requestCode == f.SMART_CARD_READER_CODE ) {
                if (resultCode == Activity.RESULT_OK) {
                    byte[] byteArray = data.getByteArrayExtra("image");
                    String strIdcard = data.getStringExtra("result");
                    if (byteArray != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        f.imgPerson.setImageBitmap(bitmap);
                    }
                    if(strIdcard!=null){
                        String[] idcardInfo = strIdcard.split("#");
                        f.citizenId.setText(idcardInfo[0].toString());
                        f.fname.setText(idcardInfo[2].toString());
                        f.lname.setText(idcardInfo[4].toString());
                        f.hno.setText(idcardInfo[9].toString());
                        if(idcardInfo[1].toString().equals("นาย")) {
                            f.sex.findViewById(R.id.male).setActivated(true);
                        } else {
                            f.sex.findViewById(R.id.female).setActivated(true);
                        }
                        String[] prenameArray = getResources().getStringArray(R.array.prename);
                        String defaultValue = idcardInfo[1];
                        int defaultPosition = -1;
                        for (int i = 0; i < prenameArray.length; i++) {
                            if (prenameArray[i].contains(defaultValue)) {
                                defaultPosition = i;
                                break;
                            }
                        }
                        int day,month,year;
                        year = Integer.parseInt(idcardInfo[18].substring(0,4))-543;
                        month = Integer.parseInt(idcardInfo[18].substring(4,6))-1;
                        day = Integer.parseInt(idcardInfo[18].substring(6,8));
                        f.birthday.updateDate(year , month, day);
                        f.prename.setSelection(defaultPosition);

                        for(MyItem province: provinces){
                            if(province.nane.equals(idcardInfo[16])){
                                f.provcode.setSelectionById(province.id);
                                System.out.println("ID: " + province.id + ", Name: " + province.nane);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    class MyItem {
        private String id;
        private String nane;

        MyItem(String id, String nane) {
            this.id = id;
            this.nane = nane;
        }
    }
}
