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
import th.in.ffc.util.Log;

/**
 * Custom Gauge View สำหรับแสดงผลการประเมินความเครียดและซึมเศร้า (ST5)
 * จาก 0-15 คะแนน แบ่งเป็น 4 ระดับ
 * ออกแบบให้เหมือนกับ StressRiskGauge2QView
 */
public class StressRiskGaugeST5View extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private int currentScore = 0;
    private int maxScore = 15; // คะแนนสูงสุด (ST5 = 0-15 คะแนน)

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม
    private static final float MAX_NEEDLE_ANGLE = SWEEP_ANGLE; // มุมสูงสุดของเข็ม

    // สีสำหรับแต่ละระดับความเครียด (ST5 มี 4 ระดับ)
    private static final String COLOR_NORMAL = "#27AE60";      // เขียว - เครียดน้อย (0-4)
    private static final String COLOR_MILD = "#F39C12";       // เหลือง - เครียดปานกลาง (5-7)
    private static final String COLOR_MODERATE = "#E67E22";   // ส้ม - เครียดมาก (8-9)
    private static final String COLOR_SEVERE = "#E74C3C";     // แดง - เครียดมากที่สุด (10-15)

    public StressRiskGaugeST5View(Context context) {
        super(context);
        init();
    }

    public StressRiskGaugeST5View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StressRiskGaugeST5View(Context context, AttributeSet attrs, int defStyleAttr) {
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

        // คำนวณขนาดและตำแหน่งของ Gauge (เหมือน 2QView)
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
        // วาดพื้นหลังของ Gauge (เหมือน 2QView)
        canvas.drawArc(arcRect, START_ANGLE, SWEEP_ANGLE, false, backgroundPaint);
    }

    private void drawGaugeArcs(Canvas canvas) {
        // คำนวณมุมสำหรับแต่ละระดับตามสัดส่วนจริงของ ST5
        // 0-4 = 5 คะแนน, 5-7 = 3 คะแนน, 8-9 = 2 คะแนน, 10-15 = 6 คะแนน
        // รวม 16 คะแนน (0-15)

        float totalScores = 16f; // 0-15 = 16 ค่า
        float angle1 = SWEEP_ANGLE * (5f / totalScores);  // เครียดน้อย (5/16)
        float angle2 = SWEEP_ANGLE * (3f / totalScores);  // เครียดปานกลาง (3/16)
        float angle3 = SWEEP_ANGLE * (2f / totalScores);  // เครียดมาก (2/16)
        float angle4 = SWEEP_ANGLE * (6f / totalScores);  // เครียดมากที่สุด (6/16)

        float currentAngle = START_ANGLE;

        // วาด arc สำหรับแต่ละระดับ
        // เครียดน้อย (0-4 คะแนน): เขียว
        arcPaint.setColor(Color.parseColor(COLOR_NORMAL));
        canvas.drawArc(arcRect, currentAngle, angle1, false, arcPaint);
        currentAngle += angle1;

        // เครียดปานกลาง (5-7 คะแนน): เหลือง
        arcPaint.setColor(Color.parseColor(COLOR_MILD));
        canvas.drawArc(arcRect, currentAngle, angle2, false, arcPaint);
        currentAngle += angle2;

        // เครียดมาก (8-9 คะแนน): ส้ม
        arcPaint.setColor(Color.parseColor(COLOR_MODERATE));
        canvas.drawArc(arcRect, currentAngle, angle3, false, arcPaint);
        currentAngle += angle3;

        // เครียดมากที่สุด (10-15 คะแนน): แดง
        arcPaint.setColor(Color.parseColor(COLOR_SEVERE));
        canvas.drawArc(arcRect, currentAngle, angle4, false, arcPaint);
    }

    private void drawNeedle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // คำนวณมุมของเข็มตามคะแนน (เหมือน 2QView)
        float needleAngle = START_ANGLE + (currentScore / (float)maxScore) * SWEEP_ANGLE;
        double radians = Math.toRadians(needleAngle);

        // ความยาวของเข็ม
        float needleLength = arcRect.width() / 2 - 50;
        float needleWidth = 10f;

        // คำนวณตำแหน่งปลายเข็ม
        float needleEndX = centerX + needleLength * (float)Math.cos(radians);
        float needleEndY = centerY + needleLength * (float)Math.sin(radians);

        // สร้างรูปร่างของเข็ม (เหมือน 2QView)
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
        StressLevel currentLevel = getCurrentStressLevel();
        needlePaint.setColor(Color.parseColor(currentLevel.color));

        canvas.drawPath(needlePath, needlePaint);
    }

    private void drawCenterCircle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // วาดวงกลมตรงกลาง (เหมือน 2QView)
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

        Log.d("StressRiskGaugeST5", "Score updated to: " + this.currentScore +
                ", Level: " + getCurrentStressLevel().label);
    }

    public int getScore() {
        return currentScore;
    }

    public StressLevel getCurrentStressLevel() {
        if (currentScore >= 0 && currentScore <= 4) {
            return new StressLevel(0, 4, "เครียดน้อย", COLOR_NORMAL, "😊", "1B132");
        } else if (currentScore >= 5 && currentScore <= 7) {
            return new StressLevel(5, 7, "เครียดปานกลาง", COLOR_MILD, "😐", "1B133");
        } else if (currentScore >= 8 && currentScore <= 9) {
            return new StressLevel(8, 9, "เครียดมาก", COLOR_MODERATE, "😟", "1B134");
        } else if (currentScore >= 10 && currentScore <= 15) {
            return new StressLevel(10, 15, "เครียดมากที่สุด", COLOR_SEVERE, "😰", "1B134");
        } else {
            return new StressLevel(0, 0, "ยังไม่ได้ประเมิน", "#9E9E9E", "🤔", "");
        }
    }

    /**
     * คลาสสำหรับเก็บข้อมูลระดับความเครียด
     */
    public static class StressLevel {
        public final int minScore;
        public final int maxScore;
        public final String label;
        public final String color;
        public final String emoji;
        public final String code;

        public StressLevel(int minScore, int maxScore, String label, String color, String emoji, String code) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.label = label;
            this.color = color;
            this.emoji = emoji;
            this.code = code;
        }

        @Override
        public String toString() {
            return String.format("%s %s (%d-%d คะแนน)", emoji, label, minScore, maxScore);
        }
    }

    /**
     * ดึงระดับความเครียดจากคะแนนโดยไม่ต้องสร้าง instance
     */
    public static StressLevel getStressLevelByScore(int score) {
        if (score >= 0 && score <= 4) {
            return new StressLevel(0, 4, "เครียดน้อย", COLOR_NORMAL, "😊", "1B132");
        } else if (score >= 5 && score <= 7) {
            return new StressLevel(5, 7, "เครียดปานกลาง", COLOR_MILD, "😐", "1B133");
        } else if (score >= 8 && score <= 9) {
            return new StressLevel(8, 9, "เครียดมาก", COLOR_MODERATE, "😟", "1B134");
        } else if (score >= 10 && score <= 15) {
            return new StressLevel(10, 15, "เครียดมากที่สุด", COLOR_SEVERE, "😰", "1B134");
        } else {
            return new StressLevel(0, 0, "ยังไม่ได้ประเมิน", "#9E9E9E", "🤔", "");
        }
    }

    /**
     * ตรวจสอบว่าเป็นระดับเสี่ยงสูงหรือไม่
     */
    public boolean isHighRisk() {
        return currentScore >= 8; // เครียดมาก (8-9) และเครียดมากที่สุด (10-15)
    }

    /**
     * ดึงคำแนะนำตามระดับความเครียด
     */
    public String getRecommendation() {
        if (currentScore >= 0 && currentScore <= 4) {
            return "ระดับความเครียดของคุณอยู่ในเกณฑ์ปกติ ควรรักษาสุขภาพจิตที่ดีต่อไป";
        } else if (currentScore >= 5 && currentScore <= 7) {
            return "คุณมีความเครียดระดับปานกลาง ควรหาวิธีผ่อนคลายความเครียด เช่น ออกกำลังกาย ทำสมาธิ หรือทำกิจกรรมที่ชื่นชอบ";
        } else if (currentScore >= 8 && currentScore <= 9) {
            return "คุณมีความเครียดระดับมาก ควรปรึกษาผู้เชี่ยวชาญด้านสุขภาพจิตเพื่อรับคำแนะนำที่เหมาะสม";
        } else if (currentScore >= 10) {
            return "คุณมีความเครียดระดับมากที่สุด ควรพบแพทย์หรือผู้เชี่ยวชาญด้านสุขภาพจิตโดยเร็วที่สุด";
        }
        return "ไม่สามารถประเมินได้";
    }

    /**
     * ดึงคำแนะนำพร้อม emoji
     */
    public String getRecommendationWithEmoji() {
        String emoji = getCurrentStressLevel().emoji;
        return emoji + " " + getRecommendation();
    }

    /**
     * Animation สำหรับการเปลี่ยนคะแนน (ถ้าต้องการ)
     */
    public void animateToScore(int targetScore) {
        setScore(targetScore);
    }

    /**
     * Method สำหรับกำหนดสีแบบ Custom
     */
    public void setCustomColors(String normal, String mild, String moderate, String severe) {
        // สามารถปรับแต่งสีได้ถ้าต้องการ
        // แต่ต้องมีการ invalidate() เพื่อวาดใหม่
    }

    /**
     * ดึงเปอร์เซ็นต์ความเสี่ยงปัจจุบัน
     */
    public float getRiskPercentage() {
        return (currentScore / (float)maxScore) * 100f;
    }

    /**
     * ดึงข้อมูลสถิติคะแนน
     */
    public String getScoreStatistics() {
        float percentage = getRiskPercentage();
        String riskLevel = isHighRisk() ? "สูง" : "ต่ำ";

        return String.format("คะแนน: %d/%d (%.1f%%) - ความเสี่ยง: %s",
                currentScore, maxScore, percentage, riskLevel);
    }

    /**
     * ตรวจสอบว่าต้องการการดูแลเร่งด่วนหรือไม่
     */
    public boolean needsUrgentCare() {
        return currentScore >= 10; // เครียดมากที่สุด
    }

    /**
     * ดึงข้อความแจ้งเตือนตามระดับความเสี่ยง
     */
    public String getAlertMessage() {
        if (needsUrgentCare()) {
            return "⚠️ ต้องการการดูแลเร่งด่วน: ควรพบแพทย์โดยเร็วที่สุด";
        } else if (isHighRisk()) {
            return "⚠️ ความเสี่ยงสูง: ควรปรึกษาผู้เชี่ยวชาญ";
        } else if (currentScore >= 5) {
            return "ℹ️ ควรติดตาม: มีความเครียดปานกลาง";
        } else {
            return "✅ ปกติ: ควรรักษาสุขภาพจิตให้ดีต่อไป";
        }
    }
}