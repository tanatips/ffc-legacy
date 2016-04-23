package th.in.ffc.googlemap.visit506;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import th.in.ffc.R;

import java.util.ArrayList;

public class listDialogCustom extends BaseAdapter {

    private ArrayList<String> name;
    private LayoutInflater inflater = null;

    public listDialogCustom(Activity activity, ArrayList<String> name) {
        this.name = name;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.visit506_namedialog_custom, null);
        TextView txtname = (TextView) vi.findViewById(R.id.txtname);
        txtname.setText(name.get(position));
        return vi;
    }
}
