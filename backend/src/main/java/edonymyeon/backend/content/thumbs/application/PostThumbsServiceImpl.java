package edonymyeon.backend.content.thumbs.application;

import edonymyeon.backend.content.thumbs.domain.AllThumbsInPost;
import edonymyeon.backend.content.thumbs.domain.Thumbs;
import edonymyeon.backend.content.thumbs.repository.ThumbsRepository;
import edonymyeon.backend.member.profile.application.dto.AnonymousMemberId;
import edonymyeon.backend.member.profile.application.dto.MemberId;
import edonymyeon.backend.content.post.application.PostThumbsService;
import edonymyeon.backend.content.post.application.dto.response.AllThumbsInPostResponse;
import edonymyeon.backend.content.post.application.dto.response.ThumbsStatusInPostResponse;
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

    @Override
    public AllThumbsInPostResponse findAllThumbsInPost(final Long postId) {
        AllThumbsInPost allThumbsInPost = AllThumbsInPost.from(thumbsRepository.findByPostId(postId));

        return new AllThumbsInPostResponse(allThumbsInPost.getUpCount(),
                allThumbsInPost.getDownCount());
    }

    @Override
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
    @Override
    public void deleteAllThumbsInPost(final Long postId) {
        thumbsRepository.deleteAllByPostId(postId);
    }
}
