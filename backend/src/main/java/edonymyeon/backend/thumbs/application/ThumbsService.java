package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_POST_IS_SELF_UP_DOWN;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.domain.AllThumbsInPost;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.dto.ThumbsStatusInPostResponse;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ThumbsService {

    private final ThumbsRepository thumbsRepository;

    private final MemberRepository memberRepository;

    private final PostRepository postRepository;

    public AllThumbsInPostResponse findAllThumbsInPost(final Long postId) {
        AllThumbsInPost allThumbsInPost = AllThumbsInPost.from(thumbsRepository.findByPostId(postId));

        return new AllThumbsInPostResponse(allThumbsInPost.getUpCount(),
                allThumbsInPost.getDownCount());
    }

    public ThumbsStatusInPostResponse findThumbsStatusInPost(final MemberIdDto memberId, final Long postId){
        if(Objects.isNull(memberId.id())){
            return new ThumbsStatusInPostResponse(false, false);
        }

        Optional<Thumbs> thumbsInPost = thumbsRepository.findByPostIdAndMemberId(postId, memberId.id());
        if(thumbsInPost.isEmpty()){
            return new ThumbsStatusInPostResponse(false, false);
        }

        Thumbs thumbs = thumbsInPost.get();
        return new ThumbsStatusInPostResponse(thumbs.isUp(), thumbs.isDown());
    }

    @Transactional
    public void thumbsUp(final MemberIdDto memberId, final Long postId) {
        Member loginMember = findMemberById(memberId);
        Post post = findPostById(postId);

        if (post.getMember().equals(loginMember)) {
            throw new EdonymyeonException(THUMBS_POST_IS_SELF_UP_DOWN);
        }

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postId, loginMember.getId());

        if (postThumbs.isEmpty()) {
            Thumbs thumbs = new Thumbs(post, loginMember, ThumbsType.UP);
            thumbsRepository.save(thumbs);
            return;
        }

        Thumbs thumbs = postThumbs.get();
        thumbs.up();
    }

    private Member findMemberById(final MemberIdDto memberId) {
        return memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }

    private Post findPostById(final Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
    }

    @Transactional
    public void thumbsDown(final MemberIdDto memberId, final Long postId) {
        Member loginMember = findMemberById(memberId);
        Post post = findPostById(postId);

        if (post.getMember().equals(loginMember)) {
            throw new EdonymyeonException(THUMBS_POST_IS_SELF_UP_DOWN);
        }

        Optional<Thumbs> postThumbs = thumbsRepository.findByPostIdAndMemberId(postId, loginMember.getId());

        if (postThumbs.isEmpty()) {
            Thumbs thumbs = new Thumbs(post, loginMember, ThumbsType.DOWN);
            thumbsRepository.save(thumbs);
            return;
        }

        Thumbs thumbs = postThumbs.get();
        thumbs.down();
    }
}
