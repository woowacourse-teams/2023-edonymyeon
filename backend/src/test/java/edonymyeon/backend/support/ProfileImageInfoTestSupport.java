package edonymyeon.backend.support;

import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.image.profileimage.repository.ProfileImageInfoRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ProfileImageInfoTestSupport {

    private final ProfileImageInfoRepository profileImageInfoRepository;

    public ProfileImageInfoBuilder builder() {
        return new ProfileImageInfoBuilder();
    }

    public final class ProfileImageInfoBuilder {

        private String fileDirectory;

        private String storeName;

        public ProfileImageInfoBuilder fileDirectory(final String fileDirectory) {
            this.fileDirectory = fileDirectory;
            return this;
        }

        public ProfileImageInfoBuilder storeName(final String storeName) {
            this.storeName = storeName;
            return this;
        }

        public ProfileImageInfo build() {
            return profileImageInfoRepository.save(
                    new ProfileImageInfo(
                            fileDirectory == null ? UUID.randomUUID().toString() : fileDirectory,
                            storeName == null ? UUID.randomUUID().toString() : storeName
                    )
            );
        }
    }
}
