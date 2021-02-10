package com.abatra.android.allowance;

import androidx.annotation.Nullable;

import com.abatra.android.wheelie.lifecycle.LifecycleObserverObservable;

public interface ConsentFormLoader extends LifecycleObserverObservable<ConsentFormLoader.Listener>, ConsentFormShower {

    void loadConsentFormIfConsentIsRequired(LoadConsentFormRequest request);

    void loadConsentForm(LoadConsentFormRequest request);

    @Override
    default void addObserver(Listener observer) {

    }

    @Override
    default void removeObserver(Listener observer) {

    }

    class Response {

        @Nullable
        private final ConsentStatusLoaderResponse response;
        private final boolean isConsentFormLoaded;

        public Response(@Nullable ConsentStatusLoaderResponse response, boolean isConsentFormLoaded) {
            this.response = response;
            this.isConsentFormLoaded = isConsentFormLoaded;
        }

        @Nullable
        public ConsentStatusLoaderResponse getResponse() {
            return response;
        }

        public boolean isConsentFormLoaded() {
            return isConsentFormLoaded;
        }

        public boolean canUserUpdateConsent() {
            return isConsentAcquired() && isConsentFormLoaded();
        }

        public boolean isConsentAcquired() {
            return getResponse() != null && getResponse().isConsentAcquired();
        }
    }


    interface Listener {

        void loadingConsentFormFailed(Throwable error);

        void consentFormLoadedSuccessfully(Response response);
    }
}
