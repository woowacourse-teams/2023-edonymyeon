package edonymyeon.backend.post.application.dto;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import java.util.Arrays;

public enum SortDirection {
    ASC("asc"),
    DESC("desc");

    private final String name;

    SortDirection(final String name) {
        this.name = name;
    }

    public static SortDirection of(final String name) {
        return Arrays.stream(SortDirection.values())
                .filter(sortDirection -> sortDirection.name.equalsIgnoreCase(name))
                .findAny()
                .orElseThrow(() -> new EdonymyeonException(ExceptionInformation.POST_INVALID_PAGINATION_CONDITION));
    }
}
