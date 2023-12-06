package edonymyeon.backend.content.post.postimage.domain;

import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.content.post.domain.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted = false")
@Entity
public class PostImageInfo extends ImageInfo {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    @ColumnDefault("false")
    private boolean deleted;

    public PostImageInfo(final String storeName, final Post post) {
        super(storeName);
        this.post = post;
    }

    public static PostImageInfo of(final ImageInfo imageInfo, final Post post) {
        return new PostImageInfo(imageInfo.getStoreName(), post);
    }
}
