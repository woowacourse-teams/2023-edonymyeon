package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_DOWN_IS_NOT_EXIST;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_IS_SELF_UP_DOWN;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_UP_IS_NOT_EXIST;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.application.event.ThumbsUpEvent;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ThumbsService {

    private final ApplicationEventPublisher publisher;

    private final ThumbsRepository thumbsRepository;

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    @Transactional
    public void thumbsUp(final MemberId memberId, final Long postId) {
        Member loginMember = findMemberById(memberId);
        Post post = findPostById(postId);
        checkPostWriter(post, loginMember);

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postId, loginMember.getId());
        if (postThumbs.isEmpty()) {
            Thumbs thumbs = new Thumbs(post, loginMember, ThumbsType.UP);
            thumbsRepository.save(thumbs);
            publisher.publishEvent(new ThumbsUpEvent());
            return;
        }

        Thumbs thumbs = postThumbs.get();
        thumbs.up();

        publisher.publishEvent(new ThumbsUpEvent());
    }

    private Member findMemberById(final MemberId memberId) {
        return memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }

    private Post findPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
    }

    private void checkPostWriter(final Post post, final Member loginMember) {
        if (post.isSameMember(loginMember)) {
            throw new EdonymyeonException(THUMBS_IS_SELF_UP_DOWN);
        }
    }

    @Transactional
    public void thumbsDown(final MemberId memberId, final Long postId) {
        Member loginMember = findMemberById(memberId);
        Post post = findPostById(postId);
        checkPostWriter(post, loginMember);

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postId, loginMember.getId());
        if (postThumbs.isEmpty()) {
            Thumbs thumbs = new Thumbs(post, loginMember, ThumbsType.DOWN);
            thumbsRepository.save(thumbs);
            return;
        }

        Thumbs thumbs = postThumbs.get();
        thumbs.down();
    }

    @Transactional
    public void deleteThumbsUp(final MemberId memberId, final Long postId) {
        Member loginMember = findMemberById(memberId);
        Post post = findPostById(postId);
        checkPostWriter(post, loginMember);

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postId, loginMember.getId());
        if (postThumbs.isEmpty()) {
            throw new EdonymyeonException(THUMBS_UP_IS_NOT_EXIST);
        }

        Thumbs thumbs = postThumbs.get();
        if (thumbs.isDown()) {
            throw new EdonymyeonException(THUMBS_UP_DELETE_FAIL_WHEN_THUMBS_DOWN);
        }
        thumbsRepository.delete(thumbs);
    }

    @Transactional
    public void deleteThumbsDown(final MemberId memberId, final Long postId) {
        Member loginMember = findMemberById(memberId);
        Post post = findPostById(postId);
        checkPostWriter(post, loginMember);

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postId, loginMember.getId());
        if (postThumbs.isEmpty()) {
            throw new EdonymyeonException(THUMBS_DOWN_IS_NOT_EXIST);
        }

        Thumbs thumbs = postThumbs.get();
        if (thumbs.isUp()) {
            throw new EdonymyeonException(THUMBS_DOWN_DELETE_FAIL_WHEN_THUMBS_UP);
        }
        thumbsRepository.delete(thumbs);
    }
}
