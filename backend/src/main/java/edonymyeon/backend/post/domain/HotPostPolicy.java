package edonymyeon.backend.post.domain;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class HotPostPolicy {

    public static final int FINDING_PERIOD = 7;

    public static final int VIEW_COUNT_WEIGHT = 1;

    public static final int THUMBS_COUNT_WEIGHT = 3;

    public static LocalDateTime getFindPeriod(){
        return LocalDateTime.now()
                .minus(FINDING_PERIOD, ChronoUnit.DAYS);
    }
}
