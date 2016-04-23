/**
 * **********************************************************************
 * _ _ _
 * ( _ _  |
 * _ _ _ _  | |
 * (_ _ _  | |_|
 * _     _   _ _ _ _     _ _ _   _ _ _ _ _   _ _ _ _     _ _ _   | |
 * |  \  | | |  _ _ _|   /  _ _| |_ _   _ _| |  _ _ _|   /  _ _|  | |
 * | | \ | | | |_ _ _   /  /         | |     | |_ _ _   /  /      |_|
 * | |\ \| | |  _ _ _| (  (          | |     |  _ _ _| (  (
 * | | \ | | | |_ _ _   \  \_ _      | |     | |_ _ _   \  \_ _
 * |_|  \__| |_ _ _ _|   \_ _ _|     |_|     |_ _ _ _|   \_ _ _|
 * a member of NSTDA, @Thailand
 * <p/>
 * ***********************************************************************
 * <p/>
 * <p/>
 * FFC-Plus Project
 * <p/>
 * Copyright (C) 2010-2012 National Electronics and Computer Technology Center
 * All Rights Reserved.
 * <p/>
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE.txt', which is part of this source code package.
 */
package th.in.ffc.security;

import android.content.Context;

import java.io.File;
import java.io.IOException;


/**
 *
 * Factory class for help create empty database in private mode because
 * context.createoropendatabse() create database file with some meta-data which
 * may not need by some application
 *
 * @version 1.0
 * @author Piruin Panichphol
 *
 * @since Family Folder Collector 2.0
 *
 */
public class DatabaseCreator {
    static boolean writable = true;
    static boolean readable = true;
    static boolean ownerOnly = true;

    /**
     * create Database file with permission 6-- (read/writable only by owner)
     *
     * @since 1.0

     * @param context
     *            context base for create database
     * @param name
     *            name of database file output
     * @return File object for database null if IOException
     */
    public static File CreateSecureDatabase(Context context, String name) {
        File db = new File(makeDatabseDir(context).getAbsolutePath(), name);


        try {
            if (!db.exists())
                db.createNewFile();
            db.setWritable(DatabaseCreator.writable, DatabaseCreator.ownerOnly);
            db.setReadable(DatabaseCreator.readable, DatabaseCreator.ownerOnly);
            return db;
        } catch (IOException io) {

            io.printStackTrace();
            return null;
        }

    }

    /**
     <<<<<<< HEAD
     * create Database file with permission 66- (owner and owner group can
     * read/write) Note this slower than create for only owner
     *
     * @since 1.0
     * @param context
     *            context base for create database
     * @param name
     *            name of database file output
    =======
     * create Database file with permission 66- (owner and owner group can read/write)
     * Note this slower than create for only owner
     *
     * @since 1.0
     * @param context context base for create database
     * @param name name of database file output
    >>>>>>> a2f84ac756f5c1a54281f81b0283942d2a95f780
     * @return File object for database null if IOException
     */
    public static File CreateSecureDatabasGroup(Context context, String name) {
        try {


            context.openFileOutput(name, Context.MODE_PRIVATE).close();
            File db = new File("/data/data/" + context.getPackageName()
                    + "/files/", name);

            File ndb = new File(makeDatabseDir(context).getAbsolutePath(), name);
            if (ndb.exists())
                ndb.delete();

            db.renameTo(ndb);

            return ndb;
        } catch (IOException io) {
            io.printStackTrace();

            return null;
        }

    }


    private static File makeDatabseDir(Context context) {
        File dir = new File("/data/data/" + context.getPackageName()
                + "/databases/");
        if (!dir.exists()) {
            context.openOrCreateDatabase("temp.db", Context.MODE_PRIVATE, null);
        }
        return dir;
    }
}
