package th.in.ffc.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

import th.in.ffc.app.form.screening.AlcoholFragment;
import th.in.ffc.app.form.screening.BloodPressureFragment;
import th.in.ffc.app.form.screening.BmiFragment;
import th.in.ffc.app.form.screening.FagerstromNicotineFragment;
import th.in.ffc.app.form.screening.FragmentTabInfo;
import th.in.ffc.app.form.screening.SmookingFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    ArrayList<FragmentTabInfo> fragmentTabInfos;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, ArrayList<FragmentTabInfo> fragmentTabInfos) {
        super(fragmentActivity);
        this.fragmentTabInfos=fragmentTabInfos;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentTabInfos.get(position).getFragment();
    }

    @Override
    public int getItemCount() {
        return fragmentTabInfos.size();
    }

    public Fragment getFragmentAt(int position){
        return fragmentTabInfos.get(position).getFragment();
    }
}
