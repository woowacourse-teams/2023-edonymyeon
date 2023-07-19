package edonymyeon.backend.post.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostFindingCondition {

    public static final int DEFAULT_SIZE = 0;
    public static final int DEFAULT_LIMIT = 20;
    public static final SortBy DEFAULT_SORT_BY = SortBy.CREATE_AT;
    public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.DESC;

    @Builder.Default
    private final Integer page = DEFAULT_SIZE;

    @Builder.Default
    private final Integer size = DEFAULT_LIMIT;

    @Builder.Default
    private final SortBy sortBy = DEFAULT_SORT_BY;

    @Builder.Default
    private final SortDirection sortDirection = DEFAULT_SORT_DIRECTION;
}
