package edonymyeon.backend.member.profile.profileimage.application;

import edonymyeon.backend.image.application.ImageService;
import edonymyeon.backend.member.profile.application.event.ProfileImageDeletionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ProfileImageEventListener {

    private final ImageService imageService;

    @Async
    @TransactionalEventListener
    public void removeFile(ProfileImageDeletionEvent event) {
        imageService.removeImage(event.imageInfo(), event.imageType());
    }
}
