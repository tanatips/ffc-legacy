package th.in.ffc.googlemap.lib;

import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import th.in.ffc.provider.PersonProvider.GET_ADDRESS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleMapSearchPosition extends AsyncTask<String, String, LatLng> {
    private Context context;
    private final Uri uri = GET_ADDRESS.CONTENT_URI;
    private final String PROJECTION[] = {"provcode", "provname", "latitude",
            "longitude"};
    private final String SORT_ORDER = "provcode";
    private ArrayList<Double> lat;
    private ArrayList<Double> lng;

    public GoogleMapSearchPosition(Context context) {
        this.context = context;
        lat = new ArrayList<Double>();
        lng = new ArrayList<Double>();
    }

	/*
     * public LatLng querySearching() { LatLng aniPosition = null; double
	 * tempLat = 0.0; double tempLng = 0.0; Cursor c =
	 * context.getContentResolver().query(uri, PROJECTION, null, null,
	 * SORT_ORDER); if (c.moveToFirst()) { do { String lattitude =
	 * c.getString(c.getColumnIndex("latitude")); String longitude =
	 * c.getString(c.getColumnIndex("longitude")); if (!lattitude.equals("0") &&
	 * !longitude.equals("0")) { tempLat = Double.parseDouble(c.getString(c
	 * .getColumnIndex("latitude"))); tempLng = Double.parseDouble(c.getString(c
	 * .getColumnIndex("longitude"))); lat.add(tempLat); lng.add(tempLng); } }
	 * while (c.moveToNext()); } if (lat.size() > 0 && lng.size() > 0) {
	 * aniPosition = getCenterPosition(); } else { aniPosition =
	 * getLocationName(c); } return aniPosition;
	 * 
	 * }
	 */

    private LatLng getCenterPosition() {
        LatLng centerPosition;
        double avgLat = 0.0;
        double avgLng = 0.0;
        for (int i = 0; i < lat.size(); i++) {
            avgLat += lat.get(i);
            avgLng += lng.get(i);
        }
        avgLat = avgLat / lat.size();
        avgLng = avgLng / lng.size();
        centerPosition = new LatLng(avgLat, avgLng);
        return centerPosition;
    }

    private LatLng getLocationName(Cursor c) {
        String province = null;
        LatLng latlng = null;
        Geocoder position;
        List<Address> test = null;
        position = new Geocoder(context);

        if (c.moveToFirst()) {
            do {
                province = c.getString(c.getColumnIndex("provname"));
                Log.d("TEST", "province" + province);
            } while (province == null && c.moveToNext());
        }
        try {
            if (province != null) {
                test = position.getFromLocationName(province, 1);
                latlng = new LatLng(test.get(0).getLatitude(), test.get(0)
                        .getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (latlng == null) {
            latlng = new LatLng(13.822578, 100.514233);
        }
        return latlng;
    }

    @Override
    protected LatLng doInBackground(String... params) {
        LatLng aniPosition = null;
        double tempLat = 0.0;
        double tempLng = 0.0;
        Cursor c = context.getContentResolver().query(uri, PROJECTION, null,
                null, SORT_ORDER);
        if (c.moveToFirst()) {
            do {
                String lattitude = c.getString(c.getColumnIndex("latitude"));
                String longitude = c.getString(c.getColumnIndex("longitude"));
                if (!lattitude.equals("0") && !longitude.equals("0")) {
                    tempLat = Double.parseDouble(c.getString(c
                            .getColumnIndex("latitude")));
                    tempLng = Double.parseDouble(c.getString(c
                            .getColumnIndex("longitude")));
                    lat.add(tempLat);
                    lng.add(tempLng);
                }
            } while (c.moveToNext());
        }
        if (lat.size() > 0 && lng.size() > 0) {
            aniPosition = getCenterPosition();
        } else {
            aniPosition = getLocationName(c);
        }
        return aniPosition;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        if (callback != null) {
            callback.returnPosition(result);
        }
        super.onPostExecute(result);
    }

    public static interface returnPosition {
        public void returnPosition(LatLng position);
    }

    returnPosition callback;

    public void setReturnPosition(returnPosition callPosition) {
        callback = callPosition;
    }
}
