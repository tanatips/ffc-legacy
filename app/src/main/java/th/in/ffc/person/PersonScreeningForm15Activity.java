package th.in.ffc.person;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import th.in.ffc.R;
import th.in.ffc.SmartCardReaderActivity;
import th.in.ffc.util.ThaiDatePicker;

public class PersonScreeningForm15Activity extends AppCompatActivity {

    int SMART_CARD_READER_CODE = 0;
    EditText citizenId, fname,lname ;
    ThaiDatePicker birthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_screening_form15);
        Button smartcard_reader = (Button) findViewById(R.id.smartcard_reader);
        citizenId  =  (EditText) findViewById(R.id.citizenId);
        fname  =  (EditText) findViewById(R.id.fname);
        lname  =  (EditText) findViewById(R.id.lname);
        birthday = (ThaiDatePicker) findViewById(R.id.birthday);
        smartcard_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SmartCardReaderActivity.class);
                startActivityForResult(intent,SMART_CARD_READER_CODE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SMART_CARD_READER_CODE ) {
            if (resultCode == Activity.RESULT_OK) {
                byte[] byteArray = data.getByteArrayExtra("image");
                String strIdcard = data.getStringExtra("result");
                if (byteArray != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//                    imgPerson.setImageBitmap(bitmap);
                }
                if(strIdcard!=null){
                    String[] idcardInfo = strIdcard.split("#");
                    citizenId.setText(idcardInfo[0].toString());
                    fname.setText(idcardInfo[2].toString());
                    lname.setText(idcardInfo[4].toString());

                    int day,month,year;
                    year = Integer.parseInt(idcardInfo[18].substring(0,4))-543;
                    month = Integer.parseInt(idcardInfo[18].substring(4,6))-1;
                    day = Integer.parseInt(idcardInfo[18].substring(6,8));
                    birthday.updateDate(year , month, day);
//                    f.hno.setText(idcardInfo[9].toString());
//                    if(idcardInfo[1].toString().equals("นาย")) {
//                        f.sex.findViewById(R.id.male).setActivated(true);
//                    } else {
//                        f.sex.findViewById(R.id.female).setActivated(true);
//                    }
//                    String[] prenameArray = getResources().getStringArray(R.array.prename);
//                    String defaultValue = idcardInfo[1];
//                    int defaultPosition = -1;
//                    for (int i = 0; i < prenameArray.length; i++) {
//                        if (prenameArray[i].contains(defaultValue)) {
//                            defaultPosition = i;
//                            break;
//                        }
//                    }
//                    int day,month,year;
//                    year = Integer.parseInt(idcardInfo[18].substring(0,4))-543;
//                    month = Integer.parseInt(idcardInfo[18].substring(4,6))-1;
//                    day = Integer.parseInt(idcardInfo[18].substring(6,8));
//                    f.birthday.updateDate(year , month, day);
//                    f.prename.setSelection(defaultPosition);

//                    for(PersonDetailEditActivity.MyItem item: provinces){
//                        if(item.nane.equals(idcardInfo[16])){
//                            f.provcode.setSelectionById(item.id);
//                            System.out.println("Province ID: " + item.id + ", Name: " + item.nane);
//                            break;
//                        }
//                    }
//
//                    for(PersonDetailEditActivity.MyItem item: districts){
//                        if(item.nane.equals(idcardInfo[16])){
//                            f.distcode.setSelectionById(item.id);
//                            System.out.println("District ID: " + item.id + ", Name: " + item.nane);
//                            break;
//                        }
//                    }
                }
            }
        }
    }
}