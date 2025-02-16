package th.in.ffc.app.form.screening;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import th.in.ffc.R;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import th.in.ffc.app.form.screening.adapter.SubstanceFiveAdapter;
import th.in.ffc.app.form.screening.listener.OnFrequencySelectedListener;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

public class QuestionFiveFragment extends Fragment implements OnFrequencySelectedListener {
    private RecyclerView recyclerView;
    private SubstanceFiveAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Map<String, Integer> selectedFrequencies = new HashMap<>();

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
    public void onFrequencySelected(String id, int frequency) {
        selectedFrequencies.put(id, frequency);
        viewModel.updateQuestionFiveAnswer(id, frequency);
        Log.d("Question five", "Item " + id + " frequency: " + frequency);
    }
}