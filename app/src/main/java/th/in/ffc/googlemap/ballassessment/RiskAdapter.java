package th.in.ffc.googlemap.ballassessment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import th.in.ffc.R;

import java.util.ArrayList;


public class RiskAdapter extends ArrayAdapter<String> {
    private Context myContext;
    ArrayList<String> name;
    ArrayList<String> lname;
    ArrayList<String> hno;
    ArrayList<String> villname;
    ArrayList<String> villno;
    ArrayList<String> age;

    public RiskAdapter(Context context, int textViewResourceId, String[] obj) {
        super(context, textViewResourceId, obj);
        myContext = context;
    }

    public RiskAdapter(Context context, int textViewResourceId, ArrayList<String> obj) {
        super(context, textViewResourceId, obj);
        myContext = context;
        name = obj;
    }

    public RiskAdapter(Context context, int textViewResourceId, ArrayList<String> name, ArrayList<String> lname,
                       ArrayList<String> hno, ArrayList<String> villno, ArrayList<String> villname, ArrayList<String> age) {
        super(context, textViewResourceId, name);
        myContext = context;
        this.name = name;
        this.lname = lname;
        this.hno = hno;
        this.villno = villno;
        this.villname = villname;
        this.age = age;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View personlist = inflater.inflate(R.layout.map_risk_fragment_adapter, parent, false);
        TextView txtname = (TextView) personlist.findViewById(R.id.name);
        TextView txthno = (TextView) personlist.findViewById(R.id.hno);
        if (name != null) {
            txtname.setText(name.get(position)+ " "+lname.get(position)+" อายุ "+age.get(position)+" ปี");
            txthno.setText(hno.get(position)+ " หมู่ "+villno.get(position)+" "+villname.get(position));
        }


        return personlist;
    }
}
