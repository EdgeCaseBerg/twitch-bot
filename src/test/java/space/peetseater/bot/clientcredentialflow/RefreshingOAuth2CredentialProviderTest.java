package space.peetseater.bot.clientcredentialflow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RefreshingOAuth2CredentialProviderTest {

    RefreshingOAuth2CredentialProvider refreshingOAuth2CredentialProvider;
    FakeTimer fakeTimer;
    FakeCredentialClient twitchCredentialClient;

    @BeforeEach
    public void setup() {
        twitchCredentialClient = new FakeCredentialClient(new ClientCredentialFlowResponse("token", 10));
        refreshingOAuth2CredentialProvider = new RefreshingOAuth2CredentialProvider(twitchCredentialClient);
        fakeTimer = new FakeTimer();
        refreshingOAuth2CredentialProvider.setTimer(fakeTimer);
    }

    @Test
    public void testRefreshWorksAsExpected() throws InterruptedException {
        assertEquals(0, twitchCredentialClient.getNumberOfCalls());
        refreshingOAuth2CredentialProvider.start();
        fakeTimer.runTasks();
        assertEquals(1, twitchCredentialClient.getNumberOfCalls());
        fakeTimer.runTasks();
        assertEquals(2, twitchCredentialClient.getNumberOfCalls());
    }

}