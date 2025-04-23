package th.in.ffc.util;

import android.database.Cursor;

public class DataFromCursor {
    public static String getStringFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getString(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }

    public static Double getDoubleFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getDouble(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }

    // เมธอดช่วยสำหรับการดึงข้อมูล Integer แล้วแปลงเป็น String
    public static Integer getIntegerFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getInt(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }
    public static byte[] getBlogFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getBlob(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }
}
