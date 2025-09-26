package th.in.ffc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import th.in.ffc.app.form.screening.dao.PersonDao;

public class ProfileImage {

    private static final String PICTURE_PERSON_PATH = "/sdcard/Android/data/th.in.ffc/files/pictures/person/";

    public static void saveImageToStorage(Context context, Bitmap bitmap, String citizenId) {
        String filename = "";
        String tempFilename="";
        try {
            PersonDao personDao = new PersonDao(context);
            PersonDao.PersonInfo  person =  personDao.getPersonByIdcard(citizenId);
            // สร้าง path สำหรับบันทึกรูปภาพ
            if (person == null) {
                android.util.Log.e("PersonInfoFragment", "ไม่พบข้อมูลบุคคลที่มีรหัสประชาชน: " + citizenId);
                return;
            }
            String name = person.getPcucodeperson()+person.getPid();
            filename = name+".jpg";
            tempFilename = "tmp_"+name+"_720p.jpg";
            String directoryPath =  PICTURE_PERSON_PATH;
            File directory = new File(directoryPath);

            // สร้าง directory หากยังไม่มี
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    android.util.Log.e("PersonInfoFragment", "ไม่สามารถสร้าง directory ได้: " + directoryPath);
                    return;
                }
            }

            // สร้างชื่อไฟล์ใช้รหัสประชาชนเป็นชื่อไฟล์

            File imageFile = new File(directory, filename);
            File tempImageFile = new File(directory, tempFilename);

            // บันทึกรูปภาพ
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            FileOutputStream tempOutputStream = new FileOutputStream(tempImageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, tempOutputStream);
            outputStream.flush();
            outputStream.close();
            tempOutputStream.flush();
            tempOutputStream.close();
            android.util.Log.d("PersonInfoFragment", "บันทึกรูปภาพสำเร็จ: " + imageFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e("PersonInfoFragment", "เกิดข้อผิดพลาดในการบันทึกรูปภาพ: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * โหลดรูปภาพจาก storage โดยใช้รหัสประชาชน
     * @param context Context ของแอพพลิเคชัน
     * @param citizenId รหัสประชาชน 13 หลัก
     * @return Bitmap หากพบรูปภาพ หรือ null หากไม่พบ
     */
    public static Bitmap loadImageFromStorage(Context context, String citizenId) {
        try {
            PersonDao personDao = new PersonDao(context);
            PersonDao.PersonInfo person = personDao.getPersonByIdcard(citizenId);

            if (person == null) {
                Log.w("ProfileImage", "ไม่พบข้อมูลบุคคลที่มีรหัสประชาชน: " + citizenId);
                return null;
            }

            String name = person.getPcucodeperson() + person.getPid();
            String filename = name + ".jpg";
            String directoryPath = PICTURE_PERSON_PATH;

            File imageFile = new File(directoryPath, filename);

            // ตรวจสอบว่าไฟล์มีอยู่หรือไม่
            if (imageFile.exists()) {
                // โหลดรูปภาพจากไฟล์
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                if (bitmap != null) {
                    Log.d("ProfileImage", "โหลดรูปภาพสำเร็จ: " + imageFile.getAbsolutePath());
                    return bitmap;
                } else {
                    Log.w("ProfileImage", "ไม่สามารถ decode รูปภาพได้: " + imageFile.getAbsolutePath());
                }
            } else {
                Log.w("ProfileImage", "ไม่พบไฟล์รูปภาพ: " + imageFile.getAbsolutePath());
            }

        } catch (Exception e) {
            Log.e("ProfileImage", "เกิดข้อผิดพลาดในการโหลดรูปภาพ: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * ตรวจสอบว่ามีรูปภาพของบุคคลหรือไม่
     * @param context Context ของแอพพลิเคชัน
     * @param citizenId รหัสประชาชน 13 หลัก
     * @return true หากมีรูปภาพ, false หากไม่มี
     */
    public static boolean hasPersonImage(Context context, String citizenId) {
        try {
            PersonDao personDao = new PersonDao(context);
            PersonDao.PersonInfo person = personDao.getPersonByIdcard(citizenId);

            if (person == null) {
                return false;
            }

            String name = person.getPcucodeperson() + person.getPid();
            String filename = name + ".jpg";
            String directoryPath = PICTURE_PERSON_PATH;

            File imageFile = new File(directoryPath, filename);
            return imageFile.exists() && imageFile.length() > 0;

        } catch (Exception e) {
            Log.e("ProfileImage", "เกิดข้อผิดพลาดในการตรวจสอบรูปภาพ: " + e.getMessage());
            return false;
        }
    }

    /**
     * ลบรูปภาพของบุคคล
     * @param context Context ของแอพพลิเคชัน
     * @param citizenId รหัสประชาชน 13 หลัก
     * @return true หากลบสำเร็จ, false หากไม่สำเร็จ
     */
    public static boolean deletePersonImage(Context context, String citizenId) {
        try {
            PersonDao personDao = new PersonDao(context);
            PersonDao.PersonInfo person = personDao.getPersonByIdcard(citizenId);

            if (person == null) {
                return false;
            }

            String name = person.getPcucodeperson() + person.getPid();
            String filename = name + ".jpg";
            String tempFilename = "tmp_" + name + "_720p.jpg";
            String directoryPath = PICTURE_PERSON_PATH;

            File imageFile = new File(directoryPath, filename);
            File tempImageFile = new File(directoryPath, tempFilename);

            boolean deleted = true;
            if (imageFile.exists()) {
                deleted = imageFile.delete();
            }
            if (tempImageFile.exists()) {
                deleted = tempImageFile.delete() && deleted;
            }

            Log.d("ProfileImage", "ลบรูปภาพ: " + (deleted ? "สำเร็จ" : "ไม่สำเร็จ"));
            return deleted;

        } catch (Exception e) {
            Log.e("ProfileImage", "เกิดข้อผิดพลาดในการลบรูปภาพ: " + e.getMessage());
            return false;
        }
    }
}