package exceptions;

public class SemesterNichtMoeglichException extends Exception {
    public SemesterNichtMoeglichException(String errorMessage) {
        super(errorMessage);
    }
}
