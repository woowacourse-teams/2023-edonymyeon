package edonymyeon.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class PostImageInfo extends ImageInfo {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public PostImageInfo(final String fileDirectory, final String storeName, final Post post) {
        super(fileDirectory, storeName);
        this.post = post;
    }

    public static PostImageInfo of(final ImageInfo imageInfo, final Post post) {
        final PostImageInfo postImageInfo = new PostImageInfo(imageInfo.fileDirectory, imageInfo.storeName, post);
        post.addPostImageInfo(postImageInfo);
        return postImageInfo;
    }
}
