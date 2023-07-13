package edonymyeon.backend.domain.exception;

public class EdonymyeonException extends RuntimeException{

    private final ExceptionCode exceptionCode;

    public EdonymyeonException(ExceptionCode exceptionCode){
        super();
        this.exceptionCode = exceptionCode;
    }
}
