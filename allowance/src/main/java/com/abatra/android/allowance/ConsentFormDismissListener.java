package com.abatra.android.allowance;

public interface ConsentFormDismissListener {

    default void consentFormDismissedSuccessfully() {
    }

    default void dismissingConsentFormFailed(Throwable error) {
    }
}
