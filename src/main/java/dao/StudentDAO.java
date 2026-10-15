package dao;

import db.DatabaseManager;
import model.Student;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentDAO {
    public static Student getStudentByRegisterNumber(int registerNumber) {
        String sql = "SELECT name, surname FROM students WHERE register_number = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, registerNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                return new Student(registerNumber, name, surname);
            }
        } catch (SQLException e) {
            System.out.println("Errore nella lettura dello studente: " + e.getMessage());
        }

        return null;
    }

    public static boolean addStudent(int registerNumber, String name, String surname) {
        String query = "INSERT INTO students (register_number, name, surname) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, registerNumber);
            pstmt.setString(2, name);
            pstmt.setString(3, surname);

            pstmt.executeUpdate();
            System.out.println("Studente aggiunto con successo!");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Errore nell'inserimento dello studente: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteStudent(int registerNumber) {
        String sql = "DELETE FROM students WHERE register_number = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, registerNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Errore nell'eliminazione dello studente: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateStudent(Student student) {
        String sql = "UPDATE students SET name = ?, surname = ? WHERE register_number = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getSurname());
            pstmt.setInt(3, student.getRegisterNumber());


            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Errore nell'aggiornamento dello studente: " + e.getMessage());
            return false;
        }
    }
}