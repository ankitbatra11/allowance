package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import androidx.annotation.NonNull;

import com.abatra.android.allowance.ConsentFormLoadRequest;
import com.abatra.android.allowance.ConsentLoadRequest;
import com.abatra.android.allowance.ConsentStatus;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.HashSet;

public class ConsentLibConsentFormLoadRequest extends ConsentFormLoadRequest {

    private final String privacyPolicyUrl;
    private boolean loadFormOnClose;

    public ConsentLibConsentFormLoadRequest(ConsentLoadRequest consentLoadRequest,
                                            Collection<ConsentStatus> requiredConsentStatuses,
                                            Context context,
                                            String privacyPolicyUrl) {
        super(consentLoadRequest, requiredConsentStatuses, context);
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public static ConsentLibConsentFormLoadRequest obtainConsent(ConsentLoadRequest consentLoadRequest,
                                                                 Context context,
                                                                 String privacyPolicyUrl) {
        HashSet<ConsentStatus> requiredConsentStatuses = Sets.newHashSet(ConsentStatus.UNKNOWN, ConsentStatus.REQUIRED);
        return new ConsentLibConsentFormLoadRequest(consentLoadRequest, requiredConsentStatuses, context, privacyPolicyUrl);
    }

    public static ConsentLibConsentFormLoadRequest updateConsent(ConsentLoadRequest consentLoadRequest,
                                                                 Context context,
                                                                 String privacyPolicyUrl) {
        HashSet<ConsentStatus> requiredConsentStatuses = Sets.newHashSet(ConsentStatus.OBTAINED);
        return new ConsentLibConsentFormLoadRequest(consentLoadRequest, requiredConsentStatuses, context, privacyPolicyUrl)
                .setLoadFormOnClose(true);
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
