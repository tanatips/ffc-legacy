package th.in.ffc.app.form.screening;

import android.content.Context;
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

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfPersonInfoDao;
import th.in.ffc.app.form.screening.dao.SfSmokerInfoDao;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SmookingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SmookingFragment extends Fragment {


    SharedViewModel shareViewModel;
    SmookingLiveData smookingLiveData;
    SfSmokerInfoDao sfSmokerInfoDao;

    private OnDataPass dataPasser;
    private SmokerInfo smokerInfo;
    private RadioGroup rdoSmokerGroup;
    private RadioGroup rdoSmokerAssist;
    private RadioGroup rdoSmokerRegularly;
    private RadioButton rdoSmokerGroup1;
    private RadioButton rdoSmokerGroup2;
    private RadioButton rdoSmokerGroup3;
    private RadioButton rdoSmokerAssist1;
    private RadioButton rdoSmokerAssist2;
    private RadioButton rdoSmokerAssist3;
    private RadioButton rdoSmokerRegularly1;
    private RadioButton rdoSmokerRegularly2;
    private RadioButton rdoSmokerRegularly3;
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
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            dataPasser = (OnDataPass) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnDataPass");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        smokerInfo = new SmokerInfo();
        sfSmokerInfoDao = new SfSmokerInfoDao(getContext());
        rdoSmokerGroup = view.findViewById(R.id.rdoSmokerGroup);
        rdoSmokerAssist = view.findViewById(R.id.rdoSmokerAssist);
        rdoSmokerRegularly = view.findViewById(R.id.rdoSmokerRegularly);
//        rdoSmokerAssist.setVisibility(View.INVISIBLE);
//        rdoSmokerRegularly.setVisibility(View.INVISIBLE);
        rdoSmokerGroup1 = view.findViewById(R.id.rdoSmokerGroup1);
        rdoSmokerGroup2 = view.findViewById(R.id.rdoSmokerGroup2);
        rdoSmokerGroup3 = view.findViewById(R.id.rdoSmokerGroup3);
        rdoSmokerAssist1 = view.findViewById(R.id.rdoSmokerAssist1);
        rdoSmokerAssist2 = view.findViewById(R.id.rdoSmokerAssist2);
        rdoSmokerAssist3 = view.findViewById(R.id.rdoSmokerAssist3);
        rdoSmokerRegularly1 = view.findViewById(R.id.rdoSmokerRegularly1);
        rdoSmokerRegularly2 = view.findViewById(R.id.rdoSmokerRegularly2);
        rdoSmokerRegularly3 = view.findViewById(R.id.rdoSmokerRegularly3);

        rdoSmokerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == R.id.rdoSmokerGroup3){
////                    rdoSmokerAssist.setVisibility(View.VISIBLE);
//                }
//                else {
////                    rdoSmokerAssist.setVisibility(View.INVISIBLE);
//                    rdoSmokerAssist1.setChecked(false);
//                    rdoSmokerAssist2.setChecked(false);
//                    rdoSmokerAssist3.setChecked(false);
//                }
                String data = "";
                if(checkedId == R.id.rdoSmokerGroup1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoSmokerGroup2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoSmokerGroup3)
                {
                    data = "3";
                }
                smookingLiveData.setSelectedRdoSmokerGroup(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);

                smokerInfo.setSmokerGroup(data);
                dataPasser.onSmokerInfo(smokerInfo);
            }
        });
        rdoSmokerAssist.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
//                if(checkedId == R.id.rdoSmokerAssist3){
////                    rdoSmokerRegularly.setVisibility(View.VISIBLE);
//                }
//                else {
////                    rdoSmokerRegularly.setVisibility(View.INVISIBLE);
//                    rdoSmokerRegularly1.setChecked(false);
//                    rdoSmokerRegularly2.setChecked(false);
//                    rdoSmokerRegularly3.setChecked(false);
//                }
                String data = "";
                if(checkedId == R.id.rdoSmokerAssist1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoSmokerAssist2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoSmokerAssist3)
                {
                    data = "3";
                }
                smookingLiveData.setSelectedRdoSmokerAssist(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);
                smokerInfo.setSmokerAssist(data);
                dataPasser.onSmokerInfo(smokerInfo);
            }
        });

        rdoSmokerRegularly.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                smookingLiveData.setSelectedRdoSmokerRegularly(checkedId);
                shareViewModel.setSmookingMutableLiveData(smookingLiveData);
                String data = "";
                if(checkedId == R.id.rdoSmokerRegularly1) {
                    data = "1";
                } else  if(checkedId == R.id.rdoSmokerRegularly2)
                {
                    data = "2";
                } else if(checkedId == R.id.rdoSmokerRegularly3)
                {
                    data = "3";
                }
                smokerInfo.setSmokerRegularly(data);
                dataPasser.onSmokerInfo(smokerInfo);
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
        loadData();
    }

    private void loadData(){

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSmookingMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<SmokerInfo> smokerInfos = sfSmokerInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(SmokerInfo smokerInfo :smokerInfos){
//                    setDataToViews(smokerInfo);
                    Log.d("smoker", "smoker infos:"+smokerInfos);
                    setSmokerInfo(smokerInfo);
                }


            }
        });

    }
    private void setDataToViews(SmokerInfo smokerInfo){

    }
    public void setSmokerInfo(SmokerInfo info) {
        this.smokerInfo = info;
        updateUI();
    }
    private void updateUI() {
        if (this.smokerInfo != null) {
            // Set SmokerGroup
            switch (this.smokerInfo.getSmokerGroup()) {
                case "1":
                    rdoSmokerGroup1.setChecked(true);
//                    hideAssistAndRegularly();
                    break;
                case "2":
                    rdoSmokerGroup2.setChecked(true);
//                    hideAssistAndRegularly();
                    break;
                case "3":
                    rdoSmokerGroup3.setChecked(true);
//                    showAssistGroup();

                    // Set SmokerAssist if SmokerGroup is 3
                    switch (this.smokerInfo.getSmokerAssist()) {
                        case "1":
                            rdoSmokerAssist1.setChecked(true);
//                            hideRegularly();
                            break;
                        case "2":
                            rdoSmokerAssist2.setChecked(true);
//                            hideRegularly();
                            break;
                        case "3":
                            rdoSmokerAssist3.setChecked(true);
//                            showRegularly();

                            // Set SmokerRegularly if SmokerAssist is 3
                            switch (this.smokerInfo.getSmokerRegularly()) {
                                case "1":
                                    rdoSmokerRegularly1.setChecked(true);
                                    break;
                                case "2":
                                    rdoSmokerRegularly2.setChecked(true);
                                    break;
                                case "3":
                                    rdoSmokerRegularly3.setChecked(true);
                                    break;
                            }
                            break;
                    }
                    break;
            }
        }
    }

}