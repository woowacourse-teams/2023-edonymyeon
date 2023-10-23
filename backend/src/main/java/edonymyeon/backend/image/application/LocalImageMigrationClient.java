package edonymyeon.backend.image.application;

import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!prod")
@RequiredArgsConstructor
@Component
public class LocalImageMigrationClient implements ImageMigrationClient{

    @Override
    public void migrate(final File image, final String directory, final String storeName) {
        System.out.println("아무것도 안하지롱");
    }
}
