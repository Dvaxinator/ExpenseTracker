package com.example.expensetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomePageFragment extends Fragment implements AddExpenseFragment.OnExpenseAddedListener, MyAdapter.OnExpenseDeletedListener {

    Button add;
    RecyclerView recyclerView;
    ArrayList<MyDataSet> dataSet = new ArrayList<>();
    private MyAdapter myAdapter;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("expenses").child(user.getUid());
        }

        add.setOnClickListener(v -> {
            AddExpenseFragment addExpenseDialog = new AddExpenseFragment();
            addExpenseDialog.show(getActivity().getSupportFragmentManager().beginTransaction(), "addExpenseDialog");
        });

        return view;
    }

    @Override
    public void onExpenseAdded(MyDataSet expense) {
        String key = mDatabase.push().getKey();
        if (key != null) {
            mDatabase.child(key).setValue(expense).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("HomePageFragment", "Expense added to Firebase successfully.");
                } else {
                    Log.w("HomePageFragment", "Error adding expense to Firebase.", task.getException());
                }
            });
        }
    }

    @Override
    public void onExpenseDeleted(String key) {
        if (key == null || key.trim().isEmpty()) {
            Log.e("ExpenseDeletion", "Attempted to delete an expense with no key.");
            if (isAdded()) {
                Toast.makeText(getContext(), "Error: No key for expense.", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mDatabase.child(key).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Expense Deleted!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Failed to delete expense!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(); // Call a method to load data and update the adapter.
    }

    private void loadData() {
        if (mDatabase != null) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dataSet.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        MyDataSet expense = snapshot.getValue(MyDataSet.class);
                        if (expense != null) {
                            expense.key = snapshot.getKey();
                            dataSet.add(expense);
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.w("HomePageFragment", "loadExpenses:onCancelled", error.toException());
                }
            });
        }
    }
}