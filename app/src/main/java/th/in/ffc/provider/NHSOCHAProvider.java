package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ContentProvider สำหรับจัดการข้อมูลทางการเงินของผู้เข้ารับบริการ NHSO CHA (แฟ้ม 8)
 */
public class NHSOCHAProvider extends ContentProvider {
    private static final String TAG = "NHSOCHAProvider";

    public static String AUTHORITY = "th.in.ffc.provider.NHSOCHAProvider";

    // NHSO CHA File 8
    private static final int NHSO_CHA = 0;
    private static final int NHSO_CHA_ITEMS = 1;
    private static final int NHSO_CHA_ITEM_ID = 2;
    // เพิ่ม URI สำหรับรายงานสรุป
    private static final int NHSO_CHA_SUMMARY_MONTHLY = 3;
    private static final int NHSO_CHA_SUMMARY_YEARLY = 4;
    private static final int NHSO_CHA_SUMMARY_BY_CHARGE = 5;
    private static final int NHSO_CHA_SUMMARY_BY_STATUS = 6;
    private static final int NHSO_CHA_MONTHLY_DETAILS = 7;

    public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsocha";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/vnd.ffc.nhsocha";
    // เพิ่ม Content Type สำหรับรายงานสรุป
    public static final String CONTENT_SUMMARY_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/vnd.ffc.nhsocha.summary";

    private DbOpenHelper mOpenHelper;
    private static UriMatcher mUriMatcher;

    static {
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        mUriMatcher.addURI(AUTHORITY, "nhso_cha", NHSO_CHA);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/list", NHSO_CHA_ITEMS);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/#", NHSO_CHA_ITEM_ID);
        // เพิ่ม URI Pattern สำหรับรายงานสรุป
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/summary/monthly/*", NHSO_CHA_SUMMARY_MONTHLY);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/summary/yearly/*", NHSO_CHA_SUMMARY_YEARLY);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/summary/by_charge/*", NHSO_CHA_SUMMARY_BY_CHARGE);
        mUriMatcher.addURI(AUTHORITY, "nhso_cha/summary/by_status/*", NHSO_CHA_SUMMARY_BY_STATUS);

        mUriMatcher.addURI(AUTHORITY, "nhso_cha/monthly_details/*/*", NHSO_CHA_MONTHLY_DETAILS);
    }

    // SimpleDateFormat สำหรับเปลี่ยนรูปแบบวันที่
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

    @Override
    public boolean onCreate() {
        try {
            mOpenHelper = new DbOpenHelper(this.getContext());

            // Create tables
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            db.execSQL(NHSOCHA.CREATE_TABLE);

            Log.i(TAG, "NHSO CHA Provider created successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error creating NHSO CHA Provider", e);
            return false;
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String groupby = null;
        String having = null;
        String fiscalYear = null;
        String fiscalMonth = null;

        int match = mUriMatcher.match(uri);
        switch (match) {
            case NHSOCHAProvider.NHSO_CHA_ITEMS:
                builder.setTables(NHSOCHA.TABLENAME);
                builder.setProjectionMap(NHSOCHA.PROJECTION_MAP);
                break;

            case NHSOCHAProvider.NHSO_CHA_ITEM_ID:
                selection = NHSOCHA.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                builder.setTables(NHSOCHA.TABLENAME);
                builder.setProjectionMap(NHSOCHA.PROJECTION_MAP);
                break;

            case NHSOCHAProvider.NHSO_CHA_SUMMARY_MONTHLY:
                // URI: nhso_cha/summary/monthly/YYYY (ปีงบประมาณ)
                fiscalYear = uri.getLastPathSegment();
                return getMonthlySummary(db, fiscalYear);

            case NHSOCHAProvider.NHSO_CHA_SUMMARY_YEARLY:
                // URI: nhso_cha/summary/yearly/YYYY (ปีงบประมาณ)
                fiscalYear = uri.getLastPathSegment();
                return getYearlySummary(db, fiscalYear);

            case NHSOCHAProvider.NHSO_CHA_SUMMARY_BY_CHARGE:
                // URI: nhso_cha/summary/by_charge/YYYY (ปีงบประมาณ)
                fiscalYear = uri.getLastPathSegment();
                return getSummaryByChargeItem(db, fiscalYear);

            case NHSOCHAProvider.NHSO_CHA_SUMMARY_BY_STATUS:
                // เนื่องจากไม่มีคอลัมน์สถานะ เราจะใช้ข้อมูลอื่นแทน เช่น แบ่งตาม chrgitem
                fiscalYear = uri.getLastPathSegment();
                return getSummaryByChargeItem(db, fiscalYear); // ใช้ประเภทรายการแทนสถานะ
            case NHSO_CHA_MONTHLY_DETAILS:
                fiscalYear = uri.getPathSegments().get(2);
                fiscalMonth = uri.getPathSegments().get(3);
                return getMonthlyDetails(db, fiscalYear, fiscalMonth);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        Cursor c = builder.query(db, projection, selection, selectionArgs,
                groupby, having, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    private Cursor getMonthlyDetails(SQLiteDatabase db, String fiscalYear, String fiscalMonth) {
        try {
            int year = Integer.parseInt(fiscalYear) - 1;
            int month = Integer.parseInt(fiscalMonth);

            Calendar calStart = Calendar.getInstance();
            calStart.set(year, Calendar.OCTOBER, 1);
            calStart.add(Calendar.MONTH, month - 1);

            Calendar calEnd = (Calendar) calStart.clone();
            calEnd.add(Calendar.MONTH, 1);
            calEnd.add(Calendar.DAY_OF_MONTH, -1);

            String startDateStr = dateFormat.format(calStart.getTime());
            String endDateStr = dateFormat.format(calEnd.getTime());

            String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {startDateStr, endDateStr};
            String sortOrder = NHSOCHA.DATE + " ASC";

            return db.query(
                    NHSOCHA.TABLENAME,
                    null,  // เลือกทุก column
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly details", e);
            return null;
        }
    }
    /**
     * คำนวณปีงบประมาณจากวันที่
     * @param date วันที่
     * @return ปีงบประมาณ
     */
    private String calculateFiscalYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int month = cal.get(Calendar.MONTH); // 0-11
        int year = cal.get(Calendar.YEAR); // ค.ศ.

        // ถ้าเดือนตุลาคม-ธันวาคม (9-11) อยู่ในปีงบประมาณถัดไป
        if (month >= 9) { // ตุลาคม = 9, พฤศจิกายน = 10, ธันวาคม = 11
            return String.valueOf(year + 1);
        } else {
            return String.valueOf(year);
        }
    }

    /**
     * คำนวณเดือนในปีงบประมาณจากวันที่
     * @param date วันที่
     * @return เดือนในปีงบประมาณ (1-12)
     */
    private int calculateFiscalMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int month = cal.get(Calendar.MONTH); // 0-11

        // แปลงเดือนปฏิทินเป็นเดือนงบประมาณ
        if (month >= 9) { // ตุลาคม-ธันวาคม
            return month - 8; // ตุลาคม=1, พฤศจิกายน=2, ธันวาคม=3
        } else {
            return month + 4; // มกราคม=4, ..., กันยายน=12
        }
    }

    /**
     * แปลงเดือนงบประมาณเป็นชื่อเดือนภาษาไทย
     * @param fiscalMonth เดือนงบประมาณ (1-12)
     * @return ชื่อเดือนภาษาไทย
     */
    private String getFiscalMonthName(int fiscalMonth) {
        switch (fiscalMonth) {
            case 1: return "ตุลาคม";
            case 2: return "พฤศจิกายน";
            case 3: return "ธันวาคม";
            case 4: return "มกราคม";
            case 5: return "กุมภาพันธ์";
            case 6: return "มีนาคม";
            case 7: return "เมษายน";
            case 8: return "พฤษภาคม";
            case 9: return "มิถุนายน";
            case 10: return "กรกฎาคม";
            case 11: return "สิงหาคม";
            case 12: return "กันยายน";
            default: return "";
        }
    }

    /**
     * คำนวณวันที่เริ่มต้นของปีงบประมาณ
     * @param fiscalYear ปีงบประมาณ
     * @return วันที่เริ่มต้นของปีงบประมาณ (1 ตุลาคม)
     */
    private Date getFiscalYearStartDate(String fiscalYear) {
        int year = Integer.parseInt(fiscalYear) - 1; // ปีก่อนหน้าปีงบประมาณ
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.OCTOBER, 1, 0, 0, 0); // 1 ตุลาคมของปีก่อนหน้า เวลา 00:00:00
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * คำนวณวันที่สิ้นสุดของปีงบประมาณ
     * @param fiscalYear ปีงบประมาณ
     * @return วันที่สิ้นสุดของปีงบประมาณ (30 กันยายน)
     */
    private Date getFiscalYearEndDate(String fiscalYear) {
        int year = Integer.parseInt(fiscalYear); // ปีงบประมาณ
        Calendar cal = Calendar.getInstance();
        cal.set(year, Calendar.SEPTEMBER, 30, 23, 59, 59); // 30 กันยายนของปีงบประมาณ เวลา 23:59:59
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * ดึงข้อมูลสรุปรายเดือนตามปีงบประมาณ
     * @param db ฐานข้อมูล
     * @param fiscalYear ปีงบประมาณ
     * @return Cursor ของข้อมูลสรุปรายเดือน
     */
    private Cursor getMonthlySummary(SQLiteDatabase db, String fiscalYear) {
        // สร้าง cursor แบบ in-memory สำหรับข้อมูลรายเดือน
        MatrixCursor result = new MatrixCursor(new String[] {
                "_id", "fiscal_month", "month_name", "total_count", "total_amount"
        });

        try {
            // คำนวณวันที่เริ่มต้นและสิ้นสุดของปีงบประมาณ
            Date startDate = getFiscalYearStartDate(fiscalYear);
            Date endDate = getFiscalYearEndDate(fiscalYear);

            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // ดึงข้อมูลทั้งหมดในปีงบประมาณ
            String selection = NHSOCHA.DATE + " BETWEEN ? AND ?";
            String[] selectionArgs = {startDateStr, endDateStr};

            Cursor cursor = db.query(
                    NHSOCHA.TABLENAME,
                    new String[]{NHSOCHA.ID, NHSOCHA.DATE, NHSOCHA.TOTAL},
                    selection,
                    selectionArgs,
                    null,
                    null,
                    NHSOCHA.DATE + " ASC"
            );

            if (cursor != null) {
                try {
                    // สร้าง map สำหรับเก็บข้อมูลรายเดือน
                    Map<Integer, MonthData> monthlyData = new HashMap<>();

                    // เตรียมข้อมูลสำหรับทุกเดือน (1-12)
                    for (int i = 1; i <= 12; i++) {
                        monthlyData.put(i, new MonthData(i, getFiscalMonthName(i)));
                    }

                    // ประมวลผลข้อมูลจาก cursor
                    while (cursor.moveToNext()) {
                        String dateStr = cursor.getString(cursor.getColumnIndex(NHSOCHA.DATE));
                        double amount = cursor.getDouble(cursor.getColumnIndex(NHSOCHA.TOTAL));

                        try {
                            Date date = dateFormat.parse(dateStr);
                            if (date != null) {
                                int fiscalMonth = calculateFiscalMonth(date);
                                MonthData data = monthlyData.get(fiscalMonth);

                                data.count++;
                                data.amount += amount;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing date: " + dateStr, e);
                        }
                    }

                    // เพิ่มข้อมูลในผลลัพธ์
                    for (int i = 1; i <= 12; i++) {
                        MonthData data = monthlyData.get(i);
                        result.addRow(new Object[]{
                                i,                   // _id
                                String.format("%02d", i),  // fiscal_month
                                data.monthName,      // month_name
                                data.count,          // total_count
                                data.amount          // total_amount
                        });
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly summary", e);
        }

        return result;
    }

    /**
     * ดึงข้อมูลสรุปรายปีตามปีงบประมาณ
     * @param db ฐานข้อมูล
     * @param fiscalYear ปีงบประมาณ
     * @return Cursor ของข้อมูลสรุปรายปี
     */
    private Cursor getYearlySummary(SQLiteDatabase db, String fiscalYear) {
        // สร้าง cursor แบบ in-memory สำหรับข้อมูลรายปี
        MatrixCursor result = new MatrixCursor(new String[] {
                "_id", "fiscal_year", "total_count", "total_amount"
        });

        try {
            // คำนวณวันที่เริ่มต้นและสิ้นสุดของปีงบประมาณ
            Date startDate = getFiscalYearStartDate(fiscalYear);
            Date endDate = getFiscalYearEndDate(fiscalYear);

            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // สร้าง SQL สำหรับดึงข้อมูลสรุปรายปี
            String sql = "SELECT " +
                    "COUNT(*) AS count, " +
                    "SUM(" + NHSOCHA.TOTAL + ") AS amount " +
                    "FROM " + NHSOCHA.TABLENAME + " " +
                    "WHERE " + NHSOCHA.DATE + " BETWEEN ? AND ?";

            Cursor cursor = db.rawQuery(sql, new String[]{startDateStr, endDateStr});

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int count = cursor.getInt(cursor.getColumnIndex("count"));
                        double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

                        result.addRow(new Object[]{
                                1,            // _id
                                fiscalYear,   // fiscal_year
                                count,        // total_count
                                amount        // total_amount
                        });
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting yearly summary", e);
        }

        return result;
    }

    /**
     * ดึงข้อมูลสรุปตามประเภทรายการในปีงบประมาณ
     * @param db ฐานข้อมูล
     * @param fiscalYear ปีงบประมาณ
     * @return Cursor ของข้อมูลสรุปตามประเภทรายการ
     */
    private Cursor getSummaryByChargeItem(SQLiteDatabase db, String fiscalYear) {
        // สร้าง cursor แบบ in-memory สำหรับข้อมูลตามประเภทรายการ
        MatrixCursor result = new MatrixCursor(new String[] {
                "_id", "chrgitem", "item_count", "item_amount"
        });

        try {
            // คำนวณวันที่เริ่มต้นและสิ้นสุดของปีงบประมาณ
            Date startDate = getFiscalYearStartDate(fiscalYear);
            Date endDate = getFiscalYearEndDate(fiscalYear);

            String startDateStr = dateFormat.format(startDate);
            String endDateStr = dateFormat.format(endDate);

            // สร้าง SQL สำหรับดึงข้อมูลสรุปตามประเภทรายการ
            String sql = "SELECT " +
                    NHSOCHA.CHRGITEM + " AS chrgitem, " +
                    "COUNT(*) AS count, " +
                    "SUM(" + NHSOCHA.TOTAL + ") AS amount " +
                    "FROM " + NHSOCHA.TABLENAME + " " +
                    "WHERE " + NHSOCHA.DATE + " BETWEEN ? AND ? " +
                    "GROUP BY " + NHSOCHA.CHRGITEM + " " +
                    "ORDER BY " + NHSOCHA.CHRGITEM;

            Cursor cursor = db.rawQuery(sql, new String[]{startDateStr, endDateStr});

            if (cursor != null) {
                try {
                    int id = 1;
                    while (cursor.moveToNext()) {
                        String chrgitem = cursor.getString(cursor.getColumnIndex("chrgitem"));
                        int count = cursor.getInt(cursor.getColumnIndex("count"));
                        double amount = cursor.getDouble(cursor.getColumnIndex("amount"));

                        result.addRow(new Object[]{
                                id++,        // _id
                                chrgitem,    // chrgitem
                                count,       // item_count
                                amount       // item_amount
                        });
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting summary by charge item", e);
        }

        return result;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case NHSOCHAProvider.NHSO_CHA_ITEMS:
                return NHSOCHA.CONTENT_DIR_TYPE;
            case NHSOCHAProvider.NHSO_CHA_ITEM_ID:
                return NHSOCHA.CONTENT_ITEM_TYPE;
            case NHSOCHAProvider.NHSO_CHA_SUMMARY_MONTHLY:
            case NHSOCHAProvider.NHSO_CHA_SUMMARY_YEARLY:
            case NHSOCHAProvider.NHSO_CHA_SUMMARY_BY_CHARGE:
            case NHSOCHAProvider.NHSO_CHA_SUMMARY_BY_STATUS:
                return CONTENT_SUMMARY_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long id = 0;
        Uri uriReturn = null;

        switch (mUriMatcher.match(uri)) {
            case NHSO_CHA:
                id = db.insert(NHSOCHA.TABLENAME, null, values);
                uriReturn = ContentUris.withAppendedId(NHSOCHA.CONTENT_URI, id);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (id > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return uriReturn;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)) {
            case NHSO_CHA_ITEMS:
                count = db.delete(NHSOCHA.TABLENAME, selection, selectionArgs);
                break;

            case NHSO_CHA_ITEM_ID:
                selection = NHSOCHA.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                count = db.delete(NHSOCHA.TABLENAME, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowUpdated = 0;

        switch (mUriMatcher.match(uri)) {
            case NHSO_CHA_ITEMS:
                rowUpdated = db.update(NHSOCHA.TABLENAME, values, selection, selectionArgs);
                break;

            case NHSO_CHA_ITEM_ID:
                selection = NHSOCHA.ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowUpdated = db.update(NHSOCHA.TABLENAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        if (rowUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowUpdated;
    }

    /**
     * คลาสช่วยสำหรับเก็บข้อมูลรายเดือน
     */
    private static class MonthData {
        int fiscalMonth;
        String monthName;
        int count;
        double amount;

        MonthData(int fiscalMonth, String monthName) {
            this.fiscalMonth = fiscalMonth;
            this.monthName = monthName;
            this.count = 0;
            this.amount = 0.0;
        }
    }
}