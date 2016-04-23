package th.in.ffc.map.dialog.buider;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import th.in.ffc.map.system.FGSystemManager;
import th.in.ffc.map.value.FinalValue;

public class BuilderAllHouseMarkerHasBeanCreated extends Builder {

    //private FGSystemManager fgSystemManager;

    public BuilderAllHouseMarkerHasBeanCreated(FGSystemManager fgSystemManager) {
        super(fgSystemManager.getFGActivity());

        //this.fgSystemManager = fgSystemManager;

        this.setMessage(FinalValue.STRING_All_HOUSE_MARKER_HAS_BEEN_CREATED_MESSAGE);

        this.setCancelable(false);

        this.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int intId) {
                dialogInterface.dismiss();
            }
        });
    }

}
