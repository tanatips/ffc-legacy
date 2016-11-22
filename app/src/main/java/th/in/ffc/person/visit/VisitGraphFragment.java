package th.in.ffc.person.visit;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.achartengine.GraphicalView;

import java.util.ArrayList;

import th.in.ffc.R;
import th.in.ffc.app.FFCFragment;
import th.in.ffc.provider.PersonProvider.Visit;

public class VisitGraphFragment extends FFCFragment {

    private Context context;
    private VisitGraphPluseWeight gph;
    private String pid;
    private ArrayList<String> dateOfWeight;
    private ArrayList<String> valueOfWeight;
    private ArrayList<String> dateOfPressure;
    private ArrayList<String> valueOfPressure;
    ArrayList<ArrayList<String>> seriesGraphValue;
    ArrayList<ArrayList<String>> seriesGraphDate;
    ArrayList<String> seriesGraphTAG;
    View v;
    String dateHilight;
    TextView textview;
    LinearLayout linear;
    GraphicalView gView;

    public VisitGraphFragment() {
        this.context = getActivity();
        init();
    }

    private void init() {
        dateOfWeight = new ArrayList<String>();
        valueOfWeight = new ArrayList<String>();
        dateOfPressure = new ArrayList<String>();
        valueOfPressure = new ArrayList<String>();
        seriesGraphValue = new ArrayList<ArrayList<String>>();
        seriesGraphDate = new ArrayList<ArrayList<String>>();
        seriesGraphTAG = new ArrayList<String>();
    }

    private void queryData() {
        String[] projection = {"pid", "visitno", "weight", "pressure", "visitdate", "pulse", "pressure2"};
        String selection = "pid =?";
        String[] selectionArgs = {pid};
        Uri uri = Visit.CONTENT_URI;
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor c = contentResolver.query(uri, projection, selection, selectionArgs, "visitno");
        if (c.moveToFirst()) {
            do {
                String weightkilo = c.getString(c.getColumnIndex("weight"));
                String pressure = c.getString(c.getColumnIndex("pressure"));
                String pressure2 = c.getString(c.getColumnIndex("pressure2"));
                String date = c.getString(c.getColumnIndex("visitdate"));
                if (checkDoubleType(weightkilo)) {
                    dateOfWeight.add(date);
                    valueOfWeight.add(weightkilo);
                }
                if (!TextUtils.isEmpty(pressure2)) {
                    String temps[] = pressure2.split("/");
                    if (temps.length == 2) {
                        if (checkDoubleType(temps[0]) && checkDoubleType(temps[1])) {
                            dateOfPressure.add(date);
                            valueOfPressure.add(pressure2);
                        }
                    }
                } else if (!TextUtils.isEmpty(pressure)) {
                    String temps[] = pressure.split("/");
                    if (temps.length == 2) {
                        if (checkDoubleType(temps[0]) && checkDoubleType(temps[1])) {
                            dateOfPressure.add(date);
                            valueOfPressure.add(pressure);
                        }
                    }
                }
            } while (c.moveToNext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        v = inflater.inflate(R.layout.visit_graph, container, false);
        linear = (LinearLayout) v.findViewById(R.id.sugarbloodgraph);
        textview = (TextView) v.findViewById(R.id.text);
        Bundle arg = getArguments();
        gph = new VisitGraphPluseWeight(context);
        if (arg != null) {
            pid = arg.getString("pid");
            dateHilight = arg.getString("visitdate");
            queryData();
        } else {
            VisitGraphDefault gg = new VisitGraphDefault(context);
            gg.setDefaultGraph(getString(R.string.plusegraphtitlenodata));
            gView = gg.getGraph();
            linear.addView(gView);
        }

        seriesGraphTAG.add(getString(R.string.weightkilo) + "-" + getString(R.string.kilo));

        if (!valueOfWeight.isEmpty()) {
            seriesGraphDate.add(dateOfWeight);
            seriesGraphValue.add(valueOfWeight);
        }
        if (!valueOfPressure.isEmpty()) {
            seriesGraphDate.add(dateOfPressure);
            seriesGraphValue.add(valueOfPressure);
            seriesGraphTAG.add(getString(R.string.pluse) + "-" + getString(R.string.pluseup) + "-" + getString(R.string.plusedown));
        }
        if (!seriesGraphValue.isEmpty()) {
            gph.setSeriesGraph(seriesGraphValue, seriesGraphDate, seriesGraphTAG, dateOfWeight);
            if (!TextUtils.isEmpty(dateHilight)) gph.setDateHilight(dateHilight);
            gView = gph.getGraph();
            linear.addView(gView);
        } else {
            VisitGraphDefault gg = new VisitGraphDefault(context);
            gg.setDefaultGraph(getString(R.string.plusegraphtitlenodata));
            gView = gg.getGraph();
            linear.addView(gView);
        }

        return v;
    }

    private boolean checkDoubleType(String checkType) {
        boolean type = false;
        try {
            Double.parseDouble(checkType);
            type = true;
        } catch (Exception o) {
            type = false;
        }
        return type;
    }
}
