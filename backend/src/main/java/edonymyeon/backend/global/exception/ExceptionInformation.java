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
    POST_ID_NOT_FOUND(2000, "존재하지 않는 게시글입니다."),
    POST_TITLE_ILLEGAL_LENGTH(2511, "제목은 1자 이상 30자 이하여야 합니다."),
    POST_CONTENT_ILLEGAL_LENGTH(2523, "내용은 1자 이상 1,000자 이하여야 합니다."),
    POST_PRICE_ILLEGAL_SIZE(2543, "가격은 0원 이상 100억 이하여야 합니다."),
    POST_MEMBER_EMPTY(2544, "게시글에는 작성자가 있어야 합니다."),
    POST_MEMBER_FORBIDDEN(2666, "게시글 작성자만 게시글을 수정/삭제할 수 있습니다."),
    POST_IMAGE_COUNT_INVALID(2667, "게시글 하나에 이미지는 최대 10개까지 등록 가능합니다."),

    // 3___: 회원 관련
    MEMBER_ID_NOT_FOUND(3000, "존재하지 않는 회원입니다."),
    MEMBER_EMAIL_INVALID(3001, "회원 이메일 정보가 잘못되었습니다."),
    MEMBER_PASSWORD_INVALID(3002, "회원 비밀번호가 잘못되었습니다."),
    MEMBER_NICKNAME_INVALID(3003, "회원 닉네임이 잘못되었습니다."),

    // 4___: 추천 관련
    THUMBS_UP_ALREADY_EXIST(4000, "이미 추천된 게시글 입니다."),
    THUMBS_DOWN_ALREADY_EXIST(4001, "이미 비추천된 게시글 입니다."),
    THUMBS_IS_SELF_UP_DOWN(4002, "본인의 게시글을 추천/비추천하거나 추천/비추천 취소할 수 없습니다."),
    THUMBS_UP_IS_NOT_EXIST(4003, "추천하지 않은 게시글의 추천을 취소할 수 없습니다."),
    THUMBS_DOWN_IS_NOT_EXIST(4004, "비추천하지 않은 게시글의 비추천을 취소할 수 없습니다."),
    THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN(4005, "비추천 한 게시글의 추천을 취소할 수 없습니다."),
    THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP(4006, "추천 한 게시글의 비추천을 취소할 수 없습니다."),

    // 5___: 이미지 관련
    IMAGE_EXTENSION_INVALID(5000, "등록할 수 없는 이미지 확장자입니다."),
    IMAGE_DOMAIN_INVALID(50001, "이미지의 url 경로가 잘못되었습니다."),
    IMAGE_STORE_NAME_INVALID(5002, "유효하지 않은 이미지 이름이 포함되어 있습니다.");

    private int code;

    private String message;
}
