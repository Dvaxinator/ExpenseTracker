package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.expensetracker.databinding.ActivityUserMenuBinding;

public class UserMenu extends AppCompatActivity implements AddExpenseFragment.OnExpenseAddedListener {

    private ActivityUserMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        HomePageFragment homePageFragment = new HomePageFragment();
        replaceFragment(homePageFragment, "HOME_FRAGMENT");
        binding.bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                replaceFragment(homePageFragment, "HOME_FRAGMENT");
            } else if (id == R.id.account) {
                replaceFragment(new AccountFragment(), "ACCOUNT_FRAGMENT");
            } else if (id == R.id.reports) {
                replaceFragment(new ReportsFragment(), "REPORTS_FRAGMENT");
            } else if (id == R.id.settings) {
                replaceFragment(new SettingsFragment(), "SETTINGS_FRAGMENT");
            }
            return true;
        });
    }

    @Override
    public void onExpenseAdded(MyDataSet expense) {
        HomePageFragment homePageFragment = (HomePageFragment) getSupportFragmentManager().findFragmentByTag("HOME_FRAGMENT");
        if (homePageFragment != null) {
            homePageFragment.onExpenseAdded(expense);
        } else {
            Log.e("UserMenu", "HomePageFragment not found");
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment, tag);
        fragmentTransaction.commit();
    }
}

