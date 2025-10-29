<%--
    Document   : index.jsp (Đã cập nhật để dùng include)
    Created on : Oct 28, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bệnh viện ABC - Đặt Lịch Khám Dễ Dàng</title>

        <%-- 1. Nhúng CSS/Font chung --%>
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

            <section class="features-section">
                <div class="container">
                    <div class="features-grid">
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-kham-chuyen-khoa.png" alt="Khám chuyên khoa"><span>Khám Chuyên Khoa</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-kham-tong-quat.png" alt="Khám tổng quát"><span>Khám Tổng Quát</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-xet-nghiem.png" alt="Xét nghiệm"><span>Xét Nghiệm</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-kham-suc-khoe.png" alt="Khám sức khỏe"><span>Khám Sức Khỏe</span></a>
                        <a href="#" class="feature-item"><img src="${pageContext.request.contextPath}/images/icon-y-te-co-quan.png" alt="Y tế cơ quan"><span>Y Tế Cơ Quan</span></a>
                    </div>
                </div>
            </section>

            <section class="specialties-section" id="specialties">
                <div class="container-title">
                    <h2 class="section-title">Chuyên Khoa Nổi Bật</h2>
                </div>
                <div class="specialties-scroll-wrapper" id="specialtiesScroller">
                    <div class="specialty-grid">
                        <%-- Các thẻ chuyên khoa --%>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-heartbeat"></i></div><h4>Tim mạch</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-female"></i></div><h4>Sản - Phụ khoa</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-bone"></i></div><h4>Cơ Xương Khớp</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-stethoscope"></i></div><h4>Nội tổng quát</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-child"></i></div><h4>Nhi khoa</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-head-side-cough"></i></div><h4>Tai Mũi Họng</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                        <div class="specialty-card"> <div class="icon"><i class="fas fa-eye"></i></div><h4>Mắt</h4><p>...</p><a href="#" class="learn-more">Xem chi tiết →</a></div>
                    </div>
                </div>
                <button class="scroll-button left" id="scrollLeftBtn" onclick="scrollSpecialties(-1)">&#10094;</button>
                <button class="scroll-button right" id="scrollRightBtn" onclick="scrollSpecialties(1)">&#10095;</button>
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

        <%-- 5. JavaScript (cho scroll chuyên khoa) --%>
        <script>
            // ... (Toàn bộ script cuộn ngang của Chuyên Khoa) ...
            const scroller = document.getElementById('specialtiesScroller');
            const btnLeft = document.getElementById('scrollLeftBtn');
            const btnRight = document.getElementById('scrollRightBtn');
            const firstCard = scroller ? scroller.querySelector('.specialty-card') : null;
            const cardStyle = firstCard ? window.getComputedStyle(firstCard) : null;
            const cardWidth = firstCard ? firstCard.offsetWidth : 280;
            const gap = cardStyle ? parseFloat(cardStyle.marginRight) || parseFloat(window.getComputedStyle(scroller.querySelector('.specialty-grid')).gap) || 20 : 20;
            const scrollAmount = 3 * (cardWidth + gap);

            function scrollSpecialties(direction) {
                if (scroller) {
                    const newScrollLeft = scroller.scrollLeft + direction * scrollAmount;
                    scroller.scrollTo({left: newScrollLeft, behavior: 'smooth'});
                }
            }
            function checkScrollButtons() {
                if (!scroller || !btnLeft || !btnRight)
                    return;
                setTimeout(() => {
                    const maxScrollLeft = scroller.scrollWidth - scroller.clientWidth;
                    const currentScroll = Math.round(scroller.scrollLeft);
                    btnLeft.classList.toggle('disabled', currentScroll < gap);
                    btnRight.classList.toggle('disabled', currentScroll >= (maxScrollLeft - gap));
                }, 350);
            }
            document.addEventListener('DOMContentLoaded', checkScrollButtons);
            if (scroller) {
                scroller.addEventListener('scroll', checkScrollButtons);
            }
            let resizeTimerScroll;
            window.addEventListener('resize', () => {
                clearTimeout(resizeTimerScroll);
                resizeTimerScroll = setTimeout(checkScrollButtons, 250);
            });
        </script>
    </body>
</html>