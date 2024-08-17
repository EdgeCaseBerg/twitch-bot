package space.peetseater.bot.rewards;

import com.google.gson.JsonObject;
import io.obswebsocket.community.client.OBSRemoteController;
import io.obswebsocket.community.client.message.response.inputs.SetInputSettingsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GibMemeTask implements OBSTask {
    private final MemeFileSelector memeSelector;
    Logger logger = LoggerFactory.getLogger(getClass().getCanonicalName());
    private boolean busy;

    String inputName = "meme-redeem-img";

    public Logger getLogger() {
        return logger;
    }

    public GibMemeTask() {
        this.memeSelector = new MemeFileSelector(Paths.get("memes"));
    }

    @Override
    public void run(OBSRemoteController obsRemoteController) {
        busy = true;
        try {
            Path meme = memeSelector.getRandomUnseenFile();
            getLogger().info("Showing meme %s".formatted(meme.toAbsolutePath()));
            swapImageWithNewFile(obsRemoteController, meme);
            // Then show it for Ns, then hide it.
            ShowMemeTask task = new ShowMemeTask();
            getLogger().info("Running task to show meme");
            task.run(obsRemoteController);

        } catch (Exception e) {
            getLogger().error(e.getLocalizedMessage(), e);
        }
        busy = false;
    }

    private void swapImageWithNewFile(OBSRemoteController obsRemoteController, Path path) {
        JsonObject fileObject = new JsonObject();
        fileObject.addProperty("file", path.toAbsolutePath().toString());
        SetInputSettingsResponse response = obsRemoteController.setInputSettings(inputName, fileObject, true, 1000);
    }

    @Override
    public boolean isBusy() {
        return busy;
    }

    private class ShowMemeTask extends AbstractShowThenHideTask {
        @Override
        protected Logger getLogger() {
            return logger;
        }

        @Override
        protected String getSceneItemName() {
            return inputName;
        }

        @Override
        protected long getMilliBeforeDisablingItem() {
            return 4000;
        }
    }
}
