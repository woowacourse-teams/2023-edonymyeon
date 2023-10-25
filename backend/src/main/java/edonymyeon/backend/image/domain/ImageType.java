package edonymyeon.backend.image.domain;

public enum ImageType {

    POST("post/"),
    COMMENT("comment/"),
    PROFILE("profile/");

    private final String saveDirectory;

    ImageType(final String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }
}
