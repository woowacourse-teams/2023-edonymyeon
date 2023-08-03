package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_INVALID_PAGINATION_CONDITION;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.post.application.dto.SortBy;
import edonymyeon.backend.post.application.dto.SortDirection;
import edonymyeon.backend.post.domain.Post;
import java.util.Objects;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

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
