package edonymyeon.backend.global.controlleradvice;

import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_FORBIDDEN;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.EnumMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final EnumMap<ExceptionInformation, HttpStatus> exceptionInfoToHttpStatus = new EnumMap<>(
            ExceptionInformation.class);

    public GlobalExceptionHandler() {
        exceptionInfoToHttpStatus.put(MEMBER_EMAIL_NOT_FOUND, UNAUTHORIZED);
        exceptionInfoToHttpStatus.put(AUTHORIZATION_EMPTY, UNAUTHORIZED);
        exceptionInfoToHttpStatus.put(POST_MEMBER_FORBIDDEN, FORBIDDEN);
    }

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

        final HttpStatus httpStatus = exceptionInfoToHttpStatus.getOrDefault(e.getExceptionInformation(), BAD_REQUEST);

        return ResponseEntity.status(httpStatus)
                .body(exceptionResponse);
    }
}
