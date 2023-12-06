package edonymyeon.backend.content.post.application;

import edonymyeon.backend.content.post.application.dto.SortBy;
import edonymyeon.backend.content.post.application.dto.SortDirection;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.content.post.domain.Post;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

import java.util.Objects;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_INVALID_PAGINATION_CONDITION;

/**
 * 보통의 게시글 목록을 조회하는 정책
 * page, size, sort 기준, sort 순서 조건을 입력 받는다
 */
@Builder
@Getter
public class GeneralFindingCondition {

    public static final int DEFAULT_PAGE = 0;
    public static final SortBy DEFAULT_SORT_BY = SortBy.CREATE_AT;
    public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.DESC;

    @Builder.Default
    private final Integer page = DEFAULT_PAGE;

    @Builder.Default
    private final Integer size = Post.DEFAULT_BATCH_SIZE;

    @Builder.Default
    private final SortBy sortBy = DEFAULT_SORT_BY;

    @Builder.Default
    private final SortDirection sortDirection = DEFAULT_SORT_DIRECTION;

    /**
     *
     * @param page 페이지 인덱스 (기본 값은 0)
     * @param size 페이지 당 사이즈 (기본 값은 20)
     * @param sortBy 페이지 정렬 조건 (기본 값은 작성일)
     * @param sortDirection 페이지 정렬 순서 (기본 값은 최신순)
     * @return GeneralFindingCondition
     */
    public static GeneralFindingCondition of(
            final Integer page,
            final Integer size,
            final String sortBy,
            final String sortDirection
    ) {
        return builder()
                .page(Objects.isNull(page) ? GeneralFindingCondition.DEFAULT_PAGE : page)
                .size(Objects.isNull(size) ? Post.DEFAULT_BATCH_SIZE : size)
                .sortBy(Objects.isNull(sortBy) ? GeneralFindingCondition.DEFAULT_SORT_BY : SortBy.of(sortBy))
                .sortDirection(Objects.isNull(sortDirection) ? GeneralFindingCondition.DEFAULT_SORT_DIRECTION
                        : SortDirection.of(sortDirection))
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
                    size,
                    Direction.fromString(sortDirection.name()),
                    sortBy.getName()
            );
        } catch (IllegalArgumentException e) {
            throw new EdonymyeonException(POST_INVALID_PAGINATION_CONDITION);
        }
    }
}
