package dao;

import db.DatabaseManager;
import model.Student;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class StudentDAOTest {
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
    void cleanUp() throws Exception{
        conn.createStatement().execute("DELETE FROM students");
    }

    @Test
    void testCreateAndReadStudent(){
        Student s = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(s.getRegisterNumber(), s.getName(), s.getSurname());
        Student fetched = StudentDAO.getStudentByRegisterNumber(s.getRegisterNumber());
        assertNotNull(fetched, "Lo studente deve essere presente e non nullo");
        assertEquals("Mario", fetched.getName());
        assertEquals("Rossi", fetched.getSurname());
    }

    @Test
    void testUpdateStudent(){
        Student s = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(s.getRegisterNumber(), s.getName(), s.getSurname());
        s.setName("Matteo");
        s.setSurname("Bianchi");
        StudentDAO.updateStudent(s);
        Student fetched = StudentDAO.getStudentByRegisterNumber(s.getRegisterNumber());
        assertNotNull(fetched, "Lo studente deve essere presente e non nullo");
        assertEquals("Matteo", fetched.getName());
        assertEquals("Bianchi", fetched.getSurname());
    }

    @Test
    void testDeleteStudent(){
        Student s = new Student(111111, "Mario", "Rossi");
        StudentDAO.addStudent(s.getRegisterNumber(), s.getName(), s.getSurname());
        StudentDAO.deleteStudent(s.getRegisterNumber());
        Student fetched = StudentDAO.getStudentByRegisterNumber(s.getRegisterNumber());
        assertNull(fetched, "Lo studente non dovrebbe piú esistere.");
    }



    @AfterAll
    static void desableTestDB(){
        System.clearProperty("useTestDB");
    }

}
