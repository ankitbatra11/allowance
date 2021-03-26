package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMutableLiveData;

public abstract class AbstractConsentRepository implements ConsentRepository {

    protected final ResourceMutableLiveData<Consent> consentStatus = new ResourceMutableLiveData<>();

    @Override
    public LiveData<Resource<Consent>> getConsentStatusResourceLiveData() {
        return consentStatus;
    }

    @Override
    public void loadConsentStatus(ConsentLoadRequest consentLoadRequest) {
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
