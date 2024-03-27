package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    ArrayList<MyDataSet> dataList;
    Context context;
    private OnExpenseDeletedListener expenseDeletedListener;

    public MyAdapter(ArrayList<MyDataSet> data, Context context, OnExpenseDeletedListener listener) {
        this.dataList = data;
        this.context = context;
        this.expenseDeletedListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_expenses, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MyDataSet data = dataList.get(position);
        holder.cardDescription.setText(data.getDescription());
        holder.cardAmount.setText(String.valueOf(data.getAmount()));
        holder.cardCategory.setText(data.getCategory());
        holder.cardDate.setText(data.getDate().toString());

        // Implementing long click listener for the card view
        holder.cardView.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Deletion");
            builder.setMessage("Are you sure you want to delete this expense?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                String key = dataList.get(holder.getAdapterPosition()).key;
                if (expenseDeletedListener != null) {
                    expenseDeletedListener.onExpenseDeleted(key);
                }
                dialog.dismiss();
            });
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.show();
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cardDescription, cardAmount, cardCategory, cardDate;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardDescription = itemView.findViewById(R.id.card_description);
            cardAmount = itemView.findViewById(R.id.card_amount);
            cardView = itemView.findViewById(R.id.card_view);
            cardCategory = itemView.findViewById(R.id.card_category);
            cardDate = itemView.findViewById(R.id.card_date);
        }
    }

    public interface OnExpenseDeletedListener {
        void onExpenseDeleted(String key);
    }
}
