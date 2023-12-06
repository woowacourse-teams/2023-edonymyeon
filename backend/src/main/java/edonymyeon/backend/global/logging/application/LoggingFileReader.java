package edonymyeon.backend.global.logging.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LoggingFileReader {

    private final ObjectMapper objectMapper;

    public List<Map<String, String>> readFileAsMaps(final Log log) {
        final String logContent = findContentByLogType(log);
        return parseContentToMaps(logContent);
    }

    private List<Map<String, String>> parseContentToMaps(final String logContent) {
        final List<String> contents = splitByLoggingFormat(logContent);
        return parseContentsIntoMaps(contents);
    }

    private List<Map<String, String>> parseContentsIntoMaps(final List<String> contents) {
        final List<Map<String, String>> contentsMap = new ArrayList<>();
        for (String content : contents) {
            contentsMap.add(parseContentIntoMap(content));
        }
        return contentsMap;
    }

    private Map<String, String> parseContentIntoMap(final String content) {
        try {
            return objectMapper.readValue(content, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> splitByLoggingFormat(final String logContent) {
        final Scanner scanner = new Scanner(logContent);
        final List<String> contents = new ArrayList<>();
        while (scanner.hasNextLine()) {
            contents.add(scanner.nextLine());
        }

        return contents;
    }

    private static String findContentByLogType(final Log log) {
        try {
            return Files.readString(log.getFilePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
