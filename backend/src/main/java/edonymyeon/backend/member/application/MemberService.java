package edonymyeon.backend.member.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_ID_NOT_FOUND;
import static edonymyeon.backend.global.exception.ExceptionInformation.MEMBER_NICKNAME_DUPLICATE;

import edonymyeon.backend.image.domain.Domain;
import edonymyeon.backend.member.application.dto.response.DuplicateCheckResponse;
import edonymyeon.backend.auth.domain.ValidateType;
import edonymyeon.backend.global.exception.EdonymyeonException;
import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.image.domain.ImageInfo;
import edonymyeon.backend.image.profileimage.domain.ProfileImageInfo;
import edonymyeon.backend.image.profileimage.repository.ProfileImageInfoRepository;
import edonymyeon.backend.member.application.dto.MemberId;
import edonymyeon.backend.member.application.dto.YearMonthDto;
import edonymyeon.backend.member.application.dto.request.MemberUpdateRequest;
import edonymyeon.backend.member.application.dto.request.PurchaseConfirmRequest;
import edonymyeon.backend.member.application.dto.request.SavingConfirmRequest;
import edonymyeon.backend.member.application.dto.response.MemberUpdateResponse;
import edonymyeon.backend.member.application.dto.response.MyPageResponseV1_0;
import edonymyeon.backend.member.application.dto.response.MyPageResponseV1_1;
import edonymyeon.backend.member.application.event.ProfileImageDeletionEvent;
import edonymyeon.backend.member.application.event.ProfileImageUploadEvent;
import edonymyeon.backend.member.domain.Device;
import edonymyeon.backend.member.domain.Member;
import edonymyeon.backend.member.repository.MemberRepository;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberService {

    private final DeviceRepository deviceRepository;

    private final MemberRepository memberRepository;

    private final MemberConsumptionService memberConsumptionService;

    private final ProfileImageInfoRepository profileImageInfoRepository;

    private final ImageFileUploader imageFileUploader;

    private final Domain domain;

    private final ApplicationEventPublisher publisher;

    public MyPageResponseV1_0 findMemberInfoByIdV1_0(final Long id) {
        final Member member = findMember(id);
        return new MyPageResponseV1_0(member.getId(), member.getNickname());
    }

    private Member findMember(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new EdonymyeonException(MEMBER_ID_NOT_FOUND));
    }

    public MyPageResponseV1_1 findMemberInfoByIdV1_1(final Long id) {
        final Member member = findMember(id);
        return new MyPageResponseV1_1(member.getId(), member.getNickname(), domain.convertToImageUrl(member.getProfileImageInfo()));
    }

    @Transactional
    public MemberUpdateResponse updateMember(final MemberId memberId, final MemberUpdateRequest updateRequest) {
        final Member member = findMember(memberId.id());
        validateDuplicateNickname(member.getNickname(), updateRequest.nickname());

        final boolean imageChanged = updateRequest.isImageChanged();

        if (imageChanged) {
            deleteProfileImage(member);
        }

        if (isImageEmpty(updateRequest.profileImage())) {
            return updateWithoutProfileImage(member, updateRequest);
        }

        return updateWithProfileImage(member, updateRequest);
    }

    private void validateDuplicateNickname(final String currentNickname, final String updateNickname) {
        if (!currentNickname.equals(updateNickname) &&
                memberRepository.existsByNickname(updateNickname)) {
            throw new EdonymyeonException(MEMBER_NICKNAME_DUPLICATE);
        }
    }

    public DuplicateCheckResponse checkDuplicate(final String target, final String value) {
        final ValidateType validateType = ValidateType.from(target);

        if (!existsByValidateType(validateType, value)) {
            return new DuplicateCheckResponse(true);
        }
        return new DuplicateCheckResponse(false);
    }

    private boolean existsByValidateType(final ValidateType validateType, final String value) {
        if (validateType.equals(ValidateType.EMAIL)) {
            return memberRepository.existsByEmail(value);
        }

        return memberRepository.existsByNickname(value);
    }

    @Transactional
    public void deleteProfileImage(final Member member) {
        if (Objects.nonNull(member.getProfileImageInfo())) {
            final ProfileImageInfo imageInfoToDelete = member.getProfileImageInfo();
            profileImageInfoRepository.delete(imageInfoToDelete);
            member.deleteProfileImage();
            publisher.publishEvent(new ProfileImageDeletionEvent(imageInfoToDelete));
        }
    }

    private boolean isImageEmpty(final MultipartFile image) {
        return Objects.isNull(image) || image.isEmpty();
    }

    private MemberUpdateResponse updateWithoutProfileImage(final Member member,
                                                          final MemberUpdateRequest updateRequest) {
        member.updateNickname(updateRequest.nickname());
        return new MemberUpdateResponse(member.getId());
    }

    private MemberUpdateResponse updateWithProfileImage(final Member member, final MemberUpdateRequest updateRequest) {
        final MultipartFile imageFile = updateRequest.profileImage();
        final ImageInfo imageInfo = imageFileUploader.from(imageFile);
        final ProfileImageInfo profileImageInfo = ProfileImageInfo.from(imageInfo);
        profileImageInfoRepository.save(profileImageInfo);
        member.updateProfileImageInfo(profileImageInfo);
        member.updateNickname(updateRequest.nickname());

        publisher.publishEvent(new ProfileImageUploadEvent(imageFile, profileImageInfo));
        return new MemberUpdateResponse(member.getId());
    }

    @Transactional
    public void confirmPurchase(
            final MemberId memberId,
            final Long postId,
            final PurchaseConfirmRequest purchaseConfirmRequest
    ) {
        final YearMonthDto yearMonthDto = new YearMonthDto(purchaseConfirmRequest.year(),
                purchaseConfirmRequest.month());
        memberConsumptionService.confirmPurchase(memberId, postId, purchaseConfirmRequest.purchasePrice(),
                yearMonthDto);
    }

    @Transactional
    public void confirmSaving(
            final MemberId memberId,
            final Long postId,
            final SavingConfirmRequest savingConfirmRequest) {
        final YearMonthDto yearMonthDto = new YearMonthDto(savingConfirmRequest.year(),
                savingConfirmRequest.month());
        memberConsumptionService.confirmSaving(memberId, postId, yearMonthDto);
    }

    @Transactional
    public void removeConfirm(final MemberId memberId, final Long postId) {
        memberConsumptionService.removeConfirm(memberId, postId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void activateDevice(final Member member, final String deviceToken) {
        final Member rePersistedMember = memberRepository.save(member);
        if (rePersistedMember.isActiveDevice(deviceToken)) {
            return;
        }
        rePersistedMember.activateDevice(deviceToken);

        deactivateOtherDevices(member, deviceToken);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateOtherDevices(final Member member, final String deviceToken) {
        deviceRepository.findAllByDeviceToken(deviceToken)
                .stream().filter(device -> !device.isOwner(member))
                .forEach(Device::deactivate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deactivateDevice(final String deviceToken) {
        final Optional<Device> device = deviceRepository.findByDeviceToken(deviceToken);
        device.ifPresent(Device::deactivate);
    }
}
