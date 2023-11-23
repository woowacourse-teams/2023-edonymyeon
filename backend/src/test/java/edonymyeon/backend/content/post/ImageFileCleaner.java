package edonymyeon.backend.content.post;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;

public interface ImageFileCleaner {

    @AfterEach
    default void cleanImageStoreDirectory() {
        final File postFolder = new File("src/test/resources/static/images/post/");
        final File commentFolder = new File("src/test/resources/static/images/comment/");
        final File profileFolder = new File("src/test/resources/static/images/profile/");
        List<File> folders = List.of(postFolder, commentFolder, profileFolder);
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return !name.equals("dummy.txt");
            }
        };
        for (File folder : folders) {
            File[] files = folder.listFiles(filter);
            assert files != null;
            Arrays.stream(files).forEach(file -> file.delete());
        }
    }
}
