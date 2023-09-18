package edonymyeon.backend.image.profileimage.application;

import edonymyeon.backend.image.ImageFileUploader;
import edonymyeon.backend.member.application.event.ProfileImageDeletionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class ProfileImageEventListener {

    private final ImageFileUploader imageFileUploader;

    @Async
    @TransactionalEventListener
    public void removeFile(ProfileImageDeletionEvent event) {
        imageFileUploader.removeFile(event.imageInfo());
    }
}
