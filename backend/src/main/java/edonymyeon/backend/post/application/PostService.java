package edonymyeon.backend.post.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;

import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.postimage.PostImageInfoRepository;
import edonymyeon.backend.image.postimage.domain.PostImageInfo;
import edonymyeon.backend.member.application.dto.MemberIdDto;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import edonymyeon.backend.post.application.dto.PostRequest;
import edonymyeon.backend.post.application.dto.PostResponse;
import edonymyeon.backend.post.domain.Post;
import edonymyeon.backend.post.repository.PostRepository;
import java.util.List;
import java.util.Objects;
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

    @Transactional
    public PostResponse createPost(final MemberIdDto memberIdDto, final PostRequest postRequest) {
        final Member member = memberRepository.findById(memberIdDto.id())
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));

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
}
