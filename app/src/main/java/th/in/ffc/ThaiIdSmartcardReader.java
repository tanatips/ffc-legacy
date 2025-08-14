package th.in.ffc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;

import java.io.ByteArrayOutputStream;

import th.in.ffc.smartcardreader.ThaiIDReaderModule;


public class ThaiIdSmartcardReader extends AppCompatActivity {

    private static final String TAG = "ThaiIdSmartcardReader";

    private Button btnReadCard;
    private Button btnSaveData;
    private TextView txtCardInfo;
    private ImageView imgPhoto;
    private ThaiIDReaderModule thaiIDReader;

    // ตัวแปรเก็บข้อมูลที่อ่านได้
    private String cardDataInfo = "";
    private byte[] cardPhotoBytes = null;
    private String[] parsedCardData = null;

    SmartCardInfo smartCardInfo = new SmartCardInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thai_id_smartcard_reader);

        // Initialize views
        btnReadCard = findViewById(R.id.btnReadCard);
        btnSaveData = findViewById(R.id.btnSaveData);
        txtCardInfo = findViewById(R.id.txtCardInfo);
        imgPhoto = findViewById(R.id.imgPhoto);

        // Initialize ThaiIDReader module
        ReactApplicationContext reactContext = new ReactApplicationContext(this);
        thaiIDReader = new ThaiIDReaderModule(reactContext);

        // Set click listeners
        btnReadCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readThaiIDCard();
            }
        });

        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCardData();
            }
        });
    }

    private void readThaiIDCard() {
        btnReadCard.setEnabled(false);
        btnReadCard.setText("กำลังอ่านบัตร...");
        btnSaveData.setVisibility(View.GONE);
        txtCardInfo.setText("กรุณาใส่บัตรประชาชนลงในเครื่องอ่านบัตร");

        // Create promise to handle callback
        Promise promise = new Promise() {
            @Override
            public void resolve(Object value) {
                runOnUiThread(() -> {
                    handleCardReadSuccess((WritableMap) value);
                });
            }

            @Override
            public void reject(String code, String message) {
                runOnUiThread(() -> {
                    handleCardReadError(code, message);
                });
            }

            @Override
            public void reject(String code, String message, Throwable throwable) {
                runOnUiThread(() -> {
                    handleCardReadError(code, message + ": " + throwable.getMessage());
                });
            }

            @Override
            public void reject(String code, WritableMap userInfo) {
                runOnUiThread(() -> {
                    handleCardReadError(code, userInfo.toString());
                });
            }

            @Override
            public void reject(String code, Throwable throwable) {
                runOnUiThread(() -> {
                    handleCardReadError(code, throwable.getMessage());
                });
            }

            @Override
            public void reject(String code, String message, WritableMap userInfo) {
                runOnUiThread(() -> {
                    handleCardReadError(code, message);
                });
            }

            @Override
            public void reject(String code, Throwable throwable, WritableMap userInfo) {
                runOnUiThread(() -> {
                    handleCardReadError(code, throwable.getMessage());
                });
            }

            @Override
            public void reject(Throwable throwable) {
                runOnUiThread(() -> {
                    handleCardReadError("ERROR", throwable.getMessage());
                });
            }

            @Override
            public void reject(Throwable throwable, WritableMap userInfo) {
                runOnUiThread(() -> {
                    handleCardReadError("ERROR", throwable.getMessage());
                });
            }

            @Override
            public void reject(String message) {
                runOnUiThread(() -> {
                    handleCardReadError("ERROR", message);
                });
            }
        };

        // Call readCard method
        thaiIDReader.readCard(promise);
    }

    private void handleCardReadSuccess(WritableMap result) {
        try {
            // Parse card information
            String info = result.getString("info");
            String photoBase64 = result.getString("photo");

            Log.d(TAG, "Raw card data: " + info);

            // เก็บข้อมูลดิบไว้
            cardDataInfo = info;

            if (info != null && !info.isEmpty()) {
                // Split by # delimiter
                String[] cardData = info.split("#", -1); // -1 เพื่อเก็บ empty strings
                parsedCardData = cardData; // เก็บข้อมูลที่แยกแล้ว

                Log.d(TAG, "Split data array length: " + cardData.length);
                for (int i = 0; i < cardData.length; i++) {
                    Log.d(TAG, "cardData[" + i + "]: '" + cardData[i] + "'");
                }

                if (cardData.length >= 22) {
                    StringBuilder displayInfo = new StringBuilder();

                    // เลขประจำตัวประชาชน
                    displayInfo.append("เลขประจำตัวประชาชน: ").append(formatCitizenId(cardData[0])).append("\n\n");
                    smartCardInfo.setCitizenId(cardData[0]);
                    // ชื่อ-นามสกุล (ไทย)
                    String thaiName = buildFullName(cardData[1], cardData[2], cardData[3], cardData[4]);
                    displayInfo.append("ชื่อ-นามสกุล (ไทย): ").append(thaiName).append("\n");
                    smartCardInfo.setTitleThai(cardData[1]);
                    smartCardInfo.setFirstNameThai(cardData[2]);
                    smartCardInfo.setMiddleNameThai(cardData[3]);
                    smartCardInfo.setLastNameThai(cardData[4]);

                    // ชื่อ-นามสกุล (อังกฤษ)
                    String englishName = buildFullName(cardData[5], cardData[6], cardData[7], cardData[8]);
                    displayInfo.append("ชื่อ-นามสกุล (อังกฤษ): ").append(englishName).append("\n\n");
                    smartCardInfo.setTitleEnglish(cardData[5]);
                    smartCardInfo.setFirstNameEnglish(cardData[6]);
                    smartCardInfo.setMiddleNameEnglish(cardData[7]);
                    smartCardInfo.setLastNameEnglish(cardData[8]);

                    // ที่อยู่
                    String address = buildAddressFromCardData(cardData);
                    displayInfo.append("ที่อยู่: ").append(address).append("\n\n");
                    smartCardInfo.setAddress(address);

                    // เพศ
                    String gender = cardData[17].equals("1") ? "ชาย" :
                            cardData[17].equals("2") ? "หญิง" : cardData[17];
                    displayInfo.append("เพศ: ").append(gender).append("\n");
                    smartCardInfo.setGender(cardData[17]);

                    // วันเกิด
                    displayInfo.append("วันเกิด: ").append(formatDateFromBuddhistEra(cardData[18])).append("\n\n");
                    smartCardInfo.setBirthDate(cardData[18]); // รูปแบบ YYYYMMDD

                    // หน่วยงานที่ออกบัตร
                    displayInfo.append("หน่วยงานที่ออกบัตร: ").append(cardData[19]).append("\n");
                    smartCardInfo.setIssuedBy(cardData[19]);

                    // วันที่ออกบัตร
                    displayInfo.append("วันที่ออกบัตร: ").append(formatDateFromBuddhistEra(cardData[20])).append("\n");
                    smartCardInfo.setIssueDate(cardData[20]); // รูปแบบ YYYYMMDD
                    // วันหมดอายุ
                    displayInfo.append("วันหมดอายุ: ").append(formatDateFromBuddhistEra(cardData[21]));
                    smartCardInfo.setExpireDate(cardData[21]); // รูปแบบ YYYYMMDD

                    txtCardInfo.setText(displayInfo.toString());

                    // แสดงปุ่มบันทึกเมื่ออ่านข้อมูลสำเร็จ
                    btnSaveData.setVisibility(View.VISIBLE);
                } else {
                    // ถ้าข้อมูลไม่ครบ แสดงข้อมูลดิบ
                    txtCardInfo.setText("ข้อมูลไม่ครบถ้วน (ได้ " + cardData.length + " ฟิลด์):\n\n" + info);
                    Log.w(TAG, "Incomplete card data, expected at least 22 fields, got: " + cardData.length);
                }
            }

            // Display photo if available
            if (photoBase64 != null && !photoBase64.isEmpty()) {
                try {
                    byte[] imageBytes = Base64.decode(photoBase64, Base64.DEFAULT);
                    cardPhotoBytes = imageBytes; // เก็บข้อมูลรูปภาพ
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    if (bitmap != null) {
                        imgPhoto.setImageBitmap(bitmap);
                        imgPhoto.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error decoding photo: " + e.getMessage());
                }
            }

            Toast.makeText(this, "อ่านบัตรสำเร็จ", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error processing card data: " + e.getMessage());
            txtCardInfo.setText("เกิดข้อผิดพลาดในการประมวลผลข้อมูลบัตร\n\nข้อมูลดิบ:\n" +
                    (result.getString("info") != null ? result.getString("info") : "ไม่มีข้อมูล"));
        }

        btnReadCard.setEnabled(true);
        btnReadCard.setText("อ่านบัตรประชาชน");
    }

    private void saveCardData() {
        if (parsedCardData != null && parsedCardData.length >= 22) {
            Intent intent = new Intent();

            // ส่งข้อมูลกลับไปยัง PersonInfoFragment
            intent.putExtra("result", cardDataInfo);

            // ส่งรูปภาพถ้ามี
            if (cardPhotoBytes != null) {
                intent.putExtra("image", cardPhotoBytes);
            }

            // ส่งข้อมูลแยกไว้สำหรับใช้งานง่าย
            intent.putExtra("citizenId", smartCardInfo.getCitizenId());
            intent.putExtra("titleThai", smartCardInfo.getCitizenId());
            intent.putExtra("firstNameThai", smartCardInfo.getFirstNameThai());
            intent.putExtra("middleNameThai", smartCardInfo.getMiddleNameThai());
            intent.putExtra("lastNameThai", smartCardInfo.getLastNameThai());
            intent.putExtra("titleEnglish", smartCardInfo.getTitleEnglish());
            intent.putExtra("firstNameEnglish", smartCardInfo.getFirstNameEnglish());
            intent.putExtra("middleNameEnglish", smartCardInfo.getMiddleNameEnglish());
            intent.putExtra("lastNameEnglish", smartCardInfo.getLastNameEnglish());
            intent.putExtra("gender", smartCardInfo.getGender()); // 1=ชาย, 2=หญิง
            intent.putExtra("birthDate", smartCardInfo.getBirthDate()); // รูปแบบ YYYYMMDD

            setResult(RESULT_OK, intent);
            finish();

            Toast.makeText(this, "บันทึกข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "ไม่มีข้อมูลบัตรให้บันทึก กรุณาอ่านบัตรก่อน", Toast.LENGTH_SHORT).show();
        }
    }

    private String buildFullName(String title, String firstName, String middleName, String lastName) {
        StringBuilder name = new StringBuilder();

        if (title != null && !title.trim().isEmpty()) {
            name.append(title.trim()).append(" ");
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            name.append(firstName.trim()).append(" ");
        }
        if (middleName != null && !middleName.trim().isEmpty()) {
            name.append(middleName.trim()).append(" ");
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            name.append(lastName.trim());
        }

        return name.toString().trim();
    }

    private String buildAddressFromCardData(String[] cardData) {
        StringBuilder address = new StringBuilder();

        // บ้านเลขที่ (index 9)
        if (cardData[9] != null && !cardData[9].trim().isEmpty()) {
            address.append("บ้านเลขที่ ").append(cardData[9].trim());
        }

        // ซอย (index 12)
        if (cardData[12] != null && !cardData[12].trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(cardData[12].trim());
        }

        // แขวง/ตำบล (index 14)
        if (cardData[14] != null && !cardData[14].trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(cardData[14].trim());
        }

        // เขต/อำเภอ (index 15)
        if (cardData[15] != null && !cardData[15].trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(cardData[15].trim());
        }

        // จังหวัด (index 16)
        if (cardData[16] != null && !cardData[16].trim().isEmpty()) {
            if (address.length() > 0) address.append(" ");
            address.append(cardData[16].trim());
        }

        return address.toString().trim();
    }

    private String formatCitizenId(String citizenId) {
        if (citizenId == null || citizenId.length() != 13) {
            return citizenId;
        }

        // Format: X-XXXX-XXXXX-XX-X
        return citizenId.substring(0, 1) + "-" +
                citizenId.substring(1, 5) + "-" +
                citizenId.substring(5, 10) + "-" +
                citizenId.substring(10, 12) + "-" +
                citizenId.substring(12, 13);
    }

    private String formatDateFromBuddhistEra(String dateString) {
        if (dateString == null || dateString.length() != 8) {
            return dateString;
        }

        try {
            // Format from YYYYMMDD (Buddhist Era) to DD/MM/YYYY
            String year = dateString.substring(0, 4);
            String month = dateString.substring(4, 6);
            String day = dateString.substring(6, 8);

            // ข้อมูลจากบัตรเป็น พ.ศ. อยู่แล้ว (25xx)
            return day + "/" + month + "/" + year;
        } catch (Exception e) {
            Log.e(TAG, "Error formatting Buddhist date: " + dateString, e);
            return dateString;
        }
    }

    private void handleCardReadError(String code, String message) {
        String errorMessage;

        switch (code) {
            case "NO_DEVICE":
                errorMessage = "ไม่พบเครื่องอ่านบัตร กรุณาเชื่อมต่อเครื่องอ่านบัตร";
                break;
            case "PERMISSION_DENIED":
                errorMessage = "ไม่ได้รับอนุญาตให้เข้าถึงเครื่องอ่านบัตร";
                break;
            case "CARD_NOT_FOUND":
                errorMessage = "ไม่พบบัตรประชาชน กรุณาใส่บัตรให้ถูกต้อง";
                break;
            case "CONNECTION_FAILED":
                errorMessage = "เชื่อมต่อเครื่องอ่านบัตรไม่สำเร็จ";
                break;
            case "READER_ERROR":
                errorMessage = "เกิดข้อผิดพลาดที่เครื่องอ่านบัตร: " + message;
                break;
            default:
                errorMessage = "เกิดข้อผิดพลาด: " + message;
                break;
        }

        txtCardInfo.setText(errorMessage);
        imgPhoto.setVisibility(View.GONE);
        btnSaveData.setVisibility(View.GONE);
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();

        Log.e(TAG, "Card read error - Code: " + code + ", Message: " + message);

        btnReadCard.setEnabled(true);
        btnReadCard.setText("อ่านบัตรประชาชน");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up resources if needed
    }
}