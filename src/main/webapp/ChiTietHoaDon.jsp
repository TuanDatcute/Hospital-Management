<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
    <head>
        <title>Chi tiết Hóa đơn ${invoice.maHoaDon}</title>
        <%-- THÊM: Liên kết đến tệp CSS chi tiết --%>
        <link rel="stylesheet" href="css/ChiTietHoaDon.css">
        <%-- THÊM: Liên kết đến tệp js chi tiết --%>
        <script src="js/ChiTietHoaDon.js"></script>
        
        <%-- TÙY CHỌN: Bạn cũng có thể liên kết CSS chung ở đây
                 để dùng chung các lớp như .message
        <link rel="stylesheet" href="css/DanhSachHoaDon.css">
        --%>
    </head>
    <body>
        <a href="MainController?action=listInvoices">&lt;&lt; Quay lại Danh sách</a>
        <hr>

        <h2>Chi tiết Hóa đơn: ${invoice.maHoaDon}</h2>

        <%-- Hiển thị thông tin từ DTO "phẳng" --%>
        <p>Bệnh nhân: <strong>${invoice.hoTenBenhNhan}</strong></p>
        <p>Trạng thái: <strong>${invoice.trangThai}</strong></p>

        <%-- ============================================= --%>
        <%--         Chi tiết Dịch vụ                     --%>
        <%-- ============================================= --%>
        <h3>Chi tiết Dịch vụ</h3>
        <%-- SỬA: Đã xóa border="1" và style="width: 70%" --%>
        <table>
            <thead>
                <tr> <th>STT</th> <th>Tên Dịch vụ</th> <th>Đơn giá</th> </tr>
            </thead>
            <tbody>
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
        <%--         Chi tiết Đơn thuốc                   --%>
        <%-- ============================================= --%>
        <h3>Chi tiết Đơn thuốc</h3>
        <%-- SỬA: Đã xóa border="1" và style="width: 70%" --%>
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
                <input type="hidden" name="invoiceId" id="invoiceId" value="${invoice.id}">

                <%-- SỬA: Dùng <p> và <label> cho cấu trúc tốt hơn --%>
                <p>
                    <label for="soTien">Số tiền:</label>
                    <input type="number" name="soTien" id="soTien" value="${invoice.tongTien}" required>
                </p>

                <%-- SỬA: Dùng <p> và <label> cho cấu trúc tốt hơn --%>
                <p>
                    <label for="phuongThuc">Phương thức:</label>
                    <select name="phuongThuc" id="phuongThuc" onchange="toggleQrCode()">
                        <option value="TIEN_MAT">Tiền mặt</option>
                        <option value="QUET_THE">Quẹt thẻ</option>
                        <option value="CHUYEN_KHOAN">Chuyển khoản</option>
                    </select>
                </p>

                <%-- ======================================================= --%>
                <%-- Khối hiển thị QR Code --%>
                <%-- SỬA: Đã xóa toàn bộ style="..." inline --%>
                <%-- ======================================================= --%>
                <div id="qrCodeContainer">
                    <h4>Quét Mã QR để Thanh Toán</h4>
                    <p>Vui lòng chuyển khoản chính xác số tiền: <br>
                        <strong><span id="qrAmount"><fmt:formatNumber value="${invoice.tongTien}" pattern="#,##0 '₫'"/></span></strong>
                    </p>

                    <%-- TODO: Cập nhật thông tin ngân hàng của bạn --%>
                    <p><strong>Ngân hàng:</strong> MB BANK</p>
                    <p><strong>Số tài khoản:</strong> 0384011575</p>
                    <p><strong>Nội dung:</strong> <span id="qrContent">Thanh toan hoa don ${invoice.maHoaDon}</span></p>

                    <%-- TODO: Thay thế bằng đường dẫn ảnh QR của bạn --%>
                    <%-- SỬA: Đã xóa style="..." inline --%>
                    <img src="images/QRCode.jpg" alt="Mã QR chuyển khoản">
                </div>
                <%-- ======================================================= --%>

                <%-- SỬA: Đã xóa style="margin-top: 15px;" --%>
                <button type="submit">Xác nhận Thanh toán</button>
            </form>
        </c:if>
        <c:if test="${invoice.trangThai == 'DA_THANH_TOAN'}">
            <%-- SỬA: Thay thế style="color:green;" bằng class --%>
            <%-- Ghi chú: Class 'message' & 'success' được định nghĩa
                     trong file 'DanhSachHoaDon.css' (trang trước)
                     Bạn cần đảm bảo class này cũng có trong 'ChiTietHoaDon.css'
                     hoặc liên kết cả 2 tệp CSS. --%>
            <p class="message success">Hóa đơn này đã được thanh toán.</p>
        </c:if>

        <hr>

        <h3>Lịch sử Giao dịch</h3>
        <%-- SỬA: Đã xóa border="1" --%>
        <table>
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
        
        <%-- ================== MỚI ================== --%>
        <%-- Thêm nút "In Hóa Đơn" --%>
        <%-- Nó gọi action 'printInvoice' mà chúng ta đã thêm vào Controller --%>
        <%-- target="_blank" sẽ mở trang in trong một tab mới --%>
        <a href="MainController?action=printInvoice&id=${invoice.id}" target="_blank" class="button-print">
             &#128424; In Hóa Đơn
        </a>
        <%-- ================== HẾT MỚI ================== --%>


    </body>
</html>