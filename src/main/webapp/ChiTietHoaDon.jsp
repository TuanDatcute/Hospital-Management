<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Chi tiết Hóa đơn ${invoice.maHoaDon}</title>
</head>
<body>
    <h2>Chi tiết Hóa đơn: ${invoice.maHoaDon}</h2>
    <p>Bệnh nhân: <strong>${invoice.hoTenBenhNhan}</strong></p>
    <p>Phiếu khám: ${invoice.maPhieuKhamBenh}</p>
    <p>Tổng tiền: 
        <strong>
            <td><fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/></td>
        </strong>
    </p>
    <p>Trạng thái: <strong>${invoice.trangThai}</strong></p>

    <%-- (Bạn có thể lặp qua chi tiết dịch vụ, thuốc ở đây nếu DTO của bạn hỗ trợ) --%>
    
    <hr>
    
    <h3>Ghi nhận Thanh toán (cho Lễ tân)</h3>
    <c:if test="${invoice.trangThai == 'CHUA_THANH_TOAN'}">
        <form action="MainController" method="POST">
            <input type="hidden" name="action" value="payInvoice">
            <input type="hidden" name="invoiceId" value="${invoice.id}">
            
            Số tiền: <input type="number" name="soTien" value="${invoice.tongTien}" required> <br/>
            Phương thức: 
            <select name="phuongThuc">
                <option value="TIEN_MAT">Tiền mặt</option>
                <option value="QUET_THE">Quẹt thẻ</option>
                <option value="CHUYEN_KHOAN">Chuyển khoản</option>
            </select> <br/>
            <button type="submit">Xác nhận Thanh toán</button>
        </form>
    </c:if>
    <c:if test="${invoice.trangThai == 'DA_THANH_TOAN'}">
        <p style="color:green;">Hóa đơn này đã được thanh toán.</p>
    </c:if>

    <hr>

    <h3>Lịch sử Giao dịch</h3>
    <table border="1">
        <thead>
            <tr>
                <th>Thời gian</th>
                <th>Số tiền</th>
                <th>Phương thức</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="txn" items="${transactions}">
                <tr>
                    <td><c:out value="${txn.thoiGianGiaoDich}"/></td>
                    <td><fmt:formatNumber value="${txn.soTien}" pattern="#,##0 '₫'"/></td>
                    <td>${txn.phuongThuc}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>