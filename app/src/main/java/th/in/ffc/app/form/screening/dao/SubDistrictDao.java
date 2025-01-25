package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.SubDistrictInfo;
import th.in.ffc.provider.CodeProvider;

public class SubDistrictDao {
    private Context mContext;

    public SubDistrictDao(Context context) {
        this.mContext = context;
    }

    public List<SubDistrictInfo> getSubdistrictsByDistrictCode(String districtCode, String provinceCode) {
        List<SubDistrictInfo> subdistricts = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = CodeProvider.Subdistrict.CONTENT_URI;
        String[] projection = {
                CodeProvider.Subdistrict.SUBDISTCODE,
                CodeProvider.Subdistrict.NAME,
                CodeProvider.Subdistrict.DISTCODE
        };
        String selection = CodeProvider.Subdistrict.DISTCODE + " = ? AND " + CodeProvider.Subdistrict.PROVCODE + " = ?";
        String[] selectionArgs = {districtCode, provinceCode};
        Cursor cursor = resolver.query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                SubDistrictInfo subdistrictInfo = new SubDistrictInfo();
                subdistrictInfo.setSubdistCode(cursor.getString(cursor.getColumnIndexOrThrow(CodeProvider.Subdistrict.SUBDISTCODE)));
                subdistrictInfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(CodeProvider.Subdistrict.NAME)));
                subdistrictInfo.setDistCode(cursor.getString(cursor.getColumnIndexOrThrow(CodeProvider.Subdistrict.DISTCODE)));
                subdistricts.add(subdistrictInfo);
            }
            cursor.close();
        }
        return subdistricts;
    }
}