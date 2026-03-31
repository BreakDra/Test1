@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String ip = req.getRemoteAddr();

        if (username == null || username.trim().isEmpty() || password == null || password.isEmpty()) {
            req.setAttribute("loginError", "Vui lòng nhập tên đăng nhập và mật khẩu.");
            req.getRequestDispatcher("index.jsp").forward(req, resp);
            return;
        }

        boolean success = false;
        try (Connection conn = DBUtil.getConn();
             PreparedStatement ps = conn.prepareStatement("SELECT password_hash FROM users WHERE username = ?")) {

            ps.setString(1, username.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    if (storedHash != null && BCrypt.checkpw(password, storedHash)) {
                        success = true;
                    }
                }
            }

            String attemptHash = BCrypt.hashpw(password, BCrypt.gensalt(10));
            try (PreparedStatement logPs = conn.prepareStatement(
                    "INSERT INTO login_logs (username, attempted_password_hash, success, ip_address) VALUES (?, ?, ?, ?)")) {
                logPs.setString(1, username.trim());
                logPs.setString(2, attemptHash);
                logPs.setInt(3, success ? 1 : 0);
                logPs.setString(4, ip);
                logPs.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException(e);
        }

        if (success) {
            HttpSession session = req.getSession(true);
            session.setAttribute("user", username.trim());
            resp.sendRedirect(req.getContextPath() + "/success.jsp");
        } else {
            req.setAttribute("loginError", "Tên đăng nhập hoặc mật khẩu không đúng");
            req.getRequestDispatcher("index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "/index.jsp");
    }
}
