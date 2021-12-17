package br.com.zonework.coopvotes.structure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus status;
    private final String code;
    private final String[] details;

    public BusinessException(HttpStatus status, String code, String ...details) {
        super("Throws exception with status code %s and code %s".formatted(status, code));
        this.status = status;
        this.code = code;
        this.details = details;
    }
}
