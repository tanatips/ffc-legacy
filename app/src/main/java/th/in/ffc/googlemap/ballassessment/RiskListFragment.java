package th.in.ffc.googlemap.ballassessment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import th.in.ffc.R;
import th.in.ffc.app.FFCListFragment;

import java.util.ArrayList;

public class RiskListFragment extends FFCListFragment {
    View v;

    onListItemClickListener onListItemClickListener;

    public static interface onListItemClickListener {
        public void onItemClickListener(int position);
    }

    public RiskListFragment() {

    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        if (onListItemClickListener != null) {
            onListItemClickListener.onItemClickListener(position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        Bundle a = getArguments();
        RiskAdapter myListAdapter = null;
        ArrayList<String> name = a.getStringArrayList("name");
        ArrayList<String> lname = a.getStringArrayList("lname");
        ArrayList<String> hno = a.getStringArrayList("hno");
        ArrayList<String> villno = a.getStringArrayList("villno");
        ArrayList<String> villname = a.getStringArrayList("villname");
        ArrayList<String> age = a.getStringArrayList("age");
        v = inflater.inflate(R.layout.map_risk_listfragment, container, false);
        if (name != null) {
            myListAdapter = new RiskAdapter(getActivity(), R.layout.map_risk_listfragment, name, lname, hno, villno, villname, age);
        }
        setListAdapter(myListAdapter);
        return v;
    }

    public void setOnItemClick(onListItemClickListener onListItemClickListener) {
        this.onListItemClickListener = onListItemClickListener;
    }

}
