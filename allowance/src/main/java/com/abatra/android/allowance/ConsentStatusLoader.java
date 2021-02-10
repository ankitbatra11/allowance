package com.abatra.android.allowance;

import android.app.Activity;

import com.abatra.android.wheelie.lifecycle.LifecycleObserverObservable;

public interface ConsentStatusLoader extends LifecycleObserverObservable<ConsentStatusLoader.Listener> {

    void loadConsentStatus(Request request);

    class Request {

        private final Activity activity;

        public Request(Activity activity) {
            this.activity = activity;
        }

        public Activity getActivity() {
            return activity;
        }
    }

    interface Listener {

        default void loadedSuccessfully(ConsentStatusLoaderResponse response) {
        }

        default void onConsentStatusLoadFailure(Throwable error) {
        }
    }


}
