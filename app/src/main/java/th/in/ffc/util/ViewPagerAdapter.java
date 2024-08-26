package th.in.ffc.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.BloodPressureFragment;
import th.in.ffc.app.form.screening.BmiFragment;
import th.in.ffc.app.form.screening.SmookingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BmiFragment();
            case 1:
                return new BloodPressureFragment();
            case 2:
                return new AlcoholFragment();
            case 3:
                return new AlcoholFragment();
            case 4:
                return new AlcoholFragment();
            default:
                return new SmookingFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4; // จำนวน tab ทั้งหมด
    }
}
