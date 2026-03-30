package com.example.auth;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/social_app?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "An_cat_m0i";

    public static Connection getConn() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
