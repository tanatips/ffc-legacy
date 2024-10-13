package th.in.ffc.app.form.screening;

import androidx.fragment.app.Fragment;

public class FragmentTabInfo {
    private Fragment fragment;
    private String tabTitle;

    public FragmentTabInfo(Fragment fragment, String tabTitle) {
        this.fragment = fragment;
        this.tabTitle = tabTitle;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public String getTabTitle() {
        return tabTitle;
    }
}

