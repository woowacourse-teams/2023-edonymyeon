package edonymyeon.backend.post.application.dto;

public enum SortBy {
    CREATE_AT("createdAt");

    private final String name;

    SortBy(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
