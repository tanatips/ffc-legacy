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

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import th.in.ffc.FamilyFolderCollector;

import java.util.HashMap;

/**
 * Content Provider of User table
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class UserProvider extends ContentProvider {

    public static String AUTHORITY = "th.in.ffc.provider.UserProvider";

    //private static final int LOGIN = 1;
    private static final int USER = 2;
    private static final int USER_USERNAME = 3;

    private static UriMatcher mUriMatcher;
    public static HashMap<String, String> PROJECTION_MAP;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(UserProvider.AUTHORITY, "user",
                USER);
        mUriMatcher.addURI(UserProvider.AUTHORITY, "user/*", USER_USERNAME);

        //mUriMatcher.addURI(UserProvider.AUTHORITY, null, LOGIN);

        PROJECTION_MAP = new HashMap<String, String>();
        // mProjectionMap.put(User._ID, " AS "+ User._ID);
        PROJECTION_MAP.put(User.PCUCODE, "pcucode AS " + User.PCUCODE);
        PROJECTION_MAP.put(User.USERNAME, "username AS " + User.USERNAME);
        PROJECTION_MAP.put(User.PASSWORD, "password AS " + User.PASSWORD);
    }

    private UserDatabaseOpenHelper mOpenHelper;

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
        mOpenHelper = new UserDatabaseOpenHelper(this.getContext());
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
            case UserProvider.USER:
                return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

            case UserProvider.USER_USERNAME:
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
     * Open helper for open user database
     *
     * @author Piruin Panichphol
     * @version 1.0
     * @since 1.0
     */

    public static class UserDatabaseOpenHelper extends SQLiteOpenHelper {


        public static final String NAME = FamilyFolderCollector.PATH_USER_DATABASE;
        public static final int VERSION = 1;

        public UserDatabaseOpenHelper(Context context) {
            super(context, UserDatabaseOpenHelper.NAME, null,
                    UserDatabaseOpenHelper.VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO do nothing

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO do nothing
        }
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
                + UserProvider.AUTHORITY + "/user");

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.user";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.user";

        public static final String PCUCODE = "pcucode";
        public static final String USERNAME = "username";
        private static final String PASSWORD = "password";

        public static final String SELECTION_LOGIN = "username=? AND password=?";


    }

}
