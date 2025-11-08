<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Danh Sách Thuốc</title>

        <link rel="stylesheet" href="<c:url value='/css/_sidebar.css?v=1.1'/>">

        <link rel="stylesheet" href="<c:url value='/css/danhSachThuoc-style.css?v=1.2'/>">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">

        <script>
            (function () {
                var themeKey = 'theme-preference'; // Key này phải khớp với theme.js
                var theme = localStorage.getItem(themeKey);
                if (theme === 'dark') {
                    document.documentElement.classList.add('dark-mode');
                }
            })();
        </script>   
    </head>
    <body>

        <jsp:include page="_sidebar.jsp" />

        <div class="main-content">



            <div class="main-container">
                <h1>Quản lý Thuốc</h1>

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
                        <input type="hidden" name="action" value="listMedications">
                        <i class="fas fa-search search-icon"></i>
                        <input type="text" name="keyword" class="form-control" placeholder="Nhập tên thuốc cần tìm..." value="${requestScope.searchKeyword}">
                        <button type="submit" class="btn btn-primary"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <c:if test="${not empty requestScope.searchKeyword}">
                            <a href="<c:url value='/MainController?action=listMedications'/>" class="btn btn-secondary">Xem tất cả</a>
                        </c:if>  
                    </form>

                    <a href="<c:url value='/MainController?action=showMedicationForm'/>" class="btn btn-success"><i class="fas fa-plus"></i> Thêm Thuốc Mới</a>
                    <div class="theme-switch-wrapper">
                        <label class="theme-switch" for="theme-toggle">
                            <input type="checkbox" id="theme-toggle" />
                            <div class="slider round">
                                <span class="sun-icon"><i class="fas fa-sun"></i></span>
                                <span class="moon-icon"><i class="fas fa-moon"></i></span>
                            </div>
                        </label>
                    </div>
                </div>


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
                                <th class="text-center">Trạng Thái</th> <%-- ✨ SỬA 1: Di chuyển Trạng Thái về vị trí 7 --%>
                                <th class="text-center">Hành động</th> <%-- ✨ SỬA 2: Cột Hành động ở vị trí 8 (cuối cùng) --%>
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
                                            <td class="text-right">
                                                <button class="stock-button update-stock-btn ...">
                                                    <i class="fas fa-box-open"></i> <span>${thuoc.soLuongTonKho}</span>
                                                </button>
                                            </td>

                                            <%-- ✨ SỬA 3: Hiển thị dữ liệu Trạng Thái ở cột 7 --%>
                                            <td class="text-center">
                                                <span class="status status-${thuoc.trangThai}">
                                                    ${thuoc.trangThai == 'SU_DUNG' ? 'Đang sử dụng' : 'Ngừng sử dụng'}
                                                </span>
                                            </td>

                                            <%-- ✨ SỬA 4: Gộp tất cả các nút vào cột Hành động (cột 8) --%>
                                            <td class="actions text-center">
                                                <a href="<c:url value='/MainController?action=showUpdateForm&id=${thuoc.id}'/>" class="btn btn-edit">
                                                    <i class="fas fa-pencil-alt"></i> Sửa
                                                </a>

                                                <c:if test="${thuoc.trangThai == 'SU_DUNG'}">
                                                    <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc muốn ngừng sử dụng thuốc này?');">
                                                        <input type="hidden" name="action" value="deactivateMedication">
                                                        <input type="hidden" name="id" value="${thuoc.id}">
                                                        <button type="submit" class="btn btn-delete">Ngừng</button>
                                                    </form>
                                                </c:if>

                                                <c:if test="${thuoc.trangThai == 'NGUNG_SU_DUNG'}">
                                                    <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;">
                                                        <input type="hidden" name="action" value="activateMedication">
                                                        <input type="hidden" name="id" value="${thuoc.id}">
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
                                        <%-- ✨ SỬA 5: Colspan phải là 8 --%>
                                        <td colspan="8" class="no-results">
                                            <%-- (Nội dung không tìm thấy giữ nguyên) --%>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div> <div id="stock-modal-overlay" class="modal-overlay">
                <div class="modal-content">
                    <div class="modal-header">
                        <h2 id="stock-modal-title">Cập nhật tồn kho</h2>
                        <button class="close-button" id="stock-close-button" aria-label="Đóng">&times;</button>
                    </div>
                    <form id="stock-modal-form" action="<c:url value='/MainController'/>" method="POST">
                        <input type="hidden" name="action" value="updateStock">
                        <input type="hidden" id="stock-thuocId-input" name="thuocId">
                        <div class="modal-body">
                            <p>Thuốc: <strong id="stock-thuocName"></strong></p>
                            <p>Tồn kho hiện tại: <strong id="stock-current"></strong></p>
                            <div class="form-group">
                                <label for="soLuongThayDoi">Số lượng Thay đổi (*)</label>
                                <input type="number" id="soLuongThayDoi" name="soLuongThayDoi" class="form-control" 
                                       placeholder="Nhập số dương để thêm, số âm để bớt" required>
                                <small>Ví dụ: Nhập `100` để thêm 100. Nhập `-25` để bớt 25.</small>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" id="stock-cancel-button" class="btn btn-secondary">Hủy</button>
                            <button type="submit" class="btn btn-primary">Lưu thay đổi</button>
                        </div>
                    </form>
                </div>
            </div>

        </div> 
        <script src="<c:url value='/js/darkmode.js'/>"></script>
        <script src="<c:url value='/js/theme.js'/>"></script>
        <script src="<c:url value='/js/danhSachThuoc.js'/>"></script>
    </body>
</html>