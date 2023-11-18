package edonymyeon.backend.notification.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Notification extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    private String title;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private Long postId;

    @Column(name = "is_read")
    private boolean read;

    public Notification(final Long memberId, final String title, final ScreenType screenType, final Long postId) {
        this.memberId = memberId;
        this.title = title;
        this.screenType = screenType;
        this.postId = postId;
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }
}
