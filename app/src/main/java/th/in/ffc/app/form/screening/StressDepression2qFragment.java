package th.in.ffc.app.form.screening;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfDrinkingInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.model.DrinkingInfo;
import th.in.ffc.app.form.screening.model.HealthRiskAssessmentInfo;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.util.Log;

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
    private RadioGroup rdoStress2qQ1;
    private RadioGroup rdoStress2qQ2;
    private TableLayout resultTable;
    int white;
    int light_gray;
    int highlightColor;
    public StressDepression2qFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stress_depression2q, container, false);

        // ผูก views
        rdoStress2qQ1 = view.findViewById(R.id.rdoStress2qQ1);
        rdoStress2qQ2 = view.findViewById(R.id.rdoStress2qQ2);
        resultTable = view.findViewById(R.id.depression2resultTable);

        return view;
    }
    private void initializeViews(View view) {
        rdoStress2qQ1 = view.findViewById(R.id.rdoStress2qQ1);
        rdoStress2qQ2 = view.findViewById(R.id.rdoStress2qQ2);
        resultTable = view.findViewById(R.id.depression2resultTable);
        white = ContextCompat.getColor(requireContext(), R.color.white);
        light_gray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow);
    }
    private void updateTableHighlight() {
        try {
            // รับค่าการเลือกจาก RadioGroup ทั้งสอง
            String answer1 = null;
            String answer2 = null;

            // ตรวจสอบคำตอบข้อ 1
            int checkedId1 = rdoStress2qQ1.getCheckedRadioButtonId();
            if (checkedId1 == R.id.rdoStress2qQ1_1) {
                answer1 = "ไม่มี";
            } else if (checkedId1 == R.id.rdoStress2qQ1_2) {
                answer1 = "มี";
            }

            // ตรวจสอบคำตอบข้อ 2
            int checkedId2 = rdoStress2qQ2.getCheckedRadioButtonId();
            if (checkedId2 == R.id.rdoStress2qQ2_1) {
                answer2 = "ไม่มี";
            } else if (checkedId2 == R.id.rdoStress2qQ2_2) {
                answer2 = "มี";
            }

            // ถ้ายังตอบไม่ครบ ไม่ต้อง highlight
            if (answer1 == null || answer2 == null) {
                clearHighlights();
                return;
            }

            if (resultTable != null && resultTable.getChildCount() >= 3) {
                // ดึง TableRow ที่ต้องการ highlight
                TableRow normalRow = (TableRow) resultTable.getChildAt(1);
                TableRow abnormalRow = (TableRow) resultTable.getChildAt(2);

                // รีเซ็ตสีพื้นหลังเริ่มต้น
                normalRow.setBackgroundColor(white);
                abnormalRow.setBackgroundColor(white);

                // ถ้าตอบ "ไม่มี" ทั้งสองข้อ
                if ("ไม่มี".equals(answer1) && "ไม่มี".equals(answer2)) {
                    normalRow.setBackgroundColor(highlightColor);
                }
                // ถ้ามีการตอบ "มี" อย่างน้อย 1 ข้อ
                else if ("มี".equals(answer1) || "มี".equals(answer2)) {
                    abnormalRow.setBackgroundColor(highlightColor);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void clearHighlights() {
        // ล้าง highlight ทั้งหมด
        TableRow normalRow = (TableRow) resultTable.getChildAt(1);
        TableRow abnormalRow = (TableRow) resultTable.getChildAt(2);
        normalRow.setBackgroundColor(white);
        abnormalRow.setBackgroundColor(light_gray);
    }
    private void setupListeners(){
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
                updatePoints();
                updateTableHighlight();
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
                updatePoints();
                updateTableHighlight();
            }
        });

    }
    private void updatePoints() {
        ArrayList<Integer> points = new ArrayList<>();

        // Add points for Q1
        points.add(stressDepression2qInfo.getQ1().equals("1") ? 1 : 0);

        // Add points for Q2
        points.add(stressDepression2qInfo.getQ2().equals("1") ? 1 : 0);

        // Update the points in the model
        stressDepression2qInfo.setPoints(points);

        // Analyze results
        stressDepression2qInfo.analyze();

    }
    public void setStressInfo(StressDepression2qInfo info) {
        this.stressDepression2qInfo = info;
        loadExistingData();
    }
    private void loadExistingData() {
        if (stressDepression2qInfo == null) return;

        // Load Q1 data
        switch (stressDepression2qInfo.getQ1()) {
            case "1":
                ((RadioButton)rdoStress2qQ1.findViewById(R.id.rdoStress2qQ1_1)).setChecked(true);
                break;
            case "2":
                ((RadioButton)rdoStress2qQ1.findViewById(R.id.rdoStress2qQ1_2)).setChecked(true);
                break;
        }

        // Load Q2 data
        switch (stressDepression2qInfo.getQ2()) {
            case "1":
                ((RadioButton)rdoStress2qQ2.findViewById(R.id.rdoStress2qQ2_1)).setChecked(true);
                break;
            case "2":
                ((RadioButton)rdoStress2qQ2.findViewById(R.id.rdoStress2qQ2_2)).setChecked(true);
                break;
        }
    }
    public StressDepression2qInfo getStressInfo() {
        return stressDepression2qInfo;
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
        View parentViewPager = (View) view.getParent();
        if (parentViewPager != null) {
            parentViewPager.post(() -> {
                int height = view.getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = parentViewPager.getLayoutParams();
                layoutParams.height = height;
                parentViewPager.setLayoutParams(layoutParams);
            });
        }
        initializeViews(view);
        setupListeners();
        loadData();
    }
    private void loadData(){
        SfStressDepression2qInfoDao sfStressDepression2qInfoDao = new SfStressDepression2qInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getStressDepression2qLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<StressDepression2qInfo> stressDepression2qInfos = sfStressDepression2qInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(StressDepression2qInfo stressDepression2qInfo :stressDepression2qInfos){
                    Log.d("Stress Depression ", "stressDepression2qInfo infos:"+stressDepression2qInfo);
                    this.stressDepression2qInfo = stressDepression2qInfo;
                    loadExistingData();
                }
            }
        });
    }

}