package model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ModelEqualityTest {
    @Test
    void student_equalsAndHashCode() {
        Student s1 = new Student(111111, "Mario", "Rossi");
        Student s2 = new Student(111111, "Mario", "Rossi");
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void subject_equalsAndHashCode() {
        TimeSlot ts = new TimeSlot("10:00", "10:30");
        Subject s1 = new Subject("Storia", Collections.singletonList(ts));
        Subject s2 = new Subject("Storia", Collections.singletonList(ts));
        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void booking_equalsAndHashCode() {
        Student student = new Student(111111, "Mario", "Rossi");
        TimeSlot t = new TimeSlot("10:00", "10:30");
        Subject subject = new Subject("Storia", Collections.emptyList());
        Booking b1 = new Booking(student, subject, t);
        Booking b2 = new Booking(student, subject, t);
        assertEquals(b1, b2);
        assertEquals(b1.hashCode(), b2.hashCode());
    }

    @Test
    void toString_containsKeyFields(){
        Student student = new Student(111111, "Mario", "Rossi");
        String str = student.toString().toLowerCase();
        assertTrue(str.contains("mario"));
        assertTrue(str.contains("rossi"));
        Subject subject = new Subject("Storia", Collections.emptyList());
        String str1 = subject.toString().toLowerCase();
        assertTrue(str1.contains("storia"));
    }

}
