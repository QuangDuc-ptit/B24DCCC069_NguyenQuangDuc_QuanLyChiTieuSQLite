package com.example.expensemanager.model;

import java.io.Serializable;

/**
 * Lớp biểu diễn một khoản chi tiêu trong cơ sở dữ liệu
 */
public class Expense implements Serializable {
    private int id;
    private String name;
    private long amount;      // Số tiền (VNĐ) > 0
    private String category;  // Ăn uống, Đi lại, Học tập, Giải trí, Khác
    private String date;      // Định dạng lưu trữ: yyyy-MM-dd
    private String note;      // Ghi chú (có thể để trống)

    public Expense() {
    }

    public Expense(int id, String name, long amount, String category, String date, String note) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public Expense(String name, long amount, String category, String date, String note) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

