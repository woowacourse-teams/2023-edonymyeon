package edonymyeon.backend.controlleradvice;

import edonymyeon.backend.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.domain.exception.EdonymyeonException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception e) {
        final ExceptionResponse exceptionResponse = new ExceptionResponse(0, "알지 못하는 예외 발생");
        log.error("알지 못하는 예외 발생", e);

        return ResponseEntity.internalServerError()
                .body(exceptionResponse);
    }

    @ExceptionHandler(EdonymyeonException.class)
    public ResponseEntity<ExceptionResponse> handleEdonymyeonException(final EdonymyeonException e) {
        final ExceptionResponse exceptionResponse = new ExceptionResponse(e.getCode(), e.getMessage());

        return ResponseEntity.badRequest()
                .body(exceptionResponse);
        // TODO: BadRequest 외의 예외가 될 수도 있음
    }
}
