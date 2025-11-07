<%--
    Document    : danhSachLichHen.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
    (ĐÃ NÂNG CẤP: Thêm Phân trang, CRUD Sửa, Sửa lỗi PRG & POST)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Lịch hẹn</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- Thêm CSS cho các nút phân trang (Giống file NhanVien) --%>
        <style>
            .pagination-container {
                margin-top: 20px;
                text-align: center;
            }
            .pagination-btn {
                display: inline-block;
                padding: 8px 16px;
                margin: 0 5px;
                background-color: #007bff;
                color: white;
                text-decoration: none;
                border-radius: 4px;
                font-weight: bold;
            }
            .pagination-btn.disabled {
                background-color: #cccccc;
                color: #666666;
                cursor: not-allowed;
            }
            .pagination-info {
                margin: 0 10px;
                font-size: 1.1em;
                vertical-align: middle;
            }
        </style>
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" />

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Lịch hẹn</h2>

            <%-- === SỬA LỖI PRG (Post-Redirect-Get) === --%>
            <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                <p class="success-message">${sessionScope.SUCCESS_MESSAGE}</p>
                <c:remove var="SUCCESS_MESSAGE" scope="session" />
            </c:if>
            <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                <p class="error-message">${sessionScope.ERROR_MESSAGE}</p>
                <c:remove var="ERROR_MESSAGE" scope="session" />
            </c:if>
            <%-- === KẾT THÚC SỬA LỖI PRG === --%>

            <a href="MainController?action=showLichHenCreateForm" class="add-new-btn">
                <i class="fas fa-calendar-plus"></i> Tạo Lịch hẹn Mới
            </a>

            <%-- TODO: Thêm bộ lọc (filter) theo ngày, bác sĩ, trạng thái ở đây --%>

            <table class="data-table">
                <thead>
                    <tr>
                        <th>STT</th>
                        <th>Thời gian hẹn</th>
                        <th>Bệnh nhân</th> <%-- SỬA: Bỏ TODO --%>
                        <th>Bác sĩ</th> <%-- SỬA: Bỏ TODO --%>
                        <th>Trạng thái</th>
                        <th style="width: 250px;">Cập nhật Trạng thái</th>
                        <th style="width: 50px;">Sửa</th> <%-- THÊM MỚI: Cột Sửa --%>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="lh" items="${requestScope.LIST_LICHHEN}">
                        <tr>
                            <td>${lh.stt}</td>
                            <td>
                                <%-- (Giữ nguyên logic format ngày) --%>
                                <fmt:parseDate value="${lh.thoiGianHen}" pattern="yyyy-MM-dd'T'HH:mmXXX" var="parsedDateTime" type="both" />
                                <fmt:formatDate value="${parsedDateTime}" pattern="dd/MM/yyyy HH:mm" />
                            </td>

                            <%-- === SỬA (HIỂN THỊ TÊN) === --%>
                            <td><c:out value="${lh.tenBenhNhan}" /></td>
                            <td><c:out value="${lh.tenBacSi}" /></td>
                            <%-- === KẾT THÚC SỬA === --%>

                            <td>
                                <%-- (Giữ nguyên logic hiển thị trạng thái) --%>
                                <c:choose>
                                    <c:when test="${lh.trangThai == 'HOAN_THANH'}"><span style="color: green; font-weight: bold;">Hoàn thành</span></c:when>
                                    <c:when test="${lh.trangThai == 'DA_XAC_NHAN'}"><span style="color: blue; font-weight: bold;">Đã xác nhận</span></c:when>
                                    <c:when test="${lh.trangThai == 'DA_HUY'}"><span style="color: red; font-weight: bold;">Đã hủy</span></c:when>
                                    <c:when test="${lh.trangThai == 'CHO_XAC_NHAN'}"><span style="color: orange; font-weight: bold;">Chờ xác nhận</span></c:when>
                                    <c:otherwise><c:out value="${lh.trangThai}"/></c:otherwise>
                                </c:choose>
                            </td>

                            <td class="actions">
                                <%-- (Giữ nguyên logic Cập nhật Trạng thái) --%>
                                <c:if test="${lh.trangThai != 'HOAN_THANH' && lh.trangThai != 'DA_HUY'}">
                                    <form action="MainController" method="post" class="table-form">
                                        <input type="hidden" name="action" value="updateLichHenStatus" />
                                        <input type="hidden" name="id" value="${lh.id}" />

                                        <select name="trangThai" class="form-control-sm">
                                            <option value="DA_XAC_NHAN" ${lh.trangThai == 'DA_XAC_NHAN' ? 'selected' : ''}>Đã xác nhận</option>
                                            <option value="DA_DEN_KHAM" ${lh.trangThai == 'DA_DEN_KHAM' ? 'selected' : ''}>Đã đến khám</option>
                                            <option value="HOAN_THANH" ${lh.trangThai == 'HOAN_THANH' ? 'selected' : ''}>Hoàn thành</option>
                                            <option value="DA_HUY" ${lh.trangThai == 'DA_HUY' ? 'selected' : ''}>Hủy lịch</option>
                                        </select>

                                        <button type="submit" class="btn-sm" title="Cập nhật">
                                            <i class="fas fa-check"></i>
                                        </button>
                                    </form>
                                </c:if>
                            </td>

                            <%-- === THÊM MỚI (CRUD SỬA) === --%>
                            <td class="actions">
                                <c:if test="${lh.trangThai != 'HOAN_THANH' && lh.trangThai != 'DA_HUY'}">
                                    <a href="MainController?action=showLichHenEditForm&id=${lh.id}" class="edit-btn" title="Sửa lịch hẹn">
                                        <i class="fas fa-edit"></i>
                                    </a>
                                </c:if>
                            </td>
                            <%-- === KẾT THÚC THÊM MỚI === --%>

                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.LIST_LICHHEN}">
                        <tr>
                            <%-- SỬA: Cập nhật colspan --%>
                            <td colspan="7" class="empty-cell">Không có dữ liệu lịch hẹn.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <%-- === BẮT ĐẦU THÊM MỚI (PHÂN TRANG) === --%>
            <div class="pagination-container">
                <%-- Lấy biến từ Controller --%>
                <c:set var="currentPage" value="${requestScope.currentPage}" />
                <c:set var="totalPages" value="${requestScope.totalPages}" />

                <c:if test="${totalPages > 1}">
                    <%-- Nút Trang Trước --%>
                    <c:choose>
                        <c:when test="${currentPage > 1}">
                            <a href="MainController?action=listLichHen&page=${currentPage - 1}" class="pagination-btn">&laquo; Trang trước</a>
                        </c:when>
                        <c:otherwise>
                            <span class="pagination-btn disabled">&laquo; Trang trước</span>
                        </c:otherwise>
                    </c:choose>

                    <%-- Hiển thị số trang --%>
                    <span class="pagination-info">
                        Trang ${currentPage} / ${totalPages}
                    </span>

                    <%-- Nút Trang Sau --%>
                    <c:choose>
                        <c:when test="${currentPage < totalPages}">
                            <a href="MainController?action=listLichHen&page=${currentPage + 1}" class="pagination-btn">Trang sau &raquo;</a>
                        </c:when>
                        <c:otherwise>
                            <span class="pagination-btn disabled">Trang sau &raquo;</span>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
            <%-- === KẾT THÚC THÊM MỚI (PHÂN TRANG) === --%>

        </div>

        <jsp:include page="/WEB-INF/footer.jsp" />

    </body>
</html>