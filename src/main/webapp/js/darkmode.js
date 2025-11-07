document.addEventListener('DOMContentLoaded', function () {
    // 1. Lấy ra các đối tượng cần thiết từ DOM
    const themeToggle = document.getElementById('theme-toggle');

    // ✨ SỬA 1: Áp dụng class vào <html>
    const htmlElement = document.documentElement;

    // Key này phải khớp với script trong <head>
    const themeKey = 'theme-preference';

    // 2. Hàm để áp dụng theme (chỉ cho NÚT GẠT)
    const setToggleState = (theme) => {
        if (theme === 'dark') {
            themeToggle.checked = true;
        } else {
            themeToggle.checked = false;
        }
    };

    // 3. Lấy theme đã lưu và CÀI ĐẶT NÚT GẠT
    // (Script trong <head> đã lo việc đổi màu, ở đây chỉ cần cài đặt nút gạt)
    const savedTheme = localStorage.getItem(themeKey) || 'light';
    setToggleState(savedTheme);

    // 4. Lắng nghe sự kiện 'change' trên nút gạt
    themeToggle.addEventListener('change', () => {
        // Lấy theme mới dựa trên nút gạt
        const newTheme = themeToggle.checked ? 'dark' : 'light';

        // Lưu lựa chọn mới vào localStorage
        localStorage.setItem(themeKey, newTheme);

        // ✨ SỬA 2: Áp dụng/xóa class 'dark-mode' trên <html>
        if (newTheme === 'dark') {
            htmlElement.classList.add('dark-mode');
        } else {
            htmlElement.classList.remove('dark-mode');
        }
    });
});