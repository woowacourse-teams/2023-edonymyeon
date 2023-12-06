package edonymyeon.backend.global.logging.application;

import edonymyeon.backend.global.logging.ui.dto.LoggingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LoggingService {

    private final LoggingFileReader loggingFileReader;

    public LoggingResponse findLogs(Log log) {
        return new LoggingResponse(loggingFileReader.readFileAsMaps(log));
    }
}
