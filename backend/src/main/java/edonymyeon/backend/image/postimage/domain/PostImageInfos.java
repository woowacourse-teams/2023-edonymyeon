package edonymyeon.backend.image.postimage.domain;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.post.domain.Post;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_STORE_NAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_IMAGE_COUNT_INVALID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Embeddable
public class PostImageInfos {

    public static final int MAX_IMAGE_COUNT = 10;
    public static final int MIN_IMAGE_COUNT = 0;

    @BatchSize(size = Post.DEFAULT_BATCH_SIZE)
    @OneToMany(mappedBy = "post")
    private List<PostImageInfo> postImageInfos;

    private PostImageInfos(final List<PostImageInfo> postImageInfos) {
        validateImageCount(postImageInfos.size());
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

    public void validateImageCount(final int imageCount) {
        if (isInvalidImageCount(imageCount)) {
            throw new EdonymyeonException(POST_IMAGE_COUNT_INVALID);
        }
    }

    private boolean isInvalidImageCount(final Integer imageCount) {
        return imageCount > MAX_IMAGE_COUNT;
    }

    public List<Long> getImageIdsToDeleteBy(final List<String> remainedStoreNames, final List<PostImageInfo> newPostImageInfos) {
        final List<PostImageInfo> imagesToDelete = findImagesToDelete(remainedStoreNames);
        int updatedImageCount = this.postImageInfos.size() - imagesToDelete.size() + newPostImageInfos.size();
        validateImageCount(updatedImageCount);

        return imagesToDelete.stream()
                .map(ImageInfo::getId)
                .toList();
    }

    private List<PostImageInfo> findImagesToDelete(final List<String> remainedStoreNames) {
        final List<PostImageInfo> unmatchedPostImageInfos = this.postImageInfos.stream().
                filter(postImageInfo -> !remainedStoreNames.contains(postImageInfo.getStoreName()))
                .toList();

        if (remainedStoreNames.size() + unmatchedPostImageInfos.size() != this.postImageInfos.size()) {
            throw new EdonymyeonException(IMAGE_STORE_NAME_INVALID);
        }
        return unmatchedPostImageInfos;
    }

    public boolean isEmpty() {
        return this.postImageInfos.isEmpty();
    }

    public String getThumbnailName() {
        if(this.postImageInfos.isEmpty()) {
            return null;
        }
        return this.postImageInfos.get(0).getStoreName();
    }
}
