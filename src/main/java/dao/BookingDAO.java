package dao;

import controller.BookingManager;
import db.DatabaseManager;
import model.Booking;
import model.Student;
import model.Subject;
import model.TimeSlot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {

    public static boolean addBooking(Booking booking) {
        Student student = booking.getStudent();
        Subject subject = booking.getSubject();
        TimeSlot timeSlot = booking.getTimeSlot();

        String sql = "INSERT INTO bookings (student_register_number, subject_name, start_time, end_time) VALUES (?,?,?,?)";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, student.getRegisterNumber());
            pstmt.setString(2, subject.getName());
            pstmt.setString(3, timeSlot.getStartTime());
            pstmt.setString(4, timeSlot.getEndTime());
            int res = pstmt.executeUpdate();
            if(res == 1){
                return true;
            } else{
                return false;
            }

        } catch(SQLException e){
            System.out.println("Errore durante l'inserimento della materia: " + e.getMessage());
            return false;
        }
    }

    public static boolean addEmptyBooking(Booking booking) {
        Subject subject = booking.getSubject();
        TimeSlot timeSlot = booking.getTimeSlot();

        String sql = "INSERT INTO bookings (student_register_number, subject_name, start_time, end_time) VALUES (null,?,?,?)";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, subject.getName());
            pstmt.setString(2, timeSlot.getStartTime());
            pstmt.setString(3, timeSlot.getEndTime());
            int res = pstmt.executeUpdate();
            if(res == 1){
                return true;
            } else{
                return false;
            }

        } catch(SQLException e){
            System.out.println("Errore durante l'inserimento della materia: " + e.getMessage());
            return false;
        }
    }

    public static boolean reserveBooking(Booking booking) {
        String sql = "UPDATE bookings SET student_register_number = ? WHERE subject_name = ? AND start_time = ? AND end_time = ? ";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, booking.getStudent().getRegisterNumber());
            pstmt.setString(2, booking.getSubject().getName());
            pstmt.setString(3, booking.getTimeSlot().getStartTime());
            pstmt.setString(4, booking.getTimeSlot().getEndTime());

            int rowsUpdated = pstmt.executeUpdate();
            if(rowsUpdated == 1){
                return true;
            } else {
                return false;
            }

        }catch(SQLException e){
            System.out.println("Errore durante la prenotazione: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteStudentBooking(Booking booking){
        Subject subject = booking.getSubject();
        TimeSlot timeSlot = booking.getTimeSlot();
        String sql = "UPDATE bookings SET student_register_number = null WHERE subject_name = ? AND start_time = ? AND end_time = ?";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, subject.getName());
            pstmt.setString(2, timeSlot.getStartTime());
            pstmt.setString(3, timeSlot.getEndTime());

            int updatedRows = pstmt.executeUpdate();

            if(updatedRows == 1){
                return true;
            }

        } catch(SQLException e ){
            System.out.println("Errore durante l'eliminazione della prenotazione: " + booking + " - " + e.getMessage() );
            return false;
        }
        return false;
    }

    // Verifico se uno studente ha già effettuato una prenotazione in uno specifico timeSlot
    public static boolean studentHasBookingAt(Student student, TimeSlot timeSlot){
        String sql = "SELECT count(*) FROM bookings WHERE student_register_number = ? AND start_time = ? AND end_time = ? ";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, student.getRegisterNumber());
            pstmt.setString(2, timeSlot.getStartTime());
            pstmt.setString(3, timeSlot.getEndTime());
            ResultSet rs = pstmt.executeQuery();
            if(rs.next() && rs.getInt(1)>0){
                return true;
            } else{
                return false;
            }

        }catch(SQLException e){
            System.out.println("Errore durante la query: " + e.getMessage());
            return false;
        }
    }

    // getAvailableSlots() metodo che ci restituisce la lista di TimeSlot che sono disponibili per una determinata materia
    public static List<TimeSlot> getAvailableSlots(Subject subject){
        List<TimeSlot> timeSlots = new ArrayList<>();
        String sql = "SELECT start_time, end_time FROM bookings WHERE subject_name = ? AND student_register_number IS NULL ";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, subject.getName());
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                String start_time = rs.getString("start_time");
                String end_time = rs.getString("end_time");
                TimeSlot timeSlot = new TimeSlot(start_time, end_time);
                timeSlots.add(timeSlot);
            }
        }catch(SQLException e){
            System.out.println("Errore durante la ricerca di Slot disponibili: " + e.getMessage());
        }
        return timeSlots;
    }

    // getBookingsByStudent() restituisce i bookings che sono stati fatti da una persona
    public static List<Booking> getBookingsByStudent(Student student){
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT subject_name, start_time, end_time FROM bookings WHERE student_register_number = ?";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1, student.getRegisterNumber());
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                TimeSlot timeSlot = new TimeSlot(rs.getString("start_time"), rs.getString("end_time"));
                String subjectName = rs.getString("subject_name");

                //Nuova query per caricare tutti i TimeSlot di quella materia
                String sql2 = "SELECT start_time, end_time FROM bookings WHERE subject_name = ? AND student_register_number IS NULL";
                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
                pstmt2.setString(1, subjectName);
                ResultSet rs2 = pstmt2.executeQuery();

                List<TimeSlot> availableSlots = new ArrayList<>();
                while(rs2.next()){
                    TimeSlot timeSlot2 = new TimeSlot(rs2.getString("start_time"), rs2.getString("end_time"));
                    availableSlots.add(timeSlot2);
                }

                //creo il subject completo con tutti i suoi timeSlot disponibili
                Subject subject = new Subject(rs.getString("subject_name"), availableSlots);

                // Creo il booking completo
                Booking booking = new Booking(student,subject,timeSlot);
                bookings.add(booking);
            }

        }catch(SQLException e){
            System.out.println("Errore durante la query: " + e.getMessage());
        }
        return bookings;
    }

    public static List<Booking> getBookingsBySubject(String subjectName) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE subject_name = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, subjectName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");
                TimeSlot timeSlot = new TimeSlot(startTime, endTime);
                Subject subject = new Subject(subjectName, null);

                // Recupero i dati dello studente, se presente
                Integer registerNumber = rs.getInt("student_register_number");
                Student student = null;

                if (!rs.wasNull()) {
                    student = BookingManager.getStudentByRegisterNumber(registerNumber);
                }

                bookings.add(new Booking(student, subject, timeSlot));
            }

        } catch (SQLException e) {
            System.out.println("Errore nel recupero delle prenotazioni: " + e.getMessage());
        }

        return bookings;
    }

    public static List<Booking> getActiveBookingsByStudentAndSubject(int registerNumber, String subjectName){
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT * FROM bookings WHERE student_register_number = ? AND subject_name = ?";

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, registerNumber);
            pstmt.setString(2, subjectName);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                String startTime = rs.getString("start_time");
                String endTime = rs.getString("end_time");

                Student student = BookingManager.getStudentByRegisterNumber(registerNumber);
                Subject subject = new Subject(subjectName, null);
                TimeSlot timeSlot = new TimeSlot(startTime, endTime);

                bookings.add(new Booking(student, subject, timeSlot));
            }
        } catch(SQLException e){
            System.out.println("Errore nel recupero delle prenotazioni attive: " + e.getMessage());
        }
        return bookings;
    }


    public static boolean checkIfStudentBusyInTimeSlot(Integer registerNumber, TimeSlot timeSlot){
        String sql = "SELECT * FROM bookings WHERE student_register_number = ? AND start_time = ? AND end_time = ?";
        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,registerNumber);
            pstmt.setString(2, timeSlot.getStartTime());
            pstmt.setString(3, timeSlot.getEndTime());

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                return true;
            }

        }catch(SQLException e){
            System.out.println("Lo studente " + registerNumber + " ha gia una prenotazione per il timeslot selezionato." + e.getMessage());
        }
        return false;
    }

    public static boolean checkIfBookingExists(Booking bookingToFind){
        String sql = "SELECT * FROM bookings WHERE student_register_number = ? AND subject_name = ? AND start_time =? AND end_time = ?";
        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setInt(1,bookingToFind.getStudent().getRegisterNumber());
            pstmt.setString(2, bookingToFind.getSubject().getName());
            pstmt.setString(3, bookingToFind.getTimeSlot().getStartTime());
            pstmt.setString(4, bookingToFind.getTimeSlot().getEndTime());

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                return true;
            }

        }catch(SQLException e){
            System.out.println("Lo studente " + bookingToFind.getStudent().getRegisterNumber() + " non ha gia una prenotazione per il timeslot selezionato." + e.getMessage());
            return false;
        }
        return false;
    }

    public static boolean checkIfEmptyBookingExists(Booking emptyBookingToFind){
        String sql = "SELECT * FROM bookings WHERE student_register_number IS NULL AND subject_name = ? AND start_time =? AND end_time = ?";
        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, emptyBookingToFind.getSubject().getName());
            pstmt.setString(2, emptyBookingToFind.getTimeSlot().getStartTime());
            pstmt.setString(3, emptyBookingToFind.getTimeSlot().getEndTime());

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                return true;
            }

        }catch(SQLException e){
            System.out.println("Lo studente " + emptyBookingToFind.getStudent().getRegisterNumber() + " non ha gia una prenotazione per il timeslot selezionato." + e.getMessage());
            return false;
        }
        return false;
    }

    public static boolean checkIfBookingIsFree(Subject subject, TimeSlot timeSlot){
        String sql = "SELECT * FROM bookings WHERE student_register_number IS NULL AND subject_name = ? AND start_time =? AND end_time = ?";

        try(Connection conn = DatabaseManager.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, subject.getName());
            pstmt.setString(2, timeSlot.getStartTime());
            pstmt.setString(3, timeSlot.getEndTime());

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                return true;
            }

        }catch(SQLException e){
            System.out.println("Errore: " + e.getMessage());
            return false;
        }
        return false;
    }
}
