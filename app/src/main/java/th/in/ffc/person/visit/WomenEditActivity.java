package th.in.ffc.person.visit;


import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.PersonProvider.Women;
import th.in.ffc.util.DateTime;
import th.in.ffc.widget.ArrayFormatSpinner;

public class WomenEditActivity extends FFCEditActivity {

    // Object
    ArrayFormatSpinner answer1;
    ArrayFormatSpinner answer2;
    EditText answer3;
    TextView omg;

    ArrayAdapter<CharSequence> answer1Adapter;
    ArrayAdapter<CharSequence> answer2Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.women_edit);
        prepareSpinner();
        setContentDisplay();

    }

    private void prepareSpinner() {
        // TODO Auto-generated method stub
        generateFromResource(answer1, answer1Adapter, R.id.answer1,
                R.array.fptype);
        generateFromResource(answer2, answer2Adapter, R.id.answer2,
                R.array.reasonnofp);

    }

    private void setContentDisplay() {
        // TODO Auto-generated method stub
        answer1 = (ArrayFormatSpinner) findViewById(R.id.answer1);
        answer1.setArray(R.array.fptype);
        omg = (TextView) findViewById(R.id.reasonnofp_txt);
        answer2 = (ArrayFormatSpinner) findViewById(R.id.answer2);
        answer2.setArray(R.array.reasonnofp);
        answer3 = (EditText) findViewById(R.id.answer3);
        answer1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                String choice = answer1.getSelectionId();
                answer2.setVisibility(!choice.equals("0") ? View.GONE : View.VISIBLE);
                omg.setVisibility(!choice.equals("0") ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        setContentQuery(Women.CONTENT_URI, new String[]{Women.FPTYPE,
                        Women.REASONNOFP, Women.CHILDALIVE}, "pid = ?",
                new String[]{share_pid}, Women.DEFAULT_SORTING);

        System.out.println("SHARE_HCODE = " + share_hcode);
        setContentForUpdate();
    }

    private void setContentForUpdate() {
        // TODO Auto-generated method stub

        answer1.setSelection(checkStrangerForStringContent(array[0], "0"));
        answer2.setSelection(checkStrangerForStringContent(array[1], "0"));
        answer3.setText(checkEditText(array[2]));


    }

    @Override
    public void Update() {

        ContentValues cv = new ContentValues();

        cv.put(Women.FPTYPE, answer1.getSelectionId());
        if (!(answer1.getSelectionId().equals("0"))) {
            cv.putNull(Women.REASONNOFP);
        } else {
            cv.put(Women.REASONNOFP, answer2.getSelectionId());
        }

        retrieveDataFromEditText(answer3, cv, "childalive");
        updateTimeStamp(cv);
        cv.put(Women.DATESURVEY, DateTime.getCurrentDate());
        if (TextUtils.isEmpty(answer3.getText()))
            Toast.makeText(this, getString(R.string.err_no_child), Toast.LENGTH_SHORT);
        else {
            if (cv != null) {

                ContentResolver cr = getContentResolver();
                int rows = cr.update(
                        Uri.withAppendedPath(Women.CONTENT_URI, share_pid), cv,
                        "pid=" + share_pid, null);
                System.out.println("Updated : " + rows);
                if (rows == 1)
                    System.out.print("Update Success");
                else {
                    System.out.println("Update failed try to INSERT");
                    cv.put("pcucodeperson", getPcuCode());
                    cv.put("pid", share_pid);
                    cr.insert(Women.CONTENT_URI, cv);
                }
                finish();
            }
        }
    }

    @Override
    protected void Delete() {
    }

    @Override
    protected void Edit() {
    }

}
