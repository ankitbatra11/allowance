package com.abatra.android.allowance.consent.lib;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.ConsentStatusType;
import com.abatra.android.allowance.ConsentType;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;

public class ConsentUtils {

    public static ConsentStatusLoaderResponse createResponse(ConsentInformation consentInformation, ConsentStatus consentStatus) {
        ConsentType consentType = mapConsentType(consentStatus);
        return new ConsentStatusLoaderResponse(mapConsentStatusType(consentInformation, consentType), consentType, true);
    }

    private static ConsentStatusType mapConsentStatusType(ConsentInformation consentInformation, ConsentType consentType) {
        if (!consentInformation.isRequestLocationInEeaOrUnknown()) {
            return ConsentStatusType.NOT_REQUIRED;
        }
        switch (consentType) {
            case NPA:
            case PA:
                return ConsentStatusType.OBTAINED;
            case UNKNOWN:
                return ConsentStatusType.REQUIRED;
            default:
                throw new IllegalArgumentException("invalid consent type=" + consentType);
        }
    }

    private static ConsentType mapConsentType(ConsentStatus consentStatus) {
        switch (consentStatus) {
            case UNKNOWN:
                return ConsentType.UNKNOWN;
            case NON_PERSONALIZED:
                return ConsentType.NPA;
            case PERSONALIZED:
                return ConsentType.PA;
            default:
                throw new IllegalArgumentException("invalid consent status=" + consentStatus);
        }
    }

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
