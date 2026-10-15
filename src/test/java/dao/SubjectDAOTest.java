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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubjectDAOTest {
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
    void cleanUp() throws Exception {
        conn.createStatement().execute("DELETE FROM bookings");
        conn.createStatement().execute("DELETE FROM subjects");
        conn.createStatement().execute("DELETE FROM timeslots");
    }

    @Test
    void testCreateAndReadSubjectWithTimeSlots(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        Subject s = new Subject("Storia", timeSlots);

        SubjectDAO.addSubject(s);
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        BookingDAO.addEmptyBooking(new Booking(null, s, timeSlot1));
        BookingDAO.addEmptyBooking(new Booking(null, s, timeSlot2));
        Subject fetched = SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia");
        assertNotNull(fetched, "La materia deve essere presente e non nulla");
        assertEquals("Storia", fetched.getName());
        assertEquals(2, fetched.getTimeSlots().size());
        TimeSlot timeSlotFetched = fetched.getTimeSlots().get(0);
        assertEquals(timeSlot1, timeSlotFetched);
        TimeSlot timeSlotFetched2 = fetched.getTimeSlots().get(1);
        assertEquals(timeSlot2, timeSlotFetched2);
    }

    @Test
    void testCreateAndReadSubjectWithTimeSlotsNotPresent(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        List<TimeSlot> timeSlots = new ArrayList<>();
        timeSlots.add(timeSlot1);
        timeSlots.add(timeSlot2);
        Subject s = new Subject("Storia", timeSlots);

        SubjectDAO.addSubject(s);
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);
        BookingDAO.addEmptyBooking(new Booking(null, s, timeSlot1));
        BookingDAO.addEmptyBooking(new Booking(null, s, timeSlot2));
        Subject fetched = SubjectDAO.getSubjectAndTimeslotsBySubjectName("Matematica");
        assertNull(fetched, "La materia non deve essere presente");
    }

    @Test
    void testCreateAndReadSubjectWithoutTimeSlots(){
        Subject s = new Subject("Storia", null);

        SubjectDAO.addSubject(s);
        Subject fetched = SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia");
        assertNotNull(fetched, "La materia deve essere presente e non nulla");
        assertEquals("Storia", fetched.getName());
    }

    @Test
    void testCreateAndReadSubjectWithoutTimeSlotsNotPresent(){
        Subject s = new Subject("Storia", null);

        SubjectDAO.addSubject(s);
        Subject fetched = SubjectDAO.getSubjectAndTimeslotsBySubjectName("Matematica");
        assertNull(fetched, "La materia non deve essere presente");
    }

    @Test
    void testGetAllSubjects(){
        Subject s1 = new Subject("Storia", null);
        Subject s2 = new Subject("Matematica", null);

        SubjectDAO.addSubject(s1);
        SubjectDAO.addSubject(s2);
        List<String> subjectListFetched = SubjectDAO.getAllSubjects();
        assertNotNull(subjectListFetched, "Le materie devono essere presenti e non nulle");
        assertEquals(2, subjectListFetched.size());
        assertEquals("Storia", subjectListFetched.get(1));
        assertEquals("Matematica", subjectListFetched.get(0));
    }

    @Test
    void testSubjectsNotPresents(){
        List<String> subjectListFetched = SubjectDAO.getAllSubjects();
        assertEquals(subjectListFetched, Collections.emptyList());
    }

    @Test
    void testDeleteSubject_success(){
        Subject s = new Subject("Storia", null);

        SubjectDAO.addSubject(s);
        Subject fetched = SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia");
        assertNotNull(fetched, "La materia deve essere presente e non nulla");

        SubjectDAO.deleteSubject(s);

        Subject deleted = SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia");
        assertNull(deleted, "La materia dovrebbe essere stata eliminata con successo dal database");
    }

    @Test
    void testDeleteSubject_nonExisting(){
        Subject s = new Subject("Filosofia", null);

        assertDoesNotThrow(()->SubjectDAO.deleteSubject(s));
    }

}
