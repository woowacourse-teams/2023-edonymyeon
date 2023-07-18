package edonymyeon.backend.image.postimage;

import edonymyeon.backend.image.domain.ImageInfo;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ProfileImageInfo extends ImageInfo {

}
