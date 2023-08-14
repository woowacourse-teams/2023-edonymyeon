package edonymyeon.backend.cache.application.dto;

import java.util.List;

public record CachedPostResponse(List<Long> postIds, boolean isLast) {
}
