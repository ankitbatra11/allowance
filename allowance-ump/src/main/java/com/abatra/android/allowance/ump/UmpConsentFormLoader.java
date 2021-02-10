package com.abatra.android.allowance.ump;

import android.app.Application;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentFormLoader;
import com.abatra.android.allowance.ConsentFormShower;
import com.abatra.android.allowance.ConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

public class UmpConsentFormLoader extends AbstractConsentFormLoader implements ConsentStatusLoader.Listener {

    private static final String ERR_MSG_LOADING_CONSENT_FORM = "Failed to load consent form.";

    @Nullable
    private ConsentForm consentForm;

    public UmpConsentFormLoader(ConsentStatusLoader consentStatusLoader, Application application) {
        super(consentStatusLoader, application);
    }

    @Override
    protected Response createConsentFormLoaderResponse(@Nullable ConsentStatusLoaderResponse consentStatusLoaderResponse) {
        return new Response(consentStatusLoaderResponse, consentForm != null);
    }

    @Override
    protected void tryLoadingConsentForm(boolean notifyResponse) {

        consentForm = null;
        setConsentFormLoading(true);

        UserMessagingPlatform.loadConsentForm(application,
                consentForm -> {
                    this.consentForm = consentForm;
                    setConsentFormLoading(false);
                    if (notifyResponse) {
                        notifyResult();
                    }
                },
                formError -> {
                    UmpConsentUtils.report(formError, () -> ERR_MSG_LOADING_CONSENT_FORM);
                    setConsentFormLoading(false);
                    if (notifyResponse) {
                        notifyError(new RuntimeException(UmpConsentUtils.toString(formError)));
                    }
                });
    }

    @Override
    protected boolean isConsentFormLoaded() {
        return consentForm != null;
    }

    @Override
    public void showConsentForm(ConsentFormShower.Request request) {
        if (consentForm != null) {
            consentForm.show(request.getActivity(), formError -> {
                loadConsentForm(false);
                notifyFormDismissResult(formError, request);
            });
        }
    }

    private void notifyFormDismissResult(FormError formError, ConsentFormShower.Request request) {
        if (formError != null) {
            RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
            request.getConsentFormDismissListener().dismissingConsentFormFailed(error);
        } else {
            request.getConsentFormDismissListener().consentFormDismissedSuccessfully();
        }
    }
}
