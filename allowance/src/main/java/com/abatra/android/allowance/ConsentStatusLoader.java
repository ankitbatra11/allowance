package com.abatra.android.allowance;

import com.abatra.android.wheelie.lifecycle.LifecycleObserverObservable;

public interface ConsentStatusLoader extends LifecycleObserverObservable<ConsentStatusLoader.Listener> {

    void loadConsentStatus(LoadConsentStatusRequest request);

    interface Listener {

        default void loadedSuccessfully(ConsentStatusLoaderResponse response) {
        }

        default void onConsentStatusLoadFailure(Throwable error) {
        }
    }


}
