<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Phiếu Khám Bệnh</title>
        <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="container">

            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>
                
            <h1>Danh Sách Phiếu Khám Bệnh</h1>



            <%-- ✨ KHU VỰC TÌM KIẾM VÀ THÊM MỚI ✨ --%>
            <div class="header-controls">
                <form action="<c:url value='/MainController'/>" method="GET" class="search-form">
                    <input type="hidden" name="action" value="listAllEncounters">
                    <input type="text" name="keyword" class="form-control" placeholder="Tìm theo mã phiếu, tên/mã bệnh nhân..." value="${requestScope.searchKeyword}">
                    <button type="submit" class="btn btn-primary">Tìm kiếm</button>
                    <%-- Nút để quay lại xem tất cả, chỉ hiện khi đang tìm kiếm --%>
                    <c:if test="${not empty requestScope.searchKeyword}">
                        <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary">Xem tất cả</a>
                    </c:if>
                </form>
                <a href="<c:url value='/MainController?action=showCreateEncounterForm'/>" class="btn btn-success">Tạo Phiếu Khám Mới</a>
            </div>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Mã Phiếu Khám</th>
                            <th>Thời Gian Khám</th>
                            <th>Bệnh Nhân</th>
                            <th>Bác Sĩ</th>
                            <th>Chẩn Đoán</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${not empty danhSachPhieuKham}">
                                <c:forEach var="pkb" items="${danhSachPhieuKham}">
                                    <tr>
                                        <td><strong>${pkb.maPhieuKham}</strong></td>
                                        <td>${pkb.thoiGianKhamFormatted}</td>
                                        <td>${pkb.tenBenhNhan}</td>
                                        <td>${pkb.tenBacSi}</td>
                                        <td>${pkb.chanDoan}</td>
                                        <td class="actions text-center">
                                            <a href="<c:url value='MainController?action=viewEncounterDetails&id=${pkb.id}'/>" class="btn btn-primary">Xem Chi Tiết</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="6" class="no-results">
                                        <%-- ✨ Hiển thị thông báo phù hợp --%>
                                        <c:choose>
                                            <c:when test="${not empty requestScope.searchKeyword}">
                                                Không tìm thấy phiếu khám nào khớp với từ khóa "${requestScope.searchKeyword}".
                                            </c:when>
                                            <c:otherwise>
                                                Chưa có phiếu khám nào trong hệ thống.
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