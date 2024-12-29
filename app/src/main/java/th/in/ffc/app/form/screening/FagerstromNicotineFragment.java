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
import th.in.ffc.app.form.screening.dao.SfNicotineInfoDao;
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;
import th.in.ffc.app.form.screening.model.SmokerInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FagerstromNicotineFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FagerstromNicotineFragment extends Fragment {

    CigaretteAddictionTestLiveData cigaretteAddictionTest;
    SharedViewModel shareViewModel;

    private OnDataPass dataPasser;
    NicotineInfo nicotineInfo;

    ArrayList<Integer> points;

    private RadioGroup rdoNicotineQ1;
    private RadioGroup rdoNicotineQ2;
    private RadioGroup rdoNicotineQ3;
    private RadioGroup rdoNicotineQ4;
    private RadioGroup rdoNicotineQ5;
    private RadioGroup rdoNicotineQ6;


    public FagerstromNicotineFragment() {

    }


    public static FagerstromNicotineFragment newInstance(String param1, String param2) {
        FagerstromNicotineFragment fragment = new FagerstromNicotineFragment();
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
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0,0,0,0,0,0));

        nicotineInfo = new NicotineInfo();
        rdoNicotineQ1 = view.findViewById(R.id.rdoNicotineQ1);
        rdoNicotineQ2 = view.findViewById(R.id.rdoNicotineQ2);
        rdoNicotineQ3 = view.findViewById(R.id.rdoNicotineQ3);
        rdoNicotineQ4 = view.findViewById(R.id.rdoNicotineQ4);
        rdoNicotineQ5 = view.findViewById(R.id.rdoNicotineQ5);
        rdoNicotineQ6 = view.findViewById(R.id.rdoNicotineQ6);
        rdoNicotineQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ1(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if(R.id.rdoNicotineQ1_1 == i) {
                    data = "1";
                    points.set(0,0);
                }
                else if(R.id.rdoNicotineQ1_2 == i) {
                    data = "2";
                    points.set(0,1);
                }
                else if(R.id.rdoNicotineQ1_3 == i) {
                    data = "3";
                    points.set(0,2);
                }
                else if(R.id.rdoNicotineQ1_4 == i) {
                    data = "4";
                    points.set(0,3);
                }
                nicotineInfo.setNicotine1(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
            }
        });

        rdoNicotineQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ2(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if(R.id.rdoNicotineQ2_1 == i) {
                    data = "1";
                    points.set(1,3);
                }
                else if(R.id.rdoNicotineQ2_2 == i) {
                    data = "2";
                    points.set(1,2);
                }
                else if(R.id.rdoNicotineQ2_3 == i) {
                    data = "3";
                    points.set(1,1);
                }
                else if(R.id.rdoNicotineQ2_4 == i) {
                    data = "4";
                    points.set(1,0);
                }
                nicotineInfo.setNicotine2(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
            }
        });

        rdoNicotineQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ3(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if(R.id.rdoNicotineQ3_1 == i) {
                    data = "1";
                    points.set(2,1);
                }
                else if(R.id.rdoNicotineQ3_2 == i) {
                    data = "2";
                    points.set(2,0);
                }
                nicotineInfo.setNicotine3(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
            }
        });

        rdoNicotineQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ4(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);

                String data = "";
                if(R.id.rdoNicotineQ4_1 == i) {
                    data = "1";
                    points.set(3,1);
                }
                else if(R.id.rdoNicotineQ4_2 == i) {
                    data = "2";
                    points.set(3,0);
                }
                nicotineInfo.setNicotine4(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
            }
        });

        rdoNicotineQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ5(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if(R.id.rdoNicotineQ5_1 == i) {
                    data = "1";
                    points.set(4,1);
                }
                else if(R.id.rdoNicotineQ5_2 == i) {
                    data = "2";
                    points.set(4,0);
                }
                nicotineInfo.setNicotine5(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
            }
        });

        rdoNicotineQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                cigaretteAddictionTest.setSelectedRdoQ6(i);
                shareViewModel.setCigatetteAddictionTestMutableLiveData(cigaretteAddictionTest);
                String data = "";
                if(R.id.rdoNicotineQ6_1 == i) {
                    data = "1";
                    points.set(5,1);
                }
                else if(R.id.rdoNicotineQ6_2 == i) {
                    data = "2";
                    points.set(5,0);
                }
                nicotineInfo.setNicotine6(data);
                nicotineInfo.setPoints(points);
                dataPasser.onNicotineInfo(nicotineInfo);
            }
        });
        loadData();
    }

    private void loadData(){
        SfNicotineInfoDao sfNicotineInfoDao = new SfNicotineInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getCigatetteAddictionTestMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<NicotineInfo> nicotineInfos = sfNicotineInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(NicotineInfo nicotineInfo :nicotineInfos){
                    Log.d("smoker", "smoker infos:"+nicotineInfo);
                    setNicotineInfo(nicotineInfo);
                }
            }
        });
    }
    public void setNicotineInfo(NicotineInfo info) {
        this.nicotineInfo = info;
        updateUI();
    }

    private void updateUI() {
        if (nicotineInfo == null) return;

        // Set Question 1
        if (!nicotineInfo.getNicotine1().equals("0")) {
            int radioId = getResources().getIdentifier(
                    "rdoNicotineQ1_" + nicotineInfo.getNicotine1(),
                    "id", requireContext().getPackageName());
            if (radioId != 0) {
                rdoNicotineQ1.check(radioId);
            }
        }

        // Set Question 2
        if (!nicotineInfo.getNicotine2().equals("0")) {
            int radioId = getResources().getIdentifier(
                    "rdoNicotineQ2_" + nicotineInfo.getNicotine2(),
                    "id", requireContext().getPackageName());
            if (radioId != 0) {
                rdoNicotineQ2.check(radioId);
            }
        }

        // Set Question 3-6
        setRadioGroupValue(rdoNicotineQ3, nicotineInfo.getNicotine3(), "rdoNicotineQ3_");
        setRadioGroupValue(rdoNicotineQ4, nicotineInfo.getNicotine4(), "rdoNicotineQ4_");
        setRadioGroupValue(rdoNicotineQ5, nicotineInfo.getNicotine5(), "rdoNicotineQ5_");
        setRadioGroupValue(rdoNicotineQ6, nicotineInfo.getNicotine6(), "rdoNicotineQ6_");

        calculatePoints();
    }
    private void setRadioGroupValue(RadioGroup group, String value, String idPrefix) {
        if (!value.equals("0")) {
            int radioId = getResources().getIdentifier(
                    idPrefix + value,
                    "id", requireContext().getPackageName());
            if (radioId != 0) {
                group.check(radioId);
            }
        }
    }

    public NicotineInfo getNicotineInfo() {
        return nicotineInfo;
    }
    private void calculatePoints() {
        ArrayList<Integer> points = new ArrayList<>();

        // Question 1: How many cigarettes per day?
        switch (nicotineInfo.getNicotine1()) {
            case "1": points.add(0); break; // 10 or less
            case "2": points.add(1); break; // 11-20
            case "3": points.add(2); break; // 21-30
            case "4": points.add(3); break; // 31 or more
            default: points.add(0);
        }

        // Question 2: Time to first cigarette
        switch (nicotineInfo.getNicotine2()) {
            case "1": points.add(3); break; // Within 5 minutes
            case "2": points.add(2); break; // 6-30 minutes
            case "3": points.add(1); break; // 31-60 minutes
            case "4": points.add(0); break; // After 60 minutes
            default: points.add(0);
        }

        // Question 3: Smoke more in morning?
        points.add(nicotineInfo.getNicotine3().equals("1") ? 1 : 0);

        // Question 4: Which cigarette would you hate to give up?
        points.add(nicotineInfo.getNicotine4().equals("1") ? 1 : 0);

        // Question 5: Find it difficult to refrain?
        points.add(nicotineInfo.getNicotine5().equals("1") ? 1 : 0);

        // Question 6: Smoke when ill?
        points.add(nicotineInfo.getNicotine6().equals("1") ? 1 : 0);

        nicotineInfo.setPoints(points);

        // Calculate total
        int sum = 0;
        for (Integer point : points) {
            sum += point;
        }
        nicotineInfo.setSum(sum);
    }
}