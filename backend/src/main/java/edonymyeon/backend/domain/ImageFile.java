package edonymyeon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String originalName;

    @Column
    private String fileDirectory;

    @Column
    private String storeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public ImageFile(final String fileDirectory, final MultipartFile multipartFile, final Post post)
            throws IOException {
        this.fileDirectory = fileDirectory;
        this.originalName = multipartFile.getOriginalFilename();
        this.storeName = createStoreName(originalName);
        this.post = post;

        String fullPath = fileDirectory + storeName;
        final Path absolutePath = Paths.get(fullPath).toAbsolutePath();
        multipartFile.transferTo(absolutePath);
    }

    private String createStoreName(final String originalName) {
        String ext = extractExt(originalName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(final String originalName) {
        int pos = originalName.lastIndexOf(".");
        return originalName.substring(pos + 1);
    }

    public String getFullPath() {
        return fileDirectory + storeName;
    }
}
