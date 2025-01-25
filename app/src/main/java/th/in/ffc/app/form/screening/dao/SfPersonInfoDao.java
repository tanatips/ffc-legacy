package th.in.ffc.app.form.screening.dao;

import static th.in.ffc.util.DataFromCursor.getDoubleFromCursor;
import static th.in.ffc.util.DataFromCursor.getIntegerFromCursor;
import static th.in.ffc.util.DataFromCursor.getStringFromCursor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfPersonInfoDao {

    private static Context mContext;

    public SfPersonInfoDao(Context context) {
        this.mContext = context;
    }
    public static Uri getPersonInfoUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfPersonInfo.CONTENT_URI,id.toString());
    }
    public static Uri getPersonInfoUriAppend(String name) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfPersonInfo.CONTENT_URI,name);
    }
    public static List<PersonInfo> getSfPersonInfoAll() {
        Cursor cursor = mContext.getContentResolver().query(getPersonInfoUriAppend("list"), null, null, null, null);
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
                personInfo.setCreated_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATED_BY));
                personInfo.setCreated_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATED_DATE));
                personInfo.setUpdated_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATED_BY));
                personInfo.setUpdated_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATED_DATE));
                personInfo.setSend_to_claim(getIntegerFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SEND_TO_CLAIM));
                personInfos.add(personInfo);
            }
            cursor.close();
        }
        return personInfos;
    }

    public static List<PersonInfo> getSfPersonInfoById(Integer id) {

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

                personInfo.setServiceCode(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SERVICE_CODE));
                personInfo.setTransId(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.TRANS_ID));
                personInfo.setSourceId(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SOURCE_ID));
                personInfo.setSubDistName(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SUB_DIST_NAME));
                personInfo.setSubDistCode(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SUB_DIST_CODE));
                personInfo.setDistCode(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.DIST_CODE));
                personInfo.setDistName(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.DIST_NAME));
                personInfo.setProvCode(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.PROV_CODE));
                personInfo.setProvName(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.PROV_NAME));
                personInfo.setPostCode(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.POST_CODE));
                personInfo.setHomeNo(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.HOME_NO));
                personInfo.setVillageNo(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.VILLAGE_NO));

                personInfo.setCreated_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATED_BY));
                personInfo.setCreated_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.CREATED_DATE));
                personInfo.setUpdated_by(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATED_BY));
                personInfo.setUpdated_date(getStringFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.UPDATED_DATE));
                personInfo.setSend_to_claim(getIntegerFromCursor(cursor, ScreeningFormProvider.SfPersonInfo.SEND_TO_CLAIM));
                personInfos.add(personInfo);
            }
            cursor.close();
        }
        return personInfos;
    }

    public static long update(PersonInfo personInfo){
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
    public static String insert(PersonInfo personInfo) {
        try {
            ContentValues values = new ContentValues();
            values= getPutvaluePersonInfo(personInfo,values);
            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfPersonInfo.CONTENT_URI, values);
            String id = "";
            // ดึง ID ที่ได้จากการ insert
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
            return id;
        }
        catch (Exception e){
            return e.getMessage();
        }
    }
    private static ContentValues getPutvaluePersonInfo(PersonInfo personInfo,ContentValues values){
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
        putString(values, "BMI", personInfo.getBmi());
        putDouble(values, "SYSTOLIC_PRESSURE", personInfo.getSystolic_pressure());
        putDouble(values, "DIASTOLIC_PRESSURE", personInfo.getDiastolic_pressure());

        putString(values, "SERVICECODE", personInfo.getServiceCode());
        putString(values, "TRANSID", personInfo.getTransId());
        putString(values, "SOURCEID", personInfo.getSourceId());
        putString(values, "SUBDISTNAME", personInfo.getSubDistName());
        putString(values, "SUBDISTCODE", personInfo.getSubDistCode());
        putString(values, "DISTCODE", personInfo.getDistCode());
        putString(values, "DISTNAME", personInfo.getDistName());
        putString(values, "PROVCODE", personInfo.getProvCode());
        putString(values, "PROVNAME", personInfo.getProvName());
        putString(values, "POSTCODE", personInfo.getPostCode());
        putString(values, "HOMENO", personInfo.getHomeNo());
        putString(values, "VILLAGENO", personInfo.getVillageNo());

        putString(values, "CREATED_BY", personInfo.getCreated_by());
        putString(values, "CREATED_DATE", personInfo.getCreated_date());
        putString(values, "UPDATED_BY", personInfo.getUpdated_by());
        putString(values, "UPDATED_DATE", personInfo.getUpdated_date());
        putInt(values, "SEND_TO_CLAIM", personInfo.getSend_to_claim());
        return values;
    }
    public static List<PersonInfo> searchPerson(String idcard, String firstName, String lastName) {
        List<PersonInfo> results = new ArrayList<>();

        // สร้าง where clause และ arguments
        List<String> whereConditions = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        if (idcard != null && !idcard.isEmpty()) {
            whereConditions.add("idcard LIKE ?");
            whereArgs.add("%" + idcard + "%");
        }

        if (firstName != null && !firstName.isEmpty()) {
            whereConditions.add("fname LIKE ?");
            whereArgs.add("%" + firstName + "%");
        }

        if (lastName != null && !lastName.isEmpty()) {
            whereConditions.add("lname LIKE ?");
            whereArgs.add("%" + lastName + "%");
        }

        // สร้าง where clause string
        String whereClause = null;
        if (!whereConditions.isEmpty()) {
            whereClause = TextUtils.join(" AND ", whereConditions);
        }

        // แปลง List<String> เป็น String[]
        String[] selectionArgs = whereArgs.toArray(new String[0]);

        // Query ข้อมูล
        Cursor cursor = mContext.getContentResolver().query(
                getPersonInfoUriAppend("list"),
                null,
                whereClause,
                selectionArgs,
                "fname ASC" // เรียงตามชื่อ
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PersonInfo person = new PersonInfo();
                person.setId(cursor.getString(cursor.getColumnIndex("id")));
                person.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                person.setFname(cursor.getString(cursor.getColumnIndex("fname")));
                person.setLname(cursor.getString(cursor.getColumnIndex("lname")));
                person.setBirthday(cursor.getString(cursor.getColumnIndex("birthday")));
                person.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                person.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
                person.setHn(cursor.getString(cursor.getColumnIndex("hn")));
                person.setAuthen_code(cursor.getString(cursor.getColumnIndex("authen_code")));
                person.setAuthen_date(cursor.getString(cursor.getColumnIndex("authen_date")));
                person.setWeight(cursor.getDouble(cursor.getColumnIndex("weight")));
                person.setHeight(cursor.getDouble(cursor.getColumnIndex("height")));
                person.setWaist_size(cursor.getDouble(cursor.getColumnIndex("waist_size")));
                person.setBp(cursor.getString(cursor.getColumnIndex("bp")));
                person.setSystolic_pressure(cursor.getDouble(cursor.getColumnIndex("systolic_pressure")));
                person.setDiastolic_pressure(cursor.getDouble(cursor.getColumnIndex("diastolic_pressure")));

                person.setServiceCode(cursor.getString(cursor.getColumnIndex("serviceCode")));
                person.setTransId(cursor.getString(cursor.getColumnIndex("transId")));
                person.setSourceId(cursor.getString(cursor.getColumnIndex("sourceId")));
                person.setSubDistName(cursor.getString(cursor.getColumnIndex("subDistName")));
                person.setSubDistCode(cursor.getString(cursor.getColumnIndex("subDistCode")));
                person.setDistCode(cursor.getString(cursor.getColumnIndex("distCode")));
                person.setDistName(cursor.getString(cursor.getColumnIndex("distName")));
                person.setProvCode(cursor.getString(cursor.getColumnIndex("provCode")));
                person.setProvName(cursor.getString(cursor.getColumnIndex("provName")));
                person.setPostCode(cursor.getString(cursor.getColumnIndex("postCode")));
                person.setHomeNo(cursor.getString(cursor.getColumnIndex("homeNo")));
                person.setVillageNo(cursor.getString(cursor.getColumnIndex("villageNo")));

                person.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                person.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                person.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                person.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                person.setSend_to_claim(cursor.getInt(cursor.getColumnIndex("send_to_claim")));

                results.add(person);
            }
            cursor.close();
        }

        return results;
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

}
