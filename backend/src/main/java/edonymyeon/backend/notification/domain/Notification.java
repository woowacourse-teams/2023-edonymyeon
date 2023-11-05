package edonymyeon.backend.notification.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.member.domain.Member;
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
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
public class Notification extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member member;

    private String title;

    private String body;

    @Enumerated(EnumType.STRING)
    private ScreenType screenType;

    private Long postId;

    @Column(name = "is_read")
    private boolean read;

    public Notification(
            final Member member,
            final String title,
            final String body,
            final ScreenType screenType,
            final Long postId
    ) {
        this.member = member;
        this.title = title;
        this.body = body;
        this.screenType = screenType;
        this.postId = postId;
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public ScreenType getScreenType() {
        return screenType;
    }

    public boolean isRead() {
        return read;
    }

    public Long getPostId() {
        return postId;
    }
}
