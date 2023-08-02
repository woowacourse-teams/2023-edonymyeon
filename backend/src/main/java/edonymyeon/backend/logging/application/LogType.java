package edonymyeon.backend.logging.application;

import static edonymyeon.backend.global.exception.ExceptionInformation.*;

import edonymyeon.backend.global.exception.EdonymyeonException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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
