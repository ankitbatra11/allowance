package com.abatra.android.allowance.consent.lib;

import android.app.Application;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentFormLoader;
import com.abatra.android.allowance.ConsentFormDismissListener;
import com.abatra.android.allowance.ConsentFormShower;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class LegacyConsentFormLoader extends AbstractConsentFormLoader {

    private static final String PRIVACY_POLICY_URL = "http://screenshotcapture.blogspot.in/2017/02/privacy-policy.html";

    private final AtomicBoolean formLoaded = new AtomicBoolean(false);
    private final FormListener formListener = new FormListener();

    @Nullable
    private ConsentForm consentForm;

    public LegacyConsentFormLoader(LegacyConsentStatusLoader consentStatusLoader, Application application) {
        super(consentStatusLoader, application);
    }

    @Override
    protected boolean isConsentFormLoaded() {
        return formLoaded.get();
    }

    @Override
    protected void tryLoadingConsentForm(boolean notifyResponse) throws MalformedURLException {

        setConsentFormLoading(true);
        formLoaded.set(false);
        consentForm = null;

        formListener.setNotifyResponse(notifyResponse);
        consentForm = new ConsentForm.Builder(lifecycleOwner.getContext(), new URL(PRIVACY_POLICY_URL))
                .withListener(formListener)
                .withNonPersonalizedAdsOption()
                .withPersonalizedAdsOption()
                .build();

        consentForm.load();
    }

    @Override
    protected Response createConsentFormLoaderResponse(ConsentStatusLoaderResponse consentStatusLoaderResponse) {
        return new Response(consentStatusLoaderResponse, formLoaded.get());
    }

    @Override
    public void showConsentForm(ConsentFormShower.Request request) {
        if (consentForm != null && !consentForm.isShowing()) {
            formListener.setConsentFormDismissListener(request.getConsentFormDismissListener());
            consentForm.show();
        }
    }

    private class FormListener extends ConsentFormListener {

        @Nullable
        private ConsentFormDismissListener consentFormDismissListener;
        private boolean notifyResponse = false;

        public void setConsentFormDismissListener(@Nullable ConsentFormDismissListener consentFormDismissListener) {
            this.consentFormDismissListener = consentFormDismissListener;
        }

        public void setNotifyResponse(boolean notifyResponse) {
            this.notifyResponse = notifyResponse;
        }

        @Override
        public void onConsentFormLoaded() {
            super.onConsentFormLoaded();
            onConsentFormLoadResponse(null);
        }

        @Override
        public void onConsentFormError(String reason) {
            super.onConsentFormError(reason);
            onConsentFormLoadResponse(reason);
        }

        private void onConsentFormLoadResponse(String error) {
            formLoaded.set(error == null);
            setConsentFormLoading(false);
            if (notifyResponse) {
                if (error != null) {
                    notifyError(new RuntimeException("Consent form error. Reason=" + error));
                } else {
                    notifyResult();
                }
            }
            Timber.d("onConsentFormLoadResponse error=%s", error);
        }

        @Override
        public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
            super.onConsentFormClosed(consentStatus, userPrefersAdFree);
            setNewConsentStatus(consentStatus);
            loadConsentForm(false);
            if (consentFormDismissListener != null) {
                consentFormDismissListener.consentFormDismissedSuccessfully();
            }
        }
    }

    private void setNewConsentStatus(ConsentStatus newConsentStatus) {
        LegacyConsentStatusLoader statusLoader = (LegacyConsentStatusLoader) LegacyConsentFormLoader.this.consentStatusLoader;
        ConsentInformation information = statusLoader.getConsentInformation();
        if (information != null) {
            LegacyConsentFormLoader.this.consentStatusLoaderResponse = ConsentUtils.createResponse(information, newConsentStatus);
        }
    }
}
