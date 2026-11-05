package org.Hospital;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class HospitalMangementSystem {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/hospital";

    public static void main(String[] args) {
        String url = System.getenv().getOrDefault("HOSPITAL_DB_URL", DEFAULT_URL);
        String username = System.getenv().getOrDefault("HOSPITAL_DB_USER", "root");
        String password = System.getenv().getOrDefault("HOSPITAL_DB_PASSWORD", "abcde");

        try (Scanner scanner = new Scanner(System.in);
             Connection connection = DriverManager.getConnection(url, username, password)) {
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);

            while (true) {
                System.out.println("\nHospital Management System");
                System.out.println("1. Add patient");
                System.out.println("2. View patients");
                System.out.println("3. View doctors");
                System.out.println("4. Book appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                String input = scanner.nextLine().trim();
                int choice;
                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Enter a number from 1 to 5.");
                    continue;
                }

                try {
                    switch (choice) {
                        case 1:
                            patient.addPatient();
                            break;
                        case 2:
                            patient.viewPatients();
                            break;
                        case 3:
                            doctor.viewDoctors();
                            break;
                        case 4:
                            bookAppointment(patient, doctor, connection, scanner);
                            break;
                        case 5:
                            return;
                        default:
                            System.out.println("Enter a valid choice from 1 to 5.");
                    }
                } catch (NumberFormatException | DateTimeParseException e) {
                    System.out.println("Invalid age or date. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Could not connect to or query the hospital database: " + e.getMessage());
        }
    }

    public static void bookAppointment(
            Patient patient, Doctor doctor, Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter patient ID: ");
        int patientId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter doctor ID: ");
        int doctorId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        LocalDate appointmentDate = LocalDate.parse(scanner.nextLine().trim());

        if (!patient.getPatientById(patientId) || !doctor.getDoctorById(doctorId)) {
            System.out.println("The patient or doctor does not exist.");
            return;
        }

        if (!isDoctorAvailable(doctorId, appointmentDate, connection)) {
            System.out.println("The doctor is unavailable on that date.");
            return;
        }

        String query = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, patientId);
            statement.setInt(2, doctorId);
            statement.setDate(3, Date.valueOf(appointmentDate));
            if (statement.executeUpdate() > 0) {
                System.out.println("Appointment booked successfully.");
            } else {
                System.out.println("Failed to book the appointment.");
            }
        }
    }

    private static boolean isDoctorAvailable(
            int doctorId, LocalDate appointmentDate, Connection connection) throws SQLException {
        String query = "SELECT 1 FROM appointments WHERE doctor_id = ? AND appointment_date = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, doctorId);
            statement.setDate(2, Date.valueOf(appointmentDate));
            try (ResultSet resultSet = statement.executeQuery()) {
                return !resultSet.next();
            }
        }
    }
}
