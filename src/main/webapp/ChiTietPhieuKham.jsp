<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi Tiết Phiếu Khám Bệnh</title>

        <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
        <link rel="stylesheet" href="<c:url value='/css/ctdt-style.css'/>">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="main-container">

            <%-- TIÊU ĐỀ --%>
            <div class="header-section">
                <h1>Chi Tiết Phiếu Khám Bệnh</h1>
                <h2>Mã Phiếu Khám: ${phieuKham.maPhieuKham}</h2>
            </div>

            <%-- THÔNG TIN CHUNG --%>
            <div class="info-grid">
                <div class="info-item">
                    <span class="label">Bệnh Nhân:</span>
                    <span class="value">${phieuKham.tenBenhNhan}</span>
                </div>
                <div class="info-item">
                    <span class="label">Bác Sĩ Khám:</span>
                    <span class="value">${phieuKham.tenBacSi}</span>
                </div>
                <div class="info-item">
                    <span class="label">Thời Gian Khám:</span>
                    <span class="value">${phieuKham.thoiGianKhamFormatted}</span>
                </div>
            </div>

            <%-- CHỈ SỐ SINH TỒN --%>
            <div class="section">
                <h3>I. Chỉ số sinh tồn</h3>
                <div class="vitals-grid">
                    <div class="vital-item"><span class="label">Nhiệt độ:</span> <span class="value">${phieuKham.nhietDo}°C</span></div>
                    <div class="vital-item"><span class="label">Huyết áp:</span> <span class="value">${phieuKham.huyetAp} mmHg</span></div>
                    <div class="vital-item"><span class="label">Nhịp tim:</span> <span class="value">${phieuKham.nhipTim} lần/phút</span></div>
                    <div class="vital-item"><span class="label">Nhịp thở:</span> <span class="value">${phieuKham.nhipTho} lần/phút</span></div>
                </div>
            </div>

            <%-- THÔNG TIN LÂM SÀNG --%>
            <div class="section">
                <h3>II. Thông tin lâm sàng</h3>
                <div class="clinical-item">
                    <h4>Triệu chứng:</h4>
                    <p>${phieuKham.trieuChung}</p>
                </div>
                <div class="clinical-item">
                    <h4>Chẩn đoán:</h4>
                    <p><strong>${phieuKham.chanDoan}</strong></p>
                </div>
                <div class="clinical-item">
                    <h4>Kết luận & Dặn dò:</h4>
                    <p>${phieuKham.ketLuan}</p>
                </div>
                <c:if test="${not empty phieuKham.ngayTaiKham}">
                    <div class="clinical-item">
                        <h4>Ngày tái khám:</h4>
                        <p>${phieuKham.ngayTaiKhamFormatted}</p>
                    </div>
                </c:if>
            </div>

            <%-- CÁC CHỈ ĐỊNH VÀ ĐƠN THUỐC --%>
            <div class="sub-grid">
                <%-- CỘT DỊCH VỤ ĐÃ CHỈ ĐỊNH --%>
                <div class="section">
                    <h3>III. Dịch vụ đã chỉ định</h3>

                    <%-- Hiển thị thông báo (nếu có) --%>
                    <c:if test="${not empty sessionScope.ERROR_MESSAGE}">
                        <div class="alert alert-danger">${sessionScope.ERROR_MESSAGE}</div>
                        <c:remove var="ERROR_MESSAGE" scope="session" />
                    </c:if>
                    <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                        <div class="alert alert-success">${sessionScope.SUCCESS_MESSAGE}</div>
                        <c:remove var="SUCCESS_MESSAGE" scope="session" />
                    </c:if>

                    <%-- Form thêm dịch vụ mới --%>
                    <div class="add-service-form">
                        <form action="<c:url value='/MainController'/>" method="POST">
                            <input type="hidden" name="action" value="addServiceRequest">
                            <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                            <div class="form-group">
                                <label for="dichVuId">Thêm dịch vụ mới:</label>
                                <select id="dichVuId" name="dichVuId" class="form-control" required>
                                    <option value="">-- Chọn dịch vụ --</option>
                                    <c:forEach var="dv" items="${danhSachDichVu}">
                                        <option value="${dv.id}">${dv.tenDichVu}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <button type="submit" class="btn btn-primary">Thêm</button>
                        </form>
                    </div>

                    <%-- Bảng liệt kê các dịch vụ đã chỉ định --%>
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Tên Dịch Vụ</th>
                                <th>Trạng Thái</th>
                                <th>Kết Quả</th>
                                <th class="text-center">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty danhSachChiDinh}">
                                    <c:forEach var="chiDinh" items="${phieuKham.danhSachChiDinh}"><tr>
                                        <tr>
                                            <td><strong>${chiDinh.tenDichVu}</strong></td>
                                            <td><span class="status status-${chiDinh.trangThai}">${chiDinh.trangThai}</span></td>
                                            <td>${chiDinh.ketQua}</td>
                                            <td class="actions text-center">
                                                <%-- Form để cập nhật kết quả và trạng thái --%>
                                                <form action="<c:url value='/MainController'/>" method="POST" style="display:inline;">
                                                    <input type="hidden" name="action" value="updateServiceResult">
                                                    <input type="hidden" name="phieuKhamId" value="${phieuKham.id}">
                                                    <input type="hidden" name="chiDinhId" value="${chiDinh.id}">
                                                    <%-- Bạn có thể thêm một popup để nhập kết quả ở đây --%>
                                                    <input type="text" name="ketQuaMoi" placeholder="Nhập kết quả..." required>
                                                    <button type="submit" class="btn btn-edit">Cập nhật</button>
                                                </form>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="4" class="no-results">Chưa có dịch vụ nào được chỉ định.</td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>


                <%-- CỘT ĐƠN THUỐC --%>
                <div class="section">
                    <h3>IV. Đơn thuốc</h3>
                    <c:choose>
                        <c:when test="${not empty phieuKham.donThuoc}">
                            <p>Đã có đơn thuốc cho lần khám này.</p>
                            <a href="<c:url value='MainController?action=viewDetails&id=${phieuKham.donThuoc.id}'/>" class="btn btn-primary">Xem & Quản lý Đơn thuốc</a>
                        </c:when>
                        <c:otherwise>
                            <p>Chưa có đơn thuốc nào được kê.</p>
                            <%-- Link để tạo đơn thuốc mới cho phiếu khám này --%>
                            <a href="<c:url value='MainController?action=showCreateForm&phieuKhamId=${phieuKham.id}'/>" class="btn btn-success">Kê Đơn Thuốc</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>


            <%-- CÁC NÚT HÀNH ĐỘNG --%>
            <div class="action-buttons">
                <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary">Quay lại danh sách</a>
                <a href="#" class="btn btn-edit">Chỉnh sửa Phiếu khám</a>
            </div>
        </div>
    </body>
</html>