package com.abatra.android.allowance;

import com.abatra.android.wheelie.lifecycle.ILifecycleObserver;

public interface ConsentStatusLoader extends ILifecycleObserver {

    void loadConsentStatus(LoadConsentStatusRequest request);

    interface Listener {

        default void loadedSuccessfully(ConsentStatusLoaderResponse response) {
        }

        default void onConsentStatusLoadFailure(Throwable error) {
        }
    }


}
