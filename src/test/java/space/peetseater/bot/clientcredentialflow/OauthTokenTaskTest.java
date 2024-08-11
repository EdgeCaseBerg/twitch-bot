package space.peetseater.bot.clientcredentialflow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OauthTokenTaskTest {

    ClientCredentialFlowResponse receivedToken;

    class FakeListener implements TokenListener {
        @Override
        public void onNewCredential(ClientCredentialFlowResponse credential) {
            receivedToken = credential;
        }
    }

    @Test
    public void broadcastsCredentialOnRun() {
        ClientCredentialFlowResponse credential = new ClientCredentialFlowResponse("token", 1);
        TwitchCredentialClient fakeClient = new FakeCredentialClient(credential);
        OauthTokenTask oauthTokenTask = new OauthTokenTask(fakeClient, new FakeListener());
        oauthTokenTask.run();
        assertEquals(credential.token(), receivedToken.token());
        assertEquals(credential.expiresInSeconds(), receivedToken.expiresInSeconds());
    }
}