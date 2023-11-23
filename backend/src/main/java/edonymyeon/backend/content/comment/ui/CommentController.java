package edonymyeon.backend.content.comment.ui;

import edonymyeon.backend.membber.auth.annotation.AuthPrincipal;
import edonymyeon.backend.content.comment.application.CommentService;
import edonymyeon.backend.content.comment.application.dto.request.CommentRequest;
import edonymyeon.backend.content.comment.application.dto.response.CommentsResponse;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.membber.member.application.dto.MemberId;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
@RestController
public class CommentController {

    private final CommentService commentService;

    @ApiVersion(from = "1.0")
    @PostMapping
    public ResponseEntity<Void> createComment(@AuthPrincipal MemberId memberId,
                                              @PathVariable Long postId,
                                              @ModelAttribute CommentRequest commentRequest) {
        final long commentId = commentService.createComment(memberId, postId, commentRequest);
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comments/" + commentId))
                .build();
    }

    @ApiVersion(from = "1.0")
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@AuthPrincipal MemberId memberId,
                                              @PathVariable Long postId,
                                              @PathVariable Long commentId) {
        commentService.deleteComment(memberId, postId, commentId);
        return ResponseEntity.noContent()
                .build();
    }

    @ApiVersion(from = "1.0")
    @GetMapping
    public ResponseEntity<CommentsResponse> findCommentsByPostId(@AuthPrincipal(required = false) MemberId memberId,
                                                                 @PathVariable Long postId) {
        final CommentsResponse response = commentService.findCommentsByPostId(memberId, postId);
        return ResponseEntity.ok().body(response);
    }
}
