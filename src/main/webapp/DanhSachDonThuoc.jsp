<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Danh Sách Đơn Thuốc</title>
    <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <div class="container">
        <h1>Danh Sách Đơn Thuốc</h1>

        <%-- Hiển thị thông báo (nếu có) --%>
        <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
            <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
            <c:remove var="SUCCESS_MESSAGE" scope="session" />
        </c:if>
        
        <%-- ✨ FORM TÌM KIẾM THEO TÊN BỆNH NHÂN ✨ --%>
        <div class="header-controls">
            <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                <input type="hidden" name="action" value="listAll">
                <input type="text" name="keyword" class="form-control" placeholder="Nhập tên bệnh nhân..." value="${requestScope.searchKeyword}">
                <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                <%-- Nút để quay lại xem tất cả, chỉ hiện khi đang tìm kiếm --%>
                <c:if test="${not empty requestScope.searchKeyword}">
                     <a href="<c:url value='/MainController?action=listAll'/>" class="btn btn-secondary">Xem tất cả</a>
                </c:if>
            </form>
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
                                        <a href="<c:url value='MainController?action=viewDetails&id=${donThuoc.id}'/>" class="btn btn-primary">Xem Chi Tiết</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5" class="no-results">
                                    <%-- Hiển thị thông báo phù hợp --%>
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