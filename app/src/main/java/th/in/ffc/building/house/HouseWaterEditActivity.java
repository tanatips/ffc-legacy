package th.in.ffc.building.house;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.provider.HouseProvider.HouseVesselWater;
import th.in.ffc.provider.PersonProvider.PersonColumns;
import th.in.ffc.widget.ArrayFormatSpinner;

public class HouseWaterEditActivity extends FFCEditActivity {

    // Object

    ArrayFormatSpinner waterdrink;
    ArrayFormatSpinner waterdrinkeno;
    ArrayFormatSpinner wateruse;
    ArrayFormatSpinner wateruseeno;
    Button goVessel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_water_edit);
        setContentDisplay();

    }


    private void setContentDisplay() {
        // TODO Auto-generated method stub
        waterdrink = (ArrayFormatSpinner) findViewById(R.id.answer1);
        waterdrink.setArray(R.array.watertype);
        waterdrinkeno = (ArrayFormatSpinner) findViewById(R.id.answer2);
        waterdrinkeno.setArray(R.array.houseEnough);
        wateruse = (ArrayFormatSpinner) findViewById(R.id.answer3);
        wateruse.setArray(R.array.watertype);
        wateruseeno = (ArrayFormatSpinner) findViewById(R.id.answer4);
        wateruseeno.setArray(R.array.houseEnough);
        goVessel = (Button) findViewById(R.id.btnGoUpdate);
        goVessel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(HouseWaterEditActivity.this, HouseWaterVesselActivity.class);
                intent.setData(Uri.withAppendedPath(House.CONTENT_URI, share_hcode));
                intent.putExtra(PersonColumns._PID, "00000");
                intent.putExtra(PersonColumns._PCUCODEPERSON, "00000");
                intent.putExtra(HouseVesselWater.HCODE, share_hcode);
                intent.putExtra(House.PCUCODE, share_pcucode);
                startActivity(intent);

            }
        });

        setContentQuery(House.CONTENT_URI, new String[]{House.Sanitation.WATERDRINK,
                House.Sanitation.WATERDRINKENO, House.Sanitation.WATERUSE, House.Sanitation.WATERUSEENO,}, "hcode = ?", new String[]{share_hcode}, House.DEFAULT_SORTING);
        setContentForUpdate();
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        wateruse.setSelection(checkStrangerForStringContent(array[0], "11"));
        waterdrinkeno.setSelection(checkStrangerForStringContent(array[1], "11"));
        wateruse.setSelection(checkStrangerForStringContent(array[2], "1"));
        wateruseeno.setSelection(checkStrangerForStringContent(array[3], "1"));

    }


    public void Update() {
        ContentValues cv = new ContentValues();

        cv.put("waterdrink", waterdrink.getSelectionId());
        cv.put("waterdrinkeno", waterdrinkeno.getSelectionId());
        cv.put("wateruse", wateruse.getSelectionId());
        cv.put("wateruseeno", wateruseeno.getSelectionId());

        updateTimeStamp(cv);
        doHouseCommit(cv, House.CONTENT_URI, true);


    }


    @Override
    protected void Delete() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void Edit() {
        // TODO Auto-generated method stub

    }

}
