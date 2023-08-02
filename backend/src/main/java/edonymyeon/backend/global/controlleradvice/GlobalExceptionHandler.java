package edonymyeon.backend.global.controlleradvice;

import static edonymyeon.backend.global.exception.ExceptionInformation.AUTHORIZATION_EMPTY;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_NOT_SAME;
import static edonymyeon.backend.global.exception.ExceptionInformation.REQUEST_PARAMETER_NOT_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.SERVER_ERROR_CONSUMPTIONS_NULL;
import static edonymyeon.backend.global.exception.ExceptionInformation.SERVER_ERROR_CONSUMPTION_MONTH_NOT_SAME;
import static edonymyeon.backend.global.exception.ExceptionInformation.SERVER_ERROR_CONSUMPTION_YEAR_NOT_SAME;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import edonymyeon.backend.global.controlleradvice.dto.ExceptionResponse;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.EnumMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
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
        exceptionInfoToHttpStatus.put(POST_MEMBER_NOT_SAME, FORBIDDEN);
        // todo: 아래의 예외 상황은 비즈니스 로직이 이상한 것이므로 예외 발생 시 500 에러로 처리하였음
        exceptionInfoToHttpStatus.put(SERVER_ERROR_CONSUMPTIONS_NULL, INTERNAL_SERVER_ERROR);
        exceptionInfoToHttpStatus.put(SERVER_ERROR_CONSUMPTION_MONTH_NOT_SAME, INTERNAL_SERVER_ERROR);
        exceptionInfoToHttpStatus.put(SERVER_ERROR_CONSUMPTION_YEAR_NOT_SAME, INTERNAL_SERVER_ERROR);
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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        final ExceptionResponse exceptionResponse = new ExceptionResponse(REQUEST_PARAMETER_NOT_EXIST.getCode(),
                REQUEST_PARAMETER_NOT_EXIST.getMessage());

        return ResponseEntity.status(BAD_REQUEST)
                .body(exceptionResponse);
    }
}
