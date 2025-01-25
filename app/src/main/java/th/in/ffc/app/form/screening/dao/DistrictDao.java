package th.in.ffc.app.form.screening.dao;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.DistrictInfo;
import th.in.ffc.provider.CodeProvider;

public class DistrictDao {
    private Context mContext;

    public DistrictDao(Context context) {
        this.mContext = context;
    }

    public List<DistrictInfo> getDistrictsByProvinceCode(String provinceCode) {
        List<DistrictInfo> districts = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = CodeProvider.District.CONTENT_URI;
        String[] projection = {
                CodeProvider.District.DISTCODE,
                CodeProvider.District.NAME,
                CodeProvider.District.PROVCODE
        };
        String selection = CodeProvider.District.PROVCODE + " = ?";
        String[] selectionArgs = {provinceCode};

        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DistrictInfo districtInfo = new DistrictInfo();
                districtInfo.setDistCode(cursor.getString(cursor.getColumnIndexOrThrow(CodeProvider.District.DISTCODE)));
                districtInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(CodeProvider.District.NAME)));
                districtInfo.setProvCode(cursor.getString(cursor.getColumnIndexOrThrow(CodeProvider.District.PROVCODE)));
                districts.add(districtInfo);
            }
            cursor.close();
        }
        return districts;
    }
}