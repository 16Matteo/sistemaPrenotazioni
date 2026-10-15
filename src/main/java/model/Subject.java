package model;

import java.util.List;
import java.util.Objects;

public class Subject {
    private String name;
    private List<TimeSlot> timeSlots;

    public Subject(String name, List<TimeSlot> timeSlots) {
        this.name = name;
        this.timeSlots = timeSlots;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public boolean equals(Subject subject) {
        return this.name.equals(subject.name);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                ", timeSlots=" + timeSlots +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(name, subject.name) && Objects.equals(timeSlots, subject.timeSlots);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timeSlots);
    }
}
