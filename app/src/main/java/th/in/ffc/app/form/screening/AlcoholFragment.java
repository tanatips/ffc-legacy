package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.datalive.DrinkingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlcoholFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlcoholFragment extends Fragment {

    DrinkingLiveData drinkingLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private DrinkingInfo drinkingInfo;
    private RadioGroup rdoDrinking;
    private RadioGroup rdoDrinkingFrequency;
    private RadioGroup rdoDrinkingAlway;
    RadioButton rdoDrinkingFrequency1;
    RadioButton rdoDrinkingFrequency2;
    RadioButton rdoDrinkingFrequency3;

    public AlcoholFragment() {
        // Required empty public constructor
    }
    private void clearNestedSelections() {
        rdoDrinkingFrequency.clearCheck();
        rdoDrinkingAlway.clearCheck();
        drinkingInfo.setDrinkingFrequency("0");
        drinkingInfo.setDrinkingAlway("0");
    }

    private void clearAlwaySelection() {
        rdoDrinkingAlway.clearCheck();
        drinkingInfo.setDrinkingAlway("0");
    }
    private void initializeViews(View view) {
        rdoDrinking = view.findViewById(R.id.rdoDrinking);
        rdoDrinkingFrequency = view.findViewById(R.id.rdoDrinkingFrequency);
        rdoDrinkingAlway = view.findViewById(R.id.rdoDrinkingAlway);
        rdoDrinkingFrequency1 = view.findViewById(R.id.rdoDrinkingFrequency1);
        rdoDrinkingFrequency2 = view.findViewById(R.id.rdoDrinkingFrequency2);
        rdoDrinkingFrequency3 = view.findViewById(R.id.rdoDrinkingFrequency3);
        // Initially disable nested groups
        //  rdoDrinkingFrequency.setVisibility(View.GONE);
        //  rdoDrinkingAlway.setVisibility(View.GONE);
    }
    public void setDrinkingInfo(DrinkingInfo info) {
        this.drinkingInfo = info;
        loadExistingData();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }

    public static AlcoholFragment newInstance(String param1, String param2) {
        AlcoholFragment fragment = new AlcoholFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        drinkingLiveData = new DrinkingLiveData();
        shareViewModel = new SharedViewModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alcohol, container, false);
    }
    private void loadData(){
        SfDrinkingInfoDao sfDrinkingInfoDao = new SfDrinkingInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getPersonInfoLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getId()!=null){
                List<DrinkingInfo> drinkingInfos = sfDrinkingInfoDao.getByPersonId(Integer.valueOf(data.getId()));
                for(DrinkingInfo drinkingInfo :drinkingInfos){
//                    setDataToViews(smokerInfo);
                    Log.d("alcohol ", "drinking infos:"+drinkingInfo);
                    this.drinkingInfo = drinkingInfo;
                    loadExistingData();
                }


            }
        });
    }
    private void loadExistingData() {
        if (this.drinkingInfo == null) return;

        // Set drinking selection
        switch (drinkingInfo.getDrinking()) {
            case "1":
                ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking1)).setChecked(true);
                break;
            case "2":
                ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking2)).setChecked(true);
                break;
            case "3":
                ((RadioButton)rdoDrinking.findViewById(R.id.rdoDrinking3)).setChecked(true);
                // Load nested selections only if "3" is selected
                if (drinkingInfo.getDrinkingFrequency() != null) {
                    switch (drinkingInfo.getDrinkingFrequency()) {
                        case "1":
                            ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency1)).setChecked(true);
                            break;
                        case "2":
                            ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency2)).setChecked(true);
                            break;
                        case "3":
                            ((RadioButton)rdoDrinkingFrequency.findViewById(R.id.rdoDrinkingFrequency3)).setChecked(true);
                            // Load "always" selection only if frequency is "3"
                            if (drinkingInfo.getDrinkingAlway() != null) {
                                switch (drinkingInfo.getDrinkingAlway()) {
                                    case "1":
                                        ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway1)).setChecked(true);
                                        break;
                                    case "2":
                                        ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway2)).setChecked(true);
                                        break;
                                    case "3":
                                        ((RadioButton)rdoDrinkingAlway.findViewById(R.id.rdoDrinkingAlway3)).setChecked(true);
                                        break;
                                }
                            }
                            break;
                    }
                }
                break;
        }
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RadioGroup rdoDrinking = view.findViewById(R.id.rdoDrinking);
        RadioGroup rdoDrinkingFrequency = view.findViewById(R.id.rdoDrinkingFrequency);
        RadioButton rdoDrinkingFrequency1 = view.findViewById(R.id.rdoDrinkingFrequency1);
        RadioButton rdoDrinkingFrequency2 = view.findViewById(R.id.rdoDrinkingFrequency2);
        RadioButton rdoDrinkingFrequency3 = view.findViewById(R.id.rdoDrinkingFrequency3);
        RadioGroup rdoDrinkingAlway = view.findViewById(R.id.rdoDrinkingAlway);
        RadioButton rdoDrinkingAlway1 = view.findViewById(R.id.rdoDrinkingAlway1);
        RadioButton rdoDrinkingAlway2 = view.findViewById(R.id.rdoDrinkingAlway2);
        RadioButton rdoDrinkingAlway3 = view.findViewById(R.id.rdoDrinkingAlway3);
//        rdoDrinkingFrequency.setVisibility(View.INVISIBLE);
//        rdoDrinkingAlway.setVisibility(View.INVISIBLE);
        initializeViews(view);
        drinkingInfo = new DrinkingInfo();
        rdoDrinking.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == R.id.rdoDrinking3){
//                    rdoDrinkingFrequency.setVisibility(View.VISIBLE);
//                }
//                else {
//                    rdoDrinkingFrequency.setVisibility(View.INVISIBLE);
//                    rdoDrinkingFrequency1.setChecked(false);
//                    rdoDrinkingFrequency2.setChecked(false);
//                    rdoDrinkingFrequency3.setChecked(false);
//                }
                drinkingLiveData.setSelectedRdoDriking(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);
                String data = "";
                if(checkedId == R.id.rdoDrinking1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoDrinking2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinking3)
                {
                    data = "3";
                }
                drinkingInfo.setDrinking(data);
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        });
        rdoDrinkingFrequency.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == R.id.rdoDrinkingFrequency3){
//                    rdoDrinkingAlway.setVisibility(View.VISIBLE);
//                }
//                else {
//                    rdoDrinkingAlway.setVisibility(View.INVISIBLE);
//                    rdoDrinkingAlway1.setChecked(false);
//                    rdoDrinkingAlway2.setChecked(false);
//                    rdoDrinkingAlway3.setChecked(false);
//                }
                drinkingLiveData.setSelectedRdoDrikingFrequency(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);
                String data = "";
                if(checkedId == R.id.rdoDrinkingFrequency1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoDrinkingFrequency2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinkingFrequency3)
                {
                    data = "3";
                }
                drinkingInfo.setDrinkingFrequency(data);
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        });

        rdoDrinkingAlway.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                drinkingLiveData.setSelectedRdoDrikingAlway(checkedId);
                shareViewModel.setDrinkingMutableLiveData(drinkingLiveData);
                String data = "";
                if(checkedId == R.id.rdoDrinkingAlway1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoDrinkingAlway2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoDrinkingAlway3)
                {
                    data = "3";
                }
                drinkingInfo.setDrinkingAlway(data);
                dataPasser.onDrinkingInfo(drinkingInfo);
            }
        });

        shareViewModel.getDrinkingMutableLiveData().observe(getViewLifecycleOwner(), drinkingLiveData -> {
            if(drinkingLiveData != null){
                if(drinkingLiveData.getSelectedRdoDriking()!=null) {
                    rdoDrinking.check(drinkingLiveData.getSelectedRdoDriking());
                }
                if(drinkingLiveData.getSelectedRdoDrikingFrequency()!=null) {
                    rdoDrinkingFrequency.check(drinkingLiveData.getSelectedRdoDrikingFrequency());
                }
                if(drinkingLiveData.getSelectedRdoDrikingAlway()!=null) {
                    rdoDrinkingAlway.check(drinkingLiveData.getSelectedRdoDrikingAlway());
                }
            }
        });
        loadData();
    }

    public static class StressDepression9qLiveDataModel  extends ViewModel {
        public StressDepression9qLiveData getStressDepression9qLiveData() {
            return stressDepression9qLiveData;
        }

        public void setStressDepression9qLiveData(StressDepression9qLiveData stressDepression9qLiveData) {
            this.stressDepression9qLiveData = stressDepression9qLiveData;
        }

        StressDepression9qLiveData stressDepression9qLiveData;

    }
}