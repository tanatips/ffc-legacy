package th.in.ffc.building.house;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.provider.HouseProvider.HouseAnimal;
import th.in.ffc.provider.PersonProvider.PersonColumns;
import th.in.ffc.widget.ArrayFormatSpinner;

public class HouseCarrierEditActivity extends FFCEditActivity {

    // Object
    ArrayFormatSpinner controlrat;
    ArrayFormatSpinner controlcockroach;
    ArrayFormatSpinner controlhousefly;
    ArrayFormatSpinner controlmqt;
    ArrayFormatSpinner controlinsectdisease;
    EditText nearhouse;
    Button goUpdateAnimal;
    Button goUpdateGenusCulex;
    Menu theMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_carrier_edit);
        setContentDisplay();

    }


    private void setContentDisplay() {
        // TODO Auto-generated method stub
        controlrat = (ArrayFormatSpinner) findViewById(R.id.answer1);
        controlcockroach = (ArrayFormatSpinner) findViewById(R.id.answer2);
        controlhousefly = (ArrayFormatSpinner) findViewById(R.id.answer3);
        controlmqt = (ArrayFormatSpinner) findViewById(R.id.answer4);
        controlinsectdisease = (ArrayFormatSpinner) findViewById(R.id.answer5);
        nearhouse = (EditText) findViewById(R.id.answer6);
        goUpdateAnimal = (Button) findViewById(R.id.btnGoUpdate);
        goUpdateGenusCulex = (Button) findViewById(R.id.btnGoUpdate2);
        controlrat.setArray(R.array.houseHave);
        controlcockroach.setArray(R.array.houseHave);
        controlhousefly.setArray(R.array.houseHave);
        controlmqt.setArray(R.array.houseHave);
        controlinsectdisease.setArray(R.array.houseHave);

        goUpdateAnimal.setOnClickListener(goUpdate);
        goUpdateGenusCulex.setOnClickListener(goUpdate);
        setContentQuery(House.CONTENT_URI, new String[]{House.CONTROLRAT, House.CONTROLCOCKROACH, House.CONTROLHOUSEFLY, House.CONTROLMQT
                , House.CONTROLINSECTDISEASE, House.NEARHOUSE}, "hcode = ?", new String[]{share_hcode}, House.DEFAULT_SORTING);

        setContentForUpdate();
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        controlrat.setSelection(array[0]);
        controlcockroach.setSelection(array[1]);
        controlhousefly.setSelection(array[2]);
        controlmqt.setSelection(array[3]);
        controlinsectdisease.setSelection(array[4]);
        nearhouse.setText(checkEditText(array[5]));
    }

    OnClickListener goUpdate = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            switch (v.getId()) {
                case R.id.btnGoUpdate:
                    Intent intent = new Intent(HouseCarrierEditActivity.this, HouseCarrierAnimalActivity.class);
                    intent.setData(Uri.withAppendedPath(House.CONTENT_URI, share_hcode));
                    intent.putExtra(PersonColumns._PID, "00000");
                    intent.putExtra(PersonColumns._PCUCODEPERSON, "00000");
                    intent.putExtra(HouseAnimal.HCODE, share_hcode);
                    intent.putExtra(House.PCUCODE, share_pcucode);
                    startActivity(intent);
                    break;
                case R.id.btnGoUpdate2:
                    Intent intent2 = new Intent(HouseCarrierEditActivity.this, HouseCarrierMosquitoActivity.class);
                    intent2.setData(Uri.withAppendedPath(House.CONTENT_URI, share_hcode));
                    intent2.putExtra(PersonColumns._PID, "00000");
                    intent2.putExtra(PersonColumns._PCUCODEPERSON, "00000");
                    intent2.putExtra(HouseAnimal.HCODE, share_hcode);
                    intent2.putExtra(House.PCUCODE, share_pcucode);
                    startActivity(intent2);
                    break;
            }

        }
    };

    public void Update() {
        ContentValues cv = new ContentValues();

        cv.put("controlrat", controlrat.getSelectionId());
        cv.put("controlcockroach", controlcockroach.getSelectionId());
        cv.put("controlhousefly", controlhousefly.getSelectionId());
        cv.put("controlmqt", controlmqt.getSelectionId());
        cv.put("controlinsetdisease", controlinsectdisease.getSelectionId());
        retrieveDataFromEditText(nearhouse, cv, "nearhouse");

        updateTimeStamp(cv);

        doHouseCommit(cv, House.CONTENT_URI, true);

    }

    @Override
    protected void Delete() {
    }

    @Override
    protected void Edit() {
    }

}
