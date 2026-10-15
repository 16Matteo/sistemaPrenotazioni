package dao;

import db.DatabaseManager;
import model.Booking;
import model.Student;
import model.Subject;
import model.TimeSlot;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BookingDAOTest {
    private static Connection conn;

    @BeforeAll
    static void setupDatabase() throws Exception {
        System.setProperty("useTestDB", "true");
        conn = DatabaseManager.getTestConnection();
        RunScript.execute(conn, new FileReader("src/test/resources/schema.sql"));
    }

    @BeforeEach
    void init() {
    }

    @AfterEach
    void cleanUp() throws Exception{
        conn.createStatement().execute("DELETE FROM bookings");
        conn.createStatement().execute("DELETE FROM students");
        conn.createStatement().execute("DELETE FROM subjects");
        conn.createStatement().execute("DELETE FROM timeslots");
    }

    @Test
    void addEmptyBookingTest(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);

        boolean result = BookingDAO.addEmptyBooking(new Booking(null, subject, timeSlot1));
        assertTrue(result);
        BookingDAO.addEmptyBooking(new Booking(null, subject, timeSlot2));
        List<Booking> fetched = BookingDAO.getBookingsBySubject("Storia");
        assertEquals(2,fetched.size());
        assertEquals(fetched.get(1).getSubject().getName(), subject.getName());
        assertEquals(fetched.get(1).getTimeSlot().getStartTime(), timeSlot2.getStartTime());
        assertEquals(fetched.get(1).getTimeSlot().getEndTime(), timeSlot2.getEndTime());

        assertEquals(fetched.get(0).getSubject().getName(), subject.getName());
        assertEquals(fetched.get(0).getTimeSlot().getStartTime(), timeSlot1.getStartTime());
        assertEquals(fetched.get(0).getTimeSlot().getEndTime(), timeSlot1.getEndTime());
    }

    @Test
    void addCompleteBookingTest(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);
        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(111111, "Mario", "Rossi");

        Booking booking = new Booking(student, subject, timeSlot1);
        assertTrue(BookingDAO.addBooking(booking));
        List<Booking> fetched = BookingDAO.getBookingsByStudent(student);
        assertEquals(1,fetched.size());
        assertEquals(fetched.get(0).getStudent().getRegisterNumber(), student.getRegisterNumber());
        assertEquals(fetched.get(0).getSubject().getName(), subject.getName());
        assertEquals(fetched.get(0).getTimeSlot().getStartTime(), timeSlot1.getStartTime());
        assertEquals(fetched.get(0).getTimeSlot().getEndTime(), timeSlot1.getEndTime());
    }

    @Test
    void reserveBookingTest(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);
        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(111111, "Mario", "Rossi");

        Booking booking = new Booking(null, subject, timeSlot1);
        BookingDAO.addEmptyBooking(booking);
        assertNull(BookingDAO.getBookingsBySubject("Storia").get(0).getStudent());

        booking.setStudent(student);
        BookingDAO.reserveBooking(booking);
        assertEquals(111111, BookingDAO.getBookingsBySubject("Storia").get(0).getStudent().getRegisterNumber());
    }

    @Test
    void deleteStudentBooking_success(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);
        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(111111, "Mario", "Rossi");

        Booking emptyBooking = new Booking(null, subject, timeSlot1);
        BookingDAO.addEmptyBooking(emptyBooking);
        Booking booking = new Booking(student, subject, timeSlot1);
        BookingDAO.reserveBooking(booking);

        assertTrue(BookingDAO.studentHasBookingAt(student, timeSlot1), "Lo studente dovrebbe avere una prenotazione prima della cancellazione");

        //elimino lo studente dalla prenotazione
        boolean result = BookingDAO.deleteStudentBooking(booking);
        assertTrue(result, "La cancellazione della prenotazione dovrebbe restituire true");

        //verifico che lo studente non abbia più la prenotazione
        assertFalse(BookingDAO.studentHasBookingAt(student, timeSlot1), "Lo studente non dovrebbe più avere la prenotazione dopo la cancellazione");
    }

    @Test
    void studentHasBookingAt_true(){
        TimeSlot timeSlot = new TimeSlot("10:00", "10:15");
        TimeSlotDAO.createTimeSlot(timeSlot);

        List<TimeSlot> timeSlots = List.of(timeSlot);

        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);

        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(111111, "Mario", "Rossi");

        Booking booking = new Booking(student, subject, timeSlot);
        BookingDAO.addBooking(booking);

        assertTrue(BookingDAO.studentHasBookingAt(student,timeSlot), "Lo studente dovrebbe avere la prenotazione per quell'orario");
    }

    @Test
    void studentHasBookingAt_false(){
        TimeSlot timeSlot = new TimeSlot("10:00", "10:45");
        TimeSlotDAO.createTimeSlot(timeSlot);

        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(111111, "Mario", "Rossi");

        assertFalse(BookingDAO.studentHasBookingAt(student, timeSlot), "Lo studente non dovrebbe avere nessuna prenotazione per quel time slot");
    }

    @Test
    void getAvailableSlots_returnsCorrectSlots(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00","11:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);

        List<TimeSlot> timeSlots = Arrays.asList(timeSlot1,timeSlot2);
        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);

        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(student.getRegisterNumber(), student.getName(), student.getSurname());

        Booking booking1 = new Booking(student, subject, timeSlot1);
        Booking booking2 = new Booking(null, subject, timeSlot2);
        BookingDAO.addBooking(booking1);
        BookingDAO.addEmptyBooking(booking2);

        List<TimeSlot> availableSlots = BookingDAO.getAvailableSlots(subject);

        assertEquals(1, availableSlots.size(), "Deve esserci solo uno slot disponibile");
        assertEquals("11:00",availableSlots.get(0).getStartTime());
        assertEquals("11:15", availableSlots.get(0).getEndTime());
    }

    @Test
    void getBookingByStudent_returnCorrectBookings(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00","11:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);

        List<TimeSlot> timeSlots = Arrays.asList(timeSlot1,timeSlot2);
        Subject subject = new Subject("Storia", timeSlots);
        SubjectDAO.addSubject(subject);

        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(student.getRegisterNumber(), student.getName(), student.getSurname());

        Booking booking = new Booking(student, subject, timeSlot1);
        Booking bookingEmpty = new Booking(null, subject, timeSlot2);
        BookingDAO.addBooking(booking);
        BookingDAO.addEmptyBooking(bookingEmpty);

        List<Booking> bookings = BookingDAO.getBookingsByStudent(student);

        assertEquals(1, bookings.size(), "dovrebbe tornare solo la prenotazione dello studente");
        Booking b = bookings.get(0);
        assertEquals(student.getRegisterNumber(), b.getStudent().getRegisterNumber());
        assertEquals("Storia", b.getSubject().getName());
        assertEquals("10:00", b.getTimeSlot().getStartTime());
        assertEquals("10:15", b.getTimeSlot().getEndTime());
    }

    @Test
    void getActiveBookingsByStudentAndSubjectTest() {
        TimeSlot timeSlot1 = new TimeSlot("09:00", "09:15");
        TimeSlot timeSlot2 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot3 = new TimeSlot("10:15", "10:30");
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        TimeSlotDAO.createTimeSlot(timeSlot3);

        List<TimeSlot> timeSlots = Arrays.asList(timeSlot1, timeSlot2);
        Subject subject1 = new Subject("Storia", timeSlots);
        Subject subject2 = new Subject("Informatica", timeSlots);
        SubjectDAO.addSubject(subject1);
        SubjectDAO.addSubject(subject2);

        Student student1 = new Student(111111, "Mario", "Rossi");
        Student student2 = new Student(666666, "Anna", "Verdi");
        StudentDAO.addStudent(student1.getRegisterNumber(), student1.getName(), student1.getSurname());
        StudentDAO.addStudent(student2.getRegisterNumber(), student2.getName(), student2.getSurname());

        Booking booking1 = new Booking(student1, subject1, timeSlot1); // dovrebbe essere recuperato
        Booking booking2 = new Booking(student1, subject2, timeSlot2); // soggetto diverso
        Booking booking3 = new Booking(student2, subject1, timeSlot3); // studente diverso
        BookingDAO.addBooking(booking1);
        BookingDAO.addBooking(booking2);
        BookingDAO.addBooking(booking3);

        List<Booking> result = BookingDAO.getActiveBookingsByStudentAndSubject(student1.getRegisterNumber(), subject1.getName());

        assertEquals(1, result.size(), "Dovrebbe tornare solo una prenotazione corrispondente");
        Booking b = result.get(0);
        assertEquals(student1.getRegisterNumber(), b.getStudent().getRegisterNumber());
        assertEquals(subject1.getName(), b.getSubject().getName());
        assertEquals("09:00", b.getTimeSlot().getStartTime());
        assertEquals("09:15", b.getTimeSlot().getEndTime());
    }

    @Test
    void checkIfStudentBusyInTimeSlotTest(){
        TimeSlot timeSlot1 = new TimeSlot("09:00", "09:15");
        TimeSlot timeSlot2 = new TimeSlot("10:00", "10:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);

        Student student = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(student.getRegisterNumber(), student.getName(), student.getSurname());

        Subject subject = new Subject("Storia", null);
        SubjectDAO.addSubject(subject);

        Booking booking = new Booking(student, subject, timeSlot1);
        BookingDAO.addBooking(booking);

        boolean isBusy = BookingDAO.checkIfStudentBusyInTimeSlot(111111, timeSlot1);
        assertTrue(isBusy, "Lo studente dovrebbe essere occupato nel time slot 9:00-9:15");

        boolean isFree = BookingDAO.checkIfStudentBusyInTimeSlot(111111, timeSlot2);
        assertFalse(isFree, "Lo studente dovrebbe essere libero nel timeslot 10:00-10:15");

        boolean noStudent = BookingDAO.checkIfStudentBusyInTimeSlot(999999, timeSlot1);
        assertFalse(noStudent, "Uno studente non esistente non dovrebbe risultare occupato");
    }

    @Test
    void CheckIfBookingExistsTest() {
        TimeSlot timeSlot = new TimeSlot("10:00", "10:15");
        Subject subject = new Subject("Storia", null);
        Student student = new Student(111111, "Mario", "Rossi");

        TimeSlotDAO.createTimeSlot(timeSlot);
        StudentDAO.addStudent(student.getRegisterNumber(), student.getName(), student.getSurname());
        SubjectDAO.addSubject(subject);

        Booking booking = new Booking(student, subject, timeSlot);
        BookingDAO.addBooking(booking);

        assertTrue(BookingDAO.checkIfBookingExists(booking), "La prenotazione dovrebbe esistere");

        TimeSlot differentTimeSlot = new TimeSlot("15:00", "15:15");
        Booking noExistingBooking = new Booking(student, subject, differentTimeSlot);
        assertFalse(BookingDAO.checkIfBookingExists(noExistingBooking), "La prenotazione non dovrebbe esistere");
    }

    @Test
    void CheckIfEmptyBookingExistsTest() {
        TimeSlot timeSlot = new TimeSlot("10:00", "10:15");
        Subject subject = new Subject("Storia", null);

        TimeSlotDAO.createTimeSlot(timeSlot);
        SubjectDAO.addSubject(subject);

        Booking emptyBooking = new Booking(null, subject, timeSlot);
        BookingDAO.addEmptyBooking(emptyBooking);

        assertTrue(BookingDAO.checkIfEmptyBookingExists(emptyBooking), "La prenotazione vuota dovrebbe esistere");

        TimeSlot timeSlot2 = new TimeSlot("17:00", "17:15");
        Booking nonExistingEmptyBooking = new Booking(null, subject, timeSlot2);
        assertFalse(BookingDAO.checkIfEmptyBookingExists(nonExistingEmptyBooking), "La prenotazione vuota non dovrebbe esistere");
    }

    @Test
    void checkIfBookingIsFreeTest() {
        Subject subject = new Subject("Storia", null);
        TimeSlot timeSlot = new TimeSlot("10:00", "10:15");

        TimeSlotDAO.createTimeSlot(timeSlot);
        SubjectDAO.addSubject(subject);

        // Caso negativo: non c'è nessuna prenotazione libera -> deve tornare false
        boolean resultBeforeInsert = BookingDAO.checkIfBookingIsFree(subject, timeSlot);
        assertFalse(resultBeforeInsert, "Non dovrebbe esserci una prenotazione libera");

        // Inserisci una prenotazione libera (student_register_number = null)
        Booking freeBooking = new Booking(null, subject, timeSlot);
        BookingDAO.addEmptyBooking(freeBooking);

        // Caso positivo: ora la prenotazione libera esiste -> deve tornare true
        boolean resultAfterInsert = BookingDAO.checkIfBookingIsFree(subject, timeSlot);
        assertTrue(resultAfterInsert, "Dovrebbe trovare la prenotazione libera");
    }

}
