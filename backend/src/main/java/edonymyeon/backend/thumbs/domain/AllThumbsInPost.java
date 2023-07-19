package edonymyeon.backend.thumbs.domain;

import java.util.List;

public class AllThumbsInPost {

    private final List<Thumbs> allThumbs;

    public AllThumbsInPost(final List<Thumbs> allThumbs) {
        this.allThumbs = allThumbs;
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
