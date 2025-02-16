package th.in.ffc.app.form.screening;

import static java.security.AccessController.getContext;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import th.in.ffc.app.form.screening.adapter.SubstanceSixAdapter;
import th.in.ffc.app.form.screening.listener.OnConcernSelectedListener;
import th.in.ffc.app.form.screening.model.QuestionsStateViewModel;
import th.in.ffc.app.form.screening.model.SubstanceItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link QuestionSixFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionSixFragment extends Fragment implements OnConcernSelectedListener {

    private RecyclerView recyclerView;
    private SubstanceSixAdapter adapter;
    private ArrayList<SubstanceItem> substanceList;
    private QuestionsStateViewModel viewModel;
    private Map<String, Integer> selectedAnswers = new HashMap<>();

    public QuestionSixFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_question_six, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new SubstanceSixAdapter(substanceList, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onConcernSelected(String id, int value) {
        selectedAnswers.put(id, value);
        viewModel.updateQuestionSixAnswer(id, value);
        Log.d("Question six", "Item " + id + " frequency: " + value);
    }
}