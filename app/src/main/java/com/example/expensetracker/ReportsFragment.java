package com.example.expensetracker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportsFragment extends Fragment {

    private DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference("expenses").child(user.getUid());
            fetchExpensesAndSetupChart(view);
        } else {
            Log.e("ReportsFragment", "User is not logged in");
        }

        return view;
    }

    private void fetchExpensesAndSetupChart(View view) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Float> categoryTotals = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MyDataSet expenseData = snapshot.getValue(MyDataSet.class);
                    if (expenseData != null) {
                        String category = expenseData.getCategory();
                        float amount = (float) expenseData.getAmount();
                        categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);
                    }
                }
                ArrayList<PieEntry> entries = new ArrayList<>();
                for (Map.Entry<String, Float> entry : categoryTotals.entrySet()) {
                    entries.add(new PieEntry(entry.getValue(), entry.getKey()));
                }
                setupPieChart(view, entries);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("ReportsFragment", "loadExpenses:onCancelled", databaseError.toException());
            }
        });
    }

    private void setupPieChart(View view, ArrayList<PieEntry> entries) {
        PieChart pieChart = view.findViewById(R.id.pie_chart);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setCenterText("Expenses");
        pieChart.setCenterTextSize(20);
        pieChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD);
        pieChart.setHoleRadius(58f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.animateY(1400, Easing.EaseInOutQuad);

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        List<Integer> darkColors = new ArrayList<>();
        darkColors.add(Color.parseColor("#1b5e20")); // Dark Green
        darkColors.add(Color.parseColor("#0d47a1")); // Dark Blue
        darkColors.add(Color.parseColor("#b71c1c")); // Dark Red
        darkColors.add(Color.parseColor("#3e2723")); // Deep Brown
        darkColors.add(Color.parseColor("#37474F")); // Deep Blue Grey
        darkColors.add(Color.parseColor("#263238")); // Blue Grey
        darkColors.add(Color.parseColor("#212121")); // Deep Grey
        darkColors.add(Color.parseColor("#004D40")); // Teal
        darkColors.add(Color.parseColor("#1A237E")); // Indigo
        darkColors.add(Color.parseColor("#BF360C")); // Deep Orange
        darkColors.add(Color.parseColor("#880E4F")); // Deep Purple
        darkColors.add(Color.parseColor("#B71C1C")); // Red
        darkColors.add(Color.parseColor("#01579B")); // Light Blue
        darkColors.add(Color.parseColor("#827717")); // Lime
        darkColors.add(Color.parseColor("#FF6F00")); // Amber

        dataSet.setColors(darkColors);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Create an instance of NumberFormat for the default locale
                NumberFormat numberFormat = NumberFormat.getInstance();

                // Convert the float value to a string with a comma for each thousand
                return "$" + numberFormat.format(value);
            }
        });

        // Set text color
        dataSet.setValueTextColor(safeGetThemeColor(R.attr.colorOnBackground));
        dataSet.setValueTextSize(10f);

        pieChart.getLegend().setTextColor(safeGetThemeColor(R.attr.colorOnBackground));

        dataSet.setValueLineColor(safeGetThemeColor(R.attr.colorOnBackground));
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.3f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.setExtraOffsets(20, 20, 20, 20);
        pieChart.invalidate(); // Refresh the chart
    }


    private int safeGetThemeColor(int attributeColor) {
        Context context = getContext();
        if (context != null) {
            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = context.getTheme();
            if (theme != null) {
                theme.resolveAttribute(attributeColor, typedValue, true);
                return typedValue.data;
            }
        }
        return Color.TRANSPARENT; // Fallback color
    }
}
