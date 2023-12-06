package edonymyeon.backend.content.post.application.dto.response;

import edonymyeon.backend.content.post.domain.Post;
import org.springframework.data.domain.Slice;

import java.util.List;

public class GeneralPostsInfoResponse {

    private GeneralPostsInfoResponse(){
    }

    public static Slice<GeneralPostInfoResponse> toSlice(Slice<Post> posts, String baseUrl) {
        return posts.map(post -> GeneralPostInfoResponse.of(post, baseUrl));
    }

    public static List<GeneralPostInfoResponse> toList(List<Post> posts, String baseUrl) {
        return posts.stream()
                .map(post -> GeneralPostInfoResponse.of(post, baseUrl))
                .toList();
    }
}
