package edonymyeon.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.image.postimage.PostImageInfoRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostFindingCondition;
import edonymyeon.backend.post.application.dto.PostFindingResponse;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.repository.PostRepository;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Transactional
@SpringBootTest
@Import(TestConfig.class)
@DisplayName("게시글 전체 조회 테스트")
public class PostServiceFindingAllPostsTest {

    public static final String POST_REQUEST1_TITLE = "Lost in Time";
    public static final String POST_REQUEST1_CONTENT = "A young archaeologist discovers a mysterious artifact that transports her back in time, forcing her to navigate ancient civilizations and find a way back home before history unravels.";
    public static final long POST_REQUEST1_PRICE = 14_000L;

    public static final String POST_REQUEST2_TITLE = "Whispers in the Dark";
    public static final String POST_REQUEST2_CONTENT = "A renowned detective embarks on a perilous journey to solve a series of murders that are linked to a secret society.";
    public static final long POST_REQUEST2_PRICE = 20_000L;

    public static final String POST_REQUEST3_TITLE = "Silent Shadows";
    public static final String POST_REQUEST3_CONTENT = "In a desolate town plagued by a decades-old curse, a curious teenager ventures into an abandoned mansion.";
    public static final long POST_REQUEST3_PRICE = 25_000L;

    public static final String IMAGE1_RELATIVE_PATH = "/static/img/file/test_image_1.jpg";
    public static final String IMAGE2_RELATIVE_PATH = "/static/img/file/test_image_2.jpg";

    private final PostService postService;

    @BeforeEach
    void setUp() throws IOException {
        final MockMultipartFile file1 = new MockMultipartFile("imageFiles", "test_image_1.jpg", "image/jpg",
                getClass().getResourceAsStream(IMAGE1_RELATIVE_PATH));

        final MockMultipartFile file2 = new MockMultipartFile("imageFiles", "test_image_2.jpg", "image/jpg",
                getClass().getResourceAsStream(IMAGE2_RELATIVE_PATH));

        final var POST_REQUEST1 = new PostRequest(
                POST_REQUEST1_TITLE,
                POST_REQUEST1_CONTENT,
                POST_REQUEST1_PRICE,
                List.of(file1, file2)
        );

        final var POST_REQUEST2 = new PostRequest(
                POST_REQUEST2_TITLE,
                POST_REQUEST2_CONTENT,
                POST_REQUEST2_PRICE,
                List.of(file1, file2)
        );

        final var POST_REQUEST3 = new PostRequest(
                POST_REQUEST3_TITLE,
                POST_REQUEST3_CONTENT,
                POST_REQUEST3_PRICE,
                List.of(file1, file2)
        );

        postService.createPost(POST_REQUEST1);
        postService.createPost(POST_REQUEST2);
        postService.createPost(POST_REQUEST3);
    }

    @Test
    void 작성된_모든_게시글을_조회할_수_있다() {
        final var postFindingCondition = PostFindingCondition.builder().build();
        final var postFindingResponses = postService.findAllPost(postFindingCondition);

        assertAll(
                () -> assertThat(postFindingResponses).hasSize(3),
                () -> assertThat(postFindingResponses)
                        .extracting(PostFindingResponse::title)
                        .containsExactlyInAnyOrder(
                                POST_REQUEST1_TITLE,
                                POST_REQUEST2_TITLE,
                                POST_REQUEST3_TITLE
                        ),
                () -> assertThat(postFindingResponses)
                        .extracting(PostFindingResponse::content)
                        .containsExactlyInAnyOrder(
                                POST_REQUEST1_CONTENT,
                                POST_REQUEST2_CONTENT,
                                POST_REQUEST3_CONTENT
                        ),
                () -> assertThat(postFindingResponses)
                        .extracting(PostFindingResponse::imagePath)
                        .containsOnly("src/test/resources/static/img/test_store/test-inserting0.jpg"),
                () -> assertThat(postFindingResponses)
                        .extracting(PostFindingResponse::createdAt)
                        .hasSize(3)
        );
        // TODO: 작성자 정보 검증
        // TODO: 조회수 검증
        // TODO: 스크랩 수 검증
        // TODO: 댓글 수 검증
    }

    @Test
    void 게시글은_기본으로_등록일_내림차순으로_정렬된다() {
        final var postFindingCondition = PostFindingCondition.builder().build();
        final var postFindingResponses = postService.findAllPost(postFindingCondition);
        final var createdAts = postFindingResponses.stream()
                .map(PostFindingResponse::createdAt)
                .toList();

        assertThat(createdAts.get(0)).isAfterOrEqualTo(createdAts.get(1));
        assertThat(createdAts.get(1)).isAfterOrEqualTo(createdAts.get(2));
    }

    @Test
    void 주어진_조회개수로_게시글_조회개수를_조절할_수_있다() throws IOException {
        setUp();
        setUp();
        setUp();
        setUp();
        setUp(); // 12개 저장됨

        final var postFindingCondition = PostFindingCondition.builder()
                .size(10)
                .build();
        final var postFindingResponses = postService.findAllPost(postFindingCondition);

        assertThat(postFindingResponses)
                .hasSize(10);
    }

    @Test
    void 조회개수가_정해진_경우_주어진_조회페이지로_게시글_조회페이지를_조절할_수_있다() {
        final var postFindingCondition = PostFindingCondition.builder()
                .size(1)
                .page(1)
                .build();
        final var postFindingResponses = postService.findAllPost(postFindingCondition);

        assertThat(postFindingResponses.get(0).title())
                .isEqualTo(POST_REQUEST2_TITLE);

    }

    @Test
    void 조회할_게시글이_없는_경우_null이_아니라_빈_리스트를_반환한다(
            @Autowired PostRepository postRepository,
            @Autowired PostImageInfoRepository imageInfoRepository
    ) {
        imageInfoRepository.deleteAll();
        postRepository.deleteAll();

        final var postFindingCondition = PostFindingCondition.builder().build();
        final var postFindingResponses = postService.findAllPost(postFindingCondition);

        assertThat(postFindingResponses)
                .isNotNull()
                .isEmpty();
    }
}
