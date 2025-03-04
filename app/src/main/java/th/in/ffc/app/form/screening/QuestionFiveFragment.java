package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import th.in.ffc.R;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import th.in.ffc.app.form.screening.adapter.SubstanceFiveAdapter;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.AnswerFrequencyData;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

public class QuestionFiveFragment extends Fragment implements OnFrequencySelectedListener {
    private RecyclerView recyclerView;
    private SubstanceFiveAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Map<String, AnswerFrequencyData> selectedFrequencies = new HashMap<>();

    private Observer<Map<String, AnswerFrequencyData>> answersObserver;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            substanceList = getArguments().getParcelableArrayList("substanceList");
        }
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question_five, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SubstanceFiveAdapter(substanceList, this);
        recyclerView.setAdapter(adapter);
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // เริ่มต้นค่าเริ่มต้นสำหรับทุก item
        for (SubstanceItem item : substanceList) {
            selectedFrequencies.put(item.getId(), new AnswerFrequencyData(0,""));
        }

        if (savedInstanceState != null) {
            Map<String, AnswerFrequencyData> savedFrequencies = (Map<String, AnswerFrequencyData>) savedInstanceState.getSerializable("selectedFrequencies");
            if (savedFrequencies != null) {
                selectedFrequencies.putAll(savedFrequencies);
                updateUI(selectedFrequencies);
            }
        }
        answersObserver = answers -> {
            if (answers != null && isAdded()) {
                boolean hasChanges = false;
                for (Map.Entry<String, AnswerFrequencyData> entry : answers.entrySet()) {
                    AnswerFrequencyData currentValue = selectedFrequencies.get(entry.getKey());
                    if (currentValue == null || !currentValue.equals(entry.getValue())) {
                        hasChanges = true;
                        break;
                    }
                }
                if (hasChanges) {
                    selectedFrequencies = new HashMap<>(answers);
                    updateUI(selectedFrequencies);
                }
            }
        };
        viewModel.getQuestionFiveAnswers().observe(getViewLifecycleOwner(), answersObserver);
    }
    private void updateUI(Map<String, AnswerFrequencyData> answers) {
        if (adapter != null && recyclerView != null) {
            recyclerView.post(() -> {
                if (isAdded()) {
                    adapter.updateAnswers(answers);
                }
            });
        }
    }
    @Override
    public void onFrequencySelected(String id, int frequency, String otherDrugs) {
        if (!isAdded()) return;

        // เช็คว่าค่าเปลี่ยนแปลงจริงๆ
        AnswerFrequencyData currentFrequency = selectedFrequencies.get(id);
        if (currentFrequency == null || currentFrequency.getFrequency() != frequency) {
            selectedFrequencies.put(id, new AnswerFrequencyData(frequency, currentFrequency.getOtherDrugs()));
            viewModel.updateQuestionFiveAnswer(id, frequency, currentFrequency.getOtherDrugs());
        }
        Log.d("QuestionFive", "Item " + id + " frequency: " + frequency);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            Map<String, AnswerFrequencyData> currentAnswers = viewModel.getQuestionFiveAnswers().getValue();
            if (currentAnswers != null && !currentAnswers.equals(selectedFrequencies)) {
                selectedFrequencies = new HashMap<>(currentAnswers);
                updateUI(selectedFrequencies);
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedFrequencies", new HashMap<>(selectedFrequencies));
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (viewModel != null && answersObserver != null) {
            viewModel.getQuestionFiveAnswers().removeObserver(answersObserver);
        }
        recyclerView = null;
        adapter = null;
    }
}