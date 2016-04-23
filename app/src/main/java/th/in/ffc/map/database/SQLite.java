package th.in.ffc.map.database;

import android.database.sqlite.SQLiteDatabase;

public class SQLite extends Database {

    private SQLiteDatabase sqLiteDatabase;

    public SQLite() {
        super();
        this.sqLiteDatabase = null;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        return sqLiteDatabase;
    }

    public void setSQLiteDatabase(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }


}
