package th.in.ffc.map.value;

import android.app.Activity;

public enum MISC_ENUM {
    VILLAGE("village", "villcode", null);

    @SuppressWarnings("unchecked")
    MISC_ENUM(String arg0, String arg1, Class<?> arg2) {
        this.DB_NAME = arg0;
        this.DB_COLUMN = arg1;
        this.INCREASE_CLASS = (Class<Activity>) arg2;
    }

    public String getDBTableName() {
        return this.DB_NAME;
    }

    public String getColumnName() {
        return DB_COLUMN;
    }

    public Class<Activity> getIncreaseClass() {
        return INCREASE_CLASS;
    }

    private final String DB_NAME;
    private final String DB_COLUMN;
    private final Class<Activity> INCREASE_CLASS;
}
