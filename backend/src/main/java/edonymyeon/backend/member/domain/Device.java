package edonymyeon.backend.member.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Device extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String deviceToken;

    private boolean isActive;

    public Device(final String deviceToken, final Member member) {
        this.deviceToken = deviceToken;
        this.member = member;
        this.isActive = true;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public boolean isActive() {
        return isActive;
    }

    /**
     * Device에서의 Member 엔티티 사용을 private로 지정하여 양방향 의존으로 인한 위험을 일부 차단합니다.
     */
    private Member getMember() {
        return member;
    }

    public boolean isDeviceTokenEqualTo(final String deviceToken) {
        return Objects.equals(this.deviceToken, deviceToken);
    }

    public void deactivate() {
        if (isActive) {
            this.isActive = false;
        }
    }

    public void activate() {
        if (!isActive) {
            this.isActive = true;
        }
    }
}
