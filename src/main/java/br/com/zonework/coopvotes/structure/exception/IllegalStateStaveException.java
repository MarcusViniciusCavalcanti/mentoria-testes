package br.com.zonework.coopvotes.structure.exception;

public class IllegalStateStaveException extends IllegalStateException {

    public IllegalStateStaveException(String old, String next) {
        super("State not pass from %s to %s state".formatted(old, next));
    }
}
