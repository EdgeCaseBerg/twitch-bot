package space.peetseater.bot.clientcredentialflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;

public class RefreshingOAuth2CredentialProvider implements TokenListener {

    private Timer timer;
    Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    private final TwitchCredentialClient twitchClient;
    OauthTokenTask lastCompletedTask;
    private RefreshingOAuth2Credential token;

    public RefreshingOAuth2CredentialProvider(TwitchCredentialClient twitchClient) {
        this.timer = getTimer();
        this.twitchClient = twitchClient;
        lastCompletedTask = null;
        this.token = null;
    }

    public void start() {
        logger.info("Starting OAuth Fetching Task");
        lastCompletedTask = new OauthTokenTask(twitchClient, this);
        timer.schedule(lastCompletedTask, 0);
    }

    public RefreshingOAuth2Credential getCredential() {
        synchronized (this) {
            return token;
        }
    }

    @Override
    public void onNewCredential(ClientCredentialFlowResponse credential) {
        if (this.token == null) {
            this.token = new RefreshingOAuth2Credential("twitch", credential.token());
        }
        // Update the token that we've given out via getCredential so it continues to work
        this.token.onNewCredential(credential);
        lastCompletedTask.cancel();
        lastCompletedTask = new OauthTokenTask(twitchClient, this);
        timer.schedule(lastCompletedTask, credential.expiresInSeconds() * 1000L);
    }

    public Timer getTimer() {
        return new Timer("tokenTimer");
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public void stop() {
        timer.cancel();
    }
}
