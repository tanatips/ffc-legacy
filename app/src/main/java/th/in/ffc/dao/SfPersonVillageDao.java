package th.in.ffc.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import th.in.ffc.model.SfPersonVillageSummary;
import th.in.ffc.provider.HouseProvider.Village;
import th.in.ffc.provider.ScreeningFormProvider;
import th.in.ffc.provider.ScreeningFormProvider.SfPersonInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for accessing sf_person_info data summarized by village
 */
public class SfPersonVillageDao {

    private Context context;

    public SfPersonVillageDao(Context context) {
        this.context = context;
    }

    /**
     * Get summary of sf_person_info count grouped by village
     *
     * @return List of SfPersonVillageSummary objects
     */
    public List<SfPersonVillageSummary> getPersonCountByVillage() {
        List<SfPersonVillageSummary> result = new ArrayList<>();

        // ใช้ Content URI พิเศษที่ระบุว่าต้องการข้อมูลสรุปตามหมู่บ้าน
        Uri summaryUri = Uri.parse("content://" + ScreeningFormProvider.AUTHORITY + "/sf_person_info/summary/village");

        // ไม่ต้องระบุ selection เป็น raw query อีกต่อไป เพราะ provider จะจัดการให้
        Cursor cursor = context.getContentResolver().query(
                summaryUri,
                null,  // ใช้ projection ที่ provider กำหนด
                null,  // ไม่ต้องระบุ selection
                null,  // ไม่ต้องระบุ selection args
                null   // ใช้ default sort order ใน provider
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String villageCode = cursor.getString(cursor.getColumnIndex(Village.VILLCODE));
                    String villageNo = cursor.getString(cursor.getColumnIndex(Village.VILLNO));
                    String villageName = cursor.getString(cursor.getColumnIndex(Village.VILLNAME));
                    int personCount = cursor.getInt(cursor.getColumnIndex("person_count"));

                    SfPersonVillageSummary summary = new SfPersonVillageSummary(villageCode, villageNo, villageName, personCount);
                    result.add(summary);
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }
    /**
     * Get summary of person count for a specific village
     *
     * @param villageCode Village code to filter
     * @return SfPersonVillageSummary object or null if not found
     */
    public SfPersonVillageSummary getPersonCountForVillage(String villageCode) {
        SfPersonVillageSummary result = null;

        // ใช้ Content URI พิเศษที่ระบุว่าต้องการข้อมูลสรุปตามหมู่บ้าน
        Uri summaryUri = Uri.parse("content://" + ScreeningFormProvider.AUTHORITY + "/sf_person_info/summary/village");

        // ใช้ selection ปกติสำหรับการกรอง village code
        Cursor cursor = context.getContentResolver().query(
                summaryUri,
                null,  // ใช้ projection ที่ provider กำหนด
                Village.VILLCODE + "=?",  // selection แบบปกติ
                new String[] { villageCode },  // selection args
                null   // ใช้ default sort order
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String vCode = cursor.getString(cursor.getColumnIndex(Village.VILLCODE));
                    String vNo = cursor.getString(cursor.getColumnIndex(Village.VILLNO));
                    String vName = cursor.getString(cursor.getColumnIndex(Village.VILLNAME));
                    int count = cursor.getInt(cursor.getColumnIndex("person_count"));

                    result = new SfPersonVillageSummary(vCode, vNo, vName, count);
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }
    /**
     * Alternative implementation using SQLiteQueryBuilder approach
     */
    public List<SfPersonVillageSummary> getPersonCountByVillageWithBuilder() {
        List<SfPersonVillageSummary> result = new ArrayList<>();

        // ใช้ ContentResolver query แทน
        Uri.Builder builder = SfPersonInfo.CONTENT_URI.buildUpon();
        builder.appendPath("summary").appendPath("village");
        Uri uri = builder.build();

        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String villageCode = cursor.getString(cursor.getColumnIndex(Village.VILLCODE));
                    String villageNo = cursor.getString(cursor.getColumnIndex(Village.VILLNO));
                    String villageName = cursor.getString(cursor.getColumnIndex(Village.VILLNAME));
                    int personCount = cursor.getInt(cursor.getColumnIndex("person_count"));

                    SfPersonVillageSummary summary = new SfPersonVillageSummary(villageCode, villageNo, villageName, personCount);
                    result.add(summary);
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }
}