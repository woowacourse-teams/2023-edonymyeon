package edonymyeon.backend.content.post.ui;

import edonymyeon.backend.content.post.application.GeneralFindingCondition;
import edonymyeon.backend.content.post.application.HotFindingCondition;
import edonymyeon.backend.content.post.application.PostReadService;
import edonymyeon.backend.content.post.application.PostSlice;
import edonymyeon.backend.content.post.application.dto.response.GeneralPostInfoResponse;
import edonymyeon.backend.content.post.application.dto.response.SpecificPostInfoResponse;
import edonymyeon.backend.membber.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.membber.member.application.dto.MemberId;
import edonymyeon.backend.content.post.ui.annotation.HotPostSizing;
import edonymyeon.backend.content.post.ui.annotation.PostPaging;
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

    @ApiVersion(from = "1.0")
    @GetMapping(value = "/posts")
    public ResponseEntity<PostSlice<GeneralPostInfoResponse>> findAllPosts(
            @PostPaging GeneralFindingCondition generalFindingCondition) {
        PostSlice<GeneralPostInfoResponse> posts = postReadService.findPostsByPagingCondition(generalFindingCondition);
        return ResponseEntity.ok()
                .body(posts);
    }

    @ApiVersion(from = "1.0")
    @GetMapping("/posts/{postId}")
    public ResponseEntity<SpecificPostInfoResponse> findSpecificPost(
            @AuthPrincipal(required = false) MemberId memberId, @PathVariable Long postId) {
        SpecificPostInfoResponse specificPost = postReadService.findSpecificPost(postId, memberId);
        return ResponseEntity.ok()
                .body(specificPost);
    }

    @ApiVersion(from = "1.0")
    @GetMapping("/posts/hot")
    public ResponseEntity<PostSlice<GeneralPostInfoResponse>> findHotPosts(
            @HotPostSizing HotFindingCondition hotFindingCondition
    ) {
        PostSlice<GeneralPostInfoResponse> hotPosts = postReadService.findHotPosts(hotFindingCondition);
        return ResponseEntity.ok()
                .body(hotPosts);
    }

    @ApiVersion(from = "1.0")
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
