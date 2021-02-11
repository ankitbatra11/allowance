package com.abatra.android.allowance;

import androidx.annotation.Nullable;

import java.util.Optional;

abstract public class AbstractConsentStatusLoader implements ConsentStatusLoader {

    private final ConsentStatusStore consentStatusStore;
    @Nullable
    protected ConsentStatusLoaderResponse response;

    protected AbstractConsentStatusLoader(ConsentStatusStore consentStatusStore) {
        this.consentStatusStore = consentStatusStore;
    }

    @Override
    public void loadConsentStatus(LoadConsentStatusRequest request) {
        LoadListenerWrapper statusLoaderListener = new LoadListenerWrapper(request.getStatusLoaderListener().orElse(null)) {
            @Override
            public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
                consentStatusStore.loadedSuccessfully(response);
                super.loadedSuccessfully(response);
            }

            @Override
            public void onConsentStatusLoadFailure(Throwable error) {
                consentStatusStore.onConsentStatusLoadFailure(error);
                super.onConsentStatusLoadFailure(error);
            }
        };
        try {
            tryLoadingConsentStatus(request.setStatusLoaderListener(statusLoaderListener));
        } catch (Throwable error) {
            statusLoaderListener.onConsentStatusLoadFailure(error);
        }
    }

    protected abstract void tryLoadingConsentStatus(LoadConsentStatusRequest loadConsentStatusRequest);

    @Override
    public void onDestroy() {
        response = null;
    }

    private static class LoadListenerWrapper implements ConsentStatusLoader.Listener {

        @Nullable
        private final ConsentStatusLoader.Listener listener;

        private LoadListenerWrapper(@Nullable Listener listener) {
            this.listener = listener;
        }

        @Override
        public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
            getListener().ifPresent(l -> l.loadedSuccessfully(response));
        }

        private Optional<Listener> getListener() {
            return Optional.ofNullable(listener);
        }

        @Override
        public void onConsentStatusLoadFailure(Throwable error) {
            getListener().ifPresent(l -> l.onConsentStatusLoadFailure(error));
        }
    }
}
