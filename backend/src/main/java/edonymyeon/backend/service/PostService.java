package edonymyeon.backend.service;

import edonymyeon.backend.domain.ImageInfo;
import edonymyeon.backend.domain.Post;
import edonymyeon.backend.repository.ImageInfoRepository;
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
    private final ImageInfoRepository imageInfoRepository;

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

        final List<ImageInfo> imageInfos = postRequest.images()
                .stream()
                .map(imageFileUploader::uploadFile)
                .toList();
        imageInfos.forEach(post::addImageInfo);
        imageInfoRepository.saveAll(imageInfos);

        return new PostResponse(post.getId());
    }
}
