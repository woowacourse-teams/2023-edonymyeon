package edonymyeon.backend.post;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;

public interface ImageFileCleaner {

    @AfterEach
    default void cleanImageStoreDirectory() {
        final File targetFolder = new File("src/test/resources/static/img/test_store/");
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return !name.equals("test.txt");
            }
        };
        File[] files = targetFolder.listFiles(filter);
        assert files != null;
        Arrays.stream(files).forEach(file -> file.delete());
    }
}
