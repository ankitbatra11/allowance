package com.abatra.android.allowance;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class ConsentStatusLoaderResponse {

    @Expose
    private final ConsentStatusType consentStatusType;

    @Expose
    private final ConsentType consentType;

    private final boolean isConsentFormAvailable;

    public ConsentStatusLoaderResponse(ConsentStatusType consentStatusType, ConsentType consentType, boolean isConsentFormAvailable) {
        this.consentStatusType = consentStatusType;
        this.consentType = consentType;
        this.isConsentFormAvailable = isConsentFormAvailable;
    }

    public ConsentStatusType getConsentStatusType() {
        return consentStatusType;
    }

    public ConsentType getConsentType() {
        return consentType;
    }

    public boolean isConsentRequired() {
        return consentStatusType == ConsentStatusType.REQUIRED;
    }

    public boolean isConsentAcquired() {
        return consentStatusType == ConsentStatusType.OBTAINED;
    }

    public boolean isConsentFormAvailable() {
        return isConsentFormAvailable;
    }

    public boolean loadAds() {
        switch (consentStatusType) {
            case NOT_REQUIRED:
            case OBTAINED:
                return true;
            default:
                return false;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Response{" +
                "consentStatusType=" + consentStatusType +
                ", consentType=" + consentType +
                ", isConsentFormAvailable=" + isConsentFormAvailable +
                '}';
    }
}
