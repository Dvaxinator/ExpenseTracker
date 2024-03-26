package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.expensetracker.databinding.ActivityUserMenuBinding;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;



public class UserMenu extends AppCompatActivity implements AddExpenseFragment.OnExpenseAddedListener {

    private ActivityUserMenuBinding binding;
    private String currentFragmentTag = "HOME_FRAGMENT"; // Default fragment


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences appSettingsPrefs = getSharedPreferences("AppSettingsPrefs", 0);
        boolean isDarkModeOn = appSettingsPrefs.getBoolean("DarkMode", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        binding = ActivityUserMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Restore the current fragment tag
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString("CURRENT_FRAGMENT_TAG", "HOME_FRAGMENT");
        }

        // Initial fragment placement
        if (savedInstanceState == null) {
            replaceFragment(new HomePageFragment(), currentFragmentTag);
        } else {
            // Ensure the correct fragment is displayed after recreation
            replaceFragment(getSupportFragmentManager().findFragmentByTag(currentFragmentTag), currentFragmentTag);
        }

        binding.bottomNavigationView.getMenu().findItem(R.id.navigation_home).setChecked(true);
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                currentFragmentTag = "HOME_FRAGMENT";
                replaceFragment(new HomePageFragment(), currentFragmentTag);
            } else if (id == R.id.account) {
                currentFragmentTag = "ACCOUNT_FRAGMENT";
                replaceFragment(new AccountFragment(), currentFragmentTag);
            } else if (id == R.id.reports) {
                currentFragmentTag = "REPORTS_FRAGMENT";
                replaceFragment(new ReportsFragment(), currentFragmentTag);
            } else if (id == R.id.settings) {
                currentFragmentTag = "SETTINGS_FRAGMENT";
                replaceFragment(new SettingsFragment(), currentFragmentTag);
            }
            return true;
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Save the current fragment tag
        outState.putString("CURRENT_FRAGMENT_TAG", currentFragmentTag);
        super.onSaveInstanceState(outState);
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

    private void replaceFragment(Fragment fragment, String tag) {
        if (fragment == null) {
            return;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, tag).commit();
    }
}

