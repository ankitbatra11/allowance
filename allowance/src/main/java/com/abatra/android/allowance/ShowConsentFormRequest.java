package com.abatra.android.allowance;

import android.app.Activity;

public class ShowConsentFormRequest {

    private final Activity activity;
    private final ConsentFormDismissListener consentFormDismissListener;

    public ShowConsentFormRequest(Activity activity, ConsentFormDismissListener consentFormDismissListener) {
        this.activity = activity;
        this.consentFormDismissListener = consentFormDismissListener;
    }

    public ShowConsentFormRequest(Activity activity) {
        this(activity, null);
    }

    public Activity getActivity() {
        return activity;
    }

    public ConsentFormDismissListener getConsentFormDismissListener() {
        return consentFormDismissListener;
    }
}
