package edonymyeon.backend.member.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;

import edonymyeon.backend.consumption.domain.Consumption;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.application.dto.response.MyPageResponse;
import edonymyeon.backend.member.application.dto.response.MyPostResponse;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    private final ConsumptionConfirmService consumptionConfirmService;

    private final ConsumptionRepository consumptionRepository;

    private final Domain domain;

    public MyPageResponse findMemberInfoById(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
        return new MyPageResponse(member.getId(), member.getNickname());
    }

    public Slice<MyPostResponse> findMyPosts(final MemberIdDto memberIdDto, Pageable pageable) {
        final Slice<Post> posts = postRepository.findAllByMemberId(memberIdDto.id(), pageable);
        final List<Long> postIds = posts.map(Post::getId)
                .toList();

        final List<Consumption> consumptions = consumptionRepository.findAllByPostIds(postIds);

        final Map<Long, Consumption> consumptionsByPostId = consumptions.stream()
                .collect(Collectors.toMap(consumption -> consumption.getPost().getId(), Function.identity()));

        return posts.map(post -> {
            if (consumptionsByPostId.containsKey(post.getId())) {
                final Consumption consumption = consumptionsByPostId.get(post.getId());
                return MyPostResponse.of(post, domain, consumption);
            }
            return MyPostResponse.of(post, domain);
        });
    }

    public void confirmPurchase(
            final MemberIdDto memberIdDto,
            final Long postId,
            final PurchaseConfirmRequest purchaseConfirmRequest
    ) {
        final YearMonthDto yearMonthDto = new YearMonthDto(purchaseConfirmRequest.year(),
                purchaseConfirmRequest.month());
        consumptionConfirmService.confirmPurchase(memberIdDto, postId, purchaseConfirmRequest.purchasePrice(),
                yearMonthDto);
    }

    public void confirmSaving(
            final MemberIdDto memberIdDto,
            final Long postId,
            final SavingConfirmRequest savingConfirmRequest) {
        final YearMonthDto yearMonthDto = new YearMonthDto(savingConfirmRequest.year(),
                savingConfirmRequest.month());
        consumptionConfirmService.confirmSaving(memberIdDto, postId, yearMonthDto);
    }

    public void removeConfirm(final MemberIdDto memberIdDto, final Long postId) {
        consumptionConfirmService.removeConfirm(memberIdDto, postId);
    }
}
