package edonymyeon.backend.content.thumbs.application.event;

import edonymyeon.backend.content.post.domain.Post;

public record ThumbsUpEvent(Post post) {
}
