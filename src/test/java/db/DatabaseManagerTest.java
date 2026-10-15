package db;

import org.h2.engine.Database;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseManagerTest {

    @Test
    void getTestConnection_returnsH2Mem() throws Exception {
        Connection c = DatabaseManager.getTestConnection();
        assertNotNull(c,"La connessione non deve essere null");
        DatabaseMetaData md = c.getMetaData();
        String url = md.getURL();
        assertTrue(url.contains("mem:test"),"L'url deve indicare un H2 in-memory, era: " + url);
        c.close();
    }
}
