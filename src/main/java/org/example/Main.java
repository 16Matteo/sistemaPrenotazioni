package org.example;

import controller.BookingManager;
import customException.*;
import model.Booking;
import model.Student;
import model.Subject;
import model.TimeSlot;

import java.awt.print.Book;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            // visualizza il menu
            System.out.println("Menu:");
            System.out.println("1. Add new Student");
            System.out.println("2. Add new Subject");
            System.out.println("3. Add new TimeSlot");
            System.out.println("4. Add new Booking");
            System.out.println("5. Reserve Booking");
            System.out.println("6: Reserve multiple slots");
            System.out.println("7. Delete Booking");
            System.out.println("8. Show All Bookings by Student");
            System.out.println("9. Show All Bookings by Subject");
            System.out.println("-1. Exit");
            System.out.print("Choose an option: ");

            int scelta = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (scelta) {
                    case 1:
                        while(true) {
                            try {
                                addStudent(scanner);
                                break;
                            } catch (StudentAlreadyExistingException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    case 2:
                        // Aggiungo una nuova materia
                        addSubject(scanner);
                        break;
                    case 3:
                        // prenota uno slot
                        addTimeSlot(scanner);
                        break;
                    case 4:
                        addEmptyBooking(scanner);
                        break;
                    case 5:
                        reserveBooking(scanner);
                        break;
                    case 6:
                        bookMultipleSlots(scanner);
                        break;
                    case 7:
                        deleteBooking(scanner);
                        break;
                    case 8:
                        getBookingsByStudent(scanner);
                        break;
                    case 9:
                        getBookingsBySubject(scanner);
                        break;
                    case -1:
                        //Esci dal programma
                        running = false;
                        System.out.println("Uscita dal programma...");
                        break;
                    default:
                        System.out.println("Opzione non valida. Riprova.");
                }
            } catch(InputMismatchException e){
                System.out.println("Input non valido.");
            }
            catch(StudentException e){
                System.out.println("Errore studente: " + e.getMessage());
            }
            catch(SubjectException e){
                System.out.println("Errore materia: " + e.getMessage());
            }
            catch(BookingException e){
                System.out.println("Errore prenotazione: " + e.getMessage());
            }
            catch(Exception e){
                System.out.println("Errore: " + e.getMessage());
                System.out.println("Riprovare!");
            }
        }
        scanner.close();
    }

    private static void addStudent (Scanner scanner) throws StudentException {
        //controllo register number sia univoco
        System.out.print("Inserisci il numero di matricola dello studente: ");
        int registerNumber;
        do {
            registerNumber = scanner.nextInt();
            scanner.nextLine();
            if (!checkRegisterNumber(registerNumber)) {
                System.out.println("Inserisci un numero di matricola di 6 cifre e positivo. \nRiprova: ");
                continue;
            }
            if (!BookingManager.isRegisterNumberAvailable(registerNumber)) {
                throw new StudentException("Lo Studente con matricola " + registerNumber + " esiste già. ");
            }
            break;
        } while(true);

        System.out.print("Inserisci il nome dello studente: ");
        String name = scanner.nextLine();
        while(!checkStudentNameSurname(name)){
            System.out.println("Inserisci un nome dello studente valido.");
            name = scanner.nextLine();
        }

        System.out.print("Inserisci il cognome dello studente: ");
        String surname = scanner.nextLine();
        while(!checkStudentNameSurname(surname)){
            System.out.println("Inserisci un cognome dello studente valido.");
            surname = scanner.nextLine();
        }

        // Creo un nuovo oggetto Student e lo aggiungo al database
        if(BookingManager.addStudent(new Student(registerNumber, name, surname))){
            System.out.println("Studente " + name + " " + surname + " aggiunto con successo!");
        }else{
            throw new StudentException("Studente non aggiunto al database!");
        }
    }

    private static void addSubject(Scanner scanner) throws SubjectException{
        System.out.println("Inserisci il nome della materia: ");
        String subjectName = askUserSubjectName(scanner).getName();
        if(BookingManager.getSubjectByName(subjectName) != null){
            throw new SubjectException("Materia già esistente!");
        }

        System.out.println("Vuoi aggiungere dei timeSlot? (s/n) ");
        String risposta = scanner.nextLine().toLowerCase();
        LinkedList<TimeSlot> timeSlotList = null;
        if(risposta.equals("s")){
            timeSlotList = new LinkedList<>();
            while(true){
                TimeSlot t = askUserTimeSlot(scanner);
                timeSlotList.add(t);
                System.out.println("Vuoi aggiungere un altro timeSlot? (s/n) ");
                String res = scanner.nextLine().toLowerCase();
                if(res.equals("n")){
                    break;
                }
            }
        }

        // Aggiungo la materia al database
        Subject subject = new Subject(subjectName, timeSlotList);
        boolean positiveInsertion = BookingManager.addSubject(subject);
        if(positiveInsertion) {
            System.out.println("Materia " + subjectName + " aggiunta con successo!");
        } else{
            throw new SubjectException("Errore aggiunta materia al database!");
        }
    }

    private static void addTimeSlot(Scanner scanner) throws TimeSlotException {
        TimeSlot timeSlot = askUserTimeSlot(scanner);
        if(BookingManager.isTimeSlotAvailable(timeSlot)){
            throw new TimeSlotException("TimeSlot giá presente nel database!");
        }
        if(BookingManager.addTimeSlot(timeSlot)){
            System.out.println("TimeSlot " + timeSlot + " aggiunto con successo!");
        } else{
            throw new TimeSlotException("Errore aggiunta time slot al database!");
        }
    }

    private static void addEmptyBooking(Scanner scanner) throws BookingException{
        System.out.println("Per quale materia vuoi aggiungere un booking? ");
        System.out.println(BookingManager.getAllSubjectsString());
        Subject subject = null;
        System.out.println("Inserisci il nome della materia: ");
        while(subject == null ) {
            String subjectName = scanner.nextLine();
            subject = BookingManager.getSubjectByName(subjectName);

            if (subject == null) {
                System.out.println("Inserisci un nome della materia esistente.");
            }
        }
        TimeSlot timeSlot = null;
        while(timeSlot == null){
            //utente inserisce timeslot
            timeSlot = askUserTimeSlot(scanner);
            if(!BookingManager.isTimeSlotAvailable(timeSlot)){
                System.out.println("Time slot non disponibile. Riprovare.");
                timeSlot=null;
                continue;
            }
            break;
        }

        Booking emptyBooking = new Booking(null, subject, timeSlot);

        if(BookingManager.checkIfBookingExists(emptyBooking)){
            throw new BookingException("Booking già esistente!");
        }

        boolean success = BookingManager.addEmptyBooking(emptyBooking);
        if(success){
            System.out.println("Booking vuoto aggiunto con successo! ");
        } else{
            throw new BookingException("Errore time slot non aggiunto al database!");
        }
    }

    private static void reserveBooking(Scanner scanner) throws StudentException, SubjectException, BookingException{
        System.out.println("Inserisci la matricola dello studente: ");
        int studentRegisterNumber = scanner.nextInt();
        scanner.nextLine();
        if(BookingManager.getStudentByRegisterNumber(studentRegisterNumber) == null){
            throw new StudentException("Studente non esistente");
        }

        System.out.print("Inserisci il nome della materia: ");
        String subjectName = scanner.nextLine();
        if(BookingManager.getSubjectByName(subjectName) == null){
            throw new SubjectException("Materia non esistente!");
        }

        System.out.println("Ecco gli slot disponibili per la materia selezionata: \n");
        List<TimeSlot> timeSlotsAvailable = BookingManager.getTimeSlotsAvailableForSubject(new Subject(subjectName, null));
        for(TimeSlot t : timeSlotsAvailable){
            System.out.println(t);
        }

        System.out.print("Inserisci l'orario di inizio dello slot: ");
        String startTime = scanner.nextLine();

        System.out.print("Inserisci l'orario di fine dello slot: ");
        String endTime = scanner.nextLine();

        TimeSlot selectedTimeSlot = new TimeSlot(startTime, endTime);

        // verifico che lo studente non sia impegnato nello stesso slot
        boolean isBusy = BookingManager.checkIfStudentBusyInTimeSlot(studentRegisterNumber, selectedTimeSlot);
        if(isBusy){
            throw new StudentException("lo studente é giá occupato in questo timeslot!");
        }

        //creo il booking
        Student student = BookingManager.getStudentByRegisterNumber(studentRegisterNumber);
        Subject subject = BookingManager.getSubjectByName(subjectName);
        Booking booking = new Booking(student, subject, selectedTimeSlot);

        boolean success = BookingManager.reserveBooking(booking);
        if(success){
            System.out.println("Prenotazione avvenuta con successo!");
        } else{
            throw new BookingException("Errore nell'aggiunta della prenotazione al database!");
        }
    }

    private static void bookMultipleSlots(Scanner scanner) throws StudentException, SubjectException, TimeSlotException, BookingException{
        System.out.println("Inserisci il numero di matricola dello studente: ");
        int registerNumber = askUserRegisterNumber(scanner);
        if(BookingManager.getStudentByRegisterNumber(registerNumber) == null){
            throw new StudentException("Studente non esistente!");
        }

        System.out.println("Inserisci il nome della materia: ");
        Subject subject = askUserSubjectName(scanner);
        if(BookingManager.getSubjectByName(subject.getName()) == null){
            throw new SubjectException("Materia non esistente!");
        }

        System.out.println("Ecco gli slot liberi: ");
        List<TimeSlot> availableSlotForSubject = BookingManager.getTimeSlotsAvailableForSubject(subject);
        if(availableSlotForSubject.isEmpty()){
            throw new TimeSlotException("Nessuno slot disponibile per la materia selezionata!");
        }
        int index = 1;
        for(TimeSlot t : availableSlotForSubject){
            System.out.println(index + ": " + t);
            index++;
        }
        List<TimeSlot> timeSlotsToBook = new LinkedList<>();
        while(true){
            System.out.println("Inserisci lo slot da prenotare o -1 per terminare: ");
            String scelta = scanner.nextLine();
            if(scelta.equals("-1")){
                break;
            }
            try{
                int indice = Integer.parseInt(scelta);
                if(indice < 1 || indice > availableSlotForSubject.size()){
                    System.out.println("numero slot non valido riprovare. ");
                    continue;
                }
                TimeSlot t = availableSlotForSubject.get(indice - 1);
                if(timeSlotsToBook.contains(t)){
                    System.out.println("Errore hai giá prenotato questo time slot! ");
                    continue;
                }
                timeSlotsToBook.add(t);
            }catch(NumberFormatException e){
                System.out.println("Input non valido, inserisci un numero.");
            }
        }
        boolean success = BookingManager.bookMultipleSlots(registerNumber, subject, timeSlotsToBook);
        if(success){
            System.out.println("Prenotazioni effettuate con successo!");
        } else{
            throw new BookingException("Errore durante l'agigunta della prenotazione al database!");
        }
    }

    private static void deleteBooking(Scanner scanner) throws StudentException, SubjectException, BookingException{
        System.out.println("Inserisci il numero di matricola dello studente: ");
        int registerNumber = askUserRegisterNumber(scanner);
        if(BookingManager.getStudentByRegisterNumber(registerNumber) == null){
            throw new StudentException("Studente non esitente!");
        }

        System.out.println("Inserisci la materia: ");
        String subjectName = askUserSubjectName(scanner).getName();
        if(BookingManager.getSubjectByName(subjectName) == null){
            throw new SubjectException("Materia non esistente!");
        }

        List<Booking> activeBookings = BookingManager.getActiveBookingsByStudentAndSubject(registerNumber, subjectName);
        if(activeBookings.isEmpty()){
            throw new BookingException("Impossibile eliminare una prenotazione: nessuna prenotazione esitente!");
        }

        System.out.println("Ecco tutte le prenotazioni attive: \n");
        int index = 1;
        for(Booking b : activeBookings){
            System.out.println(index + ": " + b.getTimeSlot());
            index++;
        }

        while(true) {
            System.out.println("Iserisci il numero della prenotazione da cancellare o -1 per terminare: ");
            String scelta = scanner.nextLine();
            if (scelta.equals("-1")) {
                break;
            }
            try {
                int indice = Integer.parseInt(scelta);
                if (indice < 1 || indice > activeBookings.size()) {
                    System.out.println("numero della prenotazione non valido. Riprovare. ");
                    continue;
                }
                Booking b = activeBookings.get(indice -1);
                boolean success = BookingManager.deleteStudentBooking(b);
                if (success) {
                    System.out.println("Prenotazioni cancellate con successo!");
                } else {
                    throw new BookingException("Errore durante la cancellazione delle prenotazioni!");
                }
            }catch(NumberFormatException e){
                System.out.println("Input non valido, inserisci un numero.");
            }
        }
    }

    private static boolean checkStudentNameSurname(String s) {
        if(s.isEmpty()){
            return false;
        } else if(s.length()<3){
            return false;
        } else if (s.matches(".*[^A-Za-z].*")){
            return false;
        } else{
            return true;
        }
    }

    private static boolean checkRegisterNumber(int registerNumber) {
        if(registerNumber < 100000 || registerNumber > 999999) {
            return false;
        }
        return true;
    }

    private static boolean checkSubjectName(String name) {
        if(name.isEmpty()){
            return false;
        } else if(name.length()<3){
            return false;
        } else if(name.matches(".*[^A-Za-z&].*")){
            return false;
        } else{
            return true;
        }
    }

    private static boolean checkTimeSlotFormat(String timeSlot) {
        if(timeSlot == null){
            return false;
        } else if(timeSlot.isEmpty()){
            return false;
        } else if(timeSlot.matches("^(?:[01][0-9]|2[0-3]):[0-5][0-9]$")){
            return true;
        } else{
            return false;
        }
    }

    private static int askUserRegisterNumber(Scanner scanner){
        String studRegNumber = scanner.nextLine();
        while(studRegNumber == null || !studRegNumber.matches("\\d+") || !checkRegisterNumber(Integer.parseInt(studRegNumber))){
            System.out.println("Errore: inserire un register number valido: ");
            studRegNumber = scanner.nextLine();
        }
        return Integer.parseInt(studRegNumber);
    }

    private static Subject askUserSubjectName(Scanner scanner){
        String subjectName = scanner.nextLine();
        while(!checkSubjectName(subjectName)){
            System.out.println("Inserisci un nome della materia valido.");
            subjectName = scanner.nextLine();
        }
        return new Subject(subjectName, null);
    }

    private static TimeSlot askUserTimeSlot(Scanner scanner){
        String startTime="";
        String endTime="";
        while(true){
            System.out.println("Inserisci startTime: ");
            startTime = scanner.nextLine();
            while(!checkTimeSlotFormat(startTime)){
                System.out.println("Inserisci un timeSlot valido.");
                startTime = scanner.nextLine();
            }

            System.out.println("Inserisci endTime: ");
            endTime = scanner.nextLine();
            while(!checkTimeSlotFormat(endTime)){
                System.out.println("Inserisci un timeSlot valido.");
                endTime = scanner.nextLine();
            }

            if(!BookingManager.checkIfTimeSlotExists(new TimeSlot(startTime, endTime))){
                System.out.println("Errore: Time slot non disponibile.");
                continue;
            }
            if(startTimeAfterEndTime(startTime, endTime)){
                System.out.println("Errore: End time deve essere superiore allo start time.");
                continue;
            }
            break;
        }

        return  new TimeSlot(startTime, endTime);
    }

    private static boolean startTimeAfterEndTime(String startTime, String endTime) {
        if(startTime.compareTo(endTime) >= 0){
            System.out.println("Il timeSlot start deve essere più piccolo del timeSlot end");
            return true;
        } else{
            return false;
        }
    }

    private static void getBookingsByStudent(Scanner scanner){
        System.out.println("Inserisci la matricola dello studente: ");
        int registerNumber = scanner.nextInt();
        scanner.nextLine();
        if(BookingManager.getStudentByRegisterNumber(registerNumber) == null){
            System.out.println("Errore studente non esistente.");
            return;
        }

        List<Booking> bookings = BookingManager.getBookingsByStudent(BookingManager.getStudentByRegisterNumber(registerNumber));
        if (bookings.isEmpty()) {
            System.out.println("Nessuna prenotazione trovata per lo studente.");
        } else {
            System.out.println("Prenotazioni dello studente:");
            for (Booking b : bookings) {
                System.out.println("Subject: " + b.getSubject().getName() +
                        ", start_time: " + b.getTimeSlot().getStartTime() +
                        ", end_time: " + b.getTimeSlot().getEndTime());
            }
        }
    }

    private static void getBookingsBySubject(Scanner scanner) {
        System.out.println("Inserisci il nome della materia: ");
        String subjectName = askUserSubjectName(scanner).getName();
        if(BookingManager.getSubjectByName(subjectName) == null){
            System.out.println("Errore: materia non trovata.");
            return;
        }

        List<Booking>bookings = BookingManager.getBookingsBySubject(subjectName);

        for(Booking b : bookings){
            String stato = (b.getStudent() == null) ? "LIBERO" : "PRENOTATO da " + b.getStudent().getName();
            System.out.println(b.getTimeSlot() + " - " + stato);
        }
    }
}