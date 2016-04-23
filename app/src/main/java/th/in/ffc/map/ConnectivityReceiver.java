package th.in.ffc.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import th.in.ffc.R;

public class ConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        FGActivity act = FGActivity.fgsys.getFGActivity();

        Resources res = act.getResources();
        String str = res.getString(R.string.wifi_text);

        ConnectivityManager connect = (ConnectivityManager) arg0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo tmp = connect.getActiveNetworkInfo();
        boolean connected = tmp != null && tmp.isConnected();

        Message msg = new Message();
        msg.what = FGActivity.UPDATE_WIFI_ICON;
        if (connected) {
            msg.arg1 = R.drawable.wifi1;
            msg.obj = str + "Online";
        } else {
            msg.arg1 = R.drawable.wifi2;
            msg.obj = str + "Offline";
        }

        act.getHandler().sendMessage(msg);

        FGActivity.fgsys.getFGMapManager().getMapView().setUseDataConnection(connected);
        FGActivity.fgsys.getFGMapManager().checkGPS();
        // Log.i("TAG!","TAG! "+(tmp.getState()));
    }

}
