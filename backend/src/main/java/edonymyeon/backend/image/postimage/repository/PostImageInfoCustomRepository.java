package edonymyeon.backend.image.postimage.repository;

import edonymyeon.backend.image.postimage.domain.PostImageInfo;

import java.util.List;

public interface PostImageInfoCustomRepository {

    void batchSave(final List<PostImageInfo> imageInfos, final Long postId);
}
