package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.util.DateConverter;

public class SfHealthRiskAssessmentInfoDao {
    private Context mContext;

    public static Uri getHealthRiskUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfHealthRiskAssessmentInfo.CONTENT_URI, id.toString());
    }

    public static Uri getHealthRiskUri() {
        return ScreeningFormProvider.SfHealthRiskAssessmentInfo.CONTENT_URI;
    }

    public SfHealthRiskAssessmentInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(HealthRiskAssessmentInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("health_risk_q1", data.getHealthRiskQ1());
            values.put("health_risk_q2", data.getHealthRiskQ2());
            values.put("health_risk_q3", data.getHealthRiskQ3());
            values.put("health_risk_q4", data.getHealthRiskQ4());
            values.put("health_risk_q5", data.getHealthRiskQ5());
            values.put("health_risk_q6", data.getHealthRiskQ6());
            values.put("fcbg", data.getFcbg());
            values.put("fpg", data.getFpg());
            values.put("created_date", DateConverter.getCurrentWesternDateTime());
            values.put("created_by", "");
            values.put("updated_date", DateConverter.getCurrentWesternDateTime());
            values.put("updated_by", "");
            values.put("visitno", data.getVisitNo());
            values.put("dateupdate", DateConverter.getCurrentWesternDateTime());
            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfHealthRiskAssessmentInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public HealthRiskAssessmentInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getHealthRiskUriById(id), null, select, selectionArgs, null);
        HealthRiskAssessmentInfo data = new HealthRiskAssessmentInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setHealthRiskQ1(cursor.getString(cursor.getColumnIndex("health_risk_q1")));
                data.setHealthRiskQ2(cursor.getString(cursor.getColumnIndex("health_risk_q2")));
                data.setHealthRiskQ3(cursor.getString(cursor.getColumnIndex("health_risk_q3")));
                data.setHealthRiskQ4(cursor.getString(cursor.getColumnIndex("health_risk_q4")));
                data.setHealthRiskQ5(cursor.getString(cursor.getColumnIndex("health_risk_q5")));
                data.setHealthRiskQ6(cursor.getString(cursor.getColumnIndex("health_risk_q6")));
                data.setFcbg(cursor.getString(cursor.getColumnIndex("fcbg")));
                data.setFpg(cursor.getString(cursor.getColumnIndex("fpg")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                data.setVisitNo(cursor.getString(cursor.getColumnIndex("visitno")));
                data.setDateUpdate(cursor.getString(cursor.getColumnIndex("dateupdate")));
                cursor.close();
            }
        }
        return data;
    }

    public List<HealthRiskAssessmentInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getHealthRiskUriById(personId), null, select, selectionArgs, null);
        List<HealthRiskAssessmentInfo> healthRiskInfos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                HealthRiskAssessmentInfo data = new HealthRiskAssessmentInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setHealthRiskQ1(cursor.getString(cursor.getColumnIndex("health_risk_q1")));
                data.setHealthRiskQ2(cursor.getString(cursor.getColumnIndex("health_risk_q2")));
                data.setHealthRiskQ3(cursor.getString(cursor.getColumnIndex("health_risk_q3")));
                data.setHealthRiskQ4(cursor.getString(cursor.getColumnIndex("health_risk_q4")));
                data.setHealthRiskQ5(cursor.getString(cursor.getColumnIndex("health_risk_q5")));
                data.setHealthRiskQ6(cursor.getString(cursor.getColumnIndex("health_risk_q6")));
                data.setFcbg(cursor.getString(cursor.getColumnIndex("fcbg")));
                data.setFpg(cursor.getString(cursor.getColumnIndex("fpg")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                data.setVisitNo(cursor.getString(cursor.getColumnIndex("visitno")));
                data.setDateUpdate(cursor.getString(cursor.getColumnIndex("dateupdate")));
                healthRiskInfos.add(data);
            }
        }
        return healthRiskInfos;
    }

    public int update(HealthRiskAssessmentInfo data) {
        String select = "ID=?";
        String[] selectionArgs = {data.getId()};
        ContentValues values = new ContentValues();

        values.put("person_info_id", data.getPersonId());
        values.put("idcard", data.getIdcard());
        values.put("health_risk_q1", data.getHealthRiskQ1());
        values.put("health_risk_q2", data.getHealthRiskQ2());
        values.put("health_risk_q3", data.getHealthRiskQ3());
        values.put("health_risk_q4", data.getHealthRiskQ4());
        values.put("health_risk_q5", data.getHealthRiskQ5());
        values.put("health_risk_q6", data.getHealthRiskQ6());
        values.put("fcbg", data.getFcbg());
        values.put("fpg", data.getFpg());
        values.put("updated_date", DateConverter.getCurrentWesternDateTime());
        values.put("updated_by", "");
        values.put("visitno", data.getVisitNo());
        values.put("dateupdate", DateConverter.getCurrentWesternDateTime());

        mContext.getContentResolver().update(getHealthRiskUriById(Integer.valueOf(data.getId())),
                values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getHealthRiskUri(), select, selectionArgs);
        return 1;
    }
}