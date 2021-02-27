package com.abatra.android.allowance;

import androidx.annotation.Nullable;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;

import java.net.MalformedURLException;
import java.util.Optional;

import timber.log.Timber;

abstract public class AbstractConsentFormLoader implements ConsentFormLoader {

    protected final ConsentStatusLoader consentStatusLoader;
    protected ILifecycleOwner lifecycleOwner;
    @Nullable
    private ConsentFormLoader.Response response;

    protected AbstractConsentFormLoader(ConsentStatusLoader consentStatusLoader) {
        this.consentStatusLoader = consentStatusLoader;
    }

    protected void setResponse(@Nullable Response response) {
        this.response = response;
    }

    protected Optional<Response> getResponse() {
        return Optional.ofNullable(response);
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        consentStatusLoader.observeLifecycle(this.lifecycleOwner);
        this.lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void loadConsentFormIfConsentIsRequired(LoadConsentFormRequest request) {
        loadConsentStatus(request, new ConsentStatusLoader.Listener() {

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
                request.getFormLoaderListener().ifPresent(listener -> listener.loadingConsentFormFailed(error));
            }
        });
    }

    private void loadConsentStatus(LoadConsentFormRequest request, ConsentStatusLoader.Listener listener) {
        LoadConsentStatusRequest statusRequest = new LoadConsentStatusRequest(request.getConsentRequest());
        consentStatusLoader.loadConsentStatus(statusRequest.setStatusLoaderListener(listener));
    }

    protected abstract void tryLoadingConsentForm(LoadConsentFormRequest request, ConsentStatusLoaderResponse response) throws MalformedURLException;

    @Override
    public void loadConsentFormIfConsentIsAcquired(LoadConsentFormRequest request) {
        loadConsentStatus(request, new ConsentStatusLoader.Listener() {

            @Override
            public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
                if (response.isConsentAcquired()) {
                    loadFormIfNotLoaded(request, response);
                } else {
                    RuntimeException error = new RuntimeException("Consent is not acquired!");
                    request.getFormLoaderListener().ifPresent(listener -> listener.loadingConsentFormFailed(error));
                }
            }

            @Override
            public void onConsentStatusLoadFailure(Throwable error) {
                request.getFormLoaderListener().ifPresent(listener -> listener.loadingConsentFormFailed(error));
            }
        });
    }

    private void loadFormIfNotLoaded(LoadConsentFormRequest request, ConsentStatusLoaderResponse response) {
        if (isFormLoaded()) {
            request.getFormLoaderListener().ifPresent(listener -> {
                Response loadFormResponse = AbstractConsentFormLoader.this.response;
                listener.consentFormLoadedSuccessfully(loadFormResponse);
            });
        } else {
            loadConsentForm(request, response);
        }
    }

    protected boolean isFormLoaded() {
        return getResponse().map(Response::isConsentFormLoaded).orElse(false);
    }

    private void loadConsentForm(LoadConsentFormRequest request, ConsentStatusLoaderResponse response) {
        Timber.d("loading consent form for req=%s statusResp=%s", request, response);
        try {
            tryLoadingConsentForm(request, response);
        } catch (Throwable error) {
            request.getFormLoaderListener().ifPresent(listener -> listener.loadingConsentFormFailed(error));
        }
    }

    @Override
    public void showConsentForm(ShowConsentFormRequest request) {
        request.setConsentFormDismissListener(new ConsentFormDismissListener.Wrapper(request.getConsentFormDismissListener()) {

            @Override
            public void consentFormDismissedSuccessfully(boolean userPickedAdsFreeOption) {
                response = null;
                invalidateCurrentForm();
                loadConsentForm(request);
                super.consentFormDismissedSuccessfully(userPickedAdsFreeOption);
            }
        });
        doShowConsentForm(request);
    }

    private void loadConsentForm(ShowConsentFormRequest request) {
        LoadConsentStatusRequest statusRequest = new LoadConsentStatusRequest(request.getConsentRequest());
        consentStatusLoader.loadConsentStatus(statusRequest.setStatusLoaderListener(new ConsentStatusLoader.Listener() {
            @Override
            public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
                loadConsentForm(new LoadConsentFormRequest(request.getConsentRequest()), response);
            }
        }));
    }

    protected abstract void invalidateCurrentForm();

    protected abstract void doShowConsentForm(ShowConsentFormRequest request);
}
