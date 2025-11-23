package com.example.airlinesystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
            "jdbc:postgresql://dpg-d4arp0q4d50c73crk970-a.oregon-postgres.render.com/ariline";

    private static final String USER = "ariline_user";
    private static final String PASSWORD = "FNPSbCh2V1FdinSEGyTH8bYO7Jam3zna";

    private static Connection connection = null;

    // -----------------------------------------------
    // MAIN METHOD USED BY CONTROLLERS: connect()
    // -----------------------------------------------
    public static Connection connect() {
        return getConnection();
    }

    // -----------------------------------------------
    // Returns a reusable connection instance
    // -----------------------------------------------
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {

                // Load PostgreSQL JDBC Driver
                Class.forName("org.postgresql.Driver");

                // Connect to PostgreSQL on Render
                connection = DriverManager.getConnection(URL, USER, PASSWORD);

                System.out.println("Connected to Render PostgreSQL successfully!");
            }

        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver missing. Ensure it is added in your Maven pom.xml.");
        } catch (SQLException e) {
            System.err.println("Failed to connect to PostgreSQL!");
            e.printStackTrace();
        }

        return connection;
    }

    // -----------------------------------------------
    // Close DB connection safely
    // -----------------------------------------------
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("PostgreSQL connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing PostgreSQL connection: " + e.getMessage());
        }
    }
}
