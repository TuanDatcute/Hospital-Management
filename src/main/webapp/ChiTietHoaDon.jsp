<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>Chi tiết Hóa đơn ${invoice.maHoaDon}</title>
</head>
<body>
    <a href="MainController?action=listInvoices">&lt;&lt; Quay lại Danh sách</a>
    <hr>
    
    <h2>Chi tiết Hóa đơn: ${invoice.maHoaDon}</h2>
    <%-- Hiển thị thông tin từ DTO "phẳng" --%>
    <p>Bệnh nhân: <strong>${invoice.hoTenBenhNhan}</strong></p>
    <p>Trạng thái: <strong>${invoice.trangThai}</strong></p>
    
    <%-- ============================================= --%>
    <%--      THAY ĐỔI: Lấy từ request attribute      --%>
    <%-- ============================================= --%>
    <h3>Chi tiết Dịch vụ</h3>
    <table border="1" style="width: 70%">
        <thead>
            <tr> <th>STT</th> <th>Tên Dịch vụ</th> <th>Đơn giá</th> </tr>
        </thead>
        <tbody>
            <%-- SỬA: Dùng "danhSachDichVu" thay vì "invoice.danhSachDichVu" --%>
            <c:forEach var="dv" items="${danhSachDichVu}" varStatus="loop">
                <tr>
                    <td>${loop.count}</td>
                    <td><c:out value="${dv.tenDichVu}"/></td>
                    <td><fmt:formatNumber value="${dv.donGia}" pattern="#,##0 '₫'"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br>

    <%-- ============================================= --%>
    <%--      THAY ĐỔI: Lấy từ request attribute      --%>
    <%-- ============================================= --%>
    <h3>Chi tiết Đơn thuốc</h3>
    <table border="1" style="width: 70%">
        <thead>
            <tr>
                <th>STT</th> <th>Tên Thuốc</th> <th>Số lượng</th> <th>Đơn giá</th> <th>Thành tiền</th>
            </tr>
        </thead>
        <tbody>
            <%-- SỬA: Dùng "danhSachThuoc" thay vì "invoice.danhSachThuoc" --%>
            <c:forEach var="thuoc" items="${danhSachThuoc}" varStatus="loop">
                <tr>
                    <td>${loop.count}</td>
                    <td><c:out value="${thuoc.tenThuoc}"/></td>
                    <td>${thuoc.soLuong}</td>
                    <td><fmt:formatNumber value="${thuoc.donGia}" pattern="#,##0 '₫'"/></td>
                    <td><fmt:formatNumber value="${thuoc.thanhTien}" pattern="#,##0 '₫'"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    <br>
    
    <hr>
    <h3>TỔNG CỘNG: 
        <fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/>
    </h3>
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