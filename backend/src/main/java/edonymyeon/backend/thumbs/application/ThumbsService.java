package edonymyeon.backend.thumbs.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.THUMBS_POST_IS_LOGIN_MEMBER;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.domain.ThumbsType;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ThumbsService {

    private final ThumbsRepository thumbsRepository;

    // TODO: 나중에 dev 받아와서 서비스들로 고쳐주기
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    @Transactional
    public void thumbsUp(final MemberIdDto memberId, final Long postId) {
        Member loginMember = memberRepository.findById(memberId.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 게시글이 없습니다."));

        // TODO: equal재정의 되면 바꿔주기
        if (post.getMember().getId().equals(loginMember.getId())) {
            throw new EdonymyeonException(THUMBS_POST_IS_LOGIN_MEMBER);
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

    public AllThumbsInPostResponse allThumbsInPost(final Long postId) {
        AllThumbsInPost allThumbsInPost = new AllThumbsInPost(thumbsRepository.findByPostId(postId));

        return new AllThumbsInPostResponse(
                allThumbsInPost.getUpCount(),
                allThumbsInPost.getDownCount()
        );
    }
}
