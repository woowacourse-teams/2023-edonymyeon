package edonymyeon.backend.member.domain;

import edonymyeon.backend.auth.domain.PasswordEncoder;
import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.global.exception.ExceptionInformation;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Getter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Member extends TemporalRecord {

    private static final int MAX_EMAIL_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    private SocialInfo socialInfo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private ProfileImageInfo profileImageInfo;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    private List<Device> devices = new ArrayList<>();

    @ColumnDefault(value = "false")
    private boolean deleted = false;

    public Member(
            final String email,
            final String password,
            final String nickname,
            final ProfileImageInfo profileImageInfo,
            final List<String> deviceTokens
    ) {
        this.email = Email.from(email);
        this.password = Password.from(password);
        this.nickname = Nickname.from(nickname);
        this.profileImageInfo = profileImageInfo;
        this.devices = deviceTokens.stream()
                .map(token -> new Device(token, this))
                .toList();
    }

    private Member(final Email email, final Password password, final Nickname nickname, final SocialInfo socialInfo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.socialInfo = socialInfo;
    }

    public static Member from(final SocialInfo socialInfo) {
        return new Member(
                Email.from(socialInfo.getSocialType()),
                Password.from(socialInfo.getSocialType()),
                Nickname.from(socialInfo.getSocialType()),
                socialInfo);
    }

    public boolean hasActiveDeviceToken() {
        return devices.stream().filter(Device::isActive)
                .map(Device::getDeviceToken)
                .findAny().isPresent();
    }

    public String getActiveDeviceToken() {
        return devices.stream().filter(Device::isActive)
                .map(Device::getDeviceToken)
                .findAny().orElseThrow(() -> new EdonymyeonException(ExceptionInformation.MEMBER_DEVICE_TOKEN_NOT_FOUND));
    }

    public void withdraw() {
        this.deleted = true;
        this.profileImageInfo = null;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getEmail() {
        return email.getValue();
    }

    public String getNickname() {
        if (deleted) {
            return Nickname.NONE;
        }
        return nickname.getValue();
    }

    public String getPassword() {
        return password.getValue();
    }

    public boolean isActiveDevice(final String deviceToken) {
        final Optional<Device> device = this.devices.stream()
                .filter(dev -> dev.isDeviceTokenEqualTo(deviceToken))
                .findAny();
        return device.map(Device::isActive).orElse(false);
    }

    public void activateDevice(final String deviceToken) {
        this.devices.forEach(Device::deactivate);

        if (isNewDevice(deviceToken)) {
            this.devices.add(new Device(deviceToken, this));
            return;
        }

        this.devices.stream()
                .filter(dev -> dev.isDeviceTokenEqualTo(deviceToken))
                .findAny()
                .ifPresent(Device::activate);
    }

    private boolean isNewDevice(final String deviceToken) {
        return this.devices.stream().noneMatch(device -> device.isDeviceTokenEqualTo(deviceToken));
    }

    public boolean hasId(final Long memberId) {
        return Objects.equals(this.id, memberId);
    }

    public void encrypt(final PasswordEncoder passwordEncoder) {
        this.password = password.encrypt(passwordEncoder);
    }

    public void deleteProfileImage() {
        this.profileImageInfo = null;
    }

    public void updateNickname(final String nickname) {
        this.nickname = Nickname.from(nickname);
    }

    public void updateProfileImageInfo(final ProfileImageInfo profileImageInfo) {
        this.profileImageInfo = profileImageInfo;
    }
}
