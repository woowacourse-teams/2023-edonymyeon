package edonymyeon.backend.content.post.postimage.repository;

import edonymyeon.backend.content.post.postimage.domain.PostImageInfo;

import java.util.List;

public interface PostImageInfoCustomRepository {

    void batchSave(final List<PostImageInfo> imageInfos, final Long postId);
}
