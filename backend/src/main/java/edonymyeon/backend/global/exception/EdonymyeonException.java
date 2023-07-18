package edonymyeon.backend.global.exception;

import lombok.Getter;

@Getter
public class EdonymyeonException extends RuntimeException {

    private final ExceptionInformation exceptionInformation;

    public EdonymyeonException(ExceptionInformation exceptionInformation) {
        super();
        this.exceptionInformation = exceptionInformation;
    }

    public int getCode() {
        return exceptionInformation.getCode();
    }

    @Override
    public String getMessage() {
        return exceptionInformation.getMessage();
    }
}
