package th.in.ffc.model;

/**
 * Model class for storing person and sf_person comparison by village
 */
public class VillagePersonComparison {
    private String villageCode;
    private String villageNo;
    private String villageName;
    private int personCount; // จำนวนคนในตาราง person
    private int sfPersonCount; // จำนวนคนในตาราง ffc_sf_person_info
    private double percentage; // เปอร์เซ็นต์ความครอบคลุม (sfPersonCount / personCount * 100)

    public VillagePersonComparison() {
    }

    public VillagePersonComparison(String villageCode, String villageNo, String villageName,
                                   int personCount, int sfPersonCount) {
        this.villageCode = villageCode;
        this.villageNo = villageNo;
        this.villageName = villageName;
        this.personCount = personCount;
        this.sfPersonCount = sfPersonCount;
        calculatePercentage();
    }

    // คำนวณเปอร์เซ็นต์ความครอบคลุม
    private void calculatePercentage() {
        if (personCount > 0) {
            this.percentage = ((double) sfPersonCount / personCount) * 100;
        } else {
            this.percentage = 0.0;
        }
    }

    public String getVillageCode() {
        return villageCode;
    }

    public void setVillageCode(String villageCode) {
        this.villageCode = villageCode;
    }

    public String getVillageNo() {
        return villageNo;
    }

    public void setVillageNo(String villageNo) {
        this.villageNo = villageNo;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    public int getPersonCount() {
        return personCount;
    }

    public void setPersonCount(int personCount) {
        this.personCount = personCount;
        calculatePercentage();
    }

    public int getSfPersonCount() {
        return sfPersonCount;
    }

    public void setSfPersonCount(int sfPersonCount) {
        this.sfPersonCount = sfPersonCount;
        calculatePercentage();
    }

    public double getPercentage() {
        return percentage;
    }

    @Override
    public String toString() {
        return "หมู่บ้าน: " + villageName + " (หมู่ " + villageNo + ")\n" +
                "- ประชากรทั้งหมด: " + personCount + " คน\n" +
                "- ผู้รับบริการ: " + sfPersonCount + " คน\n" +
                "- คิดเป็น: " + String.format("%.2f", percentage) + "%";
    }
}