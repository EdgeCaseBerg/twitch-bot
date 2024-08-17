package space.peetseater.bot.rewards;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuesoRewardTask extends AbstractShowThenHideTask {
    static String twitchRewardName = "Queso";

    Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public String getSceneItemName() {
        return "queso channepoint.mp4";
    }

    @Override
    protected long getMilliBeforeDisablingItem() {
        return 5500;
    }
}
