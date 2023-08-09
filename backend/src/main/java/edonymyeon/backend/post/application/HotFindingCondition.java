package edonymyeon.backend.post.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_INVALID_PAGINATION_CONDITION;

@Builder
@Getter
public class HotFindingCondition {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 5;

    @Builder.Default
    private final Integer page = DEFAULT_PAGE;

    @Builder.Default
    private final Integer size = DEFAULT_SIZE;

    public static HotFindingCondition of(
            final Integer page,
            final Integer size
    ) {
        return HotFindingCondition.builder()
                .page(Objects.isNull(page) ? HotFindingCondition.DEFAULT_PAGE : page)
                .size(Objects.isNull(size) ? HotFindingCondition.DEFAULT_SIZE : size)
                .build();
    }

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
