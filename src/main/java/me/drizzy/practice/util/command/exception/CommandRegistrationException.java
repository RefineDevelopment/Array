package me.drizzy.practice.util.command.exception;

public class CommandRegistrationException extends RuntimeException {

    public CommandRegistrationException() {
    }

    public CommandRegistrationException(String message) {
        super(message);
    }

    public CommandRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandRegistrationException(Throwable cause) {
        super(cause);
    }
}
