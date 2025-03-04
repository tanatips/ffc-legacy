package th.in.ffc.app.form.screening;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;

public class QuestionEightFragment extends Fragment {

    private QuestionsStateViewModel viewModel;
    private RadioGroup radioGroupInjection;
    private int selectedOption = 0; // 0 = ไม่เคย, 1 = ภายใน 3 เดือน, 2 = ก่อน 3 เดือน

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_eight, container, false);
        radioGroupInjection = view.findViewById(R.id.radioGroupInjection);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore saved state if exists
        if (savedInstanceState != null) {
            selectedOption = savedInstanceState.getInt("selectedOption", 0);
            updateRadioSelection();
        }

        // Observe ViewModel data
        viewModel.getQuestionEightAnswers().observe(getViewLifecycleOwner(), answer -> {
            if (answer != null && answer.containsKey("injection")) {
                selectedOption = answer.get("injection").getFrequency();
                updateRadioSelection();
            }
        });

        // Set up radio group listener
        radioGroupInjection.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioNever) {
                selectedOption = 0;
            } else if (checkedId == R.id.radioWithinThreeMonths) {
                selectedOption = 1;
            } else if (checkedId == R.id.radioBeforeThreeMonths) {
                selectedOption = 2;
            }

            // Update ViewModel
            viewModel.updateQuestionEightAnswer("injection", selectedOption, "");
        });
    }

    private void updateRadioSelection() {
        switch (selectedOption) {
            case 0:
                radioGroupInjection.check(R.id.radioNever);
                break;
            case 1:
                radioGroupInjection.check(R.id.radioWithinThreeMonths);
                break;
            case 2:
                radioGroupInjection.check(R.id.radioBeforeThreeMonths);
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selectedOption", selectedOption);
    }
}