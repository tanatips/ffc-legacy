package th.in.ffc.building.house;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.provider.HouseProvider.HouseAnimal;
import th.in.ffc.provider.HouseProvider.HouseGenusculex;

public class HouseCarrierViewFragment extends HouseFragment {
    private static final String[] PROJECTION = new String[]{
            House.CONTROLRAT,
            House.CONTROLCOCKROACH,
            House.CONTROLHOUSEFLY,
            House.CONTROLMQT,
            House.CONTROLINSECTDISEASE,
            House.NEARHOUSE
    };
    private static final String[] PROJECTION2 = new String[]{
            HouseAnimal.TYPE,
            HouseAnimal.MALE,
            HouseAnimal.FEMALE
    };
    private static final String[] PROJECTION3 = new String[]{
            HouseGenusculex.HCODE,
            HouseGenusculex.DATESURVEY,
            HouseGenusculex.NOOFVES,
            HouseGenusculex.NOOFGENUSCULEX
    };

    @Override
    void Edit() {
        // TODO Auto-generated method stub
        go = new Intent(getActivity(), HouseCarrierEditActivity.class);
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
        } else {
            addTitle(getString(R.string.house_carrier));
        }
    }

    private void doShowRegularData(Cursor c) {
        addTitle(getString(R.string.house_carrier));
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_controlrat), checkStangerForIntegerContent(c.getString(0)), R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_controlcockroach), checkStangerForIntegerContent(c.getString(1)), R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_controlhousefly), checkStangerForIntegerContent(c.getString(2)), R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_controlmqt), checkStangerForIntegerContent(c.getString(3)), R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_controlinsectdisease), checkStangerForIntegerContent(c.getString(4)), R.array.houseHave, 1, 1, false);
        addContent(R.string.house_nearhouse, c.getString(5));

        Uri uri = Uri.withAppendedPath(HouseAnimal.CONTENT_URI, getHcode());
        ContentResolver cr = getFFCActivity().getContentResolver();
        Cursor k = cr.query(uri, PROJECTION2, null, null, HouseAnimal.DEFAULT_SORTING);
        if (k.moveToFirst()) {
            addTitle(R.string.houseanimal_animaltype);
            do {
                String animalcode = k.getString(0);
                addContentArrayFormat(getString(R.string.houseanimal_animaltype), checkStrangerForStringContent(animalcode, "2"), R.array.animaltype);
                addContent(R.string.houseanimal_animalmale, k.getString(1));
                addContent(R.string.houseanimal_animalfemale, k.getString(2));
            } while (k.moveToNext());
        }
        k.close();

        uri = Uri.withAppendedPath(HouseGenusculex.CONTENT_URI, getHcode());
        k = cr.query(uri, PROJECTION3, null, null, HouseGenusculex.DEFAULT_SORTING);
        if (k.moveToFirst()) {
            addTitle(R.string.house_carrier_mosquito);
            do {
                addContent(R.string.housegenusculex_datesurvey, k.getString(1));
                addContent(R.string.housegenusculex_noofves, k.getString(2));
                addContent(R.string.housegenusculex_noofgenusculex, k.getString(3));
            } while (k.moveToNext());
        }
        k.close();

    }
}
