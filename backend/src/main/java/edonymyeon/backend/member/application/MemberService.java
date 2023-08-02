package edonymyeon.backend.member.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import edonymyeon.backend.consumption.domain.Consumption;
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
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.domain.Post;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberPostService memberPostService;

    private final MemberConsumptionService memberConsumptionService;

    private final Domain domain;

    public MyPageResponse findMemberInfoById(final Long id) {
        final Member member = memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
        return new MyPageResponse(member.getId(), member.getNickname());
    }

    public Slice<MyPostResponse> findMyPosts(final MemberIdDto memberIdDto,
                                             GeneralFindingCondition findingCondition) {
        final Slice<Post> posts = memberPostService.findAllByMemberId(memberIdDto.id(), findingCondition.toPage());
        final List<Long> postIds = posts.map(Post::getId).toList();

        final List<Consumption> consumptions = memberConsumptionService.findAllByPostIds(postIds);

        final Map<Long, Consumption> consumptionsByPostId = consumptions.stream()
                .collect(toMap(consumption -> consumption.getPost().getId(), identity()));

        return posts.map(post -> {
            if (consumptionsByPostId.containsKey(post.getId())) {
                final Consumption consumption = consumptionsByPostId.get(post.getId());
                return MyPostResponse.of(post, domain, consumption);
            }
            return MyPostResponse.of(post, domain);
        });
    }

    @Transactional
    public void confirmPurchase(
            final MemberIdDto memberIdDto,
            final Long postId,
            final PurchaseConfirmRequest purchaseConfirmRequest
    ) {
        final YearMonthDto yearMonthDto = new YearMonthDto(purchaseConfirmRequest.year(),
                purchaseConfirmRequest.month());
        memberConsumptionService.confirmPurchase(memberIdDto, postId, purchaseConfirmRequest.purchasePrice(),
                yearMonthDto);
    }

    @Transactional
    public void confirmSaving(
            final MemberIdDto memberIdDto,
            final Long postId,
            final SavingConfirmRequest savingConfirmRequest) {
        final YearMonthDto yearMonthDto = new YearMonthDto(savingConfirmRequest.year(),
                savingConfirmRequest.month());
        memberConsumptionService.confirmSaving(memberIdDto, postId, yearMonthDto);
    }

    @Transactional
    public void removeConfirm(final MemberIdDto memberIdDto, final Long postId) {
        memberConsumptionService.removeConfirm(memberIdDto, postId);
    }
}
