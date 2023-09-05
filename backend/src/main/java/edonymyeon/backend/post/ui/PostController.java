package edonymyeon.backend.post.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.post.application.PostService;
import edonymyeon.backend.post.application.dto.request.PostModificationRequest;
import edonymyeon.backend.post.application.dto.request.PostRequest;
import edonymyeon.backend.post.application.dto.response.PostResponse;
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

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@AuthPrincipal MemberId memberId,
                                                   @ModelAttribute PostRequest postRequest) {
        PostResponse response = postService.createPost(memberId, postRequest);
        return ResponseEntity.created(URI.create("/posts/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@AuthPrincipal MemberId memberId, @PathVariable Long postId) {
        postService.deletePost(memberId, postId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@AuthPrincipal MemberId memberId,
                                                   @ModelAttribute PostModificationRequest postModificationRequest,
                                                   @PathVariable Long postId) {
        final PostResponse postResponse = postService.updatePost(memberId, postId, postModificationRequest);
        return ResponseEntity.ok().body(postResponse);
    }
}
