package edonymyeon.backend.post.ui;

import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.GeneralPostsResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class ReadPostController {
    private final PostService postService;

    @GetMapping("/search")
    public ResponseEntity<GeneralPostsResponse> searchPosts(@RequestParam String query) {
        final List<GeneralPostInfoResponse> posts = postService.searchPosts(query);
        return ResponseEntity
                .ok()
                .body(new GeneralPostsResponse(posts));
    }
}
