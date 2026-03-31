<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%
  String user = (String) session.getAttribute("user");
  if (user == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
%>
<!doctype html>
<html>
<head><meta charset="utf-8"><title>Thành công</title></head>
<body>
  <h1>Chào <%= StringEscapeUtils.escapeHtml4(user) %></h1>
  <form method="post" action="<%= request.getContextPath() %>/logout">
    <button type="submit">Đăng xuất</button>
  </form>
</body>
</html>
