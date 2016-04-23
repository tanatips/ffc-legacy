package th.in.ffc.googlemap.visit506;


import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import th.in.ffc.R;
import th.in.ffc.app.FFCFragmentActivity;
import th.in.ffc.googlemap.lib.GoogleMapLib;
import th.in.ffc.provider.PersonProvider.VisitDiag506address;

public class Map506AddOrEditPositionFragmentActivity extends FFCFragmentActivity {
    private GoogleMapLib maps;
    private String visitno;
    private String status;
    private LatLng latlng;
    private double radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map506_addoredit_fragment);
        visitno = getIntent().getExtras().getString("visitno");
        status = getIntent().getExtras().getString("status");
        Toast.makeText(getApplicationContext(), visitno, Toast.LENGTH_SHORT).show();
        maps = new GoogleMapLib(this, R.id.mapaddposition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem submitPosition = menu.add(Menu.NONE, 1,
                Menu.NONE, "submitPosition");
        submitPosition.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        submitPosition.setIcon(R.drawable.ic_action_submitposition);

        submitPosition = menu.add(Menu.NONE, 2,
                Menu.NONE, "cancelAddPosition");
        submitPosition.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        submitPosition.setIcon(R.drawable.ic_action_cancel_add_position);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                latlng = maps.getPositionCircle();
                radius = maps.getRadius();
                updateDB();
                Toast.makeText(getApplicationContext(), "LATLNG :" + latlng.latitude + "," + latlng.longitude + " radius :" + radius, Toast.LENGTH_SHORT).show();
                return true;
            case 2:
                maps.clear();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateDB() {
        ContentValues values = new ContentValues();
        if (latlng != null) {
            //	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            //	Date date = new Date();
            //	System.out.println(dateFormat.format(date));
            String where = "visitno=?";
            String arg[] = {visitno};
            values.put(VisitDiag506address.LATITUDE, latlng.latitude);
            values.put(VisitDiag506address.LONGITUDE, latlng.longitude);

            //up date table visitdiag506address
            getContentResolver().update(VisitDiag506address.CONTENT_URI, values, where, arg);
            values.put(VisitDiag506address.VISITNO, visitno);
            values.put(VisitDiag506address.STATUS, status);
            values.put("radius", "" + radius);

            // insert data into table ffc_gis_visit506
            //	getContentResolver().insert(FFC_Gis_Visit506.CONTENT_URI, values);
        }
    }
}
