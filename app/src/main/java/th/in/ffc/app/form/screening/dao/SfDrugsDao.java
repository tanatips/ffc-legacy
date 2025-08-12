package th.in.ffc.app.form.screening.dao;

import static th.in.ffc.util.DataFromCursor.getStringFromCursor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import th.in.ffc.app.form.screening.model.DrugsInfo;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.util.DateConverter;

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
    public static int deleteDrugsByPersonIdAndQuestion(String personInfoId, String question) {
        try {
            String select = "person_info_id = ? AND question = ?";
            String[] selectionArgs = {personInfoId, question};

            int deletedRows = mContext.getContentResolver().delete(
                    getDrugsUriAppend("list"),
                    select,
                    selectionArgs
            );

            Log.d("SfDrugsDao", "Deleted " + deletedRows + " rows for personId: " +
                    personInfoId + ", question: " + question);
            return deletedRows;
        } catch (Exception e) {
            Log.e("SfDrugsDao", "Error deleting drugs info: " + e.getMessage());
            return 0;
        }
    }
    public static int deleteDrugsByPersonIdAndQuestionWithParams(String personInfoId, String question) {
        try {
            Uri deleteUri = createDeleteUriWithParams(personInfoId, question);

            int deletedRows = mContext.getContentResolver().delete(deleteUri, null, null);

            Log.d("SfDrugsDao", "Deleted " + deletedRows + " rows using parameters - PersonId: " +
                    personInfoId + ", Question: " + question);
            return deletedRows;
        } catch (Exception e) {
            Log.e("SfDrugsDao", "Error deleting drugs info with parameters: " + e.getMessage());
            return 0;
        }
    }
    public static Uri createDeleteUriWithParams(String personId, String question) {
        Uri.Builder builder = ScreeningFormProvider.SfDrugs.CONTENT_URI.buildUpon();

        if (personId != null) {
            builder.appendQueryParameter("person_id", personId);
        }

        if (question != null) {
            builder.appendQueryParameter("question", question);
        }

        return builder.build();
    }

    public static int deleteDrugsByPersonIdAndQuestions(String personInfoId, String[] questions) {
        int totalDeleted = 0;

        for (String question : questions) {
            totalDeleted += deleteDrugsByPersonIdAndQuestionWithParams(personInfoId, question);
        }

        Log.d("SfDrugsDao", "Total deleted " + totalDeleted + " rows for personId: " +
                personInfoId + ", questions: " + java.util.Arrays.toString(questions));
        return totalDeleted;
    }
    public static boolean hasDrugsData(String personInfoId, String question) {
        try {
            StringBuilder select = new StringBuilder("person_info_id = ?");
            List<String> selectionArgsList = new ArrayList<>();
            selectionArgsList.add(personInfoId);

            if (question != null && !question.isEmpty()) {
                select.append(" AND question = ?");
                selectionArgsList.add(question);
            }

            String[] selectionArgs = selectionArgsList.toArray(new String[0]);

            Cursor cursor = mContext.getContentResolver().query(
                    getDrugsUriAppend("list"),
                    new String[]{"COUNT(*) as count"},
                    select.toString(),
                    selectionArgs,
                    null
            );

            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        int count = cursor.getInt(0);
                        return count > 0;
                    }
                } finally {
                    cursor.close();
                }
            }

            return false;
        } catch (Exception e) {
            Log.e("SfDrugsDao", "Error checking drugs data: " + e.getMessage());
            return false;
        }
    }

    public static int deleteDrugsByPersonIdQuestionAndSubquestion(String personInfoId, String question, String subquestion) {
        try {
            String select = "person_info_id = ? AND question = ? AND subquestion = ?";
            String[] selectionArgs = {personInfoId, question, subquestion};

            int deletedRows = mContext.getContentResolver().delete(
                    getDrugsUriAppend("list"),
                    select,
                    selectionArgs
            );

            Log.d("SfDrugsDao", "Deleted " + deletedRows + " rows for personId: " +
                    personInfoId + ", question: " + question + ", subquestion: " + subquestion);
            return deletedRows;
        } catch (Exception e) {
            Log.e("SfDrugsDao", "Error deleting specific drugs info: " + e.getMessage());
            return 0;
        }
    }


    public static int deleteAllDrugsByPersonId(String personInfoId) {
        try {
            String select = "person_info_id = ?";
            String[] selectionArgs = {personInfoId};

            int deletedRows = mContext.getContentResolver().delete(
                    getDrugsUriAppend("list"),
                    select,
                    selectionArgs
            );

            Log.d("SfDrugsDao", "Deleted all " + deletedRows + " drugs records for personId: " + personInfoId);
            return deletedRows;
        } catch (Exception e) {
            Log.e("SfDrugsDao", "Error deleting all drugs info: " + e.getMessage());
            return 0;
        }
    }
    public static List<DrugsInfo> getSfDrugsAll() {
        Cursor cursor = mContext.getContentResolver().query(getDrugsUriAppend("list"), null, null, null, null);
        List<DrugsInfo> drugsList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DrugsInfo drugs = new DrugsInfo();
//                drugs.setId(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.ID));
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
//                drugs.setId(getStringFromCursor(cursor, ScreeningFormProvider.SfDrugs.ID));
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
    // deleteAllDrugsInfoByPersonId
    public static int deleteAllDrugsInfoByPersonId(String personInfoId,String question) {
        try {
            String select = "PERSON_INFO_ID=? AND QUESTION=?";
            String[] selectionArgs = {personInfoId, question};
            return mContext.getContentResolver().delete(getDrugsUriAppend("list"), select, selectionArgs);
        } catch (Exception e) {
            Log.e("SfDrugsDao", "Error deleting drugs info by person ID: " + e.getMessage());
            return 0;
        }
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
        putString(values,"VISITNO",drugs.getVisitNo());
        putString(values, "DATEUPDATE", DateConverter.getCurrentWesternDateTime());
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
    /**
     * ฟังก์ชั่นสำหรับดึงข้อมูลสรุปของยาเสพติด โดยจัดกลุ่มตาม subquestion
     *
     * @param personInfoId รหัสของบุคคลที่ต้องการดึงข้อมูล
     * @param question คำถามที่ต้องการ filter (เช่น "Q5") หากเป็น null จะไม่มีการ filter
     * @return List ของ Map โดยแต่ละ Map มี key คือ "subquestion" และ "total"
     */
    public static List<Map<String, Object>> getSummaryBySubquestion(Integer personInfoId, String question) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        // สร้าง URI สำหรับ summary
        Uri uri = Uri.withAppendedPath(ScreeningFormProvider.SfDrugs.CONTENT_URI, "summary");

        // สร้างเงื่อนไขสำหรับ query
        StringBuilder selection = new StringBuilder("person_info_id = ?");
        List<String> selectionArgsList = new ArrayList<>();
        selectionArgsList.add(personInfoId.toString());

        if (question != null && !question.isEmpty()) {
            selection.append(" AND question = ?");
            selectionArgsList.add(question);
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        // ทำการ query
        Cursor cursor = mContext.getContentResolver().query(
                uri,
                new String[]{"subquestion", "total"},
                selection.toString(),
                selectionArgs,
                "subquestion ASC"
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("subquestion", cursor.getString(cursor.getColumnIndex("subquestion")));
                    item.put("total", cursor.getInt(cursor.getColumnIndex("total")));
                    resultList.add(item);
                }
            } finally {
                cursor.close();
            }
        }

        return resultList;
    }

    /**
     * ฟังก์ชั่นสำหรับดึงข้อมูลสรุปของยาเสพติด และแปลงเป็น Map ของ subquestion และผลรวม
     *
     * @param personInfoId รหัสของบุคคลที่ต้องการดึงข้อมูล
     * @param question คำถามที่ต้องการ filter (เช่น "Q5") หากเป็น null จะไม่มีการ filter
     * @return Map โดยมี key เป็น subquestion และ value เป็นผลรวมของคำตอบ
     */
    public static Map<String, Integer> getSummaryMapBySubquestion(Integer personInfoId, String question) {
        Map<String, Integer> resultMap = new HashMap<>();

        List<Map<String, Object>> summaryList = getSummaryBySubquestion(personInfoId, question);
        for (Map<String, Object> item : summaryList) {
            String subquestion = (String) item.get("subquestion");
            Integer total = (Integer) item.get("total");
            resultMap.put(subquestion, total);
        }

        return resultMap;
    }

    /**
     * ฟังก์ชั่นที่ใช้วิธีการดึงข้อมูลทั้งหมดแล้วทำการรวมเอง (alternative approach)
     * เหมาะสำหรับกรณีที่ไม่สามารถใช้ GROUP BY ผ่าน ContentProvider ได้
     *
     * @param personInfoId รหัสของบุคคลที่ต้องการดึงข้อมูล
     * @param question คำถามที่ต้องการ filter (เช่น "Q5") หากเป็น null จะไม่มีการ filter
     * @return Map โดยมี key เป็น subquestion และ value เป็นผลรวมของคำตอบ
     */
    public static Map<String, Integer> calculateSummaryManually(Integer personInfoId, String question) {
        Map<String, Integer> summaryMap = new HashMap<>();

        StringBuilder whereClause = new StringBuilder("person_info_id = ?");
        List<String> whereArgs = new ArrayList<>();
        whereArgs.add(personInfoId.toString());

        if (question != null && !question.isEmpty()) {
            whereClause.append(" AND question = ?");
            whereArgs.add(question);
        }

        Cursor cursor = mContext.getContentResolver().query(
                ScreeningFormProvider.SfDrugs.CONTENT_URI,
                null,
                whereClause.toString(),
                whereArgs.toArray(new String[0]),
                null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String subquestion = cursor.getString(cursor.getColumnIndex(ScreeningFormProvider.SfDrugs.SUBQUESTION));
                    String answerStr = cursor.getString(cursor.getColumnIndex(ScreeningFormProvider.SfDrugs.ANSWER));

                    if (subquestion != null && answerStr != null && !answerStr.isEmpty()) {
                        try {
                            int answer = Integer.parseInt(answerStr);

                            // เพิ่มค่าไปยัง summaryMap
                            if (summaryMap.containsKey(subquestion)) {
                                summaryMap.put(subquestion, summaryMap.get(subquestion) + answer);
                            } else {
                                summaryMap.put(subquestion, answer);
                            }
                        } catch (NumberFormatException e) {
                            // ข้ามกรณีที่ answer ไม่ใช่ตัวเลข
                        }
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return summaryMap;
    }
}