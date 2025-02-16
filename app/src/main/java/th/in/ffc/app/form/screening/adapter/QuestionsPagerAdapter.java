package th.in.ffc.app.form.screening.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import th.in.ffc.app.form.screening.QuestionOneFragment;
import th.in.ffc.app.form.screening.QuestionTwoFragment;

public class QuestionsPagerAdapter extends FragmentStateAdapter {
    public QuestionsPagerAdapter(Fragment fragment) {
        super(fragment);
    }

    @Override
    public int getItemCount() {
        return 2; // 2 pages for Question 1 and 2
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new QuestionOneFragment();
            case 1:
                return new QuestionTwoFragment();
            default:
                throw new IllegalStateException("Unexpected position " + position);
        }
    }
}