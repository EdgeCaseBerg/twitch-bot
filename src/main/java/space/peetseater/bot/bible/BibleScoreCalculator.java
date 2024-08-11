package space.peetseater.bot.bible;

import java.util.HashMap;
import java.util.StringTokenizer;

public class BibleScoreCalculator {
    protected final BibleProvider bibleProvider;

    public BibleScoreCalculator(BibleProvider bibleProvider) {
        this.bibleProvider = bibleProvider;
    }

    public float calculateScore(String string) {
        float score = 0.0f;
        HashMap<String, Integer> words = bibleProvider.getWordsInBible();
        String delims = " ’.,;:\"'/\\=?\n[]{}()";
        StringTokenizer tokenizer = new StringTokenizer(string.toLowerCase(), delims, false);
        int wordCount = 0;
        int chars = 0;
        while(tokenizer.hasMoreTokens()) {
            wordCount++;
            String word = tokenizer.nextToken();
            chars += word.length();
            if (!words.containsKey(word.toLowerCase())) {
                continue;
            }
            score += word.length();
        }
        if (wordCount == 0) return 0;
        return score / chars;
    }
}
