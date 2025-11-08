document.addEventListener('DOMContentLoaded', function () {
    // ===================================================
    // LOGIC CHUNG CHO CHẾ ĐỘ TỐI (DARK MODE)
    // ===================================================
    // 1. Lấy ra các đối tượng cần thiết từ DOM
    const themeToggle = document.getElementById('theme-toggle');
    const body = document.body;

    // Tên key để lưu trong localStorage
    const themeKey = 'theme-preference';

    // 2. Hàm để áp dụng theme được lưu
    const applyTheme = (theme) => {
        if (theme === 'dark') {
            // Thêm class 'dark-mode' vào body
            body.classList.add('dark-mode');
            // Đánh dấu check cho nút gạt
            themeToggle.checked = true;
        } else {
            // Xóa class 'dark-mode' khỏi body
            body.classList.remove('dark-mode');
            // Bỏ check cho nút gạt
            themeToggle.checked = false;
        }
    };

    // 3. Lấy theme đã lưu từ localStorage khi tải trang
    const savedTheme = localStorage.getItem(themeKey);

    // Mặc định là 'light' nếu chưa có gì được lưu
    const currentTheme = savedTheme ? savedTheme : 'light';
    applyTheme(currentTheme);


    // 4. Lắng nghe sự kiện 'change' trên nút gạt
    themeToggle.addEventListener('change', () => {
        let newTheme;
        // Nếu nút gạt được check, theme mới là 'dark'
        if (themeToggle.checked) {
            newTheme = 'dark';
        } else {
            // Nếu không, theme mới là 'light'
            newTheme = 'light';
        }

        // Lưu lựa chọn mới vào localStorage
        localStorage.setItem(themeKey, newTheme);
        // Áp dụng theme mới ngay lập tức
        applyTheme(newTheme);
    });
});