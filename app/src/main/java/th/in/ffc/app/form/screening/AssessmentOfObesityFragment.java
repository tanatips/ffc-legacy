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
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;

public class AssessmentOfObesityFragment extends Fragment {

    AssessmentOfObesityLiveData assessmentOfObesityLiveData;
    SharedViewModel shareViewModel;

    public AssessmentOfObesityFragment() {
        // Required empty public constructor
    }

    public static AssessmentOfObesityFragment newInstance(String param1, String param2) {
        AssessmentOfObesityFragment fragment = new AssessmentOfObesityFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assessmentOfObesityLiveData = new AssessmentOfObesityLiveData();
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
        RadioGroup rdoObesityQ1 = view.findViewById(R.id.rdoObesityQ1);
        RadioGroup rdoObesityQ2 = view.findViewById(R.id.rdoObesityQ2);
        RadioGroup rdoObesityQ3 = view.findViewById(R.id.rdoObesityQ3);
        RadioGroup rdoObesityQ4 = view.findViewById(R.id.rdoObesityQ4);
        RadioGroup rdoObesityQ5 = view.findViewById(R.id.rdoObesityQ5);

        rdoObesityQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                assessmentOfObesityLiveData.setSelectedQ1(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(assessmentOfObesityLiveData);
            }
        });
        rdoObesityQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                assessmentOfObesityLiveData.setSelectedQ2(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(assessmentOfObesityLiveData);
            }
        });

        rdoObesityQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                assessmentOfObesityLiveData.setSelectedQ3(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(assessmentOfObesityLiveData);
            }
        });

        rdoObesityQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                assessmentOfObesityLiveData.setSelectedQ4(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(assessmentOfObesityLiveData);
            }
        });

        rdoObesityQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                assessmentOfObesityLiveData.setSelectedQ5(i);
                shareViewModel.setAssessmentOfObesityLiveDataMutableLiveData(assessmentOfObesityLiveData);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assessment_of_obesity, container, false);
    }
}