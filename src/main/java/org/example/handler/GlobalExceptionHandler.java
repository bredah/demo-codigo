
package org.example.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.example.dto.ErrorResponse;
import org.example.exception.MensagemNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex) {
    List<String> errors = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getDefaultMessage());
    }
    var errorResponse =
        new ErrorResponse("Validation error", errors);
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorResponse);
  }

  @ExceptionHandler(MensagemNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleMensagemExistenteException(
      MensagemNotFoundException ex) {
    var errorResponse = new ErrorResponse("requição apresenta erro",
        Collections.singletonList(ex.getMessage()));

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }
}
