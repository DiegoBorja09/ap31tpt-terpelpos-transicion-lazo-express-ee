package com.dao;

public class DAOException extends Throwable {

    public DAOException(String message) {
        super(message);
    }

    public DAOException() {
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
}
