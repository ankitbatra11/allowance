package com.abatra.android.allowance;

import androidx.lifecycle.MutableLiveData;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;
import com.abatra.android.wheelie.network.InternetConnectivityChecker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InternetConnectedConsentStatusLoaderTest {

    @Mock
    private InternetConnectivityChecker mockedInternetConnectionChecker;

    @Mock
    private ConsentStatusLoader mockedConsentStatusLoader;

    @InjectMocks
    private InternetConnectedConsentStatusLoader internetConnectedConsentStatusLoader;

    @Mock
    private LoadConsentStatusRequest mockedLoadConsentStatusRequest;

    @Mock
    private ConsentStatusLoader.Listener mockedListener;

    @Mock
    private ILifecycleOwner mockedLifecycleOwner;

    @Before
    public void setup() {
        when(mockedLoadConsentStatusRequest.getStatusLoaderListener()).thenReturn(Optional.of(mockedListener));
    }

    @Test
    public void test_loadConsentStatus_connectedToInternet() {

        when(mockedInternetConnectionChecker.isConnectedToInternet()).thenReturn(new MutableLiveData<>(true));

        internetConnectedConsentStatusLoader.loadConsentStatus(mockedLoadConsentStatusRequest);

        verify(mockedConsentStatusLoader, times(1)).loadConsentStatus(mockedLoadConsentStatusRequest);
    }

    @Test
    public void test_loadConsentStatus_notConnectedToInternet() {

        when(mockedInternetConnectionChecker.isConnectedToInternet()).thenReturn(new MutableLiveData<>(false));

        internetConnectedConsentStatusLoader.loadConsentStatus(mockedLoadConsentStatusRequest);

        verify(mockedListener, times(1)).onConsentStatusLoadFailure(any(RuntimeException.class));
    }

    @Test
    public void test_observeLifecycle() {

        internetConnectedConsentStatusLoader.observeLifecycle(mockedLifecycleOwner);

        verify(mockedConsentStatusLoader, times(1)).observeLifecycle(mockedLifecycleOwner);
        verify(mockedInternetConnectionChecker, times(1)).observeLifecycle(mockedLifecycleOwner);
    }
}
