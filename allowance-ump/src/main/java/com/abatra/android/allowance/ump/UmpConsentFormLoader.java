package com.abatra.android.allowance.ump;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentFormLoader;
import com.abatra.android.allowance.ConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.LoadConsentFormRequest;
import com.abatra.android.allowance.ShowConsentFormRequest;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.UserMessagingPlatform;

public class UmpConsentFormLoader extends AbstractConsentFormLoader implements ConsentStatusLoader.Listener {

    @Nullable
    private ConsentForm consentForm;

    public UmpConsentFormLoader(ConsentStatusLoader consentStatusLoader) {
        super(consentStatusLoader);
    }

    @Override
    protected void tryLoadingConsentForm(LoadConsentFormRequest request,
                                         ConsentStatusLoaderResponse response) {
        if (response.isConsentFormAvailable()) {
            UserMessagingPlatform.loadConsentForm(request.getActivity(),
                    consentForm -> {
                        this.consentForm = consentForm;
                        this.response = new Response(response, true);
                        if (request.getFormLoaderListener() != null) {
                            request.getFormLoaderListener().consentFormLoadedSuccessfully(this.response);
                        }
                    },
                    formError -> {
                        if (request.getFormLoaderListener() != null) {
                            RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                            request.getFormLoaderListener().loadingConsentFormFailed(error);
                        }
                    });
        } else {
            throw new IllegalStateException("Consent form is not available. Req=" + request + " status resp=" + response);
        }
    }

    @Override
    public void showConsentForm(ShowConsentFormRequest request) {
        if (consentForm != null) {
            consentForm.show(request.getActivity(), formError -> {
                if (formError != null) {
                    RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                    request.getConsentFormDismissListener().dismissingConsentFormFailed(error);
                } else {
                    request.getConsentFormDismissListener().consentFormDismissedSuccessfully();
                }
            });
        }
    }
}
