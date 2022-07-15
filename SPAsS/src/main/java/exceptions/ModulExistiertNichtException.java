package exceptions;

public class ModulExistiertNichtException extends Exception {
    public ModulExistiertNichtException(String errorMessage) {
        super(errorMessage);
    }
}
