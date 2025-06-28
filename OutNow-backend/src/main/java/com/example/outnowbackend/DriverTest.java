package com.example.outnowbackend;

public class DriverTest {
    public static void main(String[] args) {
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("✅ PostgreSQL Driver is present and working!");
        } catch (Exception e) {
            System.err.println("❌ DRIVER NOT FOUND: " + e.getMessage());
        }
    }
}
