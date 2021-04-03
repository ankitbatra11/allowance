package com.abatra.android.allowance;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.abatra.android.wheelie.lifecycle.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import bolts.Task;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class PreferenceConsentRepositoryTest {

    private PreferenceConsentRepository repository;

    @Mock
    private ConsentRepository mockedConsentRepository;

    @Mock
    private LifecycleOwner mockedLifecycleOwner;

    @Mock
    private Observer<Resource<Consent>> mockedConsentResourceObserver;

    @Captor
    private ArgumentCaptor<Resource<Consent>> consentResourceArgumentCaptor;

    private SharedPreferences sharedPreferences;

    private final MutableLiveData<Resource<Consent>> consentLiveData = new MutableLiveData<>();

    @Before
    public void setup() {

        MockitoAnnotations.openMocks(this);

        doAnswer(invocation ->
        {
            consentLiveData.setValue(Resource.loaded(new Consent(ConsentStatus.UNKNOWN)));
            return consentLiveData;

        }).when(mockedConsentRepository).loadConsentStatus(any());

        LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(mockedLifecycleOwner);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
        when(mockedLifecycleOwner.getLifecycle()).thenReturn(lifecycleRegistry);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        repository = PreferenceConsentRepository.newInstance(mockedConsentRepository, sharedPreferences);
        repository.backgroundExecutor = Task.UI_THREAD_EXECUTOR;
        repository.onCreate();
    }

    @After
    public void tearDown() {
        repository.onDestroy();
    }

    @Test
    public void test_preferenceHasConsent() {

        Consent consent = new Consent(ConsentStatus.OBTAINED).setType(ConsentType.PA);
        repository.upsert(consent);
        Robolectric.flushForegroundThreadScheduler();

        loadConsentStatus();

        await().untilAsserted(() -> {

            verifyObserverOnChangedCalls(consent);

            verifyNoInteractions(mockedConsentRepository);
        });

    }

    private void verifyObserverOnChangedCalls(Consent consent) {
        verify(mockedConsentResourceObserver, times(2)).onChanged(consentResourceArgumentCaptor.capture());
        assertThat(consentResourceArgumentCaptor.getAllValues(), hasSize(2));
        assertThat(consentResourceArgumentCaptor.getAllValues().get(0).getStatus(), equalTo(Resource.Status.LOADING));
        assertThat(consentResourceArgumentCaptor.getAllValues().get(0).getData(), nullValue());
        assertThat(consentResourceArgumentCaptor.getAllValues().get(1).getStatus(), equalTo(Resource.Status.LOADED));
        assertThat(consentResourceArgumentCaptor.getAllValues().get(1).getData().toString(), equalTo(consent.toString()));
    }

    private void loadConsentStatus() {
        repository.loadConsentStatus(null).observe(mockedLifecycleOwner, mockedConsentResourceObserver);
        Robolectric.flushForegroundThreadScheduler();
    }

    @Test
    public void test_preferenceConsentIsMissing() {

        sharedPreferences.edit().remove(PreferenceConsentRepository.PREF_KEY).apply();

        loadConsentStatus();

        await().untilAsserted(() -> {

            verifyObserverOnChangedCalls(new Consent(ConsentStatus.UNKNOWN));

            verify(mockedConsentRepository, times(1)).loadConsentStatus(null);
        });
    }
}
