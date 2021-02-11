package com.abatra.android.allowance;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class LoadConsentFormRequest {

    private final ConsentRequest consentRequest;

    @Nullable
    private ConsentFormLoader.Listener formLoaderListener;

    public LoadConsentFormRequest(ConsentRequest consentRequest) {
        this.consentRequest = consentRequest;
    }

    public ConsentRequest getConsentRequest() {
        return consentRequest;
    }

    public void setFormLoaderListener(@Nullable ConsentFormLoader.Listener formLoaderListener) {
        this.formLoaderListener = formLoaderListener;
    }

    public Optional<ConsentFormLoader.Listener> getFormLoaderListener() {
        return Optional.ofNullable(formLoaderListener);
    }

    @Override
    public String toString() {
        return "LoadConsentFormRequest{" +
                "consentRequest=" + consentRequest +
                '}';
    }
}
