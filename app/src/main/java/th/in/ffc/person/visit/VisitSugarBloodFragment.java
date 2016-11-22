package th.in.ffc.person.visit;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.achartengine.GraphicalView;

import java.util.ArrayList;

import th.in.ffc.R;
import th.in.ffc.app.FFCFragment;
import th.in.ffc.provider.PersonProvider.VISIT_VISITLABSUGARBLOOD;


public class VisitSugarBloodFragment extends FFCFragment {
    View v;
    String mPid;
    ArrayList<String> TAG;
    ArrayList<ArrayList<String>> seriesSugar;
    ArrayList<ArrayList<String>> seriesDateSugar;
    ArrayList<String> dateOfSugar;
    ArrayList<String> valueOfSugar;
    LinearLayout linear;
    boolean checkType;
    GraphicalView gView;

    public void setOnShowListener(VisitSugarBloodFragment.onShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    onShowListener onShowListener;

    public VisitSugarBloodFragment() {
        dateOfSugar = new ArrayList<String>();
        valueOfSugar = new ArrayList<String>();
        seriesSugar = new ArrayList<ArrayList<String>>();
        seriesDateSugar = new ArrayList<ArrayList<String>>();
        TAG = new ArrayList<String>();
        checkType = true;
    }

    private void query() {
        String[] projection = {"pid", "visitno", "sugarnumdigit", "visitdate"};
        String selection = "pid =?";
        String[] selectionArgs = {mPid};
        Uri uri = VISIT_VISITLABSUGARBLOOD.CONTENT_URI;
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor c = contentResolver.query(uri, projection, selection, selectionArgs, "visit.visitno");
        if (c.moveToFirst()) {
            String sugarBlood = c.getString(c.getColumnIndex("sugarnumdigit"));
            String date = c.getString(c.getColumnIndex("visitdate"));
            if (c.getCount() == 1) {
                if (!TextUtils.isEmpty(sugarBlood) && checkDoubleType(sugarBlood)) {
                    dateOfSugar.add(date);
                    valueOfSugar.add(sugarBlood);
                } else {
                    checkType = false;
                }
            } else {
                do {
                    sugarBlood = c.getString(c.getColumnIndex("sugarnumdigit"));
                    date = c.getString(c.getColumnIndex("visitdate"));
                    if (!TextUtils.isEmpty(sugarBlood) && checkDoubleType(sugarBlood)) {
                        dateOfSugar.add(date);
                        valueOfSugar.add(sugarBlood);
                    }
                } while (c.moveToNext());
            }
        }
    }

    VisitSugarBloodGraph gph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        gph = new VisitSugarBloodGraph(getActivity());
        v = inflater.inflate(R.layout.visit_graph, container, false);
        linear = (LinearLayout) v.findViewById(R.id.sugarbloodgraph);
        Bundle a = getArguments();
        if (a != null) {
            mPid = a.getString("pid");
            query();
        } else {
            String title = getString(R.string.sugargraphtitlenodata);
            gph.setDefaultGraph(title);
        }
        if (!valueOfSugar.isEmpty()) {
            if (valueOfSugar.size() == 1) {
                if (!checkType) {
                    String title = getString(R.string.sugargraphtitlenodata);
                    gph.setDefaultGraph(title);
                } else {
                    TAG.add(getString(R.string.sugarlevel) + "-" + getString(R.string.sugargraph));
                    gph.setGraph(valueOfSugar, dateOfSugar, TAG);
                }
            } else {
                TAG.add(getString(R.string.sugarlevel) + "-" + getString(R.string.sugargraph));
                gph.setGraph(valueOfSugar, dateOfSugar, TAG);
            }
            gView = gph.getGraph();
            linear.addView(gView);
            onShowListener.onShowLayoutListener(true);
        } else {
            String title = getString(R.string.sugargraphtitlenodata);
            gph.setDefaultGraph(title);
            onShowListener.onShowLayoutListener(false);
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

    public interface onShowListener {
        public void onShowLayoutListener(boolean show);
    }

    public boolean checkShow() {
        return false;
    }
}
