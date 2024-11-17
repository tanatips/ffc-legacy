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
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression2qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression2qFragment extends Fragment {

    private StressDepression2qLiveData stressDepression2qLiveData;
    private SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    private StressDepression2qInfo stressDepression2qInfo;

    private ArrayList<Integer> points;


    public StressDepression2qFragment() {
        // Required empty public constructor
    }


    public static StressDepression2qFragment newInstance(String param1, String param2) {
        StressDepression2qFragment fragment = new StressDepression2qFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stressDepression2qLiveData = new StressDepression2qLiveData();
        shareViewModel = new SharedViewModel();
        stressDepression2qInfo = new StressDepression2qInfo();
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0,0));
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
        RadioGroup rdoStress2qQ1 = view.findViewById(R.id.rdoStress2qQ1);
        RadioGroup rdoStress2qQ2 = view.findViewById(R.id.rdoStress2qQ2);
        rdoStress2qQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress2qQ1_1) {
                    data= "1";
                    points.set(0,0);
                } else if (checkedId == R.id.rdoStress2qQ1_2) {
                    data= "2";
                    points.set(0,1);
                }
                stressDepression2qLiveData.setSelectedQ1(checkedId);
                shareViewModel.setStressDepression2qLiveData(stressDepression2qLiveData);

                stressDepression2qInfo.setQ1(data);
                stressDepression2qInfo.setPoints(points);
                dataPasser.onStressDepression2q(stressDepression2qInfo);
            }
        });

        rdoStress2qQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                String data="";
                if (checkedId == R.id.rdoStress2qQ2_1) {
                    data= "1";
                    points.set(1,0);
                } else if (checkedId == R.id.rdoStress2qQ2_2) {
                    data= "2";
                    points.set(1,1);
                }
                stressDepression2qLiveData.setSelectedQ2(checkedId);
                shareViewModel.setStressDepression2qLiveData(stressDepression2qLiveData);

                stressDepression2qInfo.setQ2(data);
                stressDepression2qInfo.setPoints(points);
                dataPasser.onStressDepression2q(stressDepression2qInfo);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stress_depression2q, container, false);
    }
}