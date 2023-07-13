package edonymyeon.backend.domain.exception;

public enum ExceptionCode {

    // 클래스이름_필드명_틀린내용
    POST_TITLE_ILLEGAL_LENGTH(1511, "제목은 1자 이상 30자 이하여야 합니다."),
    POST_CONTENT_ILLEGAL_LENGTH(1523, "내용은 1자 이상 1,000자 이하여야 합니다."),
    POST_PRICE_ILLEGAL_SIZE(1543, "가격은 0원 이상 100억 이하여야 합니다.");

    private int code;
    private String message;

    ExceptionCode(final int code, final String message) {
        this.code = code;
        this.message = message;
    }
}
