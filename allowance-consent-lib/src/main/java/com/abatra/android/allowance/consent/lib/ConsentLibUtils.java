package com.abatra.android.allowance.consent.lib;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentType;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

public class ConsentLibUtils {

    public static DebugGeography mapDebugGeography(com.abatra.android.allowance.DebugGeography debugGeography) {
        switch (debugGeography) {
            case EEA:
                return DebugGeography.DEBUG_GEOGRAPHY_EEA;
            case NOT_EEA:
                return DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA;
            case DISABLED:
                return DebugGeography.DEBUG_GEOGRAPHY_DISABLED;
            default:
                throw new IllegalArgumentException("Invalid debug geography!");
        }
    }

    public static Consent createConsent(ConsentStatus consentStatus) {
        switch (consentStatus) {
            case UNKNOWN:
                return new Consent(Consent.Status.UNKNOWN);
            case NON_PERSONALIZED:
                return new Consent(Consent.Status.OBTAINED).setType(ConsentType.NPA);
            case PERSONALIZED:
                return new Consent(Consent.Status.OBTAINED).setType(ConsentType.PA);
        }
        throw new IllegalArgumentException("Invalid status=" + consentStatus);
    }
}