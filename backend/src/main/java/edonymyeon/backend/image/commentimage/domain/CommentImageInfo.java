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

    public CommentImageInfo(final String storeName) {
        super(storeName);
    }

    public void delete() {
        this.deleted = true;
    }
}
