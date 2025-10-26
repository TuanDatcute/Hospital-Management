<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh Sách Thuốc</title>
    
    <%-- ✨ Link đến file CSS ngoài để dễ quản lý --%>
    <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>">
    
    <%-- (Tùy chọn) Thêm font từ Google Fonts cho đẹp hơn --%>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <div class="container">
        <h1>Quản lý Thuốc</h1>

        <%-- Hiển thị thông báo (nếu có) --%>
        <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
            <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
            <c:remove var="ERROR_MESSAGE" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
            <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
            <c:remove var="SUCCESS_MESSAGE" scope="session" />
        </c:if>
        
        <%-- Khu vực điều khiển (Tìm kiếm và Thêm mới) --%>
        <div class="header-controls">
            <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                <input type="hidden" name="action" value="listMedications">
                <input type="text" name="keyword" class="form-control" placeholder="Nhập tên thuốc cần tìm..." value="${requestScope.searchKeyword}">
                <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                <c:if test="${not empty requestScope.searchKeyword}">
                     <a href="<c:url value='/MainController?action=listMedications'/>" class="btn btn-secondary">Xem tất cả</a>
                </c:if>
            </form>
            <a href="<c:url value='/MainController?action=showMedicationForm'/>" class="btn btn-success">Thêm Thuốc Mới</a>
        </div>

        <%-- Bảng hiển thị danh sách thuốc --%>
        <div class="table-responsive">
            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Tên Thuốc</th>
                        <th>Hoạt Chất</th>
                        <th>ĐVT</th>
                        <th class="text-right">Đơn Giá</th>
                        <th class="text-right">Tồn Kho</th>
                        <th class="text-center">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty danhSachThuoc}">
                            <c:forEach var="thuoc" items="${danhSachThuoc}">
                                <tr>
                                    <td>${thuoc.id}</td>
                                    <td><strong>${thuoc.tenThuoc}</strong></td>
                                    <td>${thuoc.hoatChat}</td>
                                    <td>${thuoc.donViTinh}</td>
                                    <td class="text-right"><fmt:formatNumber value="${thuoc.donGia}" type="currency" currencySymbol="đ" maxFractionDigits="0"/></td>
                                    <td class="text-right">${thuoc.soLuongTonKho}</td>
                                    <td class="actions text-center">
                                        <a href="<c:url value='/MainController?action=showUpdateForm&id=${thuoc.id}'/>" class="btn btn-edit">Sửa</a>
                                        <%-- Form xóa  --%>
                                        <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa thuốc này?');">
                                            <input type="hidden" name="action" value="deleteMedication">
                                            <input type="hidden" name="id" value="${thuoc.id}">
                                            <button type="submit" class="btn btn-delete">Xóa</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="no-results">
                                    <c:choose>
                                        <c:when test="${not empty requestScope.searchKeyword}">
                                            Không tìm thấy thuốc nào khớp với từ khóa "${requestScope.searchKeyword}".
                                        </c:when>
                                        <c:otherwise>
                                            Chưa có dữ liệu thuốc nào trong hệ thống.
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