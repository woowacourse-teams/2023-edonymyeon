package edonymyeon.backend.image.profileimage.domain;

import edonymyeon.backend.image.domain.ImageInfo;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProfileImageInfo extends ImageInfo {

    public ProfileImageInfo(final String storeName) {
        super(storeName);
    }
}
