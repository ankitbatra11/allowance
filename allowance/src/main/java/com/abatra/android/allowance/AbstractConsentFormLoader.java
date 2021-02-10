package com.abatra.android.allowance;

import android.app.Application;

import androidx.annotation.Nullable;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;
import com.abatra.android.wheelie.pattern.Observable;

import java.net.MalformedURLException;
import java.util.concurrent.atomic.AtomicBoolean;

abstract public class AbstractConsentFormLoader implements ConsentFormLoader, ConsentStatusLoader.Listener {

    protected final ConsentStatusLoader consentStatusLoader;
    protected final Application application;
    private final Observable<ConsentFormLoader.Listener> listeners = Observable.hashSet();
    private final AtomicBoolean consentFormLoading = new AtomicBoolean(false);
    private final AtomicBoolean loadConsentFormWithConsent = new AtomicBoolean(false);
    protected ILifecycleOwner lifecycleOwner;
    @Nullable
    protected ConsentStatusLoaderResponse consentStatusLoaderResponse;

    protected AbstractConsentFormLoader(ConsentStatusLoader consentStatusLoader, Application application) {
        this.consentStatusLoader = consentStatusLoader;
        this.application = application;
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {

        this.lifecycleOwner = lifecycleOwner;

        consentStatusLoader.addObserver(this);
        consentStatusLoader.observeLifecycle(this.lifecycleOwner);

        this.lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void addObserver(ConsentFormLoader.Listener observer) {
        listeners.addObserver(observer);
    }

    @Override
    public void removeObserver(ConsentFormLoader.Listener observer) {
        listeners.removeObserver(observer);
    }

    @Override
    public void loadConsentFormIfConsentIsRequired(ConsentFormLoader.Request request) {
        doLoadConsentForm(request, false);
    }

    private void doLoadConsentForm(ConsentFormLoader.Request request, boolean loadConsentFormWithConsent) {
        this.loadConsentFormWithConsent.set(loadConsentFormWithConsent);
        if (isConsentFormLoaded()) {
            notifyResult();
        } else {
            if (!consentFormLoading.getAndSet(true)) {
                if (consentStatusLoaderResponse != null) {
                    checkConsentStatusBeforeLoadingConsentForm();
                } else {
                    consentStatusLoader.loadConsentStatus(new LoadConsentStatusRequest(request.getActivity()));
                }
            }
        }
    }

    protected void notifyResult() {
        Response response = createConsentFormLoaderResponse(consentStatusLoaderResponse);
        listeners.forEachObserver(type -> type.consentFormLoadedSuccessfully(response));
    }

    protected abstract Response createConsentFormLoaderResponse(@Nullable ConsentStatusLoaderResponse consentStatusLoaderResponse);

    private void checkConsentStatusBeforeLoadingConsentForm() {
        if (consentStatusLoaderResponse.isConsentRequired()) {
            loadConsentFormIfAvailable();
        } else {
            if (loadConsentFormWithConsent.get()) {
                loadConsentFormIfAvailable();
            } else {
                setConsentFormNotLoadingNotifyResult();
            }
        }
    }

    private void setConsentFormNotLoadingNotifyResult() {
        setConsentFormLoading(false);
        notifyResult();
    }

    private void loadConsentFormIfAvailable() {
        if (consentStatusLoaderResponse != null) {
            if (consentStatusLoaderResponse.isConsentFormAvailable()) {
                loadConsentForm(true);
            } else {
                setConsentFormNotLoadingNotifyResult();
            }
        }
    }

    protected void loadConsentForm(boolean notifyResponse) {
        setConsentFormLoading(true);
        try {
            tryLoadingConsentForm(notifyResponse);
        } catch (Throwable error) {
            setConsentFormLoading(false);
            if (notifyResponse) {
                notifyError(error);
            }
        }
    }

    protected abstract void tryLoadingConsentForm(boolean notifyResponse) throws MalformedURLException;

    protected abstract boolean isConsentFormLoaded();

    protected void setConsentFormLoading(boolean loading) {
        consentFormLoading.set(loading);
    }

    @Override
    public void loadConsentForm(Request request) {
        doLoadConsentForm(request, true);
    }

    @Override
    public void loadedSuccessfully(ConsentStatusLoaderResponse response) {
        consentStatusLoaderResponse = response;
        checkConsentStatusBeforeLoadingConsentForm();
    }

    @Override
    public void onConsentStatusLoadFailure(Throwable error) {
        setConsentFormLoading(false);
        notifyError(new RuntimeException("Loading consent status failed!", error));
    }

    protected void notifyError(Throwable error) {
        listeners.forEachObserver(type -> type.loadingConsentFormFailed(error));
    }

    @Override
    public void onDestroy() {
        lifecycleOwner = null;
        listeners.removeObservers();
    }
}
