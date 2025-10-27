<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Dịch Vụ</title>
    
    <link rel="stylesheet" href="<c:url value='/css/dv-style.css'/>">
    
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
</head>
<body>

    <div class="form-container">
        <h1>Tạo Dịch Vụ Mới</h1>

        <%-- Hiển thị thông báo Lỗi (nếu có) --%>
        <c:if test="${not empty requestScope.ERROR_MESSAGE}">
            <div class="alert alert-danger">
                <strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}
            </div>
        </c:if>

        <%-- Hiển thị thông báo Thành công (nếu có) --%>
        <c:if test="${not empty requestScope.SUCCESS_MESSAGE}">
            <div class="alert alert-success">
                <strong>Thành công!</strong> ${requestScope.SUCCESS_MESSAGE}
            </div>
        </c:if>

        <%-- ✅ Dùng c:url để tạo đường dẫn an toàn và đúng đắn --%>
        <form action="<c:url value='/MainController'/>" method="POST" class="form-grid">
            <input type="hidden" name="action" value="createService">

            <div class="form-group full-width">
                <label for="tenDichVu">Tên Dịch Vụ (*)</label>
                <input type="text" id="tenDichVu" name="tenDichVu" class="form-control" 
                       value="${SERVICE_DATA.tenDichVu}" placeholder="Ví dụ: Xét nghiệm máu tổng quát" required>
            </div>

            <div class="form-group">
                <label for="donGia">Đơn Giá (VND) (*)</label>
                <%-- ✨ Thêm min="0" để ngăn người dùng nhập số âm --%>
                <input type="number" id="donGia" name="donGia" class="form-control" 
                       value="${SERVICE_DATA.donGia}" placeholder="Ví dụ: 350000" step="1000" min="0" required>
            </div>
            
            <div class="form-group full-width">
                <label for="moTa">Mô Tả</label>
                <textarea id="moTa" name="moTa" class="form-control" rows="4" 
                          placeholder="Mô tả chi tiết về dịch vụ...">${SERVICE_DATA.moTa}</textarea>
            </div>

            <div class="button-group">
                <button type="reset" class="btn btn-secondary">Làm lại</button>
                <button type="submit" class="btn btn-primary">Lưu Dịch Vụ</button>
            </div>
        </form>
    </div>
</body>
</html>