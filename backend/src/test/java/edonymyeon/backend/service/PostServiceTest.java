package edonymyeon.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import edonymyeon.backend.domain.ImageInfo;
import edonymyeon.backend.repository.ImageInfoRepository;
import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.PostResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
@AllArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(TestConfig.class)
@SpringBootTest
class PostServiceTest {

    private PostService postService;

    @Nested
    class 게시글_생성_테스트 {
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
        @Disabled
        void 이미지들의_멀티파트파일을_받으면_특정_폴더에_이미지를_저장한다() throws IOException, InterruptedException {
            // given
            final PostRequest postRequest = getPostRequest();

            // when
            postService.createPost(postRequest);

            // then
            final int uploadedImagesCount = new ClassPathResource("static/img/test_store")
                    .getFile()
                    .list()
                    .length;

            assertThat(uploadedImagesCount).isEqualTo(2);
        }

        @Test
        void 폴더에_저장하는_이미지의_이름은_UUID_와_확장자명_형식으로_지어진다() throws IOException {
            // given
            final PostRequest postRequest = getPostRequest();

            // when
            postService.createPost(postRequest);

            // then
            final List<String> uploadedImageNames = Arrays
                    .stream(new ClassPathResource("static/img/test_store")
                            .getFile().list()
                    ).toList();

            assertThat(uploadedImageNames)
                    .containsAll(List.of(
                            "test-inserting-one.jpg",
                            "test-inserting-two.jpg"
                    ));
        }

        @Test
        void 폴더에_이미지를_저장한_후_Post_도메인에는_파일의_경로를_넘긴다(@Autowired ImageInfoRepository imageFileRepository)
                throws IOException {
            // given
            final PostRequest postRequest = getPostRequest();

            // when
            final Long postId = postService.createPost(postRequest).id();

            // then
            List<ImageInfo> imageFiles = imageFileRepository.findAllByPostId(postId);
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
    }
}
