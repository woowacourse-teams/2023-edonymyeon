package edonymyeon.backend.image.profileimage.application;

import edonymyeon.backend.image.application.ImageService;
import edonymyeon.backend.member.application.event.ProfileImageDeletionEvent;
import edonymyeon.backend.member.application.event.ProfileImageUploadEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ProfileImageEventListener {

    private final ImageService imageService;

    @EventListener
    public void uploadFile(ProfileImageUploadEvent event) {
        imageFileUploader.uploadRealStorage(event.file(), event.imageInfo());
    }

    @Async
    @TransactionalEventListener
    public void removeFile(ProfileImageDeletionEvent event) {
        imageService.removeImage(event.imageInfo(), event.imageType());
    }
}
