package com.blayzupe.phototaker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class ImageResizer {

    public static final String TAG = "ImageResizer";
    protected int outputX = 100;
    protected int outputY = 120;

    public boolean doResizeImage(File input, File output, boolean deleteInput) {
        if (!input.exists())
            return false;

        try {
            Bitmap bitmap = BitmapFactory.decodeFile(input.getAbsolutePath());
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = outputX;

            // calculate the scale
            float scaleWidth, scaleHeight = scaleWidth = ((float) newWidth) / width;
            // float scaleHeight = scaleWidth;

            // createa matrix for the manipulation
            Matrix matrix = new Matrix();
            // resize the bit map
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
                    height, matrix, true);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
            // Create Image File from baos
            intoJPEGfile(baos.toByteArray(), output);

            if (deleteInput)
                input.delete();
            return true;
        } catch (Exception ex) {
            Log.e(TAG, "Delete temp file cause by can't ResizeImage");
            return false;
        }
    }

    private void intoJPEGfile(byte[] imageData, File output) {
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

    public boolean doResizeImage(File input, File output) {
        return doResizeImage(input, output, true);
    }

}
