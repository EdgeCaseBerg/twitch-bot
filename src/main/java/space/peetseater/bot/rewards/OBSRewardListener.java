package space.peetseater.bot.rewards;

import io.obswebsocket.community.client.OBSRemoteController;
import space.peetseater.bot.ChatMessage;
import space.peetseater.bot.EventListener;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class OBSRewardListener implements EventListener {
    private final OBSRemoteController obsRemoteController;
    HashMap<String, OBSTask> rewardTasks;
    ExecutorService executorService;

    public OBSRewardListener(OBSRemoteController obsRemoteController) {
        rewardTasks = new HashMap<String, OBSTask>();
        this.obsRemoteController = obsRemoteController;
        executorService = new ForkJoinPool();
        setupTasks();
    }

    private void setupTasks() {
        rewardTasks.put(QuesoRewardTask.twitchRewardName, new QuesoRewardTask());
        rewardTasks.put(GiveUpRewardTask.twitchRewardName, new GiveUpRewardTask());
        rewardTasks.put("Gib meme", new GibMemeTask());
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
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        task.run(obsRemoteController);
                    }
                });
            }
        }
    }

    private boolean hasTaskFor(String reward) {
        return rewardTasks.containsKey(reward);
    }

}
