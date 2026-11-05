package org.Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Patient {
    private final Connection connection;
    private final Scanner scanner;

    public Patient(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void addPatient() throws SQLException {
        System.out.println("Enter patient name:");
        String name = scanner.nextLine().trim();

        System.out.println("Enter patient age:");
        int age = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Enter patient gender:");
        String gender = scanner.nextLine().trim();

        String query = "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, gender);
            if (statement.executeUpdate() > 0) {
                System.out.println("Patient added successfully.");
            } else {
                System.out.println("Failed to add patient.");
            }
        }
    }

    public void viewPatients() throws SQLException {
        String query = "SELECT id, name, age, gender FROM patients";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            System.out.println("+-------------+--------------------+-------+-------------+");
            System.out.println("| Patient ID  | Name               | Age   | Gender      |");
            System.out.println("+-------------+--------------------+-------+-------------+");
            while (resultSet.next()) {
                System.out.printf("| %-11d | %-18s | %-5d | %-11s |%n",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("gender"));
            }
            System.out.println("+-------------+--------------------+-------+-------------+");
        }
    }

    public boolean getPatientById(int id) throws SQLException {
        String query = "SELECT 1 FROM patients WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
