package th.in.ffc.app.form.screening.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class SubstanceItem implements Parcelable {
    private String id;
    private String name;
    private String description;
    private Boolean hasUsed;
    private String otherDrugs; // เพิ่มฟิลด์ใหม่
    private int frequency;
    private int concern;

    public SubstanceItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hasUsed = null;
        this.frequency = 0;
        this.concern = 0;
        this.otherDrugs = "";
    }

    public SubstanceItem(Parcel in) {
        id = in.readString();
        name = in.readString();
        description = in.readString();
        hasUsed = in.readByte() != 0;
        otherDrugs = in.readString();
        frequency = in.readInt(); // เพิ่มบรรทัดนี้
    }



    // Getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Boolean isHasUsed() { return hasUsed; }
    public void setHasUsed(Boolean hasUsed) { this.hasUsed = hasUsed; }
    public String getOtherDrugs() { return otherDrugs; }
    public void setOtherDrugs(String otherDrugs) { this.otherDrugs = otherDrugs; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeByte((byte) (hasUsed ? 1 : 0));
        dest.writeString(otherDrugs);
        dest.writeInt(frequency); // เพิ่มบรรทัดนี้
    }
    public static final Creator<SubstanceItem> CREATOR = new Creator<SubstanceItem>() {
        @Override
        public SubstanceItem createFromParcel(Parcel in) {
            return new SubstanceItem(in);
        }

        @Override
        public SubstanceItem[] newArray(int size) {
            return new SubstanceItem[size];
        }
    };

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequence) {
        this.frequency = frequence;
    }

    public int getConcern() {
        return concern;
    }

    public void setConcern(int concern) {
        this.concern = concern;
    }
}