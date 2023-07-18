package edonymyeon.backend.post.application.dto;

import lombok.Builder;

@Builder
public class PostFindingCondition {

    public static final int DEFAULT_LIMIT = 20;

    @Builder.Default
    private final Integer page = 0;

    @Builder.Default
    private final Integer limit = DEFAULT_LIMIT;

    @Builder.Default
    private final SortBy sortBy = SortBy.CREATION_DATE;

    @Builder.Default
    private final SortDirection sortDirection = SortDirection.DESC;
}
