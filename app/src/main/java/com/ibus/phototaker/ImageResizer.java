package com.ibus.phototaker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.*;

public class ImageResizer {

    public static final String TAG = "ImageResizer";
    private static final int outputX = 200;

    // private static final int outputY = 200;

    public static boolean doResizeImage(File input, File output) {
        if (input != null && input.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(input
                        .getAbsolutePath());
                doResizeImage(bitmap, output);

                return true;
            } catch (Exception ex) {
                Log.e(TAG, "Delete temp file cause by can't ResizeImage");
                return false;
            }
        } else {
            Log.e(TAG, "input is null");
            return false;
        }
    }

    public static boolean doResizeImage(Bitmap bitmap, File output) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = outputX;
        // calculate the scale
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = scaleWidth;
        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        // Create Image File from baos
        intoJPEGfile(baos.toByteArray(), output);
        // input.delete();
        return true;
    }

    public static Bitmap decodeFileSubSampled(File file, int reqW, int reqH) {
        if (file == null)
            return null;
        try {

            FileInputStream fis = new FileInputStream(file);
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            o.inSampleSize = calculateInSampleSize(o, reqW, reqH);

            fis = new FileInputStream(file);
            // Decode with inSampleSize
            o.inJustDecodeBounds = false;
            Bitmap b = BitmapFactory.decodeStream(fis, null, o);
            fis.close();
            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    private static void intoJPEGfile(byte[] imageData, File output) {
        try {
            FileOutputStream buf;
            buf = new FileOutputStream(output);
            buf.write(imageData);
            buf.flush();
            buf.close();
        } catch (Exception e) {
            Log.v(TAG, "error while write picture to file");
            e.printStackTrace();
        }
    }

}
