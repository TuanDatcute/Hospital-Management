(function () {
    // Key này phải khớp với key bạn dùng trong file theme.js
    var themeKey = 'theme-preference';
    var theme = localStorage.getItem(themeKey);

    if (theme === 'dark') {
        // Đặt màu nền ngay lập tức để tránh bị "nháy trắng"
        // Màu này PHẢI KHỚP với màu --bg-color trong CSS dark-mode của bạn
        document.documentElement.style.backgroundColor = '#121212';
    }
})();