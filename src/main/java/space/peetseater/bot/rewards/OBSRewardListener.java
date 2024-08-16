package space.peetseater.bot.rewards;

import io.obswebsocket.community.client.OBSRemoteController;
import space.peetseater.bot.ChatMessage;
import space.peetseater.bot.EventListener;

import java.util.HashMap;

public class OBSRewardListener implements EventListener {

    private final OBSRemoteController obsRemoteController;
    HashMap<String, OBSTask> rewardTasks;

    public OBSRewardListener(OBSRemoteController obsRemoteController) {
        rewardTasks = new HashMap<String, OBSTask>();
        this.obsRemoteController = obsRemoteController;
        setupTasks();
    }

    private void setupTasks() {
        // TODO
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        // Do nothing.
    }

    @Override
    public void onReward(String reward) {
        if (hasTaskFor(reward)) {
            OBSTask task = rewardTasks.get(reward);
            if (!task.isBusy()) {
                task.run(obsRemoteController);
            }
        }
    }

    private boolean hasTaskFor(String reward) {
        return rewardTasks.containsKey(reward);
    }

}
