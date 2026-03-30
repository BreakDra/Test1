<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<!doctype html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <title>Đăng nhập</title>
</head>
<body>
  <form method="post" action="${pageContext.request.contextPath}/login">
    <label>Tên đăng nhập</label>
    <input name="username" type="text" required>
    <label>Mật khẩu</label>
    <input name="password" type="password" required>
    <button type="submit">Đăng nhập</button>
  </form>

  <% String err = (String) request.getAttribute("loginError");
     if (err != null) { %>
    <p style="color:#b91c1c"><%= StringEscapeUtils.escapeHtml4(err) %></p>
  <% } %>

  <p>Chưa có tài khoản? <a href="${pageContext.request.contextPath}/register.html">Đăng ký</a></p>
</body>
</html>
