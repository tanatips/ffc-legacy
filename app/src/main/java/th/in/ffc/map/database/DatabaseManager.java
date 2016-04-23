package th.in.ffc.map.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import th.in.ffc.map.value.MARKER_TYPE;
import th.in.ffc.map.value.MISC_ENUM;

public class DatabaseManager {

    private SQLite sqLite;

    public DatabaseManager() {
        // this.sqLite = new SQLite();
        // this.openDatabase();
    }

    public boolean openDatabase() {
        this.sqLite = new SQLite();

        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase != null) {
            if (sqLiteDatabase.isOpen()) {
                sqLiteDatabase.close();
            }
            sqLiteDatabase = null;
        }
        sqLiteDatabase = SQLiteDatabase.openDatabase(
                this.sqLite.getStringPath(), null,
                SQLiteDatabase.OPEN_READWRITE);
        this.sqLite.setSQLiteDatabase(sqLiteDatabase);
        return sqLiteDatabase.isOpen();
    }

    public boolean executeSQL(String stringSQL) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen()) {
            try {
                sqLiteDatabase.execSQL(stringSQL);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean insert(MARKER_TYPE type, ContentValues cv) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen() && this.checkIfNotExist(type, cv)) {
            long rowid = sqLiteDatabase.insert(type.getDBTableName(), null, cv);
            return rowid != -1;
        }
        return false;
    }

    public boolean insertVillage(MISC_ENUM vill, ContentValues cv) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen() && this.checkIfNotExistVillage(vill, cv)) {
            long rowid = sqLiteDatabase.insert(vill.getDBTableName(), null, cv);
            return rowid != -1;
        }
        return false;
    }

    public boolean checkIfNotExist(MARKER_TYPE type, ContentValues cv) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen()) {
            final String col = type.getColumnName();
            final String table = type.getDBTableName();
            String query = "SELECT villcode FROM " + table
                    + " WHERE villcode=? AND " + col + "=?";
            Cursor cur = sqLiteDatabase.rawQuery(
                    query,
                    new String[]{cv.getAsString("villcode"),
                            cv.getAsString(col)});
            final boolean result = cur.getCount() == 0;
            cur.close();
            cur = null;
            return result;
        }
        return false;
    }

    public boolean checkIfNotExistVillage(MISC_ENUM vill, ContentValues cv) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen()) {
            final String col = vill.getColumnName();
            final String table = vill.getDBTableName();
            String query = "SELECT " + col + " FROM " + table + " WHERE " + col
                    + " = '" + cv.getAsString(col) + "'";
//	    Log.d("TAG!", query);
            Cursor cur = sqLiteDatabase.rawQuery(query, null);
            final boolean result = (cur.getCount() == 0);
//	    Log.d("TAG!", result+" || "+cur.getCount());
            cur.close();
            cur = null;
            return result;
        }
        return false;
    }

    public boolean update(String table, ContentValues cv, String whereClause) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen()) {
            int row = sqLiteDatabase.update(table, cv, whereClause, null);
            return row > 0;
        }
        return false;
    }

    public Cursor getCursor(String stringSQL) {
        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase.isOpen()) {
            return sqLiteDatabase.rawQuery(stringSQL, null);
        } else {
            return null;
        }
    }

    public boolean closeDatabase() {
        if (this.sqLite == null)
            return true;

        SQLiteDatabase sqLiteDatabase = this.sqLite.getSQLiteDatabase();
        if (sqLiteDatabase != null && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }

        sqLiteDatabase = null;
        this.sqLite = null;
        return this.sqLite == null;
    }

}
