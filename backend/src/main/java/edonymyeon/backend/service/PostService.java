package edonymyeon.backend.service;

import edonymyeon.backend.domain.ImageInfo;
import edonymyeon.backend.domain.Post;
import edonymyeon.backend.repository.ImageInfoRepository;
import edonymyeon.backend.repository.PostRepository;
import edonymyeon.backend.service.request.PostRequest;
import edonymyeon.backend.service.response.PostResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostService {

    private final PostRepository postRepository;
    private final ImageFileRepository imageFileRepository;

    @Value("${file.dir}")
    private String fileDirectory;

    @Transactional
    public PostResponse createPost(final PostRequest postRequest) {

        System.out.println("postRequest = " + postRequest);

        final Post post = new Post(
                postRequest.title(),
                postRequest.content(),
                postRequest.price()
        );
        postRepository.save(post);

        if (!postRequest.images().isEmpty()) {
            final List<ImageInfo> imageInfos = postRequest.images()
                    .stream()
                    .map(image -> {
                        try {
                            return new ImageFile(fileDirectory, image, post);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).toList();
            imageFileRepository.saveAll(imageFiles);
        }

        return new PostResponse(post.getId());
    }
}
