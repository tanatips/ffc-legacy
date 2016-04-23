/**
 *
 */
package th.in.ffc.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import th.in.ffc.FamilyFolderCollector;

import java.io.File;

/**
 * add description here! please
 *
 * @author piruinpanichphol
 * @version 1.0
 * @since Family Folder Collector 2.0
 */
public class DbOpenHelper extends SQLiteOpenHelper {

    private static String NAME = FamilyFolderCollector.PATH_PLAIN_DATABASE;
    private static int VERSION = 1;

    public DbOpenHelper(Context context) {

        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static boolean isDbExist() {
        File file = new File(NAME);
        return file.exists();
    }

    /**
     * factory for explicit close database by close Cursor
     *
     * @author Mannaz at StackOverflow
     * @author Piruin Panichphool
     * @version 1.2
     * @since Family Folder Collector 2.0
     */
    @TargetApi(11)
    public static class LeaklessCursorFactory implements CursorFactory {

        @Override
        public Cursor newCursor(SQLiteDatabase db,
                                SQLiteCursorDriver masterQuery, String editTable,
                                SQLiteQuery query) {
            return LeaklessCursor
                    .getInstance(db, masterQuery, editTable, query);
        }

        public static final LeaklessCursorFactory newInstance() {
            if (FamilyFolderCollector.HONEYCOMB_UP)
                return null;
            return new LeaklessCursorFactory();
        }


        public static class LeaklessCursor extends SQLiteCursor {
            static final String TAG = "LeaklessCursor";
            final SQLiteDatabase mDatabase;

            @SuppressWarnings("deprecation")
            public LeaklessCursor(SQLiteDatabase database,
                                  SQLiteCursorDriver driver, String table, SQLiteQuery query) {

                super(database, driver, table, query);
                mDatabase = database;
            }

            public LeaklessCursor(SQLiteDatabase database,
                                  SQLiteCursorDriver driver, String table, SQLiteQuery query,
                                  int version) {
                super(driver, table, query);
                mDatabase = database;
            }

            @Override
            public void close() {
                super.close();
                if (mDatabase != null) {
                    mDatabase.close();
                }
            }

            public static LeaklessCursor getInstance(SQLiteDatabase database,
                                                     SQLiteCursorDriver driver, String table, SQLiteQuery query) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB)
                    return new LeaklessCursor(database, driver, table, query, 1);
                else
                    return new LeaklessCursor(database, driver, table, query);
            }
        }

    }
}
