package edonymyeon.backend.membber.member.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Embeddable
public class SocialInfo {

    @Enumerated(value = EnumType.STRING)
    private SocialType socialType;

    private Long socialId;

    public enum SocialType {

        KAKAO
    }

    public static SocialInfo of(final SocialType socialType, final Long socialId) {
        return new SocialInfo(socialType, socialId);
    }
}
