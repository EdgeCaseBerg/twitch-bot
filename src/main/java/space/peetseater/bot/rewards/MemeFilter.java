package space.peetseater.bot.rewards;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class MemeFilter implements FilenameFilter {
    Set<Path> seenToday;

    public MemeFilter() {
        this.seenToday = new HashSet<>();
    }

    public void markMemeSeen(Path path) {
        this.seenToday.add(path);
    }

    public boolean accept(File dir, String name) {
        boolean isJpg = name.endsWith(".jpg");
        boolean isPng = name.endsWith(".png");
        boolean isGif = name.endsWith(".gif");
        boolean isImage = isGif || isJpg || isPng;

        Path resolved = dir.toPath().resolve(name);
        boolean notSeen = !seenToday.contains(resolved);

        if (isImage && notSeen) {
            return true;
        }
        return false;
    }
}
