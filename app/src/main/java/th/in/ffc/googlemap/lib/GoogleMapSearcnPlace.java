package th.in.ffc.googlemap.lib;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class GoogleMapSearcnPlace extends AsyncTask<String, LatLng, LatLng> {
    private onPositionListener onPositionListener;
    private Geocoder coderSearch;
    private LatLng position;

    public void GoogleMapSearchPlace(Context context) {
        coderSearch = new Geocoder(context);
    }

    @Override
    protected LatLng doInBackground(String... addr) {
        List<Address> listAddress = null;
        try {
            listAddress = coderSearch.getFromLocationName(addr[0], 10);
            position = new LatLng(listAddress.get(0).getLatitude(), listAddress.get(0)
                    .getLongitude());
        } catch (IOException e) {
            position = null;
            e.printStackTrace();
        }
        return position;
    }

    @Override
    protected void onPostExecute(LatLng result) {
        if (onPositionListener != null) {
            onPositionListener.onPositionListener(result);
        }
        super.onPostExecute(result);
    }

    public void setOnPositionListener(onPositionListener onPositionListener) {
        this.onPositionListener = onPositionListener;
    }

    public interface onPositionListener {
        public void onPositionListener(LatLng position);
    }

}
