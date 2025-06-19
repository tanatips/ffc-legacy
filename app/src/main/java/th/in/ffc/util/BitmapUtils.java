package th.in.ffc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Utility class สำหรับจัดการ Bitmap อย่างปลอดภัย
 * ป้องกันปัญหา OutOfMemoryError และ Canvas too large bitmap
 */
public class BitmapUtils {

    private static final String TAG = "BitmapUtils";

    // ขนาดสูงสุดของ bitmap (16MB)
    private static final int MAX_BITMAP_SIZE = 16 * 1024 * 1024; // 16MB

    // ขนาดสูงสุดของ dimension
    private static final int MAX_DIMENSION = 2048;

    // คุณภาพการบีบอัด JPEG (0-100)
    private static final int JPEG_QUALITY = 85;

    /**
     * แปลง byte array เป็น Bitmap อย่างปลอดภัย
     * @param data byte array ของรูปภาพ
     * @param maxWidth ความกว้างสูงสุด
     * @param maxHeight ความสูงสูงสุด
     * @return Bitmap ที่ปรับขนาดแล้ว หรือ null ถ้าผิดพลาด
     */
    public static Bitmap safeDecodeByteArray(byte[] data, int maxWidth, int maxHeight) {
        if (data == null || data.length == 0) {
            Log.w(TAG, "Invalid byte array data");
            return null;
        }

        try {
            // ตรวจสอบขนาดข้อมูล
            if (data.length > MAX_BITMAP_SIZE) {
                Log.w(TAG, "Byte array too large: " + data.length + " bytes");
                // ลองบีบอัดข้อมูลก่อน
                data = compressByteArray(data);
                if (data == null) {
                    return null;
                }
            }

            // ขั้นตอนที่ 1: อ่านขนาดของรูปภาพก่อน (ไม่โหลดรูปจริง)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            // ตรวจสอบว่าเป็นรูปภาพที่ถูกต้องหรือไม่
            if (options.outWidth <= 0 || options.outHeight <= 0) {
                Log.w(TAG, "Invalid image dimensions");
                return null;
            }

            Log.d(TAG, "Original image size: " + options.outWidth + "x" + options.outHeight);

            // ขั้นตอนที่ 2: คำนวณ sample size
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);

            // ขั้นตอนที่ 3: โหลดรูปภาพจริงด้วย sample size
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // ใช้หน่วยความจำน้อยกว่า ARGB_8888
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

            if (bitmap == null) {
                Log.w(TAG, "Failed to decode bitmap");
                return null;
            }

            Log.d(TAG, "Decoded bitmap size: " + bitmap.getWidth() + "x" + bitmap.getHeight());

            // ขั้นตอนที่ 4: ปรับขนาดเพิ่มเติมถ้าจำเป็น
            if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
                bitmap = resizeBitmap(bitmap, maxWidth, maxHeight);
            }

            return bitmap;

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when decoding bitmap", e);
            System.gc(); // บังคับ garbage collection
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error decoding bitmap", e);
            return null;
        }
    }

    /**
     * คำนวณ sample size สำหรับลดขนาดรูปภาพ
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        // ตรวจสอบไม่ให้ sample size เล็กเกินไป
        long totalPixels = (width / inSampleSize) * (height / inSampleSize);
        long totalMemory = totalPixels * 4; // ARGB_8888 uses 4 bytes per pixel

        while (totalMemory > MAX_BITMAP_SIZE && inSampleSize < 8) {
            inSampleSize *= 2;
            totalMemory = (width / inSampleSize) * (height / inSampleSize) * 4;
        }

        Log.d(TAG, "Calculated inSampleSize: " + inSampleSize);
        return inSampleSize;
    }

    /**
     * ปรับขนาด Bitmap
     */
    private static Bitmap resizeBitmap(Bitmap original, int maxWidth, int maxHeight) {
        if (original == null) return null;

        int width = original.getWidth();
        int height = original.getHeight();

        // คำนวณขนาดใหม่โดยรักษาอัตราส่วน
        float scaleX = (float) maxWidth / width;
        float scaleY = (float) maxHeight / height;
        float scale = Math.min(scaleX, scaleY);

        if (scale >= 1) {
            return original; // ไม่ต้องปรับขนาด
        }

        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        try {
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            Bitmap resized = Bitmap.createBitmap(original, 0, 0, width, height, matrix, true);

            // ลบ bitmap เดิมถ้าไม่ใช่ตัวเดียวกัน
            if (resized != original) {
                original.recycle();
            }

            Log.d(TAG, "Resized bitmap to: " + newWidth + "x" + newHeight);
            return resized;

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when resizing bitmap", e);
            return original;
        }
    }

    /**
     * บีบอัด byte array
     */
    private static byte[] compressByteArray(byte[] data) {
        try {
            // ลองใช้ sample size สูงเพื่อลดขนาด
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4; // ลดขนาดเป็น 1/4
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            if (bitmap == null) return null;

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos); // คุณภาพต่ำลง
            bitmap.recycle();

            byte[] compressedData = baos.toByteArray();
            baos.close();

            Log.d(TAG, "Compressed from " + data.length + " to " + compressedData.length + " bytes");
            return compressedData;

        } catch (Exception e) {
            Log.e(TAG, "Error compressing byte array", e);
            return null;
        }
    }

    /**
     * แปลง Drawable เป็น Bitmap อย่างปลอดภัย
     */
    public static Bitmap drawableToBitmap(Drawable drawable, int maxWidth, int maxHeight) {
        if (drawable == null) return null;

        try {
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap != null) {
                    return resizeBitmapIfNeeded(bitmap, maxWidth, maxHeight);
                }
            }

            // สำหรับ VectorDrawable หรือ Drawable อื่นๆ
            int width = Math.min(drawable.getIntrinsicWidth(), maxWidth);
            int height = Math.min(drawable.getIntrinsicHeight(), maxHeight);

            if (width <= 0 || height <= 0) {
                width = maxWidth;
                height = maxHeight;
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);

            return bitmap;

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when converting drawable to bitmap", e);
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error converting drawable to bitmap", e);
            return null;
        }
    }

    /**
     * ปรับขนาด Bitmap ถ้าจำเป็น
     */
    private static Bitmap resizeBitmapIfNeeded(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) return null;

        if (bitmap.getWidth() <= maxWidth && bitmap.getHeight() <= maxHeight) {
            return bitmap;
        }

        return resizeBitmap(bitmap, maxWidth, maxHeight);
    }

    /**
     * แปลง Bitmap เป็น byte array อย่างปลอดภัย
     */
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        if (bitmap == null) return null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, baos);
            byte[] data = baos.toByteArray();
            baos.close();
            return data;
        } catch (Exception e) {
            Log.e(TAG, "Error converting bitmap to byte array", e);
            return null;
        }
    }

    /**
     * ตั้งค่ารูปภาพให้ ImageView อย่างปลอดภัย
     */
    public static void setImageViewBitmap(ImageView imageView, byte[] imageData, int defaultImageRes) {
        if (imageView == null) return;

        try {
            if (imageData != null && imageData.length > 0) {
                // กำหนดขนาดสูงสุดตาม ImageView
                int maxWidth = imageView.getMaxWidth() > 0 ? imageView.getMaxWidth() : MAX_DIMENSION;
                int maxHeight = imageView.getMaxHeight() > 0 ? imageView.getMaxHeight() : MAX_DIMENSION;

                Bitmap bitmap = safeDecodeByteArray(imageData, maxWidth, maxHeight);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    return;
                }
            }

            // ถ้าไม่สามารถโหลดรูปได้ ให้ใช้รูปดีฟอลต์
            imageView.setImageResource(defaultImageRes);

        } catch (Exception e) {
            Log.e(TAG, "Error setting ImageView bitmap", e);
            imageView.setImageResource(defaultImageRes);
        }
    }

    /**
     * ล้างหน่วยความจำ
     */
    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }
    public static void setImageViewFromFile(ImageView imageView, String filePath, int defaultImageRes) {
        if (imageView == null || filePath == null) {
            if (imageView != null) {
                imageView.setImageResource(defaultImageRes);
            }
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                Log.w(TAG, "File does not exist or cannot be read: " + filePath);
                imageView.setImageResource(defaultImageRes);
                return;
            }

            // ตรวจสอบขนาดไฟล์
            long fileSize = file.length();
            if (fileSize > MAX_BITMAP_SIZE) {
                Log.w(TAG, "File too large: " + fileSize + " bytes");
                imageView.setImageResource(defaultImageRes);
                return;
            }

            // กำหนดขนาดสูงสุดตาม ImageView
            int maxWidth = imageView.getMaxWidth() > 0 ? imageView.getMaxWidth() : MAX_DIMENSION;
            int maxHeight = imageView.getMaxHeight() > 0 ? imageView.getMaxHeight() : MAX_DIMENSION;

            // ขั้นตอนที่ 1: อ่านขนาดของรูปภาพก่อน
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            // ตรวจสอบว่าเป็นรูปภาพที่ถูกต้องหรือไม่
            if (options.outWidth <= 0 || options.outHeight <= 0) {
                Log.w(TAG, "Invalid image file: " + filePath);
                imageView.setImageResource(defaultImageRes);
                return;
            }

            Log.d(TAG, "Original file image size: " + options.outWidth + "x" + options.outHeight);

            // ขั้นตอนที่ 2: คำนวณ sample size
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);

            // ขั้นตอนที่ 3: โหลดรูปภาพจริง
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            if (bitmap == null) {
                Log.w(TAG, "Failed to decode file: " + filePath);
                imageView.setImageResource(defaultImageRes);
                return;
            }

            // ขั้นตอนที่ 4: ปรับขนาดเพิ่มเติมถ้าจำเป็น
            if (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight) {
                bitmap = resizeBitmap(bitmap, maxWidth, maxHeight);
            }

            imageView.setImageBitmap(bitmap);
            Log.d(TAG, "Successfully loaded image from file: " + filePath);

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError when loading file: " + filePath, e);
            System.gc();
            imageView.setImageResource(defaultImageRes);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image from file: " + filePath, e);
            imageView.setImageResource(defaultImageRes);
        }
    }

    // เพิ่ม method สำหรับตั้งค่า ScaleType อย่างปลอดภัย
    public static void setImageViewFromFileWithScaleType(ImageView imageView, String filePath,
                                                         int defaultImageRes, ImageView.ScaleType scaleType) {
        setImageViewFromFile(imageView, filePath, defaultImageRes);

        if (imageView != null && scaleType != null) {
            imageView.setScaleType(scaleType);
        }
    }

    // เพิ่ม method สำหรับการโหลดแบบ async (ไม่บล็อก UI thread)
    public static void setImageViewFromFileAsync(ImageView imageView, String filePath,
                                                 int defaultImageRes, ImageView.ScaleType scaleType) {
        if (imageView == null) return;

        // แสดงรูป loading หรือ default ก่อน
        imageView.setImageResource(defaultImageRes);

        // โหลดรูปภาพใน background thread
        new Thread(() -> {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    return;
                }

                // โหลดรูปภาพ
                int maxWidth = MAX_DIMENSION;
                int maxHeight = MAX_DIMENSION;

                Bitmap bitmap = safeDecodeFile(filePath, maxWidth, maxHeight);

                // กลับไปแสดงผลใน UI thread
                if (bitmap != null) {
                    imageView.post(() -> {
                        imageView.setImageBitmap(bitmap);
                        if (scaleType != null) {
                            imageView.setScaleType(scaleType);
                        }
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Error in async image loading", e);
            }
        }).start();
    }
    private static Bitmap safeDecodeFile(String filePath, int maxWidth, int maxHeight) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                return null;
            }

            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            if (bitmap != null && (bitmap.getWidth() > maxWidth || bitmap.getHeight() > maxHeight)) {
                bitmap = resizeBitmap(bitmap, maxWidth, maxHeight);
            }

            return bitmap;

        } catch (Exception e) {
            Log.e(TAG, "Error decoding file: " + filePath, e);
            return null;
        }
    }
}