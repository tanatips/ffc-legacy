package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StressDepression9qFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StressDepression9qFragment extends Fragment {

    StressDepression9qLiveData stressDepression9qLiveData;
    SharedViewModel shareViewModel;
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
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ1(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ2(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ3(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ4(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ5(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ6(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ7(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ8(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

        rdoStress9qQ9.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                stressDepression9qLiveData.setSelectedQ9(i);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
            }
        });

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