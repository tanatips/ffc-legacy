package th.in.ffc.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

/**
 * SafeImageView Utility - ป้องกัน ImageView crash จากรูปภาพขนาดใหญ่
 * รองรับการโหลดจาก File, Uri, InputStream, byte array
 */
public class SafeImageView {

    private static final String TAG = "SafeImageView";

    // ขนาดสูงสุดของรูปภาพ (8MB)
    private static final int MAX_FILE_SIZE = 8 * 1024 * 1024;

    // ขนาดสูงสุดของ bitmap ในหน่วยความจำ (16MB)
    private static final int MAX_BITMAP_MEMORY = 16 * 1024 * 1024;

    // ขนาดสูงสุดของ dimension
    private static final int MAX_WIDTH = 1024;
    private static final int MAX_HEIGHT = 1024;

    // คุณภาพการบีบอัด
    private static final int JPEG_QUALITY = 85;

    /**
     * โหลดรูปภาพจากไฟล์อย่างปลอดภัย (Main Thread)
     */
    public static void loadImage(ImageView imageView, String filePath, int placeholderRes) {
        if (imageView == null) return;

        // แสดงรูป placeholder ก่อน
        imageView.setImageResource(placeholderRes);

        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        try {
            File file = new File(filePath);
            if (!file.exists() || !file.canRead()) {
                Log.w(TAG, "File not found or cannot read: " + filePath);
                return;
            }

            // ตรวจสอบขนาดไฟล์
            long fileSize = file.length();
            if (fileSize > MAX_FILE_SIZE) {
                Log.w(TAG, "File too large: " + fileSize + " bytes, path: " + filePath);
                return;
            }

            // โหลดรูปภาพอย่างปลอดภัย
            Bitmap bitmap = decodeSafelyFromFile(filePath, MAX_WIDTH, MAX_HEIGHT);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError loading image: " + filePath, e);
            handleOutOfMemory();
            imageView.setImageResource(placeholderRes);
        } catch (Exception e) {
            Log.e(TAG, "Error loading image: " + filePath, e);
            imageView.setImageResource(placeholderRes);
        }
    }

    /**
     * โหลดรูปภาพจากไฟล์อย่างปลอดภัย (Background Thread)
     */
    public static void loadImageAsync(ImageView imageView, String filePath, int placeholderRes) {
        if (imageView == null) return;

        // แสดงรูป placeholder ก่อน
        imageView.setImageResource(placeholderRes);

        if (filePath == null || filePath.trim().isEmpty()) {
            return;
        }

        // ยกเลิก task เก่าถ้ามี
        cancelPotentialWork(imageView, filePath);

        // เริ่ม task ใหม่
        ImageLoadTask task = new ImageLoadTask(imageView, placeholderRes);
        AsyncDrawable asyncDrawable = new AsyncDrawable(imageView.getContext(), task);
        imageView.setImageDrawable(asyncDrawable);
        task.execute(filePath);
    }

    /**
     * โหลดรูปภาพจาก Uri อย่างปลอดภัย
     */
    public static void loadImageFromUri(ImageView imageView, Context context, Uri uri, int placeholderRes) {
        if (imageView == null || context == null || uri == null) {
            if (imageView != null) {
                imageView.setImageResource(placeholderRes);
            }
            return;
        }

        imageView.setImageResource(placeholderRes);

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Bitmap bitmap = decodeSafelyFromStream(inputStream, MAX_WIDTH, MAX_HEIGHT);
                inputStream.close();

                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading image from Uri: " + uri, e);
            imageView.setImageResource(placeholderRes);
        }
    }

    /**
     * โหลดรูปภาพจาก byte array อย่างปลอดภัย
     */
    public static void loadImageFromBytes(ImageView imageView, byte[] data, int placeholderRes) {
        if (imageView == null) return;

        imageView.setImageResource(placeholderRes);

        if (data == null || data.length == 0) {
            return;
        }

        try {
            if (data.length > MAX_FILE_SIZE) {
                Log.w(TAG, "Byte array too large: " + data.length + " bytes");
                return;
            }

            Bitmap bitmap = decodeSafelyFromByteArray(data, MAX_WIDTH, MAX_HEIGHT);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error loading image from byte array", e);
            imageView.setImageResource(placeholderRes);
        }
    }

    /**
     * ตั้งค่า ScaleType อย่างปลอดภัย
     */
    public static void setScaleTypeSafely(ImageView imageView, ImageView.ScaleType scaleType) {
        if (imageView != null && scaleType != null) {
            try {
                imageView.setScaleType(scaleType);
            } catch (Exception e) {
                Log.e(TAG, "Error setting scale type", e);
            }
        }
    }

    /**
     * โหลดรูปภาพพร้อมตั้งค่า ScaleType
     */
    public static void loadImageWithScaleType(ImageView imageView, String filePath,
                                              int placeholderRes, ImageView.ScaleType scaleType) {
        loadImage(imageView, filePath, placeholderRes);
        setScaleTypeSafely(imageView, scaleType);
    }

    /**
     * Decode รูปภาพจากไฟล์อย่างปลอดภัย
     */
    private static Bitmap decodeSafelyFromFile(String filePath, int maxWidth, int maxHeight) {
        try {
            // ขั้นตอนที่ 1: อ่านขนาดก่อน
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                Log.w(TAG, "Invalid image dimensions in file: " + filePath);
                return null;
            }

            // ตรวจสอบขนาดในหน่วยความจำ
            long memorySize = (long) options.outWidth * options.outHeight * 4; // ARGB_8888
            if (memorySize > MAX_BITMAP_MEMORY) {
                Log.w(TAG, "Image too large in memory: " + memorySize + " bytes");
                // เพิ่ม sample size เพื่อลดขนาด
                maxWidth = Math.min(maxWidth, 512);
                maxHeight = Math.min(maxHeight, 512);
            }

            // ขั้นตอนที่ 2: คำนวณ sample size
            options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight);

            // ขั้นตอนที่ 3: decode รูปภาพจริง
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565; // ใช้หน่วยความจำน้อยกว่า
            options.inDither = false;
            options.inPurgeable = true;
            options.inInputShareable = true;

            return BitmapFactory.decodeFile(filePath, options);

        } catch (OutOfMemoryError e) {
            Log.e(TAG, "OutOfMemoryError decoding file: " + filePath, e);
            handleOutOfMemory();
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error decoding file: " + filePath, e);
            return null;
        }
    }

    /**
     * Decode รูปภาพจาก InputStream อย่างปลอดภัย
     */
    private static Bitmap decodeSafelyFromStream(InputStream stream, int maxWidth, int maxHeight) {
        try {
            BufferedInputStream bufferedStream = new BufferedInputStream(stream);

            // Mark สำหรับ reset stream
            bufferedStream.mark(stream.available());

            // อ่านขนาดก่อน
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(bufferedStream, null, options);

            // Reset stream
            bufferedStream.reset();

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                return null;
            }

            // คำนวณ sample size
            options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            return BitmapFactory.decodeStream(bufferedStream, null, options);

        } catch (Exception e) {
            Log.e(TAG, "Error decoding from stream", e);
            return null;
        }
    }

    /**
     * Decode รูปภาพจาก byte array อย่างปลอดภัย
     */
    private static Bitmap decodeSafelyFromByteArray(byte[] data, int maxWidth, int maxHeight) {
        try {
            // อ่านขนาดก่อน
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, options);

            if (options.outWidth <= 0 || options.outHeight <= 0) {
                return null;
            }

            // คำนวณ sample size
            options.inSampleSize = calculateSampleSize(options, maxWidth, maxHeight);
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            return BitmapFactory.decodeByteArray(data, 0, data.length, options);

        } catch (Exception e) {
            Log.e(TAG, "Error decoding from byte array", e);
            return null;
        }
    }

    /**
     * คำนวณ sample size
     */
    private static int calculateSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int inSampleSize = 1;

        if (options.outHeight > reqHeight || options.outWidth > reqWidth) {
            final int halfHeight = options.outHeight / 2;
            final int halfWidth = options.outWidth / 2;

            while ((halfHeight / inSampleSize) >= reqHeight &&
                    (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        // ป้องกันการใช้หน่วยความจำมากเกินไป
        long totalPixels = (options.outWidth / inSampleSize) * (options.outHeight / inSampleSize);
        long totalMemory = totalPixels * 2; // RGB_565 uses 2 bytes per pixel

        while (totalMemory > MAX_BITMAP_MEMORY && inSampleSize < 32) {
            inSampleSize *= 2;
            totalMemory = (options.outWidth / inSampleSize) * (options.outHeight / inSampleSize) * 2;
        }

        return inSampleSize;
    }

    /**
     * จัดการเมื่อเกิด OutOfMemoryError
     */
    private static void handleOutOfMemory() {
        try {
            System.gc();
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * ยกเลิก task ที่กำลังทำงานอยู่
     */
    private static boolean cancelPotentialWork(ImageView imageView, String filePath) {
        ImageLoadTask task = getImageLoadTask(imageView);

        if (task != null) {
            String taskPath = task.filePath;
            if (taskPath == null || !taskPath.equals(filePath)) {
                task.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * ดึง task ที่กำลังทำงานอยู่
     */
    private static ImageLoadTask getImageLoadTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getImageLoadTask();
            }
        }
        return null;
    }

    /**
     * AsyncTask สำหรับโหลดรูปภาพใน background
     */
    private static class ImageLoadTask extends AsyncTask<String, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewRef;
        private final int placeholderRes;
        String filePath;

        ImageLoadTask(ImageView imageView, int placeholderRes) {
            this.imageViewRef = new WeakReference<>(imageView);
            this.placeholderRes = placeholderRes;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            filePath = params[0];

            if (isCancelled()) return null;

            try {
                return decodeSafelyFromFile(filePath, MAX_WIDTH, MAX_HEIGHT);
            } catch (Exception e) {
                Log.e(TAG, "Error in background image loading", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewRef != null && bitmap != null) {
                ImageView imageView = imageViewRef.get();
                ImageLoadTask task = getImageLoadTask(imageView);

                if (this == task && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    /**
     * Drawable wrapper สำหรับ AsyncTask
     */
    private static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<ImageLoadTask> taskRef;

        AsyncDrawable(Context context, ImageLoadTask task) {
            super(context.getResources());
            taskRef = new WeakReference<>(task);
        }

        ImageLoadTask getImageLoadTask() {
            return taskRef.get();
        }
    }

    /**
     * Interface สำหรับ callback เมื่อโหลดเสร็จ
     */
    public interface OnImageLoadListener {
        void onImageLoaded(Bitmap bitmap);
        void onImageLoadFailed(Exception error);
    }

    /**
     * โหลดรูปภาพพร้อม callback
     */
    public static void loadImageWithCallback(String filePath, OnImageLoadListener listener) {
        new Thread(() -> {
            try {
                Bitmap bitmap = decodeSafelyFromFile(filePath, MAX_WIDTH, MAX_HEIGHT);
                if (listener != null) {
                    if (bitmap != null) {
                        listener.onImageLoaded(bitmap);
                    } else {
                        listener.onImageLoadFailed(new Exception("Failed to decode image"));
                    }
                }
            } catch (Exception e) {
                if (listener != null) {
                    listener.onImageLoadFailed(e);
                }
            }
        }).start();
    }
}