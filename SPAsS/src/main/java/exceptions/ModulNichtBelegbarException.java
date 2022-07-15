package exceptions;

public class ModulNichtBelegbarException extends Exception {
    public ModulNichtBelegbarException(String errorMessage) {
        super(errorMessage);
    }
}
