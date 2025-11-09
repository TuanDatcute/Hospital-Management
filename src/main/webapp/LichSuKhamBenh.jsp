<%-- Tên tệp: /patient/history.jsp (hoặc tên tệp của bạn) --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Lịch Sử Khám Bệnh - Bệnh viện HQĐ</title>

        <link rel="preconnect" href="https://fonts.googleapis.com">
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet"
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" type="text/css" href="<c:url value='/css/index.css'/>?v=1.4">
        <link rel="stylesheet" type="text/css" href="<c:url value='/css/lichSuKhamBenh.css'/>?v=1.4">
    </head>
    <body>

        <%-- 1. Thêm Header --%>
        <jsp:include page="/WEB-INF/headerDat.jsp" />

        <%-- 2. Bọc nội dung trong page-content --%>
        <div class="page-content">
            <div class="container">
                <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                    <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                    <c:remove var="ERROR_MESSAGE" scope="session" />
                </c:if>
                <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                    <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                    <c:remove var="SUCCESS_MESSAGE" scope="session" />
                </c:if>

                <h1>Lịch Sử Khám Bệnh</h1>
                <h3>Bệnh nhân: <strong>${benhNhan.hoTen}</strong> (Mã: ${benhNhan.maBenhNhan})</h3>

                <div class="table-responsive">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <%-- ✨ ĐÃ XÓA CỘT NÚT EXPAND --%>
                                <th>Mã Phiếu Khám</th>
                                <th>Thời Gian Khám</th>
                                <th>Bác Sĩ</th>
                                <th>Chẩn Đoán</th>
                                <th>Kết Luận</th>
                                <th class="text-center">Trạng Thái</th>
                                <th class="text-center">Hành động</th>
                            </tr>
                        </thead>
                        <tbody id="history-table-body">
                            <%-- Dùng varStatus để tạo ID duy nhất cho mỗi hàng --%>
                            <c:forEach var="pkb" items="${danhSachLichSuKham}" varStatus="loop">
                                <%-- HÀNG 1: HÀNG CHÍNH --%>
                                <tr class="main-row">
                                    <%-- ✨ ĐÃ XÓA NÚT EXPAND --%>
                                    <td><strong>${pkb.maPhieuKham}</strong></td>
                                    <td>${pkb.thoiGianKhamFormatted}</td>
                                    <td>${pkb.tenBacSi}</td>
                                    <td>${pkb.chanDoan}</td>
                                    <td>${pkb.ketLuan}</td>
                                    <td class="text-center">
                                        <span class="status status-${fn:replace(pkb.trangThai, ' ', '_')}">${pkb.trangThai}</span>
                                    </td>                                   
                                    <td class="text-center">
                                        <%-- ✨ SỬA NÚT VIEW: Thêm data attributes cho JS --%>
                                        <button class="action-btn view view-details-btn" title="Xem chi tiết" 
                                                data-target-id="details-content-${loop.index}"
                                                data-title="Chi tiết Phiếu khám: ${pkb.maPhieuKham}">
                                            <i class="fas fa-eye"></i>
                                        </button>
                                        <a href="<c:url value='/MainController?action=printEncounter&id=${pkb.id}'/>" target="_blank" class="action-btn print" title="In phiếu">
                                            <i class="fas fa-print"></i>
                                        </a>
                                    </td>
                                </tr>

                                <%-- HÀNG 2: CONTAINER DATA (LUÔN ẨN) --%>
                                <tr class="details-row-hidden" id="details-${pkb.id}">
                                    <td colspan="6"> <%-- Cập nhật colspan=6 --%>
                                        <%-- Đặt ID cho nội dung cần clone --%>
                                        <div class="details-content" id="details-content-${loop.index}">
                                            <%-- Cột Dịch Vụ --%>
                                            <div>
                                                <strong>Dịch vụ đã chỉ định:</strong>
                                                <table class="mini-table">
                                                    <thead><tr><th>Tên Dịch Vụ</th><th>Kết Quả</th></tr></thead>
                                                    <tbody>
                                                        <c:forEach var="dv" items="${pkb.danhSachChiDinh}">
                                                            <tr><td>${dv.tenDichVu}</td><td><c:out value="${dv.ketQua}" default="Chưa có"/></td></tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                            <%-- Cột Đơn Thuốc --%>
                                            <div>
                                                <strong >Thuốc đã kê:</strong>
                                                <c:choose>
                                                    <c:when test="${not empty pkb.donThuoc.chiTietDonThuoc}">
                                                        <table class="mini-table">
                                                            <thead><tr><th>Tên Thuốc</th><th>SL</th><th>Liều Dùng</th></tr></thead>
                                                            <tbody>
                                                                <c:forEach var="thuoc" items="${pkb.donThuoc.chiTietDonThuoc}">
                                                                    <tr><td>${thuoc.tenThuoc}</td><td>${thuoc.soLuong}</td><td>${thuoc.lieuDung}</td></tr>
                                                                </c:forEach>
                                                            </tbody>
                                                        </table>
                                                    </c:when>
                                                    <c:otherwise><p>Không có đơn thuốc.</p></c:otherwise>
                                                </c:choose>
                                            </div>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <%-- 3. Thêm Footer --%>
        <footer class="main-footer">
            <div class="container">
                <jsp:include page="/WEB-INF/footer.jsp" /> 
            </div>
        </footer>

        <%-- ✨ 4. THÊM CẤU TRÚC HTML CHO POPUP (MODAL) --%>
        <div class="modal-overlay" id="detailsModalOverlay">
            <div class="modal-container" id="detailsModalContainer">
                <div class="modal-header">
                    <h4 id="modalTitle">Chi tiết Phiếu khám</h4>
                    <button class="modal-close-btn" id="modalCloseBtn">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
                <div class="modal-body" id="modalBody">
                    <%-- Nội dung chi tiết sẽ được JS chèn vào đây --%>
                </div>
            </div>
        </div>

        <%-- ✨ 5. THAY THẾ JAVASCRIPT --%>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                const tableBody = document.getElementById('history-table-body');
                const modalOverlay = document.getElementById('detailsModalOverlay');
                const modalContainer = document.getElementById('detailsModalContainer');
                const modalTitle = document.getElementById('modalTitle');
                const modalBody = document.getElementById('modalBody');
                const modalCloseBtn = document.getElementById('modalCloseBtn');

                // Hàm mở Modal
                function openModal(title, contentHtml) {
                    modalTitle.textContent = title;
                    modalBody.innerHTML = contentHtml;
                    modalOverlay.classList.add('active');
                }

                // Hàm đóng Modal
                function closeModal() {
                    modalOverlay.classList.remove('active');
                    // Xóa nội dung cũ để tránh bị "flash" khi mở lần sau
                    modalBody.innerHTML = '';
                }

                // Bắt sự kiện click trên toàn bộ body của bảng
                tableBody.addEventListener('click', function (event) {
                    const target = event.target;
                    // Kiểm tra xem có phải nhấn vào nút "view" không
                    const viewBtn = target.closest('.view-details-btn');

                    if (viewBtn) {
                        event.preventDefault(); // Ngăn hành vi mặc định của button

                        // Lấy thông tin từ data-attributes
                        const targetContentId = viewBtn.dataset.targetId;
                        const title = viewBtn.dataset.title;

                        // Tìm container chứa data
                        const contentContainer = document.getElementById(targetContentId);

                        if (contentContainer) {
                            // Lấy HTML bên trong container
                            const contentHtml = contentContainer.innerHTML;
                            // Mở modal
                            openModal(title, contentHtml);
                        } else {
                            console.error('Không tìm thấy nội dung chi tiết cho: ', targetContentId);
                        }
                    }
                });

                // Bắt sự kiện đóng modal
                modalCloseBtn.addEventListener('click', closeModal);
                modalOverlay.addEventListener('click', function (event) {
                    // Chỉ đóng khi nhấn vào chính lớp phủ (overlay), không phải modal
                    if (event.target === modalOverlay) {
                        closeModal();
                    }
                });

            });
        </script>
        <%-- ✨ 3. THÊM THƯ VIỆN SWIPER.JS (Bắt buộc) --%>
        <script src="https://cdn.jsdelivr.net/npm/swiper@11/swiper-bundle.min.js"></script>

        <%-- ✨ 4. LINK TỚI FILE JS (Sẽ cập nhật ở Bước 3) --%>
        <script src="<c:url value='/js/index.js'/>"></script>
    </body>
</html>