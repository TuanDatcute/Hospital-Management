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
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>

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
                            <th class="text-center">Hành động</th>
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
                                        <td class="actions text-center">
                                            <a href="<c:url value='/MainController?action=showUpdateServiceForm&id=${dv.id}'/>" class="btn btn-edit">
                                                <i class="fas fa-pencil-alt"></i> Sửa
                                            </a>
                                            <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn xóa dịch vụ \'${dv.tenDichVu}\'?');">
                                                <input type="hidden" name="action" value="deleteService">
                                                <input type="hidden" name="id" value="${dv.id}">
                                                <button type="submit" class="btn btn-delete">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="no-results">
                                        <c:choose>
                                            <c:when test="${not empty requestScope.searchKeyword}">Không tìm thấy dịch vụ nào khớp với từ khóa "${requestScope.searchKeyword}".</c:when>
                                            <c:otherwise>Chưa có dịch vụ nào trong hệ thống.</c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>

        <script src="<c:url value='/js/darkmode.js'/>"></script>
    </body>
</html>