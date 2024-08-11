package space.peetseater.bot;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.pubsub.domain.ChannelPointsRedemption;
import com.github.twitch4j.pubsub.events.RewardRedeemedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.peetseater.bot.clientcredentialflow.RefreshingOAuth2Credential;
import space.peetseater.bot.clientcredentialflow.RefreshingOAuth2CredentialProvider;
import space.peetseater.bot.clientcredentialflow.TwitchCredentialClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

// https://twitch4j.github.io/
public class TwitchEventPublisher extends EventPublisher {
    private Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());
    private TwitchClient twitchClient;
    private final TwitchCredentialClient credTwitchClient;
    private final RefreshingOAuth2CredentialProvider refreshingOAuth2CredentialProvider;
    transient private final String clientSecret;
    transient private final String clientId;
    private final HashSet<String> alreadySeenEvents;

    public TwitchEventPublisher(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.credTwitchClient = new TwitchCredentialClient(clientId, clientSecret);
        this.refreshingOAuth2CredentialProvider = new RefreshingOAuth2CredentialProvider(credTwitchClient);
        alreadySeenEvents = new HashSet<>();
    }

    public void connectToTwitch() throws IOException {
        refreshingOAuth2CredentialProvider.start();
        RefreshingOAuth2Credential credentials = null;
        /* Get the token from twitch on the other thread, wait here until we do */
        while (credentials == null) {
            credentials = refreshingOAuth2CredentialProvider.getCredential();
        }
        this.twitchClient = getTwitchClient();
    }

    public void disconnectFromTwitch() {
        alreadySeenEvents.clear();
        refreshingOAuth2CredentialProvider.stop();
        this.twitchClient.close();
    }

    protected TwitchClient getTwitchClient() throws IOException {
        TwitchClientBuilder builder = TwitchClientBuilder.builder()
            .withClientId(clientId)
            .withClientSecret(clientSecret)
            .withEnableHelix(true)
            .withEnableChat(true)
            .withEnablePubSub(true);

        return builder.build();
    }

    public void beginListeningForRewards(String fromChannel) {
        String channelId =  twitchClient.getHelix()
                .getUsers(null, null, Arrays.asList(fromChannel))
                .execute()
                .getUsers()
                .get(0)
                .getId();
        twitchClient.getPubSub().listenForChannelPointsRedemptionEvents(refreshingOAuth2CredentialProvider.getCredential(), channelId);

        // RewardRedeemedEvent(super=ChannelPointsRedemptionEvent(time=2024-08-07T05:05:42.765510278Z, redemption=ChannelPointsRedemption(id=e0d6f2d2-edc8-4f94-a118-8c3328dfd5d2, user=ChannelPointsUser(id=181296481, login=peetseater, displayName=PeetsEater), channelId=181296481, redeemedAt=2024-08-07T05:05:42.765510278Z, reward=ChannelPointsReward(id=ab69bc1d-24d2-47cb-9973-b9b3abd9568c, channelId=181296481, title=Gib meme, prompt=Ask me to share a stupid meme, cost=1000, isUserInputRequired=false, isSubOnly=false, image=ChannelPointsReward.Image(url1x=https://static-cdn.jtvnw.net/custom-reward-images/181296481/ab69bc1d-24d2-47cb-9973-b9b3abd9568c/cf50c3df-0a4c-47c6-b08e-b9da136f8ec8/custom-1.png, url2x=https://static-cdn.jtvnw.net/custom-reward-images/181296481/ab69bc1d-24d2-47cb-9973-b9b3abd9568c/cf50c3df-0a4c-47c6-b08e-b9da136f8ec8/custom-2.png, url4x=https://static-cdn.jtvnw.net/custom-reward-images/181296481/ab69bc1d-24d2-47cb-9973-b9b3abd9568c/cf50c3df-0a4c-47c6-b08e-b9da136f8ec8/custom-4.png), defaultImage=ChannelPointsReward.Image(url1x=https://static-cdn.jtvnw.net/custom-reward-images/default-1.png, url2x=https://static-cdn.jtvnw.net/custom-reward-images/default-2.png, url4x=https://static-cdn.jtvnw.net/custom-reward-images/default-4.png), backgroundColor=#00C7AC, isEnabled=true, isPaused=false, isInStock=true, maxPerStream=ChannelPointsReward.MaxPerStream(isEnabled=false, maxPerStream=0), shouldRedemptionsSkipRequestQueue=false, updatedForIndicatorAt=2021-08-02T18:34:22.860179119Z, maxPerUserPerStream=MaxPerUserPerStream(isEnabled=false, value=0), globalCooldown=GlobalCooldown(isEnabled=false, seconds=0), redemptionsRedeemedCurrentStream=null, cooldownExpiresAt=null, templateId=null), userInput=null, status=UNFULFILLED, cursor=ZTBkNmYyZDItZWRjOC00Zjk0LWExMTgtOGMzMzI4ZGZkNWQyX18yMDI0LTA4LTA3VDA1OjA1OjQyLjc2NTUxMDI3OFo=)))
        twitchClient.getEventManager().onEvent(ChannelPointsRedemption.class, this::redemptionHandler);
        // RewardRedeemedEvent GibMeme
        // RewardRedeemedEvent who is best girl?
        // RewardRedeemedEvent suggest topic
        twitchClient.getEventManager().onEvent(RewardRedeemedEvent.class, this::rewardHandler);
    }

    private void rewardHandler(RewardRedeemedEvent channelPointsRedemption) {
        // twitch may or may not send a redemption more than once, so only count it once
        redemptionHandler(channelPointsRedemption.getRedemption());
    }

    private void redemptionHandler(ChannelPointsRedemption channelPointsRedemption) {
        String id = channelPointsRedemption.getReward().getId();
        String title = channelPointsRedemption.getReward().getTitle();
        if (alreadySeenEvents.contains(id)) {
            logger.info("Event already seen: %s, skipping".formatted(channelPointsRedemption));
            return;
        }
        logger.info("Channel redemption: %s".formatted(title));
        this.newRewardEventReceived(title);
    }

    public void beginListeningForChatMessages(String channelName) {
        twitchClient.getChat().joinChannel(channelName);
        twitchClient.getChat().getEventManager().onEvent(ChannelMessageEvent.class, channelMessageEvent -> {
            logger.info("Message received: %s".formatted(channelMessageEvent.getMessage()));
            this.newChatMessageReceived(new ChatMessage(channelMessageEvent.getMessage(), channelMessageEvent.getUser().getName()));
        });
    }
}
