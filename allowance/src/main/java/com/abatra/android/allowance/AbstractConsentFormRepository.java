package com.abatra.android.allowance;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;
import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMediatorLiveData;

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
    public LiveData<Resource<Boolean>> loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest) {

        this.consentFormLoadRequest = consentFormLoadRequest;

        ResourceMediatorLiveData<Boolean> result = new ResourceMediatorLiveData<>();

        ConsentLoadRequest consentLoadRequest = consentFormLoadRequest.getConsentLoadRequest();
        result.addSource(consentRepository.loadConsentStatus(consentLoadRequest), r -> checkConsentAndLoadForm(r, result));

        result.setLoading();

        return result;
    }

    protected void checkConsentAndLoadForm(Resource<Consent> consentResource,
                                           ResourceMediatorLiveData<Boolean> result) {

        boolean meetsCriteria = getConsentFormLoadRequest()
                .map(ConsentFormLoadRequest::getRequiredConsentStatuses)
                .map(reqStatus -> consentResource.getStatus() == Resource.Status.LOADED && reqStatus.contains(consentResource.getData().getStatus()))
                .orElse(false);

        if (meetsCriteria) {
            withRequiredConsentLoadConsentForm(consentFormLoadRequest, result);
        } else {
            result.postError(new RuntimeException("Does not meet consent status criteria in req=" + consentFormLoadRequest));
        }
        Timber.d("consentResource=%s consentFormLoadRequest=%s meetsCriteria=%b",
                consentResource, consentFormLoadRequest, meetsCriteria);
    }

    protected Optional<ConsentFormLoadRequest> getConsentFormLoadRequest() {
        return Optional.ofNullable(consentFormLoadRequest);
    }

    private void withRequiredConsentLoadConsentForm(ConsentFormLoadRequest formLoadRequest,
                                                    ResourceMediatorLiveData<Boolean> result) {
        try {
            tryLoadingConsentForm(formLoadRequest, result);
        } catch (Throwable error) {
            result.postError(error);
        }
    }

    protected abstract void tryLoadingConsentForm(ConsentFormLoadRequest consentFormLoadRequest,
                                                  ResourceMediatorLiveData<Boolean> result);

    @Override
    @CallSuper
    public void onDestroy() {
        Timber.i("onDestroy");
        consentFormLoadRequest = null;
    }
}
