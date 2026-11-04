package com.example.expensemanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.expensemanager.db.DatabaseHelper;
import com.example.expensemanager.model.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddEditExpenseActivity extends AppCompatActivity {

    public static final String EXTRA_EXPENSE_ID = "EXPENSE_ID";

    private TextView tvFormTitle;
    private EditText etExpenseName;
    private EditText etExpenseAmount;
    private Spinner spCategory;
    private DatePicker dpExpenseDate;
    private EditText etExpenseNote;
    private Button btnSaveExpense;
    private View btnHeaderBack;

    private DatabaseHelper dbHelper;
    private int expenseId = -1;
    private boolean isEditMode = false;

    private final String[] categories = new String[]{
            "Ăn uống",
            "Đi lại",
            "Học tập",
            "Giải trí",
            "Khác"
    };

    private final SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_expense);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupCategorySpinner();

        // Không cho phép chọn ngày trong tương lai
        dpExpenseDate.setMaxDate(System.currentTimeMillis());

        // Kiểm tra xem là chế độ Thêm mới hay Sửa
        if (getIntent().hasExtra(EXTRA_EXPENSE_ID)) {
            expenseId = getIntent().getIntExtra(EXTRA_EXPENSE_ID, -1);
            if (expenseId != -1) {
                isEditMode = true;
            }
        }

        if (isEditMode) {
            setTitle(R.string.title_edit_expense);
            tvFormTitle.setText(R.string.title_edit_expense);
            btnSaveExpense.setText(R.string.btn_update_expense);
            loadExpenseData(expenseId);
        } else {
            setTitle(R.string.title_add_expense);
            tvFormTitle.setText(R.string.title_add_expense);
            btnSaveExpense.setText(R.string.btn_save_expense);
        }

        btnSaveExpense.setOnClickListener(v -> saveExpense());

        if (btnHeaderBack != null) {
            btnHeaderBack.setOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        etExpenseName = findViewById(R.id.etExpenseName);
        etExpenseAmount = findViewById(R.id.etExpenseAmount);
        spCategory = findViewById(R.id.spCategory);
        dpExpenseDate = findViewById(R.id.dpExpenseDate);
        etExpenseNote = findViewById(R.id.etExpenseNote);
        btnSaveExpense = findViewById(R.id.btnSaveExpense);
        btnHeaderBack = findViewById(R.id.btnHeaderBack);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(spinnerAdapter);
    }

    private void loadExpenseData(int id) {
        Expense expense = dbHelper.getExpenseById(id);
        if (expense == null) {
            Toast.makeText(this, R.string.err_not_found, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        etExpenseName.setText(expense.getName());
        etExpenseAmount.setText(String.valueOf(expense.getAmount()));
        etExpenseNote.setText(expense.getNote() != null ? expense.getNote() : "");

        // Chọn đúng danh mục
        for (int i = 0; i < categories.length; i++) {
            if (categories[i].equalsIgnoreCase(expense.getCategory())) {
                spCategory.setSelection(i);
                break;
            }
        }

        // Chọn đúng ngày trong DatePicker (từ yyyy-MM-dd)
        try {
            Date date = dbDateFormat.parse(expense.getDate());
            if (date != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                dpExpenseDate.updateDate(
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                );
            }
        } catch (ParseException ignored) {
        }
    }

    private void saveExpense() {
        String name = etExpenseName.getText().toString().trim();
        String amountStr = etExpenseAmount.getText().toString().trim();
        String note = etExpenseNote.getText().toString().trim();
        String category = spCategory.getSelectedItem().toString();

        // Validate tên không được để trống
        if (TextUtils.isEmpty(name)) {
            etExpenseName.setError(getString(R.string.err_empty_name));
            etExpenseName.requestFocus();
            return;
        }

        // Validate số tiền
        if (TextUtils.isEmpty(amountStr)) {
            etExpenseAmount.setError(getString(R.string.err_invalid_amount));
            etExpenseAmount.requestFocus();
            return;
        }

        long amount;
        try {
            amount = Long.parseLong(amountStr);
            if (amount <= 0) {
                etExpenseAmount.setError(getString(R.string.err_invalid_amount));
                etExpenseAmount.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etExpenseAmount.setError(getString(R.string.err_invalid_amount));
            etExpenseAmount.requestFocus();
            return;
        }

        // Lấy ngày đã chọn từ DatePicker
        int day = dpExpenseDate.getDayOfMonth();
        int month = dpExpenseDate.getMonth();
        int year = dpExpenseDate.getYear();

        Calendar selectedCal = Calendar.getInstance();
        selectedCal.set(year, month, day, 23, 59, 59);

        // Kiểm tra không được chọn ngày tương lai
        Calendar nowCal = Calendar.getInstance();
        if (selectedCal.after(nowCal)) {
            Toast.makeText(this, R.string.err_future_date, Toast.LENGTH_SHORT).show();
            return;
        }

        // Định dạng yyyy-MM-dd để lưu vào SQLite
        Calendar saveCal = Calendar.getInstance();
        saveCal.set(year, month, day);
        String dateStr = dbDateFormat.format(saveCal.getTime());

        if (isEditMode) {
            Expense expense = new Expense(expenseId, name, amount, category, dateStr, note);
            int rows = dbHelper.updateExpense(expense);
            if (rows > 0) {
                Toast.makeText(this, R.string.msg_update_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Expense expense = new Expense(name, amount, category, dateStr, note);
            long newId = dbHelper.insertExpense(expense);
            if (newId != -1) {
                Toast.makeText(this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
