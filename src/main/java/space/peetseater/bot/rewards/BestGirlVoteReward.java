package space.peetseater.bot.rewards;

import com.google.gson.JsonObject;
import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.response.sceneitems.GetSceneItemEnabledResponse;
import io.obswebsocket.community.client.message.response.sceneitems.GetSceneItemIdResponse;
import io.obswebsocket.community.client.message.response.scenes.GetSceneListResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BestGirlVoteReward implements OBSTask {

    Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());

    private final BestGirlVotesDB bestGirlVotesDB;
    private final String girl;
    private boolean busy;

    public BestGirlVoteReward(String characterName, BestGirlVotesDB bestGirlVotesDB) {
        this.busy = false;
        this.bestGirlVotesDB = bestGirlVotesDB;
        this.girl = characterName;
    }

    @Override
    public void run(OBSRemoteController obsRemoteController) {
        busy = true;
        try {
            bestGirlVotesDB.addVote(girl);
            updateObsTextForGirl(obsRemoteController);
            showScore(obsRemoteController);
        } catch (InterruptedException e) {
            logger.error("Could not show best girl scoreboard %s".formatted(e.getLocalizedMessage()), e);
        }
        busy = false;
    }

    private void updateObsTextForGirl(OBSRemoteController obsRemoteController) {
        String inputName = "%s-score".formatted(girl.toLowerCase());
        JsonObject updatedTextObject = new JsonObject();
        updatedTextObject.addProperty("text", bestGirlVotesDB.getScore(girl) + "");
        obsRemoteController.setInputSettings(inputName, updatedTextObject,true, 1000);
    }

    private void showScore(OBSRemoteController obsRemoteController) throws InterruptedException {
        String bgInput = "best-girl-scoreboard-bg.png";
        String reiScore = "rei-score";
        String asukaScore = "asuka-score";
        enableItem(obsRemoteController, bgInput);
        enableItem(obsRemoteController, reiScore);
        enableItem(obsRemoteController, asukaScore);
        Thread.sleep(2000);
        disableItem(obsRemoteController, bgInput);
        disableItem(obsRemoteController, reiScore);
        disableItem(obsRemoteController, asukaScore);
    }

    private void enableItem(OBSRemoteController obsRemoteController, String itemName) {
        GetSceneListResponse sceneListResponse = obsRemoteController.getSceneList(1000);
        String sceneName = sceneListResponse.getCurrentProgramSceneName();
        GetSceneItemIdResponse sceneItemIdResponse = obsRemoteController.getSceneItemId(sceneName, itemName, 0, 1000);

        // If there's no item by that name, then just stop
        if (!sceneItemIdResponse.getMessageData().getRequestStatus().getResult()) {
            logger.warn("No item found in scene %s by name %s".formatted(sceneName, itemName));
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
    }

    private void disableItem(OBSRemoteController obsRemoteController, String itemName) {
        GetSceneListResponse sceneListResponse = obsRemoteController.getSceneList(1000);
        String sceneName = sceneListResponse.getCurrentProgramSceneName();
        GetSceneItemIdResponse sceneItemIdResponse = obsRemoteController.getSceneItemId(sceneName, itemName, 0, 1000);

        // If there's no item by that name, then just stop
        if (!sceneItemIdResponse.getMessageData().getRequestStatus().getResult()) {
            logger.warn("No item found in scene %s by name %s".formatted(sceneName, itemName));
            return;
        }

        Number itemId = sceneItemIdResponse.getSceneItemId();
        obsRemoteController.setSceneItemEnabled(sceneName, itemId, false, 1000);
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

}
