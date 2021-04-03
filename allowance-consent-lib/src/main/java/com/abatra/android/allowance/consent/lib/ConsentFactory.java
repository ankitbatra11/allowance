package com.abatra.android.allowance.consent.lib;

import android.content.Context;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentType;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

public class ConsentFactory {

    private final Context context;

    public ConsentFactory(Context context) {
        this.context = context;
    }

    public Consent createConsent(ConsentStatus consentStatus) {
        switch (consentStatus) {
            case UNKNOWN:
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
                    return new Consent(com.abatra.android.allowance.ConsentStatus.UNKNOWN);
                } else {
                    return new Consent(com.abatra.android.allowance.ConsentStatus.NOT_REQUIRED);
                }
            case NON_PERSONALIZED:
                return new Consent(com.abatra.android.allowance.ConsentStatus.OBTAINED).setType(ConsentType.NPA);
            case PERSONALIZED:
                return new Consent(com.abatra.android.allowance.ConsentStatus.OBTAINED).setType(ConsentType.PA);
        }
        throw new IllegalArgumentException("Invalid status=" + consentStatus);
    }
}
