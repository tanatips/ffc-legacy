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

public class BloodSugarGaugeView extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private double currentGlucoseLevel = 0.0;
    private double maxGlucoseLevel = 200.0; // ค่าสูงสุดที่แสดงใน Gauge (mg/dL)
    private double minGlucoseLevel = 60.0;  // ค่าต่ำสุดที่แสดงใน Gauge (mg/dL)

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม

    // สีสำหรับแต่ละระดับน้ำตาลในเลือด (3 ระดับ)
    private static final String COLOR_NORMAL = "#27AE60";           // เขียว - ปกติ (<100)
    private static final String COLOR_PREDIABETES = "#F39C12";     // ส้ม - เสี่ยงเบาหวาน (100-125)
    private static final String COLOR_DIABETES = "#E74C3C";        // แดง - เบาหวาน (≥126)

    public BloodSugarGaugeView(Context context) {
        super(context);
        init();
    }

    public BloodSugarGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BloodSugarGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
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
    }

    private void drawGaugeBackground(Canvas canvas) {
        // วาดพื้นหลังของ Gauge
        canvas.drawArc(arcRect, START_ANGLE, SWEEP_ANGLE, false, backgroundPaint);
    }

    private void drawGaugeArcs(Canvas canvas) {
        // คำนวณสัดส่วนมุมสำหรับแต่ละระดับน้ำตาล (3 ระดับ)
        double totalRange = maxGlucoseLevel - minGlucoseLevel;

        // ปกติ: 60-99 mg/dL
        float normalRange = (float)((100 - minGlucoseLevel) / totalRange);
        float normalAngle = SWEEP_ANGLE * normalRange;

        // เสี่ยงเบาหวาน: 100-125 mg/dL
        float prediabetesRange = (float)(26.0 / totalRange);
        float prediabetesAngle = SWEEP_ANGLE * prediabetesRange;

        // เบาหวาน: 126+ mg/dL
        float diabetesAngle = SWEEP_ANGLE - (normalAngle + prediabetesAngle);

        float currentAngle = START_ANGLE;

        // วาด arc สำหรับแต่ละระดับ
        // ปกติ (<100 mg/dL) - เขียว
        arcPaint.setColor(Color.parseColor(COLOR_NORMAL));
        canvas.drawArc(arcRect, currentAngle, normalAngle, false, arcPaint);
        currentAngle += normalAngle;

        // เสี่ยงเบาหวาน (100-125 mg/dL) - ส้ม
        arcPaint.setColor(Color.parseColor(COLOR_PREDIABETES));
        canvas.drawArc(arcRect, currentAngle, prediabetesAngle, false, arcPaint);
        currentAngle += prediabetesAngle;

        // เบาหวาน (≥126 mg/dL) - แดง
        arcPaint.setColor(Color.parseColor(COLOR_DIABETES));
        canvas.drawArc(arcRect, currentAngle, diabetesAngle, false, arcPaint);
    }

    private void drawNeedle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // คำนวณมุมของเข็มตามระดับน้ำตาล
        double normalizedValue = Math.max(0, Math.min(1,
                (currentGlucoseLevel - minGlucoseLevel) / (maxGlucoseLevel - minGlucoseLevel)));
        float needleAngle = START_ANGLE + (float)(normalizedValue * SWEEP_ANGLE);
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

        // เปลี่ยนสีเข็มตามระดับน้ำตาล
        BloodSugarLevel currentLevel = getCurrentBloodSugarLevel();
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

    public void setGlucoseLevel(double glucoseLevel) {
        // จำกัดค่าให้อยู่ในช่วงที่กำหนด
        this.currentGlucoseLevel = Math.max(minGlucoseLevel, Math.min(glucoseLevel, maxGlucoseLevel));
        invalidate(); // ขอให้วาดใหม่
    }

    public double getGlucoseLevel() {
        return currentGlucoseLevel;
    }

    public BloodSugarLevel getCurrentBloodSugarLevel() {
        if (currentGlucoseLevel < 100) {
            return new BloodSugarLevel(0, 99, "ปกติ", COLOR_NORMAL, "😊", "BS01");
        } else if (currentGlucoseLevel >= 100 && currentGlucoseLevel <= 125) {
            return new BloodSugarLevel(100, 125, "เสี่ยงต่อการเป็นโรคเบาหวาน", COLOR_PREDIABETES, "😐", "BS02");
        } else {
            return new BloodSugarLevel(126, 999, "เป็นโรคเบาหวาน", COLOR_DIABETES, "😟", "BS03");
        }
    }

    // คลาสสำหรับเก็บข้อมูลระดับน้ำตาลในเลือด
    public static class BloodSugarLevel {
        public final double minLevel;
        public final double maxLevel;
        public final String label;
        public final String color;
        public final String emoji;
        public final String code;

        public BloodSugarLevel(double minLevel, double maxLevel, String label, String color, String emoji, String code) {
            this.minLevel = minLevel;
            this.maxLevel = maxLevel;
            this.label = label;
            this.color = color;
            this.emoji = emoji;
            this.code = code;
        }
    }

    // Method สำหรับ Animation
    public void animateToGlucoseLevel(double targetLevel) {
        setGlucoseLevel(targetLevel);
    }

    // Method สำหรับกำหนดช่วงค่าแบบ Custom
    public void setGlucoseRange(double minLevel, double maxLevel) {
        this.minGlucoseLevel = Math.max(0, minLevel);
        this.maxGlucoseLevel = Math.max(minLevel + 1, maxLevel);
        invalidate();
    }

    // Method สำหรับดึงคำแนะนำตามระดับน้ำตาล
    public String getRecommendation() {
        BloodSugarLevel level = getCurrentBloodSugarLevel();

        switch (level.code) {
            case "BS01":
                return "✅ ระดับน้ำตาลปกติ - รักษาสุขภาพต่อไป";
            case "BS02":
                return "⚠️ เสี่ยงเบาหวาน - ควบคุมอาหาร ออกกำลังกาย";
            case "BS03":
                return "🚨 เป็นเบาหวาน - ควรพบแพทย์เพื่อรักษา";
            default:
                return "ตรวจสอบระดับน้ำตาลในเลือด";
        }
    }

    // Method สำหรับแสดงหน่วย
    public String getUnit() {
        return "มก/ดล";
    }

    // Method สำหรับรีเซ็ต Gauge
    public void resetGauge() {
        this.currentGlucoseLevel = 0.0;
        invalidate();
    }
}