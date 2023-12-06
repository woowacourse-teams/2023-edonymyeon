package edonymyeon.backend.content.comment.application.event;

import edonymyeon.backend.content.comment.domain.Comment;

public record SavedCommentEvent(Comment comment) {
}
