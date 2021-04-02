package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMutableLiveData;

import java.util.concurrent.Executor;

import bolts.Task;
import timber.log.Timber;

import static com.abatra.android.wheelie.thread.SaferTask.callOn;

public abstract class AbstractConsentRepository implements ConsentRepository {

    Executor backgroundExecutor = Task.BACKGROUND_EXECUTOR;

    @Override
    public LiveData<Resource<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest) {
        ResourceMutableLiveData<Consent> result = new ResourceMutableLiveData<>();
        loadConsentStatusInternal(consentLoadRequest, result);
        return result;
    }

    private void loadConsentStatusInternal(ConsentLoadRequest consentLoadRequest,
                                           ResourceMutableLiveData<Consent> result) {
        result.setLoading();
        callOn(backgroundExecutor, () -> {
            try {
                tryLoadingConsentStatus(consentLoadRequest, result);
            } catch (Throwable error) {
                Timber.e(error);
                result.postError(error);
            }
            return null;
        });
    }

    protected abstract void tryLoadingConsentStatus(ConsentLoadRequest consentLoadRequest,
                                                    ResourceMutableLiveData<Consent> result);
}
