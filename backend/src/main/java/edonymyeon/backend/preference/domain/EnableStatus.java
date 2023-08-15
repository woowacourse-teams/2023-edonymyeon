package edonymyeon.backend.preference.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EnableStatus {
    ENABLED(true) {
        @Override
        public EnableStatus toggle() {
            return DISABLED;
        }

        @Override
        public EnableStatus forceDisable() {
            return ENABLED_SHUT_BY_DEPENDENCY;
        }

        @Override
        public EnableStatus releaseForceDisable() {
            return this;
        }
    },
    DISABLED(false) {
        @Override
        public EnableStatus toggle() {
            return ENABLED;
        }

        @Override
        public EnableStatus forceDisable() {
            return DISABLED_SHUT_BY_DEPENDENCY;
        }

        @Override
        public EnableStatus releaseForceDisable() {
            return this;
        }
    },
    ENABLED_SHUT_BY_DEPENDENCY(false) {
        @Override
        public EnableStatus toggle() {
            return ENABLED_SHUT_BY_DEPENDENCY;
        }

        @Override
        public EnableStatus forceDisable() {
            return this;
        }

        @Override
        public EnableStatus releaseForceDisable() {
            return ENABLED;
        }
    },
    DISABLED_SHUT_BY_DEPENDENCY(false) {
        @Override
        public EnableStatus toggle() {
            return DISABLED_SHUT_BY_DEPENDENCY;
        }

        @Override
        public EnableStatus forceDisable() {
            return this;
        }

        @Override
        public EnableStatus releaseForceDisable() {
            return DISABLED;
        }
    };

    private final boolean enabled;

    public abstract EnableStatus toggle();

    public abstract EnableStatus forceDisable();

    public abstract EnableStatus releaseForceDisable();

    public boolean isEnabled() {
        return this.enabled;
    }
}
