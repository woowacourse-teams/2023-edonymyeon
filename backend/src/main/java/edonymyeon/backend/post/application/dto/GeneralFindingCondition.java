package edonymyeon.backend.post.application.dto;

import edonymyeon.backend.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

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
}
