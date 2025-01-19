package th.in.ffc.person;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import th.in.ffc.R;
import th.in.ffc.app.form.screening.OnDataPass;
import th.in.ffc.app.form.screening.SharedViewModel;
import th.in.ffc.app.form.screening.model.AssistScore;
import th.in.ffc.app.form.screening.model.StressDepression2qInfo;
import th.in.ffc.util.Log;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AssistScoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AssistScoreFragment extends Fragment  {

    private OnDataPass dataPasser;

    public AssistScoreFragment() {
        // Required empty public constructor
    }


    public static AssistScoreFragment newInstance(AssistScore data) {
        AssistScoreFragment fragment = new AssistScoreFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assist_score, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView tvAssistScore = view.findViewById(R.id.tvAssistScore);
        SharedViewModel viewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel.getAssistScoreMutableLiveData().observe(getViewLifecycleOwner(), data -> {

            if(data.getPersonId()!=null){
                if(data.getNicotineScore()!=null){
                    tvAssistScore.setText(data.getNicotineScore().toString());
                }
            }
        });
    }
}


//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            dataPasser = (OnDataPass) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnDataPass");
//        }
//    }