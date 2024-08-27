package space.peetseater.bot.rewards;

import io.obswebsocket.community.client.OBSRemoteController;

public interface OBSTask {
    void run(OBSRemoteController obsRemoteController);

    default void orRunIfBusy() {};

    boolean isBusy();
}
