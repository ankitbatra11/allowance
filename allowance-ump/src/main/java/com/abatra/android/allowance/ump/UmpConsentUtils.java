package com.abatra.android.allowance.ump;

import androidx.annotation.Nullable;

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

}
