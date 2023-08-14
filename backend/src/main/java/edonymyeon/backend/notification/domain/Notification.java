package edonymyeon.backend.notification.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.member.domain.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Notification extends TemporalRecord {

    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    private String title;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private Long postId;

    private boolean read;

    public Notification(final Member member, final String title, final ScreenType screenType, final Long postId) {
        this.id = generateId();
        this.member = member;
        this.title = title;
        this.screenType = screenType;
        this.postId = postId;
        this.read = false;
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    public void markAsRead() {
        this.read = true;
    }
}
