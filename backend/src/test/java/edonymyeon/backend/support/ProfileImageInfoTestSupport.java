package edonymyeon.backend.support;

import edonymyeon.backend.image.application.ImageService;
import edonymyeon.backend.image.domain.ImageType;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.member.profile.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.member.profile.profileimage.repository.ProfileImageInfoRepository;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProfileImageInfoTestSupport {

    private final ProfileImageInfoRepository profileImageInfoRepository;

    private final MockMultipartFileTestSupport mockMultipartFileTestSupport;

    private final ImageService imageService;

    public ProfileImageInfoBuilder builder() {
        return new ProfileImageInfoBuilder();
    }

    public final class ProfileImageInfoBuilder {

        private String storeName;

        public ProfileImageInfoBuilder storeName(final String storeName) {
            this.storeName = storeName;
            return this;
        }

        public ProfileImageInfo build() {
            final ImageInfo imageInfo = new ImageInfo(storeName == null ? UUID.randomUUID().toString() : storeName);
            return profileImageInfoRepository.save(
                    ProfileImageInfo.from(
                            imageInfo
                    )
            );
        }

        public ProfileImageInfo buildWithImageFile() throws IOException {
            final MockMultipartFile file = mockMultipartFileTestSupport.builder().buildImageForProfile();
            final ImageInfo imageInfo = imageService.save(file, ImageType.PROFILE);
            return profileImageInfoRepository.save(
                    ProfileImageInfo.from(imageInfo)
            );
        }
    }
}
