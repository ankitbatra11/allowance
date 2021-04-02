package com.abatra.android.allowance.consent.lib;

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
}
