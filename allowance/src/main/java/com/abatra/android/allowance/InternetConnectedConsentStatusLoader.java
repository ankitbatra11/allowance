package com.abatra.android.allowance;

import com.abatra.android.wheelie.lifecycle.owner.ILifecycleOwner;
import com.abatra.android.wheelie.network.InternetConnectivityChecker;

import java.util.Optional;

public class InternetConnectedConsentStatusLoader implements ConsentStatusLoader {

    private final InternetConnectivityChecker internetConnectivityChecker;
    private final ConsentStatusLoader delegate;

    public InternetConnectedConsentStatusLoader(InternetConnectivityChecker internetConnectivityChecker,
                                                ConsentStatusLoader delegate) {
        this.internetConnectivityChecker = internetConnectivityChecker;
        this.delegate = delegate;
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        internetConnectivityChecker.observeLifecycle(lifecycleOwner);
        delegate.observeLifecycle(lifecycleOwner);
    }

    @Override
    public void loadConsentStatus(LoadConsentStatusRequest request) {
        if (isConnectedToInternet()) {
            delegate.loadConsentStatus(request);
        } else {
            request.getStatusLoaderListener().ifPresent(listener -> {
                RuntimeException error = new RuntimeException("Not connected to internet!");
                listener.onConsentStatusLoadFailure(error);
            });
        }
    }

    private boolean isConnectedToInternet() {
        return Optional.ofNullable(internetConnectivityChecker.isConnectedToInternet().getValue()).orElse(false);
    }
}
