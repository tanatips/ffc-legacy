package th.in.ffc.app.form.screening.model;

import java.io.Serializable;

// ตรวจสอบว่าคลาส AnswerFrequencyData มี Getter และ Setter สำหรับ otherDrugs ที่ถูกต้อง
public class AnswerFrequencyData implements Serializable {
    private int frequency;
    private String otherDrugs;

    public AnswerFrequencyData(int frequency, String otherDrugs) {
        this.frequency = frequency;
        // กันกรณี null
        this.otherDrugs = otherDrugs != null ? otherDrugs : "";
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getOtherDrugs() {
        // กันกรณี null
        return otherDrugs != null ? otherDrugs : "";
    }

    public void setOtherDrugs(String otherDrugs) {
        // กันกรณี null
        this.otherDrugs = otherDrugs != null ? otherDrugs : "";
    }
}
