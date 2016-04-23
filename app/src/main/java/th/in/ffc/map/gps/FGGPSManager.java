package th.in.ffc.map.gps;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import org.osmdroid.util.GeoPoint;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;

public class FGGPSManager {

    private FGSystemManager fgSystemManager;

    private String stringProvider;

    private LocationManager locationManager;

    private Criteria criteria;

    // private Location location;

    public FGGPSManager(FGSystemManager fgSystemManager) {
        this.fgSystemManager = fgSystemManager;

        FGActivity fgActivity = this.fgSystemManager.getFGActivity();

        String stringContext = Context.LOCATION_SERVICE;

        this.locationManager = (LocationManager) fgActivity
                .getSystemService(stringContext);

        this.criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);

        this.regenerateProvider();
        // this.stringProvider = LocationManager.GPS_PROVIDER;

    }

    public void regenerateProvider() {
        this.stringProvider = locationManager.getBestProvider(criteria, true);
    }

    public void requestLocationUpdates(LocationListener locationListener) {
        this.locationManager.requestLocationUpdates(this.stringProvider,
                FinalValue.INT_MIN_TIME, FinalValue.INT_MIN_DISTANCE,
                locationListener);
    }

    public GeoPoint getGeoPointCurrent() {

        Location location = this.getLastKnownLocation();

        if (location == null) {
            return null;
        }

        return new GeoPoint(location.getLatitude(), location.getLongitude());

    }

    public Location getLastKnownLocation() {
        return (this.stringProvider != null) ? this.locationManager
                .getLastKnownLocation(this.stringProvider) : null;
    }

    public LocationManager getLocationManager() {
        return this.locationManager;
    }
}
