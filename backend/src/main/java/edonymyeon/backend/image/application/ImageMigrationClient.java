package edonymyeon.backend.image.application;

import java.io.File;

public interface ImageMigrationClient {

    void migrate(final File image, final String directory, final String storeName);
}
