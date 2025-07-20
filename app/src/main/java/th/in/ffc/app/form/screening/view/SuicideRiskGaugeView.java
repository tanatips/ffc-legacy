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

public class SuicideRiskGaugeView extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private int currentScore = 0;
    private int maxScore = 52; // คะแนนสูงสุด

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม
    private static final float MAX_NEEDLE_ANGLE = SWEEP_ANGLE; // มุมสูงสุดของเข็ม

    // สีสำหรับแต่ละระดับความเสี่ยง
    private static final String COLOR_NO_RISK = "#27AE60";      // เขียว - ไม่มีความเสี่ยง
    private static final String COLOR_LOW_RISK = "#F39C12";     // เหลือง - ความเสี่ยงต่ำ
    private static final String COLOR_MEDIUM_RISK = "#E67E22";  // ส้ม - ความเสี่ยงปานกลาง
    private static final String COLOR_HIGH_RISK = "#E74C3C";    // แดง - ความเสี่ยงสูง

    public SuicideRiskGaugeView(Context context) {
        super(context);
        init();
    }

    public SuicideRiskGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SuicideRiskGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // สร้าง Paint objects
        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(35f); // เพิ่มจาก 20f เป็น 35f (หนาขึ้น 75%)
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
        backgroundPaint.setStrokeWidth(35f); // เพิ่มจาก 20f เป็น 35f (ให้เท่ากับ arcPaint)
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
//        drawLabels(canvas);
        drawNeedle(canvas);
        drawCenterCircle(canvas);
    }

    private void drawGaugeBackground(Canvas canvas) {
        // วาดพื้นหลังของ Gauge
        canvas.drawArc(arcRect, START_ANGLE, SWEEP_ANGLE, false, backgroundPaint);
    }

    private void drawGaugeArcs(Canvas canvas) {
        // คำนวณมุมสำหรับแต่ละระดับความเสี่ยง
        float anglePerScore = SWEEP_ANGLE / maxScore;

        // วาด arc สำหรับแต่ละระดับความเสี่ยง
        // 0 คะแนน: ไม่มีความเสี่ยง (เขียว)
        arcPaint.setColor(Color.parseColor(COLOR_NO_RISK));
        canvas.drawArc(arcRect, START_ANGLE, anglePerScore * 1, false, arcPaint);

        // 1-8 คะแนน: ความเสี่ยงต่ำ (เหลือง)
        arcPaint.setColor(Color.parseColor(COLOR_LOW_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 1), anglePerScore * 8, false, arcPaint);

        // 9-16 คะแนน: ความเสี่ยงปานกลาง (ส้ม)
        arcPaint.setColor(Color.parseColor(COLOR_MEDIUM_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 9), anglePerScore * 8, false, arcPaint);

        // 17+ คะแนน: ความเสี่ยงสูง (แดง)
        arcPaint.setColor(Color.parseColor(COLOR_HIGH_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 17), anglePerScore * (maxScore - 17), false, arcPaint);
    }

    private void drawLabels(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();
        float radius = arcRect.width() / 2 - 75; // เพิ่มจาก 60 เป็น 75 เพื่อให้ห่างจากแถบที่หนาขึ้น

        textPaint.setTextSize(24f);
        textPaint.setColor(Color.parseColor("#2C3E50"));

        // ป้าย "0" ที่ซ้าย
        float leftAngle = (float)Math.toRadians(START_ANGLE);
        float leftX = centerX + radius * (float)Math.cos(leftAngle);
        float leftY = centerY + radius * (float)Math.sin(leftAngle);
        canvas.drawText("0", leftX, leftY + 10, textPaint);

        // ป้าย "52" ที่ขวา
        float rightAngle = (float)Math.toRadians(START_ANGLE + SWEEP_ANGLE);
        float rightX = centerX + radius * (float)Math.cos(rightAngle);
        float rightY = centerY + radius * (float)Math.sin(rightAngle);
        canvas.drawText("52", rightX, rightY + 10, textPaint);

        // ป้ายระดับความเสี่ยงตรงกลาง
        textPaint.setTextSize(16f);
        RiskLevel currentLevel = getCurrentRiskLevel();

        // แสดงป้ายความเสี่ยงใต้ Gauge - ปรับตำแหน่งให้เหมาะสม
        canvas.drawText(currentLevel.label, centerX, centerY + radius + 70, textPaint); // เพิ่มจาก 60 เป็น 70

        // แสดงคะแนนปัจจุบัน
        textPaint.setTextSize(40f);
        textPaint.setColor(Color.parseColor(currentLevel.color));
        canvas.drawText(String.valueOf(currentScore), centerX, centerY + radius + 100, textPaint); // เพิ่มจาก 90 เป็น 100

    }

    private void drawNeedle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // คำนวณมุมของเข็มตามคะแนน
        float needleAngle = START_ANGLE + (currentScore / (float)maxScore) * SWEEP_ANGLE;
        double radians = Math.toRadians(needleAngle);

        // ความยาวของเข็ม - ปรับให้เหมาะสมกับแถบที่หนาขึ้น
        float needleLength = arcRect.width() / 2 - 50; // เพิ่มจาก 40 เป็น 50
        float needleWidth = 10f; // เพิ่มจาก 8f เป็น 10f (ให้เข็มหนาขึ้นเล็กน้อย)

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

        // เปลี่ยนสีเข็มตามระดับความเสี่ยง
        RiskLevel currentLevel = getCurrentRiskLevel();
        needlePaint.setColor(Color.parseColor(currentLevel.color));

        canvas.drawPath(needlePath, needlePaint);

    }

    private void drawCenterCircle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // วาดวงกลมตรงกลาง - เพิ่มขนาดเล็กน้อย
        canvas.drawCircle(centerX, centerY, 18f, centerPaint); // เพิ่มจาก 15f เป็น 18f

        // วาดวงกลมขาวข้างใน
        centerPaint.setColor(Color.WHITE);
        canvas.drawCircle(centerX, centerY, 10f, centerPaint); // เพิ่มจาก 8f เป็น 10f
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

    public RiskLevel getCurrentRiskLevel() {
        if (currentScore == 0) {
            return new RiskLevel(0, 0, "ไม่มีแนวโน้มฆ่าตัวตายในปัจจุบัน", COLOR_NO_RISK, "😊", "1B0270");
        } else if (currentScore >= 1 && currentScore <= 8) {
            return new RiskLevel(1, 8, "มีแนวโน้มฆ่าตัวตายในปัจจุบัน ระดับต่ำ", COLOR_LOW_RISK, "😐", "1B0271");
        } else if (currentScore >= 9 && currentScore <= 16) {
            return new RiskLevel(9, 16, "มีแนวโน้มฆ่าตัวตายในปัจจุบัน ปานกลาง", COLOR_MEDIUM_RISK, "😟", "1B0272");
        } else {
            return new RiskLevel(17, 52, "มีแนวโน้มฆ่าตัวตายในปัจจุบัน สูง", COLOR_HIGH_RISK, "😰", "1B0273");
        }
    }

    // คลาสสำหรับเก็บข้อมูลระดับความเสี่ยง
    public static class RiskLevel {
        public final int minScore;
        public final int maxScore;
        public final String label;
        public final String color;
        public final String emoji;
        public final String code;

        public RiskLevel(int minScore, int maxScore, String label, String color, String emoji, String code) {
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
        // สามารถเพิ่ม ValueAnimator ได้ที่นี่เพื่อให้เข็มหมุนแบบ smooth
        setScore(targetScore);
    }

    // Method สำหรับกำหนดสีแบบ Custom
    public void setCustomColors(String noRisk, String lowRisk, String mediumRisk, String highRisk) {
        // สามารถปรับแต่งสีได้ถ้าต้องการ
    }
}