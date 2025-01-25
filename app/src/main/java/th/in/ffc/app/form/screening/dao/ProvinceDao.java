package th.in.ffc.app.form.screening.dao;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import th.in.ffc.app.form.screening.model.ProvinceInfo;
import th.in.ffc.provider.CodeProvider;
import th.in.ffc.provider.CodeProvider.Province;

public class ProvinceDao {
    private Context mContext;

    public ProvinceDao(Context context) {
        this.mContext = context;
    }

    public String getProvinceCodeByName(String provinceName) {
        String provinceCode = null;
        Uri uri = Province.CONTENT_URI;
        String[] projection = {Province.PROVCODE};
        String selection = Province.NAME + " = ?";
        String[] selectionArgs = {provinceName};

        Cursor cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                provinceCode = cursor.getString(cursor.getColumnIndexOrThrow(Province.PROVCODE));
            }
            cursor.close();
        }
        return provinceCode;
    }
    public List<ProvinceInfo> getAllProvinces() {
        List<ProvinceInfo> provinces = new ArrayList<>();
        ContentResolver resolver = mContext.getContentResolver();
        Uri uri = CodeProvider.Province.CONTENT_URI;
        String[] projection = {
                Province.PROVCODE,
                Province.NAME,
                Province.ZONE,
                Province.REGION
        };

        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ProvinceInfo provinceinfo = new ProvinceInfo();
                provinceinfo.setProvCode(cursor.getString(cursor.getColumnIndexOrThrow(Province.PROVCODE)));
                provinceinfo.setName(cursor.getString(cursor.getColumnIndexOrThrow(Province.NAME)));
                provinceinfo.setZone(cursor.getString(cursor.getColumnIndexOrThrow(Province.ZONE)));
                provinceinfo.setRegion(cursor.getString(cursor.getColumnIndexOrThrow(Province.REGION)));
                provinces.add(provinceinfo);
            }
            cursor.close();
        }
        return provinces;
    }

}