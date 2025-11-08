<%--
    Document    : danhSachNhanVien.jsp
    Created on  : Oct 29, 2025
    Author      : ADMIN
    (ĐÃ NÂNG CẤP: Thêm Phân trang, Tìm kiếm, Sửa lỗi PRG & Xóa Mềm)
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Nhân viên</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- Thêm CSS cho Phân trang và Tìm kiếm --%>
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
            .search-container {
                margin-top: 15px;
                margin-bottom: 20px;
                display: flex;
                justify-content: flex-end;
            }
            .search-container input[type="text"] {
                padding: 8px;
                width: 250px;
                border: 1px solid #ccc;
                border-radius: 4px 0 0 4px;
            }
            .search-container button {
                padding: 8px 12px;
                border: none;
                background-color: #007bff;
                color: white;
                cursor: pointer;
                border-radius: 0 4px 4px 0;
                margin-left: -1px;
            }
            .disabled-input {
                background-color: #f4f4f4;
                cursor: not-allowed;
            }
        </style>
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Quản lý Nhân viên</h2>

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

            <a href="MainController?action=showNhanVienCreateForm" class="add-new-btn">
                <i class="fas fa-user-plus"></i> Thêm Nhân viên Mới
            </a>

            <%-- === BẮT ĐẦU THÊM MỚI (FORM TÌM KIẾM) === --%>
            <div class="search-container">
                <form action="MainController" method="GET">
                    <input type="hidden" name="action" value="listNhanVien" />
                    <input type="text" name="keyword" 
                           placeholder="Tìm theo Tên, SĐT, Chuyên môn..." 
                           value="<c:out value='${requestScope.searchKeyword}' />" />
                    <button type="submit"><i class="fas fa-search"></i></button>
                </form>
            </div>
            <%-- === KẾT THÚC THÊM MỚI (FORM TÌM KIẾM) === --%>

            <table class="data-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Họ Tên</th>
                        <th>Chuyên môn</th>
                        <th>Số điện thoại</th>
                        <th>Khoa</th> <%-- SỬA: Đổi từ "ID Khoa" thành "Khoa" --%>
                        <th style="width: 100px;">Hành động</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="nv" items="${requestScope.LIST_NHANVIEN}">
                        <tr>
                            <td>${nv.id}</td>
                            <td><c:out value="${nv.hoTen}" /></td>
                            <td><c:out value="${nv.chuyenMon}" /></td>
                            <td><c:out value="${nv.soDienThoai}" /></td>

                            <%-- === SỬA (HIỂN THỊ TÊN) === --%>
                            <td>
                                <c:if test="${not empty nv.tenKhoa}"><c:out value="${nv.tenKhoa}" /></c:if>
                                <c:if test="${empty nv.tenKhoa}"><span style="color: #888;">N/A</span></c:if>
                                </td>
                            <%-- === KẾT THÚC SỬA === --%>

                            <td class="actions">
                                <c:if test="${sessionScope.USER.id != nv.taiKhoanId}">
                                    <a href="MainController?action=showNhanVienEditForm&id=${nv.id}" class="edit-btn" title="Sửa thông tin">
                                        <i class="fas fa-edit"></i>
                                    </a>

                                    <%-- === SỬA (XÓA MỀM & BẢO MẬT) === --%>
                                    <form action="MainController" method="POST" style="display: inline-block;"
                                          onsubmit="return confirm('Bạn có chắc chắn muốn vô hiệu hóa nhân viên này? \n(Tài khoản liên kết sẽ bị khóa)');">
                                        <input type="hidden" name="action" value="softDeleteNhanVien" />
                                        <input type="hidden" name="id" value="${nv.id}" />
                                        <button type="submit" class="delete-btn" title="Vô hiệu hóa">
                                            <i class="fas fa-user-lock"></i>
                                        </button>
                                    </form>
                                </c:if>
                                <c:if test="${sessionScope.USER.id == nv.taiKhoanId}">
                                    (Đây là bạn)
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty requestScope.LIST_NHANVIEN}">
                        <tr>
                            <td colspan="6" class="empty-cell">
                                <c:if test="${not empty requestScope.searchKeyword}">
                                    Không tìm thấy nhân viên nào với từ khóa "<c:out value='${requestScope.searchKeyword}' />".
                                </c:if>
                                <c:if test="${empty requestScope.searchKeyword}">
                                    Không có dữ liệu nhân viên.
                                </c:if>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>

            <%-- === PHÂN TRANG (Đã sửa để giữ keyword) === --%>
            <div class="pagination-container">
                <c:set var="currentPage" value="${requestScope.currentPage}" />
                <c:set var="totalPages" value="${requestScope.totalPages}" />
                <c:set var="searchKeyword" value="${requestScope.searchKeyword}" />

                <c:if test="${totalPages > 1}">
                    <%-- Nút Trang Trước --%>
                    <c:url var="prevPageUrl" value="MainController">
                        <c:param name="action" value="listNhanVien" />
                        <c:param name="page" value="${currentPage - 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage > 1 ? prevPageUrl : '#'}" 
                       class="pagination-btn ${currentPage > 1 ? '' : 'disabled'}">&laquo; Trang trước</a>

                    <span class="pagination-info">
                        Trang ${currentPage} / ${totalPages}
                    </span>

                    <%-- Nút Trang Sau --%>
                    <c:url var="nextPageUrl" value="MainController">
                        <c:param name="action" value="listNhanVien" />
                        <c:param name="page" value="${currentPage + 1}" />
                        <c:if test="${not empty searchKeyword}"><c:param name="keyword" value="${searchKeyword}" /></c:if>
                    </c:url>
                    <a href="${currentPage < totalPages ? nextPageUrl : '#'}" 
                       class="pagination-btn ${currentPage < totalPages ? '' : 'disabled'}">Trang sau &raquo;</a>
                </c:if>
            </div>
            <%-- === KẾT THÚC PHÂN TRANG === --%>

        </div> <%-- Kết thúc .container.page-content --%>

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>