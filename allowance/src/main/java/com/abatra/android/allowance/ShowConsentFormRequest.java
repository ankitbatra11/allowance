package com.abatra.android.allowance;

import android.app.Activity;

public class ShowConsentFormRequest extends LoadConsentFormRequest {

    private final ConsentFormDismissListener consentFormDismissListener;

    public ShowConsentFormRequest(Activity activity, ConsentFormDismissListener consentFormDismissListener) {
        super(activity);
        this.consentFormDismissListener = consentFormDismissListener;
    }

    public ConsentFormDismissListener getConsentFormDismissListener() {
        return consentFormDismissListener;
    }
}
