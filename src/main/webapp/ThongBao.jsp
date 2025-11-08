<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Quản lý Thông báo</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <%-- BẮT BUỘC: Thêm Font Awesome cho icon --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
        <%-- BẮT BUỘC: Nhúng file CSS chung TRƯỚC --%>
        <%-- (Đây là file DanhSachHoaDon.css đã được gộp, hoặc file base.css) --%>
        <link rel="stylesheet" href="<c:url value='/css/StyleChungCuaQuang.css'/>"> 

        <%-- (MỚI) Nhúng file CSS cụ thể cho trang này SAU --%>
        <link rel="stylesheet" href="<c:url value='/css/ThongBao.css'/>">

        <script src="<c:url value='/js/darkmodeQuang.js'/>" defer></script>
    </head>
    <body>
        <jsp:include page="/WEB-INF/header.jsp" /> 

        <%-- Bọc toàn bộ trang trong .container --%>
        <div class="container">

            <div class="page-header">
                <%-- Đặt H1 và Dark mode toggle vào header --%>
                <h1>Quản lý Thông báo</h1>
                <div class="theme-switch-wrapper">
                    <label class="theme-switch" for="theme-toggle">
                        <input type="checkbox" id="theme-toggle" />
                        <span class="slider">
                            <i class="fa-solid fa-sun sun-icon"></i>
                            <i class="fa-solid fa-moon moon-icon"></i>
                        </span>
                    </label>
                </div>
            </div>


            <%-- Hiển thị thông báo bằng component .alert --%>
            <c:if test="${not empty param.createSuccess}">
                <div class="alert alert-success">Tạo thông báo thành công!</div>
            </c:if>
            <c:if test="${not empty param.createError}">
                <div class="alert alert-danger">Lỗi tạo thông báo: <c:out value="${param.createError}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger">Lỗi: ${error}</div>
            </c:if>

            <%-- Dùng thẻ H3 và class .form-card --%>
            <div class="form-card">
                <h3>Tạo Thông báo Mới</h3>
                <form action="MainController" method="POST" id="createForm">
                    <input type="hidden" name="action" value="createThongBao">

                    <%-- Áp dụng cấu trúc .form-group và class .form-control --%>
                    <div class="form-group">
                        <label for="createTieuDe">Tiêu đề:</label>
                        <input type="text" id="createTieuDe" name="tieuDe" class="form-control" required>
                    </div>

                    <div class="form-group">
                        <label for="createNoiDung">Nội dung:</label>
                        <textarea id="createNoiDung" name="noiDung" class="form-control" required></textarea>
                    </div>

                    <div class="form-group">
                        <label>Gửi đến:</label>
                        <%-- Áp dụng style cho radio buttons (đã xóa onchange) --%>
                        <div class="radio-group">
                            <label class="radio-option">
                                <input type="radio" name="targetType" value="ALL" checked>
                                <span>Tất cả</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="targetType" value="ROLE">
                                <span>Theo vai trò</span>
                            </label>
                            <label class="radio-option">
                                <input type="radio" name="targetType" value="USER">
                                <span>Tài khoản cụ thể</span>
                            </label>
                        </div>
                    </div>

                    <div class="form-group" id="targetValueDiv" style="display:none;">
                        <label id="targetValueLabel" for="targetValueInput">Chọn:</label>
                        <select name="targetValue" id="targetValueInput" class="form-control"></select>
                    </div>

                    <%-- Áp dụng .form-actions và các class .btn --%>
                    <div class="form-actions">
                        <button type="submit" class="btn btn-primary">
                            <i class="fa-solid fa-paper-plane"></i> Gửi Thông báo
                        </button>
                    </div>
                </form>
            </div>


            <h3>Danh sách Thông báo đã gửi</h3>

            <%-- Thanh tìm kiếm chuẩn --%>
            <div class="page-header">
                <div class="search-container">
                    <form action="MainController" method="GET" class="search-form">
                        <input type="hidden" name="action" value="listNotifications">
                        <i class="fa-solid fa-magnifying-glass search-icon-left"></i>
                        <input type="text"
                               name="searchKeyword"
                               class="form-control"
                               value="<c:out value='${param.searchKeyword}'/>"
                               placeholder="Tìm theo tiêu đề, nội dung...">
                        <button type="submit" class="search-button">
                            <i class="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </form>
                    <c:if test="${not empty param.searchKeyword}">
                        <a href="MainController?action=listNotifications" class="btn btn-clear-search">
                            <i class="fa-solid fa-times"></i> Xóa lọc
                        </a>
                    </c:if>
                </div>
            </div>

            <%-- Bảng được bọc trong .table-responsive --%>
            <div class="table-responsive">
                <table>
                    <thead>
                        <tr>
                            <th>Tiêu đề</th>
                            <th>Nội dung</th>
                            <th>Gửi đến</th>
                            <th>Số lượng</th>
                            <th>Thời gian gửi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="noti" items="${notificationList}">
                            <tr>
                                <td><c:out value="${noti.tieuDe}"/></td>
                                <td><c:out value="${noti.noiDung}"/></td>
                                <td><c:out value="${noti.nguoiNhanDisplay}"/></td>
                                <td>${noti.soLuongNguoiNhan} người</td>
                                <td><c:out value="${noti.thoiGianGui}"/></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty notificationList}">
                            <tr>
                                <td colspan="5">Không tìm thấy thông báo nào.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

        </div> <%-- End .container --%>

        <%-- ========================================================== --%>
        <%-- (QUAN TRỌNG) Phần Script đã được tách biệt --%>
        <%-- ========================================================== --%>

        <%-- Bước 1: Khởi tạo dữ liệu động từ JSTL cho JS --%>
        <script>
            const roles = [<c:forEach var="role" items="${roles}" varStatus="loop">'${role}'<c:if test="${!loop.last}">, </c:if></c:forEach>];
                    const accounts = [<c:forEach var="acc" items="${accountList}" varStatus="loop">{ id: ${acc.id}, username: '${acc.tenDangNhap}' }<c:if test="${!loop.last}">, </c:if></c:forEach>
                    ];
                </script>

        <%-- Bước 2: Tải file JS logic (defer để chạy sau khi DOM tải) --%>
        <script src="<c:url value='/js/ThongBao.js'/>" defer></script>

        <jsp:include page="/WEB-INF/footer.jsp" /> 
    </body>
</html>