package edonymyeon.backend.content.post.application;

import edonymyeon.backend.content.post.application.dto.response.PostConsumptionResponse;
import java.util.List;
import java.util.Map;

public interface PostConsumptionService {

    Map<Long, PostConsumptionResponse> findConsumptionsByPostIds(List<Long> postIds);
}
