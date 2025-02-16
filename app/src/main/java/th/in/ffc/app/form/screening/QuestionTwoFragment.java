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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.adapter.SubstanceTwoAdapter;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionTwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionTwoFragment extends Fragment implements OnFrequencySelectedListener {

    private RecyclerView recyclerView;
    private SubstanceTwoAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private Map<String, Integer> selectedFrequencies = new HashMap<>();
    private QuestionsStateViewModel viewModel;
    private Observer<Map<String, Integer>> answersObserver;

    public QuestionTwoFragment() {
        // Required empty public constructor
    }
    public static QuestionTwoFragment newInstance(String param1, String param2) {
        QuestionTwoFragment fragment = new QuestionTwoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            substanceList = getArguments().getParcelableArrayList("substanceList");
        }
        viewModel = new ViewModelProvider(requireActivity()).get(QuestionsStateViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_two, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewTwo);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SubstanceTwoAdapter(substanceList, this::onFrequencySelected);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // เริ่มต้นค่าเริ่มต้นสำหรับทุก item
        for (SubstanceItem item : substanceList) {
            selectedFrequencies.put(item.getId(), 0); // กำหนดค่าเริ่มต้นเป็น 0 (ไม่เคย)
        }

        if (savedInstanceState != null) {
            Map<String, Integer> savedFrequencies = (Map<String, Integer>) savedInstanceState.getSerializable("selectedFrequencies");
            if (savedFrequencies != null) {
                selectedFrequencies.putAll(savedFrequencies);
                updateUI(selectedFrequencies);
            }
        }

        answersObserver = answers -> {
            if (answers != null && isAdded()) {
                boolean hasChanges = false;
                for (Map.Entry<String, Integer> entry : answers.entrySet()) {
                    Integer currentValue = selectedFrequencies.get(entry.getKey());
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
        viewModel.getQuestionTwoAnswers().observe(getViewLifecycleOwner(), answersObserver);

    }
    private void updateUI(Map<String, Integer> answers) {
        if (adapter != null && recyclerView != null) {
            recyclerView.post(() -> {
                if (isAdded()) {
                    adapter.updateAnswers(answers);  // เรียกใช้ updateAnswers ที่นี่
                }
            });
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // ยกเลิก observer เมื่อ view ถูกทำลาย
        if (viewModel != null && answersObserver != null) {
            viewModel.getQuestionTwoAnswers().removeObserver(answersObserver);
        }
        recyclerView = null;
        adapter = null;
    }

    @Override
    public void onFrequencySelected(String id, int frequency) {
        if (!isAdded()) return;

        // เช็คว่าค่าเปลี่ยนแปลงจริงๆ
        Integer currentFrequency = selectedFrequencies.get(id);
        if (currentFrequency == null || currentFrequency != frequency) {
            selectedFrequencies.put(id, frequency);
            if (this instanceof QuestionTwoFragment) {
                viewModel.updateQuestionTwoAnswer(id, frequency); // เรียกใช้ updateQuestionTwoAnswer ที่นี่
            }
        }
        Log.d("Question Two", "Item " + id + " frequency: " + frequency);
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectedFrequencies", new HashMap<>(selectedFrequencies));
    }

    // เมธอดสำหรับเรียกดูข้อมูลที่เลือก
    public Map<String, Integer> getSelectedFrequencies() {
        return new HashMap<>(selectedFrequencies);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) {
            Map<String, Integer> currentAnswers = viewModel.getQuestionTwoAnswers().getValue();
            if (currentAnswers != null && !currentAnswers.equals(selectedFrequencies)) {
                selectedFrequencies = new HashMap<>(currentAnswers);
                updateUI(selectedFrequencies);
            }
        }
    }
}