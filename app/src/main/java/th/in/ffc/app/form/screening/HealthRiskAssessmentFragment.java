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
import th.in.ffc.app.form.screening.dao.SfHealthRiskAssessmentInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.datalive.HealthRiskAssessmentLiveData;
import th.in.ffc.app.form.screening.datalive.SmookingLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HealthRiskAssessmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HealthRiskAssessmentFragment extends Fragment {

    SharedViewModel shareViewModel;

    HealthRiskAssessmentLiveData healthRiskAssessmentLiveData;

    private OnDataPass dataPasser;
    private HealthRiskAssessmentInfo healthRiskAssessmentInfo;

    public HealthRiskAssessmentFragment() {
        // Required empty public constructor
    }


    public static HealthRiskAssessmentFragment newInstance(String param1, String param2) {
        HealthRiskAssessmentFragment fragment = new HealthRiskAssessmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        shareViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        if (shareViewModel == null) {
             healthRiskAssessmentLiveData =new HealthRiskAssessmentLiveData();
            shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);
        }
        healthRiskAssessmentLiveData = new HealthRiskAssessmentLiveData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_health_risk_assessment, container, false);
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

        healthRiskAssessmentInfo = new HealthRiskAssessmentInfo();
        RadioGroup rdoHealthRiskQ1 = view.findViewById(R.id.rdoHealthRiskQ1);
        RadioGroup rdoHealthRiskQ2 = view.findViewById(R.id.rdoHealthRiskQ2);
        RadioGroup rdoHealthRiskQ3 = view.findViewById(R.id.rdoHealthRiskQ3);
        RadioGroup rdoHealthRiskQ4 = view.findViewById(R.id.rdoHealthRiskQ4);
        RadioGroup rdoHealthRiskQ5 = view.findViewById(R.id.rdoHealthRiskQ5);
        RadioGroup rdoHealthRiskQ6 = view.findViewById(R.id.rdoHealthRiskQ6);
        rdoHealthRiskQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoHealthRiskQ1_1) {
                    data= "1";
                } else if (checkedId == R.id.rdoHealthRiskQ1_2) {
                    data= "2";
                } else if (checkedId == R.id.rdoHealthRiskQ1_3) {
                    data= "3";
                } else if (checkedId == R.id.rdoHealthRiskQ1_4) {
                    data= "4";
                }
                healthRiskAssessmentLiveData.setSelectHealthRiskQ1(checkedId);
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

                healthRiskAssessmentInfo.setHealthRiskQ1(data);
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }
        });
        rdoHealthRiskQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoHealthRiskQ2_1) {
                    data= "1";
                } else if (checkedId == R.id.rdoHealthRiskQ2_2) {
                    data= "2";
                }
                healthRiskAssessmentLiveData.setSelectHealthRiskQ2(checkedId);
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

                healthRiskAssessmentInfo.setHealthRiskQ2(data);
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }
        });

        rdoHealthRiskQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoHealthRiskQ3_1) {
                    data= "1";
                } else if (checkedId == R.id.rdoHealthRiskQ3_2) {
                    data= "2";
                } else if (checkedId == R.id.rdoHealthRiskQ3_3) {
                    data= "3";
                }
                healthRiskAssessmentLiveData.setSelectHealthRiskQ3(checkedId);
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

                healthRiskAssessmentInfo.setHealthRiskQ3(data);
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }
        });

        rdoHealthRiskQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoHealthRiskQ4_1) {
                    data= "1";
                } else if (checkedId == R.id.rdoHealthRiskQ4_2) {
                    data= "2";
                }
                healthRiskAssessmentLiveData.setSelectHealthRiskQ4(checkedId);
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

                healthRiskAssessmentInfo.setHealthRiskQ4(data);
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }
        });

        rdoHealthRiskQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoHealthRiskQ5_1) {
                    data= "1";
                } else if (checkedId == R.id.rdoHealthRiskQ5_2) {
                    data= "2";
                }
                healthRiskAssessmentLiveData.setSelectHealthRiskQ5(checkedId);
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

                healthRiskAssessmentInfo.setHealthRiskQ5(data);
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }
        });

        rdoHealthRiskQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoHealthRiskQ6_1) {
                    data= "1";
                } else if (checkedId == R.id.rdoHealthRiskQ6_2) {
                    data= "2";
                }
                healthRiskAssessmentLiveData.setSelectHealthRiskQ6(checkedId);
                shareViewModel.setHealthRiskAssessmentLiveDataMutableLiveData(healthRiskAssessmentLiveData);

                healthRiskAssessmentInfo.setHealthRiskQ6(data);
                dataPasser.onHealthRiskAssessmentInfo(healthRiskAssessmentInfo);
            }
        });
        loadData();
    }
    private void loadData(){
        SfHealthRiskAssessmentInfoDao sfHealthRiskAssessmentInfoDao = new SfHealthRiskAssessmentInfoDao(getContext());

        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getHealthRiskAssessmentLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<HealthRiskAssessmentInfo> healthRiskAssessmentInfos = sfHealthRiskAssessmentInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(HealthRiskAssessmentInfo healthRiskAssessmentInfo1 :healthRiskAssessmentInfos){
                    Log.d("healthRiskAssessmentInfo1 ", "healthRiskAssessmentInfo1 infos:"+healthRiskAssessmentInfo1);
                    setHealthRiskInfo(healthRiskAssessmentInfo1);
                }
            }
        });
    }
    public void setHealthRiskInfo(HealthRiskAssessmentInfo info) {
        this.healthRiskAssessmentInfo = info;
        loadExistingData();
    }

    private void loadExistingData() {
        if (healthRiskAssessmentInfo == null) return;

        // Load Q1 (Age)
        String q1 = healthRiskAssessmentInfo.getHealthRiskQ1();
        if (!q1.equals("0")) {
            int radioButtonId = getResources().getIdentifier(
                    "rdoHealthRiskQ1_" + q1,
                    "id",
                    requireContext().getPackageName()
            );
            RadioButton radioButton = requireView().findViewById(radioButtonId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }

        // Load Q2 (Gender)
        String q2 = healthRiskAssessmentInfo.getHealthRiskQ2();
        if (!q2.equals("0")) {
            int radioButtonId = getResources().getIdentifier(
                    "rdoHealthRiskQ2_" + q2,
                    "id",
                    requireContext().getPackageName()
            );
            RadioButton radioButton = requireView().findViewById(radioButtonId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }

        // Load Q3 (BMI)
        String q3 = healthRiskAssessmentInfo.getHealthRiskQ3();
        if (!q3.equals("0")) {
            int radioButtonId = getResources().getIdentifier(
                    "rdoHealthRiskQ3_" + q3,
                    "id",
                    requireContext().getPackageName()
            );
            RadioButton radioButton = requireView().findViewById(radioButtonId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }

        // Load Q4 (Waist)
        String q4 = healthRiskAssessmentInfo.getHealthRiskQ4();
        if (!q4.equals("0")) {
            int radioButtonId = getResources().getIdentifier(
                    "rdoHealthRiskQ4_" + q4,
                    "id",
                    requireContext().getPackageName()
            );
            RadioButton radioButton = requireView().findViewById(radioButtonId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }

        // Load Q5 (Blood pressure)
        String q5 = healthRiskAssessmentInfo.getHealthRiskQ5();
        if (!q5.equals("0")) {
            int radioButtonId = getResources().getIdentifier(
                    "rdoHealthRiskQ5_"+q5,
                    "id",
                    requireContext().getPackageName()
            );
            RadioButton radioButton = requireView().findViewById(radioButtonId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }

        // Load Q6 (Family history)
        String q6 = healthRiskAssessmentInfo.getHealthRiskQ6();
        if (!q6.equals("0")) {
            int radioButtonId = getResources().getIdentifier(
                    "rdoHealthRiskQ6_"+q6,
                    "id",
                    requireContext().getPackageName()
            );
            RadioButton radioButton = requireView().findViewById(radioButtonId);
            if (radioButton != null) {
                radioButton.setChecked(true);
            }
        }
    }

    public HealthRiskAssessmentInfo getHealthRiskAssessmentInfo() {
        return healthRiskAssessmentInfo;
    }
}