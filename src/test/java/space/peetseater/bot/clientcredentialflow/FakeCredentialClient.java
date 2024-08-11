package space.peetseater.bot.clientcredentialflow;

import java.io.IOException;
import java.net.URISyntaxException;

public class FakeCredentialClient extends TwitchCredentialClient {
    private final ClientCredentialFlowResponse cred;
    volatile int callNo;
    public FakeCredentialClient(ClientCredentialFlowResponse clientCredentialFlowResponse) {
        super("", "");
        this.cred = clientCredentialFlowResponse;
        callNo = 0;
    }

    public int getNumberOfCalls() {
        synchronized (this) {
            return callNo;
        }
    }

    @Override
    public ClientCredentialFlowResponse getCredential() throws URISyntaxException, IOException, InterruptedException {
        synchronized (this) {
            callNo++;
        }
        return cred;
    }
}
