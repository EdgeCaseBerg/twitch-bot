package space.peetseater.bot.bible;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BibleFromFileProviderTest {
    Path biblePath;
    BibleFromFileProvider bible;

    @BeforeEach
    public void setup() {
        biblePath = Paths.get("akjv.txt");
        bible = new BibleFromFileProvider(biblePath);
    }

    @Test
    public void testBibleLoader() {
        HashMap<String, Integer> words = bible.getWordsInBible();
        assertEquals(983, words.get("jesus"));
    }
}