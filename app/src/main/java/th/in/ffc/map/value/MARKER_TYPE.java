package th.in.ffc.map.value;

import android.app.Activity;
import th.in.ffc.R;
import th.in.ffc.map.ui.infoUI.*;

public enum MARKER_TYPE {
    HOUSE("house", R.drawable.house_green, "HNo", IncreaseHouse.class, R.string.house),
    TEMPLE("villagetemple", R.drawable.temple, "Name", IncreaseTemple.class, R.string.temple_create),
    WATER("villagewater", R.drawable.drinkingwater, null, IncreaseWater.class, R.string.water_create),
    SCHOOL("villageschool", R.drawable.school, "Name", IncreaseSchool.class, R.string.school_create),
    BUSINESS("villagebusiness", R.drawable.shop, "Name", IncreaseShop.class, R.string.shop_create),
    POI("ffc_poi", R.drawable.poi, "Name", IncreasePOI.class, R.string.poi_create),
    HOSPITAL("ffc_hospital", R.drawable.hospital, "Name", IncreaseHospital.class, R.string.hospital_create);

    @SuppressWarnings("unchecked")
    MARKER_TYPE(String arg0, int arg1, String arg2, Class<?> arg4, int arg5) {
        this.DB_NAME = arg0;
        this.MARKER_IMAGE = arg1;
        this.REPRESENT = arg2;
        this.INCREASE_CLASS = (Class<Activity>) arg4;
        this.NAME_ID = arg5;
    }

    public String getDBTableName() {
        return this.DB_NAME;
    }

    public int getDrawableID() {
        return this.MARKER_IMAGE;
    }

    public String getSpinnerText() {
        return this.REPRESENT;
    }

    public Class<Activity> getIncreaseClass() {
        return INCREASE_CLASS;
    }

    public String getColumnName() {
        if (this == HOUSE)
            return "hcode";

        return this.name().toLowerCase() + "no";
    }

    public int getNameID() {
        return NAME_ID;
    }

    private final String DB_NAME;
    private final String REPRESENT;
    private final int MARKER_IMAGE;
    private final Class<Activity> INCREASE_CLASS;
    private final int NAME_ID;
    public static final int size = 7; // Doesn't count VILLAGE as a Marker
}
