package dao;

import db.DatabaseManager;
import model.TimeSlot;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TimeSlotDAOTest {
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
        conn.createStatement().execute("DELETE FROM timeslots");
    }

    @Test
    void createAndReadTimeSlotTest() {
        TimeSlot timeSlot = new TimeSlot("10:00", "10:15");
        TimeSlotDAO.createTimeSlot(timeSlot);

        TimeSlot fetched = TimeSlotDAO.getTimeSlotByStartAndEndTime("10:00", "10:15");
        assertNotNull(fetched, "il timeslot deve essere presente e non nullo");
        assertEquals("10:00", fetched.getStartTime());
        assertEquals("10:15", fetched.getEndTime());
    }

    @Test
    void createAndReadTimeSlotTestNotPresent() {
        TimeSlot timeSlot = new TimeSlot("10:00", "10:15");
        TimeSlotDAO.createTimeSlot(timeSlot);

        TimeSlot fetched = TimeSlotDAO.getTimeSlotByStartAndEndTime("11:00", "11:15");
        assertNull(fetched, "il timeslot non deve essere presente");
    }

    @Test
    void getTimeSlotByStartAndEndTimeTest() {
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);

        TimeSlot fetched = TimeSlotDAO.getTimeSlotByStartAndEndTime("10:00", "10:15");
        assertNotNull(fetched, "il timeslot deve essere presente e non nullo");
        assertEquals("10:00", fetched.getStartTime());
        assertEquals("10:15", fetched.getEndTime());

        fetched = TimeSlotDAO.getTimeSlotByStartAndEndTime("11:00", "11:15");
        assertNotNull(fetched, "il timeslot deve essere presente e non nullo");
        assertEquals("11:00", fetched.getStartTime());
        assertEquals("11:15", fetched.getEndTime());
    }

    @Test
    void deleteTimeSlotTest() {
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);

        boolean fetched = TimeSlotDAO.deleteTimeSlot("10:00", "10:15");
        assertTrue(fetched);
    }

    @Test
    void deleteTimeSlotTestNotPresent() {
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);

        boolean fetched = TimeSlotDAO.deleteTimeSlot("11:00", "11:15");
        assertFalse(fetched);
    }

    @Test
    void getAllTimeSlotsTest(){
        TimeSlot timeSlot1 = new TimeSlot("10:00", "10:15");
        TimeSlot timeSlot2 = new TimeSlot("11:00", "11:15");
        TimeSlotDAO.createTimeSlot(timeSlot1);
        TimeSlotDAO.createTimeSlot(timeSlot2);

        List<TimeSlot> fetched = TimeSlotDAO.getAllTimeSlots();
        assertNotNull(fetched, "I timeslots devono esistere");
        assertEquals(fetched.size(), 2);
        assertEquals(fetched.get(0), timeSlot1);
        assertEquals(fetched.get(1), timeSlot2);
    }
}
