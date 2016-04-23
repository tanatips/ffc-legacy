package th.in.ffc.map.value;

import org.osmdroid.util.GeoPoint;

public class FinalValue {

    public static final String STRING_RED = "red";
    public static final String STRING_GREEN = "green";

    public static final String STRING_CREATE_HOUSE_MARKER_TITLE = "Create house marker.";
    public static final String STRING_EDIT_HOUSE_MARKER_TITLE = "Edit House Marker.";
    public static final String STRING_SEARCH_HOUSE_MARKER_TITLE = "Search House Marker.";

    public static final String STRING_TYPE_CREATE = "create";
    public static final String STRING_TYPE_EDIT = "edit";

    public static final String STRING_OPTION_TITLE = "Option";

    public static final int INT_MIN_TIME = 2000;
    public static final int INT_MIN_DISTANCE = 3;

    public static final String STRING_All_HOUSE_MARKER_HAS_BEEN_CREATED_TITLE = "Cannot create house marker.";
    public static final String STRING_All_HOUSE_MARKER_HAS_BEEN_CREATED_MESSAGE = "คุณได้สร้าง House marker ครบสมบูรณ์แล้ว";

    public static final String STRING_MANAGEDOVERLAY_EMPTY_NAME = "overlay empty";
    public static final String STRING_MANAGEDOVERLAY_HOUSE_RED_NAME = "overlay house red";
    public static final String STRING_MANAGEDOVERLAY_HOUSE_GREEN_NAME = "overlay house green";
    public static final String STRING_MANAGEDOVERLAY_HOUSE_RED_SPECIAL_NAME = "overlay house red special";
    public static final String STRING_MANAGEDOVERLAY_HOUSE_GREEN_SPECIAL_NAME = "overlay house green special";

    public static final String STRING_MANAGEDOVERLAY_USER = "overlay user";

    public static final String STRING_MANMANAGEDOVERLAY_NAME = "man overlay blue";

    private static final int INT_LATITUDE_VICTORY = (int) (13.764934 * Math.pow(10, 6));
    private static final int INT_LONGITUDE_VICTORY = (int) (100.538275 * Math.pow(10, 6));
    public static final GeoPoint GEOPOINT_VICTORY = new GeoPoint(INT_LATITUDE_VICTORY, INT_LONGITUDE_VICTORY);

    private static final int INT_LATITUDE = (int) (-84.719008 * Math.pow(10, 6));
    private static final int INT_LONGITUDE = (int) (-176.601577 * Math.pow(10, 6));
    public static final GeoPoint GEOPOINT_EMPTY = new GeoPoint(INT_LATITUDE, INT_LONGITUDE);

    private static final int INT_LATITUDE_DEFAULT = (int) (13.724534 * Math.pow(10, 6));
    private static final int INT_LONGITUDE_DEFAULT = (int) (100.559001 * Math.pow(10, 6));
    public static final GeoPoint GEOPOINT_DEFAULT = new GeoPoint(INT_LATITUDE_DEFAULT, INT_LONGITUDE_DEFAULT);

    public static final int INT_DIALOG_TYPE_CREATE = 1;
    public static final int INT_DIALOG_TYPE_EDIT = 2;

    public static final String STRING_DIALOG_MAPSTYLE_TITLE = "Map Style";
    public static final int INT_SATELLITE = 0;
    public static final int INT_SATELLITE_OVERLAY = 1;
    public static final int INT_GOOGLE_MAPS = 2;

    public static final int INT_REQUEST_NEW_INFO = 0x10;
    public static final int INT_REQUEST_NEW_VILLAGE = 0x11;


    public static final int FILTER_REQUEST = 0xAA;
}
