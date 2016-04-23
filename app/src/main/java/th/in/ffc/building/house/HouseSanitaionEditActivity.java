package th.in.ffc.building.house;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.widget.ArrayFormatSpinner;

public class HouseSanitaionEditActivity extends FFCEditActivity {

    // Object

    ArrayFormatSpinner houseendur;
    ArrayFormatSpinner houseclean;
    ArrayFormatSpinner housecomplete;
    ArrayFormatSpinner houseairflow;
    ArrayFormatSpinner houselight;
    ArrayFormatSpinner housesanitation;
    ArrayFormatSpinner toilet;
    ArrayFormatSpinner waterassuage;
    ArrayFormatSpinner garbageware;
    ArrayFormatSpinner garbageerase;
    ArrayFormatSpinner pets;
    ArrayFormatSpinner petsdung;


    Menu theMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_sanitation_edit);
        setContentDisplay();

    }


    private void setContentDisplay() {
        // TODO Auto-generated method stub
        houseendur = (ArrayFormatSpinner) findViewById(R.id.answer1);
        houseclean = (ArrayFormatSpinner) findViewById(R.id.answer2);
        housecomplete = (ArrayFormatSpinner) findViewById(R.id.answer3);
        houseairflow = (ArrayFormatSpinner) findViewById(R.id.answer4);
        houselight = (ArrayFormatSpinner) findViewById(R.id.answer5);
        housesanitation = (ArrayFormatSpinner) findViewById(R.id.answer6);
        toilet = (ArrayFormatSpinner) findViewById(R.id.answer7);
        waterassuage = (ArrayFormatSpinner) findViewById(R.id.answer8);
        garbageware = (ArrayFormatSpinner) findViewById(R.id.answer9);
        garbageerase = (ArrayFormatSpinner) findViewById(R.id.answer10);
        pets = (ArrayFormatSpinner) findViewById(R.id.answer11);
        petsdung = (ArrayFormatSpinner) findViewById(R.id.answer12);

        houseendur.setArray(R.array.houseEndure);
        houseclean.setArray(R.array.houseClean);
        housecomplete.setArray(R.array.houseComplete);
        houseairflow.setArray(R.array.houseHave);
        houselight.setArray(R.array.houseEnough);
        housesanitation.setArray(R.array.houseCorrect);
        toilet.setArray(R.array.houseCorrect);
        waterassuage.setArray(R.array.houseHave);
        garbageware.setArray(R.array.houseHave);
        garbageerase.setArray(R.array.houseWaste);
        pets.setArray(R.array.housePet);
        petsdung.setArray(R.array.houseHave);


        setContentQuery(House.CONTENT_URI, new String[]{House.Sanitation.HOUSEENDURE, House.Sanitation.HOUSECLEAN, House.Sanitation.HOUSECOMPLETE,
                House.Sanitation.HOUSEAIRFLOW, House.Sanitation.HOUSELIGHT, House.Sanitation.HOUSESANITATION,
                House.Sanitation.TOILET, House.Sanitation.WATERASSUAGE, House.Sanitation.GARBAGEWARE,
                House.Sanitation.GARBAGEERASE, House.Sanitation.PETS, House.Sanitation.PETSDUNG}, "hcode = ?", new String[]{share_hcode}, House.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        houseendur.setSelection(checkStrangerForStringContent(array[0], "0"));
        houseclean.setSelection(checkStrangerForStringContent(array[1], "0"));
        housecomplete.setSelection(checkStrangerForStringContent(array[2], "0"));
        houseairflow.setSelection(checkStrangerForStringContent(array[3], "0"));
        houselight.setSelection(checkStrangerForStringContent(array[4], "0"));
        housesanitation.setSelection(checkStrangerForStringContent(array[5], "0"));
        toilet.setSelection(checkStrangerForStringContent(array[6], "0"));
        waterassuage.setSelection(checkStrangerForStringContent(array[7], "0"));
        garbageware.setSelection(checkStrangerForStringContent(array[8], "0"));
        garbageerase.setSelection(checkStrangerForStringContent(array[9], "0"));
        pets.setSelection(checkStrangerForStringContent(array[10], "0"));
        petsdung.setSelection(checkStrangerForStringContent(array[11], "0"));
    }


    public void Update() {
        ContentValues cv = new ContentValues();

        cv.put(House.Sanitation.HOUSEENDURE, houseendur.getSelectionId());
        cv.put(House.Sanitation.HOUSECLEAN, houseclean.getSelectionId());
        cv.put(House.Sanitation.HOUSECOMPLETE, housecomplete.getSelectionId());
        cv.put(House.Sanitation.HOUSEAIRFLOW, houseairflow.getSelectionId());
        cv.put(House.Sanitation.HOUSELIGHT, houselight.getSelectionId());
        cv.put(House.Sanitation.HOUSESANITATION, housesanitation.getSelectionId());
        cv.put(House.Sanitation.TOILET, toilet.getSelectionId());
        cv.put(House.Sanitation.WATERASSUAGE, waterassuage.getSelectionId());
        cv.put(House.Sanitation.GARBAGEWARE, garbageware.getSelectionId());
        cv.put(House.Sanitation.GARBAGEERASE, garbageerase.getSelectionId());
        cv.put(House.Sanitation.PETS, pets.getSelectionId());
        cv.put(House.Sanitation.PETSDUNG, petsdung.getSelectionId());
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
