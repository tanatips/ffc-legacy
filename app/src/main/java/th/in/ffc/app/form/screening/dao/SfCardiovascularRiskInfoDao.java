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
    public long saveCardiovascularRiskInfo(CardiovascularRiskInfo cardioInfo) {
        try {
            // ตรวจสอบว่ามีข้อมูลอยู่แล้วหรือไม่
            List<CardiovascularRiskInfo> existingData = getByPersonId(Integer.valueOf(cardioInfo.getPersonId()));

            if (!existingData.isEmpty()) {
                // มีข้อมูลอยู่แล้ว - ทำการ update
                CardiovascularRiskInfo existing = existingData.get(0);
                cardioInfo.setId(existing.getId()); // ใช้ ID เดิม
                cardioInfo.setCreated_by(existing.getCreated_by()); // เก็บข้อมูลผู้สร้างเดิม
                cardioInfo.setCreated_date(existing.getCreated_date()); // เก็บวันที่สร้างเดิม

                int result = update(cardioInfo);
                if (result > 0) {
                    return Long.valueOf(existing.getId());
                } else {
                    return -1;
                }
            } else {
                // ไม่มีข้อมูล - ทำการ insert ใหม่
                String resultId = insert(cardioInfo);

                // ตรวจสอบว่า insert สำเร็จหรือไม่
                if (resultId != null && !resultId.isEmpty() && !resultId.startsWith("Error") && !resultId.contains("Exception")) {
                    try {
                        return Long.valueOf(resultId);
                    } catch (NumberFormatException e) {
                        // หาก resultId ไม่ใช่ตัวเลข แสดงว่าเป็น error message
                        return -1;
                    }
                } else {
                    return -1;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * บันทึกข้อมูลการประเมินโรคหัวใจและหลอดเลือดพร้อมข้อมูลผู้สร้าง
     *
     * @param cardioInfo ข้อมูลการประเมินโรคหัวใจและหลอดเลือด
     * @param userCreate ชื่อผู้สร้าง/อัปเดตข้อมูล
     * @return ID ของข้อมูลที่บันทึก หรือ -1 หากเกิดข้อผิดพลาด
     */
    public long saveCardiovascularRiskInfo(CardiovascularRiskInfo cardioInfo, String userCreate) {
        try {
            // ตั้งค่าข้อมูลผู้สร้าง/อัปเดต
            if (userCreate != null && !userCreate.isEmpty()) {
                cardioInfo.setUpdated_by(userCreate);

                // ถ้าเป็นข้อมูลใหม่ ให้ตั้งค่า created_by ด้วย
                List<CardiovascularRiskInfo> existingData = getByPersonId(Integer.valueOf(cardioInfo.getPersonId()));
                if (existingData.isEmpty()) {
                    cardioInfo.setCreated_by(userCreate);
                }
            }

            return saveCardiovascularRiskInfo(cardioInfo);

        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public boolean hasCardiovascularRiskData(Integer personId) {
        try {
            List<CardiovascularRiskInfo> data = getByPersonId(personId);
            return !data.isEmpty();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public CardiovascularRiskInfo getLatestByPersonId(Integer personId) {
        try {
            List<CardiovascularRiskInfo> dataList = getByPersonId(personId);
            if (!dataList.isEmpty()) {
                // หากมีหลายรายการ ให้เอาตัวแรก (สมมติว่าเรียงตามวันที่แล้ว)
                return dataList.get(0);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int deleteByPersonId(Integer personId) {
        try {
            String select = "person_info_id = ?";
            String[] selectionArgs = new String[]{personId.toString()};

            return mContext.getContentResolver().delete(
                    getCardiovascularRiskUri(),
                    select,
                    selectionArgs
            );
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public boolean validateRequiredFields(CardiovascularRiskInfo cardioInfo) {
        if (cardioInfo == null) return false;

        // ตรวจสอบ required fields
        if (isEmpty(cardioInfo.getPersonId())) return false;
        if (isEmpty(cardioInfo.getAge())) return false;
        if (isEmpty(cardioInfo.getGender())) return false;
        if (isEmpty(cardioInfo.getCholesterol())) return false;
        if (isEmpty(cardioInfo.getRiskPercentage())) return false;
        if (isEmpty(cardioInfo.getRiskLevel())) return false;

        // ตรวจสอบว่าค่าตัวเลขถูกต้องหรือไม่
        try {
            // ตรวจสอบ Cholesterol
            double cholesterol = Double.parseDouble(cardioInfo.getCholesterol());
            if (cholesterol <= 0) return false;

            // ตรวจสอบ Risk Percentage
            double riskPercentage = Double.parseDouble(cardioInfo.getRiskPercentage());
            if (riskPercentage < 0 || riskPercentage > 100) return false;

            // ตรวจสอบ Risk Level
            double riskLevel = Double.parseDouble(cardioInfo.getRiskLevel());
            if (riskLevel < 0) return false;

            // ตรวจสอบ Age
            int age = Integer.parseInt(cardioInfo.getAge());
            if (age <= 0 || age > 150) return false;

        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    /**
     * Helper method สำหรับตรวจสอบ string ว่าง
     */
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
    public CardiovascularRiskInfo getByPersonIdAndVisitNo(Integer personId, String visitNo) {
        try {
            String select = "person_info_id = ? AND visitno = ?";
            String[] selectionArgs = new String[]{personId.toString(), visitNo};

            Cursor cursor = mContext.getContentResolver().query(
                    getCardiovascularRiskUri(),
                    null,
                    select,
                    selectionArgs,
                    null
            );

            CardiovascularRiskInfo data = null;

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    data = new CardiovascularRiskInfo();
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
                }
                cursor.close();
            }

            return data;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public int countByPersonId(Integer personId) {
        try {
            List<CardiovascularRiskInfo> data = getByPersonId(personId);
            return data.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}