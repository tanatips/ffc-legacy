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

package th.in.ffc.building.house;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import com.blayzupe.phototaker.PhotoTaker;
import th.in.ffc.FamilyFolderCollector;
import th.in.ffc.R;
import th.in.ffc.app.FFCTabsPagerActivity;
import th.in.ffc.intent.Action;
import th.in.ffc.intent.Category;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.database.DatabaseManager;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.village.spot.Spot;
import th.in.ffc.person.PersonFragment;
import th.in.ffc.person.PersonListFragment;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.provider.HouseProvider.Village;
import th.in.ffc.provider.PersonProvider.Person;
import th.in.ffc.util.SafeImageView; // เพิ่ม import นี้

import java.io.*;
import java.util.Collection;

import th.in.ffc.security.LoginActivity;

/**
 * add description here! please
 *
 * @author piruin panichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class HouseMainActivity extends FFCTabsPagerActivity {

    private static final String TAG = "HouseMainActivity";

    PhotoTaker mPhotoTaker;
    ImageView mImage;
    String mPhotoPath;
    String mPhotoThrumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.house_main_activity);

        setShowPagerTitleStrip(true);
        setShowTabWidget(false);

        Uri data = getIntent().getData();

        Bundle personListArgs = new Bundle();
        personListArgs.putString(PersonFragment.EXTRA_HCODE,
                data.getLastPathSegment());
        personListArgs
                .putBoolean(PersonListFragment.EXTRA_DISABLE_SEARCH, true);

        Bundle houseArgs = new Bundle();
        houseArgs.putString("pcucode", getPcuCode());
        houseArgs.putString("hcode", data.getLastPathSegment());

        TabsPagerAdapter adapter = new TabsPagerAdapter(this, getTabHost(),
                getViewPager());

        adapter.addTab("Person", PersonListFragment.class, personListArgs);
        adapter.addTab("Detail", HouseDetailViewFragment.class, houseArgs);
        adapter.addTab("Food", HouseFoodViewFragment.class, houseArgs);
        adapter.addTab("Sanitation", HouseSanitationViewFragment.class,
                houseArgs);
        adapter.addTab("Carrier", HouseCarrierViewFragment.class, houseArgs);
        adapter.addTab("Water", HouseWaterViewFragment.class, houseArgs);

        setTabsPagerAdapter(adapter);

        doSetupActionBar(data.getLastPathSegment());

        Toast.makeText(HouseMainActivity.this, getPcuCode() + data.getLastPathSegment(), Toast.LENGTH_LONG).show();
        doSetupImage(getPcuCode().concat("h" + data.getLastPathSegment())
                .concat(".jpg"));
        mPhotoThrumb = getPcuCode().concat("h" + data.getLastPathSegment())
                .concat("_thumb.jpg");
    }

    private void doSetupActionBar(String hcode) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri houseUri = Uri.withAppendedPath(House.CONTENT_URI, hcode);

        String[] projection = new String[]{House.HCODE, House.HNO, House.PID, House.VILLCODE};
        Cursor c1 = getContentResolver().query(houseUri, projection, null,
                null, House.DEFAULT_SORTING);
        if (c1.moveToFirst()) {

            String villcode = c1.getString(3);
            Uri villageUri = Uri
                    .withAppendedPath(Village.CONTENT_URI, villcode);
            Cursor villcursor = getContentResolver().query(villageUri,
                    new String[]{Village.VILLNO}, null, null, Village.DEFAULT_SORTING);
            if (villcursor.moveToFirst()) {
                String villno = villcursor.getString(0);
                if (villno.equals("0"))
                    mGenogramable = false;
            }

            TextView code = (TextView) findViewById(R.id.code);
            code.setText(getString(R.string.shape).concat(c1.getString(0)));
            String hno = c1.getString(1);
            if (hno.matches("\\d.*"))
                hno = getString(R.string.houseNo) + " " + hno;
            getSupportActionBar().setTitle(hno);

            if (!TextUtils.isEmpty(c1.getString(2))) {
                Uri personUri = Uri.withAppendedPath(Person.CONTENT_URI,
                        c1.getString(2));
                Cursor c2 = getContentResolver().query(personUri,
                        new String[]{Person.FULL_NAME}, null, null,
                        Person.DEFAULT_SORTING);
                if (c2.moveToFirst()) {
                    getSupportActionBar().setSubtitle(c2.getString(0));
                }
                if (c2 != null) {
                    c2.close();
                }
            }

            if (villcursor != null) {
                villcursor.close();
            }
        }
        if (c1 != null) {
            c1.close();
        }
    }

    boolean mGenogramable = true;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.house_main, menu);
        if (!mGenogramable) {
            menu.removeItem(R.id.genogram);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.genogram:
                Intent genome = new Intent(Action.GENOGRAM);
                genome.putExtra(House.HCODE, Integer.parseInt(getIntent().getData()
                        .getLastPathSegment()));
                genome.putExtra(House.PCUCODE, getPcuCode());
                startActivity(genome);
                return true;

            case R.id.map:
                Intent map = new Intent(Action.VIEW);
                map.addCategory(Category.MAP);
                map.putExtra("hcode", getIntent().getData().getLastPathSegment());
                map.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(map);
                return true;

            case android.R.id.home:
                startHomeActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doSetupImage(String name) {
        mImage = (ImageView) findViewById(R.id.image);
        if (mImage == null) {
            throw new IllegalArgumentException("Invalid Resource Layout");
        }

        mImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mPhotoTaker != null) {
                    mPhotoTaker.doShowDialog();
                }
                return true;
            }
        });

        // === แก้ไขส่วนนี้ - ใช้ SafeImageView แทน ===
        File pick = new File(FamilyFolderCollector.PHOTO_DIR_HOUSE,
                name.indexOf("tmp_") > 0 ? name : "tmp_" + name);
        mPhotoPath = pick.getAbsolutePath();

        if (pick.exists()) {
            try {
                // ใช้ SafeImageView เพื่อโหลดรูปภาพอย่างปลอดภัย
                SafeImageView.loadImageAsync(mImage, mPhotoPath, R.drawable.ic_house);

                // ตั้งค่า ScaleType หลังโหลดเสร็จ
                mImage.post(() -> {
                    ImageView.ScaleType scaleType = getResources().getBoolean(R.bool.landscape)
                            ? ImageView.ScaleType.FIT_CENTER
                            : ImageView.ScaleType.CENTER_CROP;
                    SafeImageView.setScaleTypeSafely(mImage, scaleType);
                });

            } catch (Exception e) {
                Log.e(TAG, "Error loading house image: " + mPhotoPath, e);
                mImage.setImageResource(R.drawable.ic_house);
            }
        } else {
            // ถ้าไม่มีไฟล์รูปภาพ ให้แสดงรูปดีฟอลต์
            mImage.setImageResource(R.drawable.ic_house);
        }

        // สร้าง PhotoTaker
        try {
            mPhotoTaker = new PhotoTaker(this,
                    FamilyFolderCollector.PHOTO_DIR_HOUSE,
                    name.indexOf("tmp_") > 0 ? name : "tmp_" + name);
        } catch (Exception e) {
            Log.e(TAG, "Error creating PhotoTaker", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mImage != null && mPhotoPath != null) {
            File pic = new File(mPhotoPath);
            if (pic.exists()) {
                try {
                    // === แก้ไขส่วนนี้ - ใช้ SafeImageView แทน ===
                    SafeImageView.loadImageAsync(mImage, mPhotoPath, R.drawable.ic_house);

                    // ตั้งค่า ScaleType
                    mImage.post(() -> {
                        ImageView.ScaleType scaleType = getResources().getBoolean(R.bool.landscape)
                                ? ImageView.ScaleType.FIT_CENTER
                                : ImageView.ScaleType.CENTER_CROP;
                        SafeImageView.setScaleTypeSafely(mImage, scaleType);
                    });

                    // สร้าง thumbnail
                    File thumb = new File(FamilyFolderCollector.PHOTO_DIR_HOUSE, mPhotoThrumb);
                    copyFilesSafely(pic, thumb);

                } catch (Exception e) {
                    Log.e(TAG, "Error loading image in onResume: " + mPhotoPath, e);
                    mImage.setImageResource(R.drawable.ic_house);
                }
            } else {
                // ถ้าไม่มีไฟล์ ให้แสดงรูปดีฟอลต์
                mImage.setImageResource(R.drawable.ic_house);
            }
        }
    }

    /**
     * คัดลอกไฟล์อย่างปลอดภัย พร้อมจัดการ exceptions
     */
    private void copyFilesSafely(File sourceFile, File destFile) {
        InputStream inStream = null;
        OutputStream outStream = null;

        try {
            if (!sourceFile.exists() || !sourceFile.canRead()) {
                Log.w(TAG, "Source file does not exist or cannot be read: " + sourceFile.getPath());
                return;
            }

            // สร้างโฟลเดอร์ปลายทางถ้ายังไม่มี
            File parentDir = destFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            inStream = new FileInputStream(sourceFile);
            outStream = new FileOutputStream(destFile);

            byte[] buffer = new byte[4096]; // เพิ่มขนาด buffer สำหรับประสิทธิภาพ
            int length;

            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            outStream.flush();
            Log.d(TAG, "File copied successfully: " + destFile.getPath());

        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found during copy operation", e);
        } catch (IOException e) {
            Log.e(TAG, "IO Error during file copy", e);
        } catch (SecurityException e) {
            Log.e(TAG, "Security error during file copy", e);
        } finally {
            // ปิด streams ใน finally block
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing input stream", e);
            }

            try {
                if (outStream != null) {
                    outStream.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing output stream", e);
            }
        }
    }

    /**
     * เวอร์ชันเก่าของ copyFile - deprecated
     * @deprecated ใช้ copyFilesSafely() แทน
     */
    @Deprecated
    public void copyFile(File afile, File bfile) {
        copyFilesSafely(afile, bfile);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mPhotoTaker != null) {
            try {
                mPhotoTaker.setContext(getBaseContext());
                mPhotoTaker.onActivityResult(requestCode, resultCode, data);
            } catch (Exception e) {
                Log.e(TAG, "Error handling photo result", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // ล้างข้อมูลเพื่อป้องกัน memory leak
        if (mImage != null) {
            mImage.setImageDrawable(null);
        }

        mPhotoTaker = null;
        mImage = null;
        mPhotoPath = null;
        mPhotoThrumb = null;
    }

    @Override
    protected void onPause() {
        super.onPause();

        // ลดการใช้หน่วยความจำเมื่อแอปไม่แสดงผล
        if (mImage != null) {
            // เก็บ drawable ปัจจุบันไว้ แต่ลด quality
            Drawable currentDrawable = mImage.getDrawable();
            if (currentDrawable != null) {
                // ไม่ต้องทำอะไร เพราะ SafeImageView จัดการแล้ว
            }
        }
    }
}