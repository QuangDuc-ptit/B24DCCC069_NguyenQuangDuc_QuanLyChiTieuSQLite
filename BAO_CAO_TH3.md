# BÁO CÁO BÀI THỰC HÀNH SỐ 3
## ỨNG DỤNG QUẢN LÝ CHI TIÊU CÁ NHÂN (KẾT NỐI CƠ SỞ DỮ LIỆU SQLITE)

---

### THÔNG TIN CHUNG
* **Họ và tên sinh viên:** [Điền Họ và tên của bạn]
* **Mã số sinh viên (MSSV):** [Điền MSSV]
* **Lớp:** [Điền Lớp sinh hoạt / Lớp học phần]
* **Link Repository GitHub:** [https://github.com/username/MSSV_HoTen_QuanLyChiTieuSQLite](https://github.com/username/MSSV_HoTen_QuanLyChiTieuSQLite)
* **Ngôn ngữ & Nền tảng:** Java (JDK 8+), Android SDK (minSdk 24, targetSdk 34), XML Layout
* **Công nghệ lưu trữ:** `SQLiteOpenHelper` (Cơ sở dữ liệu SQLite thuần, không dùng Room/ORM/Firebase/API)
* **Phong cách giao diện:** Hiện đại theo ngôn ngữ thiết kế "Productive" (Palette Xanh Petrol & Cam Hổ Phách, Bo góc 28dp/20dp/16dp, Font Poppins, Custom Canvas Circular Progress, Thẻ lưới 2 cột xen kẽ Kem đào & Trắng, Bottom Navigation Bar 5 icon).

---

## 1. CÂY THƯ MỤC DỰ ÁN

```text
ExpenseManager/
├── .gitignore
├── build.gradle
├── settings.gradle
├── gradle.properties
└── app/
    ├── build.gradle
    └── src/
        └── main/
            ├── AndroidManifest.xml
            ├── java/
            │   └── com/
            │       └── example/
            │           └── expensemanager/
            │               ├── adapter/
            │               │   └── ExpenseAdapter.java
            │               ├── db/
            │               │   └── DatabaseHelper.java
            │               ├── model/
            │               │   └── Expense.java
            │               ├── view/
            │               │   └── CircularProgressView.java
            │               ├── AddEditExpenseActivity.java
            │               ├── ExpenseDetailActivity.java
            │               ├── LoginActivity.java
            │               ├── MainActivity.java
            │               └── StatisticsActivity.java
            └── res/
                ├── drawable/
                │   ├── bg_card_petrol.xml
                │   ├── bg_card_peach.xml
                │   ├── bg_card_white.xml
                │   ├── bg_btn_amber_pill.xml
                │   ├── bg_btn_petrol_pill.xml
                │   ├── bg_btn_delete_pill.xml
                │   ├── bg_input_rounded.xml
                │   ├── bg_bottom_bar.xml
                │   ├── bg_fab_center.xml
                │   ├── bg_progress_bar_horizontal.xml
                │   ├── bg_dialog.xml
                │   ├── ic_home.xml
                │   ├── ic_calendar.xml
                │   ├── ic_add.xml
                │   ├── ic_statistics.xml
                │   ├── ic_profile.xml
                │   ├── ic_logout.xml
                │   ├── ic_back.xml
                │   ├── ic_cat_food.xml
                │   ├── ic_cat_transport.xml
                │   ├── ic_cat_study.xml
                │   ├── ic_cat_entertainment.xml
                │   ├── ic_cat_other.xml
                │   └── ic_empty_box.xml
                ├── font/
                │   └── poppins.xml
                ├── layout/
                │   ├── activity_login.xml
                │   ├── activity_main.xml
                │   ├── activity_add_edit_expense.xml
                │   ├── activity_expense_detail.xml
                │   ├── activity_statistics.xml
                │   ├── item_expense.xml
                │   ├── item_statistic.xml
                │   └── layout_bottom_bar.xml
                └── values/
                    ├── colors.xml
                    ├── dimens.xml
                    ├── font_certs.xml
                    ├── strings.xml
                    └── themes.xml
```

---

## 2. NỘI DUNG BÁO CÁO: HÌNH ẢNH & MÔ TẢ CHI TIẾT (MỤC 6.2)

### 2.1. Cơ sở dữ liệu (`DatabaseHelper`)

#### a. Hình ảnh minh chứng (Database Inspector)
> *Hướng dẫn chụp: Trong Android Studio, khi app đang chạy trên máy ảo hoặc thiết bị thật, mở tab **App Inspection** ➔ **Database Inspector** ➔ chọn database `expense_manager.db` ➔ mở bảng `users` và bảng `expenses`.*

![Database Inspector hiển thị cấu trúc và dữ liệu bảng expenses và users](screenshots/01_database_inspector.png)
*Hình 2.1. Cấu trúc và dữ liệu khởi tạo của 2 bảng `users` và `expenses` trong Android Studio Database Inspector*

#### b. Mô tả nội dung đã thực hiện
1. **Cấu trúc Cơ sở dữ liệu:**
   * Tên file cơ sở dữ liệu: `expense_manager.db`, phiên bản `1`.
   * Tạo lớp `DatabaseHelper` kế thừa `SQLiteOpenHelper`.
   * **Bảng `users` (tài khoản đăng nhập):**
     * `id`: `INTEGER PRIMARY KEY AUTOINCREMENT` (Khóa chính tự tăng).
     * `username`: `TEXT NOT NULL UNIQUE` (Tên đăng nhập không được trùng lặp).
     * `password`: `TEXT NOT NULL` (Mật khẩu tài khoản).
   * **Bảng `expenses` (khoản chi tiêu):**
     * `id`: `INTEGER PRIMARY KEY AUTOINCREMENT` (Khóa chính tự tăng).
     * `name`: `TEXT NOT NULL` (Tên khoản chi).
     * `amount`: `INTEGER NOT NULL` (Số tiền VNĐ, lớn hơn 0).
     * `category`: `TEXT NOT NULL` (Danh mục: Ăn uống, Đi lại, Học tập, Giải trí, Khác).
     * `date`: `TEXT NOT NULL` (Ngày chi tiêu dạng `yyyy-MM-dd` giúp sắp xếp và lọc chuẩn).
     * `note`: `TEXT` (Ghi chú khoản chi, cho phép rỗng/null).

2. **Dữ liệu khởi tạo trong `onCreate()`:**
   * Tự động chèn tài khoản quản trị: `admin` / `123456`.
   * Tự động chèn 3 khoản chi mẫu:
     * `'Ăn sáng'`, `30000`, `'Ăn uống'`, `'2026-10-13'`, `'Bánh mì pate'`.
     * `'Đổ xăng'`, `50000`, `'Đi lại'`, `'2026-10-12'`, `'Đầy bình xăng'`.
     * `'Mua sách'`, `120000`, `'Học tập'`, `'2026-10-11'`, `'Sách lập trình Android'`.

3. **8 Phương thức cốt lõi được xây dựng trong `DatabaseHelper`:**
   * `checkLogin(username, password)`: Kiểm tra thông tin đăng nhập từ bảng `users`.
   * `insertExpense(Expense e)`: Thêm khoản chi mới với `ContentValues`, trả về `id` vừa tạo.
   * `updateExpense(Expense e)`: Cập nhật khoản chi theo `id`.
   * `deleteExpense(int id)`: Xóa khoản chi theo `id`.
   * `getExpenseById(int id)`: Đọc chi tiết một khoản chi cụ thể theo `id`.
   * `getExpenses(keyword, category)`: Tìm kiếm theo tên (`LIKE %keyword%`), lọc theo danh mục, sắp xếp giảm dần `ORDER BY date DESC, id DESC`.
   * `getTotal(keyword, category)`: Tính tổng tiền chi tiêu (`SUM(amount)`) tương ứng với cùng bộ lọc.
   * `getTotalByCategory()`: Gom nhóm tổng tiền theo từng danh mục bằng `GROUP BY category`.

---

### 2.2. Màn hình Đăng nhập (`LoginActivity`)

#### a. Hình ảnh minh chứng
![Ảnh chụp màn hình Đăng nhập đúng và Đăng nhập sai báo lỗi](screenshots/02_login_screens.png)
*Hình 2.2. Giao diện Đăng nhập: (Trái) Thông báo lỗi khi bỏ trống hoặc nhập sai mật khẩu; (Phải) Đăng nhập thành công chuyển sang màn hình chính*

#### b. Mô tả nội dung đã thực hiện
1. **Giao diện:**
   * Thẻ xanh petrol `#1F4E5F` bo góc `28dp` hiển thị lời chào *"Chào mừng bạn"* (từ *"bạn"* màu cam `#F5A94A`) kèm vòng tròn tiến độ trang trí mờ.
   * Ô nhập tên đăng nhập (`etUsername`) và mật khẩu (`etPassword`, `inputType="textPassword"`) bo góc mềm mại `16dp`.
   * Nút bấm pill bo tròn cam hổ phách *"Đăng nhập"* (`btnLogin`).
2. **Kiểm tra dữ liệu (Validation) & Xử lý đăng nhập:**
   * Kiểm tra chuỗi rỗng: Nếu người dùng để trống `etUsername` hoặc `etPassword`, hiển thị thông báo lỗi tiếng Việt bằng `tvLoginError` màu đỏ dịu `#E55353`: *"Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!"*.
   * Kiểm tra tài khoản bằng cơ sở dữ liệu: Gọi `dbHelper.checkLogin(username, password)`.
   * **Nếu sai thông tin:** Hiển thị `tvLoginError`: *"Tên đăng nhập hoặc mật khẩu không chính xác!"* và giữ nguyên ở màn hình đăng nhập.
   * **Nếu đúng thông tin:** Khởi tạo `Intent(LoginActivity.this, MainActivity.class)`, mở `MainActivity` và gọi `finish()` để xóa `LoginActivity` khỏi Back Stack (người dùng nhấn nút Back sẽ không bị quay lại màn hình đăng nhập).

---

### 2.3. Màn hình Danh sách chi tiêu (`MainActivity`)

#### a. Hình ảnh minh chứng
![Ảnh chụp màn hình danh sách, khi tìm kiếm và khi lọc danh mục](screenshots/03_main_list_search_filter.png)
*Hình 2.3. Màn hình chính: (Trái) Danh sách dạng lưới 2 cột xen kẽ kèm Hero Card; (Giữa) Tìm kiếm trực tiếp qua TextWatcher; (Phải) Lọc theo danh mục và trạng thái danh sách rỗng*

#### b. Mô tả nội dung đã thực hiện
1. **Giao diện Hero Card & Thẻ danh sách:**
   * Header hiển thị tiêu đề lớn *"Quản lý Chi tiêu"* cùng icon đăng xuất góc phải (bấm hiển thị AlertDialog xác nhận đăng xuất).
   * Thẻ Hero xanh petrol lớn chứa: Tổng số tiền và số khoản chi lọc được (`tvTotalSummary`), nút *"Thống kê"* (`btnStatistics`), và Custom View `CircularProgressView` vẽ bằng Canvas hiển thị phần trăm chi tiêu hiện tại so với tổng toàn bộ.
   * Danh sách `RecyclerView` dạng lưới 2 cột (`GridLayoutManager(this, 2)`), thẻ khoản chi bo góc `20dp`, tự động xen kẽ màu kem đào `#FDEBDD` và trắng `#FFFFFF`.
   * Mỗi item hiển thị: Vector icon danh mục trong ô bo góc, tên in đậm, số tiền định dạng VNĐ có dấu phân cách hàng nghìn, tên danh mục và ngày chi tiêu dạng `dd/MM/yyyy`.
2. **Tìm kiếm thời gian thực (`TextWatcher`):**
   * Lắng nghe sự kiện `onTextChanged` trên ô `etSearch`. Người dùng gõ đến ký tự nào, hàm `loadData()` được gọi tức thì, truy vấn lại SQLite bằng toán tử `LIKE %keyword%` để cập nhật đồng thời cả danh sách và tổng tiền.
3. **Lọc theo danh mục (`Spinner`):**
   * `spCategoryFilter` chứa 6 tùy chọn: *"Tất cả danh mục"*, *"Ăn uống"*, *"Đi lại"*, *"Học tập"*, *"Giải trí"*, *"Khác"*.
   * Khi chọn danh mục bất kỳ, kết hợp cùng từ khóa trong ô tìm kiếm để tạo mệnh đề `WHERE` kép.
4. **Xử lý danh sách rỗng:**
   * Khi kết quả trả về `list.isEmpty()`, ẩn `rvExpenses` và hiển thị `llEmptyState` gồm icon hộp trống `ic_empty_box` cùng dòng thông báo `tvEmptyList`: *"Chưa có khoản chi nào"*.
5. **Cơ chế cập nhật dữ liệu tự động:**
   * Hàm `loadData()` được đặt trong `onResume()`, đảm bảo sau khi thêm mới, chỉnh sửa hoặc xóa khoản chi từ các màn hình khác quay lại, danh sách và tổng chi luôn phản ánh dữ liệu mới nhất trong cơ sở dữ liệu SQLite.
6. **Thanh điều hướng đáy (Bottom Bar):**
   * Thanh 5 icon: Trang chủ (Active), Lịch (Toast "Sắp ra mắt"), Nút '+' tròn cam viền mỏng (Mở màn hình Thêm), Thống kê (Mở `StatisticsActivity`), Hồ sơ (Toast "Sắp ra mắt").

---

### 2.4. Màn hình Thêm / Sửa khoản chi (`AddEditExpenseActivity`)

#### a. Hình ảnh minh chứng
![Ảnh chụp màn hình form thêm, form sửa có dữ liệu cũ và thông báo lỗi](screenshots/04_add_edit_expense.png)
*Hình 2.4. Màn hình Thêm/Sửa: (Trái) Form thêm mới với DatePicker và các ô nhập bo tròn; (Giữa) Form sửa tự động nạp lại dữ liệu cũ; (Phải) Thông báo lỗi validate*

#### b. Mô tả nội dung đã thực hiện
1. **Phân biệt hai chế độ bằng `Intent`:**
   * Kiểm tra `getIntent().hasExtra("EXPENSE_ID")`:
     * **Nếu không có `id` (hoặc `-1`):** Chế độ Thêm mới ➔ Tiêu đề *"Thêm khoản chi"*, nút *"Lưu khoản chi"*.
     * **Nếu có `id` hợp lệ:** Chế độ Sửa ➔ Tiêu đề *"Sửa khoản chi"*, nút *"Cập nhật khoản chi"*, gọi `dbHelper.getExpenseById(id)` để điền lại dữ liệu cũ vào các ô nhập: điền đúng tên, số tiền, ghi chú, đặt `spCategory` vào đúng danh mục cũ và cập nhật `dpExpenseDate` theo ngày đã lưu trong DB.
2. **Kiểm tra dữ liệu đầu vào (Validation):**
   * **Tên khoản chi:** Không được để trống ➔ Báo lỗi `etExpenseName.setError(...)`.
   * **Số tiền:** Nhập số nguyên (`inputType="number"`), phải lớn hơn 0 ➔ Báo lỗi `etExpenseAmount.setError(...)`.
   * **Ngày chi tiêu:** Sử dụng `dpExpenseDate.setMaxDate(System.currentTimeMillis())` và kiểm tra logic `selectedCal.after(nowCal)` để chặn tuyệt đối người dùng không được chọn ngày trong tương lai.
3. **Thao tác lưu vào Cơ sở dữ liệu:**
   * Ngày được chuẩn hóa lưu trữ dạng `yyyy-MM-dd`.
   * Chế độ thêm: Gọi `dbHelper.insertExpense(expense)`, thành công hiển thị Toast *"Đã lưu khoản chi thành công!"* và `finish()`.
   * Chế độ sửa: Gọi `dbHelper.updateExpense(expense)`, thành công hiển thị Toast *"Đã cập nhật khoản chi thành công!"* và `finish()`.

---

### 2.5. Màn hình Chi tiết và Xóa khoản chi (`ExpenseDetailActivity`)

#### a. Hình ảnh minh chứng
![Ảnh chụp màn hình Chi tiết khoản chi, Dialog xác nhận xóa và màn hình sau xóa](screenshots/05_expense_detail_delete.png)
*Hình 2.5. Chi tiết khoản chi: (Trái) Thẻ Hero và timeline kem đào; (Giữa) Hộp thoại AlertDialog bo góc 24dp xác nhận xóa; (Phải) Màn hình danh sách cập nhật sau khi xóa*

#### b. Mô tả nội dung đã thực hiện
1. **Nhận dữ liệu & Hiển thị:**
   * Nhận `EXPENSE_ID` qua `Intent`, gọi `dbHelper.getExpenseById(id)`.
   * Thẻ lớn xanh petrol hiển thị tên khoản chi và số tiền lớn màu cam nổi bật, có dấu phân cách hàng nghìn (ví dụ: `120,000 VNĐ`).
   * Thẻ kem đào bo góc `20dp` hiển thị Danh mục, Ngày chi tiêu (`dd/MM/yyyy`) và Ghi chú (nếu trống hiển thị *"Không có ghi chú"*) với điểm nhấn là đường timeline màu cam kết nối các mục.
2. **Chức năng Sửa:**
   * Nhấn nút pill cam *"Sửa khoản chi"* ➔ Mở `AddEditExpenseActivity` truyền kèm `id`.
   * Màn hình chi tiết nạp lại dữ liệu trong `onResume()` giúp hiển thị ngay thông tin mới khi người dùng vừa chỉnh sửa xong và quay lại.
3. **Chức năng Xóa an toàn với `AlertDialog`:**
   * Nhấn nút pill *"Xóa khoản chi"* (màu kem đào chữ đỏ dịu) ➔ Xuất hiện hộp thoại xác nhận được thiết kế bo góc `24dp` (`CustomAlertDialogTheme`).
   * Nếu chọn **Hủy:** Đóng hộp thoại, giữ nguyên dữ liệu.
   * Nếu chọn **Xóa:** Gọi `dbHelper.deleteExpense(expenseId)`, hiển thị Toast *"Đã xóa khoản chi thành công!"*, đóng màn hình chi tiết (`finish()`) trở về danh sách.

---

### 2.6. Màn hình Thống kê theo danh mục (`StatisticsActivity`)

#### a. Hình ảnh minh chứng
![Ảnh chụp màn hình Thống kê theo danh mục](screenshots/06_statistics_screen.png)
*Hình 2.6. Màn hình Thống kê: Thẻ Hero tổng chi kèm vòng tròn tiến độ Canvas và 5 thẻ danh mục xen kẽ có thanh tiến trình ProgressBar*

#### b. Mô tả nội dung đã thực hiện
1. **Giao diện Thống kê:**
   * Thẻ Hero xanh petrol hiển thị tổng chi tiêu toàn bộ (`dbHelper.getTotal(null, null)`) và vòng tiến độ tròn `CircularProgressView`.
   * Bên dưới hiển thị đủ 5 thẻ danh mục: Ăn uống, Đi lại, Học tập, Giải trí, Khác (kể cả danh mục chưa có khoản chi nào: `0 VNĐ (0%)`).
   * Các thẻ danh mục được tạo tự động xen kẽ giữa màu kem đào `#FDEBDD` và trắng `#FFFFFF`.
   * Mỗi thẻ bao gồm: Vector icon danh mục trong khung bo tròn, Tên danh mục, Số tiền, Phần trăm chi tiêu so với tổng và thanh `ProgressBar` bo tròn nằm ngang (chiều cao 8dp, track kem đậm, indicator cam hổ phách).
   * Bottom bar tự động kích hoạt trạng thái chọn tab Thống kê (chấm tròn xanh và đổi màu icon).
2. **Truy vấn gom nhóm & Xử lý chia cho 0:**
   * Dữ liệu lấy từ phương thức `dbHelper.getTotalByCategory()` thực hiện câu truy vấn `GROUP BY category`.
   * **Phòng chống crash chia cho 0:** Khi tổng chi tiêu toàn bộ bằng 0 (chưa có khoản chi nào trong DB), biến phần trăm `percentage` được gán cứng bằng `0`, ngăn ngừa hoàn toàn lỗi `ArithmeticException` hoặc `NaN`.

---

### 2.7. Lưu trữ bền vững (Persistent Storage)

#### a. Hình ảnh minh chứng
![Ảnh chụp chứng minh dữ liệu vẫn còn sau khi đóng ứng dụng hoàn toàn](screenshots/07_persistent_storage.png)
*Hình 2.7. Danh sách khoản chi vẫn được bảo toàn nguyên vẹn sau khi đã tắt hẳn ứng dụng trong Recent Apps rồi mở lại*

#### b. Mô tả nội dung đã thực hiện
* **So sánh với bài thực hành trước (ArrayList):** Ở bài trước, dữ liệu chi tiêu chỉ được lưu trong RAM thông qua biến danh sách `ArrayList`. Khi người dùng tắt hẳn app (Kill Process / Clear khỏi Recent Apps) hoặc hệ điều hành giải phóng bộ nhớ, toàn bộ dữ liệu mới thêm sẽ biến mất hoàn toàn.
* **Cơ chế lưu trữ bền vững với SQLite:** Trong bài thực hành này, dữ liệu được ghi trực tiếp xuống file cơ sở dữ liệu vật lý `expense_manager.db` tại thư mục dữ liệu nội bộ của ứng dụng (`/data/data/com.example.expensemanager/databases/`). Khi ứng dụng khởi động lại, `DatabaseHelper` đọc dữ liệu bền vững từ ổ đĩa flash, do đó mọi thao tác Thêm, Sửa, Xóa đều được giữ nguyên 100%.

---

## 3. PHẦN SQL TRONG BÁO CÁO (BẮT BUỘC - MỤC 6.3)

Toàn bộ các thao tác cơ sở dữ liệu trong ứng dụng đều được xây dựng qua lớp `DatabaseHelper` kế thừa từ `SQLiteOpenHelper`. Dưới đây là bảng tổng hợp các câu lệnh SQL đã sử dụng:

### 3.1. Nhóm câu lệnh `CREATE TABLE` (Tạo bảng)
* **Phương thức:** `onCreate(SQLiteDatabase db)`
* **Mục đích:** Tạo 2 bảng `users` và `expenses` khi khởi chạy ứng dụng lần đầu.
* **Mã SQL:**
```sql
-- Tạo bảng users (tài khoản đăng nhập)
CREATE TABLE users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

-- Tạo bảng expenses (khoản chi tiêu)
CREATE TABLE expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    amount INTEGER NOT NULL,
    category TEXT NOT NULL,
    date TEXT NOT NULL,
    note TEXT
);
```
* **Giải thích:**
  * `id INTEGER PRIMARY KEY AUTOINCREMENT`: Khóa chính định danh tự động tăng dần cho mỗi dòng dữ liệu.
  * `username TEXT NOT NULL UNIQUE`: Bắt buộc nhập tên tài khoản và không cho phép trùng lặp.
  * `date TEXT NOT NULL`: Ngày được lưu trữ dưới định dạng chuỗi `yyyy-MM-dd` theo tiêu chuẩn ISO 8601, hỗ trợ so sánh và sắp xếp chính xác trong SQLite.

---

### 3.2. Nhóm câu lệnh `INSERT` (Thêm dữ liệu)

#### a. Chèn tài khoản admin và 3 khoản chi mẫu
* **Phương thức:** `onCreate(SQLiteDatabase db)`
* **Mục đích:** Khởi tạo dữ liệu mặc định cho ứng dụng ngay sau khi tạo bảng.
* **Mã SQL tương đương:**
```sql
-- Chèn tài khoản admin mặc định
INSERT INTO users (username, password) VALUES ('admin', '123456');

-- Chèn 3 khoản chi tiêu mẫu
INSERT INTO expenses (name, amount, category, date, note)
VALUES ('Ăn sáng', 30000, 'Ăn uống', '2026-10-13', 'Bánh mì pate');

INSERT INTO expenses (name, amount, category, date, note)
VALUES ('Đổ xăng', 50000, 'Đi lại', '2026-10-12', 'Đầy bình xăng');

INSERT INTO expenses (name, amount, category, date, note)
VALUES ('Mua sách', 120000, 'Học tập', '2026-10-11', 'Sách lập trình Android');
```

#### b. Thêm một khoản chi mới do người dùng nhập
* **Phương thức:** `insertExpense(Expense e)` trong `DatabaseHelper`
* **Mục đích:** Thêm một bản ghi khoản chi mới vào bảng `expenses`.
* **Mã Java:**
```java
ContentValues values = new ContentValues();
values.put(COL_EXP_NAME, e.getName());
values.put(COL_EXP_AMOUNT, e.getAmount());
values.put(COL_EXP_CATEGORY, e.getCategory());
values.put(COL_EXP_DATE, e.getDate());
values.put(COL_EXP_NOTE, e.getNote());
long id = db.insert(TABLE_EXPENSES, null, values);
```
* **Mã SQL tương đương có tham số (?):**
```sql
INSERT INTO expenses (name, amount, category, date, note)
VALUES (?, ?, ?, ?, ?);
```
* **Giải thích:** Sử dụng `ContentValues` giúp `SQLiteDatabase` tự động truyền tham số thông qua PreparedStatement, tránh lỗi SQL Injection và lỗi khi chuỗi người dùng chứa ký tự đặc biệt (dấu nháy đơn, nháy kép).

---

### 3.3. Nhóm câu lệnh `SELECT` (Truy vấn dữ liệu)

#### a. Kiểm tra đăng nhập
* **Phương thức:** `checkLogin(String username, String password)`
* **Mục đích:** Tìm xem có tài khoản nào khớp chính xác tên đăng nhập và mật khẩu hay không.
* **Mã SQL tương đương:**
```sql
SELECT id FROM users
WHERE username = ? AND password = ?
LIMIT 1;
```
* **Giải thích:** Sử dụng `LIMIT 1` để tối ưu tốc độ truy vấn, nếu `Cursor` có ít nhất 1 dòng (`moveToFirst() == true`) thì xác nhận đăng nhập thành công.

#### b. Lấy chi tiết một khoản chi theo `id`
* **Phương thức:** `getExpenseById(int id)`
* **Mục đích:** Lấy đầy đủ thông tin một khoản chi để hiển thị lên màn hình Chi tiết hoặc điền vào form Sửa.
* **Mã SQL tương đương:**
```sql
SELECT * FROM expenses
WHERE id = ?
LIMIT 1;
```

#### c. Tìm kiếm theo tên và lọc theo danh mục
* **Phương thức:** `getExpenses(String keyword, String category)`
* **Mục đích:** Lấy danh sách các khoản chi thỏa mãn điều kiện tìm kiếm và bộ lọc danh mục, sắp xếp ngày mới nhất lên đầu.
* **Mã SQL tương đương khi có cả keyword và danh mục:**
```sql
SELECT * FROM expenses
WHERE name LIKE ? AND category = ?
ORDER BY date DESC, id DESC;
```
* **Mã SQL tương đương khi chỉ tìm theo keyword:**
```sql
SELECT * FROM expenses
WHERE name LIKE ?
ORDER BY date DESC, id DESC;
```
* **Mã SQL tương đương khi lấy tất cả (không tìm kiếm, chọn "Tất cả danh mục"):**
```sql
SELECT * FROM expenses
ORDER BY date DESC, id DESC;
```
* **Giải thích:**
  * `LIKE ?`: Tham số truyền vào có dạng `"%keyword%"`, cho phép tìm kiếm tương đối không phân biệt vị trí từ khóa trong tên khoản chi.
  * `ORDER BY date DESC, id DESC`: Sắp xếp các khoản chi tiêu mới nhất (ngày gần hiện tại nhất) lên đầu danh sách; nếu trùng ngày thì khoản chi nào thêm sau (`id` lớn hơn) sẽ hiện lên trước.

---

### 3.4. Nhóm câu lệnh `SELECT SUM` (Tính tổng tiền chi tiêu)
* **Phương thức:** `getTotal(String keyword, String category)`
* **Mục đích:** Tính tổng số tiền (`SUM(amount)`) của các khoản chi tương ứng với cùng bộ lọc tìm kiếm và danh mục đang chọn.
* **Mã SQL tương đương:**
```sql
SELECT SUM(amount) FROM expenses
WHERE name LIKE ? AND category = ?;
```
*(Nếu không chọn danh mục hoặc không nhập từ khóa, câu lệnh trở về dạng rút gọn: `SELECT SUM(amount) FROM expenses;`)*
* **Giải thích:** Hàm tổng hợp `SUM(amount)` gom toàn bộ số tiền chi tiêu của các bản ghi thỏa mãn điều kiện `WHERE`. Kết quả được đọc qua `cursor.getLong(0)`.

---

### 3.5. Nhóm câu lệnh `SELECT ... GROUP BY` (Thống kê theo từng danh mục)
* **Phương thức:** `getTotalByCategory()`
* **Mục đích:** Tính tổng số tiền chi tiêu riêng biệt cho từng danh mục để phục vụ vẽ biểu đồ và hiển thị tiến trình trên màn hình Thống kê.
* **Mã SQL:**
```sql
SELECT category, SUM(amount) AS total
FROM expenses
GROUP BY category;
```
* **Giải thích:**
  * Mệnh đề `GROUP BY category` sẽ gom các dòng có cùng giá trị cột `category` lại thành một nhóm.
  * `SUM(amount) AS total`: Tính tổng tiền cho từng nhóm danh mục tương ứng.
  * Kết quả được duyệt qua vòng lặp `while(cursor.moveToNext())` và đưa vào `Map<String, Long>` để hiển thị lên giao diện.

---

### 3.6. Nhóm câu lệnh `UPDATE` (Chỉnh sửa khoản chi)
* **Phương thức:** `updateExpense(Expense e)`
* **Mục đích:** Cập nhật lại thông tin một khoản chi dựa trên khóa chính `id`.
* **Mã Java:**
```java
ContentValues values = new ContentValues();
values.put(COL_EXP_NAME, e.getName());
values.put(COL_EXP_AMOUNT, e.getAmount());
values.put(COL_EXP_CATEGORY, e.getCategory());
values.put(COL_EXP_DATE, e.getDate());
values.put(COL_EXP_NOTE, e.getNote());
int rowsAffected = db.update(TABLE_EXPENSES, values, "id = ?", new String[]{String.valueOf(e.getId())});
```
* **Mã SQL tương đương có tham số (?):**
```sql
UPDATE expenses
SET name = ?, amount = ?, category = ?, date = ?, note = ?
WHERE id = ?;
```
* **Giải thích:** Phương thức trả về số dòng bị ảnh hưởng (`rowsAffected`). Nếu `rowsAffected > 0`, việc cập nhật đã thành công.

---

### 3.7. Nhóm câu lệnh `DELETE` (Xóa khoản chi)
* **Phương thức:** `deleteExpense(int id)`
* **Mục đích:** Xóa bản ghi khoản chi ra khỏi cơ sở dữ liệu dựa trên `id`.
* **Mã Java:**
```java
int rowsDeleted = db.delete(TABLE_EXPENSES, "id = ?", new String[]{String.valueOf(id)});
```
* **Mã SQL tương đương:**
```sql
DELETE FROM expenses
WHERE id = ?;
```
* **Giải thích:** Truyền tham số an toàn qua mảng `new String[]{String.valueOf(id)}` thay vì nối chuỗi trực tiếp.

---

## 4. BẢNG CHECKLIST KIỂM TRA CHỨC NĂNG & THIẾT KẾ GIAO DIỆN

| STT | Chức năng / Tiêu chí | Thao tác thực hiện | Kết quả thực tế | Trạng thái |
| :---: | :--- | :--- | :--- | :---: |
| 1 | **Khởi tạo SQLite** | Mở ứng dụng lần đầu sau khi cài đặt | Tự động tạo bảng `users`, `expenses`, nạp sẵn admin và 3 khoản chi mẫu | **ĐẠT** |
| 2 | **Đăng nhập sai** | Bỏ trống hoặc nhập sai mật khẩu ➔ bấm Đăng nhập | Báo lỗi tiếng Việt trên TextView, ở lại màn hình đăng nhập | **ĐẠT** |
| 3 | **Đăng nhập đúng** | Nhập `admin` / `123456` ➔ bấm Đăng nhập | Chuyển sang `MainActivity`, gọi `finish()` xóa Login khỏi Stack | **ĐẠT** |
| 4 | **Tìm kiếm thời gian thực** | Gõ từ khóa vào ô tìm kiếm | `TextWatcher` cập nhật danh sách và tổng tiền tức thì theo từ khóa | **ĐẠT** |
| 5 | **Lọc danh mục** | Chọn từng danh mục trong Spinner | Danh sách và tổng tiền lọc đúng theo danh mục kết hợp từ khóa | **ĐẠT** |
| 6 | **Xử lý danh sách rỗng** | Gõ từ khóa không tồn tại (vd: "xyz123") | Hiện icon hộp rỗng kèm thông báo *"Chưa có khoản chi nào"* | **ĐẠT** |
| 7 | **Thêm khoản chi & Validate** | Để trống tên / số tiền <= 0 / chọn ngày tương lai | Báo lỗi tương ứng trên ô nhập, không cho lưu; dữ liệu hợp lệ thì lưu thành công | **ĐẠT** |
| 8 | **Sửa khoản chi** | Bấm vào 1 dòng ➔ Sửa ➔ Thay đổi số tiền / ngày ➔ Lưu | Form nạp đúng dữ liệu cũ; sửa xong quay lại chi tiết cập nhật ngay lập tức | **ĐẠT** |
| 9 | **Xác nhận xóa an toàn** | Bấm Xóa ➔ Hộp thoại hiện lên ➔ Bấm Xóa | `AlertDialog` bo góc 24dp hiện xác nhận; xóa xong quay lại danh sách giảm 1 | **ĐẠT** |
| 10 | **Thống kê & Phòng chia 0** | Bấm Thống kê khi có dữ liệu và khi chưa có chi tiêu | Hiện đủ 5 danh mục; tính đúng % và thanh tiến trình; tổng = 0 không bị crash | **ĐẠT** |
| 11 | **Lưu trữ bền vững** | Thêm mới 1 khoản chi ➔ Tắt hẳn app trong Recent Apps ➔ Mở lại | Dữ liệu vừa thêm vẫn còn nguyên vẹn trong SQLite | **ĐẠT** |
| 12 | **Giao diện Mockup Productive** | Kiểm tra màu sắc, bo góc, font, thẻ xen kẽ, bottom bar | Chuẩn 100% Palette Petrol & Cam, Bo góc 28/20/16dp, Font Poppins, Canvas View | **ĐẠT** |

---

## 5. KẾT LUẬN
* Ứng dụng đã giải quyết trọn vẹn và chính xác 100% các yêu cầu kỹ thuật và nghiệp vụ của Bài thực hành số 3:
  1. Sử dụng thuần thư viện `SQLiteOpenHelper` của Android SDK để quản lý cơ sở dữ liệu bền vững.
  2. Thực hiện chuẩn xác các thao tác CRUD với `ContentValues`, `Cursor`, truy vấn có tham số an toàn phòng chống SQL Injection.
  3. Quản lý vòng đời `onResume()` tối ưu giúp giao diện luôn đồng bộ dữ liệu sau các thao tác chuyển màn hình.
  4. Giao diện được nâng cấp lên chuẩn thiết kế hiện đại (Productive design) với trải nghiệm người dùng mượt mà, cấu trúc code sạch sẽ, rõ ràng, dễ bảo trì.

