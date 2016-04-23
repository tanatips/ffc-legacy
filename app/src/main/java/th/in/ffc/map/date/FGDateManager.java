package th.in.ffc.map.date;

import th.in.ffc.map.system.FGSystemManager;

import java.text.SimpleDateFormat;
import java.util.Date;


public class FGDateManager {

    //private FGSystemManager fgSystemManager;

    public FGDateManager(FGSystemManager fgSystemManager) {
        //this.fgSystemManager = fgSystemManager;

        this.getStringCurrentDate();
    }

    public String getStringCurrentDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y-MM-d k:m:s.S");
        Date date = new Date();
        String stringDate = simpleDateFormat.format(date);
        return stringDate;
    }

}
