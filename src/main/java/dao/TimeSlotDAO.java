package dao;

import db.DatabaseManager;
import model.TimeSlot;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class TimeSlotDAO {

    public static boolean createTimeSlot(TimeSlot timeSlot) {
        String sql = "INSERT INTO timeslots (start_time, end_time) VALUES (?, ?)";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, timeSlot.getStartTime());
            pstmt.setString(2, timeSlot.getEndTime());

            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e){
            System.out.println("Errore inserimento TimeSlot: " + e.getMessage());
            return false;
        }
    }

    public static TimeSlot getTimeSlotByStartAndEndTime(String start_time, String end_time) {
        String sql = "SELECT * FROM timeslots WHERE start_time = ? AND end_time = ?";

        try(Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, start_time);
            pstmt.setString(2, end_time);
            ResultSet rs = pstmt.executeQuery();

            if(rs.next()){
                String start = rs.getString("start_time");
                String end = rs.getString("end_time");
                return new TimeSlot(start, end);
            }
        } catch (SQLException e) {
            System.out.println("Errore lettura TimeSlot: " + e.getMessage());
        }
        return null;
    }

    public static boolean deleteTimeSlot(String start_time, String end_time) {
        String sql = "DELETE FROM timeslots WHERE start_time = ? AND end_time = ?";

        try(Connection conn = DatabaseManager.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, start_time);
            pstmt.setString(2, end_time);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Errore eliminazione TimeSlot: " + e.getMessage());
            return false;
        }
    }

    public static List<TimeSlot> getAllTimeSlots() {
        List<TimeSlot> timeSlots = new ArrayList<>();
        String sql = "SELECT start_time, end_time FROM timeslots";

        try (Connection conn = DatabaseManager.connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");

                TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                timeSlots.add(timeSlot);
            }

        } catch (SQLException e) {
            System.out.println("Errore nel recupero dei TimeSlot: " + e.getMessage());
        }

        return timeSlots;
    }
}
