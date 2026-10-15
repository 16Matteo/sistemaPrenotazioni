package dao;

import model.Subject;
import model.TimeSlot;
import db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {
    public static boolean addSubject(Subject subject) {
        String insertSubjectSql = "INSERT INTO subjects(name) VALUES(?)";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmtSubject = conn.prepareStatement(insertSubjectSql)) {

            pstmtSubject.setString(1, subject.getName());
            int n = pstmtSubject.executeUpdate();
            if(n==1){
                return true;
            } else{
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Errore durante l'inserimento della materia: " + e.getMessage());
            return false;
        }
    }

    public static void deleteSubject(Subject subject) {
        String subject_name = subject.getName();
        String deleteSubjectSql = "DELETE FROM subjects WHERE name = ?";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmtSubject = conn.prepareStatement(deleteSubjectSql)){

            pstmtSubject.setString(1, subject_name);
            pstmtSubject.executeUpdate();

        } catch(SQLException e) {
            System.out.println("Errore durante l'eliminazione della materia: " + e.getMessage());
        }
    }

    public static List<String> getAllSubjects(){
        String query = "SELECT name FROM subjects";
        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmtSubject = conn.prepareStatement(query)){
            ResultSet rs = pstmtSubject.executeQuery();
            List<String> subjects = new ArrayList<>();
            while(rs.next()){
                subjects.add(rs.getString(1));
            }
            if(subjects.isEmpty()){
                System.out.println("Attenzione nessuna materia presente nel database! ");
            }
            return subjects;
        } catch(SQLException e){
            System.out.println("Errore nel recupero delle materie nel database." + e.getMessage());
            return null;
        }
    }

    public static Subject getSubjectAndTimeslotsBySubjectName(String subjectName) {
        String subjectQuery = "SELECT name FROM subjects WHERE name = ?";
        Subject subject = null;
        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(subjectQuery)){

            pstmt.setString(1, subjectName);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                String timeslotsQuery = "SELECT start_time, end_time FROM bookings WHERE subject_name = ?";
                PreparedStatement timeslotsStmt = conn.prepareStatement(timeslotsQuery);

                timeslotsStmt.setString(1, subjectName);
                ResultSet rsTimeslots = timeslotsStmt.executeQuery();

                List<TimeSlot> timeslots = new ArrayList<>();
                while(rsTimeslots.next()){
                    TimeSlot timeSlot = new TimeSlot(rsTimeslots.getString("start_time"), rsTimeslots.getString("end_time"));
                    timeslots.add(timeSlot);
                }
                subject = new Subject(rs.getString("name"), timeslots);
            }
        } catch(SQLException e){
            System.out.println("Materia non trovata." + e.getMessage());
            return null;
        }
        return subject;
    }

}

