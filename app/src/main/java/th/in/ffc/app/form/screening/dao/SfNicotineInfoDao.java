package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.util.DateConverter;

public class SfNicotineInfoDao {
    private Context mContext;

    public static Uri getNicotineUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfNicotineInfo.CONTENT_URI, id.toString());
    }

    public static Uri getNicotineUri() {
        return ScreeningFormProvider.SfNicotineInfo.CONTENT_URI;
    }

    public SfNicotineInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(NicotineInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("nicotine1", data.getNicotine1());
            values.put("nicotine2", data.getNicotine2());
            values.put("nicotine3", data.getNicotine3());
            values.put("nicotine4", data.getNicotine4());
            values.put("nicotine5", data.getNicotine5());
            values.put("nicotine6", data.getNicotine6());
            values.put("points", data.getPoints().toString());
            values.put("sum", data.getSum());
            values.put("created_date", DateConverter.getCurrentWesternDateTime());
            values.put("created_by", "");
            values.put("updated_date", DateConverter.getCurrentWesternDateTime());
            values.put("updated_by", "");
            values.put("visitno", data.getVisitNo());
            values.put("dateupdate", DateConverter.getCurrentWesternDateTime());
            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfNicotineInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public NicotineInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getNicotineUriById(id), null, select, selectionArgs, null);
        NicotineInfo data = new NicotineInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));

                data.setNicotine1(cursor.getString(cursor.getColumnIndex("nicotine1")));
                data.setNicotine2(cursor.getString(cursor.getColumnIndex("nicotine2")));
                data.setNicotine3(cursor.getString(cursor.getColumnIndex("nicotine3")));
                data.setNicotine4(cursor.getString(cursor.getColumnIndex("nicotine4")));
                data.setNicotine5(cursor.getString(cursor.getColumnIndex("nicotine5")));
                data.setNicotine6(cursor.getString(cursor.getColumnIndex("nicotine6")));

                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                data.setVisitNo(cursor.getString(cursor.getColumnIndex("visitno")));
                data.setDateUpdate(cursor.getString(cursor.getColumnIndex("dateupdate")));
                data.setSum(cursor.getInt(cursor.getColumnIndex("sum")));

                // แปลง String points กลับเป็น ArrayList<Integer>
                String pointsStr = cursor.getString(cursor.getColumnIndex("points"));
                ArrayList<Integer> points = new ArrayList<>();
                if (pointsStr != null && !pointsStr.isEmpty()) {
                    String[] pointArray = pointsStr.replaceAll("\\[|\\]", "").split(",");
                    for (String point : pointArray) {
                        points.add(Integer.parseInt(point.trim()));
                    }
                }
                data.setPoints(points);
                cursor.close();
            }
        }
        return data;
    }

    public List<NicotineInfo> getByPersonId(Integer id) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getNicotineUriById(id), null, select, selectionArgs, null);

        List<NicotineInfo> nicotineInfos = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                NicotineInfo data = new NicotineInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));

                data.setNicotine1(cursor.getString(cursor.getColumnIndex("nicotine1")));
                data.setNicotine2(cursor.getString(cursor.getColumnIndex("nicotine2")));
                data.setNicotine3(cursor.getString(cursor.getColumnIndex("nicotine3")));
                data.setNicotine4(cursor.getString(cursor.getColumnIndex("nicotine4")));
                data.setNicotine5(cursor.getString(cursor.getColumnIndex("nicotine5")));
                data.setNicotine6(cursor.getString(cursor.getColumnIndex("nicotine6")));

                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                data.setSum(cursor.getInt(cursor.getColumnIndex("sum")));
                data.setVisitNo(cursor.getString(cursor.getColumnIndex("visitno")));
                data.setDateUpdate(cursor.getString(cursor.getColumnIndex("dateupdate")));
                // แปลง String points กลับเป็น ArrayList<Integer>
                String pointsStr = cursor.getString(cursor.getColumnIndex("points"));

                ArrayList<Integer> points = new ArrayList<>();
                if (pointsStr != null && !pointsStr.isEmpty()) {
                    String[] pointArray = pointsStr.replaceAll("\\[|\\]", "").split(",");
                    for (String point : pointArray) {
                        points.add(Integer.parseInt(point.trim()));
                    }
                }
                data.setPoints(points);
                nicotineInfos.add(data);
            }
            cursor.close();
        }
        return nicotineInfos;
    }

    public int update(NicotineInfo nicotineInfo) {
        String select = "ID=?";
        String[] selectionArgs = {nicotineInfo.getId()};
        ContentValues values = new ContentValues();

        values.put("nicotine1", nicotineInfo.getNicotine1());
        values.put("nicotine2", nicotineInfo.getNicotine2());
        values.put("nicotine3", nicotineInfo.getNicotine3());
        values.put("nicotine4", nicotineInfo.getNicotine4());
        values.put("nicotine5", nicotineInfo.getNicotine5());
        values.put("nicotine6", nicotineInfo.getNicotine6());
        values.put("points", nicotineInfo.getPoints().toString());
        values.put("sum", nicotineInfo.getSum());
        values.put("updated_date", DateConverter.getCurrentWesternDateTime());
        values.put("updated_by", nicotineInfo.getUpdated_by());
        values.put("visitno", nicotineInfo.getVisitNo());
        values.put("dateupdate", DateConverter.getCurrentWesternDateTime());

        mContext.getContentResolver().update(getNicotineUriById(Integer.valueOf(nicotineInfo.getId())),
                values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getNicotineUri(), select, selectionArgs);
        return 1;
    }
}