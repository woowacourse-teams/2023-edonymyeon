package edonymyeon.backend.image.application;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.support.IntegrationFixture;
import edonymyeon.backend.support.MockMultipartFileTestSupport;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
class ImageServiceTest extends IntegrationFixture implements ImageFileCleaner {

    private final ImageService imageService;

    @Autowired
    private MockMultipartFileTestSupport mockMultipartFileTestSupport;

    @Value("${image.root-dir}")
    private String rootDirectory;

    @Value("${image.domain}")
    private String domain;

    @ParameterizedTest
    @EnumSource
    void 이미지가_올바른_경로에_저장되는지_검증한다(final ImageType imageType) throws IOException {
        final MockMultipartFile image = mockMultipartFileTestSupport.builder().buildImageForPost();
        final String baseDirectory = rootDirectory + imageType.getSaveDirectory();

        assertThat(new File(baseDirectory).list()).containsOnly("dummy.txt");
        assertThat(new File(baseDirectory).list()).hasSize(1);

        final ImageInfo save = imageService.save(image, imageType);

        assertThat(new File(baseDirectory).list()).contains(save.getStoreName());
        assertThat(new File(baseDirectory).list()).hasSize(2);
    }

    @ParameterizedTest
    @EnumSource
    void 사용자에게_전달할__이미지_주소를_올바르게_조회하는지_검증한다(final ImageType imageType) {
        final String baseUrl = imageService.findBaseUrl(imageType);
        final String imageUrl = imageService.convertToImageUrl("image.png", imageType);

        assertThat(baseUrl).isEqualTo(domain + imageType.getSaveDirectory());
        assertThat(imageUrl.replace(baseUrl, "")).isEqualTo("image.png");
    }

    @Test
    void 프로필_이미지가_디렉터리에서_잘_삭제되는지_검증한다() throws IOException {
        final MockMultipartFile image = mockMultipartFileTestSupport.builder().buildImageForPost();
        final String baseDirectory = rootDirectory + ImageType.PROFILE.getSaveDirectory();

        assertThat(new File(baseDirectory).list()).containsOnly("dummy.txt");
        assertThat(new File(baseDirectory).list()).hasSize(1);

        final ImageInfo save = imageService.save(image, ImageType.PROFILE);

        assertThat(new File(baseDirectory).list()).contains(save.getStoreName());
        assertThat(new File(baseDirectory).list()).hasSize(2);

        imageService.removeImage(save, ImageType.PROFILE);

        assertThat(new File(baseDirectory).list()).hasSize(1);
        assertThat(new File(baseDirectory).list()).containsOnly("dummy.txt");
    }
}
