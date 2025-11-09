<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bệnh viện Quốc tế HQĐ</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.css" />
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">


        <%-- Tăng version CSS --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.2">
    </head>
    <body>

        <%-- 1. Nhúng Header (Không đổi) --%>
        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <%-- 2. Nội dung riêng của trang Index --%>
        <div class="page-content">

            <%-- ✨ HERO SLIDER - CẤU TRÚC MỚI --%>
            <section class="swiper-hero-section">
                <div class="swiper mySwiper">
                    <div class="swiper-wrapper">

                        <%-- SLIDE 1 --%>
                        <div class="swiper-slide slide-1">
                            <div class="hero-content">
                                <p class="hero-subtitle-small">Chào mừng đến với HQĐ Hospital</p>
                                <p class="hero-subtitle">Sức khỏe của bạn là ưu tiên hàng đầu</p>
                                <h1>CHĂM SÓC TOÀN DIỆN<br>BẰNG CẢ TRÁI TIM</h1>
                                <p class="hero-description">
                                    Tại Bệnh viện HQĐ, chúng tôi cam kết mang đến dịch vụ chăm sóc y tế
                                    toàn diện với đội ngũ bác sĩ chuyên nghiệp.
                                </p>
                                <div class="hero-buttons">
                                    <a href="MainController?action=myAppointments" class="btn-primary hero-btn">Đặt Lịch Hẹn Ngay <i class="fas fa-arrow-right"></i></a>
                                    <a href="#" class="btn-secondary-outline hero-btn">Xem Dịch Vụ <i class="fas fa-arrow-right"></i></a>
                                </div>
                            </div>
                        </div>

                        <%-- SLIDE 2 --%>
                        <div class="swiper-slide slide-2">
                            <div class="hero-content">
                                <p class="hero-subtitle-small">Đội ngũ chuyên gia hàng đầu</p>
                                <p class="hero-subtitle">Chúng tôi sử dụng</p>
                                <h1>CÔNG NGHỆ HIỆN ĐẠI<br>CHO SỨC KHỎE VÀNG</h1>
                                <p class="hero-description">
                                    Trang thiết bị y tế tiên tiến nhất, hỗ trợ chẩn đoán chính xác
                                    và điều trị hiệu quả.
                                </p>
                                <div class="hero-buttons">
                                    <a href="MainController?action=myAppointments" class="btn-primary hero-btn">Đặt Lịch Hẹn Ngay <i class="fas fa-arrow-right"></i></a>
                                </div>
                            </div>
                        </div>

                        <%-- SLIDE 3 (Giống hình ảnh mẫu) --%>
                        <div class="swiper-slide slide-3">
                            <div class="hero-content">
                                <p class="hero-subtitle-small">Dịch vụ y tế cao cấp</p>
                                <p class="hero-subtitle">Sức khỏe của bạn là ưu tiên hàng đầu</p>
                                <h1>TRẢI NGHIỆM KHÁM CHỮA BỆNH TẬN TÂM NHẤT</h1>
                                <p class="hero-description">
                                    Không chỉ là điều trị, chúng tôi mang đến sự an tâm và thoải mái
                                    cho mọi bệnh nhân.
                                </p>
                                <div class="hero-buttons">
                                    <a href="MainController?action=myAppointments" class="btn-primary hero-btn">Đặt Lịch Hẹn Ngay <i class="fas fa-arrow-right"></i></a>
                                    <a href="#" class="btn-secondary-outline hero-btn">Xem Dịch Vụ <i class="fas fa-arrow-right"></i></a>
                                </div>
                            </div>
                        </div>

                    </div>
                    <%-- Nút bấm và dấu chấm --%>
                    <div class="swiper-button-next"></div>
                    <div class="swiper-button-prev"></div>
                    <div class="swiper-pagination"></div>
                </div>
            </section>
            <%-- KẾT THÚC SLIDER MỚI --%>

            <%-- Form Đặt Lịch Hẹn Nhanh (Không đổi, nó sẽ tự động đè lên slider) --%>
            <section class="appointment-form-section">
                <div class="container">
                    <div class="appointment-form-card">
                        <h3>Đặt Lịch Hẹn Ngay</h3>
                        <div class="form-group">
                            <label for="location"><i class="fa-solid fa-thumbs-up"></i> Chuyên nghiệp</label>

                        </div>
                        <div class="form-group">
                            <label for="department"><i class="fa-solid fa-heart"></i> Tận tình</label>

                        </div>
                        <div class="form-group">
                            <label for="date"><i class="fa-solid fa-check"></i> Chất lượng</label>

                        </div>
                        <a href="MainController?action=myAppointments" class="btn-secondary" role="button">
                            Đặt Lịch Ngay Để Nhận Tư Vấn
                        </a>
                    </div>
                </div>
            </section>

            <%-- Các Đặc Điểm Nổi Bật (Features) (Không đổi) --%>
            <section class="info-features-section">
                <div class="container">
                    <div class="info-feature-item">
                        <i class="fas fa-microscope"></i>
                        <h4>Trang Thiết Bị Hiện Đại</h4>
                        <p>Chúng tôi luôn cập nhật công nghệ và thiết bị y tế tiên tiến nhất.</p>
                        <a href="#" class="learn-more">Đọc thêm <i class="fas fa-arrow-right"></i></a>
                    </div>
                    <div class="info-feature-item">
                        <i class="fas fa-user-tie"></i>
                        <h4>Đội Ngũ Bác Sĩ Chuyên Gia</h4>
                        <p>Với các bác sĩ hàng đầu, giàu kinh nghiệm, tận tâm với bệnh nhân.</p>
                        <a href="#" class="learn-more">Đọc thêm <i class="fas fa-arrow-right"></i></a>
                    </div>
                    <div class="info-feature-item">
                        <i class="fas fa-virus"></i>
                        <h4>Xét Nghiệm COVID-19</h4>
                        <p>Cung cấp dịch vụ xét nghiệm nhanh chóng, chính xác và an toàn.</p>
                        <a href="#" class="learn-more">Đọc thêm <i class="fas fa-arrow-right"></i></a>
                    </div>
                </div>
            </section>

            <%-- Giới thiệu (About Us) (Không đổi, nhưng đã sửa link ảnh ở lần trước) --%>
            <section class="about-section">
                <div class="container">
                    <div class="about-content">
                        <h2 class="section-title">CHÚNG TÔI MANG ĐẾN NHỮNG <br>DỊCH VỤ Y TẾ TỐT NHẤT</h2>
                        <p class="about-description">
                            Bệnh viện HQĐ tự hào là địa chỉ tin cậy cho hàng ngàn bệnh nhân với các dịch vụ y tế chất lượng cao. Chúng tôi luôn đặt sức khỏe và sự hài lòng của bạn lên hàng đầu.
                        </p>
                        <div class="about-services-grid">
                            <div class="about-service-item">
                                <h5><i class="fas fa-stomach"></i> Xét nghiệm dạ dày</h5>
                                <p>Chẩn đoán chính xác các bệnh lý liên quan đến đường tiêu hóa.</p>
                            </div>
                            <div class="about-service-item">
                                <h5><i class="fas fa-kidney"></i> Kiểm tra chức năng thận</h5>
                                <p>Phát hiện sớm các vấn đề về thận, giúp điều trị kịp thời.</p>
                            </div>
                        </div>
                        <div class="about-doctors">
                            <img src="data:image/webp;base64,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" alt="Bác sĩ A" class="doctor-avatar">
                            <img src="https://th.bing.com/th/id/OIP.jyydels4t1Muexdx-fbNqgHaHa?w=162&h=180&c=7&r=0&o=7&cb=ucfimg2&pid=1.7&rm=3&ucfimg=1" alt="Bác sĩ B" class="doctor-avatar">
                            <div class="doctor-quote">"Chúng tôi cam kết chăm sóc bạn tận tình." <br> - Nguyễn Đình Tuấn Đạt (Giám đốc Y khoa)</div>
                        </div>
                    </div>
                    <div class="about-image">
                        <img src="images/chungtoi1.jpg" alt="Về chúng tôi"/>
                        <img src="images/chungtoi.jpg" alt="Các bác sĩ" class="small-image">
                    </div>
                </div>
            </section>

            <%-- Dịch vụ Nổi bật --%>
            <section class="our-services-section">
                <div class="container">
                    <h2 class="section-title"> CÁC DỊCH VỤ Y TẾ <br> ĐẲNG CẤP CHẤT LƯỢNG CAO</h2>
                    <div class="services-grid">
                        <div class="service-card">
                            <div class="service-icon"><i class="fa-duotone fa-solid fa-lungs"></i></div>
                            <h4>Bệnh về phổi</h4>
                            <p>Chẩn đoán và điều trị các bệnh lý hô hấp.</p>

                        </div>
                        <div class="service-card">
                            <div class="service-icon"><i class="fas fa-heartbeat"></i></div>
                            <h4>Bệnh tim mạch</h4>
                            <p>Chăm sóc sức khỏe tim mạch với công nghệ tiên tiến.</p>

                        </div>
                        <div class="service-card">
                            <div class="service-icon"><i class="fas fa-bone"></i></div>
                            <h4>Chỉnh hình</h4>
                            <p>Điều trị các vấn đề xương khớp, phục hồi chức năng.</p>

                        </div>
                        <div class="service-card">
                            <div class="service-icon"><i class="fas fa-tooth"></i></div>
                            <h4>Chăm sóc răng miệng</h4>
                            <p>Dịch vụ nha khoa toàn diện, từ cơ bản đến chuyên sâu.</p>

                        </div>
                        <div class="service-card">
                            <div class="service-icon"><i class="fas fa-running"></i></div>
                            <h4>Y học thể thao</h4>
                            <p>Hỗ trợ phục hồi chấn thương, tối ưu hóa hiệu suất vận động.</p>

                        </div>
                        <div class="service-card">
                            <div class="service-icon"><i class="fas fa-user-md"></i></div>
                            <h4>Phẫu thuật tổng quát</h4>
                            <p>Thực hiện các ca phẫu thuật an toàn với đội ngũ chuyên môn cao.</p>

                        </div>
                    </div>
                </div>
            </section>

            <%-- Gọi hành động (Call to Action) --%>
            <section class="cta-section">
                <div class="container">
                    <div class="cta-content">
                        <h2 class="section-title">ĐẶT LỊCH HẸN VỚI BÁC SĨ ONLINE</h2>
                        <p>Với đội ngũ bác sĩ chuyên khoa đa dạng, bạn có thể dễ dàng tìm và đặt lịch hẹn online chỉ với vài bước đơn giản.</p>
                        <a href="#" class="btn-primary btn1">Đặt Lịch Hẹn Ngay</a>
                    </div>
                    <div class="cta-stats">
                        <div class="stat-item">
                            <div class="stat-number">200+</div>
                            <div class="stat-label">Bác sĩ</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-number">3980+</div>
                            <div class="stat-label">Bệnh nhân hài lòng</div>
                        </div>
                        <div class="stat-item">
                            <div class="stat-number">1000+</div>
                            <div class="stat-label">Ca phẫu thuật thành công</div>
                        </div>
                    </div>
                </div>
            </section>

            <%-- Footer --%>
            <footer class="main-footer">
                <div class="container">
                    <jsp:include page="/WEB-INF/footer.jsp" /> 
                </div>
            </footer>
        </div> 

        <%-- ✨ 3. THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- ✨ 4. LINK TỚI FILE JS (Sẽ cập nhật ở Bước 3) --%>
        <script src="<c:url value='/js/index.js'/>"></script>

    </body>
</html>