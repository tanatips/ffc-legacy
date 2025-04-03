package th.in.ffc.model;

/**
 * Model class for storing village summary data
 */
public class VillageSummary {
    private String villageCode;
    private String villageNo;
    private String villageName;
    private int personCount;

    public VillageSummary() {
    }

    public VillageSummary(String villageCode, String villageNo, String villageName, int personCount) {
        this.villageCode = villageCode;
        this.villageNo = villageNo;
        this.villageName = villageName;
        this.personCount = personCount;
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
    }

    @Override
    public String toString() {
        return "หมู่บ้าน: " + villageName + " (หมู่ " + villageNo + ") มีประชากร " + personCount + " คน";
    }
}