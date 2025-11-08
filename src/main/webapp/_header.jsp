<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header class="main-header">
    <div class="header-left">
        <button id="sidebar-toggle-btn" class="sidebar-toggle-btn" title="Thu gọn/Mở rộng">
            <i class="fas fa-bars"></i>
        </button>
        
        <h1 class="page-title">${param.pageTitle}</h1>
    </div>
    
    <div class="header-right">
        <div class="theme-switch-wrapper">
            <label class="theme-switch" for="theme-toggle">
                <input type="checkbox" id="theme-toggle" />
                <div class="slider round">
                    <span class="sun-icon"><i class="fas fa-sun"></i></span>
                    <span class="moon-icon"><i class="fas fa-moon"></i></span>
                </div>
            </label>
        </div>
        
        <c:if test="${not empty LOGIN_USER_INFO}">
            <div class="user-profile">
                <i class="fas fa-user-circle"></i>
                <span>${LOGIN_USER_INFO.hoTen}</span>
                <a href="<c:url value='/MainController?action=logout'/>" class="logout-btn" title="Đăng xuất">
                    <i class="fas fa-sign-out-alt"></i>
                </a>
            </div>
        </c:if>
    </div>
</header>