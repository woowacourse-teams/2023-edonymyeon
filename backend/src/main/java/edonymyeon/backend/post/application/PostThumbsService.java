package edonymyeon.backend.post.application;

import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.dto.response.AllThumbsInPostResponse;
import edonymyeon.backend.post.application.dto.response.ThumbsStatusInPostResponse;

public interface PostThumbsService {

    AllThumbsInPostResponse findAllThumbsInPost(final Long postId);

    ThumbsStatusInPostResponse findThumbsStatusInPost(final MemberId memberId, final Long postId);

    void deleteAllThumbsInPost(final Long postId);
}
