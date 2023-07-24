package edonymyeon.backend.image.postimage.domain;

import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.post.domain.Post;
import java.util.List;
import lombok.Getter;

@Getter
public class PostImageInfos {

    private final List<PostImageInfo> postImageInfos;

    public PostImageInfos(final List<PostImageInfo> postImageInfos) {
        this.postImageInfos = postImageInfos;
    }

    public static PostImageInfos of(final Post post, final List<ImageInfo> imageInfos) {
        final List<PostImageInfo> postImageInfos = imageInfos.stream()
                .map(imageInfo -> PostImageInfo.of(imageInfo, post))
                .toList();
        return new PostImageInfos(postImageInfos);
    }
}
