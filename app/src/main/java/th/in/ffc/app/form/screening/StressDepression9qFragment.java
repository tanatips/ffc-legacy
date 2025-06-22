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
import android.widget.Toast;

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

    // เพิ่มตัวแปรสำหรับตรวจสอบข้อมูล
    private boolean isFormValid = false;
    private boolean[] questionAnswered = {false, false, false, false, false, false, false, false, false}; // ตรวจสอบว่าตอบคำถามครบหรือไม่ (9 ข้อ)

    public StressDepression9qFragment() {
        // Required empty public constructor
    }


    public static StressDepression9qFragment newInstance(String param1, String param2) {
        StressDepression9qFragment fragment = new StressDepression9qFragment();
        return fragment;
    }
    /**
     * ตรวจสอบความถูกต้องของข้อมูลและบันทึกข้อมูล
     */
    private void validateAndSaveData() {
        // ตรวจสอบว่าตอบคำถามครบทุกข้อหรือไม่
        boolean allAnswered = true;
        for (boolean answered : questionAnswered) {
            if (!answered) {
                allAnswered = false;
                break;
            }
        }

        isFormValid = allAnswered;

        if (isFormValid) {
            // ถ้าตอบครบทุกข้อ ให้บันทึกข้อมูล
            Log.d("StressDepression9q", "ตอบคำถามครบทุกข้อแล้ว - บันทึกข้อมูล");
            dataPasser.onStressDepression9q(stressDepression9qInfo);
        } else {
            // ถ้ายังตอบไม่ครบ ให้แสดงข้อความแจ้งเตือน
            showIncompleteFormMessage();
        }
    }
    /**
     * แสดงข้อความแจ้งเตือนเมื่อตอบไม่ครบ
     */
    private void showIncompleteFormMessage() {
        String missingQuestions = getMissingQuestionsText();
        if (!missingQuestions.isEmpty()) {
//            Toast.makeText(getContext(),
//                    "กรุณาตอบคำถามให้ครบถ้วน: " + missingQuestions,
//                    Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * ดึงรายการคำถามที่ยังไม่ได้ตอบ
     */
    private String getMissingQuestionsText() {
        ArrayList<String> missingQuestions = new ArrayList<>();

        for (int i = 0; i < questionAnswered.length; i++) {
            if (!questionAnswered[i]) {
                missingQuestions.add("ข้อ " + (i + 1));
            }
        }

        if (missingQuestions.isEmpty()) {
            return "";
        }

        return String.join(", ", missingQuestions);
    }

    /**
     * ตรวจสอบว่าแบบฟอร์มกรอกครบหรือไม่
     */
    public boolean isFormComplete() {
        return isFormValid;
    }
    /**
     * รีเซ็ตสถานะการตรวจสอบ (ใช้เมื่อล้างข้อมูล)
     */
    public void resetValidation() {
        isFormValid = false;
        for (int i = 0; i < questionAnswered.length; i++) {
            questionAnswered[i] = false;
        }
    }
    /**
     * ตรวจสอบสถานะการตอบจาก RadioGroup
     */
    private void checkAnsweredStatus() {
        questionAnswered[0] = getView().findViewById(R.id.rdoStress9qQ1).findViewById(R.id.rdoStress9qQ1_1) != null;
        questionAnswered[1] = getView().findViewById(R.id.rdoStress9qQ2).findViewById(R.id.rdoStress9qQ2_1) != null;
        questionAnswered[2] = getView().findViewById(R.id.rdoStress9qQ3).findViewById(R.id.rdoStress9qQ3_1) != null;
        questionAnswered[3] = getView().findViewById(R.id.rdoStress9qQ4).findViewById(R.id.rdoStress9qQ4_1) != null;
        questionAnswered[4] = getView().findViewById(R.id.rdoStress9qQ5).findViewById(R.id.rdoStress9qQ5_1) != null;
        questionAnswered[5] = getView().findViewById(R.id.rdoStress9qQ6).findViewById(R.id.rdoStress9qQ6_1) != null;
        questionAnswered[6] = getView().findViewById(R.id.rdoStress9qQ7).findViewById(R.id.rdoStress9qQ7_1) != null;
        questionAnswered[7] = getView().findViewById(R.id.rdoStress9qQ8).findViewById(R.id.rdoStress9qQ8_1) != null;
        questionAnswered[8] = getView().findViewById(R.id.rdoStress9qQ9).findViewById(R.id.rdoStress9qQ9_1) != null;

        // วิธีที่ถูกต้องในการตรวจสอบ RadioGroup
        RadioGroup rdoStress9qQ1 = getView().findViewById(R.id.rdoStress9qQ1);
        RadioGroup rdoStress9qQ2 = getView().findViewById(R.id.rdoStress9qQ2);
        RadioGroup rdoStress9qQ3 = getView().findViewById(R.id.rdoStress9qQ3);
        RadioGroup rdoStress9qQ4 = getView().findViewById(R.id.rdoStress9qQ4);
        RadioGroup rdoStress9qQ5 = getView().findViewById(R.id.rdoStress9qQ5);
        RadioGroup rdoStress9qQ6 = getView().findViewById(R.id.rdoStress9qQ6);
        RadioGroup rdoStress9qQ7 = getView().findViewById(R.id.rdoStress9qQ7);
        RadioGroup rdoStress9qQ8 = getView().findViewById(R.id.rdoStress9qQ8);
        RadioGroup rdoStress9qQ9 = getView().findViewById(R.id.rdoStress9qQ9);

        questionAnswered[0] = rdoStress9qQ1.getCheckedRadioButtonId() != -1;
        questionAnswered[1] = rdoStress9qQ2.getCheckedRadioButtonId() != -1;
        questionAnswered[2] = rdoStress9qQ3.getCheckedRadioButtonId() != -1;
        questionAnswered[3] = rdoStress9qQ4.getCheckedRadioButtonId() != -1;
        questionAnswered[4] = rdoStress9qQ5.getCheckedRadioButtonId() != -1;
        questionAnswered[5] = rdoStress9qQ6.getCheckedRadioButtonId() != -1;
        questionAnswered[6] = rdoStress9qQ7.getCheckedRadioButtonId() != -1;
        questionAnswered[7] = rdoStress9qQ8.getCheckedRadioButtonId() != -1;
        questionAnswered[8] = rdoStress9qQ9.getCheckedRadioButtonId() != -1;
    }

    /**
     * เมธอดสำหรับหน้าจออื่นที่ต้องการตรวจสอบความครบถ้วนของข้อมูล
     */
    public StressDepression9qInfo getFormData() {
        if (isFormValid) {
            return stressDepression9qInfo;
        } else {
            showIncompleteFormMessage();
            return null;
        }
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

                // ตรวจสอบว่าตอบคำถามที่ 1 แล้ว
                questionAnswered[0] = true;
                validateAndSaveData();

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
//                dataPasser.onStressDepression9q(stressDepression9qInfo);
                questionAnswered[1] = true;
                validateAndSaveData();

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
                // ตรวจสอบว่าตอบคำถามที่ 2 แล้ว
                questionAnswered[2] = true;
                validateAndSaveData();

//                dataPasser.onStressDepression9q(stressDepression9qInfo);
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
//                dataPasser.onStressDepression9q(stressDepression9qInfo);

                questionAnswered[3] = true;
                validateAndSaveData();

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

                questionAnswered[4] = true;
                validateAndSaveData();

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
//                dataPasser.onStressDepression9q(stressDepression9qInfo);
                questionAnswered[5] = true;
                validateAndSaveData();

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

                questionAnswered[6] = true;
                validateAndSaveData();

//                dataPasser.onStressDepression9q(stressDepression9qInfo);
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

                questionAnswered[7] = true;
                validateAndSaveData();

//                dataPasser.onStressDepression9q(stressDepression9qInfo);
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

                questionAnswered[8] = true;
                validateAndSaveData();

//                dataPasser.onStressDepression9q(stressDepression9qInfo);
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
    // ปรับปรุง loadExistingData() ให้ตรวจสอบสถานะการตอบ
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

        // ตรวจสอบสถานะการตอบจากข้อมูลที่โหลดมา
        checkAnsweredStatus();

        // อัปเดตสถานะความถูกต้องของข้อมูล
        validateAndSaveData();

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

        int defaultWhite = ContextCompat.getColor(requireContext(), R.color.white);
        int lightGray = ContextCompat.getColor(requireContext(), R.color.light_gray);

        // กำหนดสีสำหรับแต่ละระดับ
        int lightGreen = Color.parseColor("#E8F5E8");    // เขียวอ่อน - ปกติ
        int lightYellow = Color.parseColor("#FFF3CD");   // เหลือง - ระดับน้อย
        int lightRed = Color.parseColor("#F8D7DA");      // แดงอ่อน - ระดับปานกลาง
        int darkRed = Color.parseColor("#F5C6CB");       // แดงเข้ม - ระดับมาก

        // ล้าง highlight เดิม - กลับเป็นสีพื้นหลังเดิม
        for (int i = 1; i < depression9resultTable.getChildCount(); i++) {
            TableRow row = (TableRow) depression9resultTable.getChildAt(i);

            // กำหนดสีพื้นหลังเดิมตามแถว
            if (i == 1) {
                row.setBackgroundColor(lightGreen);  // แถวปกติ
            } else if (i == 2) {
                row.setBackgroundColor(lightYellow); // แถวระดับน้อย
            } else if (i == 3) {
                row.setBackgroundColor(lightRed);    // แถวระดับปานกลาง
            } else if (i == 4) {
                row.setBackgroundColor(darkRed);     // แถวระดับมาก
            }
        }

        // กำหนด highlight ตามช่วงคะแนน
        TableRow rowToHighlight = null;
        int highlightColor = Color.parseColor("#FFE066"); // สีเหลืองเข้มสำหรับ highlight

        if (totalScore < 7) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(1);
            resultCode = "1B0260|1B0282";
            highlightColor = Color.parseColor("#C8E6C9"); // เขียวเข้มขึ้น
        } else if (totalScore >= 7 && totalScore <= 12) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(2);
            resultCode = "1B0261:1B0283";
            highlightColor = Color.parseColor("#FFE082"); // เหลืองเข้มขึ้น
        } else if (totalScore >= 13 && totalScore <= 18) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(3);
            resultCode = "1B0262:1B0284";
            highlightColor = Color.parseColor("#FFAB91"); // ส้มอ่อน
        } else if (totalScore >= 19) {
            rowToHighlight = (TableRow) depression9resultTable.getChildAt(4);
            resultCode = "1B0263:1B0285";
            highlightColor = Color.parseColor("#EF9A9A"); // แดงอ่อน
        }

        if (rowToHighlight != null) {
            rowToHighlight.setBackgroundColor(highlightColor); // highlight แถวที่ตรงกับช่วงคะแนน
        }

        depression9result.setText(String.format("คะแนนที่ได้: %d คะแนน (%s)", totalScore, resultCode));

        checkAnsweredStatus();
        validateAndSaveData();
    }
    // เพิ่มเมธอด validation ใน StressDepression9qFragment class

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอก
     */
    public String getValidationMessage() {
        StringBuilder message = new StringBuilder();

        // ตรวจสอบว่าตอบคำถามครบหรือไม่
        ArrayList<Integer> unansweredQuestions = getUnansweredQuestions();

        if (!unansweredQuestions.isEmpty()) {
            message.append("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q): ยังไม่ได้ตอบข้อ ");

            // แสดงรายการข้อที่ยังไม่ได้ตอบ
            for (int i = 0; i < unansweredQuestions.size(); i++) {
                if (i > 0) {
                    message.append(", ");
                }
                message.append(unansweredQuestions.get(i));
            }
        }

        return message.toString();
    }

    /**
     * ดึงข้อความแสดงรายละเอียดข้อที่ยังไม่ได้กรอกแบบละเอียด
     */
    public String getDetailedValidationMessage() {
        if (stressDepression9qInfo == null) {
            return "คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q):\n• ยังไม่ได้กรอกข้อมูลใดๆ";
        }

        ArrayList<String> missingQuestions = new ArrayList<>();

        // ตรวจสอบแต่ละคำถาม
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value == null || value.equals("0") || value.isEmpty()) {
                missingQuestions.add("ข้อ " + i + ": " + getQuestionDescription(i));
            }
        }

        if (!missingQuestions.isEmpty()) {
            StringBuilder message = new StringBuilder("คัดกรองโรคซึมเศร้าด้วย 9 คำถาม(9Q):\n");
            message.append("กรุณาตอบคำถามที่ยังไม่ได้ตอบ:\n");
            for (String question : missingQuestions) {
                message.append("• ").append(question).append("\n");
            }
            return message.toString().trim();
        }

        return ""; // ไม่มีข้อผิดพลาด
    }

    /**
     * รีเซ็ตฟอร์มกลับเป็นค่าเริ่มต้น
     */
    public void resetForm() {
        // ล้างการเลือกทั้งหมด
        View view = getView();
        if (view != null) {
            for (int i = 1; i <= 9; i++) {
                int radioGroupId = getResources().getIdentifier("rdoStress9qQ" + i, "id", requireContext().getPackageName());
                RadioGroup radioGroup = view.findViewById(radioGroupId);
                if (radioGroup != null) {
                    radioGroup.clearCheck();
                }
            }
        }

        // รีเซ็ต stressDepression9qInfo
        stressDepression9qInfo = new StressDepression9qInfo();

        // รีเซ็ต points
        points = new ArrayList<>();
        points.addAll(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0));

        // รีเซ็ตสถานะการตรวจสอบ
        resetValidation();

        // รีเซ็ตการแสดงผล
        if (depression9result != null) {
            depression9result.setText("คะแนนที่ได้: - คะแนน");
        }

        // ล้าง highlight ในตาราง
        clearTableHighlight();
    }

    /**
     * ล้าง highlight ในตาราง
     */
    private void clearTableHighlight() {
        if (depression9resultTable != null) {
            int lightGreen = Color.parseColor("#E8F5E8");
            int lightYellow = Color.parseColor("#FFF3CD");
            int lightRed = Color.parseColor("#F8D7DA");
            int darkRed = Color.parseColor("#F5C6CB");

            for (int i = 1; i < depression9resultTable.getChildCount(); i++) {
                TableRow row = (TableRow) depression9resultTable.getChildAt(i);
                if (row != null) {
                    if (i == 1) row.setBackgroundColor(lightGreen);
                    else if (i == 2) row.setBackgroundColor(lightYellow);
                    else if (i == 3) row.setBackgroundColor(lightRed);
                    else if (i == 4) row.setBackgroundColor(darkRed);
                }
            }
        }
    }

    /**
     * ตรวจสอบว่ามีการเปลี่ยนแปลงข้อมูลหรือไม่
     */
    public boolean hasDataChanged() {
        if (stressDepression9qInfo == null) {
            return false;
        }

        // ตรวจสอบว่ามีการตอบคำถามอย่างน้อย 1 ข้อหรือไม่
        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null && !value.equals("0") && !value.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    /**
     * ดึงสถานะการกรอกข้อมูลเป็นเปอร์เซ็นต์
     */
    public int getCompletionPercentage() {
        if (stressDepression9qInfo == null) {
            return 0;
        }

        int completedQuestions = 0;
        int totalQuestions = 9;

        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null && !value.equals("0") && !value.isEmpty()) {
                completedQuestions++;
            }
        }

        return (completedQuestions * 100) / totalQuestions;
    }

    /**
     * แสดงสถานะการกรอกข้อมูล
     */
    public void showCompletionStatus() {
        int percentage = getCompletionPercentage();
        String message;

        if (percentage == 100) {
            message = "✅ ข้อมูลครบถ้วน (" + percentage + "%)";

            // แสดงระดับความรุนแรงด้วย
            String severity = getDepressionSeverity();
            message += " - " + severity;
        } else if (percentage > 0) {
            message = "⚠️ ข้อมูลไม่ครบถ้วน (" + percentage + "%) - " + getValidationMessage();
        } else {
            message = "❌ ยังไม่ได้กรอกข้อมูล (0%)";
        }

        Log.d("StressDepression9q", "Completion Status: " + message);

        // สามารถแสดง Toast หรือ Snackbar ได้ที่นี่
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * ดึงรายชื่อคำถามที่ยังไม่ได้ตอบ
     */
    public ArrayList<Integer> getUnansweredQuestions() {
        ArrayList<Integer> unanswered = new ArrayList<>();

        if (stressDepression9qInfo == null) {
            for (int i = 1; i <= 9; i++) {
                unanswered.add(i);
            }
            return unanswered;
        }

        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value == null || value.equals("0") || value.isEmpty()) {
                unanswered.add(i);
            }
        }

        return unanswered;
    }

    /**
     * ดึงคำอธิบายของคำถามแต่ละข้อ
     */
    private String getQuestionDescription(int questionNumber) {
        switch (questionNumber) {
            case 1:
                return "เบื่อ ไม่สนใจอยากทำอะไร";
            case 2:
                return "ไม่สบายใจ ซึมเศร้า ท้อแท้";
            case 3:
                return "หลับยาก หรือหลับๆ ตื่นๆ หรือหลับมากไป";
            case 4:
                return "เหนื่อยง่าย หรือไม่ค่อยมีแรง";
            case 5:
                return "เบื่ออาหาร หรือกินมากเกินไป";
            case 6:
                return "รู้สึกไม่ดีกับตัวเอง คิดว่าตัวเองล้มเหลว";
            case 7:
                return "สมาธิไม่ดีเวลาทำอะไร";
            case 8:
                return "พูดช้า ทำอะไรช้าลง หรือกระสับกระส่าย";
            case 9:
                return "คิดทำร้ายตนเอง หรือคิดว่าถ้าตายไปคงจะดี";
            default:
                return "คำถามที่ " + questionNumber;
        }
    }

    /**
     * ตรวจสอบและเลื่อนไปยังคำถามแรกที่ยังไม่ได้ตอบ
     */
    public void scrollToFirstUnansweredQuestion() {
        ArrayList<Integer> unanswered = getUnansweredQuestions();
        if (!unanswered.isEmpty() && getView() != null) {
            int firstUnanswered = unanswered.get(0);

            int radioGroupId = getResources().getIdentifier(
                    "rdoStress9qQ" + firstUnanswered,
                    "id",
                    requireContext().getPackageName()
            );

            RadioGroup targetGroup = getView().findViewById(radioGroupId);
            if (targetGroup != null) {
                targetGroup.requestFocus();
                // สามารถเพิ่มการ scroll ไปยัง view ได้ที่นี่
            }
        }
    }

    /**
     * ดึงระดับความรุนแรงของภาวะซึมเศร้า
     */
    public String getDepressionSeverity() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int totalScore = getTotalScore();

        if (totalScore < 7) {
            return "ไม่มีอาการของโรคซึมเศร้า";
        } else if (totalScore >= 7 && totalScore <= 12) {
            return "มีอาการของโรคซึมเศร้าระดับน้อย";
        } else if (totalScore >= 13 && totalScore <= 18) {
            return "มีอาการของโรคซึมเศร้าระดับปานกลาง";
        } else if (totalScore >= 19) {
            return "มีอาการของโรคซึมเศร้าระดับรุนแรง";
        }

        return "";
    }

    /**
     * ดึงคะแนนรวม
     */
    public int getTotalScore() {
        if (!isFormComplete() || points == null) {
            return -1;
        }

        int totalScore = 0;
        for (Integer point : points) {
            if (point != null) {
                totalScore += point;
            }
        }

        return totalScore;
    }

    /**
     * ตรวจสอบว่ามีความเสี่ยงสูงหรือไม่ (คะแนน >= 19 หรือข้อ 9 >= 1)
     */
    public boolean isHighRisk() {
        if (!isFormComplete()) {
            return false;
        }

        // ตรวจสอบคะแนนรวม
        if (getTotalScore() >= 19) {
            return true;
        }

        // ตรวจสอบข้อ 9 (คิดทำร้ายตนเอง)
        String q9Value = getQuestionValue(9);
        if (q9Value != null && !q9Value.equals("1") && !q9Value.equals("0")) { // ถ้าตอบมากกว่า "ไม่มีเลย"
            return true;
        }

        return false;
    }

    /**
     * ตรวจสอบความเสี่ยงการฆ่าตัวตาย (ข้อ 9)
     */
    public boolean hasSuicidalRisk() {
        String q9Value = getQuestionValue(9);
        return q9Value != null && !q9Value.equals("1") && !q9Value.equals("0");
    }

    /**
     * แสดงคำแนะนำตามระดับความรุนแรง
     */
    public String getRecommendation() {
        if (!isFormComplete()) {
            return "กรุณาตอบคำถามให้ครบถ้วนเพื่อรับคำแนะนำ";
        }

        int totalScore = getTotalScore();

        if (hasSuicidalRisk()) {
            return "⚠️ พบความเสี่ยงในการทำร้ายตนเอง ควรพบแพทย์โดยด่วน!";
        }

        if (totalScore < 7) {
            return "ไม่มีอาการของโรคซึมเศร้า ควรดูแลสุขภาพจิตให้ดีต่อไป";
        } else if (totalScore >= 7 && totalScore <= 12) {
            return "มีอาการซึมเศร้าระดับน้อย ควรพักผ่อนให้เพียงพอ ออกกำลังกาย และทำกิจกรรมที่ชื่นชอบ";
        } else if (totalScore >= 13 && totalScore <= 18) {
            return "มีอาการซึมเศร้าระดับปานกลาง ควรปรึกษาผู้เชี่ยวชาญด้านสุขภาพจิต";
        } else if (totalScore >= 19) {
            return "มีอาการซึมเศร้าระดับรุนแรง ควรพบแพทย์เพื่อรับการรักษาโดยเร็ว";
        }

        return "";
    }

    /**
     * ดึงข้อมูลสรุปแบบสั้น
     */
    public String getSummaryText() {
        if (!isFormComplete()) {
            return "ยังไม่ได้ประเมิน";
        }

        int score = getTotalScore();
        String severity = getDepressionSeverity();

        String summary = String.format("คะแนน: %d - %s", score, severity);

        if (hasSuicidalRisk()) {
            summary += " (⚠️ เสี่ยงทำร้ายตนเอง)";
        }

        return summary;
    }

    /**
     * ตรวจสอบว่าควรทำแบบประเมิน 8Q ต่อหรือไม่
     */
    public boolean shouldDo8QAssessment() {
        return isFormComplete() && hasSuicidalRisk();
    }

    /**
     * ดึงข้อความแนะนำให้ทำ 8Q
     */
    public String get8QRecommendationText() {
        if (shouldDo8QAssessment()) {
            return "⚠️ แนะนำให้ทำแบบประเมินการฆ่าตัวตาย 8Q เพิ่มเติม เนื่องจากพบความเสี่ยงในการทำร้ายตนเอง";
        }
        return "";
    }

    /**
     * ดึงรายการคำถามที่ตอบว่ามีอาการบ่อย (คะแนน >= 2)
     */
    public ArrayList<String> getFrequentSymptoms() {
        ArrayList<String> symptoms = new ArrayList<>();

        if (!isFormComplete()) {
            return symptoms;
        }

        for (int i = 1; i <= 9; i++) {
            String value = getQuestionValue(i);
            if (value != null) {
                int score = Integer.parseInt(value) - 1; // แปลงเป็นคะแนน 0-3
                if (score >= 2) { // บ่อยครั้ง หรือ เกือบทุกวัน
                    symptoms.add("ข้อ " + i + ": " + getQuestionDescription(i));
                }
            }
        }

        return symptoms;
    }

}