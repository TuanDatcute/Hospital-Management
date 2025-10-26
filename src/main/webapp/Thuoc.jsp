<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    
    <c:choose>
        <c:when test="${empty MEDICATION_DATA.id}">
            <title>Tạo Thuốc Mới</title>
        </c:when>
        <c:otherwise>
            <title>Chỉnh Sửa Thuốc</title>
        </c:otherwise>
    </c:choose>
    
    <link rel="stylesheet" href="<c:url value='/css/dv-style.css'/>">
</head>
<body>
    <div class="form-container">
        
        <c:choose>
            <c:when test="${empty MEDICATION_DATA.id}">
                <h1>Tạo Thuốc Mới</h1>
            </c:when>
            <c:otherwise>
                <h1>Chỉnh Sửa Thuốc: ${MEDICATION_DATA.tenThuoc}</h1>
            </c:otherwise>
        </c:choose>

        <c:if test="${not empty requestScope.ERROR_MESSAGE}">
            <div class="alert alert-danger"><strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}</div>
        </c:if>

        <form action="MainController" method="POST" class="form-grid">
            
            <c:choose>
                <c:when test="${empty MEDICATION_DATA.id}">
                    <input type="hidden" name="action" value="createMedication">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="updateMedicationInfo">
                    <input type="hidden" name="id" value="${MEDICATION_DATA.id}">
                </c:otherwise>
            </c:choose>

            <div class="form-group">
                <label for="tenThuoc">Tên Thuốc (*)</label>
                <input type="text" id="tenThuoc" name="tenThuoc" class="form-control" 
                       value="${MEDICATION_DATA.tenThuoc}" required>
            </div>

            <div class="form-group">
                <label for="hoatChat">Hoạt Chất</label>
                <input type="text" id="hoatChat" name="hoatChat" class="form-control" 
                       value="${MEDICATION_DATA.hoatChat}">
            </div>

            <div class="form-group">
                <label for="donViTinh">Đơn Vị Tính (*)</label>
                <input type="text" id="donViTinh" name="donViTinh" class="form-control" 
                       value="${MEDICATION_DATA.donViTinh}" required>
            </div>

            <div class="form-group">
                <label for="donGia">Đơn Giá (VND) (*)</label>
                <input type="number" id="donGia" name="donGia" class="form-control" 
                       value="${MEDICATION_DATA.donGia}" step="100" min="0" required>
            </div>
            
            <%-- Trong form cập nhật, trường tồn kho chỉ để xem, không cho sửa --%>
            <c:if test="${not empty MEDICATION_DATA.id}">
                <div class="form-group">
                    <label>Số Lượng Tồn Kho (hiện tại)</label>
                    <input type="number" class="form-control" 
                           value="${MEDICATION_DATA.soLuongTonKho}" readonly>
                </div>
            </c:if>

            <div class="button-group">
                <a href="<c:url value='/DanhMucController?action=listMedications'/>" class="btn btn-secondary">Hủy</a>
                <c:choose>
                    <c:when test="${empty MEDICATION_DATA.id}">
                        <button type="submit" class="btn btn-primary">Tạo Mới</button>
                    </c:when>
                    <c:otherwise>
                        <button type="submit" class="btn btn-primary">Cập Nhật</button>
                    </c:otherwise>
                </c:choose>
            </div>
        </form>
    </div>
</body>
</html>