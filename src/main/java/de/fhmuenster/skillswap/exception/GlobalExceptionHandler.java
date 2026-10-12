package de.fhmuenster.skillswap.exception;

import de.fhmuenster.skillswap.dto.ExceptionResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(HolidayException.class)
    public ResponseEntity<ExceptionResponseDTO> handleHoliday(HolidayException ex, HttpServletRequest request)
    {

        ExceptionResponseDTO error = new ExceptionResponseDTO(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                "HOLIDAY_ERROR"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request)
    {
        ExceptionResponseDTO error = new ExceptionResponseDTO(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                "USER_NOT_FOUND"
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponseDTO> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        ExceptionResponseDTO error = new ExceptionResponseDTO(
                ex.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                "RESOURCE_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionResponseDTO> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        ExceptionResponseDTO error = new ExceptionResponseDTO(
                "Endpoint nicht gefunden: " + request.getRequestURI(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                "ENDPOINT_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDTO> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ExceptionResponseDTO error = new ExceptionResponseDTO(
                "Validierungsfehler: " + errors,
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                "VALIDATION_ERROR"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        assert ex.getRequiredType() != null;
        ExceptionResponseDTO error = new ExceptionResponseDTO(
                "Falscher Parameter-Typ: " + ex.getName() + " sollte " + ex.getRequiredType().getSimpleName() + " sein",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                "TYPE_MISMATCH"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponseDTO> handleJsonParseError(HttpMessageNotReadableException ex, HttpServletRequest request) {
        ExceptionResponseDTO error = new ExceptionResponseDTO(
                "Ungültiges JSON-Format",
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                "JSON_PARSE_ERROR"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(error);
    }

    // Falls bspw. ein User Admin-Endpunkte ansprechen möchte
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ExceptionResponseDTO> handleAccessDenied(Exception ex, HttpServletRequest request) {
        ExceptionResponseDTO error = new ExceptionResponseDTO(
                "Zugriff verweigert: Du hast nicht die nötigen Rechte.",
                HttpStatus.FORBIDDEN.value(),
                request.getRequestURI(),
                "ACCESS_DENIED"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDTO> handleGeneric(Exception ex, HttpServletRequest request) {

        ExceptionResponseDTO error = new ExceptionResponseDTO(
                "Interner Serverfehler: " + ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getRequestURI(),
                "INTERNAL_ERROR"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(error);
    }
}
