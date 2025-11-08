<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Dịch Vụ</title>

        <link rel="stylesheet" href="<c:url value='/css/DanhSachDichVu-style.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script>
            (function () {
                // Key này (theme-preference) phải khớp với key trong theme.js
                var themeKey = 'theme-preference';
                var theme = localStorage.getItem(themeKey);

                if (theme === 'dark') {
                    // ✨ SỬA 3: Không đổi màu nền, mà thêm class vào <html>
                    document.documentElement.classList.add('dark-mode');
                }
            })();
        </script>
    </head>
    <body>
        <jsp:include page="_sidebar.jsp" />
        <div class="main-content">
            <div class="main-container">
                <h1>Quản lý Dịch Vụ</h1>

                <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                    <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                    <c:remove var="ERROR_MESSAGE" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                    <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                    <c:remove var="SUCCESS_MESSAGE" scope="session" />
                </c:if>

                <div class="header-controls">
                    <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                        <input type="hidden" name="action" value="listAndSearchServices">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" name="keyword" class="form-control" placeholder="Nhập tên dịch vụ..." value="${requestScope.searchKeyword}">
                        <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <c:if test="${not empty requestScope.searchKeyword}">
                            <a href="<c:url value='/MainController?action=listAndSearchServices&keyword='/>" class="btn btn-secondary">Xem tất cả</a>
                        </c:if>
                    </form>

                    <a href="<c:url value='/MainController?action=showCreateServiceForm'/>" class="btn btn-success"><i class="fas fa-plus"></i> Thêm Mới</a>

                    <div class="theme-switch-wrapper">
                        <label class="theme-switch" for="theme-toggle">
                            <input type="checkbox" id="theme-toggle" />
                            <div class="slider">
                                <i class="fas fa-sun sun-icon"></i>
                                <i class="fas fa-moon moon-icon"></i>
                            </div>
                        </label>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên Dịch Vụ</th>
                                <th>Mô Tả</th>
                                <th class="text-right">Đơn Giá</th>
                                <th class="text-center">Trạng Thái</th> <%-- ✨ SỬA 1: Di chuyển Trạng Thái về vị trí 5 --%>
                                <th class="text-center">Hành động</th> <%-- ✨ SỬA 2: Cột Hành động ở vị trí 6 (cuối cùng) --%>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty danhSachDichVu}">
                                    <c:forEach var="dv" items="${danhSachDichVu}">
                                        <tr>
                                            <td>${dv.id}</td>
                                            <td><strong>${dv.tenDichVu}</strong></td>
                                            <td>${dv.moTa}</td>
                                            <td class="text-right"><fmt:formatNumber value="${dv.donGia}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>

                                            <%-- ✨ SỬA 3: Hiển thị dữ liệu Trạng Thái ở cột 5 --%>
                                            <td class="text-center">
                                                <span class="status status-${dv.trangThai}">
                                                    ${dv.trangThai == 'SU_DUNG' ? 'Đang sử dụng' : 'Ngừng sử dụng'}
                                                </span>
                                            </td>

                                            <%-- ✨ SỬA 4: Gộp tất cả các nút vào cột Hành động (cột 6) --%>
                                            <td class="actions text-center">
                                                <a href="<c:url value='/MainController?action=showUpdateServiceForm&id=${dv.id}'/>" class="btn btn-edit">
                                                    <i class="fas fa-pencil-alt"></i> Sửa
                                                </a>

                                                <c:if test="${dv.trangThai == 'SU_DUNG'}">
                                                    <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn ngừng sử dụng dịch vụ này?');">
                                                        <input type="hidden" name="action" value="deactivateService">
                                                        <input type="hidden" name="id" value="${dv.id}">
                                                        <button type="submit" class="btn btn-delete">Ngừng</button>
                                                    </form>
                                                </c:if>

                                                <%-- (Tùy chọn) Thêm nút "Kích hoạt lại" nếu bạn muốn --%>
                                                <c:if test="${dv.trangThai == 'NGUNG_SU_DUNG'}">
                                                    <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;">
                                                        <input type="hidden" name="action" value="activateService">
                                                        <input type="hidden" name="id" value="${dv.id}">
                                                        <button type="submit" class="btn btn-success">Kích hoạt</button>
                                                    </form>
                                                </c:if>

                                                <%-- (Form Xóa đã bị xóa, bạn có thể thêm lại nếu muốn) --%>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <%-- ✨ SỬA 5: Colspan phải là 6 --%>
                                        <td colspan="6" class="no-results">
                                            <%-- (Nội dung không tìm thấy giữ nguyên) --%>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <script src="<c:url value='/js/theme.js'/>"></script>
        <script src="<c:url value='/js/darkmode.js'/>"></script>
    </body>
</html>