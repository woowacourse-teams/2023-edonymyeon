package edonymyeon.backend.post.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_INVALID_PAGINATION_CONDITION;

/**
 * 핫 게시글의 목록을 조회하는 정책
 * page 와 size 정보를 받는다.
 */
@Builder
@Getter
public class HotFindingCondition {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 5;

    @Builder.Default
    private final Integer page = DEFAULT_PAGE;

    @Builder.Default
    private final Integer size = DEFAULT_SIZE;

    /**
     *
     * @param page 페이지 인덱스 (기본 값은 0)
     * @param size 페이지 당 사이즈 (기본 값은 5)
     * @return HotFindingCondition
     */
    public static HotFindingCondition of(
            final Integer page,
            final Integer size
    ) {
        return HotFindingCondition.builder()
                .page(Objects.isNull(page) ? HotFindingCondition.DEFAULT_PAGE : page)
                .size(Objects.isNull(size) ? HotFindingCondition.DEFAULT_SIZE : size)
                .build();
    }

    /**
     * 페이징 조건을 토대로 pageRequest 객체를 만든다
     * 조건에 맞지 않는 파라미터라면 2700 오류를 내려보낸다
     *
     * @return PageRequest
     */
    public Pageable toPage() {
        try {
            return PageRequest.of(
                    page,
                    size
            );
        } catch (IllegalArgumentException e) {
            throw new EdonymyeonException(POST_INVALID_PAGINATION_CONDITION);
        }
    }
}
