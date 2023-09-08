package edonymyeon.backend.member.domain;

import static edonymyeon.backend.global.exception.ExceptionInformation.ENCODED_PASSWORD_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_EMAIL_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_INVALID;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_PASSWORD_INVALID;

import edonymyeon.backend.global.domain.TemporalRecord;
import edonymyeon.backend.global.exception.BusinessLogicException;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import java.util.UUID;
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
    private static final int MAX_NICKNAME_LENGTH = 20;
    private static final String UNKNOWN = "Unknown";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

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
        validate(email, password, nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageInfo = profileImageInfo;
        this.devices = deviceTokens.stream()
                .map(token -> new Device(token, this))
                .toList();
    }

    private Member(final String email, final String password, final String nickname, final SocialInfo socialInfo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.socialInfo = socialInfo;
    }

    public static Member from(final SocialInfo socialInfo) {
        return new Member(UUID.randomUUID().toString(),
                defaultSocialPassword(),
                "#" + socialInfo.getSocialType().name() + UUID.randomUUID(),
                socialInfo);
    }

    private static String defaultSocialPassword() {
        final String uuid = UUID.randomUUID().toString();
        return uuid.replace("-", "").substring(0, 25) + "!";
    }

    private void validate(final String email, final String password, final String nickname) {
        validateEmail(email);
        validateNickName(nickname);
        validatePassword(password);
    }

    private void validateEmail(final String email) {
        if (Objects.isNull(email) || email.isBlank() || email.length() > MAX_EMAIL_LENGTH) {
            throw new EdonymyeonException(MEMBER_EMAIL_INVALID);
        }
    }

    private void validateNickName(final String nickname) {
        if (Objects.isNull(nickname) || nickname.isBlank() || nickname.length() > MAX_NICKNAME_LENGTH
                || nickname.equalsIgnoreCase(UNKNOWN)) {
            throw new EdonymyeonException(MEMBER_NICKNAME_INVALID);
        }
    }

    private void validatePassword(final String password) {
        if (PasswordValidator.isValidPassword(password)) {
            return;
        }
        throw new EdonymyeonException(MEMBER_PASSWORD_INVALID);
    }

    public Optional<String> getActiveDeviceToken() {
        return devices.stream().filter(Device::isActive)
                .map(Device::getDeviceToken)
                .findAny();
    }

    public void withdraw() {
        this.deleted = true;
        this.profileImageInfo = null;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getNickname() {
        if (deleted) {
            return UNKNOWN;
        }
        return nickname;
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

    public void encrypt(final String encodedPassword) {
        //todo 운영 DB에 암호화 적용후 라인 삭제
        validatePassword(password);

        validateEncodedPassword(encodedPassword);
        this.password = encodedPassword;
    }

    private void validateEncodedPassword(final String encodedPassword) {
        if (PasswordValidator.isValidEncodedPassword(encodedPassword)) {
            return;
        }
        throw new BusinessLogicException(ENCODED_PASSWORD_INVALID);
    }

    // todo
    // 실제로 비밀번호가 변경하고 싶을 때는 UpdateMemberInfo라는 회원 정보 변경 클래스를 받아서 수정하도록
//    public void updateMember(final UpdateMemberRequest updateMember) {
//
//    }
}
