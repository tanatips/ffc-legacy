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

public class CardiovascularRiskGaugeView extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private double currentRiskPercentage = 0; // เก็บเป็นเปอร์เซ็นต์
    private float maxRiskPercentage = 50; // ความเสี่ยงสูงสุดที่แสดงใน gauge (50%)

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม

    // สีสำหรับแต่ละระดับความเสี่ยงโรคหัวใจและหลอดเลือด
    private static final String COLOR_LOW_RISK = "#27AE60";        // เขียว - ความเสี่ยงน้อย (< 10%)
    private static final String COLOR_MEDIUM_RISK = "#F39C12";     // ส้ม - ความเสี่ยงปานกลาง (10-20%)
    private static final String COLOR_HIGH_RISK = "#E74C3C";       // แดง - ความเสี่ยงสูง (> 20%)

    public CardiovascularRiskGaugeView(Context context) {
        super(context);
        init();
    }

    public CardiovascularRiskGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardiovascularRiskGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        int centerY = h / 2 + (size / 4);

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
        drawRiskLabels(canvas);
    }

    private void drawGaugeBackground(Canvas canvas) {
        // วาดพื้นหลังของ Gauge
        canvas.drawArc(arcRect, START_ANGLE, SWEEP_ANGLE, false, backgroundPaint);
    }

    private void drawGaugeArcs(Canvas canvas) {
        // คำนวณมุมสำหรับแต่ละระดับความเสี่ยง
        float anglePerPercent = SWEEP_ANGLE / (float)maxRiskPercentage;

        // วาด arc สำหรับแต่ละระดับความเสี่ยง
        // 0-10%: ความเสี่ยงน้อย (เขียว)
        arcPaint.setColor(Color.parseColor(COLOR_LOW_RISK));
        canvas.drawArc(arcRect, START_ANGLE, anglePerPercent * 10, false, arcPaint);

        // 10-20%: ความเสี่ยงปานกลาง (ส้ม)
        arcPaint.setColor(Color.parseColor(COLOR_MEDIUM_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerPercent * 10), anglePerPercent * 10, false, arcPaint);

        // 20%+: ความเสี่ยงสูง (แดง)
        arcPaint.setColor(Color.parseColor(COLOR_HIGH_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerPercent * 20), anglePerPercent * (maxRiskPercentage - 20), false, arcPaint);
    }

    private void drawNeedle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // คำนวณมุมของเข็มตามเปอร์เซ็นต์ความเสี่ยง
        double clampedPercentage = Math.max(0, Math.min(currentRiskPercentage, maxRiskPercentage));
        float needleAngle = START_ANGLE + (float)(clampedPercentage / maxRiskPercentage) * SWEEP_ANGLE;
        double radians = Math.toRadians(needleAngle);

        // ความยาวของเข็ม
        float needleLength = arcRect.width() / 2 - 50;
        float needleWidth = 10f;

        // คำนวณตำแหน่งปลายเข็ม
        float needleEndX = centerX + needleLength * (float)Math.cos(radians);
        float needleEndY = centerY + needleLength * (float)Math.sin(radians);

        // สร้างรูปร่างของเข็ม
        needlePath.reset();
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

        // เปลี่ยนสีเข็มตามระดับความเสี่ยง
        RiskLevel currentLevel = getCurrentRiskLevel();
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

    private void drawRiskLabels(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();
        float radius = arcRect.width() / 2 - 80;

        textPaint.setTextSize(24f);
        textPaint.setColor(Color.parseColor("#2C3E50"));

        // คำนวณตำแหน่งสำหรับ label แต่ละระดับ
        // 0% (ซ้าย)
        double angle0 = Math.toRadians(START_ANGLE);
        float x0 = centerX + radius * (float)Math.cos(angle0);
        float y0 = centerY + radius * (float)Math.sin(angle0) + 10;
        canvas.drawText("0%", x0, y0, textPaint);

        // 10%
        double angle10 = Math.toRadians(START_ANGLE + SWEEP_ANGLE * 0.2);
        float x10 = centerX + radius * (float)Math.cos(angle10);
        float y10 = centerY + radius * (float)Math.sin(angle10) + 10;
        canvas.drawText("10%", x10, y10, textPaint);

        // 20%
        double angle20 = Math.toRadians(START_ANGLE + SWEEP_ANGLE * 0.4);
        float x20 = centerX + radius * (float)Math.cos(angle20);
        float y20 = centerY + radius * (float)Math.sin(angle20) + 10;
        canvas.drawText("20%", x20, y20, textPaint);

        // 50% (ขวา)
        double angle50 = Math.toRadians(START_ANGLE + SWEEP_ANGLE);
        float x50 = centerX + radius * (float)Math.cos(angle50);
        float y50 = centerY + radius * (float)Math.sin(angle50) + 10;
        canvas.drawText("50%", x50, y50, textPaint);
    }

    public void setRiskPercentage(double percentage) {
        // จำกัดค่าเปอร์เซ็นต์ให้อยู่ในช่วงที่กำหนด
        this.currentRiskPercentage = Math.max(0, Math.min(percentage, maxRiskPercentage));
        invalidate(); // ขอให้วาดใหม่
    }

    public double getRiskPercentage() {
        return currentRiskPercentage;
    }

    public RiskLevel getCurrentRiskLevel() {
        if (currentRiskPercentage < 10.0) {
            return new RiskLevel(0, 10, "กลุ่มเสี่ยงน้อย", COLOR_LOW_RISK, "😊", "CV_LOW");
        } else if (currentRiskPercentage >= 10.0 && currentRiskPercentage <= 20.0) {
            return new RiskLevel(10, 20, "กลุ่มเสี่ยงปานกลาง", COLOR_MEDIUM_RISK, "😟", "CV_MEDIUM");
        } else {
            return new RiskLevel(20, 100, "กลุ่มเสี่ยงสูง", COLOR_HIGH_RISK, "😰", "CV_HIGH");
        }
    }

    // คลาสสำหรับเก็บข้อมูลระดับความเสี่ยง
    public static class RiskLevel {
        public final double minPercentage;
        public final double maxPercentage;
        public final String label;
        public final String color;
        public final String emoji;
        public final String code;

        public RiskLevel(double minPercentage, double maxPercentage, String label, String color, String emoji, String code) {
            this.minPercentage = minPercentage;
            this.maxPercentage = maxPercentage;
            this.label = label;
            this.color = color;
            this.emoji = emoji;
            this.code = code;
        }
    }

    // Method สำหรับ Animation
    public void animateToPercentage(double targetPercentage) {
        setRiskPercentage(targetPercentage);
    }

    // Method สำหรับกำหนดสีแบบ Custom
    public void setCustomColors(String lowRisk, String mediumRisk, String highRisk) {
        // สามารถปรับแต่งสีได้ถ้าต้องการ
    }

    // Method สำหรับการตั้งค่าช่วงความเสี่ยงสูงสุด
    public void setMaxRiskPercentage(float maxPercentage) {
        this.maxRiskPercentage = maxPercentage;
        invalidate();
    }

    // Method สำหรับตรวจสอบว่าอยู่ในระดับเสี่ยงสูงหรือไม่
    public boolean isHighRisk() {
        return currentRiskPercentage > 20.0;
    }

    // Method สำหรับตรวจสอบว่าอยู่ในระดับเสี่ยงปานกลางหรือไม่
    public boolean isMediumRisk() {
        return currentRiskPercentage >= 10.0 && currentRiskPercentage <= 20.0;
    }

    // Method สำหรับตรวจสอบว่าอยู่ในระดับเสี่ยงน้อยหรือไม่
    public boolean isLowRisk() {
        return currentRiskPercentage < 10.0;
    }
}