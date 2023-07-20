package edonymyeon.backend.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.support.MemberTestSupport;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final Pattern 이미지_UUID_와_확장자_형식 = Pattern.compile("test-inserting\\d+\\.(png|jpg)");
    private static final Pattern 파일_경로_형식 = Pattern.compile(
            "src/test/resources/static/img/test_store/" + "test-inserting\\d+\\.(png|jpg)"
    );

    private final PostImageInfoRepository postImageInfoRepository;

    private final PostService postService;

    private final MemberRepository memberRepository;

    private final MemberTestSupport memberTestSupport;

    private final ImageFileUploader imageFileUploader;

    private MemberIdDto memberId;

    @BeforeEach
    public void setUp() {
        Member member = memberTestSupport.builder()
                .build();
        memberRepository.save(member);
        memberId = new MemberIdDto(member.getId());
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

    @Nested
    class 게시글을_생성할_때 {

        @Test
        void 이미지_없이_게시글을_생성할_수_있다() {
            final PostRequest request = new PostRequest(
                    "사도 돼요?",
                    "얼마 안해요",
                    100_000L,
                    null
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
            assertSoftly(softly -> {
                        softly.assertThat(imageFiles).hasSize(2);
                        softly.assertThat(이미지_UUID_와_확장자_형식.matcher(imageFiles.get(0).getStoreName()).matches()).isTrue();
                        softly.assertThat(이미지_UUID_와_확장자_형식.matcher(imageFiles.get(1).getStoreName()).matches()).isTrue();
                    }
            );
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
            assertSoftly(softly -> {
                        softly.assertThat(imageFiles).hasSize(2);
                        softly.assertThat(
                                        파일_경로_형식.matcher(imageFileUploader.getFullPath(imageFiles.get(0).getStoreName())).matches())
                                .isTrue();
                        softly.assertThat(
                                        파일_경로_형식.matcher(imageFileUploader.getFullPath(imageFiles.get(1).getStoreName())).matches())
                                .isTrue();
                    }
            );
        }
    }

    @Nested
    class 게시글을_삭제할_때 {

        @Test
        void 게시글이_삭제되면_디렉토리에_있는_이미지도_삭제된다() throws IOException {
            final PostResponse postResponse = postService.createPost(memberId, getPostRequest());
            final PostImageInfo postImageInfo = postImageInfoRepository.findAllByPostId(postResponse.id()).get(0);
            assertThat(new File(imageFileUploader.getFullPath(postImageInfo.getStoreName())).canRead()).isTrue();

            postService.deletePost(memberId, postResponse.id());
            assertThat(new File(imageFileUploader.getFullPath(postImageInfo.getStoreName())).canRead()).isFalse();
        }
    }

    @Nested
    class 게시글을_수정할_때 {

        @Test
        void 게시글_작성자가_아니면_수정할_수_없다(@Autowired PostRepository postRepository) throws IOException {
            // given
            final PostRequest postRequest = new PostRequest(
                    "I love you",
                    "He wisely contented himself with his family and his love of nature.",
                    13_000L,
                    null
            );
            final PostResponse post = postService.createPost(memberId, postRequest);

            // when
            Member 다른_사람 = memberTestSupport.builder()
                    .email("otheremail")
                    .password("password")
                    .build();
            memberRepository.save(다른_사람);
            memberId = new MemberIdDto(다른_사람.getId());
            final PostRequest updatedPostRequest = new PostRequest(
                    "I hate you",
                    "change!!",
                    26_000L,
                    null
            );
            assertThatThrownBy(
                    () -> postService.updatePost(new MemberIdDto(다른_사람.getId()), post.id(), updatedPostRequest))
                    .isInstanceOf(EdonymyeonException.class);
        }

        @Test
        void 제목과_내용과_가격을_수정할_수_있다(@Autowired PostRepository postRepository) throws IOException {
            // given
            final PostRequest postRequest = new PostRequest(
                    "I love you",
                    "He wisely contented himself with his family and his love of nature.",
                    13_000L,
                    null
            );
            final PostResponse post = postService.createPost(memberId, postRequest);

            // when
            final PostRequest updatedPostRequest = new PostRequest(
                    "I hate you",
                    "change!!",
                    26_000L,
                    null
            );
            postService.updatePost(memberId, post.id(), updatedPostRequest);

            // then
            final Post findPost = postRepository.findById(post.id()).get();
            assertSoftly(softly -> {
                        softly.assertThat(findPost.getTitle()).isEqualTo(updatedPostRequest.title());
                        softly.assertThat(findPost.getContent()).isEqualTo(updatedPostRequest.content());
                        softly.assertThat(findPost.getPrice()).isEqualTo(updatedPostRequest.price());
                    }
            );
        }

        @Nested
        class 이미지를_수정하는_경우 {

            private final PostRequest 이미지가_2개_있는_요청 = getPostRequest();

            private final PostRequest 이미지가_없는_요청 = new PostRequest(
                    "I love you",
                    "He wisely contented himself with his family and his love of nature.",
                    13_000L,
                    null
            );

            이미지를_수정하는_경우() throws IOException {
            }

            @Test
            void 이미지를_추가할_수_있다(@Autowired PostRepository postRepository) throws IOException {
                // given
                final PostResponse post = postService.createPost(memberId, 이미지가_없는_요청);

                // when
                postService.updatePost(memberId, post.id(), 이미지가_2개_있는_요청);

                // then
                final Post findPost = postRepository.findById(post.id()).get();
                assertThat(findPost.getPostImageInfos().size()).isEqualTo(이미지가_2개_있는_요청.images().size());
            }

            @Test
            void 이미지를_전부_삭제할_수_있다(@Autowired PostRepository postRepository) throws IOException {
                // given
                final PostResponse post = postService.createPost(memberId, 이미지가_2개_있는_요청);

                // when
                postService.updatePost(memberId, post.id(), 이미지가_없는_요청);

                // then
                final Post findPost = postRepository.findById(post.id()).get();
                assertThat(findPost.getPostImageInfos().size()).isEqualTo(0);
            }

            @Test
            void 이미지를_바꿀_수_있다(@Autowired PostRepository postRepository) throws IOException {
                //given
                final MockMultipartFile 바꾸기_전_이미지 = new MockMultipartFile("imageFiles", "test_image_1.jpg", "image/jpg",
                        getClass().getResourceAsStream("/static/img/file/test_image_1.jpg"));
                final PostRequest 게시글_생성_요청 = new PostRequest(
                        "I love you",
                        "He wisely contented himself with his family and his love of nature.",
                        13_000L,
                        List.of(바꾸기_전_이미지)
                );
                final PostResponse post = postService.createPost(memberId, 게시글_생성_요청);
                final PostImageInfo 바꾸기_전_이미지_정보 = postRepository.findById(post.id()).get().getPostImageInfos().get(0);

                // when
                final MockMultipartFile 바꾼_후_이미지 = new MockMultipartFile("imageFiles", "test_image_2.jpg", "image/jpg",
                        getClass().getResourceAsStream("/static/img/file/test_image_2.jpg"));
                final PostRequest 게시글_수정_요청 = new PostRequest(
                        "I love you",
                        "He wisely contented himself with his family and his love of nature.",
                        13_000L,
                        List.of(바꾼_후_이미지)
                );
                postService.updatePost(memberId, post.id(), 게시글_수정_요청);

                // then
                final Post findPost = postRepository.findById(post.id()).get();
                final PostImageInfo 바꾼_후_이미지_정보 = findPost.getPostImageInfos().get(0);

                assertSoftly(softly -> {
                            softly.assertThat(findPost.getPostImageInfos().size()).isEqualTo(게시글_수정_요청.images().size());
                            softly.assertThat(바꾸기_전_이미지_정보.getUrl().equals(바꾼_후_이미지_정보.getUrl())).isFalse();
                        }
                );
            }
        }
    }
}
