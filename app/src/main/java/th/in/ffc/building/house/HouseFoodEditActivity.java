package th.in.ffc.building.house;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.Menu;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.HouseProvider.House;
import th.in.ffc.widget.ArrayFormatSpinner;

public class HouseFoodEditActivity extends FFCEditActivity {

    // Object
    ArrayFormatSpinner foodcook;
    ArrayFormatSpinner foodkeepsafe;
    ArrayFormatSpinner foodware;
    ArrayFormatSpinner foodwarewash;
    ArrayFormatSpinner foodwarekeep;
    ArrayFormatSpinner foodgarbageware;
    ArrayFormatSpinner foodcookroom;
    ArrayFormatSpinner foodsanitation;
    ArrayFormatSpinner iodeinsalt;
    ArrayFormatSpinner iodeinmaterial;
    ArrayFormatSpinner iodeinuse;
    ArrayFormatSpinner ftlj;
    ArrayFormatSpinner whjrk;
    ArrayFormatSpinner slpp;
    ArrayFormatSpinner cht;
    ArrayFormatSpinner kmch;
    Menu theMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.house_food_edit);
        setContentDisplay();

    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        foodcook = (ArrayFormatSpinner) findViewById(R.id.answer1);
        foodkeepsafe = (ArrayFormatSpinner) findViewById(R.id.answer2);
        foodware = (ArrayFormatSpinner) findViewById(R.id.answer3);
        foodwarewash = (ArrayFormatSpinner) findViewById(R.id.answer4);
        foodwarekeep = (ArrayFormatSpinner) findViewById(R.id.answer5);
        foodgarbageware = (ArrayFormatSpinner) findViewById(R.id.answer6);
        foodcookroom = (ArrayFormatSpinner) findViewById(R.id.answer7);
        foodsanitation = (ArrayFormatSpinner) findViewById(R.id.answer8);
        iodeinsalt = (ArrayFormatSpinner) findViewById(R.id.answer9);
        iodeinmaterial = (ArrayFormatSpinner) findViewById(R.id.answer10);
        iodeinuse = (ArrayFormatSpinner) findViewById(R.id.answer11);
        ftlj = (ArrayFormatSpinner) findViewById(R.id.answer12);
        whjrk = (ArrayFormatSpinner) findViewById(R.id.answer13);
        slpp = (ArrayFormatSpinner) findViewById(R.id.answer14);
        cht = (ArrayFormatSpinner) findViewById(R.id.answer15);
        kmch = (ArrayFormatSpinner) findViewById(R.id.answer16);

        foodcook.setArray(R.array.houseCorrect);
        foodkeepsafe.setArray(R.array.houseCorrect);
        foodware.setArray(R.array.houseCorrect);
        foodwarewash.setArray(R.array.houseCorrect);
        foodwarekeep.setArray(R.array.houseCorrect);
        foodgarbageware.setArray(R.array.houseHave);
        foodcookroom.setArray(R.array.houseHave);
        foodsanitation.setArray(R.array.houseCorrect);
        iodeinsalt.setArray(R.array.houseUse2);
        iodeinmaterial.setArray(R.array.houseUse2);
        iodeinuse.setArray(R.array.houseUse2);
        ftlj.setArray(R.array.houseUse);
        whjrk.setArray(R.array.houseUse);
        slpp.setArray(R.array.houseUse);
        cht.setArray(R.array.houseUse);
        kmch.setArray(R.array.houseUse);

        setContentQuery(House.CONTENT_URI, new String[]{House.Sanitation.FOODCOOK, House.Sanitation.FOODKEEPSAFE,
                House.Sanitation.FOODWARE, House.Sanitation.FOODWAREWASH, House.Sanitation.FOODWAREKEEP, House.Sanitation.FOODGARBAGEWARE,
                House.Sanitation.FOODCOOKROOM, House.Sanitation.FOODSANITATION, House.Sanitation.IODEINSALT, House.Sanitation.IODEINMATERIAL,
                House.Sanitation.IODEINUSE, House.Sanitation.FTLJ, House.Sanitation.WHJRK, House.Sanitation.SLPP,
                House.Sanitation.CHT, House.Sanitation.KMCH}, "hcode = ?", new String[]{share_hcode}, House.DEFAULT_SORTING);

        if (cursorChecker == true) {
            setContentForUpdate();
        }
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        foodcook.setSelection(checkStrangerForStringContent(array[0], "0"));
        foodkeepsafe.setSelection(checkStrangerForStringContent(array[1], "0"));
        foodware.setSelection(checkStrangerForStringContent(array[2], "0"));
        foodwarewash.setSelection(checkStrangerForStringContent(array[3], "0"));
        foodwarekeep.setSelection(checkStrangerForStringContent(array[4], "0"));
        foodgarbageware.setSelection(checkStrangerForStringContent(array[5], "0"));
        foodcookroom.setSelection(checkStrangerForStringContent(array[6], "0"));
        foodsanitation.setSelection(checkStrangerForStringContent(array[7], "0"));
        iodeinsalt.setSelection(checkStrangerForStringContent(array[8], "0"));
        iodeinmaterial.setSelection(checkStrangerForStringContent(array[9], "0"));
        iodeinuse.setSelection(checkStrangerForStringContent(array[10], "0"));
        ftlj.setSelection(checkStrangerForStringContent(array[11], "0"));
        whjrk.setSelection(checkStrangerForStringContent(array[12], "0"));
        slpp.setSelection(checkStrangerForStringContent(array[13], "0"));
        cht.setSelection(checkStrangerForStringContent(array[14], "0"));
        kmch.setSelection(checkStrangerForStringContent(array[15], "0"));

    }

    @Override
    public void Update() {
        ContentValues cv = new ContentValues();

        cv.put(House.Sanitation.FOODCOOK, foodcook.getSelectionId());
        cv.put(House.Sanitation.FOODKEEPSAFE, foodkeepsafe.getSelectionId());
        cv.put(House.Sanitation.FOODWARE, foodware.getSelectionId());
        cv.put(House.Sanitation.FOODWAREWASH, foodwarewash.getSelectionId());
        cv.put(House.Sanitation.FOODWAREKEEP, foodwarekeep.getSelectionId());
        cv.put(House.Sanitation.FOODGARBAGEWARE, foodgarbageware.getSelectionId());
        cv.put(House.Sanitation.FOODCOOKROOM, foodcookroom.getSelectionId());
        cv.put(House.Sanitation.FOODSANITATION, foodsanitation.getSelectionId());
        cv.put(House.Sanitation.IODEINSALT, iodeinsalt.getSelectionId());
        cv.put(House.Sanitation.IODEINMATERIAL, iodeinmaterial.getSelectionId());
        cv.put(House.Sanitation.IODEINUSE, iodeinuse.getSelectionId());
        cv.put(House.Sanitation.FTLJ, ftlj.getSelectionId());
        cv.put(House.Sanitation.WHJRK, whjrk.getSelectionId());
        cv.put(House.Sanitation.SLPP, slpp.getSelectionId());
        cv.put(House.Sanitation.CHT, cht.getSelectionId());
        cv.put(House.Sanitation.KMCH, kmch.getSelectionId());
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
