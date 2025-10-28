<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    </head>
    <body>
        <div class="container">
            <c:choose>
                <c:when test="${empty phieuKham.id}"><h1>Tạo Phiếu Khám Mới</h1></c:when>
                <c:otherwise><h1>Chỉnh Sửa Phiếu Khám: ${phieuKham.maPhieuKham}</h1></c:otherwise>
            </c:choose>
            <c:if test="${not empty requestScope.ERROR_MESSAGE}"><div class="alert alert-danger"><strong>Lỗi!</strong> ${requestScope.ERROR_MESSAGE}</div></c:if>

                <form action="<c:url value='/MainController'/>" method="POST">
                <c:choose>
                    <c:when test="${empty phieuKham.id}"><input type="hidden" name="action" value="createEncounter"></c:when>
                    <c:otherwise><input type="hidden" name="action" value="updateEncounter"><input type="hidden" name="id" value="${phieuKham.id}"></c:otherwise>
                </c:choose>

                <div class="form-grid">
                    <div class="form-group">
                        <label for="maPhieuKham">Mã Phiếu Khám</label>
                        <input type="text" id="maPhieuKham" name="maPhieuKham" class="form-control" value="${phieuKham.maPhieuKham}" 
                               ${not empty phieuKham.id ? 'readonly' : ''} required>
                    </div>

                    <div class="form-group">
                        <label for="thoiGianKham">Thời Gian Khám</label>
                        <input type="datetime-local" id="thoiGianKham" name="thoiGianKham" class="form-control"
                               value="${phieuKham.thoiGianKhamFormatted}" required>
                    </div>

                    <div class="form-group">
                        <label for="benhNhanId">Bệnh Nhân</label>
                        <c:choose>
                            <c:when test="${empty phieuKham.id}">
                                <select id="benhNhanId" name="benhNhanId" class="form-control" required>
                                    <option value="">-- Chọn bệnh nhân --</option>
                                    <c:forEach var="bn" items="${danhSachBenhNhan}">
                                        <option value="${bn.id}" ${phieuKham.benhNhanId == bn.id ? 'selected' : ''}>${bn.hoTen}</option>
                                    </c:forEach>
                                </select>
                            </c:when>
                            <c:otherwise>
                                <input type="text" value="${phieuKham.tenBenhNhan}" readonly class="form-control readonly-input">
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="form-group">
                        <label for="bacSiId">Bác sĩ</label>
                        <select id="bacSiId" name="bacSiId" class="form-control" required>
                            <option value="">-- Chọn bác sĩ --</option>
                            <c:forEach var="bs" items="${danhSachBacSi}">
                                <option value="${bs.id}" ${phieuKham.bacSiId == bs.id ? 'selected' : ''}>${bs.hoTen}</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group full-width">
                        <label for="trieuChung">Triệu Chứng</label>
                        <textarea id="trieuChung" name="trieuChung" class="form-control" rows="3">${phieuKham.trieuChung}</textarea>
                    </div>

                    <div class="form-group"><label for="nhietDo">Nhiệt Độ (°C)</label><input type="number" id="nhietDo" name="nhietDo" class="form-control" value="${phieuKham.nhietDo}" step="0.1"></div>
                    <div class="form-group"><label for="huyetAp">Huyết Áp (mmHg)</label><input type="text" id="huyetAp" name="huyetAp" class="form-control" value="${phieuKham.huyetAp}"></div>
                    <div class="form-group"><label for="nhipTim">Nhịp Tim</label><input type="number" id="nhipTim" name="nhipTim" class="form-control" value="${phieuKham.nhipTim}"></div>
                    <div class="form-group"><label for="nhipTho">Nhịp Thở</label><input type="number" id="nhipTho" name="nhipTho" class="form-control" value="${phieuKham.nhipTho}"></div>

                    <div class="form-group full-width"><label for="chanDoan">Chẩn Đoán</label><textarea id="chanDoan" name="chanDoan" class="form-control" rows="4">${phieuKham.chanDoan}</textarea></div>
                    <div class="form-group full-width"><label for="ketLuan">Kết Luận & Dặn Dò</label><textarea id="ketLuan" name="ketLuan" class="form-control" rows="4">${phieuKham.ketLuan}</textarea></div>

                    <div class="form-group">
                        <label for="ngayTaiKham">Ngày Tái Khám</label>
                        <input type="datetime-local" id="ngayTaiKham" name="ngayTaiKham" class="form-control" value="${phieuKham.ngayTaiKhamFormatted}">
                    </div>

                    <div class="form-group">
                        <label for="lichHenId">Lịch Hẹn liên quan (nếu có)</label>
                        <select id="lichHenId" name="lichHenId" class="form-control">
                            <option value="">-- Không có --</option>
                            <c:forEach var="lichHen" items="${danhSachLichHen}">
                                <option value="${lichHen.id}" ${phieuKham.lichHenId == lichHen.id ? 'selected' : ''}>
                                    STT ${lichHen.stt} - ${lichHen.lyDoKham}
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="button-group">
                        <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="btn btn-secondary">Hủy</a>
                        <c:choose>
                            <c:when test="${empty phieuKham.id}"><button type="submit" class="btn btn-primary">Tạo Mới</button></c:when>
                            <c:otherwise><button type="submit" class="btn btn-primary">Cập Nhật</button></c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </form>
        </div>
    </body>
</html>