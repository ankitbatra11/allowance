package com.abatra.android.allowance;

import com.abatra.android.wheelie.network.InternetConnectionObserver;

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
    private InternetConnectionObserver mockedInternetConnectionObserver;

    @Mock
    private ConsentStatusLoader mockedConsentStatusLoader;

    @InjectMocks
    private InternetConnectedConsentStatusLoader internetConnectedConsentStatusLoader;

    @Mock
    private LoadConsentStatusRequest mockedLoadConsentStatusRequest;

    @Mock
    private ConsentStatusLoader.Listener mockedListener;

    @Before
    public void setup() {
        when(mockedLoadConsentStatusRequest.getStatusLoaderListener()).thenReturn(Optional.of(mockedListener));
    }

    @Test
    public void test_loadConsentStatus_connectedToInternet() {

        when(mockedInternetConnectionObserver.isConnectedToInternet()).thenReturn(true);

        internetConnectedConsentStatusLoader.loadConsentStatus(mockedLoadConsentStatusRequest);

        verify(mockedConsentStatusLoader, times(1)).loadConsentStatus(mockedLoadConsentStatusRequest);
    }

    @Test
    public void test_loadConsentStatus_notConnectedToInternet() {

        when(mockedInternetConnectionObserver.isConnectedToInternet()).thenReturn(false);

        internetConnectedConsentStatusLoader.loadConsentStatus(mockedLoadConsentStatusRequest);

        verify(mockedListener, times(1)).onConsentStatusLoadFailure(any(RuntimeException.class));
    }
}
