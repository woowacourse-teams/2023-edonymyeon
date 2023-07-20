package edonymyeon.backend.image.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public class ImageInfo {

    @Value("${domain}")
    private String domain;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String fileDirectory;

    @Column
    private String storeName;

    public ImageInfo(
            final String fileDirectory,
            final String storeName
    ) {
        this.fileDirectory = fileDirectory;
        this.storeName = storeName;
    }

    public String getUrl() {
        return domain + storeName;
    }
}
