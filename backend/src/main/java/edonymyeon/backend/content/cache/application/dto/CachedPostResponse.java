package edonymyeon.backend.content.cache.application.dto;

import java.util.List;

public record CachedPostResponse(List<Long> postIds, boolean isLast) {

    public int size(){
        return postIds.size();
    }
}
