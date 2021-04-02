package com.abatra.android.allowance.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.abatra.android.allowance.Consent;
import com.abatra.android.allowance.consent.lib.ConsentFactory;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormRepository;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentRepository;
import com.abatra.android.allowance.demo.databinding.ActivityMainBinding;
import com.abatra.android.wheelie.lifecycle.Resource;

import timber.log.Timber;

import static com.abatra.android.allowance.demo.Utils.createFormLoadRequest;

public class MainActivity extends AppCompatActivity {

    ConsentLibConsentRepository consentRepository;
    ConsentLibConsentFormRepository consentFormRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());

        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.buttonSettings.setOnClickListener(v -> startActivity(new Intent(v.getContext(), SettingsActivity.class)));

        ConsentFactory consentFactory = new ConsentFactory(this);
        consentRepository = new ConsentLibConsentRepository(getApplicationContext(), consentFactory);
        consentFormRepository = new ConsentLibConsentFormRepository(consentRepository, consentFactory);
        getLifecycle().addObserver(consentFormRepository);

        consentFormRepository.loadConsentForm(createFormLoadRequest(this, Consent.Status.required())).observe(this, booleanResource -> {
            if (booleanResource.getStatus() == Resource.Status.LOADED) {
                consentFormRepository.showConsentForm((consent, userPrefersAdFreeOption) -> {
                });
            }
        });
    }
}