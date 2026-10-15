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

import java.io.IOException;
import java.util.List;

public class BookingManager {
    public static boolean addStudent(Student s) throws StudentException {
        if(s == null){
            //return false;
            throw new StudentException("Variabile studente nulla.");
        }
        // se non esiste lo aggiungo
        return StudentDAO.addStudent(s.getRegisterNumber(), s.getName(), s.getSurname());
    }

    public static boolean addSubject(Subject s){
        if(s==null){
            return false;
        }
        boolean result = SubjectDAO.addSubject(s);
        if(s.getTimeSlots()==null){
            return result;
        } else{
            if(!result){
                return false;
            }
            // Per ogni timeslot creo un'oggetto booking e lo vado ad aggiungere alla tabella
            for(TimeSlot t: s.getTimeSlots()){
                Booking b = new Booking(null, s, t);
                BookingDAO.addEmptyBooking(b);
            }
            return true;
        }
    }

    public static boolean addTimeSlot(TimeSlot ts) {
        if(ts == null){
            return false;
        }
        return TimeSlotDAO.createTimeSlot(ts);
    }

    public static boolean addEmptyBooking(Booking booking) {
        return BookingDAO.addEmptyBooking(booking);
    }

    public static boolean reserveBooking(Booking booking) {
        if(booking==null){
            return false;
        } else{
            return BookingDAO.reserveBooking(booking);
        }
    }

    public static boolean bookMultipleSlots(int regNumber, Subject subject, List<TimeSlot> timeSlots){
        if(StudentDAO.getStudentByRegisterNumber(regNumber) == null){
            System.out.println("La matricola inserita non esiste!");
            return false;
        }
        if(SubjectDAO.getSubjectAndTimeslotsBySubjectName(subject.getName()) == null){
            System.out.println("La materia inserita non esiste! ");
            return false;
        }
        //controllo che tutti i time slot possano essere prenotati
        for(TimeSlot t: timeSlots){
            if(!BookingDAO.checkIfBookingIsFree(subject, t)){
                System.out.println("Errore: time slot" + t + " non disponibile per questa materia.");
                return false;
            }
        }
        for(TimeSlot t : timeSlots){
            Booking b = new Booking(StudentDAO.getStudentByRegisterNumber(regNumber), subject, t);
            if(!reserveBooking(b)){
                System.out.println("Errore nella prenotazione del seguente booking: " + b );
                return false;
            } else{
                System.out.println("Booking " + b + " prenotato con successo");
            }
        }
        return true;
    }

    public static boolean deleteStudentBooking(Booking booking){
        return BookingDAO.deleteStudentBooking(booking);
    }

    public static boolean checkIfTimeSlotExists(TimeSlot t){
        if(TimeSlotDAO.getTimeSlotByStartAndEndTime(t.getStartTime(), t.getEndTime()) == null){
            return false;
        } else{
            return true;
        }
    }

    public static List<Booking> getActiveBookingsByStudentAndSubject(int registerNumber, String subjectName){
        return BookingDAO.getActiveBookingsByStudentAndSubject(registerNumber, subjectName);
    }

    public static boolean isRegisterNumberAvailable(int registerNumber) {
        Student isAvailable = StudentDAO.getStudentByRegisterNumber(registerNumber);
        if(isAvailable != null){
            return false;
        } else {
            return true;
        }
    }

    public static boolean isTimeSlotAvailable(TimeSlot timeSlot){
        TimeSlot isAvailable = TimeSlotDAO.getTimeSlotByStartAndEndTime(timeSlot.getStartTime(), timeSlot.getEndTime());
        if(isAvailable != null){
            return true;
        } else {
            return false;
        }
    }

    public static Student getStudentByRegisterNumber(int registerNumber) {
        return StudentDAO.getStudentByRegisterNumber(registerNumber);
    }

    public static String getAllSubjectsString() {
        List<String> dbSubjects = SubjectDAO.getAllSubjects();
        String res = "";
        for(String s: dbSubjects){
            res += s+"\n";
        }
        return res;
    }

    public static List<String> getAllSubjects() {
        return SubjectDAO.getAllSubjects();
    }

    public static Subject getSubjectByName(String subjectName) {
        return SubjectDAO.getSubjectAndTimeslotsBySubjectName(subjectName);
    }

    public static List<TimeSlot> getTimeSlotsAvailableForSubject(Subject subject){
        return BookingDAO.getAvailableSlots(subject);
    }

    public static List<Booking> getBookingsByStudent(Student student){
        return BookingDAO.getBookingsByStudent(student);
    }

    public static List<Booking> getBookingsBySubject(String subjectName){
        return BookingDAO.getBookingsBySubject(subjectName);
    }

    public static boolean checkIfStudentBusyInTimeSlot(Integer registerNumber, TimeSlot timeSlot) {
        return BookingDAO.checkIfStudentBusyInTimeSlot(registerNumber, timeSlot);
    }

    public static boolean checkIfBookingExists(Booking booking) {
        if (booking.getStudent() != null) {
            if (!BookingDAO.checkIfBookingExists(booking)) {
                return false;
            } else {
                return true;
            }
        } else{
            if (!BookingDAO.checkIfEmptyBookingExists(booking)) {
                return false;
            } else {
                return true;
            }
        }
    }

}
