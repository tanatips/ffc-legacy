package th.in.ffc.map.village.spot;

import android.content.res.Resources;
import android.os.Bundle;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import th.in.ffc.R;
import th.in.ffc.map.FGActivity;
import th.in.ffc.map.value.MARKER_TYPE;

public class Spot extends OverlayItem {

    // protected String stringVillName;

    protected String stringVillCode;
    protected int intPartialID;
    private Bundle mBundle;
    protected String pcucode;

    public Spot(String pcucode, MARKER_TYPE type, String stringVillCode,
                int intPartialID, double doubleLat, double doubleLong,
                Bundle mBundle) {

        super(type.name(), null, null, new GeoPoint(doubleLat, doubleLong));

        this.pcucode = pcucode;
        this.stringVillCode = stringVillCode;
        this.intPartialID = intPartialID;

        this.mBundle = mBundle;
    }

    public String getStringVillName() {
        return FGActivity.fgsys.getFGDatabaseManager().getVillageName(
                stringVillCode);
    }

    public Bundle getBundle() {
        return this.mBundle;
    }

    public String getStringVillCode() {
        return this.stringVillCode;
    }

    public int getPartialID() {
        return this.intPartialID;
    }

    public String getRepresentativeString(String str) {
        if (this.mBundle == null || str == null)
            return this.intPartialID + " (" + this.getStringVillName() + ")";
        return this.mBundle.getString(str) + " (" + this.getStringVillName()
                + ")";
    }

    public double getDoubleLong() {
        return ((double) this.mGeoPoint.getLongitudeE6()) / 1E6;
    }

    public double getDoubleLat() {
        return ((double) this.mGeoPoint.getLatitudeE6()) / 1E6;
    }

/*    public void setLatitude(double arg0) {
        this.mGeoPoint.setLatitudeE6((int) (arg0 * 1E6));
    }

    public void setLongitude(double arg0) {
        this.mGeoPoint.setLongitudeE6((int) (arg0 * 1E6));
    }*/

    public String getID() {
        return pcucode + "h" + this.intPartialID;
    }

    @Override
    public String getTitle() {

        String title = super.getTitle();
        if (title != null)
            return title;

        return getStringVillName();
    }

    @Override
    public String getSnippet() {
        if (this.mBundle == null)
            return this.intPartialID + "";

        MARKER_TYPE type = Enum.valueOf(MARKER_TYPE.class, this.mUid);
        StringBuilder sb = new StringBuilder();
        Resources res = FGActivity.fgsys.getFGActivity().getResources();

        switch (type) {
            case HOUSE:
                sb.append(
                        res.getString(R.string.houseNo) + ": "
                                + mBundle.getString("HNo")).append("\n");
                sb.append(
                        res.getString(R.string.telephone) + ": "
                                + mBundle.getString("Telephone")).append("\n");
                break;
            case BUSINESS:
                sb.append(
                        res.getString(R.string.shop_name) + ": "
                                + mBundle.getString("Name")).append("\n");
                sb.append(
                        res.getString(R.string.type) + ": "
                                + mBundle.getString("Type")).append("\n");
                break;
            case HOSPITAL:
                sb.append(
                        res.getString(R.string.hospital_name) + ": "
                                + mBundle.getString("Name")).append("\n");
                sb.append(
                        res.getString(R.string.telephone) + ": "
                                + mBundle.getString("Telephone")).append("\n");
                break;
            case POI:
                sb.append(
                        res.getString(R.string.poi_name) + ": "
                                + mBundle.getString("Name")).append("\n");
                sb.append(
                        res.getString(R.string.telephone) + ": "
                                + mBundle.getString("Telephone")).append("\n");
                break;
            case SCHOOL:
                sb.append(
                        res.getString(R.string.school_name) + ": "
                                + mBundle.getString("Name")).append("\n");
                sb.append(
                        res.getString(R.string.telephone) + ": "
                                + mBundle.getString("Telephone")).append("\n");
                break;
            case TEMPLE:
                sb.append(
                        res.getString(R.string.temple_create) + ": "
                                + mBundle.getString("Name")).append("\n");
                sb.append(
                        res.getString(R.string.religion) + " "
                                + mBundle.getString("Religion")).append("\n");
                break;
            case WATER:
                sb.append(
                        res.getString(R.string.water_type) + ": "
                                + mBundle.getString("Type")).append("\n");
                sb.append(
                        res.getString(R.string.sector) + ": "
                                + mBundle.getString("Sector")).append("\n");
                break;
        }

        return sb.toString();

        // if(this.mUid.equals("HOUSE"))
        // return this.mBundle.getString("HNo");
        //
        // String snippet = super.getSnippet();
        // if(snippet == null){
        // snippet = this.mBundle.getString("Name");
        // if(snippet == null){
        // snippet = this.intPartialID+"";
        // }
        // }
        // return snippet;
    }

    // @Override
    // public String toString() {
    // return this.getID();
    // }
}
