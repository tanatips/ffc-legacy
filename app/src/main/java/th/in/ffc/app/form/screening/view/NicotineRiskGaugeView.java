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

public class NicotineRiskGaugeView extends View {

    private Paint arcPaint;
    private Paint needlePaint;
    private Paint centerPaint;
    private Paint textPaint;
    private Paint backgroundPaint;

    private RectF arcRect;
    private Path needlePath;

    private int currentFagerstromScore = 0; // คะแนน Fagerstrom (ข้อ 1)
    private int currentNicotineScore = 0;   // คะแนน Nicotine (ข้อ 2)
    private int maxFagerstromScore = 10;    // คะแนนสูงสุดของ Fagerstrom Test
    private int maxNicotineScore = 40;      // คะแนนสูงสุดของ Nicotine (สมมติ)

    // มุมเริ่มต้นและมุมสิ้นสุดของ Gauge (180 degrees = ครึ่งวงกลม)
    private static final float START_ANGLE = 180f; // เริ่มจากซ้าย
    private static final float SWEEP_ANGLE = 180f; // ครึ่งวงกลม
    private static final float MAX_NEEDLE_ANGLE = SWEEP_ANGLE; // มุมสูงสุดของเข็ม

    // สีสำหรับแต่ละระดับการติดนิโคติน
    private static final String COLOR_NO_ADDICTION = "#27AE60";      // เขียว - ไม่ติด (0-3)
    private static final String COLOR_LOW_ADDICTION = "#F1C40F";     // เหลือง - ติดเล็กน้อย (4-5)
    private static final String COLOR_MEDIUM_ADDICTION = "#FF9800";  // ส้ม - ติดปานกลาง (6-7)
    private static final String COLOR_HIGH_ADDICTION = "#E74C3C";    // แดง - ติดสูง (8-9)
    private static final String COLOR_VERY_HIGH_ADDICTION = "#C0392B"; // แดงเข้ม - ติดสูงมาก (10)

    public NicotineRiskGaugeView(Context context) {
        super(context);
        init();
    }

    public NicotineRiskGaugeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NicotineRiskGaugeView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        // คำนวณมุมสำหรับแต่ละระดับการติดนิโคติน (ใช้ Fagerstrom Score)
        float anglePerScore = SWEEP_ANGLE / maxFagerstromScore;

        // วาด arc สำหรับแต่ละระดับการติดนิโคติน
        // 0-3 คะแนน: ไม่ติด (เขียว)
        arcPaint.setColor(Color.parseColor(COLOR_NO_ADDICTION));
        canvas.drawArc(arcRect, START_ANGLE, anglePerScore * 4, false, arcPaint);

        // 4-5 คะแนน: ติดเล็กน้อย (เหลือง)
        arcPaint.setColor(Color.parseColor(COLOR_LOW_ADDICTION));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 4), anglePerScore * 2, false, arcPaint);

        // 6-7 คะแนน: ติดปานกลาง (ส้ม)
        arcPaint.setColor(Color.parseColor(COLOR_MEDIUM_ADDICTION));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 6), anglePerScore * 2, false, arcPaint);

        // 8-9 คะแนน: ติดสูง (แดง)
        arcPaint.setColor(Color.parseColor(COLOR_HIGH_ADDICTION));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 8), anglePerScore * 2, false, arcPaint);

        // 10 คะแนน: ติดสูงมาก (แดงเข้ม) - เพิ่มพื้นที่เล็กน้อยสำหรับ visibility
        arcPaint.setColor(Color.parseColor(COLOR_VERY_HIGH_ADDICTION));
        canvas.drawArc(arcRect, START_ANGLE + (anglePerScore * 10), anglePerScore * 0.5f, false, arcPaint);
    }

    private void drawNeedle(Canvas canvas) {
        float centerX = arcRect.centerX();
        float centerY = arcRect.centerY();

        // คำนวณมุมของเข็มตามคะแนน Fagerstrom
        float needleAngle = START_ANGLE + (currentFagerstromScore / (float)maxFagerstromScore) * SWEEP_ANGLE;
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

        // เปลี่ยนสีเข็มตามระดับการติดนิโคติน
        AddictionLevel currentLevel = getCurrentAddictionLevel();
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

    public void setFagerstromScore(int score) {
        // จำกัดค่าคะแนนให้อยู่ในช่วงที่กำหนด
        this.currentFagerstromScore = Math.max(0, Math.min(score, maxFagerstromScore));
        invalidate(); // ขอให้วาดใหม่
    }

    public void setNicotineScore(int score) {
        // จำกัดค่าคะแนนให้อยู่ในช่วงที่กำหนด
        this.currentNicotineScore = Math.max(0, Math.min(score, maxNicotineScore));
        invalidate(); // ขอให้วาดใหม่
    }

    public void setScores(int fagerstromScore, int nicotineScore) {
        this.currentFagerstromScore = Math.max(0, Math.min(fagerstromScore, maxFagerstromScore));
        this.currentNicotineScore = Math.max(0, Math.min(nicotineScore, maxNicotineScore));
        invalidate(); // ขอให้วาดใหม่
    }

    // เก็บเมธอดเก่าไว้เผื่อ backward compatibility
    public void setScore(int score) {
        setFagerstromScore(score);
    }

    public int getFagerstromScore() {
        return currentFagerstromScore;
    }

    public int getNicotineScore() {
        return currentNicotineScore;
    }

    // เก็บเมธอดเก่าไว้เผื่อ backward compatibility
    public int getScore() {
        return currentFagerstromScore;
    }

    public AddictionLevel getCurrentAddictionLevel() {
        // ใช้คะแนน Fagerstrom สำหรับการประเมิน
        if (currentFagerstromScore >= 0 && currentFagerstromScore <= 3) {
            return new AddictionLevel(0, 3, "ไม่นับว่าคุณติดสารนิโคติน", COLOR_NO_ADDICTION, "😊", "1B500");
        } else if (currentFagerstromScore >= 4 && currentFagerstromScore <= 5) {
            return new AddictionLevel(4, 5, "คุณติดสารนิโคตินในระดับปานกลาง", COLOR_LOW_ADDICTION, "🙂", "1B501");
        } else if (currentFagerstromScore >= 6 && currentFagerstromScore <= 7) {
            return new AddictionLevel(6, 7, "คุณติดสารนิโคตินในระดับปานกลางและมีแนวโน้มอย่างมากในการพัฒนาไปเป็นการติดนิโคตินระดับสูง", COLOR_MEDIUM_ADDICTION, "😟", "1B502");
        } else if (currentFagerstromScore >= 8 && currentFagerstromScore <= 9) {
            return new AddictionLevel(8, 9, "คุณติดสารนิโคตินในระดับสูง", COLOR_HIGH_ADDICTION, "😰", "1B503");
        } else {
            return new AddictionLevel(10, 10, "คุณติดสารนิโคตินในระดับสูงมาก", COLOR_VERY_HIGH_ADDICTION, "😱", "1B504");
        }
    }

    // คลาสสำหรับเก็บข้อมูลระดับการติดนิโคติน
    public static class AddictionLevel {
        public final int minScore;
        public final int maxScore;
        public final String label;
        public final String color;
        public final String emoji;
        public final String code;

        public AddictionLevel(int minScore, int maxScore, String label, String color, String emoji, String code) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.label = label;
            this.color = color;
            this.emoji = emoji;
            this.code = code;
        }
    }

    // Method สำหรับ Animation
    public void animateToScore(int fagerstromScore, int nicotineScore) {
        setScores(fagerstromScore, nicotineScore);
    }

    // เก็บเมธอดเก่าไว้เผื่อ backward compatibility
    public void animateToScore(int targetScore) {
        setFagerstromScore(targetScore);
    }

    // Method สำหรับกำหนดสีแบบ Custom
    public void setCustomColors(String noAddiction, String lowAddiction, String mediumAddiction, String highAddiction, String veryHighAddiction) {
        // สามารถปรับแต่งสีได้ถ้าต้องการ
    }
}