package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMutableLiveData;

import timber.log.Timber;

public abstract class AbstractConsentRepository implements ConsentRepository {

    @Override
    public LiveData<Resource<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest) {
        ResourceMutableLiveData<Consent> result = new ResourceMutableLiveData<>();
        loadConsentStatusInternal(consentLoadRequest, result);
        return result;
    }

    private void loadConsentStatusInternal(ConsentLoadRequest consentLoadRequest,
                                           ResourceMutableLiveData<Consent> result) {
        result.setLoading();
        try {
            tryLoadingConsentStatus(consentLoadRequest, result);
        } catch (Throwable error) {
            Timber.e(error);
            result.setError(error);
        }
    }

    protected abstract void tryLoadingConsentStatus(ConsentLoadRequest consentLoadRequest,
                                                    ResourceMutableLiveData<Consent> result);
}
