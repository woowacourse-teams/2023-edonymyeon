package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.POST_MEMBER_FORBIDDEN;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.postimage.PostImageInfoRepository;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.application.dto.ReactionCountResponse;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import edonymyeon.backend.post.application.dto.WriterResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import edonymyeon.backend.thumbs.application.ThumbsService;
import edonymyeon.backend.thumbs.dto.AllThumbsInPostResponse;
import edonymyeon.backend.thumbs.dto.ThumbsStatusInPostResponse;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;

    private final ImageFileUploader imageFileUploader;

    private final PostImageInfoRepository postImageInfoRepository;

    private final MemberRepository memberRepository;

    private final ThumbsService thumbsService;

    @Transactional
    public PostResponse createPost(final MemberIdDto memberIdDto, final PostRequest postRequest) {
        final Member member = findMemberById(memberIdDto);

        final Post post = new Post(
                postRequest.title(),
                postRequest.content(),
                postRequest.price(),
                member
        );
        postRepository.save(post);

        if (Objects.isNull(postRequest.images()) || postRequest.images().isEmpty()) {
            return new PostResponse(post.getId());
        }

        final List<PostImageInfo> postImageInfos = uploadImages(postRequest).stream()
                .map(imageInfo -> PostImageInfo.of(imageInfo, post))
                .toList();
        postImageInfoRepository.saveAll(postImageInfos);

        return new PostResponse(post.getId());
    }

    private List<ImageInfo> uploadImages(final PostRequest postRequest) {
        return postRequest.images()
                .stream()
                .map(imageFileUploader::uploadFile)
                .toList();
    }

    @Transactional
    public void deletePost(final MemberIdDto memberIdDto, final Long postId) {
        final Member member = findMemberById(memberIdDto);
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));
        if (post.isSameMember(member)) {
            final List<ImageInfo> imageInfos = post.getPostImageInfos()
                    .stream()
                    .map(postImage -> new ImageInfo(postImage.getFileDirectory(), postImage.getStoreName()))
                    .toList();
            postImageInfoRepository.deleteAllByPostId(postId);
            postRepository.deleteById(postId);
            imageInfos.forEach(imageFileUploader::removeFile);
            return;
        }
        throw new EdonymyeonException(POST_MEMBER_FORBIDDEN);
    }

    public SpecificPostInfoResponse findSpecificPost(final Long postId, final MemberIdDto memberIdDto) {
        final Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EdonymyeonException(POST_ID_NOT_FOUND));

        final Optional<Member> member = memberRepository.findById(memberIdDto.id());

        final ReactionCountResponse reactionCountResponse = new ReactionCountResponse(
                0, // TODO: 조회수 기능 구현 필요
                0, // TODO: 댓글 수 기능 구현 필요
                0 // TODO: 스크랩 기능 구현 필요
        );
        final AllThumbsInPostResponse allThumbsInPost = thumbsService.findAllThumbsInPost(postId);
        final WriterResponse writerResponse = getWriterResponse(post.getMember());

        if (member.isEmpty()) {
            return new SpecificPostInfoResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getPrice(),
                    post.getContent(),
                    post.getCreateAt(),
                    post.getPostImageInfos().stream().map(ImageInfo::getFullPath).toList(),
                    writerResponse,
                    reactionCountResponse,
                    allThumbsInPost.thumbsUpCount(),
                    allThumbsInPost.thumbsDownCount(),
                    false,
                    false,
                    false, // TODO: 스크랩 기능 구현 필요
                    false
            );
        }

        final ThumbsStatusInPostResponse thumbsStatusInPost = thumbsService.findThumbsStatusInPost(memberIdDto, postId);

        return new SpecificPostInfoResponse(
                post.getId(),
                post.getTitle(),
                post.getPrice(),
                post.getContent(),
                post.getCreateAt(),
                post.getPostImageInfos().stream().map(ImageInfo::getFullPath).toList(),
                writerResponse,
                reactionCountResponse,
                allThumbsInPost.thumbsUpCount(),
                allThumbsInPost.thumbsDownCount(),
                thumbsStatusInPost.isUp(),
                thumbsStatusInPost.isDown(),
                false, // TODO: 스크랩 기능 구현 필요
                post.isSameMember(member.get())
        );
    }

    private WriterResponse getWriterResponse(final Member member) {
        return new WriterResponse(
                member.getId(),
                member.getNickname(),
                member.getProfileImageInfo().getFileDirectory()
        );
    }

    private Member findMemberById(final MemberIdDto memberIdDto) {
        return memberRepository.findById(memberIdDto.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }
}
