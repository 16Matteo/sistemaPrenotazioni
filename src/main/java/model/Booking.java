package model;

import java.util.Objects;

public class Booking {
    private Student student;
    private Subject subject;
    private TimeSlot timeSlot;

    public Booking(Student student, Subject subject, TimeSlot timeSlot) {
        this.student = student;
        this.subject = subject;
        this.timeSlot = timeSlot;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public String toString() {
        return "Booking{" + student +
                ", " + subject +
                ", " + timeSlot +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(student, booking.student) && Objects.equals(subject, booking.subject) && Objects.equals(timeSlot, booking.timeSlot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, subject, timeSlot);
    }
}
