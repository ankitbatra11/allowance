package com.abatra.android.allowance.demo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;

import androidx.appcompat.app.AppCompatActivity;

import com.abatra.android.allowance.ConsentFormRepository;
import com.abatra.android.allowance.ConsentRepository;
import com.abatra.android.allowance.PreferenceConsentRepository;
import com.abatra.android.allowance.consent.lib.ConsentFactory;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentFormRepository;
import com.abatra.android.allowance.consent.lib.ConsentLibConsentRepository;
import com.abatra.android.allowance.demo.databinding.ActivityMainBinding;
import com.abatra.android.wheelie.lifecycle.Lce;
import com.abatra.android.wheelie.lifecycle.owner.ILifecycleOwner;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    ConsentRepository consentRepository;
    ConsentFormRepository consentFormRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Timber.plant(new Timber.DebugTree());

        ActivityMainBinding binding = ActivityMainBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        binding.buttonSettings.setOnClickListener(v -> startActivity(new Intent(v.getContext(), SettingsActivity.class)));

        ConsentFactory consentFactory = new ConsentFactory(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ConsentLibConsentRepository delegate = new ConsentLibConsentRepository(getApplicationContext(), consentFactory);
        consentRepository = PreferenceConsentRepository.newInstance(delegate, sharedPreferences);

        consentFormRepository = new ConsentLibConsentFormRepository(consentRepository, consentFactory);
        consentFormRepository.observeLifecycle(ILifecycleOwner.activity(this));

        consentFormRepository.loadConsentForm(Utils.obtainConsentFormLoadRequest(this)).observe(this, booleanResource -> {
            if (booleanResource.getStatus() == Lce.Status.LOADED) {
                consentFormRepository.showConsentForm((consent, userPrefersAdFreeOption) -> {
                });
            }
        });
    }
}