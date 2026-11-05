package org.Hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    private final Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() throws SQLException {
        String query = "SELECT id, name, specialization FROM doctors";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            System.out.println("+-------------+---------------------+--------------------+");
            System.out.println("| Doctor ID   | Name                | Specialization     |");
            System.out.println("+-------------+---------------------+--------------------+");
            while (resultSet.next()) {
                System.out.printf("| %-11d | %-19s | %-18s |%n",
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getString("specialization"));
            }
            System.out.println("+-------------+---------------------+--------------------+");
        }
    }

    public boolean getDoctorById(int id) throws SQLException {
        String query = "SELECT 1 FROM doctors WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}
