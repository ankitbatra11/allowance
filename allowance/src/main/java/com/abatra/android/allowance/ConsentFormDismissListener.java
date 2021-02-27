package com.abatra.android.allowance;

public interface ConsentFormDismissListener {

    default void consentFormDismissedSuccessfully(boolean userPickedAdsFreeOption) {
    }

    default void dismissingConsentFormFailed(Throwable error) {
    }

    class Wrapper implements ConsentFormDismissListener {

        private final ConsentFormDismissListener listener;

        public Wrapper(ConsentFormDismissListener listener) {
            this.listener = listener;
        }

        @Override
        public void consentFormDismissedSuccessfully(boolean userPickedAdsFreeOption) {
            listener.consentFormDismissedSuccessfully(userPickedAdsFreeOption);
        }

        @Override
        public void dismissingConsentFormFailed(Throwable error) {
            listener.dismissingConsentFormFailed(error);
        }
    }
}
