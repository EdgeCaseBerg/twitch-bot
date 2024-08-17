package space.peetseater.bot.rewards;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.*;
import java.util.concurrent.ThreadLocalRandom;

public class MemeFileSelector {
    private final Path folder;
    MemeFilter memeFilter;

    public MemeFileSelector(Path memeFolder) {
        this.folder = memeFolder;
        this.memeFilter = new MemeFilter();
        // If I want to persist memes shown to chat across streams, then
        // we'd want to load those files in here.
        this.memeFilter.markMemeSeen(getNoMemeToShowMeme());
    }

    private @NotNull Path getNoMemeToShowMeme() {
        return folder.resolve("nice-boat.jpg");
    }

    public Path getRandomUnseenFile() {
        File[] files = folder.toFile().listFiles(this.memeFilter);
        if (files.length == 0) {
            return getNoMemeToShowMeme();
        }

        int randomIndex = ThreadLocalRandom.current().nextInt(0, files.length);
        if (randomIndex < files.length) {
            File file = files[randomIndex];
            memeFilter.markMemeSeen(file.toPath());
            return file.toPath();
        } else {
            // ??? Shouldn't get here but if so...
            return getNoMemeToShowMeme();
        }
    };

}
