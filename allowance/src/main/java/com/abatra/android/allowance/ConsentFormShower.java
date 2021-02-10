package com.abatra.android.allowance;

import android.app.Activity;

public interface ConsentFormShower {

    void showConsentForm(Request request);

    class Request {

        private final Activity activity;
        private final ConsentFormDismissListener consentFormDismissListener;

        public Request(Activity activity, ConsentFormDismissListener consentFormDismissListener) {
            this.activity = activity;
            this.consentFormDismissListener = consentFormDismissListener;
        }

        public Request(Activity activity) {
            this(activity, null);
        }

        public Activity getActivity() {
            return activity;
        }

        public ConsentFormDismissListener getConsentFormDismissListener() {
            return consentFormDismissListener;
        }
    }
}
