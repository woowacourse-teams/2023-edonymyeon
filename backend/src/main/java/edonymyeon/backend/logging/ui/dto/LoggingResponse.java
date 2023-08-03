package edonymyeon.backend.logging.ui.dto;

import java.util.List;
import java.util.Map;

public record LoggingResponse(List<Map<String, String>> response) {
}
