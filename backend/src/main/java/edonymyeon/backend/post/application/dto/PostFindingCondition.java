package edonymyeon.backend.post.application.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostFindingCondition {

    public static final int DEFAULT_LIMIT = 20;

    @Builder.Default
    private final Integer page = 0;

    @Builder.Default
    private final Integer size = DEFAULT_LIMIT;

    @Builder.Default
    private final SortBy sortBy = SortBy.CREATE_AT;

    @Builder.Default
    private final SortDirection sortDirection = SortDirection.DESC;
}
