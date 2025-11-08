<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="sidebar-nav" id="sidebar-nav">
    <div class="sidebar-header">
        <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="sidebar-logo">
            <i class="fas fa-hospital-user"></i>
            <span>Hospital MIS</span>
        </a>
        <button id="sidebar-toggle-btn" class="sidebar-toggle-btn" title="Thu gọn/Mở rộng">
            <i class="fas fa-bars"></i>
        </button>
    </div>

    <ul class="sidebar-menu">
        <li>
            <a href="<c:url value='/MainController?action=listAllEncounters'/>" class="nav-link" data-link="listAllEncounters">
                <i class="fas fa-notes-medical"></i>
                <span class="nav-text">DS. Phiếu Khám</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=showCreateEncounterForm'/>" class="nav-link" data-link="showCreateEncounterForm">
                <i class="fas fa-plus-circle"></i>
                <span class="nav-text">Tạo Phiếu Khám</span>
            </a>
        </li>
        
        <li class="menu-divider"><span class="nav-text">Quản lý</span></li>
        
        <li>
            <a href="<c:url value='/MainController?action=listAppointments'/>" class="nav-link" data-link="listAppointments">
                <i class="fas fa-calendar-alt"></i>
                <span class="nav-text">Lịch Hẹn</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=listMedications'/>" class="nav-link" data-link="listMedications">
                <i class="fas fa-pills"></i>
                <span class="nav-text">Thuốc</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=listAndSearchServices'/>" class="nav-link" data-link="listAndSearchServices">
                <i class="fas fa-vials"></i>
                <span class="nav-text">Dịch Vụ</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=listPatients'/>" class="nav-link" data-link="listPatients">
                <i class="fas fa-user-injured"></i>
                <span class="nav-text">Bệnh Nhân</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=listInvoices'/>" class="nav-link" data-link="listInvoices">
                <i class="fas fa-user-injured"></i>
                <span class="nav-text">Hóa đơn</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=listRooms'/>" class="nav-link" data-link="listRooms">
                <i class="fas fa-user-injured"></i>
                <span class="nav-text">Phòng bệnh</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=listBeds'/>" class="nav-link" data-link="listBeds">
                <i class="fas fa-user-injured"></i>
                <span class="nav-text">Giường bệnh</span>
            </a>
        </li>
        <li>
            <a href="<c:url value='/MainController?action=viewMyNotifications'/>" class="nav-link" data-link="viewMyNotifications">
                <i class="fas fa-user-injured"></i>
                <span class="nav-text">Thông báo</span>
            </a>
        </li>
    </ul>
</nav>