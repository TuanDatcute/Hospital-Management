<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <title>Thông báo của tôi</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <%-- BẮT BUỘC: Thêm Font Awesome cho icon --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">

        <%-- BẮT BUỘC: Nhúng file CSS chung TRƯỚC --%>
        <link rel="stylesheet" href="<c:url value='/css/StyleChungCuaQuang.css'/>"> <%-- Hoặc base.css --%>

        <%-- (MỚI) Nhúng file CSS cụ thể cho trang này SAU --%>
        <link rel="stylesheet" href="<c:url value='/css/UserThongBao.css'/>">

        <script src="<c:url value='/js/darkmode.js'/>" defer></script>
    </head>
    <body>

        <%-- (SỬA) Bọc toàn bộ trang trong .container --%>
        <div class="container" style="max-width: 900px;"> <%-- Container hẹp hơn cho giao diện hộp thư --%>

            <div class="page-header">
                <h1>Thông báo của tôi</h1>
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

            <%-- (SỬA) Thay thế form tìm kiếm cũ bằng component .search-form chuẩn --%>
            <div class="page-header" style="margin-bottom: 20px;">
                <div class="search-container">
                    <form action="MainController" method="GET" class="search-form">
                        <input type="hidden" name="action" value="viewMyNotifications">
                        <i class="fa-solid fa-magnifying-glass search-icon-left"></i>
                        <input type="text"
                               name="searchKeyword"
                               class="form-control"
                               value="<c:out value='${param.searchKeyword}'/>"
                               placeholder="Tìm trong thông báo...">
                        <button type="submit" class="search-button">
                            <i class="fa-solid fa-magnifying-glass"></i>
                        </button>
                    </form>
                     <c:if test="${not empty param.searchKeyword}">
                        <a href="MainController?action=viewMyNotifications" class="btn btn-clear-search">
                           <i class="fa-solid fa-times"></i> Xóa lọc
                        </a>
                     </c:if>
                </div>
            </div>

            <%-- (SỬA) Thay <b style=""> bằng component .alert --%>
            <c:if test="${not empty param.deleteSuccess}">
                <div class="alert alert-success">Đã xóa thông báo.</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger">Lỗi: <c:out value="${param.error}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                 <div class="alert alert-danger">Lỗi: ${error}</div>
            </c:if>

            <%-- Danh sách thông báo (Dùng <ul> <li>) --%>
            <ul class="notification-list">
                <c:forEach var="noti" items="${notificationList}">
                    <%-- (SỬA) Class .unread được thêm/xóa động --%>
                    <li class="notification-item ${noti.daDoc ? '' : 'unread'}">

                        <div class="notification-content">
                            <h4><c:out value="${noti.tieuDe}"/></h4>
                            <p><c:out value="${noti.noiDung}"/></p>
                            <span class="notification-time">
                                <i class="fa-solid fa-clock"></i> <c:out value="${noti.thoiGianGui}"/>
                            </span>
                        </div>
                        
                        <%-- (SỬA) Gộp các nút vào .actions --%>
                        <div class="notification-actions">
                            <%-- Nút Đánh dấu đã đọc --%>
                            <c:if test="${!noti.daDoc}">
                                <form action="MainController" method="POST">
                                    <input type="hidden" name="action" value="markNotificationAsRead">
                                    <input type="hidden" name="id" value="${noti.id}">
                                    <%-- (SỬA) Dùng class .btn chuẩn --%>
                                    <button type="submit" class="btn btn-sm btn-success">
                                        <i class="fa-solid fa-check"></i> Đánh dấu đã đọc
                                    </button>
                                </form>
                            </c:if>

                            <%-- Nút Xóa mềm --%>
                            <%-- (SỬA) Thêm class .form-delete-confirm để JS bắt sự kiện --%>
                            <form action="MainController" method="POST" class="form-delete-confirm">
                                <input type="hidden" name="action" value="deleteMyNotification">
                                <input type="hidden" name="id" value="${noti.id}">
                                <%-- (SỬA) Dùng class .btn chuẩn --%>
                                <button type="submit" class="btn btn-sm btn-danger">
                                    <i class="fa-solid fa-trash"></i> Xóa
                                </button>
                            </form>
                        </div>
                    </li>
                </c:forEach>

                <c:if test="${empty notificationList}">
                    <li class="notification-item-empty">
                        <i class="fa-solid fa-bell-slash"></i>
                        Bạn không có thông báo nào.
                    </li>
                </c:if>
            </ul>

        </div> <%-- End .container --%>

        <%-- ========================================================== --%>
        <%-- (MỚI) Nhúng file JS cho logic xác nhận xóa --%>
        <%-- ========================================================== --%>
        <script src="<c:url value='/js/UserThongBao.js'/>" defer></script>

    </body>
</html>