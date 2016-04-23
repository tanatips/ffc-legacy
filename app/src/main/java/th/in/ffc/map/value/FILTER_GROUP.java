package th.in.ffc.map.value;

public enum FILTER_GROUP {
    CHRONIC("chronic", "SELECT DISTINCT groupcode,groupname FROM cdiseasechronic"), DISEASE("disease",
            "SELECT DISTINCT group506code,group506name FROM cdisease506"), VOLA("vola",
            "SELECT DISTINCT p1.pid,p1.fname FROM person p1,persontype p2 where p1.pid = p2.pid and p2.typecode = '09'");

    private FILTER_GROUP(String str, String query) {
        this.NAME_ID = str;
        this.QUERY_STRING = query;
    }

    public String getName() {
        return NAME_ID;
    }

    public String getSubCategory() {
        return NAME_ID + "_subcat";
    }

    public String getSubCategoryAllCheck() {
        return getSubCategory() + "_all";
    }

    public String getCheckBoxName() {
        return NAME_ID + "_checkbox";
    }

    public String getQueryString() {
        return QUERY_STRING;
    }

    public String getPreferenceName() {
        return name() + "_preference";
    }

    private String NAME_ID;
    private String QUERY_STRING;
}
