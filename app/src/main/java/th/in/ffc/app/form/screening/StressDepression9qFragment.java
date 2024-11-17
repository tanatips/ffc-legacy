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
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression9qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression9qFragment extends Fragment {

    StressDepression9qLiveData stressDepression9qLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepression9qInfo stressDepression9qInfo;

    private ArrayList<Integer> points;
    public StressDepression9qFragment() {
        // Required empty public constructor
    }


    public static StressDepression9qFragment newInstance(String param1, String param2) {
        StressDepression9qFragment fragment = new StressDepression9qFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stressDepression9qLiveData = new StressDepression9qLiveData();
        shareViewModel = new SharedViewModel();
        stressDepression9qInfo = new StressDepression9qInfo();
        points = new ArrayList<>();
        points.addAll(Arrays.asList(
                0,0,0,
                0,0,0,
                0,0,0));
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
        RadioGroup rdoStress9qQ1 = view.findViewById(R.id.rdoStress9qQ1);
        RadioGroup rdoStress9qQ2 = view.findViewById(R.id.rdoStress9qQ2);
        RadioGroup rdoStress9qQ3 = view.findViewById(R.id.rdoStress9qQ3);
        RadioGroup rdoStress9qQ4 = view.findViewById(R.id.rdoStress9qQ4);
        RadioGroup rdoStress9qQ5 = view.findViewById(R.id.rdoStress9qQ5);
        RadioGroup rdoStress9qQ6 = view.findViewById(R.id.rdoStress9qQ6);
        RadioGroup rdoStress9qQ7 = view.findViewById(R.id.rdoStress9qQ7);
        RadioGroup rdoStress9qQ8 = view.findViewById(R.id.rdoStress9qQ8);
        RadioGroup rdoStress9qQ9 = view.findViewById(R.id.rdoStress9qQ9);
        rdoStress9qQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress9qQ1_1) {
                    data= "1";
                    points.set(0,0);
                } else if (checkedId == R.id.rdoStress9qQ1_2) {
                    data= "2";
                    points.set(0,1);
                } else if (checkedId == R.id.rdoStress9qQ1_3) {
                    data= "3";
                    points.set(0,2);
                } else if (checkedId == R.id.rdoStress9qQ1_4) {
                    data= "4";
                    points.set(0,3);
                }
                stressDepression9qInfo.setQ1(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ1(checkedId);

                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);

            }
        });

        rdoStress9qQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress9qQ2_1) {
                    data= "1";
                    points.set(1,0);
                } else if (checkedId == R.id.rdoStress9qQ2_2) {
                    data= "2";
                    points.set(1,1);
                } else if (checkedId == R.id.rdoStress9qQ2_3) {
                    data= "3";
                    points.set(1,2);
                } else if (checkedId == R.id.rdoStress9qQ2_4) {
                    data= "4";
                    points.set(1,3);
                }

                stressDepression9qInfo.setQ2(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ2(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress9qQ3_1) {
                    data= "1";
                    points.set(2,0);
                } else if (checkedId == R.id.rdoStress9qQ3_2) {
                    data= "2";
                    points.set(2,1);
                } else if (checkedId == R.id.rdoStress9qQ3_3) {
                    data= "3";
                    points.set(2,2);
                } else if (checkedId == R.id.rdoStress9qQ3_4) {
                    data= "4";
                    points.set(2,3);
                }

                stressDepression9qInfo.setQ3(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ3(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData); }
        });

        rdoStress9qQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress9qQ4_1) {
                    data= "1";
                    points.set(3,0);
                } else if (checkedId == R.id.rdoStress9qQ4_2) {
                    data= "2";
                    points.set(3,1);
                } else if (checkedId == R.id.rdoStress9qQ4_3) {
                    data= "3";
                    points.set(3,2);
                } else if (checkedId == R.id.rdoStress9qQ4_4) {
                    data= "4";
                    points.set(3,3);
                }

                stressDepression9qInfo.setQ4(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ4(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData); }
        });

        rdoStress9qQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ5_1) {
                    data = "1";
                    points.set(4,0);
                } else if (checkedId == R.id.rdoStress9qQ5_2) {
                    data = "2";
                    points.set(4,1);
                } else if (checkedId == R.id.rdoStress9qQ5_3) {
                    data = "3";
                    points.set(4,2);
                } else if (checkedId == R.id.rdoStress9qQ5_4) {
                    data = "4";
                    points.set(4,3);
                }

                stressDepression9qInfo.setQ5(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ5(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ6_1) {
                    data = "1";
                    points.set(5,0);
                } else if (checkedId == R.id.rdoStress9qQ6_2) {
                    data = "2";
                    points.set(5,1);
                } else if (checkedId == R.id.rdoStress9qQ6_3) {
                    data = "3";
                    points.set(5,2);
                } else if (checkedId == R.id.rdoStress9qQ6_4) {
                    data = "4";
                    points.set(5,3);
                }

                stressDepression9qInfo.setQ6(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ6(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ7_1) {
                    data = "1";
                    points.set(6,0);
                } else if (checkedId == R.id.rdoStress9qQ7_2) {
                    data = "2";
                    points.set(6,1);
                } else if (checkedId == R.id.rdoStress9qQ7_3) {
                    data = "3";
                    points.set(6,2);
                } else if (checkedId == R.id.rdoStress9qQ7_4) {
                    data = "4";
                    points.set(6,3);
                }

                stressDepression9qInfo.setQ7(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ7(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ8_1) {
                    data = "1";
                    points.set(7,0);
                } else if (checkedId == R.id.rdoStress9qQ8_2) {
                    data = "2";
                    points.set(7,1);
                } else if (checkedId == R.id.rdoStress9qQ8_3) {
                    data = "3";
                    points.set(7,2);
                } else if (checkedId == R.id.rdoStress9qQ8_4) {
                    data = "4";
                    points.set(7,3);
                }

                stressDepression9qInfo.setQ8(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ8(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ9.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data = "";
                if (checkedId == R.id.rdoStress9qQ9_1) {
                    data = "1";
                    points.set(8,0);
                } else if (checkedId == R.id.rdoStress9qQ9_2) {
                    data = "2";
                    points.set(8,1);
                } else if (checkedId == R.id.rdoStress9qQ9_3) {
                    data = "3";
                    points.set(8,2);
                } else if (checkedId == R.id.rdoStress9qQ9_4) {
                    data = "4";
                    points.set(8,3);
                }

                stressDepression9qInfo.setQ9(data);
                stressDepression9qInfo.setPoint(points);
                dataPasser.onStressDepression9q(stressDepression9qInfo);

                stressDepression9qLiveData.setSelectedQ9(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);

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
        return inflater.inflate(R.layout.fragment_stress_depression9q, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}