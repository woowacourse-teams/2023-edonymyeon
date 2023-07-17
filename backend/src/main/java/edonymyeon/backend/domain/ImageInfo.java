package edonymyeon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass
public class ImageInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column
    protected String fileDirectory;

    @Column
    protected String storeName;

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
