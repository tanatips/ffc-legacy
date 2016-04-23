package th.in.ffc.person.visit;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import th.in.ffc.R;
import th.in.ffc.app.FFCEditActivity;
import th.in.ffc.provider.PersonProvider.VisitScreenspecialdisease;

public class VisitScreenspecialquestionActivity extends FFCEditActivity {


    RadioGroup Q1;
    RadioGroup Q2;
    RadioGroup Q3;
    RadioGroup Q4;
    RadioGroup Q5;
    RadioGroup Q6;
    RadioGroup Q7;
    RadioGroup Q8;
    RadioGroup Q9;
    RadioGroup Q10;
    RadioGroup Q11;
    LinearLayout QContainer;
    TextView tSum;
    TextView tMeaning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        share_visitno = getIntent().getExtras().getString(VisitScreenspecialdisease.VISITNO);

        System.out.println(share_visitno);
        System.out.println(getPcuCode());

        setContentView(R.layout.visit_screen2q9q_fragment);


        tSum = (TextView) findViewById(R.id.score1);
        tMeaning = (TextView) findViewById(R.id.score1_1);
        setContentDisplay();
    }


    private void setContentDisplay() {
        QContainer = (LinearLayout) findViewById(R.id.nineQuestion);
        QContainer.setVisibility(View.GONE);
        Q1 = (RadioGroup) findViewById(R.id.answer1);
        Q2 = (RadioGroup) findViewById(R.id.answer2);
        Q3 = (RadioGroup) findViewById(R.id.answer3);
        Q4 = (RadioGroup) findViewById(R.id.answer4);
        Q5 = (RadioGroup) findViewById(R.id.answer5);
        Q6 = (RadioGroup) findViewById(R.id.answer6);
        Q7 = (RadioGroup) findViewById(R.id.answer7);
        Q8 = (RadioGroup) findViewById(R.id.answer8);
        Q9 = (RadioGroup) findViewById(R.id.answer9);
        Q10 = (RadioGroup) findViewById(R.id.answer10);
        Q11 = (RadioGroup) findViewById(R.id.answer11);

        Q1.setOnCheckedChangeListener(RGlistener);
        Q2.setOnCheckedChangeListener(RGlistener);

        setContentQuery(VisitScreenspecialdisease.CONTENT_URI, new String[]{
                        VisitScreenspecialdisease.DEPRESSED,
                        VisitScreenspecialdisease.FEDUP, VisitScreenspecialdisease.Q91,
                        VisitScreenspecialdisease.Q92, VisitScreenspecialdisease.Q93,
                        VisitScreenspecialdisease.Q94, VisitScreenspecialdisease.Q95,
                        VisitScreenspecialdisease.Q96, VisitScreenspecialdisease.Q97,
                        VisitScreenspecialdisease.Q98, VisitScreenspecialdisease.Q99},
                "visitno =? AND pcucode = ?", new String[]{share_visitno, getPcuCode()},
                VisitScreenspecialdisease.DEFAULT_SORTING);
        if (cursorChecker == true) {
            setContentForUpdate();
        }

        Q3.setOnCheckedChangeListener(Q9Listener);
        Q4.setOnCheckedChangeListener(Q9Listener);
        Q5.setOnCheckedChangeListener(Q9Listener);
        Q6.setOnCheckedChangeListener(Q9Listener);
        Q7.setOnCheckedChangeListener(Q9Listener);
        Q8.setOnCheckedChangeListener(Q9Listener);
        Q9.setOnCheckedChangeListener(Q9Listener);
        Q10.setOnCheckedChangeListener(Q9Listener);
        Q11.setOnCheckedChangeListener(Q9Listener);

    }

    OnCheckedChangeListener RGlistener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            if (Q1.getCheckedRadioButtonId() == R.id.rad1 || Q2.getCheckedRadioButtonId() == R.id.rad1) {
                QContainer.setVisibility(View.VISIBLE);
            } else {
                QContainer.setVisibility(View.GONE);
            }
        }
    };

    OnCheckedChangeListener Q9Listener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            calculateScore(Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10, Q11);
        }
    };

    private void setContentForUpdate() {
        // TODO Auto-generated method stub
        setCheckRadio(array[0], Q1);
        setCheckRadio(array[1], Q2);
        setCheckRadio(array[2], Q3);
        setCheckRadio(array[3], Q4);
        setCheckRadio(array[4], Q5);
        setCheckRadio(array[5], Q6);
        setCheckRadio(array[6], Q7);
        setCheckRadio(array[7], Q8);
        setCheckRadio(array[8], Q9);
        setCheckRadio(array[9], Q10);
        setCheckRadio(array[10], Q11);

        calculateScore(Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10, Q11);
    }

    private void setCheckRadio(String data, RadioGroup Rg) {
        if (TextUtils.isEmpty(data) || data.equals("-")) {
            Rg.check(R.id.rad1);
        } else {
            int select = Integer.parseInt(data);
            switch (select) {
                case 1:
                    Rg.check(R.id.rad1);
                    break;
                case 2:
                    Rg.check(R.id.rad2);
                    break;
                case 3:
                    Rg.check(R.id.rad3);
                    break;
                default:
                    Rg.check(R.id.rad1);
                    break;
            }
        }
    }

    @Override
    protected void Delete() {
        // TODO Auto-generated method stub
    }

    @Override
    protected void Edit() {
        // TODO Auto-generated method stub

    }

    public void calculateScore(RadioGroup... Rg) {
        int sum = 0;
        for (RadioGroup r : Rg) {
            int score = 0;
            int check = r.getCheckedRadioButtonId();
            switch (check) {
                case R.id.rad1:
                    score = 0;
                    break;
                case R.id.rad2:
                    score = 1;
                    break;
                case R.id.rad3:
                    score = 2;
                    break;
            }
            sum += score;
        }
        tSum.setText("" + sum);

        if (sum < 7) {
            tMeaning.setText("ไม่น่าจะเป็น");
        } else if (sum < 13) {
            tMeaning.setText("ซึมเศร้า (ระดับน้อย)");
        } else if (sum < 19) {
            tMeaning.setText("ซึมเศร้า (ระดับปานกลาง)");
        } else { //> 10{
            tMeaning.setText("ซึมเศร้า (ระดับมาก)");
        }
    }


    @Override
    protected void Update() {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        cv.put("pcucode", getPcuCode());
        cv.put("visitno", share_visitno);
        cv.put(VisitScreenspecialdisease.CODESCREEN, "c01");

        // retrive CodeScreen
        retrieveDataFromRadioGroup(Q1, cv, VisitScreenspecialdisease.DEPRESSED);
        retrieveDataFromRadioGroup(Q2, cv, VisitScreenspecialdisease.FEDUP);
        retrieveDataFromRadioGroup(Q3, cv, VisitScreenspecialdisease.Q91);
        retrieveDataFromRadioGroup(Q4, cv, VisitScreenspecialdisease.Q92);
        retrieveDataFromRadioGroup(Q5, cv, VisitScreenspecialdisease.Q93);
        retrieveDataFromRadioGroup(Q6, cv, VisitScreenspecialdisease.Q94);
        retrieveDataFromRadioGroup(Q7, cv, VisitScreenspecialdisease.Q95);
        retrieveDataFromRadioGroup(Q8, cv, VisitScreenspecialdisease.Q96);
        retrieveDataFromRadioGroup(Q9, cv, VisitScreenspecialdisease.Q97);
        retrieveDataFromRadioGroup(Q10, cv, VisitScreenspecialdisease.Q98);
        retrieveDataFromRadioGroup(Q11, cv, VisitScreenspecialdisease.Q99);
        doCommit(cv, VisitScreenspecialdisease.CONTENT_URI, true);

    }
}
