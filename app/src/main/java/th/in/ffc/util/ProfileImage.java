package th.in.ffc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

import th.in.ffc.app.form.screening.dao.PersonDao;

public class ProfileImage {
    public static void saveImageToStorage(Context context, Bitmap bitmap, String citizenId) {
        String PICTURE_PERSON_PATH = "/sdcard/Android/data/th.in.ffc/files/pictures/person/";
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

}
