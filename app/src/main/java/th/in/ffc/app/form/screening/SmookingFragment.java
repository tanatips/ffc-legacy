package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmookingFragment extends Fragment {


    SharedViewModel shareViewModel;

    SmookingLiveData smookingLiveData;
    public SmookingFragment() {
        // Required empty public constructor
    }

    public static SmookingFragment newInstance(String param1, String param2) {
        SmookingFragment fragment = new SmookingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (shareViewModel == null) {
            StressDepression9qLiveData stressDepression9qLiveData =new StressDepression9qLiveData();
            shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
        }
        smookingLiveData = new SmookingLiveData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_smooking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup rdoSmokerGroup = view.findViewById(R.id.rdoSmokerGroup);
        RadioGroup rdoSmokerAssist = view.findViewById(R.id.rdoSmokerAssist);
        RadioGroup rdoSmokerRegularly = view.findViewById(R.id.rdoSmokerRegularly);
        rdoSmokerAssist.setVisibility(View.INVISIBLE);
        rdoSmokerRegularly.setVisibility(View.INVISIBLE);
        RadioButton rdoSmokerGroup1 = view.findViewById(R.id.rdoSmokerGroup1);
        RadioButton rdoSmokerGroup2 = view.findViewById(R.id.rdoSmokerGroup2);
        RadioButton rdoSmokerGroup3 = view.findViewById(R.id.rdoSmokerGroup3);
        RadioButton rdoSmokerAssist1 = view.findViewById(R.id.rdoSmokerAssist1);
        RadioButton rdoSmokerAssist2 = view.findViewById(R.id.rdoSmokerAssist2);
        RadioButton rdoSmokerAssist3 = view.findViewById(R.id.rdoSmokerAssist3);
        RadioButton rdoSmokerRegularly1 = view.findViewById(R.id.rdoSmokerRegularly1);
        RadioButton rdoSmokerRegularly2 = view.findViewById(R.id.rdoSmokerRegularly2);
        RadioButton rdoSmokerRegularly3 = view.findViewById(R.id.rdoSmokerRegularly3);

        rdoSmokerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.rdoSmokerGroup3){
                    rdoSmokerAssist.setVisibility(View.VISIBLE);
                }
                else {
                    rdoSmokerAssist.setVisibility(View.INVISIBLE);
                    rdoSmokerAssist1.setChecked(false);
                    rdoSmokerAssist2.setChecked(false);
                    rdoSmokerAssist3.setChecked(false);
                }
                smookingLiveData.setSelectedRdoSmokerGroup(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);
            }
        });
        rdoSmokerAssist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.rdoSmokerAssist3){
                    rdoSmokerRegularly.setVisibility(View.VISIBLE);
                }
                else {
                    rdoSmokerRegularly.setVisibility(View.INVISIBLE);
                    rdoSmokerRegularly1.setChecked(false);
                    rdoSmokerRegularly2.setChecked(false);
                    rdoSmokerRegularly3.setChecked(false);
                }
                smookingLiveData.setSelectedRdoSmokerAssist(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);
            }
        });

        rdoSmokerRegularly.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                smookingLiveData.setSelectedRdoSmokerRegularly(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);
            }
        });

        shareViewModel.getSmookingMutableLiveData().observe(getViewLifecycleOwner(), smookingLiveData -> {
            if(smookingLiveData != null){
                if(smookingLiveData.getSelectedRdoSmokerGroup()!=null) {
                    rdoSmokerGroup.check(smookingLiveData.getSelectedRdoSmokerGroup());
                }
                if(smookingLiveData.getSelectedRdoSmokerAssist()!=null) {
                    rdoSmokerAssist.check(smookingLiveData.getSelectedRdoSmokerAssist());
                }
                if(smookingLiveData.getSelectedRdoSmokerRegularly()!=null) {
                    rdoSmokerRegularly.check(smookingLiveData.getSelectedRdoSmokerRegularly());
                }
            }
        });
    }
}