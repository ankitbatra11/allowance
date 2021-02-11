package com.abatra.android.allowance;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Optional;

public class LoadConsentFormRequest extends LoadConsentStatusRequest {

    @Nullable
    private ConsentFormLoader.Listener formLoaderListener;

    @Nullable
    private String privacyPolicyUrl;

    public LoadConsentFormRequest(Activity activity) {
        super(activity);
    }

    public void setFormLoaderListener(@Nullable ConsentFormLoader.Listener formLoaderListener) {
        this.formLoaderListener = formLoaderListener;
    }

    public LoadConsentFormRequest setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
        return this;
    }

    @Nullable
    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public Optional<ConsentFormLoader.Listener> getFormLoaderListener() {
        return Optional.ofNullable(formLoaderListener);
    }

    @NonNull
    @Override
    public String toString() {
        return "LoadConsentFormRequest{" +
                "loadConsentStatusRequest='" + super.toString() + '\'' +
                ", privacyPolicyUrl='" + privacyPolicyUrl + '\'' +
                '}';
    }
}
