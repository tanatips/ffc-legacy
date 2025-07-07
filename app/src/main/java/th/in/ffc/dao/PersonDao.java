package th.in.ffc.dao;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import th.in.ffc.model.Person;
import th.in.ffc.provider.PersonProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object สำหรับจัดการข้อมูล Person
 * รองรับการค้นหาด้วย ID Card และการดำเนินการ CRUD อื่นๆ
 *
 * @author Generated from PersonProvider
 * @version 1.0
 */
public class PersonDao {

    private Context context;
    private ContentResolver contentResolver;

    // URI สำหรับการเข้าถึงข้อมูล Person
    private static final Uri PERSON_URI = PersonProvider.Person.CONTENT_URI;

    // คอลัมน์ที่ต้องการดึงจากฐานข้อมูล
    private static final String[] PROJECTION = {
            PersonProvider.Person.PID,
            PersonProvider.Person.PCUPERSONCODE,
            PersonProvider.Person.CITIZEN_ID,
            PersonProvider.Person.PRENAME,
            PersonProvider.Person.FIRST_NAME,
            PersonProvider.Person.LAST_NAME,
            PersonProvider.Person.NICKNAME,
            PersonProvider.Person.BIRTH,
            PersonProvider.Person.SEX,
            PersonProvider.Person.BLOOD_GROUP,
            PersonProvider.Person.BLOOD_RH,
            PersonProvider.Person.ALLERGIC,
            PersonProvider.Person.OCCUPA,
            PersonProvider.Person.EDUCATION,
            PersonProvider.Person.NATION,
            PersonProvider.Person.ORIGIN,
            PersonProvider.Person.RELIGION,
            PersonProvider.Person.INCOME,
            PersonProvider.Person.HCODE,
            PersonProvider.Person.MARRY_STATUS,
            PersonProvider.Person.FAMILY_NO,
            PersonProvider.Person.FAMILY_POSITION,
            PersonProvider.Person.RIGHT_CODE,
            PersonProvider.Person.RIGHT_NO,
            PersonProvider.Person.RIGHT_HMAIN,
            PersonProvider.Person.RIGHT_HSUB,
            PersonProvider.Person.TEL,
            PersonProvider.Person._DATEUPDATE
    };

    public PersonDao(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    /**
     * ค้นหาบุคคลด้วยเลขบัตรประชาชน
     * @param idCard เลขบัตรประชาชน 13 หลัก
     * @return Person object หากพบข้อมูล, null หากไม่พบ
     */
    public Person findByIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return null;
        }

        // ตรวจสอบว่าเป็นเลขบัตรประชาชน 13 หลัก
        if (!idCard.matches("\\d{13}")) {
            return null;
        }

        String selection = PersonProvider.Person.CITIZEN_ID + " = ?";
        String[] selectionArgs = {idCard};

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    PERSON_URI,
                    PROJECTION,
                    selection,
                    selectionArgs,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return createPersonFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * ค้นหาบุคคลด้วย PID
     * @param pid รหัส PID
     * @return Person object หากพบข้อมูล, null หากไม่พบ
     */
    public Person findByPid(String pid) {
        if (pid == null || pid.trim().isEmpty()) {
            return null;
        }

        // สร้าง URI สำหรับค้นหาด้วย PID
        Uri personUri = Uri.withAppendedPath(PERSON_URI, pid);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    personUri,
                    PROJECTION,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return createPersonFromCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return null;
    }

    /**
     * ค้นหาบุคคลทั้งหมดในบ้านเลขที่
     * @param hcode รหัสบ้าน
     * @return List ของ Person ในบ้านดังกล่าว
     */
    public List<Person> findByHouseCode(String hcode) {
        List<Person> persons = new ArrayList<>();

        if (hcode == null || hcode.trim().isEmpty()) {
            return persons;
        }

        String selection = PersonProvider.Person.HCODE + " = ?";
        String[] selectionArgs = {hcode};

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    PERSON_URI,
                    PROJECTION,
                    selection,
                    selectionArgs,
                    PersonProvider.Person.DEFAULT_SORTING
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Person person = createPersonFromCursor(cursor);
                    if (person != null) {
                        persons.add(person);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return persons;
    }

    /**
     * ค้นหาบุคคลด้วยชื่อ-นามสกุล
     * @param firstName ชื่อ
     * @param lastName นามสกุล
     * @return List ของ Person ที่ตรงกับเงื่อนไข
     */
    public List<Person> findByName(String firstName, String lastName) {
        List<Person> persons = new ArrayList<>();

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgsList = new ArrayList<>();

        if (firstName != null && !firstName.trim().isEmpty()) {
            selection.append(PersonProvider.Person.FIRST_NAME).append(" LIKE ?");
            selectionArgsList.add("%" + firstName.trim() + "%");
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(PersonProvider.Person.LAST_NAME).append(" LIKE ?");
            selectionArgsList.add("%" + lastName.trim() + "%");
        }

        if (selection.length() == 0) {
            return persons;
        }

        String[] selectionArgs = selectionArgsList.toArray(new String[0]);

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    PERSON_URI,
                    PROJECTION,
                    selection.toString(),
                    selectionArgs,
                    PersonProvider.Person.DEFAULT_SORTING
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Person person = createPersonFromCursor(cursor);
                    if (person != null) {
                        persons.add(person);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return persons;
    }

    /**
     * ค้นหาบุคคลทั้งหมด
     * @return List ของ Person ทั้งหมด
     */
    public List<Person> findAll() {
        List<Person> persons = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    PERSON_URI,
                    PROJECTION,
                    null,
                    null,
                    PersonProvider.Person.DEFAULT_SORTING
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    Person person = createPersonFromCursor(cursor);
                    if (person != null) {
                        persons.add(person);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return persons;
    }

    /**
     * ตรวจสอบว่ามีบุคคลที่มีเลขบัตรประชาชนนี้หรือไม่
     * @param idCard เลขบัตรประชาชน
     * @return true หากมีอยู่, false หากไม่มี
     */
    public boolean existsByIdCard(String idCard) {
        return findByIdCard(idCard) != null;
    }

    /**
     * นับจำนวนบุคคลในบ้าน
     * @param hcode รหัสบ้าน
     * @return จำนวนบุคคลในบ้าน
     */
    public int countByHouseCode(String hcode) {
        if (hcode == null || hcode.trim().isEmpty()) {
            return 0;
        }

        String[] projection = {PersonProvider.Person._COUNT};
        String selection = PersonProvider.Person.HCODE + " = ?";
        String[] selectionArgs = {hcode};

        Cursor cursor = null;
        try {
            cursor = contentResolver.query(
                    PERSON_URI,
                    projection,
                    selection,
                    selectionArgs,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return 0;
    }

    /**
     * สร้าง Person object จาก Cursor
     * @param cursor Cursor ที่มีข้อมูล
     * @return Person object
     */
    private Person createPersonFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        try {
            Person person = new Person();

            // ดึงข้อมูลจาก cursor
            int pidIndex = cursor.getColumnIndex(PersonProvider.Person.PID);
            if (pidIndex >= 0) {
                person.setPid(cursor.getString(pidIndex));
            }

            int pcuCodeIndex = cursor.getColumnIndex(PersonProvider.Person.PCUPERSONCODE);
            if (pcuCodeIndex >= 0) {
                person.setPcuCodePerson(cursor.getString(pcuCodeIndex));
            }

            int idCardIndex = cursor.getColumnIndex(PersonProvider.Person.CITIZEN_ID);
            if (idCardIndex >= 0) {
                person.setIdCard(cursor.getString(idCardIndex));
            }

            int prenameIndex = cursor.getColumnIndex(PersonProvider.Person.PRENAME);
            if (prenameIndex >= 0) {
                person.setPrename(cursor.getString(prenameIndex));
            }

            int firstNameIndex = cursor.getColumnIndex(PersonProvider.Person.FIRST_NAME);
            if (firstNameIndex >= 0) {
                person.setFirstName(cursor.getString(firstNameIndex));
            }

            int lastNameIndex = cursor.getColumnIndex(PersonProvider.Person.LAST_NAME);
            if (lastNameIndex >= 0) {
                person.setLastName(cursor.getString(lastNameIndex));
            }

            int nicknameIndex = cursor.getColumnIndex(PersonProvider.Person.NICKNAME);
            if (nicknameIndex >= 0) {
                person.setNickname(cursor.getString(nicknameIndex));
            }

            int birthIndex = cursor.getColumnIndex(PersonProvider.Person.BIRTH);
            if (birthIndex >= 0) {
                person.setBirth(cursor.getString(birthIndex));
            }

            int sexIndex = cursor.getColumnIndex(PersonProvider.Person.SEX);
            if (sexIndex >= 0) {
                person.setSex(cursor.getString(sexIndex));
            }

            int bloodGroupIndex = cursor.getColumnIndex(PersonProvider.Person.BLOOD_GROUP);
            if (bloodGroupIndex >= 0) {
                person.setBloodGroup(cursor.getString(bloodGroupIndex));
            }

            int bloodRhIndex = cursor.getColumnIndex(PersonProvider.Person.BLOOD_RH);
            if (bloodRhIndex >= 0) {
                person.setBloodRh(cursor.getString(bloodRhIndex));
            }

            int allergicIndex = cursor.getColumnIndex(PersonProvider.Person.ALLERGIC);
            if (allergicIndex >= 0) {
                person.setAllergic(cursor.getString(allergicIndex));
            }

            int occupaIndex = cursor.getColumnIndex(PersonProvider.Person.OCCUPA);
            if (occupaIndex >= 0) {
                person.setOccupa(cursor.getString(occupaIndex));
            }

            int educationIndex = cursor.getColumnIndex(PersonProvider.Person.EDUCATION);
            if (educationIndex >= 0) {
                person.setEducation(cursor.getString(educationIndex));
            }

            int nationIndex = cursor.getColumnIndex(PersonProvider.Person.NATION);
            if (nationIndex >= 0) {
                person.setNation(cursor.getString(nationIndex));
            }

            int originIndex = cursor.getColumnIndex(PersonProvider.Person.ORIGIN);
            if (originIndex >= 0) {
                person.setOrigin(cursor.getString(originIndex));
            }

            int religionIndex = cursor.getColumnIndex(PersonProvider.Person.RELIGION);
            if (religionIndex >= 0) {
                person.setReligion(cursor.getString(religionIndex));
            }

            int incomeIndex = cursor.getColumnIndex(PersonProvider.Person.INCOME);
            if (incomeIndex >= 0) {
                person.setIncome(cursor.getString(incomeIndex));
            }

            int hcodeIndex = cursor.getColumnIndex(PersonProvider.Person.HCODE);
            if (hcodeIndex >= 0) {
                person.setHcode(cursor.getString(hcodeIndex));
            }

            int marryStatusIndex = cursor.getColumnIndex(PersonProvider.Person.MARRY_STATUS);
            if (marryStatusIndex >= 0) {
                person.setMarryStatus(cursor.getString(marryStatusIndex));
            }

            int familyNoIndex = cursor.getColumnIndex(PersonProvider.Person.FAMILY_NO);
            if (familyNoIndex >= 0) {
                person.setFamilyNo(cursor.getString(familyNoIndex));
            }

            int familyPositionIndex = cursor.getColumnIndex(PersonProvider.Person.FAMILY_POSITION);
            if (familyPositionIndex >= 0) {
                person.setFamilyPosition(cursor.getString(familyPositionIndex));
            }

            int rightCodeIndex = cursor.getColumnIndex(PersonProvider.Person.RIGHT_CODE);
            if (rightCodeIndex >= 0) {
                person.setRightCode(cursor.getString(rightCodeIndex));
            }

            int rightNoIndex = cursor.getColumnIndex(PersonProvider.Person.RIGHT_NO);
            if (rightNoIndex >= 0) {
                person.setRightNo(cursor.getString(rightNoIndex));
            }

            int rightHmainIndex = cursor.getColumnIndex(PersonProvider.Person.RIGHT_HMAIN);
            if (rightHmainIndex >= 0) {
                person.setRightHmain(cursor.getString(rightHmainIndex));
            }

            int rightHsubIndex = cursor.getColumnIndex(PersonProvider.Person.RIGHT_HSUB);
            if (rightHsubIndex >= 0) {
                person.setRightHsub(cursor.getString(rightHsubIndex));
            }

            int telIndex = cursor.getColumnIndex(PersonProvider.Person.TEL);
            if (telIndex >= 0) {
                person.setTel(cursor.getString(telIndex));
            }

            int dateUpdateIndex = cursor.getColumnIndex(PersonProvider.Person._DATEUPDATE);
            if (dateUpdateIndex >= 0) {
                person.setDateUpdate(cursor.getString(dateUpdateIndex));
            }

            return person;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
