package edonymyeon.backend.consumption.application;

import static edonymyeon.backend.consumption.domain.ConsumptionType.SAVING;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_POST_ID_ALREADY_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.CONSUMPTION_POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.domain.ConsumptionType;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.ConsumptionConfirmService;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ConsumptionConfirmServiceImpl implements ConsumptionConfirmService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final ConsumptionRepository consumptionRepository;

    @Override
    @Transactional
    public void confirm(
            final MemberIdDto memberIdDto,
            final Long postId,
            final Long purchasePrice,
            final YearMonthDto yearMonth
    ) {
        validateExistMemberById(memberIdDto.id());
        final Post post = findPostById(postId);
        post.validateWriter(memberIdDto.id());
        validateConsumptionExist(postId);

        ConsumptionType consumptionType = ConsumptionType.classifyConsumptionType(purchasePrice);

        final Consumption consumption = new Consumption(
                post,
                consumptionType,
                consumptionType == SAVING ? post.getPrice() : purchasePrice,
                yearMonth.year(),
                yearMonth.month()
        );
        consumptionRepository.save(consumption);
    }

    private void validateExistMemberById(final Long memberId) {
        if (memberRepository.existsById(memberId)) {
            return;
        }
        throw new EdonymyeonException(MEMBER_ID_NOT_FOUND);
    }

    private Post findPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
    }

    private void validateConsumptionExist(final Long postId) {
        if (consumptionRepository.existsByPostId(postId)) {
            throw new EdonymyeonException(CONSUMPTION_POST_ID_ALREADY_EXIST);
        }
    }

    @Override
    @Transactional
    public void removeConfirm(final MemberIdDto memberIdDto, final Long postId) {
        validateExistMemberById(memberIdDto.id());
        final Post post = findPostById(postId);
        post.validateWriter(memberIdDto.id());
        final Consumption consumption = findConsumptionByPostID(postId);
        consumptionRepository.delete(consumption);
    }

    private Consumption findConsumptionByPostID(final Long postId) {
        return consumptionRepository.findByPostId(postId)
                .orElseThrow(() -> new EdonymyeonException(CONSUMPTION_POST_ID_NOT_FOUND));
    }
}
