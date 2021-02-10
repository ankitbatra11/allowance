package com.abatra.android.allowance;

import androidx.annotation.Nullable;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;

import java.net.MalformedURLException;

import timber.log.Timber;

abstract public class AbstractConsentFormLoader implements ConsentFormLoader {

    protected final ConsentStatusLoader consentStatusLoader;
    protected ILifecycleOwner lifecycleOwner;
    @Nullable
    protected ConsentFormLoader.Response response;

    protected AbstractConsentFormLoader(ConsentStatusLoader consentStatusLoader) {
        this.consentStatusLoader = consentStatusLoader;
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        consentStatusLoader.observeLifecycle(this.lifecycleOwner);
        this.lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void loadConsentFormIfConsentIsRequired(LoadConsentFormRequest request) {
        consentStatusLoader.loadConsentStatus(request.setStatusLoaderListener(new ConsentStatusLoader.Listener() {

            @Override
            public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
                if (response.isConsentRequired()) {
                    loadConsentForm(request, response);
                } else {
                    Timber.d("not loading consent form as consent is not required");
                }
            }

            @Override
            public void onConsentStatusLoadFailure(Throwable error) {
                Timber.e(error, "Failed to load consent status!");
                if (request.getFormLoaderListener() != null) {
                    request.getFormLoaderListener().loadingConsentFormFailed(error);
                }
            }
        }));
    }

    protected abstract void tryLoadingConsentForm(LoadConsentFormRequest request, ConsentStatusLoaderResponse response) throws MalformedURLException;

    @Override
    public void loadConsentForm(LoadConsentFormRequest request) {
        if (isFormLoaded()) {
            if (request.getFormLoaderListener() != null) {
                request.getFormLoaderListener().consentFormLoadedSuccessfully(response);
            }
        } else {
            consentStatusLoader.loadConsentStatus(request.setStatusLoaderListener(new ConsentStatusLoader.Listener() {

                @Override
                public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
                    loadConsentForm(request, response);
                }

                @Override
                public void onConsentStatusLoadFailure(Throwable error) {
                    Timber.e(error, "Failed to load consent status!");
                    if (request.getFormLoaderListener() != null) {
                        request.getFormLoaderListener().loadingConsentFormFailed(error);
                    }
                }
            }));
        }
    }

    protected boolean isFormLoaded() {
        return response != null && response.isConsentFormLoaded();
    }

    private void loadConsentForm(LoadConsentFormRequest request, ConsentStatusLoaderResponse response) {
        Timber.d("loading consent form for req=%s statusResp=%s", request, response);
        try {
            tryLoadingConsentForm(request, response);
        } catch (Throwable error) {
            if (request.getFormLoaderListener() != null) {
                request.getFormLoaderListener().loadingConsentFormFailed(error);
            }
        }
    }
}
