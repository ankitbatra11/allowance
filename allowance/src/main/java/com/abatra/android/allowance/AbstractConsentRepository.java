package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMutableLiveData;

public abstract class AbstractConsentRepository implements ConsentRepository {

    protected final ResourceMutableLiveData<Consent> consentStatus = new ResourceMutableLiveData<>();

    @Override
    public LiveData<Resource<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest) {
        if (consentStatus.getValue() == null) {
            loadConsentStatusInternal(consentLoadRequest);
        }
        return consentStatus;
    }

    private void loadConsentStatusInternal(ConsentLoadRequest consentLoadRequest) {
        consentStatus.setLoading();
        try {
            tryLoadingConsentStatus(consentLoadRequest);
        } catch (Throwable error) {
            consentStatus.setError(error);
        }
    }

    protected abstract void tryLoadingConsentStatus(ConsentLoadRequest consentLoadRequest);

    @Override
    public void upsert(Consent consent) {
        consentStatus.setResourceValue(consent);
    }
}