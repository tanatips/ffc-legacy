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
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FagerstromNicotineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FagerstromNicotineFragment extends Fragment {

    CigaretteAddictionTestLiveData cigaretteAddictionTest;
    SharedViewModel shareViewModel;

    public FagerstromNicotineFragment() {

    }


    public static FagerstromNicotineFragment newInstance(String param1, String param2) {
        FagerstromNicotineFragment fragment = new FagerstromNicotineFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cigaretteAddictionTest = new CigaretteAddictionTestLiveData();
        shareViewModel = new SharedViewModel();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fagerstrom_nicotine, container, false);
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
        RadioGroup rdoNicotineQ1 = view.findViewById(R.id.rdoNicotineQ1);
        RadioGroup rdoNicotineQ2 = view.findViewById(R.id.rdoNicotineQ2);
        RadioGroup rdoNicotineQ3 = view.findViewById(R.id.rdoNicotineQ3);
        RadioGroup rdoNicotineQ4 = view.findViewById(R.id.rdoNicotineQ4);
        RadioGroup rdoNicotineQ5 = view.findViewById(R.id.rdoNicotineQ5);
        RadioGroup rdoNicotineQ6 = view.findViewById(R.id.rdoNicotineQ6);
        rdoNicotineQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ1(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
            }
        });

        rdoNicotineQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ2(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
            }
        });

        rdoNicotineQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ3(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
            }
        });

        rdoNicotineQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ4(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
            }
        });

        rdoNicotineQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ5(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
            }
        });

        rdoNicotineQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ6(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
            }
        });

    }
}