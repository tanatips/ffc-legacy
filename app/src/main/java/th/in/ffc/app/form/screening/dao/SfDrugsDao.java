package th.in.ffc.app.form.screening.dao;

import static th.in.ffc.util.DataFromCursor.getStringFromCursor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.provider.ScreeningFormProvider;

public class SfDrugsDao {

    private static Context mContext;

    public SfDrugsDao(Context context) {
        this.mContext = context;
    }

    public static Uri getDrugsUriById(Integer id) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfDrugs.CONTENT_URI, id.toString());
    }

    public static Uri getDrugsUriAppend(String name) {
        return Uri.withAppendedPath(ScreeningFormProvider.SfDrugs.CONTENT_URI, name);
    }

    public static List<DrugsInfo> getSfDrugsAll() {
        Cursor cursor = mContext.getContentResolver().query(getDrugsUriAppend("list"), null, null, null, null);
        List<DrugsInfo> drugsList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DrugsInfo drugs = new DrugsInfo();
                drugs.setId(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.ID));
                drugs.setPersonInfoId(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.PERSON_INFO_ID));
                drugs.setQuestion(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.QUESTION));
                drugs.setSubquestion(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.SUBQUESTION));
                drugs.setOtherDrugs(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.OTHER_DRUGS));
                drugs.setAnswer(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.ANSWER));
                drugs.setCreatedBy(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.CREATED_BY));
                drugs.setCreatedDate(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.CREATED_DATE));
                drugs.setUpdatedBy(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.UPDATED_BY));
                drugs.setUpdatedDate(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.UPDATED_DATE));
                drugs.setIdcard(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.IDCARD));
                drugsList.add(drugs);
            }
            cursor.close();
        }
        return drugsList;
    }

    public static List<DrugsInfo> getSfDrugsByPersonInfoId(Integer id) {
        String select = "person_info_id = ?";
        String[] selectionArgs = new String[]{id.toString()};
        Cursor cursor = mContext.getContentResolver().query(getDrugsUriById(id), null, select, selectionArgs, null);
        List<DrugsInfo> drugsList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DrugsInfo drugs = new DrugsInfo();
                drugs.setId(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.ID));
                drugs.setPersonInfoId(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.PERSON_INFO_ID));
                drugs.setQuestion(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.QUESTION));
                drugs.setSubquestion(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.SUBQUESTION));
                drugs.setOtherDrugs(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.OTHER_DRUGS));
                drugs.setAnswer(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.ANSWER));
                drugs.setCreatedBy(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.CREATED_BY));
                drugs.setCreatedDate(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.CREATED_DATE));
                drugs.setUpdatedBy(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.UPDATED_BY));
                drugs.setUpdatedDate(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.UPDATED_DATE));
                drugs.setIdcard(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.IDCARD));
                drugsList.add(drugs);
            }
            cursor.close();
        }
        return drugsList;
    }

    public static long update(DrugsInfo drugs) {
        try {
            String select = "ID=?";
            String[] selectionArgs = {drugs.getId()};
            ContentValues values = new ContentValues();
            values = getPutvalueDrugs(drugs, values);
            putString(values, "ID", drugs.getId());
            mContext.getContentResolver().update(getDrugsUriById(Integer.valueOf(drugs.getId())), values, select, selectionArgs);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String insert(DrugsInfo drugs) {
        try {
            ContentValues values = new ContentValues();
            values = getPutvalueDrugs(drugs, values);
            Uri uri = mContext.getContentResolver().insert(ScreeningFormProvider.SfDrugs.CONTENT_URI, values);
            String id = "";
            if (uri != null) {
                id = uri.getLastPathSegment();
            }
            return id;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    private static ContentValues getPutvalueDrugs(DrugsInfo drugs, ContentValues values) {
        putString(values, "PERSON_INFO_ID", drugs.getPersonInfoId());
        putString(values, "QUESTION", drugs.getQuestion());
        putString(values, "SUBQUESTION", drugs.getSubquestion());
        putString(values, "OTHER_DRUGS", drugs.getOtherDrugs());
        putString(values, "ANSWER", drugs.getAnswer());
        putString(values, "CREATED_BY", drugs.getCreatedBy());
        putString(values, "CREATED_DATE", drugs.getCreatedDate());
        putString(values, "UPDATED_BY", drugs.getUpdatedBy());
        putString(values, "UPDATED_DATE", drugs.getUpdatedDate());
        putString(values, "IDCARD", drugs.getIdcard());
        return values;
    }

    public static List<DrugsInfo> searchDrugs(String personInfoId, String question, String idcard) {
        List<DrugsInfo> results = new ArrayList<>();

        List<String> whereConditions = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        if (personInfoId != null && !personInfoId.isEmpty()) {
            whereConditions.add("person_info_id LIKE ?");
            whereArgs.add("%" + personInfoId + "%");
        }

        if (question != null && !question.isEmpty()) {
            whereConditions.add("question LIKE ?");
            whereArgs.add("%" + question + "%");
        }

        if (idcard != null && !idcard.isEmpty()) {
            whereConditions.add("idcard LIKE ?");
            whereArgs.add("%" + idcard + "%");
        }

        String whereClause = null;
        if (!whereConditions.isEmpty()) {
            whereClause = TextUtils.join(" AND ", whereConditions);
        }

        String[] selectionArgs = whereArgs.toArray(new String[0]);

        Cursor cursor = mContext.getContentResolver().query(
                getDrugsUriAppend("list"),
                null,
                whereClause,
                selectionArgs,
                "question ASC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                DrugsInfo drugs = new DrugsInfo();
                drugs.setId(cursor.getString(cursor.getColumnIndex("id")));
                drugs.setPersonInfoId(cursor.getString(cursor.getColumnIndex("person_info_id")));
                drugs.setQuestion(cursor.getString(cursor.getColumnIndex("question")));
                drugs.setSubquestion(cursor.getString(cursor.getColumnIndex("subquestion")));
                drugs.setAnswer(cursor.getString(cursor.getColumnIndex("answer")));
                drugs.setOtherDrugs(cursor.getString(cursor.getColumnIndex("other_drugs")));
                drugs.setCreatedBy(cursor.getString(cursor.getColumnIndex("created_by")));
                drugs.setCreatedDate(cursor.getString(cursor.getColumnIndex("created_date")));
                drugs.setUpdatedBy(cursor.getString(cursor.getColumnIndex("updated_by")));
                drugs.setUpdatedDate(cursor.getString(cursor.getColumnIndex("updated_date")));
                drugs.setIdcard(cursor.getString(cursor.getColumnIndex("idcard")));
                results.add(drugs);
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
}