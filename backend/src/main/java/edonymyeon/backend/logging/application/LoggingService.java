package edonymyeon.backend.logging.application;

import edonymyeon.backend.logging.ui.dto.LoggingResponse;
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
