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

public class AlcoholRiskGaugeView extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private int currentScore = 0;
    private int maxScore = 40; // คะแนนสูงสุดที่คาดการณ์ไว้สำหรับแอลกอฮอล์

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม
    private static final float MAX_NEEDLE_ANGLE = SWEEP_ANGLE; // มุมสูงสุดของเข็ม

    // สีสำหรับแต่ละระดับความเสี่ยงแอลกอฮอล์
    private static final String COLOR_NO_DRINKING = "#27AE60";     // เขียว - ไม่ดื่ม
    private static final String COLOR_LOW_RISK = "#2ECC71";        // เขียวอ่อน - ไม่ต้องบำบัด
    private static final String COLOR_MEDIUM_RISK = "#F39C12";     // ส้ม - บำบัดอย่างย่อ
    private static final String COLOR_HIGH_RISK = "#E74C3C";       // แดง - บำบัดเข้มข้น

    public AlcoholRiskGaugeView(Context context) {
        super(context);
        init();
    }

    public AlcoholRiskGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AlcoholRiskGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        // คำนวณมุมสำหรับแต่ละระดับความเสี่ยงแอลกอฮอล์
        float anglePerScore = SWEEP_ANGLE / maxScore;

        // วาด arc สำหรับแต่ละระดับความเสี่ยง
        // 0 คะแนน: ไม่ดื่ม (เขียวเข้ม)
        arcPaint.setColor(Color.parseColor(COLOR_NO_DRINKING));
        canvas.drawArc(arcRect, START_ANGLE, anglePerScore * 1, false, arcPaint);

        // 1-10 คะแนน: ไม่ต้องบำบัด (เขียวอ่อน)
        arcPaint.setColor(Color.parseColor(COLOR_LOW_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 1), anglePerScore * 10, false, arcPaint);

        // 11-26 คะแนน: บำบัดอย่างย่อ (ส้ม)
        arcPaint.setColor(Color.parseColor(COLOR_MEDIUM_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 11), anglePerScore * 16, false, arcPaint);

        // 27+ คะแนน: บำบัดเข้มข้น (แดง)
        arcPaint.setColor(Color.parseColor(COLOR_HIGH_RISK));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 27), anglePerScore * (maxScore - 27), false, arcPaint);
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
            return new RiskLevel(0, 0, "ไม่ดื่ม", COLOR_NO_DRINKING, "😊", "1B600");
        } else if (currentScore >= 1 && currentScore <= 10) {
            return new RiskLevel(1, 10, "ไม่ต้องบำบัด (ความเสี่ยงต่ำ)", COLOR_LOW_RISK, "🙂", "1B602");
        } else if (currentScore >= 11 && currentScore <= 26) {
            return new RiskLevel(11, 26, "บำบัดอย่างย่อ (ความเสี่ยงปานกลาง)", COLOR_MEDIUM_RISK, "😟", "1B603");
        } else {
            return new RiskLevel(27, maxScore, "บำบัดเข้มข้น (ความเสี่ยงสูง)", COLOR_HIGH_RISK, "😰", "1B604");
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

    // Method สำหรับ Animation
    public void animateToScore(int targetScore) {
        setScore(targetScore);
    }

    // Method สำหรับกำหนดสีแบบ Custom
    public void setCustomColors(String noDrinking, String lowRisk, String mediumRisk, String highRisk) {
        // สามารถปรับแต่งสีได้ถ้าต้องการ
    }
}