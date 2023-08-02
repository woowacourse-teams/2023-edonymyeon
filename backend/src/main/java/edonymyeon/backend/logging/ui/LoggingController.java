package edonymyeon.backend.logging.ui;

import edonymyeon.backend.logging.application.LogType;
import edonymyeon.backend.logging.application.LoggingService;
import edonymyeon.backend.logging.ui.dto.LoggingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/log")
public class LoggingController {

    private final LoggingService loggingService;

    @GetMapping
    public String showLoggingPage(@RequestParam String type, Model model) {
        final LoggingResponse loggingResponse = loggingService.findLogs(LogType.from(type));

        return "log";
    }
}
