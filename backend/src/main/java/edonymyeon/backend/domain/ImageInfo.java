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
    private String fileDirectory;

    @Column
    private String storeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private Post post;

    public ImageInfo(
            final String fileDirectory,
            final String storeName
    ) {
        this.fileDirectory = fileDirectory;
        this.storeName = storeName;
    }

    public String getFullPath() {
        return fileDirectory + storeName;
    }
}
