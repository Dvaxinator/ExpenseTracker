package com.example.expensetracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.expensetracker.databinding.ActivityUserMenuBinding;

public class UserMenu extends AppCompatActivity {

    private ActivityUserMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new AccountFragment());
        binding.bottomNavigationView.getMenu().findItem(R.id.account).setChecked(true);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.account) {
                replaceFragment(new AccountFragment());
            } else if (id == R.id.settings) {
                replaceFragment(new SettingsFragment());
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}

