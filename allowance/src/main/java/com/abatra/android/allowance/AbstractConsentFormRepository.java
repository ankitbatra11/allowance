package com.abatra.android.allowance;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMediatorLiveData;

import java.util.Optional;

import timber.log.Timber;

public abstract class AbstractConsentFormRepository implements ConsentFormRepository {

    protected final ResourceMediatorLiveData<Boolean> consentFormResource = new ResourceMediatorLiveData<>();

    protected final ConsentRepository consentRepository;

    @Nullable
    protected ConsentFormLoadRequest consentFormLoadRequest;

    @Nullable
    protected IConsentForm consentForm;

    protected AbstractConsentFormRepository(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    @Override
    public LiveData<Resource<Boolean>> loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest) {

        this.consentFormLoadRequest = consentFormLoadRequest;

        ConsentLoadRequest consentLoadRequest = consentFormLoadRequest.getConsentLoadRequest();
        consentFormResource.removeSource(consentRepository.loadConsentStatus(consentLoadRequest));
        consentFormResource.addSource(consentRepository.loadConsentStatus(consentLoadRequest), this::checkConsentAndLoadForm);
        consentFormResource.setLoading();

        return consentFormResource;
    }

    private void checkConsentAndLoadForm(Resource<Consent> consentResource) {

        boolean meetsCriteria = getConsentFormLoadRequest()
                .map(ConsentFormLoadRequest::getRequiredConsentStatuses)
                .map(reqStatus -> consentResource.getStatus() == Resource.Status.LOADED && reqStatus.contains(consentResource.getData().getStatus()))
                .orElse(false);

        if (meetsCriteria) {
            withRequiredConsentLoadConsentForm(consentFormLoadRequest);
        } else {
            consentFormResource.setError(new RuntimeException("Does not meet consent status criteria in req=" + consentFormLoadRequest));
        }
        Timber.d("consentResource=%s consentFormLoadRequest=%s meetsCriteria=%b",
                consentResource, consentFormLoadRequest, meetsCriteria);
    }

    private Optional<ConsentFormLoadRequest> getConsentFormLoadRequest() {
        return Optional.ofNullable(consentFormLoadRequest);
    }

    private void withRequiredConsentLoadConsentForm(ConsentFormLoadRequest formLoadRequest) {
        try {
            tryLoadingConsentForm(formLoadRequest);
        } catch (Throwable error) {
            consentFormResource.setError(error);
        }
    }

    protected abstract void tryLoadingConsentForm(ConsentFormLoadRequest consentFormLoadRequest);

    @Override
    public Optional<IConsentForm> getLoadedConsentForm() {
        return Optional.ofNullable(consentForm);
    }

    @Override
    public void onDestroy() {
        Timber.i("onDestroy");
        consentForm = null;
        consentFormLoadRequest = null;
    }
}
