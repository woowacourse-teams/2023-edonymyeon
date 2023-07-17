package edonymyeon.backend.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionCode {

    // 클래스이름_필드명_틀린내용
    // 1___: 인증 관련
    MEMBER_EMAIL_NOT_FOUND(1511, "회원의 이메일이 존재하지 않습니다."),
    AUTHORIZATION_EMPTY(1523, "인증 정보가 없습니다."),

    // 2___: 게시글 관련
    POST_TITLE_ILLEGAL_LENGTH(2511, "제목은 1자 이상 30자 이하여야 합니다."),
    POST_CONTENT_ILLEGAL_LENGTH(2523, "내용은 1자 이상 1,000자 이하여야 합니다."),
    POST_PRICE_ILLEGAL_SIZE(2543, "가격은 0원 이상 100억 이하여야 합니다.");

    private int code;
    private String message;
}
