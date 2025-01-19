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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.dao.SfStressDepression2qInfoDao;
import th.in.ffc.app.form.screening.dao.SfStressDepression9qInfoDao;
import th.in.ffc.app.form.screening.datalive.StressDepression2qLiveData;
import th.in.ffc.app.form.screening.datalive.StressDepression9qLiveData;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.app.form.screening.model.StressDepression9qInfo;
import th.in.ffc.util.Log;

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

    private RadioGroup[] radioGroups;

    private TableLayout depression9resultTable;
    private static final int QUESTION_COUNT = 9;
    private TextView depression9result;
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
    private void displayPoints() {
        int totalScore = sumPoints();
//        depression9result.setText("คะแนน: "+String.valueOf(totalScore));
    }
    private int sumPoints() {
        // ตรวจสอบว่า points ไม่เป็น null
        if (points == null) {
            return 0;
        }

        // คำนวณผลรวมของคะแนนทั้งหมด
        int totalScore = 0;
        for (Integer point : points) {
            if (point != null) {
                totalScore += point;
            }
        }

        return totalScore;
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
        depression9resultTable = view.findViewById(R.id.depression9resultTable);
        depression9result = view.findViewById(R.id.depression9result);
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
                displayPoints();

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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ2(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ3(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
            }

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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ4(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
            }
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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ5(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ6(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ7(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ8(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();
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
                updateTableHighlight();
                stressDepression9qLiveData.setSelectedQ9(checkedId);
                shareViewModel.setStressDepression9qLiveData(stressDepression9qLiveData);
                displayPoints();

            }
        });
        loadData();
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

    private void  loadData(){
        SfStressDepression9qInfoDao sfStressDepression9qInfoDao = new SfStressDepression9qInfoDao(getContext());
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getStressDepression9qLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                List<StressDepression9qInfo> stressDepression9qInfos = sfStressDepression9qInfoDao.getByPersonId(Integer.valueOf(data.getPersonId()));
                for(StressDepression9qInfo stressDepression9qInfo :stressDepression9qInfos){
                    Log.d("Stress Depression 9q ", "stressDepression9qInfo infos:"+stressDepression9qInfo);
                    this.stressDepression9qInfo = stressDepression9qInfo;
                    loadExistingData();
                }
            }
        });
    }
    private void loadExistingData() {
        if (this.stressDepression9qInfo == null) return;

        for (int i = 0; i < QUESTION_COUNT; i++) {
            String value = getQuestionValue(i + 1);
            if (!value.equals("0")) {
                int radioButtonId = getResources().getIdentifier(
                        "rdoStress9qQ" + (i + 1) + "_" + (Integer.parseInt(value)),
                        "id",
                        requireContext().getPackageName()
                );
                RadioButton radioButton = requireView().findViewById(radioButtonId);
                if (radioButton != null) {
                    radioButton.setChecked(true);
                }
            }
        }
        displayPoints();
    }
    private void setQuestionValue(int questionNumber, String value) {
        switch (questionNumber) {
            case 1: stressDepression9qInfo.setQ1(value); break;
            case 2: stressDepression9qInfo.setQ2(value); break;
            case 3: stressDepression9qInfo.setQ3(value); break;
            case 4: stressDepression9qInfo.setQ4(value); break;
            case 5: stressDepression9qInfo.setQ5(value); break;
            case 6: stressDepression9qInfo.setQ6(value); break;
            case 7: stressDepression9qInfo.setQ7(value); break;
            case 8: stressDepression9qInfo.setQ8(value); break;
            case 9: stressDepression9qInfo.setQ9(value); break;
        }
    }

    private String getQuestionValue(int questionNumber) {
        switch (questionNumber) {
            case 1: return stressDepression9qInfo.getQ1();
            case 2: return stressDepression9qInfo.getQ2();
            case 3: return stressDepression9qInfo.getQ3();
            case 4: return stressDepression9qInfo.getQ4();
            case 5: return stressDepression9qInfo.getQ5();
            case 6: return stressDepression9qInfo.getQ6();
            case 7: return stressDepression9qInfo.getQ7();
            case 8: return stressDepression9qInfo.getQ8();
            case 9: return stressDepression9qInfo.getQ9();
            default: return "0";
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
    private void updateTableHighlight() {
        // คำนวณคะแนนรวม
        String resultCode = "";
        int totalScore = 0;
        for (Integer point : points) {
            totalScore += point;
        }
        int defaultWhite = ContextCompat.getColor(requireContext(), R.color.white);// Color.WHITE;
        int lightGray = ContextCompat.getColor(requireContext(), R.color.light_gray);
        int highlightColor = ContextCompat.getColor(requireContext(), R.color.highlight_yellow); // สีเหลืองสำหรับ highlight

        // ล้าง highlight เดิม
        for (int i = 1; i < depression9resultTable.getChildCount(); i++) {
            TableRow row = (TableRow) depression9resultTable.getChildAt(i);
            row.setBackgroundColor(i % 2 == 0 ?
                    lightGray : defaultWhite);
        }

        // กำหนด highlight ตามช่วงคะแนน
        TableRow rowToHighlight = null;
        if (totalScore < 7) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(1);
            resultCode = "1B0260|1B0282";
        } else if (totalScore >= 7 && totalScore <= 12) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(2);
            resultCode = "1B0261:1B0283";
        } else if (totalScore >= 13 && totalScore <= 18) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(3);
            resultCode = "1B0262:1B0284";
        } else if (totalScore >= 19) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(4);
            resultCode = "1B0263:1B0285";
        }

        if (rowToHighlight != null) {
            rowToHighlight.setBackgroundColor(highlightColor); // highlight แถวที่ตรงกับช่วงคะแนน
        }
        depression9result.setText(String.format("คะแนนที่ได้: %d คะแนน (%s)", totalScore, resultCode));
    }
}