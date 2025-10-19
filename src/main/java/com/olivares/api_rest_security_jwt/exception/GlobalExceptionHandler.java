package com.olivares.api_rest_security_jwt.exception;


import com.olivares.api_rest_security_jwt.model.dto.response.ResponseApiDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.*;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseApiDTO<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
        }
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Map<String, List<String>>> handleRuntimeExceptions(RuntimeException ex, HttpStatusCode status) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        List<String> errors = Collections.singletonList(ex.getMessage());
        return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), status);
    }

    private Map<String, List<String>> getErrorsMap(List<String> errors) {
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return errorResponse;
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ResponseApiDTO<Object>> handleConstraintViolation(final ConstraintViolationException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final List<String> errors = new ArrayList<>();
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errors.add(violation.getRootBeanClass().getName() + " " + violation.getPropertyPath() + ": " + violation.getMessage());
        }
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    // 404
    @ExceptionHandler({NoHandlerFoundException.class})
    protected ResponseEntity<ResponseApiDTO<Object>> handleNoHandlerFoundException(final NoHandlerFoundException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final String error = "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL();
        final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), Arrays.asList(error));
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    // 405
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    protected ResponseEntity<ResponseApiDTO<Object>> handleHttpRequestMethodNotSupported(final HttpRequestMethodNotSupportedException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" Method is not supported for this request. Supported methods are ");
        ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
        final ApiError apiError = new ApiError(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(), Arrays.asList(builder.toString()));
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    // 415
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    protected ResponseEntity<ResponseApiDTO<Object>> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" Media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));
        final ApiError apiError = new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getLocalizedMessage(), Arrays.asList(builder.substring(0, builder.length() - 2)));
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ResponseApiDTO<Object>> handleAll(final Exception ex, final WebRequest request) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getLocalizedMessage(), Arrays.asList("error occurred"));
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }


    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        // Devuelve una ResponseEntity con el código de estado y el mensaje personalizados
        // de la ResponseStatusException
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.valueOf(ex.getStatusCode().value()), ex.getLocalizedMessage(), Arrays.asList(builder.toString()));
        ResponseApiDTO responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    @ExceptionHandler(UsuarioNotFoundException.class)
    public ResponseEntity<Object> handleUsuarioNotFoundException(UsuarioNotFoundException ex) {
        // Devuelve una ResponseEntity con el código de estado y el mensaje personalizados
        // de la ResponseStatusException
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), Arrays.asList(builder.toString()));
        ResponseApiDTO<Object> responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Object> handlePasswordException(PasswordException ex) {
        // Devuelve una ResponseEntity con el código de estado y el mensaje personalizados
        // de la ResponseStatusException
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), Arrays.asList(builder.toString()));
        ResponseApiDTO<Object> responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }


    @ExceptionHandler(RolNotFoundException.class)
    public ResponseEntity<Object> handleRolNotFoundException(RolNotFoundException ex) {
        // Devuelve una ResponseEntity con el código de estado y el mensaje personalizados
        // de la ResponseStatusException
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMessage());
        final ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), Arrays.asList(builder.toString()));
        ResponseApiDTO<Object> responseApiDTO = new ResponseApiDTO(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseApiDTO<Object>> handleAuthenticationException(AuthenticationException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, ex.getLocalizedMessage(), List.of("Autenticación fallida: " + ex.getMessage()));
        ResponseApiDTO<Object> responseApiDTO = new ResponseApiDTO<>(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseApiDTO<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        log.info(ex.getClass().getName());
        log.error(ex.getMessage(), ex);
        final ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, ex.getLocalizedMessage(), List.of("Acceso denegado: " + ex.getMessage()));
        ResponseApiDTO<Object> responseApiDTO = new ResponseApiDTO<>(null, apiError);
        return new ResponseEntity<>(responseApiDTO, new HttpHeaders(), apiError.status());
    }

}