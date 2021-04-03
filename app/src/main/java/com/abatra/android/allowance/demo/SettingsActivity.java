package com.abatra.android.allowance.demo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.abatra.android.allowance.ConsentFormRepository;
import com.abatra.android.allowance.ConsentRepository;
import com.abatra.android.allowance.PreferenceConsentRepository;
import com.abatra.android.allowance.consent.lib.ConsentFactory;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormRepository;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentRepository;
import com.abatra.android.allowance.demo.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ConsentRepository consentRepository;
    ConsentFormRepository consentFormRepository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        ConsentFactory consentFactory = new ConsentFactory(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ConsentLibConsentRepository delegate = new ConsentLibConsentRepository(getApplicationContext(), consentFactory);
        consentRepository = PreferenceConsentRepository.newInstance(delegate, sharedPreferences);
        getLifecycle().addObserver(consentRepository);

        consentFormRepository = new ConsentLibConsentFormRepository(consentRepository, consentFactory);
        getLifecycle().addObserver(consentFormRepository);

        consentFormRepository.loadConsentForm(Utils.updateConsentFormLoadRequest(this)).observe(this, booleanResource -> {
            switch (booleanResource.getStatus()) {
                case LOADING:
                    binding.buttonShowConsentForm.setVisibility(View.INVISIBLE);
                    binding.progressLoadingConsentForm.setVisibility(View.VISIBLE);
                    break;
                case LOADED:
                    binding.buttonShowConsentForm.setVisibility(View.VISIBLE);
                    binding.progressLoadingConsentForm.setVisibility(View.INVISIBLE);
                    break;
            }
        });
        binding.buttonShowConsentForm.setOnClickListener(v -> consentFormRepository.showConsentForm((consent, userPrefersAdFreeOption) -> {
        }));
    }
}
