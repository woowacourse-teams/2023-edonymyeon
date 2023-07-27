package edonymyeon.backend.post.ui;

import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.GeneralFindingCondition;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.GeneralPostsResponse;
import edonymyeon.backend.post.ui.annotation.PostPaging;
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
    public ResponseEntity<GeneralPostsResponse> searchPosts(
            @RequestParam String query,
            @PostPaging GeneralFindingCondition generalFindingCondition
    ) {
        final List<GeneralPostInfoResponse> posts = postService.searchPosts(query, generalFindingCondition);
        return ResponseEntity
                .ok()
                .body(new GeneralPostsResponse(posts));
    }
}
