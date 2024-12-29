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
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfSuicideAssessment8qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;
import th.in.ffc.util.Log;

public class SuicideAssessment8qFragment extends Fragment {

    SuicideAssessment8qLiveData suicideAssessment8qLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private SuicideAssessment8qInfo suicideAssessment8qInfo;

//    private SuicideAssessment8qInfo suicideInfo;
//    private RadioGroup[] mainQuestionGroups;
//    private RadioGroup subQuestionGroup;
    private static final int MAIN_QUESTION_COUNT = 8;
    public SuicideAssessment8qFragment() {
        // Required empty public constructor
    }

    public static SuicideAssessment8qFragment newInstance(String param1, String param2) {
        SuicideAssessment8qFragment fragment = new SuicideAssessment8qFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        suicideAssessment8qLiveData = new SuicideAssessment8qLiveData();
        shareViewModel = new SharedViewModel();
        suicideAssessment8qInfo = new SuicideAssessment8qInfo();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View parentViewPager = (View) view.getParent();
        if (parentViewPager != null) {
            parentViewPager.post(() -> {
                int height = view.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = parentViewPager.getLayoutParams();
                layoutParams.height = height;
                parentViewPager.setLayoutParams(layoutParams);
            });
        }
        RadioGroup rdoSuicideQ1 = view.findViewById(R.id.rdoSuicideQ1);
        RadioGroup rdoSuicideQ2 = view.findViewById(R.id.rdoSuicideQ2);
        RadioGroup rdoSuicideQ3 = view.findViewById(R.id.rdoSuicideQ3);
        RadioGroup rdoSuicideQ3_2_1 = view.findViewById(R.id.rdoSuicideQ3_2_1);
        RadioGroup rdoSuicideQ4 = view.findViewById(R.id.rdoSuicideQ4);
        RadioGroup rdoSuicideQ5 = view.findViewById(R.id.rdoSuicideQ5);
        RadioGroup rdoSuicideQ6 = view.findViewById(R.id.rdoSuicideQ6);
        RadioGroup rdoSuicideQ7 = view.findViewById(R.id.rdoSuicideQ7);
        RadioGroup rdoSuicideQ8 = view.findViewById(R.id.rdoSuicideQ8);
        rdoSuicideQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ1_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ1_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ1(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ1(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });

        rdoSuicideQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ2_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ2_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ2(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ2(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });
        rdoSuicideQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ3_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ3_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ3(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ3(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);

            }
        });

        rdoSuicideQ3_2_1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ3_2_1_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ3_2_1_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ3_2_1(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ3_2_1(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });
        rdoSuicideQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ4_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ4_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ4(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ4(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });
        rdoSuicideQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ5_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ5_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ5(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ5(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });
        rdoSuicideQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ6_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ6_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ6(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ6(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);

            }
        });
        rdoSuicideQ7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ7_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ7_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ7(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ7(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);

            }
        });
        rdoSuicideQ8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                String data = "";
                if (i == R.id.rdoSuicideQ8_1) {
                    data = "1";
                } else if (i == R.id.rdoSuicideQ8_2) {
                    data = "2";
                }
                suicideAssessment8qInfo.setQ8(data);
                dataPasser.onSuicideAssessment8q(suicideAssessment8qInfo);

                suicideAssessment8qLiveData.setSelectedQ8(i);
                shareViewModel.setSuicideAssessment8qMutableLiveData(suicideAssessment8qLiveData);
            }
        });
        loadData();
    }
    private void loadData(){
        SfSuicideAssessment8qInfoDao sfSuicideAssessment8qInfoDao = new SfSuicideAssessment8qInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getSuicideAssessment8qMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<SuicideAssessment8qInfo> suicideAssessment8qInfos = sfSuicideAssessment8qInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(SuicideAssessment8qInfo suicideAssessment8qInfo1 :suicideAssessment8qInfos){
                    Log.d("suicideAssessment8qInfo1 ", "suicideAssessment8qInfo1 infos:"+suicideAssessment8qInfo1);
//                    this.suicideAssessment8qInfo = stressDepression2qInfo;
                    setSuicideAssessment8qInfo(suicideAssessment8qInfo1);
                }
            }
        });
    }
    private void setQuestionValue(int questionNumber, String value) {
        switch (questionNumber) {
            case 1: suicideAssessment8qInfo.setQ1(value); break;
            case 2: suicideAssessment8qInfo.setQ2(value); break;
            case 3: suicideAssessment8qInfo.setQ3(value); break;
            case 4: suicideAssessment8qInfo.setQ4(value); break;
            case 5: suicideAssessment8qInfo.setQ5(value); break;
            case 6: suicideAssessment8qInfo.setQ6(value); break;
            case 7: suicideAssessment8qInfo.setQ7(value); break;
            case 8: suicideAssessment8qInfo.setQ8(value); break;
        }
    }

    public void setSuicideAssessment8qInfo(SuicideAssessment8qInfo info) {
        this.suicideAssessment8qInfo = info;
        loadExistingData();
    }
    private void loadExistingData() {
        if (suicideAssessment8qInfo == null) return;

        // Load main questions
        for (int i = 0; i < MAIN_QUESTION_COUNT; i++) {
            String value = getQuestionValue(i + 1);
            if (!value.equals("0")) {
                int radioButtonId = getResources().getIdentifier(
                        "rdoSuicideQ" + (i + 1) + "_" + (Integer.parseInt(value)),
                        "id",
                        requireContext().getPackageName()
                );
                RadioButton radioButton = requireView().findViewById(radioButtonId);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }
            }
        }

        // Load Q3 sub-question if necessary
        if (suicideAssessment8qInfo.getQ3().equals("1") || suicideAssessment8qInfo.getQ3().equals("2")) {
//            handleQ3Visibility(true);
            String subValue = suicideAssessment8qInfo.getQ3_2_1();
            if (!subValue.equals("0")) {
                RadioButton radioButton = requireView().findViewById(
                        subValue.equals("0") ? R.id.rdoSuicideQ3_2_1_1 : R.id.rdoSuicideQ3_2_1_2
                );
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }
            }
        }
    }

    private String getQuestionValue(int questionNumber) {
        switch (questionNumber) {
            case 1: return suicideAssessment8qInfo.getQ1();
            case 2: return suicideAssessment8qInfo.getQ2();
            case 3: return suicideAssessment8qInfo.getQ3();
            case 4: return suicideAssessment8qInfo.getQ4();
            case 5: return suicideAssessment8qInfo.getQ5();
            case 6: return suicideAssessment8qInfo.getQ6();
            case 7: return suicideAssessment8qInfo.getQ7();
            case 8: return suicideAssessment8qInfo.getQ8();
            default: return "0";
        }
    }

    public SuicideAssessment8qInfo getSuicideAssessment8qInfo() {
        return suicideAssessment8qInfo;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_suicide_assessment8q, container, false);
    }
}