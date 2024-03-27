package com.example.expensetracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddExpenseFragment extends DialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText addDescription, addAmount, addCategory, addDate;
    Button add, calendar_btn;
    Calendar calendar;
    int day, month, year;


    public AddExpenseFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };

        // Set dialog dimensions
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        return dialog;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddExpenseFragment newInstance(String param1, String param2) {
        AddExpenseFragment fragment = new AddExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public interface OnExpenseAddedListener {
        void onExpenseAdded(MyDataSet expense);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_expense, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Adjust dialog size after it's been created
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addDescription = view.findViewById(R.id.description);
        addAmount = view.findViewById(R.id.amount);
        addCategory = view.findViewById(R.id.category);
        addDate = view.findViewById(R.id.date);
        add = view.findViewById(R.id.confirm_add);
        calendar_btn = view.findViewById(R.id.calendar_btn);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        displayDate(year, month, day);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = addDescription.getText().toString().trim();
                String amountText = addAmount.getText().toString().trim();
                String category = addCategory.getText().toString().trim();
                String date = addDate.getText().toString().trim();

                if (description.isEmpty()) {
                    addDescription.setError("Description is required");
                    addDescription.requestFocus();
                    return;
                }
                if (amountText.isEmpty()) {
                    addAmount.setError("Amount is required");
                    addDescription.requestFocus();
                    return;
                }
                if (category.isEmpty()) {
                    addCategory.setError("Category is required");
                    addDescription.requestFocus();
                    return;
                }
                if (date.isEmpty()) {
                    addDate.setError("Date is required");
                    addDescription.requestFocus();
                    return;
                }

                double amount = Double.parseDouble(amountText);

                // Create a new MyDataSet object with the entered data
                MyDataSet expense = new MyDataSet(description, amount, category, date);

                // Pass the expense data to the activity
                if (getActivity() instanceof OnExpenseAddedListener) {
                    ((OnExpenseAddedListener) getActivity()).onExpenseAdded(expense);
                    Toast.makeText(getContext(), "Expense Added!", Toast.LENGTH_SHORT).show();
                }
                replaceFragment(new HomePageFragment(), "HOME_FRAGMENT");
                dismiss();
            }
        });

        calendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), date, year, month, day);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            displayDate(year, month, dayOfMonth);
        }
    };

    private void displayDate(int year, int month, int day) {
        String date = day + "-" + (month + 1) + "-" + year;
        addDate.setText(date);
    }

    private void replaceFragment(Fragment fragment, String tag) {
        if (fragment == null) {
            return;
        }
        getParentFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, tag).commit();
    }
}