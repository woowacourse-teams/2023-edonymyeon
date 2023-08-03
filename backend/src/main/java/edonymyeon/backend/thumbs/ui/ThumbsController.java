package edonymyeon.backend.thumbs.ui;

import edonymyeon.backend.auth.annotation.AuthPrincipal;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.thumbs.application.ThumbsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ThumbsController {

    private final ThumbsService thumbsService;

    @PutMapping("posts/{postId}/up")
    public ResponseEntity<Void> thumbsUp(@AuthPrincipal final MemberId memberId,
                                         @PathVariable final Long postId) {
        thumbsService.thumbsUp(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("posts/{postId}/down")
    public ResponseEntity<Void> thumbsDown(@AuthPrincipal final MemberId memberId,
                                           @PathVariable final Long postId) {
        thumbsService.thumbsDown(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("posts/{postId}/up")
    public ResponseEntity<Void> deleteThumbsUp(@AuthPrincipal final MemberId memberId,
                                               @PathVariable final Long postId) {
        thumbsService.deleteThumbsUp(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("posts/{postId}/down")
    public ResponseEntity<Void> deleteThumbsDown(@AuthPrincipal final MemberId memberId,
                                                 @PathVariable final Long postId) {
        thumbsService.deleteThumbsDown(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }
}
