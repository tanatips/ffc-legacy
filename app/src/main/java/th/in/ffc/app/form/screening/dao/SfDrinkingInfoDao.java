package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.util.DateConverter;

public class SfDrinkingInfoDao {
    private Context mContext;

    public static Uri getDrinkingUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfDrinkingInfo.CONTENT_URI, id.toString());
    }

    public static Uri getDrinkingUri() {
        return ScreeningFormProvider.SfDrinkingInfo.CONTENT_URI;
    }

    public SfDrinkingInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(DrinkingInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("drinking", data.getDrinking());
            values.put("drinking_frequency", data.getDrinkingFrequency());
            values.put("drinking_alway", data.getDrinkingAlway());
            values.put("created_date", DateConverter.getCurrentWesternDateTime());
            values.put("created_by", data.getCreated_by());
            values.put("updated_date", DateConverter.getCurrentWesternDateTime());
            values.put("updated_by", data.getUpdated_by());
            values.put("visitno", data.getVisitNo());
            values.put("dateupdate", DateConverter.getCurrentWesternDateTime());
            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfDrinkingInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public DrinkingInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getDrinkingUriById(id), null, select, selectionArgs, null);
        DrinkingInfo data = new DrinkingInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setDrinking(cursor.getString(cursor.getColumnIndex("drinking")));
                data.setDrinkingFrequency(cursor.getString(cursor.getColumnIndex("drinking_frequency")));
                data.setDrinkingAlway(cursor.getString(cursor.getColumnIndex("drinking_alway")));
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

    public List<DrinkingInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getDrinkingUriById(personId), null, select, selectionArgs, null);
        List<DrinkingInfo> drinkingInfos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DrinkingInfo data = new DrinkingInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setDrinking(cursor.getString(cursor.getColumnIndex("drinking")));
                data.setDrinkingFrequency(cursor.getString(cursor.getColumnIndex("drinking_frequency")));
                data.setDrinkingAlway(cursor.getString(cursor.getColumnIndex("drinking_alway")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                data.setVisitNo(cursor.getString(cursor.getColumnIndex("visitno")));
                data.setDateUpdate(cursor.getString(cursor.getColumnIndex("dateupdate")));
                drinkingInfos.add(data);
            }
        }
        return drinkingInfos;
    }

    public int update(DrinkingInfo drinkingInfo) {
        String select = "ID=?";
        String[] selectionArgs = {drinkingInfo.getId()};
        ContentValues values = new ContentValues();

        values.put("person_info_id", drinkingInfo.getPersonId());
        values.put("idcard", drinkingInfo.getIdcard());
        values.put("drinking", drinkingInfo.getDrinking());
        values.put("drinking_frequency", drinkingInfo.getDrinkingFrequency());
        values.put("drinking_alway", drinkingInfo.getDrinkingAlway());
        values.put("updated_date", DateConverter.getCurrentWesternDateTime());
        values.put("updated_by", drinkingInfo.getUpdated_by());
        values.put("visitno", drinkingInfo.getVisitNo());
        values.put("dateupdate", DateConverter.getCurrentWesternDateTime());

        mContext.getContentResolver().update(getDrinkingUriById(Integer.valueOf(drinkingInfo.getId())),
                values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getDrinkingUri(), select, selectionArgs);
        return 1;
    }
}