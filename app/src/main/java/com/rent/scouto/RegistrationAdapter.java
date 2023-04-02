package com.rent.scouto;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RegistrationAdapter extends FragmentStateAdapter {


    public RegistrationAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginFragment();
        }
        else {
            return new SignupFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
