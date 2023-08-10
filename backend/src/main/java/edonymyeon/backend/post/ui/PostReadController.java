package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.GeneralFindingCondition;
import edonymyeon.backend.post.application.HotFindingCondition;
import edonymyeon.backend.post.application.PostReadService;
import edonymyeon.backend.post.application.PostSlice;
import edonymyeon.backend.post.application.dto.GeneralPostInfoResponse;
import edonymyeon.backend.post.application.dto.SpecificPostInfoResponse;
import edonymyeon.backend.post.ui.annotation.HotPostSizing;
import edonymyeon.backend.post.ui.annotation.PostPaging;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostReadController {

    private final PostReadService postReadService;

    @GetMapping("/posts")
    public ResponseEntity<PostSlice<GeneralPostInfoResponse>> findAllPosts(
            @PostPaging GeneralFindingCondition generalFindingCondition) {
        PostSlice<GeneralPostInfoResponse> posts = postReadService.findPostsByPagingCondition(generalFindingCondition);
        return ResponseEntity.ok()
                .body(posts);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<SpecificPostInfoResponse> findSpecificPost(
            @AuthPrincipal(required = false) MemberId memberId, @PathVariable Long postId) {
        SpecificPostInfoResponse specificPost = postReadService.findSpecificPost(postId, memberId);
        return ResponseEntity.ok()
                .body(specificPost);
    }

    @GetMapping("/posts/hot")
    public ResponseEntity<Slice<GeneralPostInfoResponse>> findHotPosts(
            @HotPostSizing HotFindingCondition hotFindingCondition
    ) {
        Slice<GeneralPostInfoResponse> posts = postReadService.findHotPosts(hotFindingCondition);
        return ResponseEntity.ok()
                .body(posts);
    }

    @GetMapping("/search")
    public ResponseEntity<PostSlice<GeneralPostInfoResponse>> searchPosts(
            @RequestParam String query,
            @PostPaging GeneralFindingCondition generalFindingCondition
    ) {
        PostSlice<GeneralPostInfoResponse> searchPosts = postReadService.searchPosts(query, generalFindingCondition);
        return ResponseEntity.ok()
                .body(searchPosts);
    }
}
