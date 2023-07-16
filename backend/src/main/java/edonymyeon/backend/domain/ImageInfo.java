package edonymyeon.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ImageInfo {

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

    public void updatePost(Post post) {
        this.post = post;
    }
}
