package com.abatra.android.allowance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class LoadConsentStatusRequest {

    private final ConsentRequest consentRequest;
    @Nullable
    private ConsentStatusLoader.Listener statusLoaderListener;

    public LoadConsentStatusRequest(ConsentRequest consentRequest) {
        this.consentRequest = consentRequest;
    }

    public ConsentRequest getConsentRequest() {
        return consentRequest;
    }

    public LoadConsentStatusRequest setStatusLoaderListener(@Nullable ConsentStatusLoader.Listener statusLoaderListener) {
        this.statusLoaderListener = statusLoaderListener;
        return this;
    }

    public Optional<ConsentStatusLoader.Listener> getStatusLoaderListener() {
        return Optional.ofNullable(statusLoaderListener);
    }

    @NonNull
    @Override
    public String toString() {
        return "LoadConsentStatusRequest{" +
                "consentRequest=" + consentRequest +
                '}';
    }
}
