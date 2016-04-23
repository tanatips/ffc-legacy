package th.in.ffc.building.house;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.provider.HouseProvider.House;

public class HouseFoodViewFragment extends HouseFragment {
    private static final String[] PROJECTION = new String[]{
            House.Sanitation.FOODCOOK,
            House.Sanitation.FOODKEEPSAFE,
            House.Sanitation.FOODWARE,
            House.Sanitation.FOODWAREWASH,
            House.Sanitation.FOODWAREKEEP,
            House.Sanitation.FOODGARBAGEWARE,
            House.Sanitation.FOODCOOKROOM,
            House.Sanitation.FOODSANITATION,
            House.Sanitation.IODEINSALT,
            House.Sanitation.IODEINMATERIAL,
            House.Sanitation.IODEINUSE,
            House.Sanitation.FTLJ,
            House.Sanitation.WHJRK,
            House.Sanitation.SLPP,
            House.Sanitation.CHT,
            House.Sanitation.KMCH
    };

    @Override
    void Edit() {
        // TODO Auto-generated method stub
        go = new Intent(getActivity(), HouseFoodEditActivity.class);
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
            addTitle(getString(R.string.house_food_sanitation));
        }
    }

    private void doShowRegularData(Cursor c) {
        addTitle(getString(R.string.house_food_sanitation));

        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodcook),
                checkStangerForIntegerContent(c.getString(0)),
                R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodkeepsafe),
                checkStangerForIntegerContent(c.getString(1)),
                R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodware),
                checkStangerForIntegerContent(c.getString(2)),
                R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodwarewash),
                checkStangerForIntegerContent(c.getString(3)),
                R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodwarekeep),
                checkStangerForIntegerContent(c.getString(4)),
                R.array.houseCorrect, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodgarbageware),
                checkStangerForIntegerContent(c.getString(5)),
                R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodcookroom),
                checkStangerForIntegerContent(c.getString(6)),
                R.array.houseHave, 1, 1, false);
        addContentWithColorStateForArrayFormatSpinner(getString(R.string.house_foodsanitation),
                checkStangerForIntegerContent(c.getString(7)),
                R.array.houseCorrect, 1, 1, false);
        addContentArrayFormat(getString(R.string.house_iodeinsalt),
                checkStrangerForStringContent(c.getString(8), "2"),
                R.array.houseUse2);
        addContentArrayFormat(getString(R.string.house_iodeinmaterial),
                checkStrangerForStringContent(c.getString(9), "2"),
                R.array.houseUse2);
        addContentArrayFormat(getString(R.string.house_iodeinuse),
                checkStrangerForStringContent(c.getString(10), "2"),
                R.array.houseUse2);
        addContentArrayFormat(getString(R.string.house_ftlj),
                checkStrangerForStringContent(c.getString(11), "2"),
                R.array.houseUse);
        addContentArrayFormat(getString(R.string.house_whjrk),
                checkStrangerForStringContent(c.getString(12), "2"),
                R.array.houseUse);
        addContentArrayFormat(getString(R.string.house_slpp),
                checkStrangerForStringContent(c.getString(13), "2"),
                R.array.houseUse);
        addContentArrayFormat(getString(R.string.house_cht),
                checkStrangerForStringContent(c.getString(14), "2"),
                R.array.houseUse);
        addContentArrayFormat(getString(R.string.house_kmch),
                checkStrangerForStringContent(c.getString(15), "2"),
                R.array.houseUse);
    }
}
