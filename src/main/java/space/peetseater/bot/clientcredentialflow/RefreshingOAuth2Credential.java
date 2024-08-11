package space.peetseater.bot.clientcredentialflow;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;

public class RefreshingOAuth2Credential extends OAuth2Credential implements TokenListener {
    public RefreshingOAuth2Credential(String identityProvider, String accessToken) {
        super(identityProvider, accessToken);
    }

    @Override
    public String getAccessToken() {
        return super.getAccessToken();
    }

    @Override
    public void onNewCredential(ClientCredentialFlowResponse credential) {
        setAccessToken(credential.token());
        setExpiresIn(credential.expiresInSeconds());
    }
}
