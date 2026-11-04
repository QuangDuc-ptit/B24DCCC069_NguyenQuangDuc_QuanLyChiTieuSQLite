package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.adapter.ExpenseAdapter;
import com.example.expensemanager.db.DatabaseHelper;
import com.example.expensemanager.model.Expense;
import com.example.expensemanager.view.CircularProgressView;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etSearch;
    private Spinner spCategoryFilter;
    private TextView tvTotalSummary;
    private TextView tvEmptyList;
    private RecyclerView rvExpenses;
    private Button btnAddExpense;
    private Button btnStatistics;

    // View mở rộng giao diện Mockup
    private TextView tvMainHeadline;
    private CircularProgressView cpvMainRatio;
    private View flLogout;
    private View llEmptyState;

    private DatabaseHelper dbHelper;
    private ExpenseAdapter expenseAdapter;
    private final DecimalFormat formatter = new DecimalFormat("#,### VNĐ");

    private final String[] categories = new String[]{
            "Tất cả danh mục",
            "Ăn uống",
            "Đi lại",
            "Học tập",
            "Giải trí",
            "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupHeadline();
        setupCategorySpinner();
        setupRecyclerView();
        setupBottomBar();
        setupListeners();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        spCategoryFilter = findViewById(R.id.spCategoryFilter);
        tvTotalSummary = findViewById(R.id.tvTotalSummary);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        rvExpenses = findViewById(R.id.rvExpenses);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnStatistics = findViewById(R.id.btnStatistics);

        tvMainHeadline = findViewById(R.id.tvMainHeadline);
        cpvMainRatio = findViewById(R.id.cpvMainRatio);
        flLogout = findViewById(R.id.flLogout);
        llEmptyState = findViewById(R.id.llEmptyState);
    }

    private void setupHeadline() {
        if (tvMainHeadline != null) {
            String styledTitle = getString(R.string.headline_part1) + "<font color='#F5A94A'>" + getString(R.string.headline_part2) + "</font>";
            tvMainHeadline.setText(Html.fromHtml(styledTitle));
        }

        if (flLogout != null) {
            flLogout.setOnClickListener(v -> showLogoutDialog());
        }
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_logout_title)
                .setMessage(R.string.dialog_logout_message)
                .setNegativeButton(R.string.dialog_btn_cancel, null)
                .setPositiveButton(R.string.btn_logout_confirm, (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .show();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategoryFilter.setAdapter(spinnerAdapter);

        spCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupRecyclerView() {
        // Dạng lưới 2 cột chuẩn mockup thẻ Productive
        rvExpenses.setLayoutManager(new GridLayoutManager(this, 2));
        expenseAdapter = new ExpenseAdapter(expense -> {
            Intent intent = new Intent(MainActivity.this, ExpenseDetailActivity.class);
            intent.putExtra("EXPENSE_ID", expense.getId());
            startActivity(intent);
        });
        rvExpenses.setAdapter(expenseAdapter);
    }

    private void setupBottomBar() {
        View navHome = findViewById(R.id.navHome);
        View navCalendar = findViewById(R.id.navCalendar);
        View navAdd = findViewById(R.id.navAdd);
        View navStatistics = findViewById(R.id.navStatistics);
        View navProfile = findViewById(R.id.navProfile);

        if (navHome != null) {
            navHome.setOnClickListener(v -> {
                // Đang ở Home
            });
        }

        if (navCalendar != null) {
            navCalendar.setOnClickListener(v ->
                    Toast.makeText(MainActivity.this, R.string.toast_coming_soon, Toast.LENGTH_SHORT).show()
            );
        }

        if (navAdd != null) {
            navAdd.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddEditExpenseActivity.class);
                startActivity(intent);
            });
        }

        if (navStatistics != null) {
            navStatistics.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                startActivity(intent);
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v ->
                    Toast.makeText(MainActivity.this, R.string.toast_coming_soon, Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupListeners() {
        // Gõ tìm kiếm đến đâu cập nhật đến đó
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadData();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Nút Thêm mới pill cam
        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditExpenseActivity.class);
            startActivity(intent);
        });

        // Nút Thống kê pill cam
        btnStatistics.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Tải dữ liệu từ database và cập nhật giao diện
     */
    private void loadData() {
        String keyword = etSearch.getText() != null ? etSearch.getText().toString().trim() : "";
        String selectedCategory = spCategoryFilter.getSelectedItem() != null ?
                spCategoryFilter.getSelectedItem().toString() : categories[0];

        List<Expense> list = dbHelper.getExpenses(keyword, selectedCategory);
        long currentTotal = dbHelper.getTotal(keyword, selectedCategory);
        long overallTotal = dbHelper.getTotal(null, null);

        expenseAdapter.setExpenseList(list);

        // Hiển thị tổng tiền và số khoản chi
        String summaryText = formatter.format(currentTotal) + "\n(" + list.size() + " khoản)";
        tvTotalSummary.setText(summaryText);

        // Cập nhật vòng tròn tiến độ canvas (% chi tiêu theo bộ lọc hiện tại)
        if (cpvMainRatio != null) {
            int ratio = 100;
            if (overallTotal > 0) {
                ratio = (int) Math.round(((double) currentTotal / overallTotal) * 100);
            } else if (list.isEmpty()) {
                ratio = 0;
            }
            cpvMainRatio.setProgress(ratio);
        }

        // Xử lý danh sách rỗng
        if (list.isEmpty()) {
            rvExpenses.setVisibility(View.GONE);
            if (llEmptyState != null) {
                llEmptyState.setVisibility(View.VISIBLE);
            }
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            rvExpenses.setVisibility(View.VISIBLE);
            if (llEmptyState != null) {
                llEmptyState.setVisibility(View.GONE);
            }
            tvEmptyList.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
