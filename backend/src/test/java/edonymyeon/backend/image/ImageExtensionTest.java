package edonymyeon.backend.image;

import static edonymyeon.backend.global.exception.ExceptionInformation.IMAGE_EXTENSION_INVALID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.domain.ImageExtension;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ImageExtensionTest {

    @ParameterizedTest(name = "{index} : {0}을 넣으면 {1}을 반환한다.")
    @CsvSource({"jpg, true", "jpEg, true", "png, true", "csv, false", "txt, false"})
    void 지원하는_확장자인지_테스트(String ext, boolean expected) {
        final boolean actual = ImageExtension.contains(ext);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 해당하는_확장자의_미디어타입을_반환한다() {
        final MediaType actual = ImageExtension.findMediaType("test.jpg");

        assertThat(actual).isEqualTo(MediaType.IMAGE_JPEG);
    }

    @Test
    void 해당하는_확장자의_미디어타입이_존재하지않으면_예외가_발생한다() {
        assertThatThrownBy(() -> ImageExtension.findMediaType("test.txt"))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(IMAGE_EXTENSION_INVALID.getMessage());
    }

    @Test
    void 해당하는_확장자의_열거타입을_반환한다() {
        final ImageExtension extension = ImageExtension.from("jpg");
        assertThat(extension.equals(ImageExtension.JPG)).isTrue();
    }

    @Test
    void 해당하는_확장자의_열거타입이_없다면_예외가_발생한다() {
        assertThatThrownBy(() -> ImageExtension.from("txt"))
                .isInstanceOf(EdonymyeonException.class)
                .hasMessage(IMAGE_EXTENSION_INVALID.getMessage());
    }
}
