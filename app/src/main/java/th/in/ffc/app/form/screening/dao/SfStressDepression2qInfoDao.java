package th.in.ffc.app.form.screening.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfStressDepression2qInfoDao {
    private Context mContext;

    public static Uri getStressDepression2qUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfStressDepression2qInfo.CONTENT_URI, id.toString());
    }

    public static Uri getStressDepression2qUri() {
        return ScreeningFormProvider.SfStressDepression2qInfo.CONTENT_URI;
    }

    public SfStressDepression2qInfoDao(Context context) {
        this.mContext = context;
    }

    public String insert(StressDepression2qInfo data) {
        String id = "";
        try {
            ContentValues values = new ContentValues();
            values.put("person_info_id", data.getPersonId());
            values.put("idcard", data.getIdcard());
            values.put("q1", data.getQ1());
            values.put("q2", data.getQ2());
            values.put("points", data.getPoints().toString());
            values.put("sum", data.getSum());
//            values.put("result_code", data.getResultCode());
//            values.put("result_description", data.getResultDescription());
            values.put("created_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("created_by", "");
            values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            values.put("updated_by", "");

            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfStressDepression2qInfo.CONTENT_URI, values);
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return id;
    }

    public StressDepression2qInfo getById(Integer id) {
        String select = "ID = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getStressDepression2qUriById(id), null, select, selectionArgs, null);
        StressDepression2qInfo data = new StressDepression2qInfo();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setQ1(cursor.getString(cursor.getColumnIndex("q1")));
                data.setQ2(cursor.getString(cursor.getColumnIndex("q2")));

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
                data.setSum(cursor.getInt(cursor.getColumnIndex("sum")));
//                data.setResultCode(cursor.getString(cursor.getColumnIndex("result_code")));
//                data.setResultDescription(cursor.getString(cursor.getColumnIndex("result_description")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                cursor.close();
            }
        }
        return data;
    }

    public List<StressDepression2qInfo> getByPersonId(Integer personId) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{personId.toString()};
        Cursor cursor = mContext.getContentResolver().query(getStressDepression2qUriById(personId), null, select, selectionArgs, null);
        List<StressDepression2qInfo> stressDepressionInfos = new ArrayList<>();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                StressDepression2qInfo data = new StressDepression2qInfo();
                data.setId(cursor.getString(cursor.getColumnIndex("id")));
                data.setPersonId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                data.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                data.setQ1(cursor.getString(cursor.getColumnIndex("q1")));
                data.setQ2(cursor.getString(cursor.getColumnIndex("q2")));

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
                data.setSum(cursor.getInt(cursor.getColumnIndex("sum")));
//                data.setResultCode(cursor.getString(cursor.getColumnIndex("result_code")));
//                data.setResultDescription(cursor.getString(cursor.getColumnIndex("result_description")));
                data.setCreated_by(cursor.getString(cursor.getColumnIndex("created_by")));
                data.setCreated_date(cursor.getString(cursor.getColumnIndex("created_date")));
                data.setUpdated_by(cursor.getString(cursor.getColumnIndex("updated_by")));
                data.setUpdated_date(cursor.getString(cursor.getColumnIndex("updated_date")));
                stressDepressionInfos.add(data);
            }
        }
        return stressDepressionInfos;
    }

    public int update(StressDepression2qInfo data) {
        String select = "ID=?";
        String[] selectionArgs = {data.getId()};
        ContentValues values = new ContentValues();

        values.put("person_info_id", data.getPersonId());
        values.put("idcard", data.getIdcard());
        values.put("q1", data.getQ1());
        values.put("q2", data.getQ2());
        values.put("points", data.getPoints().toString());
        values.put("sum", data.getSum());
//        values.put("result_code", data.getResultCode());
//        values.put("result_description", data.getResultDescription());
        values.put("updated_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put("updated_by", "");

        mContext.getContentResolver().update(getStressDepression2qUriById(Integer.valueOf(data.getId())),
                values, select, selectionArgs);
        return 1;
    }

    public int delete(String id) {
        String select = "ID=?";
        String[] selectionArgs = {id};
        mContext.getContentResolver().delete(getStressDepression2qUri(), select, selectionArgs);
        return 1;
    }
}
