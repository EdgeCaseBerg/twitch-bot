package space.peetseater.bot.rewards;

import io.obswebsocket.community.client.OBSRemoteController;

public interface OBSTask {
    void run(OBSRemoteController obsRemoteController);

    boolean isBusy();
}
