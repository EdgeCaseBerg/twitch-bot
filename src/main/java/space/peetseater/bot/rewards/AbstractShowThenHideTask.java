package space.peetseater.bot.rewards;

import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.response.sceneitems.GetSceneItemEnabledResponse;
import io.obswebsocket.community.client.message.response.sceneitems.GetSceneItemIdResponse;
import io.obswebsocket.community.client.message.response.scenes.GetSceneListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractShowThenHideTask implements OBSTask {

    abstract protected Logger getLogger();

    protected boolean busy = false;

    abstract protected String getSceneItemName();

    abstract protected long getMilliBeforeDisablingItem();

    @Override
    public void run(OBSRemoteController obsRemoteController) {
        if (busy) {
            return;
        }
        try {
            busy = true;
            toggleSceneItemOnForDuration(obsRemoteController);
        } catch (Exception e) {
            // Ensure we
            getLogger().error(e.getLocalizedMessage(), e);
        }
        busy = false;
    }

    private void toggleSceneItemOnForDuration(OBSRemoteController obsRemoteController) throws InterruptedException {
        GetSceneListResponse sceneListResponse = obsRemoteController.getSceneList(1000);
        String sceneName = sceneListResponse.getCurrentProgramSceneName();
        GetSceneItemIdResponse sceneItemIdResponse = obsRemoteController.getSceneItemId(sceneName, getSceneItemName(), 0, 1000);

        // If there's no item by that name, then just stop
        if (!sceneItemIdResponse.getMessageData().getRequestStatus().getResult()) {
            getLogger().warn("No item found in scene %s by name %s".formatted(sceneName, getSceneItemName()));
            return;
        }

        Number itemId = sceneItemIdResponse.getSceneItemId();
        GetSceneItemEnabledResponse s = obsRemoteController.getSceneItemEnabled(sceneName, itemId, 1000);
        Boolean isEnabled = s.getSceneItemEnabled();
        if (!isEnabled) {
            obsRemoteController.setSceneItemEnabled(sceneName, itemId, true, 1000);
        } else {
            obsRemoteController.setSceneItemEnabled(sceneName, itemId, false, 1000);
            obsRemoteController.setSceneItemEnabled(sceneName, itemId, true, 1000);
        }
        // Is there a better way to do this. Sure. Do we need to?
        // No. No this is just running on my local machine. It's fine.
        Thread.sleep(getMilliBeforeDisablingItem());
        obsRemoteController.setSceneItemEnabled(sceneName, itemId, false, 1000);
    }

    @Override
    public boolean isBusy() {
        return busy;
    }
}
