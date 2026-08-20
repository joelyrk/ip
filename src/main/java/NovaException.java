/**
 * Represents an input or command error that Nova can explain to the user.
 */
public class NovaException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message explanation shown to the user
     */
    public NovaException(String message) {
        super(message);
    }
}
