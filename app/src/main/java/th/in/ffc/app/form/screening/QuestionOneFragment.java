package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import th.in.ffc.R;

import th.in.ffc.app.form.screening.adapter.SubstanceOneAdapter;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionOneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionOneFragment extends Fragment implements OnSubstanceSelectionListener{


    private Map<String, AnswerData> selectedAnswers = new HashMap<>();
    private RecyclerView recyclerView;
    private SubstanceOneAdapter adapter;
    private List<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Observer<Map<String, AnswerData>> answersObserver;
    final boolean[] isUpdating = {false};
    public QuestionOneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            substanceList = getArguments().getParcelableArrayList("substanceList");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_one, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOne);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        initializeData();
        adapter = new SubstanceOneAdapter(substanceList,this);
        for (SubstanceItem item : substanceList) {
            selectedAnswers.put(item.getId(), new AnswerData(false, ""));
        }
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);

        // สร้าง map สำหรับเก็บค่าเริ่มต้น
        for (SubstanceItem item : substanceList) {
            selectedAnswers.put(item.getId(), new AnswerData(item.isHasUsed(), item.getOtherSubstance()));
        }

        answersObserver = answers -> {
            if (answers != null && isAdded() && !isUpdating[0]) {
                isUpdating[0] = true;
                try {
                    selectedAnswers = new HashMap<>(answers);
                    updateUI(selectedAnswers);
                } finally {
                    isUpdating[0] = false;
                }
            }
        };

        viewModel.getQuestionOneAnswers().observe(getViewLifecycleOwner(), answersObserver);

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ยกเลิก observer เมื่อ view ถูกทำลาย
        if (viewModel != null && answersObserver != null) {
            viewModel.getQuestionOneAnswers().removeObserver(answersObserver);
        }
        recyclerView = null;
        adapter = null;
    }
    private void updateUI(Map<String, AnswerData> answers) {
        if (adapter != null) {
            recyclerView.post(() -> adapter.updateAnswers(answers));
        }
    }
//    private void onAnswerSelected(String id, boolean value) {
//        viewModel.updateQuestionOneAnswer(id, value);
//    }
    public static QuestionOneFragment newInstance(String param1, String param2) {
        QuestionOneFragment fragment = new QuestionOneFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    private void printCurrentSelections() {
    StringBuilder result = new StringBuilder("Current selections:\n");
    for (Map.Entry<String, AnswerData> entry : selectedAnswers.entrySet()) {
        result.append(entry.getKey())
                .append(": hasUsed=")
                .append(entry.getValue().isHasUsed());
        if (entry.getKey().equals("j")) {
            result.append(", otherSubstance=")
                    .append(entry.getValue().getOtherSubstance());
        }
        result.append("\n");
    }
    Log.d("QuestionOneFragment", result.toString());
}
    public Map<String, AnswerData> getSelectedAnswers() {
        return new HashMap<>(selectedAnswers);
    }

    // เมธอดสำหรับตรวจสอบว่าตอบครบทุกข้อหรือยัง
    public boolean isAllQuestionsAnswered() {
        return selectedAnswers.size() == substanceList.size();
    }

    @Override
    public void onAnswerChanged(String id, boolean hasUsed, String otherSubstance) {
        if (!isAdded() || isUpdating[0]) return;

        isUpdating[0] = true;
        try {
            // บันทึกลง local map
            AnswerData newAnswer = new AnswerData(hasUsed, otherSubstance);
            AnswerData currentAnswer = selectedAnswers.get(id);

            if (currentAnswer == null ||
                    currentAnswer.isHasUsed() != hasUsed ||
                    !Objects.equals(currentAnswer.getOtherSubstance(), otherSubstance)) {

                selectedAnswers.put(id, newAnswer);
                viewModel.updateQuestionOneAnswer(id, hasUsed, otherSubstance);
            }
        } finally {
            isUpdating[0] = false;
        }
        printCurrentSelections();
    }
    public static class AnswerData {
        private boolean hasUsed;
        private String otherSubstance;

        public AnswerData(boolean hasUsed, String otherSubstance) {
            this.hasUsed = hasUsed;
            this.otherSubstance = otherSubstance;
        }

        // Getters
        public boolean isHasUsed() { return hasUsed; }
        public String getOtherSubstance() { return otherSubstance; }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null && !isUpdating[0]) {
            isUpdating[0] = true;
            try {
                Map<String, AnswerData> currentAnswers = viewModel.getQuestionOneAnswers().getValue();
                if (currentAnswers != null) {
                    selectedAnswers = new HashMap<>(currentAnswers);
                    updateUI(selectedAnswers);
                }
            } finally {
                isUpdating[0] = false;
            }
        }
    }
}