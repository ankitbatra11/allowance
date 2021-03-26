package com.abatra.android.allowance;

import java.util.Arrays;
import java.util.Collection;

public class Consent {

    private final Status status;
    private ConsentType type;

    public Consent(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public ConsentType getType() {
        return type;
    }

    public Consent setType(ConsentType type) {
        this.type = type;
        return this;
    }

    public enum Status {
        REQUIRED,
        NOT_REQUIRED,
        OBTAINED,
        UNKNOWN;

        public static Collection<Status> required() {
            return Arrays.asList(REQUIRED, UNKNOWN);
        }

        public static Collection<Status> obtained() {
            return Arrays.asList(NOT_REQUIRED, OBTAINED);
        }
    }
}
