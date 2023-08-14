package edonymyeon.backend.post.application;

import edonymyeon.backend.post.application.dto.response.PostConsumptionResponse;
import java.util.List;
import java.util.Map;

public interface PostConsumptionService {

    Map<Long, PostConsumptionResponse> findConsumptionsByPostIds(List<Long> postIds);

    void deleteConsumptionByPostId(long postId);
}
