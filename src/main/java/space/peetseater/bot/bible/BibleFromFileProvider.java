package space.peetseater.bot.bible;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;

/* Expects a file where verse and verse text is separated by tabs */
public class BibleFromFileProvider implements BibleProvider {
    private final HashMap<String, Integer> wordAppearance;
    public BibleFromFileProvider(Path pathToBibleTxt) {
        String bibleText;
        try {
            bibleText = Files.readString(pathToBibleTxt);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.wordAppearance = calculateWords(bibleText);
    }

    private HashMap<String, Integer> calculateWords(String bibleText) {
        HashMap<String, Integer> wordAppearance = new HashMap<>();
        StringTokenizer verseTokenizer = new StringTokenizer(bibleText,"\t\n", false);
        String wordDelims = " ’.\",;:?)([]!\n";
        while (verseTokenizer.hasMoreTokens()) {
            verseTokenizer.nextToken(); // Ignore verse number.
            if (!verseTokenizer.hasMoreTokens()) continue;
            String line = verseTokenizer.nextToken();
            StringTokenizer wordTokenizer = new StringTokenizer(line, wordDelims, false);
            while (wordTokenizer.hasMoreTokens()) {
                String word = wordTokenizer.nextToken();
                if (word != null) {
                    String lower = word.toLowerCase(Locale.ENGLISH);
                    if (wordAppearance.containsKey(lower)) {
                        int newCount = wordAppearance.get(lower) + 1;
                        wordAppearance.put(lower, newCount);
                    } else {
                        wordAppearance.put(lower, 1);
                    }
                }
            }
        }
        return wordAppearance;
    }


    @Override
    public HashMap<String, Integer> getWordsInBible() {
        return wordAppearance;
    }
}
