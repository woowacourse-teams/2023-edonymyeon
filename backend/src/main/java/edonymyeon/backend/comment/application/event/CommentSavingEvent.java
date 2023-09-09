package edonymyeon.backend.comment.application.event;

import edonymyeon.backend.comment.domain.Comment;

public record CommentSavingEvent(Comment comment) {
}
