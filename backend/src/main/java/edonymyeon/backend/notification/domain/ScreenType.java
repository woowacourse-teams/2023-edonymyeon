package edonymyeon.backend.notification.domain;

/**
 * 알림을 통해 접속할 페이지의 종류를 나타내는 enum입니다.
 */
public enum ScreenType {
    /**
     * 알림을 클릭할 경우 특정 게시글의 상세 페이지로 이동합니다.
     */
    POST,

    /**
     * 알림을 클릭할 경우 내가 쓴 글 페이지로 이동합니다.
     */
    MYPOST
}
