package com.abatra.android.allowance;

import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.core.Lce;
import com.abatra.android.wheelie.lifecycle.liveData.LceMutableLiveData;
import com.abatra.android.wheelie.lifecycle.owner.ILifecycleOwner;

import java.util.concurrent.Executor;

import bolts.Task;
import timber.log.Timber;

import static com.abatra.android.wheelie.core.async.bolts.SaferTask.callOn;

public abstract class AbstractConsentRepository implements ConsentRepository {

    Executor backgroundExecutor = Task.BACKGROUND_EXECUTOR;

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public LiveData<Lce<Consent>> loadConsentStatus(ConsentLoadRequest consentLoadRequest) {
        LceMutableLiveData<Consent> result = new LceMutableLiveData<>();
        loadConsentStatusInternal(consentLoadRequest, result);
        return result;
    }

    private void loadConsentStatusInternal(ConsentLoadRequest consentLoadRequest,
                                           LceMutableLiveData<Consent> result) {
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
                                                    LceMutableLiveData<Consent> result);
}
