package com.abatra.android.allowance;

import android.content.Context;

import androidx.annotation.NonNull;

import java.util.Collection;

public abstract class ConsentFormLoadRequest {

    private final ConsentLoadRequest consentLoadRequest;
    private final Collection<Consent.Status> requiredConsentStatuses;
    private final Context context;

    protected ConsentFormLoadRequest(ConsentLoadRequest consentLoadRequest,
                                     Collection<Consent.Status> requiredConsentStatuses,
                                     Context context) {
        this.consentLoadRequest = consentLoadRequest;
        this.requiredConsentStatuses = requiredConsentStatuses;
        this.context = context;
    }

    public ConsentLoadRequest getConsentLoadRequest() {
        return consentLoadRequest;
    }

    public Collection<Consent.Status> getRequiredConsentStatuses() {
        return requiredConsentStatuses;
    }

    public Context getContext() {
        return context;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConsentFormLoadRequest{" +
                "consentLoadRequest=" + consentLoadRequest +
                ", requiredConsentStatuses=" + requiredConsentStatuses +
                ", context=" + context +
                '}';
    }
}
