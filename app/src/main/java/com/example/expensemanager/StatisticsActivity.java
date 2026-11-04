package com.example.expensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.expensemanager.db.DatabaseHelper;
import com.example.expensemanager.view.CircularProgressView;

import java.text.DecimalFormat;
import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {

    private TextView tvStatTotalExpense;
    private LinearLayout llCategoriesContainer;
    private Button btnStatBack;
    private CircularProgressView cpvStatHero;
    private View btnStatHeaderBack;

    private DatabaseHelper dbHelper;
    private final DecimalFormat formatter = new DecimalFormat("#,### VNĐ");

    // Đủ 5 danh mục theo đặc tả
    private final String[] allCategories = new String[]{
            "Ăn uống",
            "Đi lại",
            "Học tập",
            "Giải trí",
            "Khác"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupBottomBar();
        loadStatistics();
    }

    private void initViews() {
        tvStatTotalExpense = findViewById(R.id.tvStatTotalExpense);
        llCategoriesContainer = findViewById(R.id.llCategoriesContainer);
        btnStatBack = findViewById(R.id.btnStatBack);
        cpvStatHero = findViewById(R.id.cpvStatHero);
        btnStatHeaderBack = findViewById(R.id.btnStatHeaderBack);

        btnStatBack.setOnClickListener(v -> finish());
        if (btnStatHeaderBack != null) {
            btnStatHeaderBack.setOnClickListener(v -> finish());
        }
    }

    private void setupBottomBar() {
        View navHome = findViewById(R.id.navHome);
        View navCalendar = findViewById(R.id.navCalendar);
        View navAdd = findViewById(R.id.navAdd);
        View navStatistics = findViewById(R.id.navStatistics);
        View navProfile = findViewById(R.id.navProfile);

        ImageView ivNavHome = findViewById(R.id.ivNavHome);
        ImageView ivNavStatistics = findViewById(R.id.ivNavStatistics);
        View dotNavHome = findViewById(R.id.dotNavHome);
        View dotNavStatistics = findViewById(R.id.dotNavStatistics);

        // Chuyển trạng thái active sang tab Thống kê
        if (dotNavHome != null) dotNavHome.setVisibility(View.INVISIBLE);
        if (dotNavStatistics != null) dotNavStatistics.setVisibility(View.VISIBLE);
        if (ivNavHome != null) ivNavHome.setColorFilter(ContextCompat.getColor(this, R.color.text_muted));
        if (ivNavStatistics != null) ivNavStatistics.setColorFilter(ContextCompat.getColor(this, R.color.petrol_dark));

        if (navHome != null) {
            navHome.setOnClickListener(v -> finish());
        }

        if (navCalendar != null) {
            navCalendar.setOnClickListener(v ->
                    Toast.makeText(StatisticsActivity.this, R.string.toast_coming_soon, Toast.LENGTH_SHORT).show()
            );
        }

        if (navAdd != null) {
            navAdd.setOnClickListener(v -> {
                Intent intent = new Intent(StatisticsActivity.this, AddEditExpenseActivity.class);
                startActivity(intent);
            });
        }

        if (navStatistics != null) {
            navStatistics.setOnClickListener(v -> {
                // Đang ở tab Thống kê
            });
        }

        if (navProfile != null) {
            navProfile.setOnClickListener(v ->
                    Toast.makeText(StatisticsActivity.this, R.string.toast_coming_soon, Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void loadStatistics() {
        llCategoriesContainer.removeAllViews();

        // 1. Lấy tổng toàn bộ chi tiêu
        long totalExpense = dbHelper.getTotal(null, null);
        tvStatTotalExpense.setText(formatter.format(totalExpense));

        if (cpvStatHero != null) {
            cpvStatHero.setProgress(totalExpense > 0 ? 100 : 0);
        }

        // 2. Lấy tổng tiền gom nhóm theo từng danh mục từ SQLite GROUP BY
        Map<String, Long> categoryMap = dbHelper.getTotalByCategory();

        LayoutInflater inflater = LayoutInflater.from(this);

        // 3. Hiển thị đủ 5 danh mục, kể cả danh mục 0 đ, 0%
        for (int i = 0; i < allCategories.length; i++) {
            String cat = allCategories[i];
            long catAmount = categoryMap.containsKey(cat) ? categoryMap.get(cat) : 0L;

            // Xử lý chia cho 0 khi tính phần trăm nếu tổng = 0
            int percentage = 0;
            if (totalExpense > 0) {
                percentage = (int) Math.round(((double) catAmount / totalExpense) * 100);
            }

            View itemView = inflater.inflate(R.layout.item_statistic, llCategoriesContainer, false);
            View cardContainer = itemView.findViewById(R.id.cardStatContainer);
            ImageView ivIcon = itemView.findViewById(R.id.ivStatIcon);
            TextView tvCat = itemView.findViewById(R.id.tvStatCategory);
            TextView tvDetail = itemView.findViewById(R.id.tvStatDetail);
            ProgressBar pb = itemView.findViewById(R.id.pbStatCategory);

            // Xen kẽ thẻ màu kem đào và màu trắng
            if (i % 2 == 0) {
                cardContainer.setBackgroundResource(R.drawable.bg_card_peach);
            } else {
                cardContainer.setBackgroundResource(R.drawable.bg_card_white);
            }

            // Gán icon danh mục tương ứng
            if (cat.equalsIgnoreCase("Ăn uống")) {
                ivIcon.setImageResource(R.drawable.ic_cat_food);
            } else if (cat.equalsIgnoreCase("Đi lại")) {
                ivIcon.setImageResource(R.drawable.ic_cat_transport);
            } else if (cat.equalsIgnoreCase("Học tập")) {
                ivIcon.setImageResource(R.drawable.ic_cat_study);
            } else if (cat.equalsIgnoreCase("Giải trí")) {
                ivIcon.setImageResource(R.drawable.ic_cat_entertainment);
            } else {
                ivIcon.setImageResource(R.drawable.ic_cat_other);
            }

            tvCat.setText(cat);
            tvDetail.setText(getString(R.string.format_stat_detail, formatter.format(catAmount), percentage));
            pb.setProgress(percentage);

            llCategoriesContainer.addView(itemView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStatistics();
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
