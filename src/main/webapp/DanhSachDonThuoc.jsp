<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Đơn Thuốc</title>
        <link rel="stylesheet" href="<c:url value='/css/danhSachDonThuoc-style.css'/>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script src="js/darkmode.js"></script>
    </head>
    <body>

        <div class="main-container">
            <h1>Danh Sách Đơn Thuốc</h1>

            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>

            <div class="header-controls">
                <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                    <input type="hidden" name="action" value="listAll">
                    <i class="fas fa-search search-icon"></i>
                    <input type="text" name="keyword" class="form-control" placeholder="Nhập tên bệnh nhân..." value="${requestScope.searchKeyword}">
                    <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                    <c:if test="${not empty requestScope.searchKeyword}">
                        <a href="<c:url value='/MainController?action=listAll'/>" class="btn btn-secondary">Xem tất cả</a>
                    </c:if>
                </form>

                <div class="theme-switch-wrapper">
                    <label class="theme-switch" for="theme-toggle">
                        <input type="checkbox" id="theme-toggle" />
                        <div class="slider round"></div>
                    </label>
                </div>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID Đơn thuốc</th>
                            <th>Ngày Kê Đơn</th>
                            <th>Mã Phiếu Khám</th>
                            <th>Bệnh Nhân</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty danhSachDonThuoc}">
                                <c:forEach var="donThuoc" items="${danhSachDonThuoc}">
                                    <tr>
                                        <td><strong>#${donThuoc.id}</strong></td>
                                        <td>${donThuoc.ngayKeDonFormatted}</td>
                                        <td>#${donThuoc.phieuKhamId}</td>
                                        <td>${donThuoc.tenBenhNhan}</td>
                                        <td class="actions text-center">
                                            <a href="<c:url value='MainController?action=viewDetails&id=${donThuoc.id}'/>" class="btn btn-primary">
                                                <i class="fas fa-eye"></i> Xem Chi Tiết
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="5" class="no-results">
                                        <c:choose>
                                            <c:when test="${not empty requestScope.searchKeyword}">
                                                Không tìm thấy đơn thuốc nào cho bệnh nhân "${requestScope.searchKeyword}".
                                            </c:when>
                                            <c:otherwise>
                                                Chưa có đơn thuốc nào trong hệ thống.
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </div>


    </body>
</html>