package com.example.expensemanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.R;
import com.example.expensemanager.model.Expense;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Expense expense);
    }

    private List<Expense> expenseList = new ArrayList<>();
    private final OnItemClickListener listener;
    private final DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    public ExpenseAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setExpenseList(List<Expense> list) {
        this.expenseList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.tvName.setText(expense.getName());
        holder.tvAmount.setText(formatter.format(expense.getAmount()));
        holder.tvCategory.setText(expense.getCategory());

        // Chuyển đổi định dạng ngày yyyy-MM-dd sang dd/MM/yyyy
        String displayDate = expense.getDate();
        try {
            Date d = dbDateFormat.parse(expense.getDate());
            if (d != null) {
                displayDate = displayDateFormat.format(d);
            }
        } catch (ParseException ignored) {
        }
        holder.tvDate.setText(displayDate);

        // Xen kẽ thẻ màu kem đào và màu trắng chuẩn mockup
        if (position % 2 == 0) {
            holder.cardContainer.setBackgroundResource(R.drawable.bg_card_peach);
        } else {
            holder.cardContainer.setBackgroundResource(R.drawable.bg_card_white);
        }

        // Chọn icon danh mục tương ứng
        String cat = expense.getCategory();
        if (cat != null) {
            if (cat.equalsIgnoreCase("Ăn uống")) {
                holder.ivIcon.setImageResource(R.drawable.ic_cat_food);
            } else if (cat.equalsIgnoreCase("Đi lại")) {
                holder.ivIcon.setImageResource(R.drawable.ic_cat_transport);
            } else if (cat.equalsIgnoreCase("Học tập")) {
                holder.ivIcon.setImageResource(R.drawable.ic_cat_study);
            } else if (cat.equalsIgnoreCase("Giải trí")) {
                holder.ivIcon.setImageResource(R.drawable.ic_cat_entertainment);
            } else {
                holder.ivIcon.setImageResource(R.drawable.ic_cat_other);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(expense);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        View cardContainer;
        ImageView ivIcon;
        TextView tvName, tvAmount, tvCategory, tvDate;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            cardContainer = itemView.findViewById(R.id.cardExpenseContainer);
            ivIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvName = itemView.findViewById(R.id.tvExpenseName);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvCategory = itemView.findViewById(R.id.tvExpenseCategory);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}
