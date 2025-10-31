<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Quản lý Hóa đơn & Thanh toán</title>
    <%-- Liên kết đến tệp CSS --%>
    <link rel="stylesheet" href="css/DanhSachHoaDon.css">
</head>
<body>
    <h2>Quản lý Hóa đơn & Thanh toán</h2>
    <hr>

    <%-- ============================================= --%>
    <%--    THÔNG BÁO (ĐÃ CẬP NHẬT DÙNG CLASS CSS)   --%>
    <%-- ============================================= --%>
    <c:if test="${not empty param.genSuccess}">
        <div class="message success">
            Đã tạo hóa đơn mới thành công!
        </div>
    </c:if>
    <c:if test="${not empty param.genError}">
        <div class="message error">
            Lỗi tạo hóa đơn: <c:out value="${param.genError}"/>
        </div>
    </c:if>

    <h3>Danh sách Hóa đơn (Đã lập)</h3>
    
    <%-- Form Tìm kiếm --%>
    <form action="MainController" method="GET">
        <input type="hidden" name="action" value="listInvoices">    
        <label for="searchKeyword">Tìm kiếm:</label>
        <input type="text" 
               id="searchKeyword"
               name="searchKeyword"    
               value="<c:out value='${param.searchKeyword}'/>"    
               placeholder="Nhập mã HĐ, tên bệnh nhân, trạng thái...">

        <button type="submit">Tìm</button>
        <a href="MainController?action=listInvoices">Xóa lọc</a>
    </form>
    <br> <%-- Thêm một khoảng ngắt nhỏ --%>

    <%-- Bảng Hóa đơn --%>
    <table>
        <thead>
            <tr>
                <th>Mã Hóa Đơn</th>
                <th>Bệnh nhân</th>
                <th>Ngày tạo</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="invoice" items="${invoiceList}">
                <tr>
                    <td><c:out value="${invoice.maHoaDon}"/></td>
                    <td><c:out value="${invoice.hoTenBenhNhan}"/></td>
                    
                    <td><c:out value="${invoice.ngayTao}"/></td>    
                    
                    <td><fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/></td>
                    <td><c:out value="${invoice.trangThai}"/></td>
                    <td>
                        <a href="MainController?action=viewInvoice&id=${invoice.id}">
                            Xem chi tiết
                        </a>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty invoiceList}">
                <tr>
                    <td colspan="6" style="text-align: center; color: var(--secondary-color);">Không tìm thấy hóa đơn nào.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

    <hr>
    
    <%-- ============================================= --%>
    <%--    Bảng Chờ lập hóa đơn                 --%>
    <%-- ============================================= --%>
    <h3>Danh sách Phiếu khám (Chờ lập hóa đơn)</h3>
    
    <table>
        <thead>
            <tr>
                <th>Mã Phiếu Khám</th>
                <th>Bệnh nhân</th>
                <th>Bác sĩ</th>
                <th>Thời gian khám</th>
                <th>Hành động</th>
            </tr>
        </thead>
        <tbody>
            <%-- Lặp qua 'pendingEncounterList' (được gửi từ Controller) --%>
            <c:forEach var="pk" items="${pendingEncounterList}">
                <tr>
                    <td><c:out value="${pk.maPhieuKham}"/></td>
                    <td><c:out value="${pk.tenBenhNhan}"/></td>
                    <td><c:out value="${pk.tenBacSi}"/></td>
                    
                    <td><c:out value="${pk.thoiGianKham}"/></td>
                    
                    <td>
                        <%-- Form này sẽ POST đến action 'generateInvoice' --%>
                        <form action="MainController" method="POST" style="margin:0;">
                            <input type="hidden" name="action" value="generateInvoice">
                            <input type="hidden" name="phieuKhamId" value="${pk.id}">
                            <button type="submit">Lập Hóa đơn</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty pendingEncounterList}">
                <tr>
                    <td colspan="5" style="text-align: center; color: var(--secondary-color);">Không có phiếu khám nào chờ lập hóa đơn.</td>
                </tr>
            </c:if>
        </tbody>
    </table>

</body>
</html>