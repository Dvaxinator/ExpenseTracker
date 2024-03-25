package com.example.expensetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class HomePageFragment extends Fragment implements AddExpenseFragment.OnExpenseAddedListener, MyAdapter.OnExpenseDeletedListener {

    Button add;
    RecyclerView recyclerView;
    ArrayList<MyDataSet> dataSet = new ArrayList<>();
    private MyAdapter myAdapter;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_page, container, false);

        add = view.findViewById(R.id.addExpenseButton);
        recyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        myAdapter = new MyAdapter(dataSet, getActivity(), this); // getActivity() is used to provide a context
        recyclerView.setAdapter(myAdapter);

        // Retrieve saved expenses data from SharedPreferences
        sharedPreferences = getActivity().getSharedPreferences("expenses", Context.MODE_PRIVATE);
        Set<String> expensesSet = sharedPreferences.getStringSet("expenses_set", new HashSet<String>());
        for (String expense : expensesSet) {
            String[] expenseData = expense.split(",");
            dataSet.add(new MyDataSet(expenseData[0], Double.parseDouble(expenseData[1]), expenseData[2], expenseData[3]));
        }
        myAdapter.notifyDataSetChanged();

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddExpenseFragment addExpenseDialog = new AddExpenseFragment();
                addExpenseDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), "addExpenseDialog");
            }
        });

        return view;
    }

    @Override
    public void onExpenseAdded(MyDataSet expense) {
        dataSet.add(expense);
        myAdapter.notifyDataSetChanged();
        Log.d("Expense added", expense.getDescription());
        updateSharedPreferences();
    }

    @Override
    public void onExpenseDeleted() {
        updateSharedPreferences();
    }

    private void updateSharedPreferences() {
        Set<String> expensesSet = new HashSet<>();
        for (MyDataSet data : dataSet) {
            expensesSet.add(data.getDescription() + "," + data.getAmount() + "," + data.getCategory() + "," + data.getDate());
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("expenses_set", expensesSet);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Call a method to load data and update the adapter.
    }

    private void loadData() {
        // Clear the existing dataset to avoid duplicates.
        dataSet.clear();

        // Reload saved expenses data from SharedPreferences.
        Set<String> expensesSet = sharedPreferences.getStringSet("expenses_set", new HashSet<String>());
        for (String expense : expensesSet) {
            String[] expenseData = expense.split(",");
            dataSet.add(new MyDataSet(expenseData[0], Double.parseDouble(expenseData[1]), expenseData[2], expenseData[3]));
        }
        myAdapter.notifyDataSetChanged(); // Notify the adapter about the dataset change.
    }
}