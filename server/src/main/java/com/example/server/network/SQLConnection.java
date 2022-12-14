package com.example.server.network;

import com.example.server.LoggerApp;

import java.sql.*;

public class SQLConnection {

    private static Connection connection;
    private static Statement statement;

    public static void connect() {
        try {
            String url = "jdbc:sqlite:cloud.db";
            connection = DriverManager.getConnection(url);
            statement = connection.createStatement();
            LoggerApp.info("Connection to SQLLite DB has been establishing");
        } catch (SQLException e) {
            LoggerApp.info(e.getMessage());
        }
    }

    public static void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            LoggerApp.info(e.getMessage());
        }
    }

    public static boolean addUser(String user, String password){
        try {
            String sql = String.format("INSERT INTO users VALUES ('%s', '%s')", user, password);
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean authorizeUser(String login, String password){
        try {
            String sql = String.format("SELECT login, password FROM users WHERE login ='%s' and password ='%s'", login, password);
            ResultSet rs = statement.executeQuery(sql);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteUser(String login, String password){
        try {
            String sql = String.format("DELETE FROM users WHERE login ='%s' and password ='%s'", login, password);
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

}
