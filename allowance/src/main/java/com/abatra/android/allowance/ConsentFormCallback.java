package com.abatra.android.allowance;

public interface ConsentFormCallback {
    void onConsentFormDismissed(Consent consent, boolean userPrefersAdFreeOption);
}
