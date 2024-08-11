package space.peetseater.bot.clientcredentialflow;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.TimerTask;

public class OauthTokenTask extends TimerTask {

    Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    private final TokenListener tokenListener;
    private final TwitchCredentialClient twitchClient;

    public OauthTokenTask(TwitchCredentialClient twitchClient, TokenListener tokenListener) {
        this.tokenListener = tokenListener;
        this.twitchClient = twitchClient;
    }

    @Override
    public void run() {
        try {
            ClientCredentialFlowResponse credential = twitchClient.getCredential();
            logger.info("Token from twitch retrieved!");
            tokenListener.onNewCredential(credential);
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
