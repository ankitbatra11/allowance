package com.abatra.android.allowance;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.abatra.android.wheelie.lifecycle.Resource;
import com.abatra.android.wheelie.lifecycle.ResourceMediatorLiveData;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Optional;

import timber.log.Timber;

public abstract class AbstractConsentFormRepository implements ConsentFormRepository {

    protected final ResourceMediatorLiveData<Boolean> consentForm = new ResourceMediatorLiveData<>();

    protected final ConsentRepository consentRepository;
    @Nullable
    private WeakReference<ConsentFormLoadRequest> weakFormLoadRequest;
    @Nullable
    protected WeakReference<IConsentForm> weakConsentForm;

    protected AbstractConsentFormRepository(ConsentRepository consentRepository) {
        this.consentRepository = consentRepository;
    }

    @Override
    public LiveData<Resource<Boolean>> getConsentFormResourceLiveData() {
        if (consentForm.getValue() == null) {
            consentForm.addSource(consentRepository.getConsentStatusResourceLiveData(), this::onConsentUpdated);
        }
        return consentForm;
    }

    private void onConsentUpdated(Resource<Consent> consentResource) {

        Optional<ConsentFormLoadRequest> consentFormLoadRequest = Optional.ofNullable(weakFormLoadRequest).map(Reference::get);
        boolean meetsCriteria = consentFormLoadRequest
                .map(ConsentFormLoadRequest::getRequiredConsentStatuses)
                .map(reqStatus -> consentResource.getStatus() == Resource.Status.LOADED && reqStatus.contains(consentResource.getData().getStatus()))
                .orElse(false);

        if (meetsCriteria) {
            withRequiredConsentLoadConsentForm(weakFormLoadRequest.get());
        } else {
            consentForm.setError(new RuntimeException("Does not meet consent status criteria in req=" + consentFormLoadRequest));
        }
        Timber.d("consentResource=%s weakFormLoadRequest=%s meetsCriteria=%b",
                consentResource, weakFormLoadRequest, meetsCriteria);
    }

    private void withRequiredConsentLoadConsentForm(ConsentFormLoadRequest formLoadRequest) {
        try {
            tryLoadingConsentForm(formLoadRequest);
        } catch (Throwable error) {
            consentForm.setError(error);
        }
    }

    @Override
    public void loadConsentForm(ConsentFormLoadRequest consentFormLoadRequest) {
        weakFormLoadRequest = new WeakReference<>(consentFormLoadRequest);
        consentForm.setLoading();
        consentRepository.loadConsentStatus(consentFormLoadRequest.getConsentLoadRequest());
    }

    protected abstract void tryLoadingConsentForm(ConsentFormLoadRequest consentFormLoadRequest);

    @Override
    public Optional<IConsentForm> getLoadedConsentForm() {
        return Optional.ofNullable(weakConsentForm)
                .map(WeakReference::get);
    }
}
