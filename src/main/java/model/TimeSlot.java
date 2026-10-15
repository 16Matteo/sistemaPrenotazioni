package model;

import java.util.Objects;

public class TimeSlot implements Comparable<TimeSlot> {
    private String startTime;
    private String endTime;

    public TimeSlot(String startTime, String endTime) {
        if(startTime == null || endTime == null || !startTime.matches("^([01][0-9]|2[0-3]):[0-5][0-9]$") || !endTime.matches("^([01][0-9]|2[0-3]):[0-5][0-9]$")) {
            throw new IllegalArgumentException("Formato orario non valido.");
        }
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public int compareTo(TimeSlot o) {
        return this.startTime.compareTo(o.startTime);
    }

    public String toString() {
        return "TimeSlot{ startTime: " + startTime + ", endTime: " + endTime + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot timeSlot = (TimeSlot) o;
        return Objects.equals(startTime, timeSlot.startTime) && Objects.equals(endTime, timeSlot.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTime, endTime);
    }
}
