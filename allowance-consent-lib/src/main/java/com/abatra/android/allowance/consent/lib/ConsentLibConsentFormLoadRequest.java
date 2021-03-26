package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import androidx.annotation.NonNull;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentFormLoadRequest;
import com.abatra.android.allowance.ConsentLoadRequest;

import java.util.Collection;

public class ConsentLibConsentFormLoadRequest extends ConsentFormLoadRequest {

    private final String privacyPolicyUrl;
    private boolean loadFormOnClose;

    public ConsentLibConsentFormLoadRequest(ConsentLoadRequest consentLoadRequest,
                                            Collection<Consent.Status> requiredConsentStatuses,
                                            Context context,
                                            String privacyPolicyUrl) {
        super(consentLoadRequest, requiredConsentStatuses, context);
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public ConsentLibConsentFormLoadRequest setLoadFormOnClose(boolean loadFormOnClose) {
        this.loadFormOnClose = loadFormOnClose;
        return this;
    }

    public boolean isLoadFormOnClose() {
        return loadFormOnClose;
    }

    @NonNull
    @Override
    public String toString() {
        return "ConsentLibConsentFormLoadRequest{" +
                "super='" + super.toString() + '\'' +
                "privacyPolicyUrl='" + privacyPolicyUrl + '\'' +
                ", loadFormOnClose=" + loadFormOnClose +
                '}';
    }
}
