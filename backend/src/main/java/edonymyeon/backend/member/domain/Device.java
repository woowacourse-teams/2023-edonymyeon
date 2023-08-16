package edonymyeon.backend.member.domain;

import edonymyeon.backend.global.domain.TemporalRecord;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Device extends TemporalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceToken;

    private boolean isActive;

    public Device(final String deviceToken) {
        this.deviceToken = deviceToken;
        this.isActive = true;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public boolean isActive() {
        return isActive;
    }
}
