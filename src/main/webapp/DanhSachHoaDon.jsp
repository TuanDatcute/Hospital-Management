<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Quản lý Hóa đơn & Thanh toán</title>
</head>
<body>
    <h2>Quản lý Hóa đơn & Thanh toán</h2>
    <hr>

    <%-- ============================================= --%>
    <%--     THÊM: Thông báo cho việc tạo hóa đơn      --%>
    <%-- ============================================= --%>
    <c:if test="${not empty param.genSuccess}">
        <b style="color:green;">Đã tạo hóa đơn mới thành công!</b>
    </c:if>
    <c:if test="${not empty param.genError}">
        <b style="color:red;">Lỗi tạo hóa đơn: <c:out value="${param.genError}"/></b>
    </c:if>

    <h3>Danh sách Hóa đơn (Đã lập)</h3>
    
    <%-- Form Tìm kiếm (Giữ nguyên theo code của bạn) --%>
    <form action="MainController" method="GET">
        <input type="hidden" name="action" value="listInvoices"> 
        Tìm kiếm:
        <input type="text" 
               name="searchKeyword" 
               value="<c:out value='${param.searchKeyword}'/>" 
               placeholder="Nhập mã HĐ, tên bệnh nhân, trạng thái...">

        <button type="submit">Tìm</button>
        <a href="MainController?action=listInvoices">Xóa lọc</a>
    </form>
    <br> <%-- Thêm một khoảng ngắt nhỏ --%>

    <%-- Bảng Hóa đơn (Giữ nguyên theo code của bạn) --%>
    <table border="1" style="width:100%">
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
                    
                    <%-- Giữ nguyên theo yêu cầu của bạn --%>
                    <td><c:out value="${invoice.ngayTao}"/></td> 
                    
                    <td><fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/></td>
                    <td>${invoice.trangThai}</td>
                    <td>
                        <a href="MainController?action=viewInvoice&id=${invoice.id}">
                            Xem chi tiết
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <hr>
    
    <%-- ============================================= --%>
    <%--        THÊM: Bảng Chờ lập hóa đơn            --%>
    <%-- ============================================= --%>
    <h3>Danh sách Phiếu khám (Chờ lập hóa đơn)</h3>
    <p>Đây là các phiếu khám đã hoàn thành nhưng chưa được tính tiền.</p>
    
    <table border="1" style="width:100%">
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
                    
                    <%-- Hiển thị ngày theo cách bạn yêu cầu --%>
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
        </tbody>
    </table>

</body>
</html>