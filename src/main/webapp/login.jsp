<%--
    Document   : login.jsp (Giao diện gộp mới + Social Login)
    Created on : Oct 29, 2025
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- **THÊM MỚI (CLEAN CODE):** Tạo biến URL động --%>
<c:url var="mainControllerUrl" value="/MainController" />


<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đăng nhập / Đăng ký</title>

        <%-- 1. Nhúng CSS/Font chung (style.css) --%>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body class="login-page-body"> <%-- Sử dụng class body mới từ CSS --%>

        <%-- 
            Kiểm tra xem có lỗi đăng ký không (từ UserController)
            (Logic này rất tốt, giữ nguyên)
        --%>
        <c:set var="containerClass" value="" />
        <c:if test="${requestScope.formAction == 'register'}">
            <c:set var="containerClass" value="active" />
        </c:if>
        

        <div class="login-container ${containerClass}" id="container">

            <%-- Nút quay về Home --%>
            <a href="${pageContext.request.contextPath}/index.jsp" class="home-link" title="Quay về Trang chủ">
                <i class="fas fa-home"></i>
            </a>

            <%-- ======================= --%>
            <%-- FORM ĐĂNG KÝ (SIGN UP) --%>
            <%-- ======================= --%>
            <div class="form-container sign-up">
                <%-- **CẬP NHẬT (CLEAN CODE):** Dùng biến URL --%>
                <form action="${mainControllerUrl}" method="post">
                    <input type="hidden" name="action" value="register" />
                    <h1>Tạo tài khoản</h1>

                    <%-- **THÊM MỚI: ICON SOCIAL** --%>
                    <div class="social-icons">
                        <a href="#" class="icon"><i class="fa-brands fa-google-plus-g"></i></a>
                        <a href="#" class="icon"><i class="fa-brands fa-facebook-f"></i></a>
                    </div>
                    <span>hoặc dùng email của bạn để đăng ký</span>

                    <%-- Hiển thị lỗi nếu action là 'register' --%>
                    <c:if test="${requestScope.formAction == 'register'}">
                        <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                            <div class="error-message">${requestScope.ERROR_MESSAGE}</div>
                        </c:if>
                    </c:if>

                    <input type="text" placeholder="Tên đăng nhập" name="username" required="required" value="<c:out value="${requestScope.username_register}"/>"/>
                    <input type="email" placeholder="Email" name="email" required="required" value="<c:out value="${requestScope.email_register}"/>"/>
                    <input type="password" placeholder="Mật khẩu" name="password" required="required"/>
                    <input type="password" placeholder="Xác nhận Mật khẩu" name="confirmPassword" required="required"/>

                    <input type="submit" value="Đăng Ký" />
                </form>
            </div>

            <%-- ======================== --%>
            <%-- FORM ĐĂNG NHẬP (SIGN IN) --%>
            <%-- ======================== --%>
            <div class="form-container sign-in">
                <%-- **CẬP NHẬT (CLEAN CODE):** Dùng biến URL --%>
                <form action="${mainControllerUrl}" method="post">
                    <input type="hidden" name="action" value="login" />
                    <h1>Đăng nhập</h1>

                    <%-- **THÊM MỚI: ICON SOCIAL** --%>
                    <div class="social-icons">
                        <a href="#" class="icon"><i class="fa-brands fa-google-plus-g"></i></a>
                    </div>
                    <span>hoặc dùng Tên đăng nhập của bạn</span>

                    <%-- Hiển thị lỗi nếu action là 'login' (hoặc không có action) --%>
                    <c:if test="${requestScope.formAction == 'login' || empty requestScope.formAction}">
                        <c:if test="${not empty requestScope.ERROR_MESSAGE}">
                            <div class="error-message">${requestScope.ERROR_MESSAGE}</div>
                        </c:if>
                    </c:if>
                    <%-- Hiển thị thông báo đăng ký thành công (hoặc reset pass thành công) --%>
                    <c:if test="${not empty sessionScope.SUCCESS_MESSAGE}">
                        <p class="success-message">
                            ${sessionScope.SUCCESS_MESSAGE}
                        </p>
                        <c:remove var="SUCCESS_MESSAGE" scope="session" />
                    </c:if>

                    <input type="text" name="username" placeholder="Tên đăng nhập" required="required"/>
                    <input type="password" name="password" placeholder="Mật khẩu" required="required"/>
                    
                    <%-- --- **BẮT ĐẦU CẬP NHẬT (Quên Mật khẩu)** --- --%>
                    <a href="forgotPassword.jsp">Quên mật khẩu?</a>
                    <%-- --- **KẾT THÚC CẬP NHẬT** --- --%>
                    
                    <input type="submit" value="Đăng Nhập" />
                </form>
            </div>

            <%-- ======================== --%>
            <%-- PHẦN OVERLAY CHUYỂN ĐỔI --%>
            <%-- ======================== --%>
            <div class="toggle-container">
                <div class="toggle">
                    <div class="toggle-panel toggle-left">
                        <h1>Chào mừng trở lại!</h1>
                        <p>Nếu đã có tài khoản, hãy đăng nhập để tiếp tục nhé</p>
                        <button class="hidden" id="login">Đăng Nhập</button>
                    </div>
                    <div class="toggle-panel toggle-right">
                        <h1>Chào bạn!</h1>
                        <p>Hãy đăng ký tài khoản để có thể sử dụng đầy đủ các tính năng</p>
                        <button class="hidden" id="register">Đăng Ký</button>
                    </div>
                </div>
            </div>
        </div>

        <%-- SCRIPT ĐIỀU KHIỂN ANIMATION (Giữ nguyên) --%>
        <script>
            const container = document.getElementById('container');
            const registerBtn = document.getElementById('register');
            const loginBtn = document.getElementById('login');

            registerBtn.addEventListener('click', () => {
                container.classList.add("active");
            });

            loginBtn.addEventListener('click', () => {
                container.classList.remove("active");
            });

            // Tự động mở panel Đăng ký nếu có lỗi đăng ký
            <c:if test="${requestScope.formAction == 'register'}">
            container.classList.add("active");
            </c:if>

            // Tự động mở panel Đăng ký nếu URL có #register
//            if (window.location.hash === '#register') {
//                container.classList.add("active");
//            }
        </script>
    </body>
</html>