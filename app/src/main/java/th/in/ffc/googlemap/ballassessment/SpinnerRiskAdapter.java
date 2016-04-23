package th.in.ffc.googlemap.ballassessment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import th.in.ffc.R;

public class SpinnerRiskAdapter extends ArrayAdapter<String> {
    LayoutInflater inflator;
    String[] list;
    LayoutInflater inflater;
    int[] resourceBall;

    public SpinnerRiskAdapter(Context context, int textViewResourceId,
                              String[] objects) {
        super(context, textViewResourceId, objects);
    }

    public SpinnerRiskAdapter(Context applicationContext,
                              int mapNcdRiskSpinnercustom, String[] risk, LayoutInflater inflater2, int[] riskresource) {
        super(applicationContext, mapNcdRiskSpinnercustom, risk);
        this.inflater = inflater2;
        this.list = risk;
        resourceBall = riskresource;

    }

    /*
     * public SpinnerRiskAdapter1(Context applicationContext, int
     * mapNcdRiskSpinnercustom, String[] risk, LayoutInflater inflater2) { //
     * TODO Auto-generated constructor stub }
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View v = inflater.inflate(R.layout.map_ncd_risk_spinnercustom, parent, false);
        ImageView imageball = (ImageView) v.findViewById(R.id.imageball);
        TextView txtList = (TextView) v.findViewById(R.id.spinner_text_custom);
        txtList.setText(list[position]);
        imageball.setImageResource(resourceBall[position]);

        return v;
    }
}
