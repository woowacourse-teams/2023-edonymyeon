package edonymyeon.backend.image.application;

public enum ImageType {

    POST("images/post/"),
    COMMENT("images/comment/"),
    PROFILE("images/profile/");

    private final String saveDirectory;

    ImageType(final String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }
}
