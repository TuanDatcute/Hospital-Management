<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> 
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <c:choose>
            <c:when test="${empty phieuKham.id}"><title>Tạo Phiếu Khám Mới</title></c:when>
            <c:otherwise><title>Chỉnh Sửa Phiếu Khám</title></c:otherwise>
        </c:choose>

        <link rel="stylesheet" href="<c:url value='/css/pkb-style.css'/>">

        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
        <script>
            (function () {
                // Key này (theme-preference) phải khớp với key trong theme.js
                var themeKey = 'theme-preference';
                var theme = localStorage.getItem(themeKey);

                if (theme === 'dark') {
                    // ✨ SỬA 3: Không đổi màu nền, mà thêm class vào <html>
                    document.documentElement.classList.add('dark-mode');
                }
            })();
        </script>

    </head>
    <body>
        <c:if test="${not empty LOGIN_USER_INFO and not empty LOGIN_ACCOUNT}">
            <div class="user-info-bar">
                <div class="user-details">
                    <i class="fas fa-user-circle"></i>
                    <div class="user-text">
                        <strong>${LOGIN_USER_INFO.hoTen}</strong>
                        <span>${fn:replace(LOGIN_ACCOUNT.vaiTro, '_', ' ')}</span>
                    </div>
                </div>
                <%-- Nút đăng xuất có thể thêm ở đây --%>
            </div>
        </c:if>

        <div class="container">
            <div class="theme-switch-wrapper">
                <label class="theme-switch" for="theme-toggle">
                    <input type="checkbox" id="theme-toggle" />
                    <div class="slider round">
                        <span class="sun-icon"><i class="fas fa-sun"></i></span>
                        <span class="moon-icon"><i class="fas fa-moon"></i></span>
                    </div>
                </label>
            </div>

            <c:choose>
                <c:when test="${empty phieuKham.id}"><h1>Tạo Phiếu Khám Mới</h1></c:when>
                <c:otherwise><h1>Chỉnh Sửa Phiếu Khám: ${phieuKham.maPhieuKham}</h1></c:otherwise>
            </c:choose>

            <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                <div class="alert alert-danger"><strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}</div>
            </c:if>

            <form action="<c:url value='/MainController'/>" method="POST">
                <c:choose>
                    <c:when test="${empty phieuKham.id}"><input type="hidden" name="action" value="createEncounter"></c:when>
                    <c:otherwise>
                        <input type="hidden" name="action" value="updateEncounter">
                        <input type="hidden" name="id" value="${phieuKham.id}">
                    </c:otherwise>
                </c:choose>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="maPhieuKham">Mã Phiếu Khám</label>
                        <input type="text" id="maPhieuKham" name="maPhieuKham" class="form-control readonly-input" 
                               value="${empty phieuKham.maPhieuKham ? 'Tự động tạo' : phieuKham.maPhieuKham}" readonly>
                    </div>

                    <div class="form-group">
                        <label for="lichHenId">Lịch Hẹn (nếu có)</label>
                        <select id="lichHenId" name="lichHenId" class="form-control">
                            <option value="">-- Chọn từ lịch hẹn chờ --</option>
                            <c:forEach var="lichHen" items="${danhSachLichHen}">
                                <option value="${lichHen.id}" 
                                        ${phieuKham.lichHenId == lichHen.id ? 'selected' : ''}
                                        data-patient-id="${lichHen.benhNhanId}">
                                    STT ${lichHen.stt} - ${lichHen.tenBenhNhan} (${lichHen.lyDoKham})
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <jsp:useBean id="now" class="java.util.Date" />
                    <c:choose>
                        <c:when test="${not empty phieuKham.thoiGianKhamFormatted}">
                            <c:set var="thoiGianKhamValue" value="${phieuKham.thoiGianKhamFormatted}" />
                        </c:when>
                        <c:otherwise>
                            <fmt:formatDate value="${now}" pattern="yyyy-MM-dd'T'HH:mm" var="thoiGianKhamValue" />
                        </c:otherwise>
                    </c:choose>

                    <div class="form-group">
                        <label for="thoiGianKham">Thời Gian Khám</label>
                        <input type="datetime-local" id="thoiGianKham" name="thoiGianKham" class="form-control"
                               value="${thoiGianKhamValue}" required>
                    </div>


                    <div class="form-group">
                        <label for="benhNhanId_select">Bệnh Nhân</label>
                        <c:choose>
                            <c:when test="${empty phieuKham.id}">                              
                                <select id="benhNhanId_select" name="benhNhanId_select" class="form-control" required>
                                    <option value="">-- Chọn bệnh nhân --</option>
                                    <c:forEach var="bn" items="${danhSachBenhNhan}">
                                        <option value="${bn.id}" ${phieuKham.benhNhanId == bn.id ? 'selected' : ''}>${bn.hoTen}</option>
                                    </c:forEach>
                                </select>
                                <input type="hidden" id="benhNhanId_hidden" name="benhNhanId" value="${phieuKham.benhNhanId}">
                            </c:when>
                            <c:otherwise>
                                <input type="text" value="${phieuKham.tenBenhNhan}" readonly class="form-control readonly-input">
                                <input type="hidden" name="benhNhanId" value="${phieuKham.benhNhanId}">
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="form-group">
                        <label for="bacSiId">Bác sĩ</label>
                        <c:choose>
                            <c:when test="${not empty LOGIN_ACCOUNT and LOGIN_ACCOUNT.vaiTro == 'BAC_SI'}">
                                <input type="text" value="${LOGIN_USER_INFO.hoTen}" class="form-control readonly-input" readonly>
                                <input type="hidden" name="bacSiId" value="${LOGIN_USER_INFO.id}">
                            </c:when>
                            <c:otherwise>
                                <select id="bacSiId" name="bacSiId" class="form-control" required>
                                    <option value="">-- Chọn bác sĩ --</option>
                                    <c:forEach var="bs" items="${danhSachBacSi}">
                                        <option value="${bs.id}" ${phieuKham.bacSiId == bs.id ? 'selected' : ''}>
                                            ${bs.hoTen} (${bs.chuyenMon})
                                        </option>
                                    </c:forEach>
                                </select>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="form-group">
                        <label for="ngayTaiKham">Ngày Tái Khám</label>
                        <input type="datetime-local" id="ngayTaiKham" name="ngayTaiKham" class="form-control" value="${phieuKham.ngayTaiKhamFormatted}">
                    </div>

                    <div class="form-group full-width">
                        <label for="trieuChung">Triệu Chứng</label>
                        <textarea id="trieuChung" name="trieuChung" class="form-control" rows="3" required>${phieuKham.trieuChung}</textarea>
                    </div>

                    <div class="form-group"><label for="nhietDo">Nhiệt Độ (°C)</label>
                        <input type="number" id="nhietDo" name="nhietDo" class="form-control" value="${phieuKham.nhietDo}" step="0.1" min="34" max="43" placeholder="37.0">
                    </div>
                    <div class="form-group"><label for="huyetAp">Huyết Áp (mmHg)</label>
                        <input type="text" id="huyetAp" name="huyetAp" class="form-control" value="${phieuKham.huyetAp}" placeholder="120/80" pattern="\d{2,3}/\d{2,3}" title="Nhập huyết áp dạng 120/80">
                    </div>
                    <div class="form-group"><label for="nhipTim">Nhịp Tim (lần/phút)</label>
                        <input type="number" id="nhipTim" name="nhipTim" class="form-control" value="${phieuKham.nhipTim}" min="1" max="300" placeholder="70">
                    </div>
                    <div class="form-group"><label for="nhipTho">Nhịp Thở (lần/phút)</label>
                        <input type="number" id="nhipTho" name="nhipTho" class="form-control" value="${phieuKham.nhipTho}" min="1" max="60" placeholder="20">
                    </div>

                    <div class="form-group full-width"><label for="chanDoan">Chẩn Đoán</label>
                        <textarea id="chanDoan" name="chanDoan" class="form-control" rows="4" required>${phieuKham.chanDoan}</textarea>
                    </div>
                    <div class="form-group full-width"><label for="ketLuan">Kết Luận & Dặn Dò</label>
                        <textarea id="ketLuan" name="ketLuan" class="form-control" rows="4" required>${phieuKham.ketLuan}</textarea>
                    </div>
                </div>

                <div class="button-group">
                    <c:choose>
                        <c:when test="${empty phieuKham.id}">
                            <a href="javascript:history.back()" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </a>
                        </c:when>
                        <c:otherwise>
                            <a href="<c:url value='MainController?action=viewEncounterDetails&id=${phieuKham.id}'/>" class="btn btn-secondary">Quay Lại</a>
                        </c:otherwise>
                    </c:choose>

                    <c:choose>
                        <c:when test="${empty phieuKham.id}"><button type="submit" class="btn btn-primary">Tạo Mới</button></c:when>
                        <c:otherwise><button type="submit" class="btn btn-primary">Cập Nhật</button></c:otherwise>
                    </c:choose>
                </div>
            </form>
        </div>

        <script src="<c:url value='/js/darkmode.js'/>"></script>
        <script src="<c:url value='/js/pkb-form.js'/>"></script>
    </body>
</html>