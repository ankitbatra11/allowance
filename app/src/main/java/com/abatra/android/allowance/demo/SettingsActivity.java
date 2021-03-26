package com.abatra.android.allowance.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.IConsentForm;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormLoadRequest;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormRepository;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentRepository;
import com.abatra.android.allowance.demo.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ConsentLibConsentRepository consentRepository;
    ConsentLibConsentFormRepository consentFormRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        consentRepository = new ConsentLibConsentRepository(getApplicationContext());
        consentFormRepository = new ConsentLibConsentFormRepository(consentRepository);

        consentFormRepository.getConsentFormResourceLiveData().observe(this, booleanResource -> {
            switch (booleanResource.getStatus()) {
                case LOADING:
                    binding.buttonShowConsentForm.setVisibility(View.INVISIBLE);
                    binding.progressLoadingConsentForm.setVisibility(View.VISIBLE);
                    break;
                case LOADED:
                    binding.buttonShowConsentForm.setVisibility(View.VISIBLE);
                    binding.progressLoadingConsentForm.setVisibility(View.INVISIBLE);
                    break;
                case FAILED:
                    break;
            }
        });

        ConsentLibConsentFormLoadRequest consentFormLoadRequest = new ConsentLibConsentFormLoadRequest(
                Constants.CONSENT_LOAD_REQUEST,
                Consent.Status.obtained(),
                this,
                "http://www.this.app.com/privacyPolicy");

        consentFormLoadRequest.setLoadFormOnClose(true);
        consentFormRepository.loadConsentForm(consentFormLoadRequest);

        binding.buttonShowConsentForm.setOnClickListener(v -> consentFormRepository.getLoadedConsentForm().ifPresent(IConsentForm::show));
    }
}
