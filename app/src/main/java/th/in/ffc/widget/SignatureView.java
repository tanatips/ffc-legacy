package th.in.ffc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

// แก้ไขใน SignatureView.java

public class SignatureView extends View {
    private static final String TAG = "SignatureView";

    private Path mPath;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    // เพิ่มตัวแปรสำหรับเก็บข้อมูลลายเซ็นอย่างถาวร
    private byte[] storedSignatureBytes = null;
    private boolean hasDrawnSignature = false;
    private boolean isLoadingSignature = false;

    // เพิ่มตัวแปรป้องกันการโหลดซ้ำ
    private boolean isRestoringSignature = false;
    private long lastRestoreTime = 0;
    private static final long RESTORE_COOLDOWN = 500; // 500ms

    public SignatureView(Context context) {
        this(context, null);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(6);

        setBackgroundColor(Color.WHITE);
        Log.d(TAG, "SignatureView initialized");
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged: " + w + "x" + h + ", old: " + oldw + "x" + oldh);

        if (w > 0 && h > 0) {
            // สร้าง Bitmap ใหม่
            createBitmap(w, h);

            // ถ้ามีลายเซ็นที่เก็บไว้ ให้โหลดกลับมา แต่ป้องกันการโหลดบ่อยเกินไป
            if (storedSignatureBytes != null && storedSignatureBytes.length > 0 && !isRestoringSignature) {
                Log.d(TAG, "Scheduling signature restore after size change");
                postDelayed(() -> {
                    if (!isRestoringSignature && storedSignatureBytes != null) {
                        restoreSignatureFromBytes();
                    }
                }, 100);
            }
        }
    }

    private void createBitmap(int width, int height) {
        // ล้าง bitmap เดิม
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }

        // สร้างใหม่
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.WHITE);

        Log.d(TAG, "Created new bitmap: " + width + "x" + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }

        // วาด path ปัจจุบัน (สำหรับลายเซ็นที่กำลังวาดอยู่)
        if (!mPath.isEmpty()) {
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void touchStart(float x, float y) {
        // ป้องกันการวาดระหว่างโหลดลายเซ็น
        if (isLoadingSignature || isRestoringSignature) {
            Log.d(TAG, "Touch ignored - currently loading signature");
            return;
        }

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        hasDrawnSignature = true;
        Log.d(TAG, "Touch start at: " + x + ", " + y);
    }

    private void touchMove(float x, float y) {
        if (isLoadingSignature || isRestoringSignature) {
            return;
        }

        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        if (isLoadingSignature || isRestoringSignature) {
            return;
        }

        mPath.lineTo(mX, mY);

        // บันทึก path ลงใน bitmap
        if (mCanvas != null) {
            mCanvas.drawPath(mPath, mPaint);

            // อัพเดตข้อมูลที่เก็บไว้
            updateStoredSignature();
        }

        mPath.reset();
        Log.d(TAG, "Touch up - signature committed and stored");
    }

    private void updateStoredSignature() {
        try {
            storedSignatureBytes = getSignatureAsByteArray();
            Log.d(TAG, "Updated stored signature, size: " +
                    (storedSignatureBytes != null ? storedSignatureBytes.length : "null"));
        } catch (Exception e) {
            Log.e(TAG, "Error updating stored signature", e);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // ป้องกันการ touch ระหว่างโหลดลายเซ็น
        if (isLoadingSignature || isRestoringSignature) {
            Log.d(TAG, "Touch event ignored - signature is being loaded/restored");
            return true;
        }

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    public void clear() {
        Log.d(TAG, "clear() called");

        // ป้องกันการเคลียร์ระหว่างกำลังโหลด
        if (isRestoringSignature) {
            Log.d(TAG, "Clear ignored - signature is being restored");
            return;
        }

        // รีเซ็ตสถานะ
        hasDrawnSignature = false;
        isLoadingSignature = false;
        storedSignatureBytes = null;

        if (getWidth() > 0 && getHeight() > 0) {
            createBitmap(getWidth(), getHeight());
            mPath.reset();
            invalidate();
            Log.d(TAG, "Signature cleared successfully");
        } else {
            post(() -> {
                if (getWidth() > 0 && getHeight() > 0) {
                    clear();
                }
            });
        }
    }

    public byte[] getSignatureAsByteArray() {
        if (mBitmap == null || mBitmap.isRecycled()) {
            Log.w(TAG, "Cannot get signature - bitmap is null or recycled, returning stored data");
            return storedSignatureBytes; // คืนค่าที่เก็บไว้
        }

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] result = baos.toByteArray();
            baos.close();

            Log.d(TAG, "Signature converted to byte array, size: " + result.length);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error converting signature to byte array", e);
            return storedSignatureBytes; // คืนค่าที่เก็บไว้
        }
    }

    public boolean isEmpty() {
        // ถ้ากำลังโหลดลายเซ็น ให้ถือว่าไม่ว่าง
        if (isLoadingSignature || isRestoringSignature) {
            Log.d(TAG, "isEmpty: false (currently loading/restoring)");
            return false;
        }

        // ตรวจสอบจากข้อมูลที่เก็บไว้ก่อน
        if (storedSignatureBytes != null && storedSignatureBytes.length > 0) {
            Log.d(TAG, "isEmpty: false (has stored signature)");
            return false;
        }

        // ถ้าไม่เคยมีการวาดเลย
        if (!hasDrawnSignature) {
            Log.d(TAG, "isEmpty: true (never drawn)");
            return true;
        }

        // ตรวจสอบว่ามี Bitmap หรือไม่
        if (mBitmap == null || mBitmap.isRecycled() || getWidth() <= 0 || getHeight() <= 0) {
            Log.d(TAG, "isEmpty: true (no valid bitmap)");
            return true;
        }

        // ตรวจสอบว่า bitmap เป็นภาพว่างหรือไม่
        try {
            Bitmap emptyBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas emptyCanvas = new Canvas(emptyBitmap);
            emptyCanvas.drawColor(Color.WHITE);

            boolean result = mBitmap.sameAs(emptyBitmap);
            emptyBitmap.recycle();

            Log.d(TAG, "isEmpty: " + result + " (bitmap comparison)");
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error checking if signature is empty", e);
            return storedSignatureBytes == null || storedSignatureBytes.length == 0;
        }
    }

    /**
     * เมธอดหลักสำหรับตั้งค่าลายเซ็นจาก byte array
     */
    public void setSignatureSafely(byte[] signatureBytes) {
        Log.d(TAG, "setSignatureSafely called, data size: " +
                (signatureBytes != null ? signatureBytes.length : "null"));

        if (signatureBytes == null || signatureBytes.length == 0) {
            Log.d(TAG, "No signature data provided, clearing view");
            clear();
            return;
        }

        // ป้องกันการตั้งค่าซ้ำๆ ในเวลาสั้นๆ
        long currentTime = System.currentTimeMillis();
        if (isRestoringSignature || (currentTime - lastRestoreTime) < RESTORE_COOLDOWN) {
            Log.d(TAG, "setSignatureSafely ignored - too soon or already restoring");
            return;
        }

        // เก็บข้อมูลไว้ก่อนเสมอ
        storedSignatureBytes = signatureBytes.clone();
        hasDrawnSignature = true;
        isLoadingSignature = true;

        if (getWidth() > 0 && getHeight() > 0) {
            // View มีขนาดแล้ว ตั้งค่าทันที
            restoreSignatureFromBytes();
        } else {
            // View ยังไม่มีขนาด รอจนกว่าจะมี
            Log.d(TAG, "View not sized yet, will load signature when ready");
            post(() -> {
                if (getWidth() > 0 && getHeight() > 0 && storedSignatureBytes != null) {
                    restoreSignatureFromBytes();
                }
            });
        }
    }

    /**
     * เมธอดภายในสำหรับกู้คืนลายเซ็นจากข้อมูลที่เก็บไว้
     */
    private void restoreSignatureFromBytes() {
        if (storedSignatureBytes == null || storedSignatureBytes.length == 0) {
            Log.w(TAG, "No stored signature data to restore");
            isLoadingSignature = false;
            return;
        }

        if (isRestoringSignature) {
            Log.d(TAG, "Already restoring signature, skipping");
            return;
        }

        isRestoringSignature = true;
        lastRestoreTime = System.currentTimeMillis();

        try {
            Log.d(TAG, "Restoring signature from stored bytes, size: " + storedSignatureBytes.length);

            // แปลง byte array เป็น Bitmap
            Bitmap signatureBitmap = BitmapFactory.decodeByteArray(
                    storedSignatureBytes, 0, storedSignatureBytes.length);

            if (signatureBitmap == null) {
                Log.e(TAG, "Failed to decode signature bitmap from stored bytes");
                return;
            }

            Log.d(TAG, "Decoded bitmap size: " + signatureBitmap.getWidth() + "x" + signatureBitmap.getHeight());

            // เตรียม canvas
            if (mBitmap == null || mBitmap.isRecycled()) {
                createBitmap(getWidth(), getHeight());
            } else {
                // เคลียร์ bitmap เดิม
                mCanvas.drawColor(Color.WHITE);
            }

            // คำนวณการ scale
            float scaleX = (float) getWidth() / signatureBitmap.getWidth();
            float scaleY = (float) getHeight() / signatureBitmap.getHeight();
            float scale = Math.min(scaleX, scaleY);

            if (scale != 1.0f && scale > 0) {
                Log.d(TAG, "Scaling signature bitmap by factor: " + scale);

                int newWidth = Math.round(signatureBitmap.getWidth() * scale);
                int newHeight = Math.round(signatureBitmap.getHeight() * scale);

                if (newWidth > 0 && newHeight > 0) {
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(signatureBitmap, newWidth, newHeight, true);

                    float left = (getWidth() - newWidth) / 2f;
                    float top = (getHeight() - newHeight) / 2f;
                    mCanvas.drawBitmap(scaledBitmap, left, top, null);

                    scaledBitmap.recycle();
                }
            } else {
                // วาดโดยไม่ scale
                mCanvas.drawBitmap(signatureBitmap, 0, 0, null);
            }

            signatureBitmap.recycle();

            // ตั้งสถานะ
            hasDrawnSignature = true;

            // บังคับให้ redraw
            post(() -> {
                invalidate();
                Log.d(TAG, "Signature restored and invalidated");
            });

            Log.d(TAG, "Signature restored successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error restoring signature from stored bytes", e);
        } finally {
            isLoadingSignature = false;
            isRestoringSignature = false;
        }
    }

    /**
     * บังคับให้ refresh ลายเซ็น (เรียกเมื่อมีปัญหาการแสดงผล)
     */
    public void refreshSignature() {
        Log.d(TAG, "refreshSignature() called");

        // ป้องกันการ refresh ระหว่างกำลัง restore
        if (isRestoringSignature) {
            Log.d(TAG, "refreshSignature ignored - currently restoring");
            return;
        }

        if (storedSignatureBytes != null && storedSignatureBytes.length > 0) {
            post(() -> {
                if (!isRestoringSignature && storedSignatureBytes != null) {
                    restoreSignatureFromBytes();
                }
            });
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);

        Log.d(TAG, "onVisibilityChanged: " + visibility);

        // เมื่อ View กลับมา visible ให้ refresh ลายเซ็น แต่ป้องกันการทำงานบ่อยเกินไป
        if (visibility == VISIBLE && storedSignatureBytes != null && !isRestoringSignature) {
            postDelayed(() -> {
                if (getVisibility() == VISIBLE && !isRestoringSignature && storedSignatureBytes != null) {
                    refreshSignature();
                }
            }, 300);
        }
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);

        Log.d(TAG, "onWindowVisibilityChanged: " + visibility);

        // เมื่อ Window กลับมา visible ให้ refresh ลายเซ็น แต่ป้องกันการทำงานบ่อยเกินไป
        if (visibility == VISIBLE && storedSignatureBytes != null && !isRestoringSignature) {
            postDelayed(() -> {
                if (getWindowVisibility() == VISIBLE && !isRestoringSignature && storedSignatureBytes != null) {
                    refreshSignature();
                }
            }, 500);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        // ไม่ล้าง storedSignatureBytes เพื่อให้สามารถกู้คืนได้เมื่อ attach กลับมา

        // ทำความสะอาดเฉพาะ bitmap ที่ใช้งานอยู่
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }

        Log.d(TAG, "SignatureView detached, bitmap cleaned but signature data preserved");
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "SignatureView attached to window");

        // เมื่อ attach กลับมา ให้ restore ลายเซ็นถ้ามี
        if (storedSignatureBytes != null && storedSignatureBytes.length > 0) {
            postDelayed(() -> {
                if (getWidth() > 0 && getHeight() > 0 && !isRestoringSignature) {
                    Log.d(TAG, "Restoring signature after reattach");
                    restoreSignatureFromBytes();
                }
            }, 200);
        }
    }

    // เพิ่มเมธอดสำหรับ debug
    public void debugSignatureState() {
        Log.d(TAG, "=== SIGNATURE VIEW DEBUG STATE ===");
        Log.d(TAG, "View size: " + getWidth() + "x" + getHeight());
        Log.d(TAG, "View visibility: " + getVisibility());
        Log.d(TAG, "Window visibility: " + getWindowVisibility());
        Log.d(TAG, "hasDrawnSignature: " + hasDrawnSignature);
        Log.d(TAG, "isLoadingSignature: " + isLoadingSignature);
        Log.d(TAG, "isRestoringSignature: " + isRestoringSignature);
        Log.d(TAG, "storedSignatureBytes: " + (storedSignatureBytes != null ? storedSignatureBytes.length + " bytes" : "null"));
        Log.d(TAG, "mBitmap: " + (mBitmap != null && !mBitmap.isRecycled() ? "valid " + mBitmap.getWidth() + "x" + mBitmap.getHeight() : "invalid"));
        Log.d(TAG, "=== END SIGNATURE VIEW DEBUG STATE ===");
    }
}