package edonymyeon.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.domain.ImageInfo;
import edonymyeon.backend.repository.ImageInfoRepository;
import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.PostResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(TestConfig.class)
@SpringBootTest
class PostServiceTest {

    private final ImageInfoRepository imageInfoRepository;
    private final PostService postService;

    @Test
    void 게시글_생성() {
        final PostRequest request = new PostRequest(
                "사도 돼요?",
                "얼마 안해요",
                100_000L,
                Collections.emptyList()
        );

        final PostResponse target = postService.createPost(request);
        assertThat(target.id()).isNotNull();
    }

    @Test
    void 폴더에_저장하는_이미지의_이름은_UUID_와_확장자명_형식으로_지어진다() throws IOException {
        // given
        final PostRequest postRequest = getPostRequest();

        // when
        final var postId = postService.createPost(postRequest).id();

        // then
        List<ImageInfo> imageFiles = imageInfoRepository.findAllByPostId(postId);
        assertThat(imageFiles).hasSize(2);
        assertThat(imageFiles)
                .extracting(ImageInfo::getStoreName)
                .containsAll(List.of(
                        "test-inserting-one.jpg",
                        "test-inserting-two.jpg"
                ));
    }

    @Test
    void 폴더에_이미지를_저장한_후_Post_도메인에는_파일의_경로를_넘긴다()
            throws IOException {
        // given
        final PostRequest postRequest = getPostRequest();

        // when
        final Long postId = postService.createPost(postRequest).id();

        // then
        List<ImageInfo> imageFiles = imageInfoRepository.findAllByPostId(postId);
        assertThat(imageFiles).hasSize(2);
        assertThat(imageFiles)
                .extracting(ImageInfo::getFullPath)
                .containsExactlyInAnyOrder(
                        "src/test/resources/static/img/test_store/test-inserting-one.jpg",
                        "src/test/resources/static/img/test_store/test-inserting-two.jpg"
                );
    }

    private PostRequest getPostRequest() throws IOException {
        final InputStream file1InputStream = getClass().getResourceAsStream("/static/img/file/test_image_1.jpg");
        final MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test_image_1.jpg", "image/jpg",
                file1InputStream);

        final InputStream file2InputStream = getClass().getResourceAsStream("/static/img/file/test_image_2.jpg");
        final MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test_image_2.jpg", "image/jpg",
                file2InputStream);

        final List<MultipartFile> multipartFiles = List.of(file1, file2);

        return new PostRequest(
                "I love you",
                "He wisely contented himself with his family and his love of nature.",
                13_000L,
                multipartFiles
        );
    }

    @Test
    void 이미지가_없어도_게시글을_저장할수있다() {
        final PostRequest postRequest = new PostRequest(
                "I love you",
                "He wisely contented himself with his family and his love of nature.",
                13_000L,
                null
        );

        assertThatCode(() -> postService.createPost(postRequest)).doesNotThrowAnyException();
    }
}
