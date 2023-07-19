package edonymyeon.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.postimage.PostImageInfoRepository;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@DisplayNameGeneration(ReplaceUnderscores.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(TestConfig.class)
@SpringBootTest
class PostServiceTest {

    private final PostImageInfoRepository postImageInfoRepository;

    private final PostService postService;

    private final MemberRepository memberRepository;

    private MemberIdDto memberId;

    @BeforeEach
    public void setUp() {
        Member member = new Member(
                "email",
                "password",
                "nickname",
                null
        );
        memberRepository.save(member);
        memberId = new MemberIdDto(member.getId());
    }

    @Test
    void 게시글_생성() {
        final PostRequest request = new PostRequest(
                "사도 돼요?",
                "얼마 안해요",
                100_000L,
                Collections.emptyList()
        );

        final PostResponse target = postService.createPost(memberId, request);
        assertThat(target.id()).isNotNull();
    }

    @Test
    void 폴더에_저장하는_이미지의_이름은_UUID_와_확장자명_형식으로_지어진다() throws IOException {
        // given
        final PostRequest postRequest = getPostRequest();

        // when
        System.out.println("폴더에_저장하는_이미지의_이름은_UUID_와_확장자명_형식으로_지어진다");
        System.out.println("memberId.id() = " + memberId.id());
        final var postId = postService.createPost(memberId, postRequest).id();

        // then
        List<PostImageInfo> imageFiles = postImageInfoRepository.findAllByPostId(postId);
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
        System.out.println("폴더에_이미지를_저장한_후_Post_도메인에는_파일의_경로를_넘긴다");
        System.out.println("memberId.id() = " + memberId.id());
        final Long postId = postService.createPost(memberId, postRequest).id();

        // then
        List<PostImageInfo> imageFiles = postImageInfoRepository.findAllByPostId(postId);
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

        System.out.println("이미지가 없어도 게시글 저장 가능");
        System.out.println("memberId.id() = " + memberId.id());
        assertThatCode(() -> postService.createPost(memberId, postRequest)).doesNotThrowAnyException();
    }

    @Test
    void 게시글이_삭제되면_디렉토리에_있는_이미지도_삭제된다() throws IOException {
        final PostResponse postResponse = postService.createPost(memberId, getPostRequest());
        final PostImageInfo postImageInfo = postImageInfoRepository.findAllByPostId(postResponse.id()).get(0);
        assertThat(new File(postImageInfo.getFullPath()).canRead()).isTrue();

        postService.deletePost(memberId, postResponse.id());
        assertThat(new File(postImageInfo.getFullPath()).canRead()).isFalse();
    }
}
