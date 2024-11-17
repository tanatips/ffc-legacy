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

import java.util.ArrayList;
import java.util.Arrays;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.StressDepressionLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;

public class StressDepressionFragment extends Fragment {

    StressDepressionLiveData stressDepressionLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepressionInfo stressDepressionInfo;
    ArrayList<Integer> points;

    public StressDepressionFragment() {
        // Required empty public constructor
    }

    public static StressDepressionFragment newInstance(String param1, String param2) {
        StressDepressionFragment fragment = new StressDepressionFragment();
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stressDepressionLiveData = new StressDepressionLiveData();
        shareViewModel = new SharedViewModel();
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0,0,0,0,0,0));
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
        RadioGroup rdoObesityQ1 = view.findViewById(R.id.rdoObesityQ1);
        RadioGroup rdoObesityQ2 = view.findViewById(R.id.rdoObesityQ2);
        RadioGroup rdoObesityQ3 = view.findViewById(R.id.rdoObesityQ3);
        RadioGroup rdoObesityQ4 = view.findViewById(R.id.rdoObesityQ4);
        RadioGroup rdoObesityQ5 = view.findViewById(R.id.rdoObesityQ5);
        stressDepressionInfo = new StressDepressionInfo();
        rdoObesityQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ1(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);

                String data = "";
                if(R.id.rdoObesityQ1_1 == i) {
                    data = "1";
                    points.set(0,0);
                }
                else if(R.id.rdoObesityQ1_2 == i) {
                    data = "2";
                    points.set(0,1);
                }
                else if(R.id.rdoObesityQ1_3 == i) {
                    data = "3";
                    points.set(0,2);
                }
                else if(R.id.rdoObesityQ1_4 == i) {
                    data = "4";
                    points.set(0,3);
                }
                stressDepressionInfo.setQ1(data);
                stressDepressionInfo.setPoints(points);
                dataPasser.onStressDepression(stressDepressionInfo);
            }
        });
        rdoObesityQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ2(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ2_1 == i) {
                    data = "1";
                    points.set(1,0);
                }
                else if(R.id.rdoObesityQ2_2 == i) {
                    data = "2";
                    points.set(1,1);
                }
                else if(R.id.rdoObesityQ2_3 == i) {
                    data = "3";
                    points.set(1,2);
                }
                else if(R.id.rdoObesityQ2_4 == i) {
                    data = "4";
                    points.set(1,3);
                }
                stressDepressionInfo.setQ2(data);
                stressDepressionInfo.setPoints(points);
                dataPasser.onStressDepression(stressDepressionInfo);
            }
        });

        rdoObesityQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ3(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ3_1 == i) {
                    data = "1";
                    points.set(2,0);
                }
                else if(R.id.rdoObesityQ3_2 == i) {
                    data = "2";
                    points.set(2,1);
                }
                else if(R.id.rdoObesityQ3_3 == i) {
                    data = "3";
                    points.set(2,2);
                }
                else if(R.id.rdoObesityQ3_4 == i) {
                    data = "4";
                    points.set(2,3);
                }
                stressDepressionInfo.setQ3(data);
                stressDepressionInfo.setPoints(points);
                dataPasser.onStressDepression(stressDepressionInfo);
            }
        });

        rdoObesityQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ4(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ4_1 == i) {
                    data = "1";
                    points.set(3,0);
                }
                else if(R.id.rdoObesityQ4_2 == i) {
                    data = "2";
                    points.set(3,1);
                }
                else if(R.id.rdoObesityQ4_3 == i) {
                    data = "3";
                    points.set(3,2);
                }
                else if(R.id.rdoObesityQ4_4 == i) {
                    data = "4";
                    points.set(3,3);
                }
                stressDepressionInfo.setQ4(data);
                stressDepressionInfo.setPoints(points);
                dataPasser.onStressDepression(stressDepressionInfo);
            }
        });

        rdoObesityQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepressionLiveData.setSelectedQ5(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(stressDepressionLiveData);
                String data = "";
                if(R.id.rdoObesityQ5_1 == i) {
                    data = "1";
                    points.set(4,0);
                }
                else if(R.id.rdoObesityQ5_2 == i) {
                    data = "2";
                    points.set(4,1);
                }
                else if(R.id.rdoObesityQ5_3 == i) {
                    data = "3";
                    points.set(4,2);
                }
                else if(R.id.rdoObesityQ5_4 == i) {
                    data = "4";
                    points.set(4,3);
                }
                stressDepressionInfo.setQ5(data);
                stressDepressionInfo.setPoints(points);
                dataPasser.onStressDepression(stressDepressionInfo);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stress_depression, container, false);
    }
}