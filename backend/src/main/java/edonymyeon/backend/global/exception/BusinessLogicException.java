package edonymyeon.backend.global.exception;

public class BusinessLogicException extends RuntimeException {

    private final ExceptionInformation exceptionInformation;

    public BusinessLogicException(ExceptionInformation exceptionInformation) {
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
