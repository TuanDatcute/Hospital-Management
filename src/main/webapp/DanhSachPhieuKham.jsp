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
            <h1>Danh Sách Phiếu Khám Bệnh</h1>

            <div class="controls">
                <a href="<c:url value='/MainController?action=showCreateForm'/>" class="btn btn-success">Tạo Phiếu Khám Mới</a>
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
                                <td colspan="6" class="no-results">Chưa có phiếu khám nào trong hệ thống.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>