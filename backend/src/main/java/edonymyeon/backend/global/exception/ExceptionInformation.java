package edonymyeon.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionInformation {

    // 클래스이름_필드명_틀린내용
    // 1___: 인증 관련
    MEMBER_EMAIL_NOT_FOUND(1511, "회원의 이메일이 존재하지 않습니다."),
    AUTHORIZATION_EMPTY(1523, "인증 정보가 없습니다."),

    // 2___: 게시글 관련
    POST_TITLE_ILLEGAL_LENGTH(2511, "제목은 1자 이상 30자 이하여야 합니다."),
    POST_CONTENT_ILLEGAL_LENGTH(2523, "내용은 1자 이상 1,000자 이하여야 합니다."),
    POST_PRICE_ILLEGAL_SIZE(2543, "가격은 0원 이상 100억 이하여야 합니다."),
    POST_MEMBER_EMPTY(2544, "게시글에는 작성자가 있어야 합니다."),

    // 3___: 회원 관련
    MEMBER_ID_NOT_FOUND(3000, "존재하지 않는 회원입니다."),

    // 4___: 추천 관련
    THUMBS_UP_ALREADY_EXIST(4000, "이미 추천된 게시글 입니다"),
    THUMBS_POST_IS_LOGIN_MEMBER(4001, "본인의 게시글을 추천/비추천 할 수 없습니다");

    private int code;

    private String message;
}