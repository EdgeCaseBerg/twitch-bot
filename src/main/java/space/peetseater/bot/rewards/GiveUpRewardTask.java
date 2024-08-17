package space.peetseater.bot.rewards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GiveUpRewardTask extends AbstractShowThenHideTask {
    public static String twitchRewardName = "I had bad day";

    Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Override
    protected Logger getLogger() {
        return logger;
    }

    @Override
    protected String getSceneItemName() {
        return "give up wisdom.mp4";
    }

    @Override
    protected long getMilliBeforeDisablingItem() {
        return 33 * 1000 + 500;
    }
}
