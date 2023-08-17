package edonymyeon.backend.support;

import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberTestSupport {

    private static final String DEFAULT_EMAIL = "email";
    private static final String DEFAULT_PASSWORD = "password123!";
    private static final String DEFAULT_NICK_NAME = "nickName";
    private static final String DEFAULT_DEVICE_TOKEN = "unknownDevice";
    private static int emailCount = 1;
    private static int nickNameCount = 1;

    private final MemberRepository memberRepository;

    private final ProfileImageInfoTestSupport profileImageInfoTestSupport;

    public MemberBuilder builder() {
        return new MemberBuilder();
    }

    public final class MemberBuilder {

        private String email;

        private String password;

        private String nickname;

        private ProfileImageInfo profileImageInfo;

        private List<String> deviceTokens;

        public MemberBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public MemberBuilder password(final String password) {
            this.password = password;
            return this;
        }

        public MemberBuilder nickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberBuilder profileImageInfo(final ProfileImageInfo profileImageInfo) {
            this.profileImageInfo = profileImageInfo;
            return this;
        }

        public MemberBuilder deviceToken(final List<String> deviceTokens) {
            this.deviceTokens = deviceTokens;
            return this;
        }

        public Member build() {
            return memberRepository.save(
                    new Member(
                            email == null ? (DEFAULT_EMAIL + emailCount++) : email,
                            password == null ? DEFAULT_PASSWORD : password,
                            nickname == null ? (DEFAULT_NICK_NAME + nickNameCount++) : nickname,
                            profileImageInfo == null ? profileImageInfoTestSupport.builder().build() : profileImageInfo,
                            deviceTokens == null ? List.of(DEFAULT_DEVICE_TOKEN) : deviceTokens
                    )
            );
        }
    }
}
