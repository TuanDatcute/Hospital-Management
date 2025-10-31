<%--
    Document   : formLichHen.jsp
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Tạo Lịch hẹn Mới</title>

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">

        <%-- Trang này sử dụng các class: .data-form, .form-group, .btn-submit, .btn-cancel --%>
    </head>
    <body>

        <jsp:include page="/WEB-INF/header.jsp" /> 

        <div class="container page-content" style="padding-top: 30px;">

            <h2 class="section-title">Tạo Lịch hẹn Mới</h2>

            <%-- Hiển thị lỗi (ví dụ: validation thất bại) --%>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <p class="error-message">${requestScope.ERROR_MESSAGE}</p>
            </c:if>
            <c:if test="${not empty requestScope.LOAD_FORM_ERROR}">
                <p class="error-message">${requestScope.LOAD_FORM_ERROR}</p>
            </c:if>

            <%-- 
                Form này gửi đến MainController.
                Action sẽ là 'createLichHen' (được set cứng)
            --%>
            <form action="MainController" method="post" class="data-form">

                <input type="hidden" name="action" value="createLichHen" />

                <div class="form-group">
                    <label for="benhNhanId">Chọn Bệnh nhân:</label>
                    <select id="benhNhanId" name="benhNhanId" required="required">
                        <option value="">-- Chọn một bệnh nhân --</option>
                        <%-- Lặp qua danh sách bệnh nhân (do LichHenController tải) --%>
                        <c:forEach var="bn" items="${requestScope.LIST_BENHNHAN}">
                            <option value="${bn.id}" ${requestScope.LICHHEN_DATA.benhNhanId == bn.id ? 'selected' : ''}>
                            <c:out value="${bn.hoTen}" /> (Mã: ${bn.maBenhNhan})
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${empty requestScope.LIST_BENHNHAN}">
                        <p style="color: red;">Không tìm thấy bệnh nhân nào đang hoạt động.</p>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="bacSiId">Chọn Bác sĩ:</label>
                    <select id="bacSiId" name="bacSiId" required="required">
                        <option value="">-- Chọn một bác sĩ --</option>
                        <%-- Lặp qua danh sách bác sĩ (do LichHenController tải) --%>
                        <c:forEach var="bs" items="${requestScope.LIST_BACSI}">
                            <option value="${bs.id}" ${requestScope.LICHHEN_DATA.bacSiId == bs.id ? 'selected' : ''}>
                            <c:out value="${bs.hoTen}" /> (Chuyên môn: ${bs.chuyenMon})
                            </option>
                        </c:forEach>
                    </select>
                    <c:if test="${empty requestScope.LIST_BACSI}">
                        <p style="color: red;">Không tìm thấy bác sĩ nào đang hoạt động.</p>
                    </c:if>
                </div>

                <div class="form-group">
                    <label for="thoiGianHen">Thời gian hẹn:</label>
                    <%-- 
                        Dùng datetime-local để khớp với code xử lý trong LichHenController
                        (Controller sẽ tự thêm múi giờ +07:00)
                    --%>
                    <input type="datetime-local" id="thoiGianHen" name="thoiGianHen" 
                           value="${requestScope.LICHHEN_DATA.thoiGianHen}" required="required">
                </div>

                <div class="form-group">
                    <label for="lyDoKham">Lý do khám:</label>
                    <textarea id="lyDoKham" name="lyDoKham" rows="3"><c:out value="${requestScope.LICHHEN_DATA.lyDoKham}"/></textarea>
                </div>

                <div class="form-group">
                    <label for="ghiChu">Ghi chú (nếu có):</label>
                    <textarea id="ghiChu" name="ghiChu" rows="2"><c:out value="${requestScope.LICHHEN_DATA.ghiChu}"/></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn-submit">
                        <i class="fas fa-save"></i> Tạo Lịch hẹn
                    </button>
                    <a href="MainController?action=listLichHen" class="btn-cancel">Hủy</a>
                </div>
            </form>

        </div>

        <jsp:include page="/WEB-INF/footer.jsp" /> 

    </body>
</html>