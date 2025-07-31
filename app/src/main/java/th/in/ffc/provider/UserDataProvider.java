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

package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.HashMap;

import th.in.ffc.FamilyFolderCollector;

/**
 * Content Provider of User table
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class UserDataProvider extends ContentProvider {

    public static String AUTHORITY = "th.in.ffc.provider.UserDataProvider";

    //private static final int LOGIN = 1;
    private static final int USER = 2;
    private static final int USER_USERNAME = 3;

    private static UriMatcher mUriMatcher;
    public static HashMap<String, String> PROJECTION_MAP;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(UserDataProvider.AUTHORITY, "user",
                USER);
        mUriMatcher.addURI(UserDataProvider.AUTHORITY, "user/*", USER_USERNAME);

        //mUriMatcher.addURI(UserProvider.AUTHORITY, null, LOGIN);

        PROJECTION_MAP = new HashMap<String, String>();
        // mProjectionMap.put(User._ID, " AS "+ User._ID);
        PROJECTION_MAP.put(User.PCUCODE, "pcucode AS " + User.PCUCODE);
        PROJECTION_MAP.put(User.USERNAME, "username AS " + User.USERNAME);
        PROJECTION_MAP.put(User.PASSWORD, "password AS " + User.PASSWORD);
        PROJECTION_MAP.put(User.IDCARD, User.IDCARD + " AS " + User.IDCARD);
    }

    private DbOpenHelper mOpenHelper;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // this method is not support because policy for delete user from
        // database
        throw new UnsupportedOperationException();
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Not support like delete method
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean onCreate() {
        // TODO Create new instance of user's database open helper
        mOpenHelper = new DbOpenHelper(this.getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(User.TABLENAME);
        builder.setProjectionMap(PROJECTION_MAP);


        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case UserDataProvider.USER:
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            case UserDataProvider.USER_USERNAME:
                builder.appendWhere("username=\'" + uri.getLastPathSegment() + "\'");
                return builder.query(db, projection, null, null, null, null, null);
            default:
                throw new IllegalArgumentException("Unknown URI : " + uri.toString());
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    /**
     * BaseColumns Class for information about user table
     *
     * @author piruin panichphol
     * @version 1.0
     * @since Family Folder Collector 2.0
     */
    public static final class User implements BaseColumns {

        public static final String TABLENAME = "user";

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + UserDataProvider.AUTHORITY + "/user");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.user";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.user";

        public static final String PCUCODE = "pcucode";
        public static final String USERNAME = "username";
        public static final String IDCARD = "idcard";
        private static final String PASSWORD = "password";

        public static final String SELECTION_LOGIN = "username=? AND password=?";


    }

}
