package th.in.ffc.map.user;

import org.osmdroid.util.GeoPoint;

public class User {

    private GeoPoint geoPointCurrent;

    public User(GeoPoint geoPointCurrent) {
        this.geoPointCurrent = geoPointCurrent;
    }

    public GeoPoint getGeoPointCurrent() {
        return geoPointCurrent;
    }

    public void setGeoPointCurrent(GeoPoint geoPointCurrent) {
        this.geoPointCurrent = geoPointCurrent;
    }

}
