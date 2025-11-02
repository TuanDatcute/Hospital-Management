<%--
    Document   : index.jsp (Đã cập nhật JavaScript cho cả 2 thanh cuộn)
    Created on : Oct 28, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <%-- Bắt buộc phải có cho menu động --%>


<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bệnh viện ABC - Đặt Lịch Khám Dễ Dàng</title>

        <link href="https://fonts.googleapis.com/css2?family=Open+Sans:wght@400;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

        <%-- 2. Nhúng Header --%>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <%-- 3. Nội dung riêng của trang Index --%>
        <div class="page-content">

            <section class="hero">
                <div class="container">
                    <h1>Đặt Lịch Khám Dễ Dàng</h1>
                    <p class="subtitle">Tìm kiếm và đặt hẹn với bác sĩ chuyên khoa tại Bệnh viện ABC nhanh chóng.</p>
                    <div class="search-box">
                        <input type="text" placeholder="Tìm kiếm bác sĩ, chuyên khoa, triệu chứng...">
                        <button type="button"><i class="fas fa-search"></i> Tìm kiếm</button>
                    </div>
                </div>
            </section>

            <%-- Section Danh mục nổi bật (Đã thêm ID và nút bấm) --%>
            <section class="features-section" id="features">
                <%-- **THÊM WRAPPER VÀ ID** --%>
                <div class="features-scroll-wrapper" id="featuresScroller">
                    <div class="features-grid">
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-kham-chuyen-khoa.png" alt="Khám chuyên khoa"><span>Khám Chuyên Khoa</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-kham-tong-quat.png" alt="Khám tổng quát"><span>Khám Tổng Quát</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-xet-nghiem.png" alt="Xét nghiệm"><span>Xét Nghiệm</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-kham-suc-khoe.png" alt="Khám sức khỏe"><span>Khám Sức Khỏe</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-y-te-co-quan.png" alt="Y tế cơ quan"><span>Y Tế Cơ Quan</span></a>
                            <%-- Thêm thẻ khác nếu muốn (ví dụ: 'Gọi video bác sĩ', 'Mua thuốc') --%>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-goi-video.png" alt="Gọi video bác sĩ"><span>Gọi video bác sĩ</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-mua-thuoc.png" alt="Mua thuốc"><span>Mua thuốc</span></a>
                    </div>
                </div>
                <%-- **THÊM NÚT BẤM CUỘN** (Tái sử dụng class .scroll-button) --%>
                <button class="scroll-button left" id="featuresScrollLeftBtn" onclick="scrollFeatures(-1)">&#10094;</button>
                <button class="scroll-button right" id="featuresScrollRightBtn" onclick="scrollFeatures(1)">&#10095;</button>
            </section>

            <%-- Section Chuyên Khoa (Giữ nguyên) --%>
            <section class="specialties-section" id="specialties">
                <div class="container-title">
                    <h2 class="section-title">Chuyên Khoa Nổi Bật</h2>
                </div>
                <div class="specialties-scroll-wrapper" id="specialtiesScroller">
                    <div class="specialty-grid">
                        <%-- Các thẻ chuyên khoa --%>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-heartbeat"></i></div><h4>Tim mạch</h4><p>Khám, chẩn đoán...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-female"></i></div><h4>Sản - Phụ khoa</h4><p>Chăm sóc sức khỏe...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-bone"></i></div><h4>Cơ Xương Khớp</h4><p>Điều trị các vấn đề...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-stethoscope"></i></div><h4>Nội tổng quát</h4><p>Khám và tư vấn...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-child"></i></div><h4>Nhi khoa</h4><p>Chăm sóc sức khỏe...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-head-side-cough"></i></div><h4>Tai Mũi Họng</h4><p>Điều trị các bệnh...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-eye"></i></div><h4>Mắt</h4><p>Khám và điều trị...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                    </div>
                </div>
                <button class="scroll-button left" id="specialtiesScrollLeftBtn" onclick="scrollSpecialties(-1)">&#10094;</button>
                <button class="scroll-button right" id="specialtiesScrollRightBtn" onclick="scrollSpecialties(1)">&#10095;</button>
            </section>

            <section class="section" id="about">
                <h3 class="section-title">Về Bệnh viện ABC</h3>
                <p>Nội dung giới thiệu...</p>
            </section>
            <section class="section" id="contact">
                <h3 class="section-title">Thông tin Liên hệ</h3>
                <p>Địa chỉ, Điện thoại, Email...</p>
            </section>

            <%-- 4. Nhúng Footer --%>
            <jsp:include page="/WEB-INF/footer.jsp" /> 
        </div> 

        <%-- 5. JavaScript (Đã cập nhật cho cả 2 thanh cuộn) --%>
        <script>
            // --- SCRIPT CHO THANH CUỘN CHUYÊN KHOA ---
            const specialtiesScroller = document.getElementById('specialtiesScroller');
            const specialtiesBtnLeft = document.getElementById('specialtiesScrollLeftBtn'); // Sửa ID
            const specialtiesBtnRight = document.getElementById('specialtiesScrollRightBtn'); // Sửa ID
            const specialtyFirstCard = specialtiesScroller ? specialtiesScroller.querySelector('.specialty-card') : null;

            let specialtyCardWidth = 280;
            let specialtyGap = 20;
            if (specialtyFirstCard) {
                specialtyCardWidth = specialtyFirstCard.offsetWidth;
                const gridStyle = window.getComputedStyle(specialtyFirstCard.parentElement);
                specialtyGap = parseFloat(gridStyle.gap) || 20;
            }
            const specialtyScrollAmount = 3 * (specialtyCardWidth + specialtyGap);

            function scrollSpecialties(direction) {
                if (specialtiesScroller) {
                    const newScrollLeft = specialtiesScroller.scrollLeft + direction * specialtyScrollAmount;
                    specialtiesScroller.scrollTo({left: newScrollLeft, behavior: 'smooth'});
                }
            }
            function checkSpecialtiesScrollButtons() {
                if (!specialtiesScroller || !specialtiesBtnLeft || !specialtiesBtnRight)
                    return;
                setTimeout(() => {
                    const maxScrollLeft = specialtiesScroller.scrollWidth - specialtiesScroller.clientWidth;
                    const currentScroll = Math.round(specialtiesScroller.scrollLeft);
                    specialtiesBtnLeft.classList.toggle('disabled', currentScroll < specialtyGap);
                    specialtiesBtnRight.classList.toggle('disabled', currentScroll >= (maxScrollLeft - specialtyGap));
                }, 350);
            }


            // --- **SCRIPT MỚI CHO THANH CUỘN DANH MỤC (FEATURES)** ---
            const featuresScroller = document.getElementById('featuresScroller');
            const featuresBtnLeft = document.getElementById('featuresScrollLeftBtn');
            const featuresBtnRight = document.getElementById('featuresScrollRightBtn');
            const featureFirstCard = featuresScroller ? featuresScroller.querySelector('.feature-item') : null;

            let featureCardWidth = 170;
            let featureGap = 15;
            if (featureFirstCard) {
                featureCardWidth = featureFirstCard.offsetWidth;
                const gridStyle = window.getComputedStyle(featureFirstCard.parentElement);
                featureGap = parseFloat(gridStyle.gap) || 15;
            }
            const featuresScrollAmount = 4 * (featureCardWidth + featureGap); // Cuộn 4 thẻ

            function scrollFeatures(direction) {
                if (featuresScroller) {
                    const newScrollLeft = featuresScroller.scrollLeft + direction * featuresScrollAmount;
                    featuresScroller.scrollTo({left: newScrollLeft, behavior: 'smooth'});
                }
            }
            function checkFeaturesScrollButtons() {
                if (!featuresScroller || !featuresBtnLeft || !featuresBtnRight)
                    return;
                setTimeout(() => {
                    const maxScrollLeft = featuresScroller.scrollWidth - featuresScroller.clientWidth;
                    const currentScroll = Math.round(featuresScroller.scrollLeft);
                    featuresBtnLeft.style.display = currentScroll < featureGap ? 'none' : 'flex';
                    featuresBtnRight.style.display = currentScroll >= (maxScrollLeft - featureGap) ? 'none' : 'flex';
                }, 100);
            }


            // --- GỌI CÁC HÀM CHECK KHI TẢI TRANG VÀ RESIZE ---
            document.addEventListener('DOMContentLoaded', () => {
                checkSpecialtiesScrollButtons();
                checkFeaturesScrollButtons(); // <-- Gọi hàm check mới
            });

            if (specialtiesScroller) {
                specialtiesScroller.addEventListener('scroll', checkSpecialtiesScrollButtons);
            }
            if (featuresScroller) {
                featuresScroller.addEventListener('scroll', checkFeaturesScrollButtons); // <-- Gắn event listener mới
            }

            let resizeTimer;
            window.addEventListener('resize', () => {
                clearTimeout(resizeTimer);
                resizeTimer = setTimeout(() => {
                    checkSpecialtiesScrollButtons();
                    checkFeaturesScrollButtons(); // <-- Gọi hàm check mới
                }, 250);
            });
        </script>
    </body>
</html>