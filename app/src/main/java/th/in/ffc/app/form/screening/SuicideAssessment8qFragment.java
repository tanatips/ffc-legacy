package th.in.ffc.app.form.screening;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.datalive.SuicideAssessment8qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.app.form.screening.model.SuicideAssessment8qInfo;

public class SuicideAssessment8qFragment extends Fragment {

    SuicideAssessment8qLiveData suicideAssessment8qLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private SuicideAssessment8qInfo suicideAssessment8qInfo;
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