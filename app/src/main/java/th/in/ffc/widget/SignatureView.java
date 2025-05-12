package th.in.ffc.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayOutputStream;

public class SignatureView extends View {
    private Path mPath;
    private Paint mPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private byte[] pendingSignatureBytes = null;


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
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

            // ถ้ามีลายเซ็นที่รอโหลด ให้โหลดตอนนี้
            if (pendingSignatureBytes != null) {
                byte[] temp = pendingSignatureBytes;
                pendingSignatureBytes = null;
                setSignatureFromByteArray(temp);
            }
        }
    }
    // เพิ่มเมธอดสำหรับตั้งค่าลายเซ็นที่ปลอดภัย
    public void setSignatureSafely(byte[] signatureBytes) {
        if (getWidth() > 0 && getHeight() > 0) {
            // ถ้า View มีขนาดแล้ว ให้ตั้งค่าทันที
            setSignatureFromByteArray(signatureBytes);
        } else {
            // ถ้า View ยังไม่มีขนาด ให้เก็บไว้ก่อน
            pendingSignatureBytes = signatureBytes;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        canvas.drawPath(mPath, mPaint);
    }

    private void touchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        // Commit the path to the canvas
        mCanvas.drawPath(mPath, mPaint);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
        // ตรวจสอบว่า View มีขนาดมากกว่า 0 ก่อนสร้าง Bitmap
        if (getWidth() > 0 && getHeight() > 0) {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            invalidate();
        } else {
            // ถ้า View ยังไม่มีขนาด ให้เก็บสถานะว่าต้องการล้างข้อมูล
            // และจะดำเนินการเมื่อ View มีขนาดแล้ว
            post(() -> {
                if (getWidth() > 0 && getHeight() > 0) {
                    mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                    mCanvas = new Canvas(mBitmap);
                    invalidate();
                }
            });
        }
    }

    public byte[] getSignatureAsByteArray() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public boolean isEmpty() {
        // ตรวจสอบว่ามี Bitmap หรือไม่
        if (mBitmap == null || mBitmap.isRecycled() || getWidth() <= 0 || getHeight() <= 0) {
            return true;
        }

        // สร้าง Bitmap ว่าง (สีขาว) เพื่อเปรียบเทียบ
        Bitmap emptyBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas emptyCanvas = new Canvas(emptyBitmap);
        emptyCanvas.drawColor(Color.WHITE);

        boolean result = mBitmap.sameAs(emptyBitmap);
        emptyBitmap.recycle(); // คืนหน่วยความจำ
        return result;
    }
    /**
     * ตั้งค่าลายเซ็นจาก byte array
     * @param signatureBytes byte array ที่เก็บข้อมูลลายเซ็น
     */
    public void setSignatureFromByteArray(byte[] signatureBytes) {
        if (signatureBytes == null || signatureBytes.length == 0) {
            clear();
            return;
        }

        try {
            // แปลง byte array เป็น Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(signatureBytes, 0, signatureBytes.length);
            if (bitmap != null) {
                // ตรวจสอบว่า View มีขนาดมากกว่า 0 หรือยัง
                if (getWidth() > 0 && getHeight() > 0) {
                    // ล้างลายเซ็นเดิม
                    if (mBitmap == null || mBitmap.isRecycled()) {
                        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                        mCanvas = new Canvas(mBitmap);
                    } else {
                        // เคลียร์ Bitmap ที่มีอยู่แทนการสร้างใหม่
                        mCanvas.drawColor(Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR);
                    }

                    // วาดลายเซ็นใหม่จาก Bitmap
                    Canvas canvas = new Canvas(mBitmap);
                    canvas.drawBitmap(bitmap, 0, 0, null); // ไม่ใช้ mPaint ที่นี่เพราะอาจมีผลต่อการแสดงผล

                    // อัพเดตการแสดงผล
                    invalidate();
                } else {
                    // ถ้า View ยังไม่มีขนาด ให้รอจนกว่า View จะถูกวาด
                    final Bitmap finalBitmap = bitmap;
                    post(() -> {
                        if (getWidth() > 0 && getHeight() > 0) {
                            // สร้าง Bitmap สำหรับ View ถ้ายังไม่มี
                            if (mBitmap == null || mBitmap.isRecycled()) {
                                mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                                mCanvas = new Canvas(mBitmap);
                            }

                            // วาดลายเซ็นจาก Bitmap
                            mCanvas.drawBitmap(finalBitmap, 0, 0, null);
                            invalidate();
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
