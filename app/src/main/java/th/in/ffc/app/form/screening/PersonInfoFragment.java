package th.in.ffc.app.form.screening;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import th.in.ffc.R;
import th.in.ffc.SmartCardReaderActivity;
import th.in.ffc.util.ThaiDatePicker;

public class PersonInfoFragment extends Fragment {

    int SMART_CARD_READER_CODE = 0;
    EditText citizenId, fname,lname ;
    ThaiDatePicker birthday;
    RadioGroup rdoGender;

    RadioButton rdoMale,rdoFemale;

    public PersonInfoFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_info, container, false);
        Button smartcard_reader = (Button) view.findViewById(R.id.smartcard_reader);
        citizenId  =  (EditText) view.findViewById(R.id.citizenId);
        fname  =  (EditText) view.findViewById(R.id.fname);
        lname  =  (EditText) view.findViewById(R.id.lname);
        birthday = (ThaiDatePicker) view.findViewById(R.id.birthday);
        rdoGender = (RadioGroup) view.findViewById(R.id.rdoGender);
        rdoMale = (RadioButton) view.findViewById(R.id.rdoMale);
        rdoFemale = (RadioButton) view.findViewById(R.id.rdoFemale);
        smartcard_reader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SmartCardReaderActivity.class);
                activityResultLauncher.launch(intent);
            }
        });
        return view;
    }
    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
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
                    if(idcardInfo[1].toString().equals("นาย")) {
                        rdoMale.setChecked(true);
                    } else {
                        rdoMale.setChecked(true);
                    }
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
    );
}