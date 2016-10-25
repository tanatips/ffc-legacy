package th.in.ffc.map.overlay;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.view.MotionEvent;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.ui.markerActivity.CreateMarkerActivity;

public class FGOverlay extends Overlay {

    private FGSystemManager fgSystemManager;

    public FGOverlay(FGSystemManager fgSystemManager) {
        super(fgSystemManager.getFGActivity().getApplicationContext());
        this.fgSystemManager = fgSystemManager;
    }

    @Override
    protected void draw(Canvas arg0, MapView arg1, boolean arg2) {
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e, MapView mapView) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e, MapView mapView) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e, MapView mapView) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean onLongPress(MotionEvent e, MapView mapView) {

        IGeoPoint geoPoint = mapView.getProjection().fromPixels((int) e.getX(),
                (int) e.getY());

        if (this.fgSystemManager.getFGDatabaseManager().getAvailable().size() == 0) {
            AlertDialog alertDialogAllHouseHasBeenCreated = this.fgSystemManager
                    .getFGDialogManager()
                    .getAlertDialogAllHouseHasBeenCreated();
            alertDialogAllHouseHasBeenCreated.show();
            return true;
        }

        Intent act = new Intent(fgSystemManager.getFGActivity(),
                CreateMarkerActivity.class);

        double doubleLatitude = (double) (geoPoint.getLatitudeE6() / Math.pow(
                10, 6));
        act.putExtra("doubleLatitude", doubleLatitude);

        double doubleLongitude = (double) (geoPoint.getLongitudeE6() / Math
                .pow(10, 6));
        act.putExtra("doubleLongitude", doubleLongitude);

        // act.putExtra("number", 0);

        fgSystemManager.startActivity(act);

        return true;
    }

}
