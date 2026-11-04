package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.db.DatabaseHelper;
import com.example.expensemanager.model.Expense;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "EXPENSE_ID";

    private TextView tvDetailName;
    private TextView tvDetailAmount;
    private TextView tvDetailCategory;
    private TextView tvDetailDate;
    private TextView tvDetailNote;
    private Button btnEditExpense;
    private Button btnDeleteExpense;
    private Button btnBack;
    private View btnDetailHeaderBack;

    private DatabaseHelper dbHelper;
    private int expenseId = -1;
    private final DecimalFormat formatter = new DecimalFormat("#,### VNĐ");
    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_detail);

        dbHelper = new DatabaseHelper(this);
        expenseId = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);

        initViews();
        setupListeners();
    }

    private void initViews() {
        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailAmount = findViewById(R.id.tvDetailAmount);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailDate = findViewById(R.id.tvDetailDate);
        tvDetailNote = findViewById(R.id.tvDetailNote);
        btnEditExpense = findViewById(R.id.btnEditExpense);
        btnDeleteExpense = findViewById(R.id.btnDeleteExpense);
        btnBack = findViewById(R.id.btnBack);
        btnDetailHeaderBack = findViewById(R.id.btnDetailHeaderBack);
    }

    private void setupListeners() {
        btnEditExpense.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseDetailActivity.this, AddEditExpenseActivity.class);
            intent.putExtra(AddEditExpenseActivity.EXTRA_EXPENSE_ID, expenseId);
            startActivity(intent);
        });

        btnDeleteExpense.setOnClickListener(v -> showDeleteConfirmDialog());

        btnBack.setOnClickListener(v -> finish());

        if (btnDetailHeaderBack != null) {
            btnDetailHeaderBack.setOnClickListener(v -> finish());
        }
    }

    private void loadExpenseDetail() {
        if (expenseId == -1) {
            Toast.makeText(this, R.string.err_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Expense expense = dbHelper.getExpenseById(expenseId);
        if (expense == null) {
            finish();
            return;
        }

        tvDetailName.setText(expense.getName());
        tvDetailAmount.setText(formatter.format(expense.getAmount()));
        tvDetailCategory.setText(expense.getCategory());

        String displayDate = expense.getDate();
        try {
            Date d = dbDateFormat.parse(expense.getDate());
            if (d != null) {
                displayDate = displayDateFormat.format(d);
            }
        } catch (ParseException ignored) {
        }
        tvDetailDate.setText(displayDate);

        String note = expense.getNote();
        if (TextUtils.isEmpty(note)) {
            tvDetailNote.setText(R.string.text_none);
        } else {
            tvDetailNote.setText(note);
        }
    }

    private void showDeleteConfirmDialog() {
        // Hộp thoại xác nhận bo góc 24dp, nút Xóa màu cam
        new AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_delete_message)
                .setNegativeButton(R.string.dialog_btn_cancel, null)
                .setPositiveButton(R.string.dialog_btn_delete, (dialog, which) -> {
                    int rows = dbHelper.deleteExpense(expenseId);
                    if (rows > 0) {
                        Toast.makeText(this, R.string.msg_delete_success, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenseDetail();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
