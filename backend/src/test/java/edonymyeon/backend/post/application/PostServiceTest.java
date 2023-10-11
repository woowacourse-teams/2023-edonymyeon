package edonymyeon.backend.post.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import edonymyeon.backend.TestConfig;
import edonymyeon.backend.comment.application.CommentService;
import edonymyeon.backend.comment.application.dto.request.CommentRequest;
import edonymyeon.backend.comment.repository.CommentRepository;
import edonymyeon.backend.consumption.repository.ConsumptionRepository;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.image.postimage.repository.PostImageInfoRepository;
import edonymyeon.backend.member.application.dto.ActiveMemberId;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.ImageFileCleaner;
import edonymyeon.backend.post.application.dto.request.PostModificationRequest;
import edonymyeon.backend.post.application.dto.request.PostRequest;
import edonymyeon.backend.post.application.dto.response.PostIdResponse;
import edonymyeon.backend.post.application.dto.response.SpecificPostInfoResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.support.ConsumptionTestSupport;
import edonymyeon.backend.support.IntegrationTest;
import edonymyeon.backend.support.MockMultipartFileTestSupport;
import edonymyeon.backend.support.TestMemberBuilder;
import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SuppressWarnings("NonAsciiCharacters")
@RequiredArgsConstructor
@Import(TestConfig.class)
@IntegrationTest
class PostServiceTest implements ImageFileCleaner {

    private static final Pattern 이미지_UUID_와_확장자_형식 = Pattern.compile("test-inserting\\d+\\.(png|jpg)");

    private final TestMemberBuilder testMemberBuilder;

    private final PostImageInfoRepository postImageInfoRepository;

    private final PostService postService;

    private final PostReadService postReadService;

    private final MemberRepository memberRepository;

    private final TestMemberBuilder memberTestSupport;

    private final MockMultipartFileTestSupport mockMultipartFileTestSupport;

    private final ImageFileUploader imageFileUploader;

    private final ConsumptionTestSupport consumptionTestSupport;

    private MemberId memberId;

    private static Post getFetchedPostWithPostImageInfos(final EntityManager entityManager, final PostIdResponse post) {
        return entityManager
                .createQuery(
                        "select p from Post p left join fetch p.postImageInfos.postImageInfos where p.id = :postId",
                        Post.class)
                .setParameter("postId", post.id())
                .getSingleResult();
    }

    private static List<PostImageInfo> getImageInfos(final EntityManager entityManager, final PostIdResponse post) {
        return entityManager
                .createQuery("select pi from PostImageInfo pi where pi.post.id = :postId", PostImageInfo.class)
                .setParameter("postId", post.id())
                .getResultList();
    }

    @BeforeEach
    public void setUp() {
        Member member = memberTestSupport.builder()
                .build();
        memberRepository.save(member);
        memberId = new ActiveMemberId(member.getId());
    }

    private PostRequest getPostRequest() throws IOException {
        final MockMultipartFile file = mockMultipartFileTestSupport.builder()
                .build();

        final List<MultipartFile> multipartFiles = List.of(file, file);

        return new PostRequest(
                "I love you",
                "He wisely contented himself with his family and his love of nature.",
                13_000L,
                multipartFiles
        );
    }

    @Test
    void 작성자의_프로필_사진이_없더라도_상세조회가_가능하다() throws IOException {
        final Member member = testMemberBuilder.builder().build();

        final ActiveMemberId memberId = new ActiveMemberId(member.getId());
        final PostIdResponse postIdResponse = postService.createPost(memberId, getPostRequest());

        Assertions
                .assertThatCode(() -> postReadService.findSpecificPost(postIdResponse.id(), memberId))
                .doesNotThrowAnyException();
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

            final PostIdResponse target = postService.createPost(memberId, request);
            assertThat(target.id()).isNotNull();
        }

        @Test
        void 폴더에_저장하는_이미지의_이름은_UUID_와_확장자명_형식으로_지어진다() throws IOException {
            // given
            final PostRequest postRequest = getPostRequest();

            // when
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
            final Long postId = postService.createPost(memberId, postRequest).id();

            // then
            List<PostImageInfo> imageFiles = postImageInfoRepository.findAllByPostId(postId);
            assertSoftly(softly -> softly.assertThat(imageFiles).hasSize(2));
        }

        @Test
        void 이미지는_10개까지_가능하다() throws IOException {
            // given
            List<MultipartFile> images = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                images.add(mockMultipartFileTestSupport.builder()
                        .build());
            }
            final PostRequest request = new PostRequest(
                    "사도 돼요?",
                    "얼마 안해요",
                    100_000L,
                    images
            );

            // when
            assertThatCode(() -> postService.createPost(memberId, request)).doesNotThrowAnyException();
        }

        @Test
        void 이미지가_10개_초과일_수_없다()
                throws IOException {
            // given
            List<MultipartFile> images = new ArrayList<>();
            for (int i = 0; i < 11; i++) {
                images.add(mockMultipartFileTestSupport.builder()
                        .build());
            }
            final PostRequest request = new PostRequest(
                    "사도 돼요?",
                    "얼마 안해요",
                    100_000L,
                    images
            );

            // when
            assertThatThrownBy(() -> postService.createPost(memberId, request)).isInstanceOf(EdonymyeonException.class)
                    .hasMessage(ExceptionInformation.POST_IMAGE_COUNT_INVALID.getMessage());
        }
    }

    @Nested
    class 게시글을_삭제할_때 {

        @Test
        void 연관된_소비내역도_삭제된다(@Autowired PostRepository postRepository,
                            @Autowired ConsumptionRepository consumptionRepository) throws IOException {
            final PostIdResponse postIdResponse = postService.createPost(memberId, getPostRequest());
            final Post post = postRepository.findById(postIdResponse.id()).get();

            consumptionTestSupport.builder().post(post).build();
            assertThat(consumptionRepository.findByPostId(post.getId())).isPresent();

            postService.deletePost(memberId, postIdResponse.id());
            assertThat(consumptionRepository.findByPostId(post.getId())).isEmpty();
        }

//        @Test
//        void 게시글이_삭제되면_디렉토리에_있는_이미지도_삭제된다() throws IOException {
//            final PostIdResponse postIdResponse = postService.createPost(id, getPostRequest());
//            final PostImageInfo postImageInfo = postImageInfoRepository.findAllByPostId(postIdResponse.id()).get(0);
//            assertThat(new File(imageFileUploader.getFullPath(postImageInfo.getStoreName())).canRead()).isTrue();
//
//            postService.deletePost(id, postIdResponse.id());
//            assertThat(new File(imageFileUploader.getFullPath(postImageInfo.getStoreName())).canRead()).isFalse();
//        }

        @Test
        void 게시글이_삭제되면_조회되지_않는다() throws IOException {
            final PostIdResponse postIdResponse = postService.createPost(memberId, getPostRequest());

            postService.deletePost(memberId, postIdResponse.id());

            assertThatThrownBy(() -> postReadService.findSpecificPost(postIdResponse.id(), memberId)).isInstanceOf(EdonymyeonException.class)
                    .hasMessage(ExceptionInformation.POST_ID_NOT_FOUND.getMessage());
        }

        @Test
        void 게시글이_삭제되면_댓글도_삭제된다(@Autowired CommentService commentService, @Autowired CommentRepository commentRepository) throws IOException {
            final PostIdResponse postIdResponse = postService.createPost(memberId, getPostRequest());
            commentService.createComment(
                    memberId,
                    postIdResponse.id(),
                    new CommentRequest(null, "댓글이다")
            );
            postService.deletePost(memberId, postIdResponse.id());

            assertThat(commentRepository.findAllByPostId(postIdResponse.id())).isEmpty();
        }
    }

    @Nested
    class 게시글을_수정할_때 {

        @Test
        void 게시글_작성자가_아니면_수정할_수_없다() {
            // given
            final PostRequest postRequest = new PostRequest(
                    "I love you",
                    "He wisely contented himself with his family and his love of nature.",
                    13_000L,
                    null
            );
            final PostIdResponse post = postService.createPost(memberId, postRequest);

            // when
            Member 다른_사람 = memberTestSupport.builder()
                    .email("otheremail")
                    .build();

            memberId = new ActiveMemberId(다른_사람.getId());
            final PostModificationRequest updatedPostRequest = new PostModificationRequest(
                    "I hate you",
                    "change!!",
                    26_000L,
                    Collections.emptyList(),
                   null
            );
            assertThatThrownBy(
                    () -> postService.updatePost(new ActiveMemberId(다른_사람.getId()), post.id(), updatedPostRequest))
                    .isInstanceOf(EdonymyeonException.class);
        }

        @Test
        void 제목과_내용과_가격을_수정할_수_있다(@Autowired PostRepository postRepository) {
            // given
            final PostRequest postRequest = new PostRequest(
                    "I love you",
                    "He wisely contented himself with his family and his love of nature.",
                    13_000L,
                    null
            );
            final PostIdResponse post = postService.createPost(memberId, postRequest);

            // when
            final PostModificationRequest updatedPostRequest = new PostModificationRequest(
                    "I hate you",
                    "change!!",
                    26_000L,
                    Collections.emptyList(),
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
            void 기존에_이미지가_없었다면_이미지는_10개까지_추가_가능하다(@Autowired EntityManager entityManager) throws IOException {
                // given
                final PostIdResponse post = postService.createPost(memberId, 이미지가_없는_요청);

                // when
                List<MultipartFile> images = createImages(10);

                final PostModificationRequest request = new PostModificationRequest(
                        "I hate you",
                        "change!!",
                        26_000L,
                        Collections.emptyList(),
                        images
                );

                // when
                assertThatCode(() -> postService.updatePost(memberId, post.id(), request)).doesNotThrowAnyException();
                assertThat(getFetchedPostWithPostImageInfos(entityManager, post).getPostImageInfos()).hasSameSizeAs(images);
            }

            @Test
            void 이미지가_10개_초과일_수_없다(@Autowired EntityManager entityManager)
                    throws IOException {
                // given
                final PostIdResponse post = postService.createPost(memberId, 이미지가_없는_요청);

                // when
                List<MultipartFile> images = createImages(11);
                final PostModificationRequest request = new PostModificationRequest(
                        "I hate you",
                        "change!!",
                        26_000L,
                        Collections.emptyList(),
                        images
                );
                assertThatThrownBy(() -> postService.updatePost(memberId, post.id(), request)).isInstanceOf(
                                EdonymyeonException.class)
                        .hasMessage(ExceptionInformation.POST_IMAGE_COUNT_INVALID.getMessage());
                assertThat(getFetchedPostWithPostImageInfos(entityManager, post).getPostImageInfos()).isEmpty();
            }

            @Test
            void 이미지를_추가할_수_있다(@Autowired EntityManager entityManager) throws IOException {
                // given
                final PostIdResponse 게시글_생성_결과 = postService.createPost(memberId, 이미지가_없는_요청);
                final SpecificPostInfoResponse 게시글_상세조회_결과 = postReadService.findSpecificPost(게시글_생성_결과.id(), memberId);

                // when
                final List<MultipartFile> 추가할_이미지 = List.of(mockMultipartFileTestSupport.builder()
                        .build());
                final PostModificationRequest request = new PostModificationRequest(
                        "I hate you",
                        "change!!",
                        26_000L,
                        게시글_상세조회_결과.images(),
                        추가할_이미지
                );
                postService.updatePost(memberId, 게시글_생성_결과.id(), request);

                // then
                final Post findPost = getFetchedPostWithPostImageInfos(entityManager, 게시글_생성_결과);
                assertThat(findPost.getPostImageInfos()).hasSize(
                        request.newImages().size() + request.originalImages().size());
            }

            @Test
            void 이미지를_전부_삭제할_수_있다(@Autowired EntityManager entityManager) {
                // given
                final PostIdResponse post = postService.createPost(memberId, 이미지가_2개_있는_요청);

                // when
                final PostModificationRequest 이미지가_없는_요청 = new PostModificationRequest(
                        "I hate you",
                        "change!!",
                        26_000L,
                        Collections.emptyList(),
                        null
                );
                postService.updatePost(memberId, post.id(), 이미지가_없는_요청);

                // then
                final Post findPost = getFetchedPostWithPostImageInfos(entityManager, post);
                assertThat(findPost.getPostImageInfos()).isEmpty();
            }

            @Test
            void 이미지를_바꿀_수_있다(@Autowired EntityManager entityManager) throws IOException {
                //given
                final MockMultipartFile 바꾸기_전_이미지 = mockMultipartFileTestSupport.builder().build();
                final PostRequest 게시글_생성_요청 = new PostRequest(
                        "I love you",
                        "He wisely contented himself with his family and his love of nature.",
                        13_000L,
                        List.of(바꾸기_전_이미지)
                );
                final PostIdResponse post = postService.createPost(memberId, 게시글_생성_요청);
                final PostImageInfo 바꾸기_전_이미지_정보 = getImageInfos(entityManager, post)
                        .get(0);

                // when
                final MockMultipartFile 바꾼_후_이미지 = mockMultipartFileTestSupport.builder().build();
                final PostModificationRequest 게시글_수정_요청 = new PostModificationRequest(
                        "I love you",
                        "He wisely contented himself with his family and his love of nature.",
                        13_000L,
                        Collections.emptyList(),
                        List.of(바꾼_후_이미지)
                );
                postService.updatePost(memberId, post.id(), 게시글_수정_요청);

                // then
                final Post findPost = getFetchedPostWithPostImageInfos(entityManager, post);
                final PostImageInfo 바꾼_후_이미지_정보 = findPost.getPostImageInfos().get(0);

                assertSoftly(softly -> {
                            softly.assertThat(findPost.getPostImageInfos().size()).isEqualTo(게시글_수정_요청.newImages().size());
                            softly.assertThat(바꾸기_전_이미지_정보.getStoreName().equals(바꾼_후_이미지_정보.getStoreName())).isFalse();
                        }
                );
            }

            @Test
            void 기존_이미지_10개를_다_갈아치운다(@Autowired EntityManager entityManager) throws IOException {
                // given
                final PostRequest creationRequest = new PostRequest(
                        "I love you",
                        "He wisely contented himself with his family and his love of nature.",
                        13_000L,
                        createImages(10)
                );

                final PostIdResponse post = postService.createPost(memberId, creationRequest);

                // when
                final List<MultipartFile> newImages = createImages(10);
                final PostModificationRequest modificationRequest = new PostModificationRequest(
                        "I hate you",
                        "change!!",
                        26_000L,
                        Collections.emptyList(),
                        newImages
                );
                final Post fetchedPost = getFetchedPostWithPostImageInfos(entityManager, post);
                final List<PostImageInfo> fetchedPostImages = fetchedPost.getPostImageInfos();

                // when
                assertThatCode(() -> postService.updatePost(memberId, post.id(), modificationRequest)).doesNotThrowAnyException();
                assertThat(fetchedPostImages).hasSameSizeAs(newImages);
            }

            private List<MultipartFile> createImages(final int count) throws IOException {
                List<MultipartFile> images = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    images.add(mockMultipartFileTestSupport.builder()
                            .build());
                }
                return images;
            }
        }
    }
}
