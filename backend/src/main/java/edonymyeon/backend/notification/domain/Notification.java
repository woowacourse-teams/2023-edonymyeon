package edonymyeon.backend.notification.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.notification.domain.notification_content.domain.NotificationContent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Notification extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    @OneToOne
    private NotificationContent notificationContent;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private Long postId;

    @Column(name = "is_read")
    private boolean read;

    public Notification(final Member member, final NotificationContent notificationContent, final ScreenType screenType, final Long postId) {
        this.member = member;
        this.notificationContent = notificationContent;
        this.screenType = screenType;
        this.postId = postId;
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    public String getTitle() {
        return this.notificationContent.getTitle();
    }

    public String getBody() {
        return this.notificationContent.getBody();
    }
}
