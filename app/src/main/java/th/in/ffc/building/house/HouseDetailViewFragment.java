package th.in.ffc.building.house;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import th.in.ffc.R;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.provider.HouseProvider.Village;
import th.in.ffc.provider.PersonProvider.Person;

public class HouseDetailViewFragment extends HouseFragment {
    private static final String[] PROJECTION = new String[]{
            House.VILLCODE,
            House.PID,
            House.PIDVOLA,
            House.CHAR,
            House.CHARGROUND,
            House.AREA,
            House.COMMUNITY,
            House.ROAD,
            House.TEL,
            House.USERNAMEDOC,
            House.HEADHEALTHHOUSE
    };

    @Override
    void Edit() {
        // TODO Auto-generated method stub
        go = new Intent(getActivity(), HouseDetailEditActivity.class);
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
            addTitle(getString(R.string.house_detail));
        }
    }

    private void doShowRegularData(Cursor c) {
        addTitle(getString(R.string.house_detail));
        String villcode = c.getString(c.getColumnIndex(House.VILLCODE));
        if (villcode != null)
            addContentQuery(R.string.house_villcode, Village.VILLNAME, Uri.withAppendedPath(Village.CONTENT_URI, villcode), null);
        String pid = c.getString(c.getColumnIndex(House.PID));
        if (pid != null)
            addContentQuery(R.string.house_pid, Person.FULL_NAME, Uri.withAppendedPath(Person.CONTENT_URI, pid), null);
        String pidvola = c.getString(c.getColumnIndex(House.PIDVOLA));
        if (pidvola != null)
            addContentQuery(R.string.house_pidvola, Person.FULL_NAME, Uri.withAppendedPath(Person.CONTENT_URI, pidvola), null);

//		addContentNonZeroBased(R.string.house_housechar,c.getInt(c.getColumnIndex(House.CHAR)),R.array.houseChar);
//		addContentNonZeroBased(R.string.house_housecharground,c.getInt(c.getColumnIndex(House.CHARGROUND)),R.array.houseCharground);
//		if(c.getString(c.getColumnIndex(House.AREA))!=null)
//			addContentNonZeroBased(R.string.area,c.getInt(c.getColumnIndex(House.AREA)),R.array.houseArea);

        addContentArrayFormat(getString(R.string.house_housechar), c.getString(c.getColumnIndex(House.CHAR)), R.array.houseChar);
        addContentArrayFormat(getString(R.string.house_housecharground), c.getString(c.getColumnIndex(House.CHARGROUND)), R.array.houseCharground);
        if (c.getString(c.getColumnIndex(House.AREA)) != null)
            addContentArrayFormat(getString(R.string.house_area), c.getString(c.getColumnIndex(House.AREA)), R.array.houseArea);
        addContent(R.string.communityno, c.getString(c.getColumnIndex(House.COMMUNITY)));
        addContent(R.string.road, c.getString(c.getColumnIndex(House.ROAD)));
        addContent(R.string.house_telephonehouse, c.getString(c.getColumnIndex(House.TEL)));

        String headhealthhouse = c.getString(c.getColumnIndex(House.HEADHEALTHHOUSE));
        if (headhealthhouse != null)
            addContentQuery(R.string.house_headhealthhouse, Person.FULL_NAME, Uri.withAppendedPath(Person.CONTENT_URI, headhealthhouse), null);

        addContent(R.string.house_usernamedoc, c.getString(c.getColumnIndex(House.USERNAMEDOC)));
    }


}
