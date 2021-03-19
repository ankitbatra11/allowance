package com.abatra.android.allowance;

import androidx.annotation.NonNull;

public class ShowConsentFormRequest {

    private final ConsentRequest consentRequest;
    private ConsentFormDismissListener consentFormDismissListener;

    public ShowConsentFormRequest(ConsentRequest consentRequest, ConsentFormDismissListener consentFormDismissListener) {
        this.consentRequest = consentRequest;
        this.consentFormDismissListener = consentFormDismissListener;
    }

    public ConsentRequest getConsentRequest() {
        return consentRequest;
    }

    public void setConsentFormDismissListener(ConsentFormDismissListener consentFormDismissListener) {
        this.consentFormDismissListener = consentFormDismissListener;
    }

    public ConsentFormDismissListener getConsentFormDismissListener() {
        return consentFormDismissListener;
    }

    @NonNull
    @Override
    public String toString() {
        return "ShowConsentFormRequest{" +
                "consentRequest=" + consentRequest +
                '}';
    }
}
