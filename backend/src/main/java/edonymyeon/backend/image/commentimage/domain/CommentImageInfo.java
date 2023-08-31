package edonymyeon.backend.image.commentimage.domain;

import edonymyeon.backend.image.domain.ImageInfo;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class CommentImageInfo extends ImageInfo {

    @ColumnDefault("false")
    private boolean deleted;

    private CommentImageInfo(final String storeName) {
        super(storeName);
    }

    public static CommentImageInfo from(final ImageInfo imageInfo) {
        return new CommentImageInfo(imageInfo.getStoreName());
    }

    public void delete() {
        this.deleted = true;
    }
}
