package com.abatra.android.allowance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class Consent {

    private final ConsentStatus status;
    @Nullable
    private ConsentType type;

    public Consent(ConsentStatus status) {
        this.status = status;
    }

    public ConsentStatus getStatus() {
        return status;
    }

    public Optional<ConsentType> getType() {
        return Optional.ofNullable(type);
    }

    public Consent setType(@Nullable ConsentType type) {
        this.type = type;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "Consent{" +
                "status=" + status +
                ", type=" + type +
                '}';
    }
}
