package edonymyeon.backend.thumbs.application;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.application.event.ThumbsDownEvent;
import edonymyeon.backend.thumbs.application.event.ThumbsUpEvent;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static edonymyeon.backend.global.exception.ExceptionInformation.*;

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
            save(thumbs, THUMBS_UP_ALREADY_EXIST);
            publisher.publishEvent(new ThumbsUpEvent(post));
            return;
        }

        Thumbs thumbs = postThumbs.get();
        thumbs.up();

        publisher.publishEvent(new ThumbsUpEvent(post));
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

    private void save(final Thumbs thumbs, final ExceptionInformation thumbsUpAlreadyExist) {
        try {
            thumbsRepository.save(thumbs);
        } catch (DataIntegrityViolationException e) {
            throw new EdonymyeonException(thumbsUpAlreadyExist);
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
            save(thumbs, THUMBS_DOWN_ALREADY_EXIST);
            publisher.publishEvent(new ThumbsDownEvent(post));
            return;
        }

        Thumbs thumbs = postThumbs.get();
        thumbs.down();

        publisher.publishEvent(new ThumbsDownEvent(post));
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

    public Long countReactions(final Long postId) {
        return thumbsRepository.countByPostId(postId);
    }
}
