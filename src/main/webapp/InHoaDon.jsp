<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
<head>
    <title>In Hóa Đơn - ${invoice.maHoaDon}</title>
    
    <%-- 
      ĐÃ TÁCH CSS RA FILE RIÊNG
      Giả sử bạn lưu file CSS trong thư mục /css/ nằm ở thư mục gốc của webapp 
    --%>
    <link rel="stylesheet" href="css/InHoaDon.css">

</head>

<body onload="window.print()">

    <h1>HÓA ĐƠN DỊCH VỤ Y TẾ</h1>
    
    <%-- Thông tin chung --%>
    <p><strong>Mã Hóa Đơn:</strong> ${invoice.maHoaDon}</p>
    <p><strong>Bệnh nhân:</strong> ${invoice.hoTenBenhNhan}</p>
    <p><strong>Trạng thái:</strong> ${invoice.trangThai}</p>
    <p><strong>Ngày tạo:</strong> <c:out value="${invoice.ngayTao}"/></p>
    
    <hr>

    <h3>Chi tiết Dịch vụ</h3>
    <table>
        <thead>
            <tr> <th>STT</th> <th>Tên Dịch vụ</th> <th>Đơn giá</th> </tr>
        </thead>
        <tbody>
            <c:forEach var="dv" items="${danhSachDichVu}" varStatus="loop">
                <tr>
                    <td>${loop.count}</td>
                    <td><c:out value="${dv.tenDichVu}"/></td>
                    <td style="text-align: right;"><fmt:formatNumber value="${dv.donGia}" pattern="#,##0 '₫'"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <h3>Chi tiết Đơn thuốc</h3>
    <table>
        <thead>
            <tr>
                <th>STT</th> <th>Tên Thuốc</th> <th>Số lượng</th> <th>Đơn giá</th> <th>Thành tiền</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="thuoc" items="${danhSachThuoc}" varStatus="loop">
                <tr>
                    <td>${loop.count}</td>
                    <td><c:out value="${thuoc.tenThuoc}"/></td>
                    <td style="text-align: right;">${thuoc.soLuong}</td>
                    <td style="text-align: right;"><fmt:formatNumber value="${thuoc.donGia}" pattern="#,##0 '₫'"/></td>
                    <td style="text-align: right;"><fmt:formatNumber value="${thuoc.thanhTien}" pattern="#,##0 '₫'"/></td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="summary-section">
        <p class="total">
            TỔNG CỘNG: 
            <fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/>
        </p>
    </div>

    <hr>

    <h3>Lịch sử Giao dịch</h3>
    <table>
        <thead>
            <tr>
                <th>Thời gian</th>
                <th>Số tiền</th>
                <th>Phương thức</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty transactions}">
                <tr>
                    <td colspan="3">Chưa có giao dịch nào.</td>
                </tr>
            </c:if>
            <c:forEach var="txn" items="${transactions}">
                <tr>
                    <%-- Giả sử txn.thoiGianGiaoDich là java.sql.Timestamp hoặc java.util.Date --%>
                    <td><c:out value="${txn.thoiGianGiaoDich}"/></td>
                    <td style="text-align: right;"><fmt:formatNumber value="${txn.soTien}" pattern="#,##0 '₫'"/></td>
                    <td>${txn.phuongThuc}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <div class="no-print">
        <hr>
        <button onclick="window.print()">In lại Hóa Đơn</button>
        <button onclick="window.close()">Đóng cửa sổ</button>
    </div>

</body>
</html>