package br.com.zonework.coopvotes.application.handler;

import static br.com.zonework.coopvotes.structure.data.MessageMapper.ARGUMENT_INVALID;
import static br.com.zonework.coopvotes.structure.data.MessageMapper.KNOWN;
import static br.com.zonework.coopvotes.structure.data.MessageMapper.MEDIA_TYPE_NOT_SUPPORTED;
import static br.com.zonework.coopvotes.structure.data.MessageMapper.REQUEST_INVALID;
import static br.com.zonework.coopvotes.structure.data.MessageMapper.REQUEST_METHOD_INVALID;
import static br.com.zonework.coopvotes.structure.data.MessageMapper.RESOURCE_NOT_FOUND;
import static br.com.zonework.coopvotes.structure.data.MessageMapper.URL_NOT_FOUND;
import static org.springframework.http.HttpHeaders.ACCEPT_LANGUAGE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE;
import br.com.zonework.coopvotes.application.controller.EndpointsTranslator;
import br.com.zonework.coopvotes.application.dto.ErrorBaseDto;
import br.com.zonework.coopvotes.application.dto.ErrorDetailsDto;
import br.com.zonework.coopvotes.application.dto.ErrorDto;
import br.com.zonework.coopvotes.structure.exception.BusinessException;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.util.StreamUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class HandlerError implements EndpointsTranslator {

    private final MessageSource messageSource;

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorBaseDto> httpMessageNotReadableException(
            HttpMessageNotReadableException exception,
            HttpServletRequest request) {
        log.error("Erro read request {}", exception.getMessage());
        return buildResponseError(request, REQUEST_INVALID.getCode(), BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorBaseDto> httpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request) {
        var path = request.getServletPath();
        var method = exception.getMethod();
        log.error("Erro attempt action in url: {}, with http method: {}",
                path,
                exception.getMessage());
        return buildResponseError(request, REQUEST_METHOD_INVALID.getCode(), METHOD_NOT_ALLOWED,
                path, method);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorBaseDto> noHandlerFoundException(
            NoHandlerFoundException exception,
            HttpServletRequest request) {
        var path = request.getServletPath();
        var method = exception.getHttpMethod();
        log.error("Router not undefined to {}", path);
        return buildResponseError(request, URL_NOT_FOUND.getCode(), NOT_FOUND, path, method);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorBaseDto> httpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request) {
        var type = exception.getContentType();
        log.error("Request error media type {}", type);

        var locale = getLocale(request.getHeader(ACCEPT_LANGUAGE));
        var message = getMessage(MEDIA_TYPE_NOT_SUPPORTED.getCode(), locale);
        var error = new ErrorDto()
                .message(message)
                .path(request.getServletPath())
                .statusCode(UNSUPPORTED_MEDIA_TYPE.value());

        return ResponseEntity.status(UNSUPPORTED_MEDIA_TYPE).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorBaseDto> methodArgumentNotValidException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {
        log.error("Invalid field {}", exception.getMessage());
        var errorsDetails = exception.getFieldErrors().stream()
                .map(error -> {
                    var field = error.getField();
                    var msg = StringUtils.defaultString(
                            error.getDefaultMessage(),
                            StringUtils.EMPTY);

                    return new ErrorDetailsDto()
                            .descriptionError(msg)
                            .field(field);
                }).toList();

        return getErrorBaseDtoResponseEntity(request, errorsDetails);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorBaseDto> constraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request) {
        log.error("Error invalida field", exception);
        var errors = exception.getConstraintViolations().stream()
                .map(constraintViolation -> {
                    var field = fieldNameFromPropertyPath(constraintViolation.getPropertyPath());
                    var msg = constraintViolation.getMessage();
                    return new ErrorDetailsDto()
                            .descriptionError(msg)
                            .field(field);
                }).toList();

        return getErrorBaseDtoResponseEntity(request, errors);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<ErrorBaseDto> defaultHandler(
            Exception exception,
            HttpServletRequest request) {
        log.error("Erro internal {}", exception.getMessage(), exception);
        return buildResponseError(request, KNOWN.getCode(), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorBaseDto> entityNotFount(
        EntityNotFoundException exception,
        HttpServletRequest request) {
        log.error("Erro resource not found {}", exception.getMessage());

        var path = request.getServletPath();
        var locale = getLocale(request.getHeader(ACCEPT_LANGUAGE));
        var details = exception.getMessage();
        var message = getMessage(RESOURCE_NOT_FOUND.getCode(), locale, details);

        var error = new ErrorDto()
            .statusCode(NOT_FOUND.value())
            .path(path)
            .message(message);
        return ResponseEntity.status(NOT_FOUND).body(error);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorBaseDto> businessException(
        BusinessException exception,
        HttpServletRequest request) {
        log.error("Error business {}", exception.getMessage());

        var path = request.getServletPath();
        var status = exception.getStatus();
        var code = exception.getCode();
        var details = exception.getDetails();
        var locale = getLocale(request.getHeader(ACCEPT_LANGUAGE));
        var message = getMessage(code, locale, details);

        var error = new ErrorDto()
            .message(message)
            .path(path)
            .statusCode(status.value());

        return ResponseEntity.status(status).body(error);
    }

    private ResponseEntity<ErrorBaseDto> getErrorBaseDtoResponseEntity(HttpServletRequest request,
            List<ErrorDetailsDto> errors) {
        var path = request.getServletPath();
        var locale = getLocale(request.getHeader(ACCEPT_LANGUAGE));
        var message = getMessage(ARGUMENT_INVALID.getCode(), locale);
        var error = new ErrorDto()
                .details(errors)
                .message(message)
                .path(path)
                .statusCode(HttpStatus.BAD_REQUEST.value());

        return ResponseEntity.status(BAD_REQUEST).body(error);
    }

    private ResponseEntity<ErrorBaseDto> buildResponseError(
            HttpServletRequest request,
            String code,
            HttpStatus httpStatus,
            String... args) {
        var path = request.getServletPath();
        var locale = getLocale(request.getHeader(ACCEPT_LANGUAGE));
        var message = getMessage(code, locale, args);
        var error = new ErrorDto()
                .message(message)
                .path(path)
                .statusCode(httpStatus.value());

        return ResponseEntity.status(httpStatus).body(error);
    }

    @Override
    public MessageSource getMessageSource() {
        return messageSource;
    }

    private String fieldNameFromPropertyPath(Path path) {
        var list = StreamUtils.createStreamFromIterator(path.iterator()).toList();
        return list.get(list.size() - 1).getName();
    }

}
