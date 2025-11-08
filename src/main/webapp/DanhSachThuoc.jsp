<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Quản lý Danh Sách Thuốc</title>

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

        <div class="theme-switch-wrapper">
            <label class="theme-switch" for="theme-toggle">
                <input type="checkbox" id="theme-toggle" />
                <div class="slider round">
                    <span class="sun-icon"><i class="fas fa-sun"></i></span>
                    <span class="moon-icon"><i class="fas fa-moon"></i></span>
                </div>
            </label>
        </div>

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
                                        <td class="text-right">
                                           
                                            <button class="stock-button update-stock-btn
                                                    <c:if test='${thuoc.soLuongTonKho == 0}'>stock-empty</c:if>
                                                    <c:if test='${thuoc.soLuongTonKho > 0 && thuoc.soLuongTonKho <= 20}'>stock-low</c:if>" 
                                                    data-id="${thuoc.id}" 
                                                    data-name="${thuoc.tenThuoc}" 
                                                    data-current-stock="${thuoc.soLuongTonKho}">

                                                <i class="fas fa-box-open"></i> <span>${thuoc.soLuongTonKho}</span>

                                            </button>
                                        </td>
                                        <td class="actions text-center">
                                            <a href="<c:url value='/MainController?action=showUpdateForm&id=${thuoc.id}'/>" class="btn btn-edit">
                                                <i class="fas fa-pencil-alt"></i> Sửa
                                            </a>
                                            <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;" onsubmit="return confirm('Bạn có chắc chắn muốn xóa thuốc này?');">
                                                <input type="hidden" name="action" value="deleteMedication">
                                                <input type="hidden" name="id" value="${thuoc.id}">
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

        <div id="stock-modal-overlay" class="modal-overlay">
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

        <script src="<c:url value='/js/darkmode.js'/>"></script>
        <script src="<c:url value='/js/danhSachThuoc.js'/>"></script>
    </body>
</html>