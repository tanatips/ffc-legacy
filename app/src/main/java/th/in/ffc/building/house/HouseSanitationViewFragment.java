package th.in.ffc.building.house;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.provider.HouseProvider.House;

public class HouseSanitationViewFragment extends HouseFragment {
    private static final String[] PROJECTION = new String[]{
            House.Sanitation.HOUSEENDURE, House.Sanitation.HOUSECLEAN, House.Sanitation.HOUSECOMPLETE, House.Sanitation.HOUSEAIRFLOW,
            House.Sanitation.HOUSELIGHT, House.Sanitation.HOUSESANITATION, House.Sanitation.TOILET, House.Sanitation.WATERASSUAGE,
            House.Sanitation.GARBAGEWARE, House.Sanitation.GARBAGEERASE, House.Sanitation.PETS, House.Sanitation.PETSDUNG
    };

    @Override
    void Edit() {
        // TODO Auto-generated method stub
        go = new Intent(getActivity(), HouseSanitaionEditActivity.class);
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
            addTitle(getString(R.string.house_sanitation_waste));
        }
    }

    private void doShowRegularData(Cursor c) {
        addTitle(getString(R.string.house_sanitation_waste));
        addContentArrayFormat(getString(R.string.house_houseendur), checkStrangerForStringContent(c.getString(0), "2"), R.array.houseEndure);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_houseclean), checkStangerForIntegerContent(c.getString(1)), R.array.houseClean, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_housecomplete), checkStangerForIntegerContent(c.getString(2)), R.array.houseComplete, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_houseairflow), checkStangerForIntegerContent(c.getString(3)), R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_houselight), checkStangerForIntegerContent(c.getString(4)), R.array.houseEnough, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_housesanitation), checkStangerForIntegerContent(c.getString(5)), R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_toilet), checkStangerForIntegerContent(c.getString(6)), R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_waterassuage), checkStangerForIntegerContent(c.getString(7)), R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_garbageware), checkStangerForIntegerContent(c.getString(8)), R.array.houseHave, 1, 1, false);
        addContentArrayFormat(getString(R.string.house_garbageerase), checkStrangerForStringContent(c.getString(9), "2"), R.array.houseWaste);
        addContentArrayFormat(getString(R.string.house_pets), checkStrangerForStringContent(c.getString(10), "2"), R.array.housePet);
        addContentArrayFormat(getString(R.string.house_petsdung), checkStrangerForStringContent(c.getString(11), "2"), R.array.houseHave);

    }

}
