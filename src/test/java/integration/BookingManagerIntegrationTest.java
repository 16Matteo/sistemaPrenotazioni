package integration;

import controller.BookingManager;
import dao.TimeSlotDAO;
import db.DatabaseManager;
import model.Booking;
import model.Student;
import model.TimeSlot;
import model.Subject;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class BookingManagerIntegrationTest {
    private Connection conn;

    @BeforeAll
    void initDatabase() throws Exception {
        System.setProperty("useTestDB", "true");
        conn = DatabaseManager.getTestConnection();
        RunScript.execute(conn, new FileReader("src/test/resources/schema.sql"));
    }

    @AfterAll
    void cleanUpDatabase() throws Exception {
        conn.close();
        System.clearProperty("useTestDb");
    }

    @BeforeEach
    void cleanTables() throws Exception {
        conn.createStatement().execute("DELETE FROM bookings");
        conn.createStatement().execute("DELETE FROM timeslots");
        conn.createStatement().execute("DELETE FROM subjects");
        conn.createStatement().execute("DELETE FROM students");
    }

    @Test
    void fullBookingFlow_shouldSucceed() throws Exception {
        System.out.println(BookingManager.getStudentByRegisterNumber(111111));
        //creiamo uno studente
        Student s = new Student(111111, "Mario", "Rossi");
        assertTrue(BookingManager.addStudent(s));

        TimeSlot ts1 = new TimeSlot("10:00", "10:15");
        assertTrue(BookingManager.addTimeSlot(ts1));
        TimeSlot ts2 = new TimeSlot("11:00", "11:15");
        assertTrue(BookingManager.addTimeSlot(ts2));

        Subject subject = new Subject("Storia", Arrays.asList(ts1, ts2));
        assertTrue(BookingManager.addSubject(subject));

        assertNotNull(TimeSlotDAO.getTimeSlotByStartAndEndTime("10:00", "10:15"));
        assertNotNull(TimeSlotDAO.getTimeSlotByStartAndEndTime("11:00", "11:15"));

        boolean booked = BookingManager.bookMultipleSlots(s.getRegisterNumber(), subject, Arrays.asList(ts1, ts2));
        assertTrue(booked, "Booking non deve fallire");
        List<Booking> active = BookingManager.getActiveBookingsByStudentAndSubject(s.getRegisterNumber(), subject.getName());
        assertEquals(2, active.size());

        Booking toDelete = active.get(0);
        assertTrue(BookingManager.deleteStudentBooking(toDelete));

        List<Booking> remaining = BookingManager.getActiveBookingsByStudentAndSubject(s.getRegisterNumber(), subject.getName());
        assertEquals(1, remaining.size());
    }
}
