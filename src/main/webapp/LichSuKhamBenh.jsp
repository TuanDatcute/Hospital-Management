<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch Sử Khám Bệnh</title>
        <link rel="stylesheet" href="<c:url value='/css/danhSach-style.css'/>"> <%-- Tái sử dụng CSS cũ --%>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="container">
            <h1>Lịch Sử Khám Bệnh</h1>
            <h3>Bệnh nhân: <strong>${benhNhan.hoTen}</strong> (Mã: ${benhNhan.maBenhNhan})</h3>

            <%-- (Bạn có thể đặt link "Quay lại" ở đây) --%>

            <div class="table-responsive">
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>Mã Phiếu Khám</th>
                            <th>Thời Gian Khám</th>
                            <th>Bác Sĩ</th>
                            <th>Chẩn Đoán</th>
                            <th class="text-center">Trạng Thái</th>
                            <th class="text-center">Hành động</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${not empty danhSachLichSuKham}">
                            <c:forEach var="pkb" items="${danhSachLichSuKham}">
                                <tr>
                                    <td><strong>${pkb.maPhieuKham}</strong></td>
                                    <td>${pkb.thoiGianKhamFormatted}</td>
                                    <td>${pkb.tenBacSi}</td>
                                    <td>${pkb.chanDoan}</td>
                                    <td class="text-center">
                                        <span class="status status-${pkb.trangThai}">${pkb.trangThai}</span>
                                    </td>
                                    <td class="actions text-center">
                                        <%-- Link đến trang chi tiết bệnh án --%>
                                        <a href="<c:url value='MainController?action=viewEncounterDetails&id=${pkb.id}'/>" class="btn btn-primary">Xem Chi Tiết</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="6" class="no-results">Bạn chưa có lịch sử khám bệnh nào.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>