package com.abatra.android.allowance;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;
import com.abatra.android.wheelie.network.InternetConnectionObserver;

public class InternetConnectedConsentStatusLoader implements ConsentStatusLoader {

    private final InternetConnectionObserver internetConnectionObserver;
    private final ConsentStatusLoader delegate;

    public InternetConnectedConsentStatusLoader(InternetConnectionObserver internetConnectionObserver,
                                                ConsentStatusLoader delegate) {
        this.internetConnectionObserver = internetConnectionObserver;
        this.delegate = delegate;
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        internetConnectionObserver.observeLifecycle(lifecycleOwner);
        delegate.observeLifecycle(lifecycleOwner);
    }

    @Override
    public void loadConsentStatus(LoadConsentStatusRequest request) {
        if (internetConnectionObserver.isConnectedToInternet()) {
            delegate.loadConsentStatus(request);
        } else {
            request.getStatusLoaderListener().ifPresent(listener -> {
                RuntimeException error = new RuntimeException("Not connected to internet!");
                listener.onConsentStatusLoadFailure(error);
            });
        }
    }
}
