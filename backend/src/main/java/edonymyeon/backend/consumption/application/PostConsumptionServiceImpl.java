package edonymyeon.backend.consumption.application;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.content.post.application.PostConsumptionService;
import edonymyeon.backend.content.post.application.dto.response.PostConsumptionResponse;
import edonymyeon.backend.content.post.application.event.PostDeletionEvent;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostConsumptionServiceImpl implements PostConsumptionService {

    private final ConsumptionRepository consumptionRepository;

    @Override
    public Map<Long, PostConsumptionResponse> findConsumptionsByPostIds(List<Long> postIds) {
        final Map<Long, Consumption> consumptionsByPostIds = findAllByPostIds(postIds);
        return postIds.stream()
                .collect(toMap(identity(), mapConsumptionResponseByPostId(consumptionsByPostIds)));
    }

    private Map<Long, Consumption> findAllByPostIds(final List<Long> postIds) {
        final List<Consumption> consumptions = consumptionRepository.findAllByPostIds(postIds);

        return consumptions.stream()
                .collect(toMap(consumption -> consumption.getPost().getId(), identity()));
    }

    private Function<Long, PostConsumptionResponse> mapConsumptionResponseByPostId(
            final Map<Long, Consumption> consumptionsByPostIds) {
        return postId -> {
            if (consumptionsByPostIds.containsKey(postId)) {
                return from(consumptionsByPostIds.get(postId));
            }
            return PostConsumptionResponse.none();
        };
    }

    private PostConsumptionResponse from(Consumption consumption) {
        return new PostConsumptionResponse(
                consumption.getConsumptionType().name(),
                consumption.getPrice(),
                consumption.getConsumptionYear(),
                consumption.getConsumptionMonth()
        );
    }

    @Transactional
    @TransactionalEventListener(classes = {PostDeletionEvent.class}, phase = TransactionPhase.BEFORE_COMMIT)
    public void deleteConsumptionByPostId(final PostDeletionEvent postDeletionEvent) {
        consumptionRepository.deleteByPostId(postDeletionEvent.postId());
    }
}
