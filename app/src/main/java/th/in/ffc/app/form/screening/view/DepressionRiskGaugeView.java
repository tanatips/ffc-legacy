package th.in.ffc.app.form.screening.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class DepressionRiskGaugeView extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private int currentScore = 0;
    private int maxScore = 27; // คะแนนสูงสุด (9 คำถาม x 3 คะแนน)

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม
    private static final float MAX_NEEDLE_ANGLE = SWEEP_ANGLE; // มุมสูงสุดของเข็ม

    // สีสำหรับแต่ละระดับความรุนแรง
    private static final String COLOR_NONE = "#27AE60";         // เขียว - ไม่มีอาการ
    private static final String COLOR_MILD = "#F39C12";        // เหลือง - ระดับน้อย
    private static final String COLOR_MODERATE = "#E67E22";    // ส้ม - ระดับปานกลาง
    private static final String COLOR_SEVERE = "#E74C3C";      // แดง - ระดับรุนแรง

    public DepressionRiskGaugeView(Context context) {
        super(context);
        init();
    }

    public DepressionRiskGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DepressionRiskGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // สร้าง Paint objects
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(35f);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setStyle(Paint.Style.FILL);
        needlePaint.setColor(Color.parseColor("#2C3E50"));

        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setColor(Color.parseColor("#34495E"));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setColor(Color.parseColor("#2C3E50"));

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(35f);
        backgroundPaint.setStrokeCap(Paint.Cap.ROUND);
        backgroundPaint.setColor(Color.parseColor("#ECF0F1"));

        arcRect = new RectF();
        needlePath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // คำนวณขนาดและตำแหน่งของ Gauge
        int padding = 5;
        int size = Math.min(w, h) - (padding * 2);
        int centerX = w / 2;
        int centerY = h / 2 + (size / 4); // ปรับตำแหน่งให้ดูสมดุล

        int left = centerX - size / 2;
        int top = centerY - size / 2;
        int right = centerX + size / 2;
        int bottom = centerY + size / 2;

        arcRect.set(left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawGaugeBackground(canvas);
        drawGaugeArcs(canvas);
        drawNeedle(canvas);
        drawCenterCircle(canvas);
    }

    private void drawGaugeBackground(Canvas canvas) {
        // วาดพื้นหลังของ Gauge
        canvas.drawArc(arcRect, START_ANGLE, SWEEP_ANGLE, false, backgroundPaint);
    }

    private void drawGaugeArcs(Canvas canvas) {
        // คำนวณมุมสำหรับแต่ละระดับความรุนแรง
        float anglePerScore = SWEEP_ANGLE / maxScore;

        // วาด arc สำหรับแต่ละระดับความรุนแรง
        // 0-6 คะแนน: ไม่มีอาการ (เขียว)
        arcPaint.setColor(Color.parseColor(COLOR_NONE));
        canvas.drawArc(arcRect, START_ANGLE, anglePerScore * 7, false, arcPaint);

        // 7-12 คะแนน: ระดับน้อย (เหลือง)
        arcPaint.setColor(Color.parseColor(COLOR_MILD));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 7), anglePerScore * 6, false, arcPaint);

        // 13-18 คะแนน: ระดับปานกลาง (ส้ม)
        arcPaint.setColor(Color.parseColor(COLOR_MODERATE));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 13), anglePerScore * 6, false, arcPaint);

        // 19+ คะแนน: ระดับรุนแรง (แดง)
        arcPaint.setColor(Color.parseColor(COLOR_SEVERE));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 19), anglePerScore * (maxScore - 19), false, arcPaint);
    }

    private void drawNeedle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // คำนวณมุมของเข็มตามคะแนน
        float needleAngle = START_ANGLE + (currentScore / (float)maxScore) * SWEEP_ANGLE;
        double radians = Math.toRadians(needleAngle);

        // ความยาวของเข็ม
        float needleLength = arcRect.width() / 2 - 50;
        float needleWidth = 10f;

        // คำนวณตำแหน่งปลายเข็ม
        float needleEndX = centerX + needleLength * (float)Math.cos(radians);
        float needleEndY = centerY + needleLength * (float)Math.sin(radians);

        // สร้างรูปร่างของเข็ม
        needlePath.reset();

        // จุดกึ่งกลาง
        needlePath.moveTo(centerX, centerY);

        // คำนวณจุดข้างๆ ของเข็ม
        double perpendicular1 = radians + Math.PI / 2;
        double perpendicular2 = radians - Math.PI / 2;

        float side1X = centerX + needleWidth * (float)Math.cos(perpendicular1);
        float side1Y = centerY + needleWidth * (float)Math.sin(perpendicular1);
        float side2X = centerX + needleWidth * (float)Math.cos(perpendicular2);
        float side2Y = centerY + needleWidth * (float)Math.sin(perpendicular2);

        // วาดเข็มแบบสามเหลี่ยม
        needlePath.lineTo(side1X, side1Y);
        needlePath.lineTo(needleEndX, needleEndY);
        needlePath.lineTo(side2X, side2Y);
        needlePath.close();

        // เปลี่ยนสีเข็มตามระดับความรุนแรง
        DepressionLevel currentLevel = getCurrentDepressionLevel();
        needlePaint.setColor(Color.parseColor(currentLevel.color));

        canvas.drawPath(needlePath, needlePaint);
    }

    private void drawCenterCircle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // วาดวงกลมตรงกลาง
        canvas.drawCircle(centerX, centerY, 18f, centerPaint);

        // วาดวงกลมขาวข้างใน
        centerPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, 10f, centerPaint);
        centerPaint.setColor(Color.parseColor("#34495E")); // รีเซ็ตสี
    }

    public void setScore(int score) {
        // จำกัดค่าคะแนนให้อยู่ในช่วงที่กำหนด
        this.currentScore = Math.max(0, Math.min(score, maxScore));
        invalidate(); // ขอให้วาดใหม่
    }

    public int getScore() {
        return currentScore;
    }

    public DepressionLevel getCurrentDepressionLevel() {
        if (currentScore < 7) {
            return new DepressionLevel(0, 6, "ไม่มีอาการของโรคซึมเศร้า", COLOR_NONE, "😊", "1B0260|1B0282");
        } else if (currentScore >= 7 && currentScore <= 12) {
            return new DepressionLevel(7, 12, "มีอาการของโรคซึมเศร้าระดับน้อย", COLOR_MILD, "😐", "1B0261|1B0283");
        } else if (currentScore >= 13 && currentScore <= 18) {
            return new DepressionLevel(13, 18, "มีอาการของโรคซึมเศร้าระดับปานกลาง", COLOR_MODERATE, "😟", "1B0262|1B0284");
        } else {
            return new DepressionLevel(19, 27, "มีอาการของโรคซึมเศร้าระดับรุนแรง", COLOR_SEVERE, "😰", "1B0263|1B0285");
        }
    }

    // คลาสสำหรับเก็บข้อมูลระดับความรุนแรง
    public static class DepressionLevel {
        public final int minScore;
        public final int maxScore;
        public final String label;
        public final String color;
        public final String emoji;
        public final String code;

        public DepressionLevel(int minScore, int maxScore, String label, String color, String emoji, String code) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.label = label;
            this.color = color;
            this.emoji = emoji;
            this.code = code;
        }
    }

    // Method สำหรับ Animation (ถ้าต้องการ)
    public void animateToScore(int targetScore) {
        setScore(targetScore);
    }

    // Method สำหรับกำหนดสีแบบ Custom
    public void setCustomColors(String none, String mild, String moderate, String severe) {
        // สามารถปรับแต่งสีได้ถ้าต้องการ
    }
}