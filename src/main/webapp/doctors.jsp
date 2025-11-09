<%-- Tên tệp: doctors.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ page import="java.util.Random" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đội Ngũ Bác Sĩ - Bệnh viện HQĐ</title>

        <%-- ✨ QUAN TRỌNG: Thêm CẢ HAI font từ trang chủ --%>
        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

        <%-- Link CSS chung (index.css) - Nhớ dùng version mới nhất --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/index.css?v=1.5">

        <%-- ✨ Link CSS riêng cho trang này (Tạo ở Bước 2) --%>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/doctors.css?v=1.0">
    </head>
    <body>

        <%-- 1. Nhúng Header --%>
        <jsp:include page="/WEB-INF/headerDat.jsp" /> 

        <%-- 2. Nội dung riêng của trang Bác sĩ --%>
        <div class="page-content">

            <%-- Section Tiêu Đề Trang --%>
            <section class="page-title-section">
                <div class="container">
                    <p class="breadcrumb">Trang chủ / Bác sĩ</p>
                    <h1>Đội Ngũ Bác Sĩ</h1>
                </div>
            </section>

            <%-- Section Bộ Lọc (Đã cập nhật) --%>
            <section class="doctor-filters">
                <div class="container">
                    <%-- ✨ SỬA LẠI: Form này giờ sẽ gửi GET đến Controller --%>
                    <form action="<c:url value='/MainController'/>" method="GET" class="filter-container">
                        <input type="hidden" name="action" value="viewDoctors">

                        <div class="filter-group">
                            <label for="filterKhoa">Chuyên khoa</label>
                            <%-- ✨ Tải động danh sách khoa --%>
                            <select id="filterKhoa" name="khoaId">
                                <option value="">Tất cả chuyên khoa</option>
                                <c:forEach var="khoa" items="${danhSachKhoa}">
                                    <option value="${khoa.id}" ${khoa.id == selectedKhoaId ? 'selected' : ''}>
                                        ${khoa.tenKhoa}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="filter-group">
                            <label for="filterTen">Tên bác sĩ</label>
                            <input type="text" id="filterTen" name="keyword" placeholder="Nhập tên bác sĩ..." value="${searchKeyword}">
                        </div>
                        <button type="submit" class="btn-primary">Tìm kiếm</button>
                    </form>
                </div>
            </section>

            <%-- 1. Tạo một đối tượng Random để sử dụng --%>
            <jsp:useBean id="random" class="java.util.Random" scope="page" />

            <%-- 2. Tạo danh sách các bio mặc định, ngăn cách bởi dấu chấm phẩy ; --%>
            <c:set var="defaultBios" value="Là một chuyên gia hàng đầu của chúng tôi, luôn tận tâm vì bệnh nhân.;Có nhiều năm kinh nghiệm trong chẩn đoán và điều trị.;Chuyên gia trong nhiều lĩnh vực.;Đã hoàn thành nhiều ca mổ phức tạp."/>

            <%-- 3. Tách chuỗi thành một mảng và đếm số lượng --%>
            <c:set var="bioArray" value="${fn:split(defaultBios, ';')}" />
            <c:set var="bioCount" value="${fn:length(bioArray)}" />
            <%-- Section Lưới Bác Sĩ (Đã cập nhật) --%>
            <section class="doctor-grid-section">
                <div class="container">
                    <div class="doctor-grid">

                        <%-- ✨ SỬA LẠI: Dùng c:forEach để lặp qua danh sách bác sĩ --%>
                        <c:forEach var="bacSi" items="${danhSachBacSi}">
                            <div class="doctor-card">
                                <div class="doctor-card-image">

                                    <img src="https://cdn-icons-png.freepik.com/512/7135/7135381.png" alt="${bacSi.hoTen}">
                                </div>
                                <div class="doctor-card-content">
                                    <h4>${bacSi.hoTen}</h4>
                                    <p class="doctor-specialty">${bacSi.chuyenMon} (${bacSi.tenKhoa})</p>

                                    <p class="doctor-bio">
                                        <c:choose>
                                            <c:when test="${bacSi.id == 2}">
                                                Hơn 20 năm kinh nghiệm trong lĩnh vực tim mạch, chuyên gia hàng đầu về can thiệp nội mạch.
                                            </c:when>
                                            <c:when test="${bacSi.id == 3}">
                                                Tận tâm, yêu trẻ, có kinh nghiệm điều trị các bệnh lý hô hấp và tiêu hóa ở trẻ sơ sinh.
                                            </c:when>
                                            <c:when test="${bacSi.id == 4}">
                                                Chuyên gia phẫu thuật thay khớp gối và khớp háng với kỹ thuật xâm lấn tối thiểu, giúp bệnh nhân phục hồi nhanh chóng.
                                            </c:when>
                                            <c:when test="${bacSi.id == 5}">
                                                Nha sĩ chuyên sâu về cấy ghép Implant và nha khoa thẩm mỹ, đã kiến tạo hàng ngàn nụ cười tự tin.
                                            </c:when>
                                            <c:when test="${bacSi.id == 6}">
                                                Điều trị chuyên sâu các bệnh về da và thẩm mỹ da bằng công nghệ laser tiên tiến nhất hiện nay.
                                            </c:when>
                                            <c:when test="${bacSi.id == 8}">
                                                Là chuyên gia hàng đầu của bệnh viện chúng tôi
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="randomIndex" value="${random.nextInt(bioCount)}" />
                                                ${bioArray[randomIndex]}
                                            </c:otherwise>
                                        </c:choose>
                                    </p>

                                    <a href="#" class="btn-secondary">Đặt Lịch Ngay</a>
                                </div>
                            </div>
                        </c:forEach>

                        <%-- ✨ Hiển thị thông báo nếu không tìm thấy bác sĩ --%>
                        <c:if test="${empty danhSachBacSi}">
                            <div class="no-results-found">
                                <p>Không tìm thấy bác sĩ nào phù hợp với tiêu chí của bạn.</p>
                            </div>
                        </c:if>

                    </div>
                </div>
            </section>
        </div> 

        <%-- 3. Nhúng Footer --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- 4. Nhúng JS (cho menu mobile) --%>
        <script src="<c:url value='/js/index.js'/>"></script>

    </body>
</html>