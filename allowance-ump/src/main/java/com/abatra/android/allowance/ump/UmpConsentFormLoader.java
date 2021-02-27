package com.abatra.android.allowance.ump;

import androidx.annotation.Nullable;

import com.abatra.android.allowance.AbstractConsentFormLoader;
import com.abatra.android.allowance.ConsentStatusLoader;
import com.abatra.android.allowance.ConsentStatusLoaderResponse;
import com.abatra.android.allowance.LoadConsentFormRequest;
import com.abatra.android.allowance.ShowConsentFormRequest;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.UserMessagingPlatform;

import java.util.Optional;

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
            UserMessagingPlatform.loadConsentForm(request.getConsentRequest().getActivity(),
                    consentForm -> {
                        this.consentForm = consentForm;
                        Response formLoadResponse = new Response(response, true);
                        setResponse(formLoadResponse);
                        request.getFormLoaderListener().ifPresent(l -> l.consentFormLoadedSuccessfully(formLoadResponse));
                    },
                    formError -> {
                        Optional<Listener> formLoaderListener = request.getFormLoaderListener();
                        formLoaderListener.ifPresent(l -> {
                            RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                            l.loadingConsentFormFailed(error);
                        });
                    });
        } else {
            throw new IllegalStateException("Consent form is not available. Req=" + request + " status resp=" + response);
        }
    }

    @Override
    protected void doShowConsentForm(ShowConsentFormRequest request) {
        getConsentForm().ifPresent(cf -> cf.show(request.getConsentRequest().getActivity(), formError -> {
            if (formError != null) {
                RuntimeException error = new RuntimeException(UmpConsentUtils.toString(formError));
                request.getConsentFormDismissListener().dismissingConsentFormFailed(error);
            } else {
                request.getConsentFormDismissListener().consentFormDismissedSuccessfully(false);
            }
        }));
    }

    private Optional<ConsentForm> getConsentForm() {
        return Optional.ofNullable(consentForm);
    }

    @Override
    protected void invalidateCurrentForm() {
        consentForm = null;
    }
}
