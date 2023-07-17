package edonymyeon.backend.service;

import edonymyeon.backend.domain.ImageInfo;
import edonymyeon.backend.domain.Post;
import edonymyeon.backend.domain.PostImageInfo;
import edonymyeon.backend.repository.PostImageInfoRepository;
import edonymyeon.backend.repository.PostRepository;
import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.PostResponse;
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

    @Transactional
    public PostResponse createPost(final PostRequest postRequest) {
        final Post post = new Post(
                postRequest.title(),
                postRequest.content(),
                postRequest.price()
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
