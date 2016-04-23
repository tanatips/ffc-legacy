package th.in.ffc.building.house;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.provider.CodeProvider.WaterType;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.provider.HouseProvider.HouseVesselWater;

public class HouseWaterViewFragment extends HouseFragment {
    private static final String[] PROJECTION = new String[]{
            House.Sanitation.WATERDRINK,
            House.Sanitation.WATERDRINKENO,
            House.Sanitation.WATERUSE,
            House.Sanitation.WATERUSEENO,

    };
    private static final String[] PROJECTION2 = new String[]{
            HouseVesselWater.VESSELCODE,
            HouseVesselWater.QUANTITY
    };

    @Override
    void Edit() {
        // TODO Auto-generated method stub
        go = new Intent(getActivity(), HouseWaterEditActivity.class);
        box = new Bundle();
        box.putString("hcode", getHcode());
        go.putExtras(box);
        startActivity(go);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        mForm.removeAllViewsInLayout();
        mForm.refreshDrawableState();
        Uri uri = Uri.withAppendedPath(House.CONTENT_URI, getHcode());
        ContentResolver cr = getFFCActivity().getContentResolver();
        Cursor c = cr
                .query(uri, PROJECTION, null, null, House.DEFAULT_SORTING);
        if (c.moveToFirst()) {
            doShowRegularData(c);
        }
    }

    private void doShowRegularData(Cursor c) {
        addTitle(getString(R.string.house_detail_water_provider));
        String watertype = c.getString(c.getColumnIndex(House.Sanitation.WATERDRINK));
        if (watertype != null)
            addContentQuery(R.string.house_waterdrink, WaterType.NAME, Uri.withAppendedPath(WaterType.CONTENT_URI, watertype), null);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_waterdrinkeno), c.getInt(1), R.array.houseEnough, 1, 1, false);
        String wateruse = c.getString(c.getColumnIndex(House.Sanitation.WATERUSE));
        if (wateruse != null)
            addContentQuery(R.string.house_wateruse, WaterType.NAME, Uri.withAppendedPath(WaterType.CONTENT_URI, wateruse), null);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_wateruseeno), c.getInt(3), R.array.houseEnough, 1, 1, false);


        Uri uri = Uri.withAppendedPath(HouseVesselWater.CONTENT_URI, getHcode());
        ContentResolver cr = getFFCActivity().getContentResolver();
        Cursor k = cr.query(uri, PROJECTION2, null, null, HouseVesselWater.DEFAULT_SORTING);
        if (k.moveToFirst()) {
            addTitle(R.string.housevesselwater_vessel);
            do {
                String vesselname = k.getString(0);
                addContentArrayFormat(getString(R.string.housevesselwater_vessel), checkStrangerForStringContent(vesselname, "2"), R.array.vesselItem);
                addContent(R.string.housevesselwater_quantity, k.getString(1));
            } while (k.moveToNext());
        }
        k.close();
    }
}

