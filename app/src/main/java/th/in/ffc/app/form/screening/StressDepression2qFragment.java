package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.AssessmentOfObesityLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression2qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression2qFragment extends Fragment {

    StressDepression2qLiveData stressDepression2qLiveData;
    SharedViewModel shareViewModel;

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
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression2qLiveData.setSelectedQ1(i);
                shareViewModel.setStressDepression2qLiveData(stressDepression2qLiveData);
            }
        });

        rdoStress2qQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression2qLiveData.setSelectedQ2(i);
                shareViewModel.setStressDepression2qLiveData(stressDepression2qLiveData);
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