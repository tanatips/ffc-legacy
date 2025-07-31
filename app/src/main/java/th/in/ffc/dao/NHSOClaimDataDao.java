package th.in.ffc.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.provider.NHSOClaimData;
import th.in.ffc.provider.NHSOClaimDataProvider;

/**
 * DAO สำหรับจัดการข้อมูลการส่ง claim ไปยัง สปสช
 */
public class NHSOClaimDataDao {
    private static final String TAG = "NHSOClaimDataDao";
    private ContentResolver contentResolver;
    private Context context;
    private SimpleDateFormat dateFormat;

    public NHSOClaimDataDao(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    }

    /**
     * บันทึกข้อมูล JSON claim ใหม่
     *
     * @param visitno    หมายเลขการเยี่ยม
     * @param jsonObject ข้อมูล JSON ที่จะส่งไป NHSO
     * @return URI ของข้อมูลที่บันทึก หรือ null หากไม่สำเร็จ
     */
    public Uri saveClaimData(int visitno, JSONObject jsonObject) {
        try {
            String currentDateTime = dateFormat.format(new Date());

            ContentValues values = NHSOClaimData.ClaimData.createClaimRecord(
                    visitno,
                    jsonObject.toString(),
                    currentDateTime
            );

            Uri result = NHSOClaimDataProvider.insertClaimData(contentResolver, values);

            if (result != null) {
                Log.d(TAG, "Claim data saved successfully for visitno: " + visitno);
            } else {
                Log.e(TAG, "Failed to save claim data for visitno: " + visitno);
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error saving claim data for visitno: " + visitno, e);
            return null;
        }
    }

    /**
     * ดึงข้อมูล claim ตาม visitno
     *
     * @param visitno หมายเลขการเยี่ยม
     * @return NHSOClaimDataInfo หรือ null หากไม่พบข้อมูล
     */
    public NHSOClaimDataInfo getClaimDataByVisitNo(int visitno) {
        Cursor cursor = null;
        try {
            cursor = NHSOClaimDataProvider.getClaimDataByVisitNo(contentResolver, visitno);

            if (cursor != null && cursor.moveToFirst()) {
                return createClaimDataInfoFromCursor(cursor);
            }

            Log.d(TAG, "No claim data found for visitno: " + visitno);
            return null;

        } catch (Exception e) {
            Log.e(TAG, "Error getting claim data for visitno: " + visitno, e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * อัพเดทสถานะเป็นสำเร็จ
     *
     * @param visitno      หมายเลขการเยี่ยม
     * @param seq          SEQ number ที่ได้จาก NHSO
     * @param responseData Response จาก NHSO API
     * @return จำนวนแถวที่อัพเดท
     */
    public int updateSuccessStatus(int visitno, String seq, String responseData) {
        try {
            String currentDateTime = dateFormat.format(new Date());

            ContentValues values = NHSOClaimData.ClaimData.updateSuccessStatus(
                    seq, responseData, currentDateTime
            );

            int result = NHSOClaimDataProvider.updateClaimStatusByVisitNo(
                    contentResolver, visitno, values
            );

            Log.d(TAG, "Updated success status for visitno: " + visitno + ", rows affected: " + result);
            return result;

        } catch (Exception e) {
            Log.e(TAG, "Error updating success status for visitno: " + visitno, e);
            return 0;
        }
    }

    /**
     * อัพเดทสถานะเป็นไม่สำเร็จ
     *
     * @param visitno      หมายเลขการเยี่ยม
     * @param errorMessage ข้อความ error
     * @return จำนวนแถวที่อัพเดท
     */
    public int updateFailedStatus(int visitno, String errorMessage) {
        try {
            String currentDateTime = dateFormat.format(new Date());

            ContentValues values = NHSOClaimData.ClaimData.updateFailedStatus(
                    errorMessage, currentDateTime
            );

            int result = NHSOClaimDataProvider.updateClaimStatusByVisitNo(
                    contentResolver, visitno, values
            );

            Log.d(TAG, "Updated failed status for visitno: " + visitno + ", rows affected: " + result);
            return result;

        } catch (Exception e) {
            Log.e(TAG, "Error updating failed status for visitno: " + visitno, e);
            return 0;
        }
    }

    /**
     * ดึงข้อมูล claim ทั้งหมดที่ยังไม่ได้ส่ง
     *
     * @return List ของ NHSOClaimDataInfo
     */
    public List<NHSOClaimDataInfo> getUnsentClaimData() {
        List<NHSOClaimDataInfo> result = new ArrayList<>();
        Cursor cursor = null;

        try {
            String selection = NHSOClaimData.CLAIM_STATUS + "=?";
            String[] selectionArgs = {NHSOClaimData.CLAIM_STATUS_NOT_SENT};
            String sortOrder = NHSOClaimData.CREATED_DATE + " ASC";

            cursor = contentResolver.query(
                    NHSOClaimData.CONTENT_LIST_URI,
                    null,
                    selection,
                    selectionArgs,
                    sortOrder
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    result.add(createClaimDataInfoFromCursor(cursor));
                }
            }

            Log.d(TAG, "Found " + result.size() + " unsent claim data records");

        } catch (Exception e) {
            Log.e(TAG, "Error getting unsent claim data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    /**
     * ลบข้อมูล claim ตาม visitno
     *
     * @param visitno หมายเลขการเยี่ยม
     * @return จำนวนแถวที่ลบ
     */
    public int deleteClaimData(int visitno) {
        try {
            int result = NHSOClaimDataProvider.deleteClaimDataByVisitNo(contentResolver, visitno);
            Log.d(TAG, "Deleted claim data for visitno: " + visitno + ", rows affected: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting claim data for visitno: " + visitno, e);
            return 0;
        }
    }

    /**
     * ตรวจสอบว่ามีข้อมูล claim สำหรับ visitno นี้หรือไม่
     *
     * @param visitno หมายเลขการเยี่ยม
     * @return true หากมีข้อมูล, false หากไม่มี
     */
    public boolean hasClaimData(int visitno) {
        Cursor cursor = null;
        try {
            cursor = NHSOClaimDataProvider.getClaimDataByVisitNo(contentResolver, visitno);
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking claim data existence for visitno: " + visitno, e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * สร้าง NHSOClaimDataInfo จาก Cursor
     */
    private NHSOClaimDataInfo createClaimDataInfoFromCursor(Cursor cursor) {
        NHSOClaimDataInfo info = new NHSOClaimDataInfo();

        info.setId(cursor.getLong(cursor.getColumnIndex(NHSOClaimData.ID)));
        info.setVisitno(cursor.getInt(cursor.getColumnIndex(NHSOClaimData.VISITNO)));
        info.setJsonData(cursor.getString(cursor.getColumnIndex(NHSOClaimData.JSON_DATA)));
        info.setSeq(cursor.getString(cursor.getColumnIndex(NHSOClaimData.SEQ)));
        info.setClaimStatus(cursor.getString(cursor.getColumnIndex(NHSOClaimData.CLAIM_STATUS)));
        info.setResponseData(cursor.getString(cursor.getColumnIndex(NHSOClaimData.RESPONSE_DATA)));
        info.setErrorMessage(cursor.getString(cursor.getColumnIndex(NHSOClaimData.ERROR_MESSAGE)));
        info.setCreatedDate(cursor.getString(cursor.getColumnIndex(NHSOClaimData.CREATED_DATE)));
        info.setUpdatedDate(cursor.getString(cursor.getColumnIndex(NHSOClaimData.UPDATED_DATE)));
        info.setSendDate(cursor.getString(cursor.getColumnIndex(NHSOClaimData.SEND_DATE)));

        return info;
    }

    /**
     * คลาสสำหรับเก็บข้อมูล claim data
     */
    public static class NHSOClaimDataInfo {
        private long id;
        private int visitno;
        private String jsonData;
        private String seq;
        private String claimStatus;
        private String responseData;
        private String errorMessage;
        private String createdDate;
        private String updatedDate;
        private String sendDate;

        // Getters
        public long getId() {
            return id;
        }

        public int getVisitno() {
            return visitno;
        }

        public String getJsonData() {
            return jsonData;
        }

        public String getSeq() {
            return seq;
        }

        public String getClaimStatus() {
            return claimStatus;
        }

        public String getResponseData() {
            return responseData;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public String getUpdatedDate() {
            return updatedDate;
        }

        public String getSendDate() {
            return sendDate;
        }

        // Setters
        public void setId(long id) {
            this.id = id;
        }

        public void setVisitno(int visitno) {
            this.visitno = visitno;
        }

        public void setJsonData(String jsonData) {
            this.jsonData = jsonData;
        }

        public void setSeq(String seq) {
            this.seq = seq;
        }

        public void setClaimStatus(String claimStatus) {
            this.claimStatus = claimStatus;
        }

        public void setResponseData(String responseData) {
            this.responseData = responseData;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public void setUpdatedDate(String updatedDate) {
            this.updatedDate = updatedDate;
        }

        public void setSendDate(String sendDate) {
            this.sendDate = sendDate;
        }

        /**
         * ตรวจสอบว่าการส่ง claim สำเร็จหรือไม่
         */
        public boolean isSuccess() {
            return NHSOClaimData.CLAIM_STATUS_SUCCESS.equals(claimStatus);
        }

        /**
         * ตรวจสอบว่าการส่ง claim ล้มเหลวหรือไม่
         */
        public boolean isFailed() {
            return NHSOClaimData.CLAIM_STATUS_FAILED.equals(claimStatus);
        }

        /**
         * ตรวจสอบว่ายังไม่ได้ส่ง claim หรือไม่
         */
        public boolean isNotSent() {
            return NHSOClaimData.CLAIM_STATUS_NOT_SENT.equals(claimStatus);
        }

        /**
         * แปลงข้อมูล JSON กลับเป็น JSONObject
         */
        public JSONObject getJsonObject() {
            try {
                return new JSONObject(jsonData);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing JSON data", e);
                return null;
            }
        }

        @Override
        public String toString() {
            return "NHSOClaimDataInfo{" +
                    "id=" + id +
                    ", visitno=" + visitno +
                    ", claimStatus='" + claimStatus + '\'' +
                    ", seq='" + seq + '\'' +
                    ", createdDate='" + createdDate + '\'' +
                    ", sendDate='" + sendDate + '\'' +
                    (errorMessage != null ? ", errorMessage='" + errorMessage + '\'' : "") +
                    '}';
        }
    }
}