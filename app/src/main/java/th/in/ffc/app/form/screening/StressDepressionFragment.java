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
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfStressDepressionInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepressionLiveData;
import th.in.ffc.app.form.screening.model.StressDepressionInfo;
import th.in.ffc.util.Log;

public class StressDepressionFragment extends Fragment {

    StressDepressionLiveData stressDepressionLiveData;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepressionInfo stressDepressionInfo;
    ArrayList<Integer> points;

    private RadioGroup rdoObesityQ1;
    private RadioGroup rdoObesityQ2;
    private RadioGroup rdoObesityQ3;
    private RadioGroup rdoObesityQ4;
    private RadioGroup rdoObesityQ5;

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
        rdoObesityQ1 = view.findViewById(R.id.rdoObesityQ1);
        rdoObesityQ2 = view.findViewById(R.id.rdoObesityQ2);
        rdoObesityQ3 = view.findViewById(R.id.rdoObesityQ3);
        rdoObesityQ4 = view.findViewById(R.id.rdoObesityQ4);
        rdoObesityQ5 = view.findViewById(R.id.rdoObesityQ5);
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
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stress_depression, container, false);
    }
    private void loadData(){
        SfStressDepressionInfoDao sfStressDepressionInfoDao = new SfStressDepressionInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getStressDepressionLiveDataMutableLiveData().observe(getViewLifecycleOwner(), data -> {
            if(data.getPersonId()!=null){
                List<StressDepressionInfo> stressDepressionInfos = sfStressDepressionInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(StressDepressionInfo stressDepressionInfo :stressDepressionInfos){
                    Log.d("Stress Depression", "Stress Depression info:"+stressDepressionInfo);
                    setStressDepressionInfo(stressDepressionInfo);
                }
            }
        });
    }
    public void setStressDepressionInfo(StressDepressionInfo info) {
        this.stressDepressionInfo = info;
        updateUI();
    }

    private void updateUI() {
        if (stressDepressionInfo == null) return;

        // Set answers from existing data
        setRadioGroupFromAnswer(rdoObesityQ1, stressDepressionInfo.getQ1(), "rdoObesityQ1_");
        setRadioGroupFromAnswer(rdoObesityQ2, stressDepressionInfo.getQ2(), "rdoObesityQ2_");
        setRadioGroupFromAnswer(rdoObesityQ3, stressDepressionInfo.getQ3(), "rdoObesityQ3_");
        setRadioGroupFromAnswer(rdoObesityQ4, stressDepressionInfo.getQ4(), "rdoObesityQ4_");
        setRadioGroupFromAnswer(rdoObesityQ5, stressDepressionInfo.getQ5(), "rdoObesityQ5_");

        calculatePoints();
    }

    private void setRadioGroupFromAnswer(RadioGroup group, String answer, String idPrefix) {
        if (!answer.equals("0")) {
            int radioId = getResources().getIdentifier(
                    idPrefix + answer,
                    "id", requireContext().getPackageName());
            if (radioId != 0) {
                group.check(radioId);
            }
        }
    }
    private void calculatePoints() {
        ArrayList<Integer> points = new ArrayList<>();

        // แปลงค่าคำตอบเป็นคะแนน (0-3 คะแนน)
        points.add(getPointFromAnswer(stressDepressionInfo.getQ1()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ2()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ3()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ4()));
        points.add(getPointFromAnswer(stressDepressionInfo.getQ5()));

        stressDepressionInfo.setPoints(points);

        // คำนวณผลอัตโนมัติ (getSum() จะคำนวณ resultCode และ resultDescription ให้)
        stressDepressionInfo.getSum();
    }
    private int getPointFromAnswer(String answer) {
        // แปลงคำตอบเป็นคะแนน
        switch (answer) {
            case "1": return 0; // เป็นน้อยมากหรือแทบไม่มี
            case "2": return 1; // เป็นบางครั้ง
            case "3": return 2; // เป็นบ่อยครั้ง
            case "4": return 3; // เป็นประจำ
            default: return 0;
        }
    }

}