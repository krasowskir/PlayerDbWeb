package org.richard.home.exception;

import java.sql.SQLException;

public class DatabaseAccessFailed extends Exception {

    public DatabaseAccessFailed() {
        super("database access failed to postgres");
    }

    public DatabaseAccessFailed(SQLException message) {
        super(message);
    }

    public DatabaseAccessFailed(String message, Throwable cause) {
        super(message, cause);
    }
}
