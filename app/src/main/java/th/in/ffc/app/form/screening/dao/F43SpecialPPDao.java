package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import th.in.ffc.provider.F43SpecialPP;
import th.in.ffc.provider.F43SpecialPPProvider;
public class F43SpecialPPDao {
    private static final String TAG = "F43SpecialPPDao";

    private Context context;
    private ContentResolver contentResolver;

    public F43SpecialPPDao(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }
    public Uri saveServiceRecord(F43SpecialPPData serviceData) {
        try {
            ContentValues values = convertToContentValues(serviceData);

            // ลบข้อมูลเก่าที่มี person, dateserv และ ppspecial เดียวกันก่อน
            deleteExistingRecord(serviceData.pcucodeperson, serviceData.pid,
                    serviceData.dateserv, serviceData.ppspecial);

            // Insert ข้อมูลใหม่
            Uri result = contentResolver.insert(F43SpecialPP.CONTENT_URI, values);

            if (result != null) {
                Log.d(TAG, "บันทึกข้อมูลบริการสำเร็จ: " + result.toString());
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกข้อมูลบริการ", e);
            return null;
        }
    }

    /**
     * บันทึกการให้บริการส่งเสริม ป้องกันโรค แบบเต็ม
     */
    public Uri saveFullServiceRecord(String pcucodeperson, int pid, String dateserv,
                                     String ppspecial, String ppresult, String pcucode, Integer visitno,
                                     String servplace, String ppsplace, String provider, String userCreate) {
        try {
            F43SpecialPPData data = new F43SpecialPPData();
            data.pcucodeperson = pcucodeperson;
            data.pid = pid;
            data.dateserv = dateserv;
            data.ppspecial = ppspecial;
            data.ppresult = ppresult;
            data.pcucode = pcucode;
            data.visitno = visitno != null ? visitno : 0;
            data.servplace = servplace;
            data.ppsplace = ppsplace;
            data.provider = provider;
            data.issend2hisgateway = F43SpecialPP.SEND_STATUS_NOT_SENT;

            return saveServiceRecord(data);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกข้อมูลบริการแบบเต็ม", e);
            return null;
        }
    }

    /**
     * บันทึกการให้บริการในสถานบริการ
     */
    public Uri saveInServiceRecord(String pcucodeperson, int pid, String dateserv,
                                   String ppspecial, String ppresult, String provider, String userCreate) {
        try {
            return saveFullServiceRecord(pcucodeperson, pid, dateserv, ppspecial,
                    ppresult, null, null, F43SpecialPP.SERVPLACE_IN, null, provider, userCreate);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกบริการในสถานบริการ", e);
            return null;
        }
    }

    /**
     * บันทึกการให้บริการนอกสถานบริการ
     */
    public Uri saveOutServiceRecord(String pcucodeperson, int pid, String dateserv,
                                    String ppspecial, String ppresult, Integer visitno, String provider, String userCreate) {
        try {
            return saveFullServiceRecord(pcucodeperson, pid, dateserv, ppspecial,
                    ppresult, null, visitno, F43SpecialPP.SERVPLACE_OUT, null, provider, userCreate);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการบันทึกบริการนอกสถานบริการ", e);
            return null;
        }
    }

    /**
     * ลบข้อมูลเก่าที่ซ้ำกัน
     */
    private void deleteExistingRecord(String pcucodeperson, int pid, String dateserv, String ppspecial) {
        try {
            String selection = F43SpecialPP.PCUCODEPERSON + "=? AND " +
                    F43SpecialPP.PID + "=? AND " +
                    F43SpecialPP.DATESERV + "=? AND " +
                    F43SpecialPP.PPSPECIAL + "=?";
            String[] selectionArgs = {pcucodeperson, String.valueOf(pid), dateserv, ppspecial};

            int deletedRows = contentResolver.delete(
                    F43SpecialPP.CONTENT_LIST_URI,
                    selection,
                    selectionArgs
            );

            if (deletedRows > 0) {
                Log.d(TAG, "ลบข้อมูลเก่าสำเร็จ จำนวน " + deletedRows + " รายการ " +
                        "สำหรับ person: " + pcucodeperson + "/" + pid +
                        ", date: " + dateserv + ", ppspecial: " + ppspecial);
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลเก่า person: " + pcucodeperson + "/" + pid +
                    ", date: " + dateserv + ", ppspecial: " + ppspecial, e);
        }
    }

    private void deactivateExistingRecord(String pcucodeperson, int pid, String dateserv, String ppspecial) {
        try {
            ContentValues updateValues = new ContentValues();


            String selection = F43SpecialPP.PCUCODEPERSON + "=? AND " +
                    F43SpecialPP.PID + "=? AND " +
                    F43SpecialPP.DATESERV + "=? AND " +
                    F43SpecialPP.PPSPECIAL + "=? ";
            String[] selectionArgs = {pcucodeperson, String.valueOf(pid), dateserv, ppspecial};

            int updatedRows = contentResolver.update(
                    F43SpecialPP.CONTENT_LIST_URI,
                    updateValues,
                    selection,
                    selectionArgs
            );

            if (updatedRows > 0) {
                Log.d(TAG, "ปิดการใช้งานข้อมูลเก่าสำเร็จ จำนวน " + updatedRows + " รายการ " +
                        "สำหรับ person: " + pcucodeperson + "/" + pid +
                        ", date: " + dateserv + ", ppspecial: " + ppspecial);
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการปิดการใช้งานข้อมูลเก่า person: " + pcucodeperson + "/" + pid +
                    ", date: " + dateserv + ", ppspecial: " + ppspecial, e);
        }
    }

    /**
     * ดึงข้อมูลบริการทั้งหมด
     */
    public List<F43SpecialPPData> getAllServices() {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Cursor cursor = contentResolver.query(
                    F43SpecialPP.CONTENT_LIST_URI,
                    null, null, null,
                    F43SpecialPP.DATESERV + " DESC "
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลทั้งหมด", e);
        }

        return services;
    }

    /**
     * ดึงข้อมูลบริการตาม Person
     */
    public List<F43SpecialPPData> getServicesByPerson(String pcucodeperson, int pid) {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/person/" + pcucodeperson + "/" + pid);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตาม Person: " + pcucodeperson + "/" + pid, e);
        }

        return services;
    }

    /**
     * ดึงข้อมูลบริการตาม Visit Number
     */
    public List<F43SpecialPPData> getServicesByVisitNo(int visitno) {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/visitno/" + visitno);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตาม Visit No: " + visitno, e);
        }

        return services;
    }

    /**
     * ดึงข้อมูลบริการตาม Person และ Visit Number
     */
    public List<F43SpecialPPData> getServicesByPersonAndVisit(String pcucodeperson, int pid, int visitno) {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/person/" + pcucodeperson + "/" + pid + "/visit/" + visitno);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตาม Person: " + pcucodeperson + "/" + pid +
                    " และ Visit No: " + visitno, e);
        }

        return services;
    }

    /**
     * ดึงข้อมูลบริการตามประเภท
     */
    public List<F43SpecialPPData> getServicesByType(String ppspecial) {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/ppspecial/" + ppspecial);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตามประเภท: " + ppspecial, e);
        }

        return services;
    }

    /**
     * ดึงข้อมูลบริการตามวันที่
     */
    public List<F43SpecialPPData> getServicesByDate(String dateserv) {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/dateserv/" + dateserv);

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลตามวันที่: " + dateserv, e);
        }

        return services;
    }

    /**
     * ดึงข้อมูลบริการล่าสุดของผู้รับบริการ
     */
    public F43SpecialPPData getLatestServiceByPerson(String pcucodeperson, int pid) {
        try {
            String selection = F43SpecialPP.PCUCODEPERSON + "=? AND " +
                    F43SpecialPP.PID + "=? ";
            String[] selectionArgs = {pcucodeperson, String.valueOf(pid)};
            String sortOrder = F43SpecialPP.DATESERV + " DESC LIMIT 1";

            Cursor cursor = contentResolver.query(
                    F43SpecialPP.CONTENT_LIST_URI,
                    null, selection, selectionArgs, sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                F43SpecialPPData result = convertFromCursor(cursor);
                cursor.close();
                return result;
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลล่าสุดของ Person: " + pcucodeperson + "/" + pid, e);
        }

        return null;
    }

    /**
     * ดึงข้อมูลบริการตามประเภทและ Person
     */
    public F43SpecialPPData getLatestServiceByTypeAndPerson(String pcucodeperson, int pid, String ppspecial) {
        try {
            String selection = F43SpecialPP.PCUCODEPERSON + "=? AND " +
                    F43SpecialPP.PID + "=? AND " +
                    F43SpecialPP.PPSPECIAL + "=? ";
            String[] selectionArgs = {pcucodeperson, String.valueOf(pid), ppspecial};
            String sortOrder = F43SpecialPP.DATESERV + " DESC LIMIT 1";

            Cursor cursor = contentResolver.query(
                    F43SpecialPP.CONTENT_LIST_URI,
                    null, selection, selectionArgs, sortOrder
            );

            if (cursor != null && cursor.moveToFirst()) {
                F43SpecialPPData result = convertFromCursor(cursor);
                cursor.close();
                return result;
            }

            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลล่าสุดตามประเภท: " + ppspecial +
                    " สำหรับ Person: " + pcucodeperson + "/" + pid, e);
        }

        return null;
    }

    /**
     * ดึงข้อมูลบริการที่ยังไม่ส่งไป HIS Gateway
     */
    public List<F43SpecialPPData> getUnsentServices() {
        List<F43SpecialPPData> services = new ArrayList<>();

        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/unsent");

            Cursor cursor = contentResolver.query(uri, null, null, null, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    services.add(convertFromCursor(cursor));
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงข้อมูลที่ยังไม่ส่ง", e);
        }

        return services;
    }

    /**
     * ตรวจสอบว่ามีบริการในวันที่กำหนดหรือไม่
     */
    public boolean hasServiceOnDate(String pcucodeperson, int pid, String dateserv, String ppspecial) {
        try {
            String selection = F43SpecialPP.PCUCODEPERSON + "=? AND " +
                    F43SpecialPP.PID + "=? AND " +
                    F43SpecialPP.DATESERV + "=? AND " +
                    F43SpecialPP.PPSPECIAL + "=? ";
            String[] selectionArgs = {pcucodeperson, String.valueOf(pid), dateserv, ppspecial};

            Cursor cursor = contentResolver.query(
                    F43SpecialPP.CONTENT_LIST_URI,
                    new String[]{},
                    selection, selectionArgs, null
            );

            boolean hasService = false;
            if (cursor != null) {
                hasService = cursor.getCount() > 0;
                cursor.close();
            }

            return hasService;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการตรวจสอบบริการ Person: " + pcucodeperson + "/" + pid +
                    " วันที่: " + dateserv + " ประเภท: " + ppspecial, e);
            return false;
        }
    }

    /**
     * อัพเดทข้อมูลบริการ
     */
    public int updateService(long serviceId, F43SpecialPPData newData) {
        try {
            ContentValues values = convertToContentValues(newData);
            values.put(F43SpecialPP.DATEUPDATE, getCurrentDateTime());

            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/" + serviceId);

            int rowsUpdated = contentResolver.update(uri, values, null, null);

            if (rowsUpdated > 0) {
                Log.d(TAG, "อัพเดทข้อมูลสำเร็จ: " + serviceId);
            }

            return rowsUpdated;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการอัพเดทข้อมูล ID: " + serviceId, e);
            return 0;
        }
    }

    /**
     * อัพเดทสถานะการส่งข้อมูล
     */
    public int updateSendStatus(long serviceId, String sendStatus, String sendDateTime) {
        try {
            ContentValues values = new ContentValues();
            values.put(F43SpecialPP.ISSEND2HISGATEWAY, sendStatus);
            values.put(F43SpecialPP.ISSEND2HISGATEWAYDT, sendDateTime);
            values.put(F43SpecialPP.DATEUPDATE, getCurrentDateTime());

            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/" + serviceId);

            int rowsUpdated = contentResolver.update(uri, values, null, null);

            if (rowsUpdated > 0) {
                Log.d(TAG, "อัพเดทสถานะการส่งสำเร็จ: " + serviceId + " -> " + sendStatus);
            }

            return rowsUpdated;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการอัพเดทสถานะการส่ง ID: " + serviceId, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลบริการ (Hard Delete)
     */
    public int deleteService(long serviceId) {
        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/" + serviceId);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลสำเร็จ: " + serviceId);
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูล ID: " + serviceId, e);
            return 0;
        }
    }

    /**
     * ปิดการใช้งานบริการ (Soft Delete)
     */
    public int deactivateService(long serviceId) {
        try {
            ContentValues values = new ContentValues();


            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/" + serviceId);

            int rowsUpdated = contentResolver.update(uri, values, null, null);

            if (rowsUpdated > 0) {
                Log.d(TAG, "ปิดการใช้งานข้อมูลสำเร็จ: " + serviceId);
            }

            return rowsUpdated;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการปิดการใช้งานข้อมูล ID: " + serviceId, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทั้งหมดของผู้รับบริการ
     */
    public int deleteAllServicesByPerson(String pcucodeperson, int pid) {
        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/person/" + pcucodeperson + "/" + pid);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลทั้งหมดของ Person สำเร็จ: " + pcucodeperson + "/" + pid);
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลทั้งหมดของ Person: " + pcucodeperson + "/" + pid, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทั้งหมดของการเยี่ยมนั้นๆ
     */
    public int deleteAllServicesByVisitNo(int visitno) {
        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/visitno/" + visitno);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลทั้งหมดของ Visit No สำเร็จ: " + visitno);
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลทั้งหมดของ Visit No: " + visitno, e);
            return 0;
        }
    }

    /**
     * ลบข้อมูลทั้งหมดของผู้รับบริการในการเยี่ยมนั้นๆ
     */
    public int deleteAllServicesByPersonAndVisit(String pcucodeperson, int pid, int visitno) {
        try {
            Uri uri = Uri.parse("content://" + F43SpecialPPProvider.AUTHORITY +
                    "/f43specialpp/person/" + pcucodeperson + "/" + pid + "/visit/" + visitno);

            int rowsDeleted = contentResolver.delete(uri, null, null);

            if (rowsDeleted > 0) {
                Log.d(TAG, "ลบข้อมูลทั้งหมดของ Person: " + pcucodeperson + "/" + pid +
                        " Visit No: " + visitno + " สำเร็จ");
            }

            return rowsDeleted;
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการลบข้อมูลทั้งหมดของ Person: " + pcucodeperson + "/" + pid +
                    " Visit No: " + visitno, e);
            return 0;
        }
    }

    /**
     * ดึงสถิติการให้บริการตามประเภท
     */
    public ServiceStatistics getStatisticsByType(String ppspecial) {
        ServiceStatistics stats = new ServiceStatistics();
        stats.ppspecial = ppspecial;

        try {
            String selection = F43SpecialPP.PPSPECIAL + "=? ";
            String[] selectionArgs = {ppspecial};

            Cursor cursor = contentResolver.query(
                    F43SpecialPP.CONTENT_LIST_URI,
                    null, selection, selectionArgs, null
            );

            if (cursor != null) {
                stats.totalCount = cursor.getCount();

                while (cursor.moveToNext()) {
                    String servplace = cursor.getString(cursor.getColumnIndex(F43SpecialPP.SERVPLACE));
                    String sendStatus = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAY));

                    if (F43SpecialPP.SERVPLACE_IN.equals(servplace)) {
                        stats.inServiceCount++;
                    } else if (F43SpecialPP.SERVPLACE_OUT.equals(servplace)) {
                        stats.outServiceCount++;
                    }

                    if (F43SpecialPP.SEND_STATUS_SENT.equals(sendStatus)) {
                        stats.sentCount++;
                    } else if (F43SpecialPP.SEND_STATUS_NOT_SENT.equals(sendStatus)) {
                        stats.unsentCount++;
                    }
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติสำหรับประเภท: " + ppspecial, e);
        }

        return stats;
    }

    /**
     * ดึงสถิติการให้บริการตามวันที่
     */
    public ServiceStatistics getStatisticsByDate(String dateserv) {
        ServiceStatistics stats = new ServiceStatistics();
        stats.dateserv = dateserv;

        try {
            String selection = F43SpecialPP.DATESERV + "=? ";
            String[] selectionArgs = {dateserv};

            Cursor cursor = contentResolver.query(
                    F43SpecialPP.CONTENT_LIST_URI,
                    null, selection, selectionArgs, null
            );

            if (cursor != null) {
                stats.totalCount = cursor.getCount();

                while (cursor.moveToNext()) {
                    String servplace = cursor.getString(cursor.getColumnIndex(F43SpecialPP.SERVPLACE));
                    String sendStatus = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAY));

                    if (F43SpecialPP.SERVPLACE_IN.equals(servplace)) {
                        stats.inServiceCount++;
                    } else if (F43SpecialPP.SERVPLACE_OUT.equals(servplace)) {
                        stats.outServiceCount++;
                    }

                    if (F43SpecialPP.SEND_STATUS_SENT.equals(sendStatus)) {
                        stats.sentCount++;
                    } else if (F43SpecialPP.SEND_STATUS_NOT_SENT.equals(sendStatus)) {
                        stats.unsentCount++;
                    }
                }

                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการดึงสถิติสำหรับวันที่: " + dateserv, e);
        }

        return stats;
    }

    // Helper Methods

    /**
     * แปลง F43SpecialPPData เป็น ContentValues
     */
    private ContentValues convertToContentValues(F43SpecialPPData data) {
        ContentValues values = new ContentValues();

        if (data.pcucodeperson != null) values.put(F43SpecialPP.PCUCODEPERSON, data.pcucodeperson);
        values.put(F43SpecialPP.PID, data.pid);
        if (data.dateserv != null) values.put(F43SpecialPP.DATESERV, data.dateserv);
        if (data.ppspecial != null) values.put(F43SpecialPP.PPSPECIAL, data.ppspecial);
        if (data.ppresult != null) values.put(F43SpecialPP.PPRESULT, data.ppresult);
        if (data.pcucode != null) values.put(F43SpecialPP.PCUCODE, data.pcucode);
        values.put(F43SpecialPP.VISITNO, data.visitno);
        if (data.servplace != null) values.put(F43SpecialPP.SERVPLACE, data.servplace);
        if (data.ppsplace != null) values.put(F43SpecialPP.PPSPLACE, data.ppsplace);
        if (data.provider != null) values.put(F43SpecialPP.PROVIDER, data.provider);
        if (data.dateupdate != null) values.put(F43SpecialPP.DATEUPDATE, data.dateupdate);
        if (data.issend2hisgateway != null) values.put(F43SpecialPP.ISSEND2HISGATEWAY, data.issend2hisgateway);
        if (data.issend2hisgatewaydt != null) values.put(F43SpecialPP.ISSEND2HISGATEWAYDT, data.issend2hisgatewaydt);
        if (data.issend2hisgatewayall != null) values.put(F43SpecialPP.ISSEND2HISGATEWAYALL, data.issend2hisgatewayall);

        return values;
    }

    /**
     * แปลง Cursor เป็น F43SpecialPPData
     */
    private F43SpecialPPData convertFromCursor(Cursor cursor) {
        F43SpecialPPData data = new F43SpecialPPData();

        try {

            data.pcucodeperson = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PCUCODEPERSON));
            data.pid = cursor.getInt(cursor.getColumnIndex(F43SpecialPP.PID));
            data.dateserv = cursor.getString(cursor.getColumnIndex(F43SpecialPP.DATESERV));
            data.ppspecial = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PPSPECIAL));
            data.ppresult = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PPRESULT));
            data.pcucode = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PCUCODE));
            data.visitno = cursor.getInt(cursor.getColumnIndex(F43SpecialPP.VISITNO));
            data.servplace = cursor.getString(cursor.getColumnIndex(F43SpecialPP.SERVPLACE));
            data.ppsplace = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PPSPLACE));
            data.provider = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PROVIDER));
            data.dateupdate = cursor.getString(cursor.getColumnIndex(F43SpecialPP.DATEUPDATE));
            data.issend2hisgateway = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAY));
            data.issend2hisgatewaydt = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAYDT));
            data.issend2hisgatewayall = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAYALL));

            // กำหนดค่าคุณสมบัติเสริม
            data.isInService = F43SpecialPP.SERVPLACE_IN.equals(data.servplace);
            data.isOutService = F43SpecialPP.SERVPLACE_OUT.equals(data.servplace);
            data.isSent = F43SpecialPP.SEND_STATUS_SENT.equals(data.issend2hisgateway);
        } catch (Exception e) {
            Log.e(TAG, "เกิดข้อผิดพลาดในการแปลง Cursor เป็น F43SpecialPPData", e);
        }

        return data;
    }

    /**
     * ดึงวันที่ปัจจุบันในรูปแบบ yyyy-MM-dd
     */
    private String getCurrentDate() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    }

    /**
     * ดึงวันเวลาปัจจุบันในรูปแบบ yyyy-MM-dd HH:mm:ss
     */
    private String getCurrentDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
    }

    // Data Classes

    /**
     * คลาสสำหรับเก็บข้อมูลการให้บริการ
     */
    public static class F43SpecialPPData {
        public long id;
        public String pcucodeperson;
        public int pid;
        public String dateserv;
        public String ppspecial;
        public String ppresult;
        public String pcucode;
        public int visitno;
        public String servplace;
        public String ppsplace;
        public String provider;
        public String dateupdate;
        public String issend2hisgateway;
        public String issend2hisgatewaydt;
        public String issend2hisgatewayall;
        public String status;
        public String createTime;
        public String updateTime;
        public String userCreate;
        public String userUpdate;

        // เพิ่มคุณสมบัติเสริม
        public boolean isInService;
        public boolean isOutService;
        public boolean isSent;
        public boolean isActive;

        public F43SpecialPPData() {
            // Constructor เปล่า
        }

        public F43SpecialPPData(Cursor cursor) {
            this.pcucodeperson = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PCUCODEPERSON));
            this.pid = cursor.getInt(cursor.getColumnIndex(F43SpecialPP.PID));
            this.dateserv = cursor.getString(cursor.getColumnIndex(F43SpecialPP.DATESERV));
            this.ppspecial = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PPSPECIAL));
            this.ppresult = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PPRESULT));
            this.pcucode = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PCUCODE));
            this.visitno = cursor.getInt(cursor.getColumnIndex(F43SpecialPP.VISITNO));
            this.servplace = cursor.getString(cursor.getColumnIndex(F43SpecialPP.SERVPLACE));
            this.ppsplace = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PPSPLACE));
            this.provider = cursor.getString(cursor.getColumnIndex(F43SpecialPP.PROVIDER));
            this.dateupdate = cursor.getString(cursor.getColumnIndex(F43SpecialPP.DATEUPDATE));
            this.issend2hisgateway = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAY));
            this.issend2hisgatewaydt = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAYDT));
            this.issend2hisgatewayall = cursor.getString(cursor.getColumnIndex(F43SpecialPP.ISSEND2HISGATEWAYALL));

            // กำหนดค่าคุณสมบัติเสริม
            this.isInService = F43SpecialPP.SERVPLACE_IN.equals(this.servplace);
            this.isOutService = F43SpecialPP.SERVPLACE_OUT.equals(this.servplace);
            this.isSent = F43SpecialPP.SEND_STATUS_SENT.equals(this.issend2hisgateway);
        }

        /**
         * ตรวจสอบว่าเป็นบริการในสthานบริการหรือไม่
         */
        public boolean isInService() {
            return F43SpecialPP.SERVPLACE_IN.equals(this.servplace);
        }

        /**
         * ตรวจสอบว่าเป็นบริการนอกสถานบริการหรือไม่
         */
        public boolean isOutService() {
            return F43SpecialPP.SERVPLACE_OUT.equals(this.servplace);
        }

        /**
         * ตรวจสอบว่าส่งข้อมูลแล้วหรือไม่
         */
        public boolean isSent() {
            return F43SpecialPP.SEND_STATUS_SENT.equals(this.issend2hisgateway);
        }



        /**
         * ตรวจสอบว่าเกิดข้อผิดพลาดในการส่งหรือไม่
         */
        public boolean hasError() {
            return F43SpecialPP.SEND_STATUS_ERROR.equals(this.issend2hisgateway);
        }

        /**
         * ตรวจสอบว่าต้องส่งซ้ำหรือไม่
         */
        public boolean needsRetry() {
            return F43SpecialPP.SEND_STATUS_RETRY.equals(this.issend2hisgateway);
        }

        /**
         * ดึงชื่อประเภทการให้บริการ
         */
        public String getServiceTypeName() {
            // คุณสามารถเพิ่ม mapping ของรหัสบริการกับชื่อได้ที่นี่
            switch (this.ppspecial) {
                case "001": return "การตรวจสุขภาพทั่วไป";
                case "002": return "การฉีดวัคซีน";
                case "003": return "การคัดกรองโรค";
                case "004": return "การให้คำปรึกษา";
                default: return "บริการอื่นๆ (" + this.ppspecial + ")";
            }
        }

        /**
         * ดึงชื่อสถานที่ให้บริการ
         */
        public String getServicePlaceName() {
            if (F43SpecialPP.SERVPLACE_IN.equals(this.servplace)) {
                return "ในสถานบริการ";
            } else if (F43SpecialPP.SERVPLACE_OUT.equals(this.servplace)) {
                return "นอกสถานบริการ";
            } else {
                return "ไม่ระบุ";
            }
        }

        /**
         * ดึงสถานะการส่งข้อมูล
         */
        public String getSendStatusName() {
            switch (this.issend2hisgateway) {
                case F43SpecialPP.SEND_STATUS_NOT_SENT: return "ยังไม่ส่ง";
                case F43SpecialPP.SEND_STATUS_SENT: return "ส่งแล้ว";
                case F43SpecialPP.SEND_STATUS_ERROR: return "เกิดข้อผิดพลาด";
                case F43SpecialPP.SEND_STATUS_RETRY: return "ส่งซ้ำ";
                default: return "ไม่ทราบสถานะ";
            }
        }

        @Override
        public String toString() {
            return "F43SpecialPPData{" +
                    "id=" + id +
                    ", pcucodeperson='" + pcucodeperson + '\'' +
                    ", pid=" + pid +
                    ", dateserv='" + dateserv + '\'' +
                    ", ppspecial='" + ppspecial + '\'' +
                    ", ppresult='" + ppresult + '\'' +
                    ", visitno=" + visitno +
                    ", servplace='" + servplace + '\'' +
                    ", provider='" + provider + '\'' +
                    ", issend2hisgateway='" + issend2hisgateway + '\'' +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    /**
     * คลาสสำหรับเก็บสถิติการให้บริการ
     */
    public static class ServiceStatistics {
        public String ppspecial;
        public String dateserv;
        public int totalCount;
        public int inServiceCount;
        public int outServiceCount;
        public int sentCount;
        public int unsentCount;
        public int errorCount;
        public int retryCount;

        @Override
        public String toString() {
            return "ServiceStatistics{" +
                    "ppspecial='" + ppspecial + '\'' +
                    ", dateserv='" + dateserv + '\'' +
                    ", totalCount=" + totalCount +
                    ", inServiceCount=" + inServiceCount +
                    ", outServiceCount=" + outServiceCount +
                    ", sentCount=" + sentCount +
                    ", unsentCount=" + unsentCount +
                    ", errorCount=" + errorCount +
                    ", retryCount=" + retryCount +
                    '}';
        }
    }

    /**
     * Utility methods สำหรับการทำงานกับข้อมูล
     */
    public static class ServiceUtils {

        /**
         * สร้างข้อมูลบริการใหม่
         */
        public static F43SpecialPPData createNewService(String pcucodeperson, int pid, String dateserv,
                                                        String ppspecial, String ppresult, String provider, String userCreate) {
            F43SpecialPPData data = new F43SpecialPPData();
            data.pcucodeperson = pcucodeperson;
            data.pid = pid;
            data.dateserv = dateserv;
            data.ppspecial = ppspecial;
            data.ppresult = ppresult;
            data.provider = provider;
            data.issend2hisgateway = F43SpecialPP.SEND_STATUS_NOT_SENT;
            return data;
        }

        /**
         * สร้างข้อมูลบริการในสถานบริการ
         */
        public static F43SpecialPPData createInService(String pcucodeperson, int pid, String dateserv,
                                                       String ppspecial, String ppresult, String provider, String userCreate) {
            F43SpecialPPData data = createNewService(pcucodeperson, pid, dateserv, ppspecial, ppresult, provider, userCreate);
            data.servplace = F43SpecialPP.SERVPLACE_IN;
            return data;
        }

        /**
         * สร้างข้อมูลบริการนอกสถานบริการ
         */
        public static F43SpecialPPData createOutService(String pcucodeperson, int pid, String dateserv,
                                                        String ppspecial, String ppresult, Integer visitno, String provider, String userCreate) {
            F43SpecialPPData data = createNewService(pcucodeperson, pid, dateserv, ppspecial, ppresult, provider, userCreate);
            data.servplace = F43SpecialPP.SERVPLACE_OUT;
            data.visitno = visitno != null ? visitno : 0;
            return data;
        }

        /**
         * ตรวจสอบความถูกต้องของข้อมูล
         */
        public static boolean validateServiceData(F43SpecialPPData data) {
            if (data == null) return false;
            if (data.pcucodeperson == null || data.pcucodeperson.trim().isEmpty()) return false;
            if (data.pid <= 0) return false;
            if (data.dateserv == null || data.dateserv.trim().isEmpty()) return false;
            if (data.ppspecial == null || data.ppspecial.trim().isEmpty()) return false;
            return true;
        }

        /**
         * เปรียบเทียบข้อมูลบริการ
         */
        public static boolean isSameService(F43SpecialPPData data1, F43SpecialPPData data2) {
            if (data1 == null || data2 == null) return false;

            return data1.pcucodeperson.equals(data2.pcucodeperson) &&
                    data1.pid == data2.pid &&
                    data1.dateserv.equals(data2.dateserv) &&
                    data1.ppspecial.equals(data2.ppspecial);
        }
    }
}
