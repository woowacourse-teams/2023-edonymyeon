package edonymyeon.backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionInformation {

    // 0번: 서버 내부 예상치 못한 오류
    // ___: 요청 오류
    REQUEST_PARAMETER_NOT_EXIST(1, "필수 쿼리 파라미터가 비었습니다."),
    REQUEST_FILE_SIZE_TOO_LARGE(2, "첨부 파일의 용량이 제한을 초과하였습니다."),

    // 클래스이름_필드명_틀린내용
    // 1___: 인증 관련
    MEMBER_EMAIL_NOT_FOUND(1511, "회원의 이메일이 존재하지 않습니다."),
    MEMBER_PASSWORD_NOT_MATCH(1512, "회원의 비밀번호가 일치하지 않습니다."),
    AUTHORIZATION_EMPTY(1523, "인증 정보가 없습니다."),
    ILLEGAL_ARGUMENT_TYPE(1524, "잘못된 타입의 요청입니다."),

    // 2___: 게시글 관련
    POST_ID_NOT_FOUND(2000, "존재하지 않는 게시글입니다."),
    POST_TITLE_ILLEGAL_LENGTH(2511, "제목은 1자 이상 30자 이하여야 합니다."),
    POST_CONTENT_ILLEGAL_LENGTH(2523, "내용은 0자 이상 1,000자 이하여야 합니다."),
    POST_PRICE_ILLEGAL_SIZE(2543, "가격은 0원 이상 100억 이하여야 합니다."),
    POST_MEMBER_EMPTY(2544, "게시글에는 작성자가 있어야 합니다."),
    POST_MEMBER_NOT_SAME(2666, "게시글 작성자가 아닙니다."),
    POST_IMAGE_COUNT_INVALID(2667, "게시글 하나에 이미지는 최대 10개까지 등록 가능합니다."),
    POST_INVALID_PAGINATION_CONDITION(2700, "유효하지 않은 게시글 조회 조건입니다."),

    // 3___: 회원 관련
    MEMBER_ID_NOT_FOUND(3000, "존재하지 않는 회원입니다."),
    MEMBER_EMAIL_INVALID(3001, "회원 이메일 정보가 잘못되었습니다."),
    MEMBER_PASSWORD_INVALID(3002, "회원 비밀번호가 잘못되었습니다."),
    MEMBER_NICKNAME_INVALID(3003, "회원 닉네임이 잘못되었습니다."),
    MEMBER_EMAIL_DUPLICATE(3004, "이미 존재하는 이메일입니다."),
    MEMBER_NICKNAME_DUPLICATE(3005, "이미 존재하는 닉네임입니다."),
    MEMBER_IS_DELETED(3006, "삭제된 회원입니다."),

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
    IMAGE_DOMAIN_INVALID(5001, "이미지의 url 경로가 잘못되었습니다."),
    IMAGE_STORE_NAME_INVALID(5002, "유효하지 않은 이미지 이름이 포함되어 있습니다."),

    // 6___: 소비, 절약 관련
    CONSUMPTION_POST_ID_ALREADY_EXIST(6000, "이미 소비, 절약 여부가 확정된 게시글입니다."),
    CONSUMPTION_PRICE_ILLEGAL_SIZE(6543, "가격은 0원 이상 100억 이하여야 합니다."),
    CONSUMPTION_YEAR_ILLEGAL(6544, "소비한 년도가 유효하지 않습니다."),
    CONSUMPTION_MONTH_ILLEGAL(6545, "소비한 달이 유효하지 않습니다."),
    CONSUMPTION_POST_ID_NOT_FOUND(6546, "소비 확정 내역이 존재하지 않습니다."),
    CONSUMPTION_YEAR_MONTH_ILLEGAL(6547, "소비 확정 년도, 달이 현재 시각보다 미래입니다."),
    CONSUMPTION_PERIOD_MONTH_ILLEGAL(6548, "해당 기간의 소비 금액은 조회할 수 없습니다."),

    // 9___: 어드민 페이지 관련
    LOGGING_TYPE_NOT_EXISTS(9001,"존재하지 않는 로그 타입입니다."),
    ABUSING_TYPE_NOT_FOUND(9100, "존재하지 않는 리포팅 분류입니다.");

    private int code;

    private String message;
}
