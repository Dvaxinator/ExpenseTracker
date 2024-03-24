package com.example.expensetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
        holder.imageView.setImageResource(R.drawable.expense_symbol);
        holder.cardDescription.setText(data.getDescription());
        holder.cardAmount.setText(String.valueOf(data.getAmount()));
        holder.cardCategory.setText(data.getCategory());
        holder.cardDate.setText(data.getDate().toString());

        // Implementing long click listener for the card view
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Create and show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Deletion");
                builder.setMessage("Are you sure you want to delete this expense?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Remove the expense from the dataset
                        dataList.remove(position);
                        // Notify adapter of the change
                        notifyDataSetChanged();
                        // Notify user that the expense has been deleted
                        Toast.makeText(context, "Expense Deleted!", Toast.LENGTH_SHORT).show();
                        // Call the listener to inform about expense deletion
                        expenseDeletedListener.onExpenseDeleted();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Dismiss the dialog if the user clicks "No"
                        dialog.dismiss();
                    }
                });
                // Show the dialog
                builder.show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView cardDescription, cardAmount, cardCategory, cardDate;
        CardView cardView;
        ImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            cardDescription = itemView.findViewById(R.id.card_description);
            cardAmount = itemView.findViewById(R.id.card_amount);
            cardView = itemView.findViewById(R.id.card_view);
            cardCategory = itemView.findViewById(R.id.card_category);
            cardDate = itemView.findViewById(R.id.card_date);
        }
    }

    public interface OnExpenseDeletedListener {
        void onExpenseDeleted();
    }
}
