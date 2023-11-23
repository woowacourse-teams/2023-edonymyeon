package edonymyeon.backend.content.post.application;

import edonymyeon.backend.content.post.application.dto.response.AllThumbsInPostResponse;
import edonymyeon.backend.content.post.application.dto.response.ThumbsStatusInPostResponse;
import edonymyeon.backend.member.profile.application.dto.MemberId;

public interface PostThumbsService {

    AllThumbsInPostResponse findAllThumbsInPost(final Long postId);

    ThumbsStatusInPostResponse findThumbsStatusInPost(final MemberId memberId, final Long postId);

    void deleteAllThumbsInPost(final Long postId);
}
