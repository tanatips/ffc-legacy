package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfPersonInfoDao {


    private Context mContext;

    public SfPersonInfoDao(Context context) {
        this.mContext = context;
    }
    public static Uri getPersonInfoUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfPersonInfo.CONTENT_URI,id.toString());
    }
    public List<PersonInfo> getSfPersonInfoAll() {
        Cursor cursor = mContext.getContentResolver().query(ScreeningFormProvider.SfPersonInfo.CONTENT_URI, null, null, null, null);
        List<PersonInfo> personInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                PersonInfo personInfo = new PersonInfo();
                personInfo.setId(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.ID));
                personInfo.setIdcard(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.IDCARD));
                personInfo.setFname(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.FNAME));
                personInfo.setLname(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.LNAME));
                personInfo.setBirthday(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.BIRTHDAY));
                personInfo.setGender(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.GENDER));
                personInfo.setPhone(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.PHONE));
                personInfo.setHn(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.HN));
                personInfo.setAuthen_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.AUTHEN_DATE));
                personInfo.setAuthen_code(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.AUTHEN_CODE));
                personInfo.setWeight(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.WEIGHT));
                personInfo.setHeight(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.HEIGHT));
                personInfo.setWaist_size(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.WAIST_SIZE));
                personInfo.setBp(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.BP));
                personInfo.setSystolic_pressure(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SYSTOLIC_PRESSURE));
                personInfo.setDiastolic_pressure(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.DIASTOLIC_PRESSURE));
                personInfo.setCreate_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATE_BY));
                personInfo.setCreate_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATE_DATE));
                personInfo.setUpdate_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATE_BY));
                personInfo.setUpdate_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATE_DATE));
                personInfo.setSend_to_claim(getIntegerFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SEND_TO_CLAIM));
                personInfos.add(personInfo);
            }
            cursor.close();
        }
        return personInfos;
    }

    public List<PersonInfo> getSfPersonInfoById(Integer id) {

        String select =  "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getPersonInfoUriById(id), null, select, selectionArgs, null);
        List<PersonInfo> personInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                PersonInfo personInfo = new PersonInfo();
                personInfo.setId(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.ID));
                personInfo.setIdcard(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.IDCARD));
                personInfo.setFname(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.FNAME));
                personInfo.setLname(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.LNAME));
                personInfo.setBirthday(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.BIRTHDAY));
                personInfo.setGender(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.GENDER));
                personInfo.setPhone(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.PHONE));
                personInfo.setHn(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.HN));
                personInfo.setAuthen_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.AUTHEN_DATE));
                personInfo.setAuthen_code(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.AUTHEN_CODE));
                personInfo.setWeight(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.WEIGHT));
                personInfo.setHeight(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.HEIGHT));
                personInfo.setWaist_size(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.WAIST_SIZE));
                personInfo.setBp(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.BP));
                personInfo.setSystolic_pressure(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SYSTOLIC_PRESSURE));
                personInfo.setDiastolic_pressure(getDoubleFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.DIASTOLIC_PRESSURE));
                personInfo.setCreate_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATE_BY));
                personInfo.setCreate_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATE_DATE));
                personInfo.setUpdate_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATE_BY));
                personInfo.setUpdate_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATE_DATE));
                personInfo.setSend_to_claim(getIntegerFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SEND_TO_CLAIM));
                personInfos.add(personInfo);
            }
            cursor.close();
        }
        return personInfos;
    }

    public long update(PersonInfo personInfo){
        try {
            String select="ID=?";
            String[] selectionArgs = {personInfo.getId()};
            ContentValues values = new ContentValues();
            values= getPutvaluePersonInfo(personInfo,values);
            putString(values, "ID", personInfo.getId());
            mContext.getContentResolver().update(getPersonInfoUriById(Integer.valueOf(personInfo.getId())), values,select,selectionArgs);
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }
    public long add(PersonInfo personInfo) {
        try {
            ContentValues values = new ContentValues();
            values= getPutvaluePersonInfo(personInfo,values);
            mContext.getContentResolver().insert(ScreeningFormProvider.SfPersonInfo.CONTENT_URI, values);
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }
    private ContentValues getPutvaluePersonInfo(PersonInfo personInfo,ContentValues values){
        putString(values, "IDCARD", personInfo.getIdcard());
        putString(values, "FNAME", personInfo.getFname());
        putString(values, "LNAME", personInfo.getLname());
        putString(values, "BIRTHDAY", personInfo.getBirthday());
        putString(values, "GENDER", personInfo.getGender());
        putString(values, "PHONE", personInfo.getPhone());
        putString(values, "HN", personInfo.getHn());
        putString(values, "AUTHEN_DATE", personInfo.getAuthen_date());
        putString(values, "AUTHEN_CODE", personInfo.getAuthen_code());
        putDouble(values, "WEIGHT", personInfo.getWeight());
        putDouble(values, "HEIGHT", personInfo.getHeight());
        putDouble(values, "WAIST_SIZE", personInfo.getWaist_size());
        putString(values, "BP", personInfo.getBp());
        putDouble(values, "SYSTOLIC_PRESSURE", personInfo.getSystolic_pressure());
        putDouble(values, "DIASTOLIC_PRESSURE", personInfo.getDiastolic_pressure());
        putString(values, "CREATE_BY", personInfo.getCreate_by());
        putString(values, "CREATE_DATE", personInfo.getCreate_date());
        putString(values, "UPDATE_BY", personInfo.getUpdate_by());
        putString(values, "UPDATE_DATE", personInfo.getUpdate_date());
        putInt(values, "SEND_TO_CLAIM", personInfo.getSend_to_claim());
        return values;
    }
    private static void putString(ContentValues values, String key, String value) {
        if (value != null) {
            values.put(key, value);
        }
    }
    private static void putInt(ContentValues values, String key, Integer value) {
        if (value != null) {
            values.put(key, value);
        }
    }
    private static void putDouble(ContentValues values, String key, Double value) {
        if (value != null) {
            values.put(key, value);
        }
    }
    private static String getStringFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getString(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }

    private static Double getDoubleFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getDouble(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }

    // เมธอดช่วยสำหรับการดึงข้อมูล Integer แล้วแปลงเป็น String
    private static Integer getIntegerFromCursor(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getInt(columnIndex);
        }
        return null; // คืนค่า null หากคอลัมน์ไม่มี
    }
}
