package com.example.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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




}
