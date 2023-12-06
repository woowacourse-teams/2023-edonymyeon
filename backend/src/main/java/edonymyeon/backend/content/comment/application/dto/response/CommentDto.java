package edonymyeon.backend.content.comment.application.dto.response;

import edonymyeon.backend.content.comment.domain.Comment;
import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String image,
        String comment,
        boolean isWriter,
        LocalDateTime createdAt,
        WriterDto writer
) {

    public static CommentDto of(final boolean isWriter, final Comment comment, final String image) {
        return new CommentDto(
                comment.getId(),
                image,
                comment.getContent(),
                isWriter,
                comment.getCreatedAt(),
                new WriterDto(comment.getMember().getNickname())
        );
    }
}
