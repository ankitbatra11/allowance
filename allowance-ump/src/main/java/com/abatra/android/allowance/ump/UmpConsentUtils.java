package com.abatra.android.allowance.ump;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.ConsentStatusType;
import com.abatra.android.allowance.ConsentType;
import com.abatra.android.allowance.DebugGeography;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.FormError;
import com.google.common.base.MoreObjects;
import com.google.common.base.Supplier;

import java.util.Locale;

import timber.log.Timber;

public final class UmpConsentUtils {

    private UmpConsentUtils() {
    }

    public static void report(@Nullable FormError formError, Supplier<String> errorMessageFormatSupplier) {
        if (formError != null) {
            if (shouldReport(formError)) {

                String message = String.format(Locale.ENGLISH,
                        errorMessageFormatSupplier.get() + ". formError=%s",
                        formError.getErrorCode(), formError.getMessage(), toString(formError));

                Timber.e(new RuntimeException(message));

            } else {
                Timber.i("requestConsentInfoUpdate failed! Error code=%d Message=%s",
                        formError.getErrorCode(), formError.getMessage());
            }
        }
    }

    private static boolean shouldReport(FormError formError) {
        return formError.getErrorCode() != FormError.ErrorCode.INTERNET_ERROR;
    }

    public static String toString(FormError formError) {
        return MoreObjects.toStringHelper(FormError.class)
                .add("errorCode", formError.getErrorCode())
                .add("errorMessage", formError.getMessage())
                .toString();
    }

    public static ConsentStatusLoaderResponse createResponse(ConsentInformation consentInformation) {
        return new ConsentStatusLoaderResponse(
                mapConsentStatusType(consentInformation.getConsentStatus()),
                mapConsentType(consentInformation.getConsentType()),
                consentInformation.isConsentFormAvailable());
    }

    private static ConsentType mapConsentType(int umpType) {
        switch (umpType) {
            case ConsentInformation.ConsentType.NON_PERSONALIZED:
                return ConsentType.NPA;
            case ConsentInformation.ConsentType.PERSONALIZED:
                return ConsentType.PA;
            case ConsentInformation.ConsentType.UNKNOWN:
                return ConsentType.UNKNOWN;
            default:
                throw new IllegalArgumentException("invalid ump type=" + umpType);
        }
    }

    private static ConsentStatusType mapConsentStatusType(int umpType) {
        switch (umpType) {
            case ConsentInformation.ConsentStatus.NOT_REQUIRED:
                return ConsentStatusType.NOT_REQUIRED;
            case ConsentInformation.ConsentStatus.OBTAINED:
                return ConsentStatusType.OBTAINED;
            case ConsentInformation.ConsentStatus.REQUIRED:
                return ConsentStatusType.REQUIRED;
            case ConsentInformation.ConsentStatus.UNKNOWN:
                return ConsentStatusType.UNKNOWN;
            default:
                throw new IllegalArgumentException("invalid ump type=" + umpType);
        }
    }

    public static int mapDebugGeography(DebugGeography debugGeography) {
        switch (debugGeography) {
            case EEA:
                return ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA;
            case NOT_EEA:
                return ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA;
            case DISABLED:
                return ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_DISABLED;
            default:
                throw new IllegalArgumentException("invalid debug geography=" + debugGeography);
        }
    }

}
