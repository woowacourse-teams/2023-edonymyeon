package edonymyeon.backend.thumbs.application;

import edonymyeon.backend.member.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.PostThumbsService;
import edonymyeon.backend.post.application.dto.AllThumbsInPostResponse;
import edonymyeon.backend.post.application.dto.ThumbsStatusInPostResponse;
import edonymyeon.backend.thumbs.domain.AllThumbsInPost;
import edonymyeon.backend.thumbs.domain.Thumbs;
import edonymyeon.backend.thumbs.repository.ThumbsRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostThumbsServiceImpl implements PostThumbsService {

    private final ThumbsRepository thumbsRepository;

    public AllThumbsInPostResponse findAllThumbsInPost(final Long postId) {
        AllThumbsInPost allThumbsInPost = AllThumbsInPost.from(thumbsRepository.findByPostId(postId));

        return new AllThumbsInPostResponse(allThumbsInPost.getUpCount(),
                allThumbsInPost.getDownCount());
    }

    public ThumbsStatusInPostResponse findThumbsStatusInPost(final MemberId memberId, final Long postId) {
        if (Objects.equals(memberId.id(), AnonymousMemberId.ANONYMOUS_MEMBER_ID)) {
            return new ThumbsStatusInPostResponse(false, false);
        }

        Optional<Thumbs> thumbsInPost = thumbsRepository.findByPostIdAndMemberId(postId, memberId.id());
        if (thumbsInPost.isEmpty()) {
            return new ThumbsStatusInPostResponse(false, false);
        }

        Thumbs thumbs = thumbsInPost.get();
        return new ThumbsStatusInPostResponse(thumbs.isUp(), thumbs.isDown());
    }

    @Transactional
    public void deleteAllThumbsInPost(final Long postId) {
        thumbsRepository.deleteAllByPostId(postId);
    }
}
