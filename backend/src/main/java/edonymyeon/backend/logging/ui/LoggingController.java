package edonymyeon.backend.logging.ui;

import edonymyeon.backend.logging.application.LogType;
import edonymyeon.backend.logging.application.LoggingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/log")
public class LoggingController {

    private final LoggingService loggingService;

    @GetMapping
    public String showLoggingPage(Model model) {
        model.addAttribute("info_logs", loggingService.findLogs(LogType.INFO).response());
        model.addAttribute("warn_logs", loggingService.findLogs(LogType.WARN).response());
        model.addAttribute("error_logs", loggingService.findLogs(LogType.ERROR).response());
        return "log";
    }
}
