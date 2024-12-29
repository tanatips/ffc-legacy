package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfSuicideAssessment8qInfoDao {
    private Context mContext;

    public static Uri getSuicideAssessmentUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfSuicideAssessment8qInfo.CONTENT_URI, id.toString());
    }

    public static Uri getSuicideAssessmentUri() {
        return ScreeningFormProvider.SfSuicideAssessment8qInfo.CONTENT_URI;
    }

    public SfSuicideAssessment8qInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(SuicideAssessment8qInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("q1", data.getQ1());
            values.put("q2", data.getQ2());
            values.put("q3", data.getQ3());
            values.put("q3_2_1", data.getQ3_2_1());
            values.put("q4", data.getQ4());
            values.put("q5", data.getQ5());
            values.put("q6", data.getQ6());
            values.put("q7", data.getQ7());
            values.put("q8", data.getQ8());
            values.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("created_by", "");
            values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("updated_by", "");

            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfSuicideAssessment8qInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public SuicideAssessment8qInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getSuicideAssessmentUriById(id), null, select, selectionArgs, null);
        SuicideAssessment8qInfo data = new SuicideAssessment8qInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setQ1(cursor.getString(cursor.getColumnIndex("q1")));
                data.setQ2(cursor.getString(cursor.getColumnIndex("q2")));
                data.setQ3(cursor.getString(cursor.getColumnIndex("q3")));
                data.setQ3_2_1(cursor.getString(cursor.getColumnIndex("q3_2_1")));
                data.setQ4(cursor.getString(cursor.getColumnIndex("q4")));
                data.setQ5(cursor.getString(cursor.getColumnIndex("q5")));
                data.setQ6(cursor.getString(cursor.getColumnIndex("q6")));
                data.setQ7(cursor.getString(cursor.getColumnIndex("q7")));
                data.setQ8(cursor.getString(cursor.getColumnIndex("q8")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                cursor.close();
            }
        }
        return data;
    }

    public List<SuicideAssessment8qInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getSuicideAssessmentUriById(personId), null, select, selectionArgs, null);
        List<SuicideAssessment8qInfo> suicideAssessmentInfos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                SuicideAssessment8qInfo data = new SuicideAssessment8qInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setQ1(cursor.getString(cursor.getColumnIndex("q1")));
                data.setQ2(cursor.getString(cursor.getColumnIndex("q2")));
                data.setQ3(cursor.getString(cursor.getColumnIndex("q3")));
                data.setQ3_2_1(cursor.getString(cursor.getColumnIndex("q3_2_1")));
                data.setQ4(cursor.getString(cursor.getColumnIndex("q4")));
                data.setQ5(cursor.getString(cursor.getColumnIndex("q5")));
                data.setQ6(cursor.getString(cursor.getColumnIndex("q6")));
                data.setQ7(cursor.getString(cursor.getColumnIndex("q7")));
                data.setQ8(cursor.getString(cursor.getColumnIndex("q8")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                suicideAssessmentInfos.add(data);
            }
        }
        return suicideAssessmentInfos;
    }

    public int update(SuicideAssessment8qInfo data) {
        String select = "ID=?";
        String[] selectionArgs = {data.getId()};
        ContentValues values = new ContentValues();

        values.put("person_info_id", data.getPersonId());
        values.put("idcard", data.getIdcard());
        values.put("q1", data.getQ1());
        values.put("q2", data.getQ2());
        values.put("q3", data.getQ3());
        values.put("q3_2_1", data.getQ3_2_1());
        values.put("q4", data.getQ4());
        values.put("q5", data.getQ5());
        values.put("q6", data.getQ6());
        values.put("q7", data.getQ7());
        values.put("q8", data.getQ8());
        values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put("updated_by", "");

        mContext.getContentResolver().update(getSuicideAssessmentUriById(Integer.valueOf(data.getId())),
                values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getSuicideAssessmentUri(), select, selectionArgs);
        return 1;
    }
}