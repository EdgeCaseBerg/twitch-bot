package space.peetseater.bot.rewards;

public class GiveUpRewardTask extends AbstractShowThenHideTask {
    public static String twitchRewardName = "I had bad day";
    @Override
    protected String getSceneItemName() {
        return "give up wisdom.mp4";
    }

    @Override
    protected long getMilliBeforeDisablingItem() {
        return 33 * 1000 + 500;
    }
}
