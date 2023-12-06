package edonymyeon.backend.content.thumbs.domain;

import java.util.List;

public class AllThumbsInPost {

    private final List<Thumbs> allThumbs;

    private AllThumbsInPost(final List<Thumbs> allThumbs) {
        this.allThumbs = allThumbs;
    }

    public static AllThumbsInPost from(final List<Thumbs> thumbs) {
        return new AllThumbsInPost(thumbs);
    }

    public int getUpCount() {
        long count = allThumbs.stream()
                .filter(thumbs -> thumbs.getThumbsType() == ThumbsType.UP)
                .count();
        return (int) count;
    }

    public int getDownCount() {
        return allThumbs.size() - getUpCount();
    }
}
