package org.nicholas.bookstorevaadin.exceptions;

public class UnexpectedIdException extends Exception {
    public UnexpectedIdException() {
        super("Id is unexpected here!!!");
    }

    public UnexpectedIdException(String errMessage) {
        super(errMessage);
    }
}
