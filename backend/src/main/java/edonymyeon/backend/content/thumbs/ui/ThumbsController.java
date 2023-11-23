package edonymyeon.backend.content.thumbs.ui;

import edonymyeon.backend.membber.auth.annotation.AuthPrincipal;
import edonymyeon.backend.global.version.ApiVersion;
import edonymyeon.backend.membber.member.application.dto.MemberId;
import edonymyeon.backend.content.thumbs.application.ThumbsService;
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

    @ApiVersion(from = "1.0")
    @PutMapping("posts/{postId}/up")
    public ResponseEntity<Void> thumbsUp(@AuthPrincipal final MemberId memberId,
                                         @PathVariable final Long postId) {
        thumbsService.thumbsUp(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }

    @ApiVersion(from = "1.0")
    @PutMapping("posts/{postId}/down")
    public ResponseEntity<Void> thumbsDown(@AuthPrincipal final MemberId memberId,
                                           @PathVariable final Long postId) {
        thumbsService.thumbsDown(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }

    @ApiVersion(from = "1.0")
    @DeleteMapping("posts/{postId}/up")
    public ResponseEntity<Void> deleteThumbsUp(@AuthPrincipal final MemberId memberId,
                                               @PathVariable final Long postId) {
        thumbsService.deleteThumbsUp(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }

    @ApiVersion(from = "1.0")
    @DeleteMapping("posts/{postId}/down")
    public ResponseEntity<Void> deleteThumbsDown(@AuthPrincipal final MemberId memberId,
                                                 @PathVariable final Long postId) {
        thumbsService.deleteThumbsDown(memberId, postId);
        return ResponseEntity.ok()
                .build();
    }
}
