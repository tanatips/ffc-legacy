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
import th.in.ffc.app.form.screening.datalive.CigaretteAddictionTestLiveData;
import th.in.ffc.app.form.screening.model.NicotineInfo;
import th.in.ffc.app.form.screening.model.PersonInfo;

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

    }
}