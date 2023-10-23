package edonymyeon.backend.image.domain;

public interface ImageFileNameStrategy {

    String createName(String originalFileName);
}
