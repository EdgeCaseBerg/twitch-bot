package space.peetseater.bot.rewards;

public class QuesoRewardTask extends AbstractShowThenHideTask {
    static String twitchRewardName = "Queso";

    @Override
    public String getSceneItemName() {
        return "queso channepoint.mp4";
    }

    @Override
    protected long getMilliBeforeDisablingItem() {
        return 5500;
    }
}
