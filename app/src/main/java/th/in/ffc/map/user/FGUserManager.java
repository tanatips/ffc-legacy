package th.in.ffc.map.user;

import org.osmdroid.util.GeoPoint;
import th.in.ffc.map.gps.FGGPSManager;
import th.in.ffc.map.system.FGSystemManager;


public class FGUserManager {

    private FGSystemManager fgSystemManager;

    private User user;

    public FGUserManager(FGSystemManager fgSystemManage) {
        this.fgSystemManager = fgSystemManage;

        FGGPSManager fgGPSManager = this.fgSystemManager.getFGGPSManager();

        GeoPoint geoPointCurrent = fgGPSManager.getGeoPointCurrent();

        this.user = new User(geoPointCurrent);
    }

    public User getUser() {
        return user;
    }
}
