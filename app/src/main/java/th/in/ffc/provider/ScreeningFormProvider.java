package th.in.ffc.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ScreeningFormProvider extends ContentProvider {
    public static String AUTHORITY = "th.in.ffc.provider.ScreeningFormProvider";
    private static final int id = 1;
    private static final int idcard = 2;
    private static final int fname = 3;
    private static final int lname = 4;
    private static final int birthday = 5;
    private static final int gender = 6;
    private static final int phone = 7;
    private static final int authen_date = 8;
    private static final int authen_no = 9;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    public static final class ScreeningForm implements BaseColumns {
        public static final String TABLENAME = "screeningform";

        public static final Uri CONTENT_URI = Uri.parse("content://"
                + UserProvider.AUTHORITY + "/screeningform");
        public static final String CONTENT_DIR_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/vnd.ffc.screeningform";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
                + "/vnd.ffc.screeningform";

        public static final String ID = "id";
        public static final String IDCARD = "idcard";
        public static final String FNAME = "fname";
        public static final String LNAME = "lname";

        public static final String BIRTHDAY = "birthday";

        public static final String GENDER = "gender";

        public static final String PHONE = "phone";
        public static final String HN = "hn";

        public static final String AUTHEN_DATE = "authen_date";

        public static final String WEIGHT = "weight";

        public static final String HEIGHT = "height";
        public static final String WAIST_SIZE = "waist_size";
        public static final String BP = "bp";
        public static final String SYSTOLIC_PRESSURE = "systolic_pressure";
        public static final String DIASTOLIC_PRESSURE = "diastolic_pressure";

        public static final String SMOOKING = "smooking"; // 1B52, 1B51, 1B50
        public static final String SMOOKING_FREQUENCY = "smooking_frequency"; // 1 = สูบนานๆ ครั้ง, 2 = สูบเป็นครั้งคราว 3 = สูบเป็นประจำ
        public static final String SMOMOKIN_ADVICE = "smooking_advice";
        // 1B530 (ฺฺBrief Advice)
        // 1B531 (Counseling Advice)
        // !B532 (Conseling Advice + MedicineX
        public static final String DRINKING = "drinking";
        public static final String DRINKING_FREQUENCY = "drinking_frequency";

        public static final String DRINKING_POINT = "drinking_point";
        public static final String RISK_LEVEL = "risk_level";
        public static final String DRINKING_ADVICE = "drinking_advice";
    }
}

