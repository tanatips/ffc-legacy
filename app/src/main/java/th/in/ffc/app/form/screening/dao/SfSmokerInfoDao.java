package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import th.in.ffc.app.form.screening.model.PersonInfo;
import  th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfSmokerInfoDao {

    private Context mContext;
    public static Uri getSmokerUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfSmokerInfo.CONTENT_URI,id.toString());
    }
    public static Uri getSmokerUri(){
        return ScreeningFormProvider.SfSmokerInfo.CONTENT_URI;
    }

    public SfSmokerInfoDao(Context context) {
        this.mContext = context;
    }
    public String insert(SmokerInfo data) {

        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("smoker_group", data.getSmokerGroup());
            values.put("smoker_assist", data.getSmokerAssist());
            values.put("smoker_regularly", data.getSmokerRegularly());
            values.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("created_by", "");
            values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("updated_by", "");
            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfSmokerInfo.CONTENT_URI, values);
            // ดึง ID ที่ได้จากการ insert
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        }
        catch (Exception e){
            return e.getMessage();
        }
        return id;
    }
    public SmokerInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getSmokerUriById(id), null, select, selectionArgs, null);
        SmokerInfo data = new SmokerInfo();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setSmokerAssist(cursor.getString(cursor.getColumnIndex("smoker_assist")));
                data.setSmokerGroup(cursor.getString(cursor.getColumnIndex("smoker_group")));
                data.setSmokerRegularly(cursor.getString(cursor.getColumnIndex("smoker_regularly")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                cursor.close();
            }

        }
        return data;
    }

    public List<SmokerInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getSmokerUri(), null, select, selectionArgs, null);
        List<SmokerInfo> smokerInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SmokerInfo data = new SmokerInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setSmokerAssist(cursor.getString(cursor.getColumnIndex("smoker_assist")));
                data.setSmokerGroup(cursor.getString(cursor.getColumnIndex("smoker_group")));
                data.setSmokerRegularly(cursor.getString(cursor.getColumnIndex("smoker_regularly")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("id_card")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                smokerInfos.add(data);
            }
        }
        return smokerInfos;
    }



    public int update(SmokerInfo smokerInfo) {
        String select="ID=?";
        String[] selectionArgs = {smokerInfo.getId()};
        ContentValues values = new ContentValues();
        values.put("person_info_id", smokerInfo.getPersonId());
        values.put("smoker_assist", smokerInfo.getSmokerAssist());
        values.put("smoker_group", smokerInfo.getSmokerGroup());
        values.put("smoker_regularly", smokerInfo.getSmokerRegularly());
        values.put("updated_by", smokerInfo.getUpdated_by());
        values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        mContext.getContentResolver().update(getSmokerUriById(Integer.valueOf(smokerInfo.getId())), values,select,selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select="ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getSmokerUri(),select,selectionArgs);
        return 1;
    }
}
