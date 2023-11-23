package edonymyeon.backend.membber.auth.ui;

public enum SessionConst {

    USER("userId", 30 * Constants.DAY);

    private final String sessionId;
    private final int validatedTime;

    SessionConst(final String sessionId, final int validatedTime) {
        this.sessionId = sessionId;
        this.validatedTime = validatedTime;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getValidatedTime() {
        return validatedTime;
    }

    private static class Constants {

        private static final int DAY = 86400;
    }
}
