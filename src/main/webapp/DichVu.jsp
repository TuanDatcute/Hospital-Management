<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <%-- Tiêu đề động tùy theo chế độ --%>
        <c:choose>
            <c:when test="${empty SERVICE_DATA.id}">
                <title>Tạo Dịch Vụ Mới</title>
            </c:when>
            <c:otherwise>
                <title>Chỉnh Sửa Dịch Vụ</title>
            </c:otherwise>
        </c:choose>

        <link rel="stylesheet" href="<c:url value='/css/style.css'/>"> <%-- Link CSS chung --%>
        <link rel="stylesheet" href="<c:url value='/css/dv-style.css'/>">
        <link href="https://fonts.googleapis.com/css2?family=Be+Vietnam+Pro:wght@400;500;600;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="form-container">

            <%-- Tiêu đề và thông báo lỗi/thành công --%>
            <c:choose>
                <c:when test="${empty SERVICE_DATA.id}">
                    <h1>Tạo Dịch Vụ Mới</h1>
                </c:when>
                <c:otherwise>
                    <h1>Chỉnh Sửa Dịch Vụ: ${SERVICE_DATA.tenDichVu}</h1>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="alert alert-danger"><strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}</div>
            </c:if>

            <form action="<c:url value='/MainController'/>" method="POST" class="form-grid">

                <%-- ✨ LOGIC QUAN TRỌNG: Gửi action và ID phù hợp --%>
                <c:choose>
                    <c:when test="${empty SERVICE_DATA.id}">
                        <input type="hidden" name="action" value="createService">
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="action" value="updateService">
                        <input type="hidden" name="id" value="${SERVICE_DATA.id}">
                    </c:otherwise>
                </c:choose>

                <div class="form-group full-width">
                    <label for="tenDichVu">Tên Dịch Vụ (*)</label>
                    <input type="text" id="tenDichVu" name="tenDichVu" class="form-control" 
                           value="${SERVICE_DATA.tenDichVu}" placeholder="Ví dụ: Xét nghiệm máu tổng quát" required>
                </div>

                <div class="form-group">
                    <label for="donGia">Đơn Giá (VND) (*)</label>
                    <input type="number" id="donGia" name="donGia" class="form-control" 
                           value="${SERVICE_DATA.donGia}" placeholder="Ví dụ: 350000" step="1000" min="0" required>
                </div>

                <div class="form-group full-width">
                    <label for="moTa">Mô Tả</label>
                    <textarea id="moTa" name="moTa" class="form-control" rows="4" 
                              placeholder="Mô tả chi tiết về dịch vụ...">${SERVICE_DATA.moTa}</textarea>
                </div>

                <div class="button-group">
                    <a href="<c:url value='/MainController?action=listAndSearchServices'/>" class="btn btn-secondary" style="text-decoration: none">Hủy</a>
                    <c:choose>
                        <c:when test="${empty SERVICE_DATA.id}">
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