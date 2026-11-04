package com.example.expensemanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.expensemanager.model.Expense;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Lớp quản lý cơ sở dữ liệu SQLite: expense_manager.db
 * Đầy đủ 8 phương thức tối thiểu theo yêu cầu bài thực hành
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "expense_manager.db";
    public static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COL_USER_ID = "id";
    public static final String COL_USER_USERNAME = "username";
    public static final String COL_USER_PASSWORD = "password";

    // Bảng expenses
    public static final String TABLE_EXPENSES = "expenses";
    public static final String COL_EXP_ID = "id";
    public static final String COL_EXP_NAME = "name";
    public static final String COL_EXP_AMOUNT = "amount";
    public static final String COL_EXP_CATEGORY = "category";
    public static final String COL_EXP_DATE = "date";
    public static final String COL_EXP_NOTE = "note";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL tương đương:
        // CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL);
        String createTableUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_USER_PASSWORD + " TEXT NOT NULL);";
        db.execSQL(createTableUsers);

        // SQL tương đương:
        // CREATE TABLE expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, amount INTEGER NOT NULL, category TEXT NOT NULL, date TEXT NOT NULL, note TEXT);
        String createTableExpenses = "CREATE TABLE " + TABLE_EXPENSES + " ("
                + COL_EXP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_EXP_NAME + " TEXT NOT NULL, "
                + COL_EXP_AMOUNT + " INTEGER NOT NULL, "
                + COL_EXP_CATEGORY + " TEXT NOT NULL, "
                + COL_EXP_DATE + " TEXT NOT NULL, "
                + COL_EXP_NOTE + " TEXT);";
        db.execSQL(createTableExpenses);

        // Chèn tài khoản mẫu bảng users
        // SQL tương đương: INSERT INTO users (username, password) VALUES ('admin', '123456');
        ContentValues userValues = new ContentValues();
        userValues.put(COL_USER_USERNAME, "admin");
        userValues.put(COL_USER_PASSWORD, "123456");
        db.insert(TABLE_USERS, null, userValues);

        // Chèn 3 khoản chi mẫu bảng expenses
        // SQL tương đương:
        // INSERT INTO expenses (name, amount, category, date, note) VALUES ('Ăn sáng', 30000, 'Ăn uống', '2026-10-13', 'Bánh mì pate');
        // INSERT INTO expenses (name, amount, category, date, note) VALUES ('Đổ xăng', 50000, 'Đi lại', '2026-10-12', 'Đầy bình xăng');
        // INSERT INTO expenses (name, amount, category, date, note) VALUES ('Mua sách', 120000, 'Học tập', '2026-10-11', 'Sách lập trình Android');
        insertSampleExpense(db, "Ăn sáng", 30000, "Ăn uống", "2026-10-13", "Bánh mì pate");
        insertSampleExpense(db, "Đổ xăng", 50000, "Đi lại", "2026-10-12", "Đầy bình xăng");
        insertSampleExpense(db, "Mua sách", 120000, "Học tập", "2026-10-11", "Sách lập trình Android");
    }

    private void insertSampleExpense(SQLiteDatabase db, String name, long amount, String category, String date, String note) {
        ContentValues values = new ContentValues();
        values.put(COL_EXP_NAME, name);
        values.put(COL_EXP_AMOUNT, amount);
        values.put(COL_EXP_CATEGORY, category);
        values.put(COL_EXP_DATE, date);
        values.put(COL_EXP_NOTE, note);
        db.insert(TABLE_EXPENSES, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
        onCreate(db);
    }

    // ==========================================
    // 8 PHƯƠNG THỨC BẮT BUỘC THEO ĐẶC TẢ
    // ==========================================

    /**
     * Phương thức 1: checkLogin(username, password)
     * Chức năng: Kiểm tra thông tin đăng nhập với bảng users. Trả về true nếu có dòng khớp.
     * SQL tương đương:
     * SELECT id FROM users WHERE username = ? AND password = ? LIMIT 1;
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = db.rawQuery("SELECT " + COL_USER_ID + " FROM " + TABLE_USERS
                            + " WHERE " + COL_USER_USERNAME + " = ? AND " + COL_USER_PASSWORD + " = ? LIMIT 1",
                    new String[]{username, password});
            if (cursor != null && cursor.moveToFirst()) {
                exists = true;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }

    /**
     * Phương thức 2: insertExpense(Expense e)
     * Chức năng: Thêm khoản chi mới vào bảng expenses. Trả về id vừa thêm (-1 nếu lỗi).
     * SQL tương đương:
     * INSERT INTO expenses (name, amount, category, date, note) VALUES (?, ?, ?, ?, ?);
     */
    public long insertExpense(Expense e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXP_NAME, e.getName());
        values.put(COL_EXP_AMOUNT, e.getAmount());
        values.put(COL_EXP_CATEGORY, e.getCategory());
        values.put(COL_EXP_DATE, e.getDate());
        values.put(COL_EXP_NOTE, e.getNote());

        return db.insert(TABLE_EXPENSES, null, values);
    }

    /**
     * Phương thức 3: updateExpense(Expense e)
     * Chức năng: Cập nhật thông tin khoản chi theo id. Trả về số dòng bị ảnh hưởng.
     * SQL tương đương:
     * UPDATE expenses SET name = ?, amount = ?, category = ?, date = ?, note = ? WHERE id = ?;
     */
    public int updateExpense(Expense e) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EXP_NAME, e.getName());
        values.put(COL_EXP_AMOUNT, e.getAmount());
        values.put(COL_EXP_CATEGORY, e.getCategory());
        values.put(COL_EXP_DATE, e.getDate());
        values.put(COL_EXP_NOTE, e.getNote());

        return db.update(TABLE_EXPENSES, values, COL_EXP_ID + " = ?", new String[]{String.valueOf(e.getId())});
    }

    /**
     * Phương thức 4: deleteExpense(int id)
     * Chức năng: Xóa khoản chi theo id. Trả về số dòng bị ảnh hưởng.
     * SQL tương đương:
     * DELETE FROM expenses WHERE id = ?;
     */
    public int deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_EXPENSES, COL_EXP_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Phương thức 5: getExpenseById(int id)
     * Chức năng: Lấy thông tin một khoản chi theo id.
     * SQL tương đương:
     * SELECT * FROM expenses WHERE id = ? LIMIT 1;
     */
    public Expense getExpenseById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Expense expense = null;
        try {
            cursor = db.query(TABLE_EXPENSES, null, COL_EXP_ID + " = ?",
                    new String[]{String.valueOf(id)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                expense = new Expense(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXP_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_NAME)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXP_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_NOTE))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return expense;
    }

    /**
     * Phương thức 6: getExpenses(keyword, category)
     * Chức năng: Lấy danh sách khoản chi có tên chứa keyword (LIKE), lọc theo danh mục
     * (bỏ qua nếu chọn 'Tất cả danh mục' hoặc 'Tất cả'), sắp xếp giảm dần theo ngày và id.
     * SQL tương đương:
     * SELECT * FROM expenses [WHERE name LIKE ? AND category = ?] ORDER BY date DESC, id DESC;
     */
    public List<Expense> getExpenses(String keyword, String category) {
        List<Expense> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            selection.append(COL_EXP_NAME).append(" LIKE ?");
            selectionArgs.add("%" + keyword.trim() + "%");
        }

        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("Tất cả danh mục") && !category.equalsIgnoreCase("Tất cả")) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(COL_EXP_CATEGORY).append(" = ?");
            selectionArgs.add(category);
        }

        String whereClause = selection.length() > 0 ? selection.toString() : null;
        String[] args = selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[0]);
        String orderBy = COL_EXP_DATE + " DESC, " + COL_EXP_ID + " DESC";

        try {
            cursor = db.query(TABLE_EXPENSES, null, whereClause, args, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Expense e = new Expense(
                            cursor.getInt(cursor.getColumnIndexOrThrow(COL_EXP_ID)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_NAME)),
                            cursor.getLong(cursor.getColumnIndexOrThrow(COL_EXP_AMOUNT)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_CATEGORY)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_DATE)),
                            cursor.getString(cursor.getColumnIndexOrThrow(COL_EXP_NOTE))
                    );
                    list.add(e);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Phương thức 7: getTotal(keyword, category)
     * Chức năng: Tính tổng tiền (SUM) theo cùng điều kiện lọc keyword và danh mục.
     * SQL tương đương:
     * SELECT SUM(amount) FROM expenses [WHERE name LIKE ? AND category = ?];
     */
    public long getTotal(String keyword, String category) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        long total = 0;

        StringBuilder sql = new StringBuilder("SELECT SUM(" + COL_EXP_AMOUNT + ") FROM " + TABLE_EXPENSES);
        StringBuilder selection = new StringBuilder();
        List<String> selectionArgs = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            selection.append(COL_EXP_NAME).append(" LIKE ?");
            selectionArgs.add("%" + keyword.trim() + "%");
        }

        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("Tất cả danh mục") && !category.equalsIgnoreCase("Tất cả")) {
            if (selection.length() > 0) {
                selection.append(" AND ");
            }
            selection.append(COL_EXP_CATEGORY).append(" = ?");
            selectionArgs.add(category);
        }

        if (selection.length() > 0) {
            sql.append(" WHERE ").append(selection);
        }

        try {
            String[] args = selectionArgs.isEmpty() ? null : selectionArgs.toArray(new String[0]);
            cursor = db.rawQuery(sql.toString(), args);
            if (cursor != null && cursor.moveToFirst()) {
                if (!cursor.isNull(0)) {
                    total = cursor.getLong(0);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    /**
     * Phương thức 8: getTotalByCategory()
     * Chức năng: Tính tổng tiền theo từng danh mục nhóm lại (GROUP BY category).
     * SQL tương đương:
     * SELECT category, SUM(amount) AS total FROM expenses GROUP BY category;
     */
    public Map<String, Long> getTotalByCategory() {
        Map<String, Long> map = new LinkedHashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String sql = "SELECT " + COL_EXP_CATEGORY + ", SUM(" + COL_EXP_AMOUNT + ") AS total FROM "
                + TABLE_EXPENSES + " GROUP BY " + COL_EXP_CATEGORY;

        try {
            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.moveToFirst()) {
                int catIndex = cursor.getColumnIndexOrThrow(COL_EXP_CATEGORY);
                int totalIndex = cursor.getColumnIndexOrThrow("total");
                do {
                    String category = cursor.getString(catIndex);
                    long total = cursor.getLong(totalIndex);
                    map.put(category, total);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return map;
    }
}

