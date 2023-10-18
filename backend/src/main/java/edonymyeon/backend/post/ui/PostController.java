package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.request.PostModificationRequest;
import edonymyeon.backend.post.application.dto.request.PostRequest;
import edonymyeon.backend.post.application.dto.response.PostIdResponse;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts")
@RestController
public class PostController {

    private final PostService postService;

    @ApiVersion(value = {1, 2})
    @PostMapping
    public ResponseEntity<PostIdResponse> createPost(@AuthPrincipal MemberId memberId,
                                                     @ModelAttribute PostRequest postRequest) {
        PostIdResponse response = postService.createPost(memberId, postRequest);
        return ResponseEntity.created(URI.create("/posts/" + response.id()))
                .body(response);
    }

    @ApiVersion(value = {1, 2})
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthPrincipal MemberId memberId, @PathVariable Long postId) {
        postService.deletePost(memberId, postId);
        return ResponseEntity.noContent().build();
    }

    @ApiVersion(value = {1, 2})
    @PutMapping("/{postId}")
    public ResponseEntity<PostIdResponse> updatePost(@AuthPrincipal MemberId memberId,
                                                     @ModelAttribute PostModificationRequest postModificationRequest,
                                                     @PathVariable Long postId) {
        final PostIdResponse postIdResponse = postService.updatePost(memberId, postId, postModificationRequest);
        return ResponseEntity.ok().body(postIdResponse);
    }
}
