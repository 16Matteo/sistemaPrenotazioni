package controller;

import customException.StudentException;
import dao.BookingDAO;
import dao.StudentDAO;
import dao.SubjectDAO;
import dao.TimeSlotDAO;
import model.Booking;
import model.Student;
import model.Subject;
import model.TimeSlot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@SuppressWarnings("ALL")
public class BookingManagerTest {

    static{
        System.setProperty("net.bytebuddy.dynamic.module.infer", "false");
    }

    // -- addStudent --
    @Test
    void addStudent_null_throwsException(){
        assertThrows(StudentException.class, () -> BookingManager.addStudent(null));
    }

    @Test
    void addStudent_valid_delegatesToDAO() throws StudentException {
        Student s = new Student(111111, "Mario", "Rossi");
        try(MockedStatic<StudentDAO> ms = mockStatic(StudentDAO.class)) {
            ms.when(() -> StudentDAO.addStudent(111111, "Mario", "Rossi")).thenReturn(true);
            assertTrue(BookingManager.addStudent(s));
            ms.verify(() -> StudentDAO.addStudent(111111, "Mario", "Rossi"));
        }
    }

    // -- addSubject --
    @Test
    void addSubject_null_return_false(){
        assertFalse(BookingManager.addSubject(null));
    }

    @Test
    void addSubject_noTimeSlots_delegatesOnlyToSubjectDAO(){
        Subject s = new Subject("Storia", Collections.emptyList());
        try(MockedStatic<SubjectDAO> ms = mockStatic(SubjectDAO.class);
            MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            ms.when(() -> SubjectDAO.addSubject(s)).thenReturn(true);
            assertTrue(BookingManager.addSubject(s));
            ms.verify(() -> SubjectDAO.addSubject(s));
            mb.verifyNoInteractions();
        }
    }

    @Test
    void addSubject_withTimeSlots_createsEmptyBooking(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        Subject s = new Subject("Storia", Arrays.asList(t));
        try(MockedStatic<SubjectDAO> ms = mockStatic(SubjectDAO.class);
            MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            ms.when(() -> SubjectDAO.addSubject(s)).thenReturn(true);
            assertTrue(BookingManager.addSubject(s));
            ms.verify(() -> SubjectDAO.addSubject(s));
            mb.verify(() -> BookingDAO.addEmptyBooking(argThat(b ->
                    b.getSubject()==s && b.getTimeSlot()==t)));
        }
    }

    // -- addTimeSlot --
    @Test
    void addTimeSlot_null_return_false(){
        assertFalse(BookingManager.addTimeSlot(null));
    }

    @Test
    void addTimeSlot_valid_delegatesToDAO(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        try(MockedStatic<TimeSlotDAO> ms = mockStatic(TimeSlotDAO.class)) {
            ms.when(() -> TimeSlotDAO.createTimeSlot(t)).thenReturn(true);
            assertTrue(BookingManager.addTimeSlot(t));
            ms.verify(() -> TimeSlotDAO.createTimeSlot(t));
        }
    }

    // -- addEmptyBooking --
    @Test
    void addEmptyBooking_delegatesToDAO(){
        Booking b = new Booking(null, null, null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            mb.when(() -> BookingDAO.addEmptyBooking(b)).thenReturn(true);
            assertTrue(BookingManager.addEmptyBooking(b));
            mb.verify(() -> BookingDAO.addEmptyBooking(b));
        }
    }

    // -- reserveBooking --
    @Test
    void reserveBooking_null_returnsFalse(){
        assertFalse(BookingManager.reserveBooking(null));
    }

    @Test
    void reservBooking_valid_delegatesToDAO(){
        Booking b = new Booking(null, null, null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            mb.when(() -> BookingDAO.reserveBooking(b)).thenReturn(true);
            assertTrue(BookingManager.reserveBooking(b));
            mb.verify(() -> BookingDAO.reserveBooking(b));
        }
    }

    // -- BookMultipleSlots --
    @Test
    void bookMultipleSlots_studentMissing_returnsFalse(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        Subject sub = new Subject("Storia", Arrays.asList(t));
        try(MockedStatic<StudentDAO> ms = mockStatic(StudentDAO.class)) {
            ms.when(() -> StudentDAO.getStudentByRegisterNumber(1)).thenReturn(null);
            assertFalse(BookingManager.bookMultipleSlots(1,sub,Arrays.asList(t)));
        }
    }

    @Test
    void bookMultipleSlots_busySlot_returnsFalse(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        Subject sub = new Subject("Storia", Arrays.asList(t));
        Student stud = new Student(111111, "Mario", "Rossi");
        try(MockedStatic<StudentDAO> mst = mockStatic(StudentDAO.class);
            MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class);
            MockedStatic<SubjectDAO> msb = mockStatic(SubjectDAO.class)) {
            mst.when(() -> StudentDAO.getStudentByRegisterNumber(111111)).thenReturn(stud);
            msb.when(() -> SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia")).thenReturn(sub);
            mb.when(() -> BookingDAO.checkIfBookingIsFree(sub,t)).thenReturn(false);
            assertFalse(BookingManager.bookMultipleSlots(111111,sub,Arrays.asList(t)));
        }
    }

    @Test
    void bookMultipleSlots_allFree_reservesAll(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        Subject sub = new Subject("Storia", Arrays.asList(t));
        Student stud = new Student(111111, "Mario", "Rossi");
        try(MockedStatic<StudentDAO> mst = mockStatic(StudentDAO.class);
            MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class);
            MockedStatic<SubjectDAO> msb = mockStatic(SubjectDAO.class)){
            mst.when(() -> StudentDAO.getStudentByRegisterNumber(111111)).thenReturn(stud);
            msb.when(() -> SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia")).thenReturn(sub);
            mb.when(() -> BookingDAO.checkIfBookingIsFree(sub,t)).thenReturn(true);
            mb.when(() -> BookingDAO.reserveBooking(argThat(b->true))).thenReturn(true);
            assertTrue(BookingManager.bookMultipleSlots(111111, sub,Arrays.asList(t)));
            mb.verify(() -> BookingDAO.reserveBooking(argThat(b->b.getStudent()==stud && b.getSubject()==sub && b.getTimeSlot()==t)));
        }
    }

    // -- deleteStudentBooking --
    @Test
    void deleteStudentBooking_delegatesToDAO(){
        Booking b = new Booking(null, null, null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            mb.when(() -> BookingDAO.deleteStudentBooking(b)).thenReturn(true);
            assertTrue(BookingManager.deleteStudentBooking(b));
            mb.verify(() -> BookingDAO.deleteStudentBooking(b));
        }
    }

    // -- checkIfTimeSlotsExists --
    @Test
    void checkIfTimeSlotsExists_missing_returnsFalse(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        try(MockedStatic<TimeSlotDAO> mt = mockStatic(TimeSlotDAO.class) ) {
            mt.when(() -> TimeSlotDAO.getTimeSlotByStartAndEndTime(t.getStartTime(), t.getEndTime())).thenReturn(null);
            assertFalse(BookingManager.checkIfTimeSlotExists(t));
        }
    }

    @Test
    void checkIfTimeSlotsPresent_returnsTrue(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        try(MockedStatic<TimeSlotDAO> mt = mockStatic(TimeSlotDAO.class)) {
            mt.when(() -> TimeSlotDAO.getTimeSlotByStartAndEndTime(t.getStartTime(), t.getEndTime())).thenReturn(t);
            assertTrue(BookingManager.checkIfTimeSlotExists(t));
        }
    }

    // -- getActiveBookingByStudentsAndSubject --
    @Test
    void getActiveBookingByStudentsAndSubject_delegatesToDAO(){
        List<Booking> bookingList = Arrays.asList(new Booking(null, null, null), new Booking(null, null, null));
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            mb.when(() -> BookingDAO.getActiveBookingsByStudentAndSubject(111111, "Storia")).thenReturn(bookingList);
            assertEquals(bookingList,BookingManager.getActiveBookingsByStudentAndSubject(111111,"Storia"));
        }
    }

    // -- isRegisterNumberAvailable --
    @Test
    void isRegisterNumberAvailable_taken_returnsFalse(){
        try(MockedStatic<StudentDAO> ms = mockStatic(StudentDAO.class)){
            ms.when(() -> StudentDAO.getStudentByRegisterNumber(111111)).thenReturn(new Student(111111, "Mario", "Rossi"));
            assertFalse(BookingManager.isRegisterNumberAvailable(111111));
        }
    }

    @Test
    void isRegisterNumberAvailable_free_returnsTrue(){
        try(MockedStatic<StudentDAO> ms = mockStatic(StudentDAO.class)){
            ms.when(() -> StudentDAO.getStudentByRegisterNumber(111111)).thenReturn(null);
            assertTrue(BookingManager.isRegisterNumberAvailable(111111));
        }
    }

    // -- isTimeSlotAvailable --
    @Test
    void isTimeSlotAvailable_missing_returnFalse(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        try(MockedStatic<TimeSlotDAO> mt = mockStatic(TimeSlotDAO.class)){
            mt.when(() -> TimeSlotDAO.getTimeSlotByStartAndEndTime(t.getStartTime(), t.getEndTime())).thenReturn(null);
            assertFalse(BookingManager.isTimeSlotAvailable(t));
        }
    }

    @Test
    void isTimeSlotAvailable_present_returnsTrue(){
        TimeSlot t = new TimeSlot("10:00", "10:15");
        try(MockedStatic<TimeSlotDAO> mt = mockStatic(TimeSlotDAO.class)){
            mt.when(() -> TimeSlotDAO.getTimeSlotByStartAndEndTime(t.getStartTime(), t.getEndTime())).thenReturn(t);
            assertTrue(BookingManager.isTimeSlotAvailable(t));
        }
    }

    // -- getStudentByRegisterNumber --
    @Test
    void getStudentByRegisterNumber_delegatesToDAO(){
        Student s = new Student(111111, "Mario", "Rossi");
        try(MockedStatic<StudentDAO> ms = mockStatic(StudentDAO.class)) {
            ms.when(() -> StudentDAO.getStudentByRegisterNumber(111111)).thenReturn(s);
            assertEquals(s,BookingManager.getStudentByRegisterNumber(111111));
        }
    }

    // -- getAllSubjectsString && getAllSubject --
    @Test
    void getAllSubjectsString_formatscorrectly(){
        List<String> subjects = Arrays.asList("Storia", "Geografia");
        try(MockedStatic<SubjectDAO> ms = mockStatic(SubjectDAO.class)) {
            ms.when(() -> SubjectDAO.getAllSubjects()).thenReturn(subjects);
            assertEquals("Storia\nGeografia\n",BookingManager.getAllSubjectsString());
        }
    }

    @Test
    void getAllSubjects_delegatesToDAO(){
        List<String> subjects = Collections.singletonList("Storia");
        try(MockedStatic<SubjectDAO> ms = mockStatic(SubjectDAO.class)) {
            ms.when(() -> SubjectDAO.getAllSubjects()).thenReturn(subjects);
            assertEquals(subjects, BookingManager.getAllSubjects());
        }
    }

    // -- getSubjectByName --
    @Test
    void getSubjectByName_delegatesToDAO(){
        Subject s = new Subject(null, null);
        try(MockedStatic<SubjectDAO> ms = mockStatic(SubjectDAO.class)) {
            ms.when(()->SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia")).thenReturn(s);
            assertEquals(s,BookingManager.getSubjectByName("Storia"));
        }
    }

    // -- getTimeSlotAvailableForSubject --
    @Test
    void getTimeSlotAvailableForSubject_delegatesToDAO(){
        Student s = new Student(111111, "Mario", "Rossi");
        TimeSlot t1 = new TimeSlot("10:00","10:15");
        TimeSlot t2 = new TimeSlot("10:15","10:30");
        List<TimeSlot> timeSlots = Arrays.asList(t1,t2);
        Subject sub = new Subject("Storia",timeSlots);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)) {
            mb.when(() -> BookingDAO.getAvailableSlots(sub)).thenReturn(timeSlots);
            assertEquals(timeSlots, BookingManager.getTimeSlotsAvailableForSubject(sub));
        }
    }

    // -- getBookingsByStudents --
    @Test
    void getBookingsByStudents_delegatesToDAO(){
        Student s = new Student(111111, "Mario", "Rossi");
        List<Booking> bookings = Arrays.asList(new Booking(s, null, null));
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
            mb.when(()->BookingDAO.getBookingsByStudent(s)).thenReturn(bookings);
            assertEquals(bookings, BookingManager.getBookingsByStudent(s));
        }
    }

    @Test
    void getBookingsBySubject_delegatesToDAO(){
        Subject s = new Subject("Storia", null);
        List<Booking> bookings = Arrays.asList(new Booking(null, null, null));
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
            mb.when(()->BookingDAO.getBookingsBySubject("Storia")).thenReturn(bookings);
            assertEquals(bookings, BookingManager.getBookingsBySubject("Storia"));
        }
    }

    // -- checkIfBookingExists --
    @Test
    void checkIfBookingExists_withStudent_callsExists(){
        Student s = new Student(111111, "Mario", "Rossi");
        Booking b = new Booking(s, null, null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
            mb.when(()->BookingDAO.checkIfBookingExists(b)).thenReturn(true);
            assertTrue(BookingManager.checkIfBookingExists(b));
        }
    }

    @Test
    void checkIfBookingExists_withStudent_false(){
        Booking b = new Booking(new Student(111111, "Mario", "Rossi"), new Subject("Storia", null), null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
            mb.when(()->BookingDAO.checkIfBookingExists(b)).thenReturn(false);
            assertFalse(BookingManager.checkIfBookingExists(b));
        }
    }

    @Test
    void checkIfBookingExists_empty_callsEmptyExists(){
        Booking b = new Booking(null, null, null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
            mb.when(()->BookingDAO.checkIfEmptyBookingExists(b)).thenReturn(true);
            assertTrue(BookingManager.checkIfBookingExists(b));
        }
    }

    @Test
    void checkIfBookingExists_empty_false(){
        Booking b = new Booking(null, null, null);
        try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
            mb.when(()->BookingDAO.checkIfEmptyBookingExists(b)).thenReturn(false);
            assertFalse(BookingManager.checkIfBookingExists(b));
        }
    }

    @Test
    void bookMultipleSlots_subjectMissing_returnFalse(){
        Student student = new Student(111111, "Mario", "Rossi");
        Subject subject = new Subject("Storia", null);
        try(MockedStatic<StudentDAO> st = mockStatic(StudentDAO.class);
            MockedStatic<SubjectDAO> sb = mockStatic(SubjectDAO.class)){
            st.when(()->StudentDAO.getStudentByRegisterNumber(111111)).thenReturn(student);
            sb.when(()->SubjectDAO.getSubjectAndTimeslotsBySubjectName("Storia")).thenReturn(null);
            assertFalse(BookingManager.bookMultipleSlots(111111, subject,Collections.emptyList()));
            try(MockedStatic<BookingDAO> mb = mockStatic(BookingDAO.class)){
                mb.verifyNoInteractions();
            }
        }
    }

    @Test
    void addSubject_subjectDAOFails_returnFalseAndNoEmptyBooking(){
        TimeSlot ts = new TimeSlot("10:00", "10:15");
        Subject subject = new Subject("Storia", Arrays.asList(ts));
        try(MockedStatic<SubjectDAO> sb = mockStatic(SubjectDAO.class);
            MockedStatic<BookingDAO> b = mockStatic(BookingDAO.class)){
            sb.when(()->SubjectDAO.addSubject(subject)).thenReturn(false);
            assertFalse(BookingManager.addSubject(subject));
            sb.verify(()->SubjectDAO.addSubject(subject));
            b.verifyNoInteractions();
        }
    }

    @Test
    void getActiveBookingsByStudentAndSubject_delegatesToDAO(){
        List<Booking> expected = Arrays.asList(new Booking(null, null,null), new Booking(null, null, null));
        try(MockedStatic<BookingDAO> b = mockStatic(BookingDAO.class)){
            b.when(()->BookingDAO.getActiveBookingsByStudentAndSubject(111111, "Storia")).thenReturn(expected);
            List<Booking> actual = BookingManager.getActiveBookingsByStudentAndSubject(111111, "Storia");
            assertEquals(expected, actual);
            b.verify(()->BookingDAO.getActiveBookingsByStudentAndSubject(111111, "Storia"));
        }
    }

    @Test
    void checkIfStudentBusyInTimeSlot_delegatesToDAO(){
        TimeSlot ts = new TimeSlot("10:00", "10:15");
        try(MockedStatic<BookingDAO> b  = mockStatic(BookingDAO.class)){
            b.when(()->BookingDAO.checkIfStudentBusyInTimeSlot(111111, ts)).thenReturn(true);
            assertTrue(BookingManager.checkIfStudentBusyInTimeSlot(111111, ts));
            b.verify(()->BookingDAO.checkIfStudentBusyInTimeSlot(111111, ts));
        }
    }

    @Test
    void reserveBooking_DAOReturnFalse_returnFalse(){
        Booking booking = new Booking(null, null, null);
        try(MockedStatic<BookingDAO> b = mockStatic(BookingDAO.class)){
            b.when(()->BookingDAO.reserveBooking(booking)).thenReturn(false);
            assertFalse(BookingManager.reserveBooking(booking));
            b.verify(()->BookingDAO.reserveBooking(booking));
        }
    }
}
