package edonymyeon.backend.support;

import edonymyeon.backend.member.auth.domain.SimplePasswordEncoder;
import edonymyeon.backend.member.profile.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.member.profile.domain.Device;
import edonymyeon.backend.member.profile.domain.Member;
import edonymyeon.backend.member.profile.domain.SocialInfo;
import edonymyeon.backend.member.profile.repository.MemberRepository;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

@RequiredArgsConstructor
@Component
public class TestMemberBuilder {

    private static final String DEFAULT_EMAIL = "email@edonymyeon.com";

    private static final String DEFAULT_PASSWORD = "password123!";

    private static final String DEFAULT_NICK_NAME = "nickname";

    private static int emailCount = 1;

    private static int nickNameCount = 1;

    private final MemberRepository memberRepository;

    public static String getRawPassword() {
        return DEFAULT_PASSWORD;
    }

    public MemberBuilder builder() {
        return new MemberBuilder();
    }

    public final class MemberBuilder {

        private Long id;

        private String email;

        private String nickname;

        private SocialInfo socialInfo;

        private ProfileImageInfo profileImageInfo;

        private List<Device> devices;

        private boolean deleted = false;

        public MemberBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        public MemberBuilder email(final String email) {
            this.email = email;
            return this;
        }

        public MemberBuilder nickname(final String nickname) {
            this.nickname = nickname;
            return this;
        }

        public MemberBuilder socialInfo(final SocialInfo socialInfo) {
            this.socialInfo = socialInfo;
            return this;
        }

        public MemberBuilder profileImageInfo(final ProfileImageInfo profileImageInfo) {
            this.profileImageInfo = profileImageInfo;
            return this;
        }

        public MemberBuilder devices(final List<Device> devices) {
            this.devices = devices;
            return this;
        }

        public MemberBuilder deleted(final boolean deleted) {
            this.deleted = deleted;
            return this;
        }

        public Member build() {
            final Member member = new Member(
                    this.email != null ? this.email : DEFAULT_EMAIL + emailCount++,
                    DEFAULT_PASSWORD,
                    this.nickname != null ? this.nickname : DEFAULT_NICK_NAME + nickNameCount++,
                    this.profileImageInfo,
                    List.of()
            );
            encryptPassword(member);

            setField(member, "id", this.id, null);
            setField(member, "socialInfo", this.socialInfo, null);
            setField(member, "devices", this.devices, List.of(new Device("testToken", member)));
            setField(member, "deleted", this.deleted, false);
            return memberRepository.save(member);
        }

        private void encryptPassword(final Member member) {
            final SimplePasswordEncoder encoder = new SimplePasswordEncoder();
            member.encrypt(encoder);
        }

        public Member buildWithoutSaving() {
            final Member member = new Member(
                    this.email != null ? this.email : DEFAULT_EMAIL + emailCount++,
                    DEFAULT_PASSWORD,
                    this.nickname != null ? this.nickname : DEFAULT_NICK_NAME + nickNameCount++,
                    this.profileImageInfo,
                    List.of()
            );
            setField(member, "id", this.id, null);
            setField(member, "socialInfo", this.socialInfo, null);
            setField(member, "devices", this.devices, List.of(new Device("testToken", member)));
            setField(member, "deleted", this.deleted, false);
            return member;
        }

        private void setField(final Member member, final String fieldName, final Object value, final Object orElse) {
            final Field field = ReflectionUtils.findField(Member.class, fieldName);
            ReflectionUtils.makeAccessible(field);
            if (Objects.nonNull(value)) {
                ReflectionUtils.setField(field, member, value);
                return;
            }
            ReflectionUtils.setField(field, member, orElse);
        }
    }
}
