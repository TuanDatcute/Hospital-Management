<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html>
    <head>
        <title>Danh sách Hóa đơn</title>
    </head>
    <body>
        <h2>Danh sách Hóa đơn</h2>
        <%-- THÊM: Form Tìm kiếm (từ yêu cầu trước) --%>
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
        <table border="1">
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
    </body>
</html>