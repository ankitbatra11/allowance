package com.abatra.android.allowance.consent.lib;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentFormLoader;
import com.abatra.android.allowance.ConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.LoadConsentFormRequest;
import com.abatra.android.allowance.ShowConsentFormRequest;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class LegacyConsentFormLoader extends AbstractConsentFormLoader {

    @Nullable
    private FormListener formListener;

    @Nullable
    private ConsentForm consentForm;

    public LegacyConsentFormLoader(ConsentStatusLoader consentStatusLoader) {
        super(consentStatusLoader);
    }

    @Override
    protected void tryLoadingConsentForm(LoadConsentFormRequest request, ConsentStatusLoaderResponse response) throws MalformedURLException {

        formListener = new FormListener();
        formListener.setLoadConsentFormListener(new LoadConsentFormListener(request, response));

        consentForm = new ConsentForm.Builder(lifecycleOwner.getContext(), new URL(request.getPrivacyPolicyUrl()))
                .withListener(formListener)
                .withNonPersonalizedAdsOption()
                .withPersonalizedAdsOption()
                .build();

        consentForm.load();
    }

    @Override
    protected void invalidateCurrentForm() {
        consentForm = null;
        formListener = null;
    }

    @Override
    protected void doShowConsentForm(ShowConsentFormRequest request) {
        if (isFormLoaded() && !isFormShowing()) {
            getFormListener().ifPresent(l -> {
                l.setShowConsentFormListener(new ShowConsentFormListener(request));
                getConsentForm().ifPresent(ConsentForm::show);
            });
        }
    }

    private boolean isFormShowing() {
        return getConsentForm().map(ConsentForm::isShowing).orElse(false);
    }

    private Optional<FormListener> getFormListener() {
        return Optional.ofNullable(formListener);
    }

    private Optional<ConsentForm> getConsentForm() {
        return Optional.ofNullable(consentForm);
    }

    private static class FormListener extends ConsentFormListener {

        @Nullable
        private ConsentFormListener loadConsentFormListener;

        @Nullable
        private ConsentFormListener showConsentFormListener;

        public void setLoadConsentFormListener(@Nullable ConsentFormListener loadConsentFormListener) {
            this.loadConsentFormListener = loadConsentFormListener;
        }

        public void setShowConsentFormListener(@Nullable ConsentFormListener showConsentFormListener) {
            this.showConsentFormListener = showConsentFormListener;
        }

        @Override
        public void onConsentFormLoaded() {
            super.onConsentFormLoaded();
            if (loadConsentFormListener != null) {
                loadConsentFormListener.onConsentFormLoaded();
            }
        }

        @Override
        public void onConsentFormError(String reason) {
            super.onConsentFormError(reason);
            if (loadConsentFormListener != null) {
                loadConsentFormListener.onConsentFormError(reason);
            }
        }

        @Override
        public void onConsentFormOpened() {
            super.onConsentFormOpened();
            if (showConsentFormListener != null) {
                showConsentFormListener.onConsentFormOpened();
            }
        }

        @Override
        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
            super.onConsentFormClosed(consentStatus, userPrefersAdFree);
            if (showConsentFormListener != null) {
                showConsentFormListener.onConsentFormClosed(consentStatus, userPrefersAdFree);
            }
        }
    }

    private class LoadConsentFormListener extends ConsentFormListener {

        private final LoadConsentFormRequest loadConsentFormRequest;
        private final ConsentStatusLoaderResponse consentStatusLoaderResponse;

        private LoadConsentFormListener(LoadConsentFormRequest loadConsentFormRequest,
                                        ConsentStatusLoaderResponse consentStatusLoaderResponse) {
            this.loadConsentFormRequest = loadConsentFormRequest;
            this.consentStatusLoaderResponse = consentStatusLoaderResponse;
        }

        @Override
        public void onConsentFormLoaded() {
            super.onConsentFormLoaded();
            Response response = new Response(consentStatusLoaderResponse, true);
            setResponse(response);
            loadConsentFormRequest.getFormLoaderListener().ifPresent(listener -> listener.consentFormLoadedSuccessfully(response));
        }

        @Override
        public void onConsentFormError(String reason) {
            super.onConsentFormError(reason);
            loadConsentFormRequest.getFormLoaderListener().ifPresent(listener -> {
                RuntimeException error = new RuntimeException(reason);
                listener.loadingConsentFormFailed(error);
            });
        }
    }

    private static class ShowConsentFormListener extends ConsentFormListener {

        private final ShowConsentFormRequest request;

        private ShowConsentFormListener(ShowConsentFormRequest request) {
            this.request = request;
        }

        @Override
        public void onConsentFormOpened() {
            super.onConsentFormOpened();
        }

        @Override
        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
            super.onConsentFormClosed(consentStatus, userPrefersAdFree);
            request.getConsentFormDismissListener().consentFormDismissedSuccessfully();
        }
    }

}
