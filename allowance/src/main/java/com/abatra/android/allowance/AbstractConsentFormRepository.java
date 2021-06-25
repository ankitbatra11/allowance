package com.abatra.android.allowance;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.core.Lce;
import com.abatra.android.wheelie.lifecycle.liveData.LceMediatorLiveData;
import com.abatra.android.wheelie.lifecycle.owner.ILifecycleOwner;

import java.util.Optional;

import timber.log.Timber;

public abstract class AbstractConsentFormRepository implements ConsentFormRepository {

    protected final ConsentRepository consentRepository;

    @Nullable
    private ConsentFormLoadRequest consentFormLoadRequest;

    protected AbstractConsentFormRepository(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        consentRepository.observeLifecycle(lifecycleOwner);
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public LiveData<Lce<Boolean>> loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest) {

        this.consentFormLoadRequest = consentFormLoadRequest;

        LceMediatorLiveData<Boolean> result = new LceMediatorLiveData<>();

        ConsentLoadRequest consentLoadRequest = consentFormLoadRequest.getConsentLoadRequest();
        result.addSource(consentRepository.loadConsentStatus(consentLoadRequest), r -> checkConsentAndLoadForm(r, result));

        result.setLoading();

        return result;
    }

    protected void checkConsentAndLoadForm(Lce<Consent> consentLce,
                                           LceMediatorLiveData<Boolean> result) {

        boolean meetsCriteria = getConsentFormLoadRequest()
                .map(ConsentFormLoadRequest::getRequiredConsentStatuses)
                .map(reqStatus -> consentLce.getStatus() == Lce.Status.LOADED && reqStatus.contains(consentLce.getData().getStatus()))
                .orElse(false);

        if (meetsCriteria) {
            withRequiredConsentLoadConsentForm(consentFormLoadRequest, result);
        } else {
            result.postError(new RuntimeException("Does not meet consent status criteria in req=" + consentFormLoadRequest));
        }
        Timber.d("consentLce=%s consentFormLoadRequest=%s meetsCriteria=%b",
                consentLce, consentFormLoadRequest, meetsCriteria);
    }

    protected Optional<ConsentFormLoadRequest> getConsentFormLoadRequest() {
        return Optional.ofNullable(consentFormLoadRequest);
    }

    private void withRequiredConsentLoadConsentForm(ConsentFormLoadRequest formLoadRequest,
                                                    LceMediatorLiveData<Boolean> result) {
        try {
            tryLoadingConsentForm(formLoadRequest, result);
        } catch (Throwable error) {
            result.postError(error);
        }
    }

    protected abstract void tryLoadingConsentForm(ConsentFormLoadRequest consentFormLoadRequest,
                                                  LceMediatorLiveData<Boolean> result);

    @Override
    @CallSuper
    public void onDestroy() {
        Timber.i("onDestroy");
        consentFormLoadRequest = null;
    }
}
