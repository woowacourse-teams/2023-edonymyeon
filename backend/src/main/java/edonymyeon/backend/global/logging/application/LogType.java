package edonymyeon.backend.global.logging.application;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum LogType implements Log {

    INFO(getAbsoluteFilePath("logs/info.log")),
    WARN(getAbsoluteFilePath("logs/warn.log")),
    ERROR(getAbsoluteFilePath("logs/error.log"))
    ;

    private final Path filePath;

    LogType(final Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public Path getFilePath() {
        return filePath;
    }

    private static Path getAbsoluteFilePath(String relativePath) {
        return Paths.get(relativePath).toAbsolutePath();
    }
}
