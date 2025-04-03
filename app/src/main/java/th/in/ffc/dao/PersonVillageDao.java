package th.in.ffc.dao;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import th.in.ffc.model.VillageSummary;
import th.in.ffc.provider.HouseProvider.Village;
import th.in.ffc.provider.PersonProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for accessing person data summarized by village
 */
public class PersonVillageDao {

    private Context context;

    public PersonVillageDao(Context context) {
        this.context = context;
    }

    /**
     * Get summary of person count grouped by village
     *
     * @return List of VillageSummary objects
     */
    public List<VillageSummary> getPersonCountByVillage() {
        List<VillageSummary> result = new ArrayList<>();

        Uri uri = Uri.parse("content://" + PersonProvider.AUTHORITY + "/person/summary/village");
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,  // ต้องการทุก column
                null,  // ไม่ต้องการ filter
                null,  // ไม่มี selection args
                null   // ใช้การเรียงลำดับเริ่มต้น
        );

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    String villcode = cursor.getString(cursor.getColumnIndex(Village.VILLCODE));
                    String villno = cursor.getString(cursor.getColumnIndex(Village.VILLNO));
                    String villname = cursor.getString(cursor.getColumnIndex(Village.VILLNAME));
                    int personCount = cursor.getInt(cursor.getColumnIndex("person_count"));

                    VillageSummary summary = new VillageSummary(villcode, villno, villname, personCount);
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
     * @return VillageSummary object or null if not found
     */
    public VillageSummary getPersonCountForVillage(String villageCode) {
        VillageSummary result = null;

        Uri uri = Uri.parse("content://" + PersonProvider.AUTHORITY + "/person/summary/village");
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                "village.villcode = ?",  // filter by village code
                new String[] { villageCode },
                null
        );

        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    String villcode = cursor.getString(cursor.getColumnIndex(Village.VILLCODE));
                    String villno = cursor.getString(cursor.getColumnIndex(Village.VILLNO));
                    String villname = cursor.getString(cursor.getColumnIndex(Village.VILLNAME));
                    int personCount = cursor.getInt(cursor.getColumnIndex("person_count"));

                    result = new VillageSummary(villcode, villno, villname, personCount);
                }
            } finally {
                cursor.close();
            }
        }

        return result;
    }
}