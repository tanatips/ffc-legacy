package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import th.in.ffc.app.form.screening.model.CardiovascularRiskInfo;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.util.DateConverter;

public class SfCardiovascularRiskInfoDao {
    private Context mContext;

    public static Uri getCardiovascularRiskUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfCardiovascularRiskInfo.CONTENT_URI, id.toString());
    }

    public static Uri getCardiovascularRiskUri() {
        return ScreeningFormProvider.SfCardiovascularRiskInfo.CONTENT_URI;
    }

    public SfCardiovascularRiskInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(CardiovascularRiskInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("age", data.getAge());
            values.put("gender", data.getGender());
            values.put("blood_pressure", data.getBloodPressure());
            values.put("waist_size", data.getWaistSize());
            values.put("height", data.getHeight());
            values.put("cholesterol", data.getCholesterol());
            values.put("is_smoking", data.getIsSmoking());
            values.put("has_diabetes", data.getHasDiabetes());
            values.put("risk_level", data.getRiskLevel());
            values.put("risk_percentage", data.getRiskPercentage());
            values.put("recommendation", data.getRecommendation());
            values.put("created_date", DateConverter.getCurrentWesternDateTime());
            values.put("created_by", "");
            values.put("updated_date", DateConverter.getCurrentWesternDateTime());
            values.put("updated_by", "");
            values.put("visitno", data.getVisitNo());
            values.put("dateupdate", DateConverter.getCurrentWesternDateTime());

            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfCardiovascularRiskInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public CardiovascularRiskInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getCardiovascularRiskUriById(id), null, select, selectionArgs, null);
        CardiovascularRiskInfo data = new CardiovascularRiskInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setAge(cursor.getString(cursor.getColumnIndex("age")));
                data.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                data.setBloodPressure(cursor.getString(cursor.getColumnIndex("blood_pressure")));
                data.setWaistSize(cursor.getString(cursor.getColumnIndex("waist_size")));
                data.setHeight(cursor.getString(cursor.getColumnIndex("height")));
                data.setCholesterol(cursor.getString(cursor.getColumnIndex("cholesterol")));
                data.setIsSmoking(cursor.getString(cursor.getColumnIndex("is_smoking")));
                data.setHasDiabetes(cursor.getString(cursor.getColumnIndex("has_diabetes")));
                data.setRiskLevel(cursor.getString(cursor.getColumnIndex("risk_level")));
                data.setRiskPercentage(cursor.getString(cursor.getColumnIndex("risk_percentage")));
                data.setRecommendation(cursor.getString(cursor.getColumnIndex("recommendation")));
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

    public List<CardiovascularRiskInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getCardiovascularRiskUriById(personId), null, select, selectionArgs, null);
        List<CardiovascularRiskInfo> riskInfos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                CardiovascularRiskInfo data = new CardiovascularRiskInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setAge(cursor.getString(cursor.getColumnIndex("age")));
                data.setGender(cursor.getString(cursor.getColumnIndex("gender")));
                data.setBloodPressure(cursor.getString(cursor.getColumnIndex("blood_pressure")));
                data.setWaistSize(cursor.getString(cursor.getColumnIndex("waist_size")));
                data.setHeight(cursor.getString(cursor.getColumnIndex("height")));
                data.setCholesterol(cursor.getString(cursor.getColumnIndex("cholesterol")));
                data.setIsSmoking(cursor.getString(cursor.getColumnIndex("is_smoking")));
                data.setHasDiabetes(cursor.getString(cursor.getColumnIndex("has_diabetes")));
                data.setRiskLevel(cursor.getString(cursor.getColumnIndex("risk_level")));
                data.setRiskPercentage(cursor.getString(cursor.getColumnIndex("risk_percentage")));
                data.setRecommendation(cursor.getString(cursor.getColumnIndex("recommendation")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                data.setVisitNo(cursor.getString(cursor.getColumnIndex("visitno")));
                data.setDateUpdate(cursor.getString(cursor.getColumnIndex("dateupdate")));
                riskInfos.add(data);
            }
            cursor.close();
        }
        return riskInfos;
    }

    public int update(CardiovascularRiskInfo data) {
        String select = "ID=?";
        String[] selectionArgs = {data.getId()};
        ContentValues values = new ContentValues();

        values.put("person_info_id", data.getPersonId());
        values.put("idcard", data.getIdcard());
        values.put("age", data.getAge());
        values.put("gender", data.getGender());
        values.put("blood_pressure", data.getBloodPressure());
        values.put("waist_size", data.getWaistSize());
        values.put("height", data.getHeight());
        values.put("cholesterol", data.getCholesterol());
        values.put("is_smoking", data.getIsSmoking());
        values.put("has_diabetes", data.getHasDiabetes());
        values.put("risk_level", data.getRiskLevel());
        values.put("risk_percentage", data.getRiskPercentage());
        values.put("recommendation", data.getRecommendation());
        values.put("updated_date", DateConverter.getCurrentWesternDateTime());
        values.put("updated_by", data.getUpdated_by());
        values.put("visitno", data.getVisitNo());
        values.put("dateupdate", DateConverter.getCurrentWesternDateTime());

        mContext.getContentResolver().update(getCardiovascularRiskUriById(Integer.valueOf(data.getId())),
                values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getCardiovascularRiskUri(), select, selectionArgs);
        return 1;
    }
}