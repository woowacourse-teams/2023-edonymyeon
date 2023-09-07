package edonymyeon.backend.post.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HotPostPolicy {

    private static final int FINDING_PERIOD = 7;

    private static final int VIEW_COUNT_WEIGHT = 1;

    private static final int THUMBS_COUNT_WEIGHT = 3;

    private static final int COMMENT_COUNT_WEIGHT = 5;

    public static LocalDateTime getFindPeriod(){
        return LocalDateTime.now()
                .minus(FINDING_PERIOD, ChronoUnit.DAYS);
    }

    public static int getViewCountWeight(){
        return VIEW_COUNT_WEIGHT;
    }

    public static int getThumbsCountWeight(){
        return THUMBS_COUNT_WEIGHT;
    }

    public static int getCommentCountWeight() {
        return COMMENT_COUNT_WEIGHT;
    }
}
