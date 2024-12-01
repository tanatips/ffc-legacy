package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;

public class SfStressDepressionInfoDao {

    private Context mContext;

    public static Uri getStressDepressionUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfStressDepressionInfo.CONTENT_URI, id.toString());
    }

    public static Uri getStressDepressionUri() {
        return ScreeningFormProvider.SfStressDepressionInfo.CONTENT_URI;
    }

    public SfStressDepressionInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(StressDepressionInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("q1", data.getQ1());
            values.put("q2", data.getQ2());
            values.put("q3", data.getQ3());
            values.put("q4", data.getQ4());
            values.put("q5", data.getQ5());
            values.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("created_by", "");
            values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("updated_by", "");

            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfStressDepressionInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public StressDepressionInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getStressDepressionUriById(id), null, select, selectionArgs, null);
        StressDepressionInfo data = new StressDepressionInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setQ1(cursor.getString(cursor.getColumnIndex("q1")));
                data.setQ2(cursor.getString(cursor.getColumnIndex("q2")));
                data.setQ3(cursor.getString(cursor.getColumnIndex("q3")));
                data.setQ4(cursor.getString(cursor.getColumnIndex("q4")));
                data.setQ5(cursor.getString(cursor.getColumnIndex("q5")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                cursor.close();
            }
        }
        return data;
    }

    public List<StressDepressionInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getStressDepressionUriById(personId), null, select, selectionArgs, null);
        List<StressDepressionInfo> stressDepressionInfos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                StressDepressionInfo data = new StressDepressionInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setQ1(cursor.getString(cursor.getColumnIndex("q1")));
                data.setQ2(cursor.getString(cursor.getColumnIndex("q2")));
                data.setQ3(cursor.getString(cursor.getColumnIndex("q3")));
                data.setQ4(cursor.getString(cursor.getColumnIndex("q4")));
                data.setQ5(cursor.getString(cursor.getColumnIndex("q5")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                stressDepressionInfos.add(data);
            }
            cursor.close();
        }
        return stressDepressionInfos;
    }

    public int update(StressDepressionInfo data) {
        String select = "ID=?";
        String[] selectionArgs = {data.getId()};
        ContentValues values = new ContentValues();
        values.put("person_info_id", data.getPersonId());
        values.put("idcard", data.getIdcard());
        values.put("q1", data.getQ1());
        values.put("q2", data.getQ2());
        values.put("q3", data.getQ3());
        values.put("q4", data.getQ4());
        values.put("q5", data.getQ5());
        values.put("updated_by", data.getUpdated_by());
        values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        mContext.getContentResolver().update(getStressDepressionUriById(Integer.valueOf(data.getId())), values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getStressDepressionUri(), select, selectionArgs);
        return 1;
    }
}