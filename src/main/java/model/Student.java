package model;

import java.util.Objects;

public class Student implements Comparable<Student> {
    private int registerNumber;
    private String name;
    private String surname;

    public Student(int registerNumber, String name, String surname) {
        if(registerNumber < 100000 || registerNumber > 999999) {
            throw new IllegalArgumentException("Matricola non valida.");
        }
        if(name.trim().isEmpty() || name.length() < 3 || name.matches(".*[^A-Za-z].*")) {
            throw new IllegalArgumentException("Nome non valido.");
        }
        if(surname.trim().isEmpty() || surname.length() < 3 || surname.matches(".*[^A-Za-z].*")) {
            throw new IllegalArgumentException("Cognome non valido.");
        }

        this.registerNumber = registerNumber;
        this.name = name;
        this.surname = surname;
    }

    public int getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(int registerNumber) {
        this.registerNumber = registerNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }


    public int compareTo(Student other) {
        return Integer.compare(this.registerNumber, other.registerNumber);
    }

    public String toString() {
        return "Student{" + "registerNumber:" + registerNumber + ", name:'" + name + '\'' + ", surname:'" + surname + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return registerNumber == student.registerNumber && Objects.equals(name, student.name) && Objects.equals(surname, student.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registerNumber, name, surname);
    }
}