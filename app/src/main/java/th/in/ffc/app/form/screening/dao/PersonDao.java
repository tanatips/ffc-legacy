package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import th.in.ffc.provider.PersonProvider;

/**
 * DAO class สำหรับจัดการข้อมูลใน table person
 * รองรับการค้นหาและอัปเดตข้อมูลด้วย idcard
 */
public class PersonDao {

    private static final String TAG = "PersonDao";
    private Context context;
    private ContentResolver contentResolver;

    public PersonDao(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    /**
     * ค้นหาข้อมูลบุคคลด้วยเลขบัตรประชาชน
     * @param idcard เลขบัตรประชาชน 13 หลัก
     * @return PersonInfo object หรือ null ถ้าไม่พบข้อมูล
     */
    public PersonInfo getPersonByIdcard(String idcard) {
        PersonInfo person = null;

        try {
            // สร้าง URI สำหรับค้นหาด้วย idcard
            Uri uri = Uri.withAppendedPath(PersonProvider.Person.CONTENT_URI, idcard);

            // กำหนด columns ที่ต้องการ
            String[] projection = {
                    PersonProvider.Person.PID,
                    PersonProvider.Person.CITIZEN_ID,
                    PersonProvider.Person.FIRST_NAME,
                    PersonProvider.Person.LAST_NAME,
                    PersonProvider.Person.BIRTH,
                    PersonProvider.Person.SEX,
                    PersonProvider.Person.PRENAME,
                    PersonProvider.Person.PCUPERSONCODE,
                    PersonProvider.Person.HCODE
            };

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                person = new PersonInfo();
                person.setPid(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.PID)));
                person.setIdcard(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.CITIZEN_ID)));
                person.setFname(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.FIRST_NAME)));
                person.setLname(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.LAST_NAME)));
                person.setBirth(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.BIRTH)));
                person.setSex(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.SEX)));
                person.setPrename(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.PRENAME)));
                person.setPcucodeperson(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.PCUPERSONCODE)));
                person.setHcode(cursor.getString(cursor.getColumnIndex(PersonProvider.Person.HCODE)));

                Log.d(TAG, "Found person with idcard: " + idcard);
            } else {
                Log.d(TAG, "No person found with idcard: " + idcard);
            }

            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error getting person by idcard: " + idcard, e);
        }

        return person;
    }

    /**
     * อัปเดตข้อมูลชื่อ นามสกุล และวันเกิดด้วยเลขบัตรประชาชน
     * @param idcard เลขบัตรประชาชน 13 หลัก
     * @param firstName ชื่อใหม่
     * @param lastName นามสกุลใหม่
     * @param birthDate วันเกิดใหม่ (รูปแบบ yyyy-MM-dd)
     * @return จำนวนแถวที่ถูกอัปเดต
     */
    public int updatePersonByIdcard(String idcard, String firstName, String lastName, String birthDate) {
        int updatedRows = 0;

        try {
            ContentValues values = new ContentValues();

            // เพิ่มข้อมูลที่ต้องการอัปเดต
            if (firstName != null && !firstName.trim().isEmpty()) {
                values.put(PersonProvider.Person.FIRST_NAME, firstName.trim());
            }

            if (lastName != null && !lastName.trim().isEmpty()) {
                values.put(PersonProvider.Person.LAST_NAME, lastName.trim());
            }

            if (birthDate != null && !birthDate.trim().isEmpty()) {
                values.put(PersonProvider.Person.BIRTH, birthDate);
            }

            // เพิ่มข้อมูลการอัปเดต
            values.put(PersonProvider.Person._DATEUPDATE, getCurrentDateTime());

            // ตรวจสอบว่ามีข้อมูลที่จะอัปเดตหรือไม่
            if (values.size() > 1) { // มากกว่า 1 เพราะมี dateupdate อยู่แล้ว
                // กำหนดเงื่อนไขการอัปเดต
                String selection = PersonProvider.Person.CITIZEN_ID + "=?";
                String[] selectionArgs = {idcard};

                // ทำการอัปเดต
                updatedRows = contentResolver.update(
                        PersonProvider.Person.CONTENT_URI,
                        values,
                        selection,
                        selectionArgs
                );

                Log.d(TAG, "Updated " + updatedRows + " rows for idcard: " + idcard);
            } else {
                Log.w(TAG, "No data to update for idcard: " + idcard);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating person with idcard: " + idcard, e);
        }

        return updatedRows;
    }

    /**
     * อัปเดตข้อมูลบุคคลด้วย PersonInfo object
     * @param person PersonInfo object ที่มีข้อมูลใหม่
     * @return จำนวนแถวที่ถูกอัปเดต
     */
    public int updatePerson(PersonInfo person) {
        if (person == null || person.getIdcard() == null || person.getIdcard().trim().isEmpty()) {
            Log.w(TAG, "Invalid person data or missing idcard");
            return 0;
        }

        return updatePersonByIdcard(
                person.getIdcard(),
                person.getFname(),
                person.getLname(),
                person.getBirth()
        );
    }

    /**
     * ตรวจสอบว่ามีบุคคลที่มีเลขบัตรประชาชนนี้อยู่หรือไม่
     * @param idcard เลขบัตรประชาชน 13 หลัก
     * @return true ถ้าพบข้อมูล, false ถ้าไม่พบ
     */
    public boolean isPersonExists(String idcard) {
        boolean exists = false;

        try {
            Uri uri = Uri.withAppendedPath(PersonProvider.Person.CONTENT_URI, idcard);
            String[] projection = {PersonProvider.Person.PID};

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            if (cursor != null) {
                exists = cursor.getCount() > 0;
                cursor.close();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error checking person existence for idcard: " + idcard, e);
        }

        return exists;
    }

    /**
     * ค้นหาข้อมูลบุคคลหลายคนด้วยเงื่อนไข
     * @param selection เงื่อนไขการค้นหา (WHERE clause)
     * @param selectionArgs ค่าพารามิเตอร์สำหรับเงื่อนไข
     * @param sortOrder การเรียงลำดับ
     * @return Cursor ที่มีผลลัพธ์การค้นหา
     */
    public Cursor queryPersons(String selection, String[] selectionArgs, String sortOrder) {
        try {
            String[] projection = {
                    PersonProvider.Person.PID,
                    PersonProvider.Person.CITIZEN_ID,
                    PersonProvider.Person.FIRST_NAME,
                    PersonProvider.Person.LAST_NAME,
                    PersonProvider.Person.BIRTH,
                    PersonProvider.Person.SEX,
                    PersonProvider.Person.PRENAME,
                    PersonProvider.Person.PCUPERSONCODE
            };

            return contentResolver.query(
                    PersonProvider.Person.CONTENT_URI,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
            );

        } catch (Exception e) {
            Log.e(TAG, "Error querying persons", e);
            return null;
        }
    }

    /**
     * ดึงวันที่และเวลาปัจจุบันในรูปแบบ yyyy-MM-dd HH:mm:ss
     * @return String วันที่เวลาปัจจุบัน
     */
    private String getCurrentDateTime() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
                .format(new java.util.Date());
    }

    /**
     * Inner class สำหรับเก็บข้อมูลบุคคล
     */
    public static class PersonInfo {
        private String pid;
        private String idcard;
        private String fname;
        private String lname;
        private String birth;
        private String sex;
        private String prename;
        private String pcucodeperson;

        private String hcode;

        // Constructors
        public PersonInfo() {}

        public PersonInfo(String idcard, String fname, String lname, String birth) {
            this.idcard = idcard;
            this.fname = fname;
            this.lname = lname;
            this.birth = birth;
        }

        // Getters and Setters
        public String getPid() { return pid; }
        public void setPid(String pid) { this.pid = pid; }

        public String getIdcard() { return idcard; }
        public void setIdcard(String idcard) { this.idcard = idcard; }

        public String getFname() { return fname; }
        public void setFname(String fname) { this.fname = fname; }

        public String getLname() { return lname; }
        public void setLname(String lname) { this.lname = lname; }

        public String getBirth() { return birth; }
        public void setBirth(String birth) { this.birth = birth; }

        public String getSex() { return sex; }
        public void setSex(String sex) { this.sex = sex; }

        public String getPrename() { return prename; }
        public void setPrename(String prename) { this.prename = prename; }

        public String getPcucodeperson() { return pcucodeperson; }
        public void setPcucodeperson(String pcucodeperson) { this.pcucodeperson = pcucodeperson; }

        public String getHcode() { return hcode; }
        public void setHcode(String hcode) { this.hcode = hcode; }

        @Override
        public String toString() {
            return "PersonInfo{" +
                    "pid='" + pid + '\'' +
                    ", idcard='" + idcard + '\'' +
                    ", fname='" + fname + '\'' +
                    ", lname='" + lname + '\'' +
                    ", birth='" + birth + '\'' +
                    ", sex='" + sex + '\'' +
                    '}';
        }
    }
}