package edonymyeon.backend.image.postimage.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.POST_IMAGE_COUNT_INVALID;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class PostImageInfos {

    public static final int MAX_IMAGE_COUNT = 10;
    public static final int MIN_IMAGE_COUNT = 0;

    @OneToMany(mappedBy = "post")
    private List<PostImageInfo> postImageInfos;

    private PostImageInfos(final List<PostImageInfo> postImageInfos) {
        checkImageCount(postImageInfos.size());
        this.postImageInfos = postImageInfos;
    }

    public static PostImageInfos create() {
        return new PostImageInfos(new ArrayList<>());
    }

    public static PostImageInfos of(final Post post, final List<ImageInfo> imageInfos) {
        final List<PostImageInfo> postImageInfos = imageInfos.stream()
                .map(imageInfo -> PostImageInfo.of(imageInfo, post))
                .toList();
        return new PostImageInfos(postImageInfos);
    }

    public void add(final PostImageInfo postImageInfo) {
        if (this.postImageInfos.contains(postImageInfo)) {
            return;
        }
        checkImageCount(this.postImageInfos.size());
        this.postImageInfos.add(postImageInfo);
    }

    public void update(final List<PostImageInfo> postImageInfos) {
        checkImageCount(postImageInfos.size());
        this.postImageInfos.clear();
        this.postImageInfos.addAll(postImageInfos);
    }

    public void checkImageCount(final Integer imageCount) {
        if (isInvalidImageCount(imageCount)) {
            throw new EdonymyeonException(POST_IMAGE_COUNT_INVALID);
        }
    }

    private boolean isInvalidImageCount(final Integer imageCount) {
        return imageCount > MAX_IMAGE_COUNT;
    }
}
