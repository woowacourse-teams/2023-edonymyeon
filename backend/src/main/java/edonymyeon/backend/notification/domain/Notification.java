package edonymyeon.backend.notification.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Notification extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private Long postId;

    private boolean read;

    public Notification(final String title, final ScreenType screenType, final Long postId) {
        this.title = title;
        this.screenType = screenType;
        this.postId = postId;
        this.read = false;
    }
}
