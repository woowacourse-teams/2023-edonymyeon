package edonymyeon.backend.content.post.application.dto;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.Arrays;

public enum SortBy {
    CREATE_AT("createdAt");

    private final String name;

    SortBy(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static SortBy of(String name) {
        return Arrays.stream(SortBy.values())
                .filter(sortBy -> sortBy.name.equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.POST_INVALID_PAGINATION_CONDITION));
    }
}
